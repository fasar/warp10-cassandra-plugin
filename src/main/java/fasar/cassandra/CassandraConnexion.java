package fasar.cassandra;

import com.datastax.driver.core.*;
import com.datastax.driver.core.policies.*;
import com.datastax.driver.extras.codecs.jdk8.InstantCodec;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

public class CassandraConnexion {
    private static final Logger LOG = LoggerFactory.getLogger(CassandraConnexion.class);
    private int contactPort;
    private InetSocketAddress currentContactPoint;

    private Cluster cluster;
    private PoolingOptions poolingOptions;
    private LoadBalancingPolicy loadBalancingPolicy;
    private Session session;
    private CassandraCnxConfig cassConfig;

    @Inject
    public CassandraConnexion(
            CassandraCnxConfig cassConfig
    ) throws Exception {
        long t1 = System.currentTimeMillis();
        this.cassConfig = cassConfig;

        long t2 = System.currentTimeMillis();
        long t8 = 0;
        long t9 = 0;
        long t10 = 0, t10a = 0;
        long t3 = 0, t4 = 0;
        boolean isConnected = false;
        long nbRetry = 0;
        while (!isConnected) {
            List<String> contactPointsFeeder = getContactPointsFeeder(cassConfig.getContactPoints());
            for (String toTry : contactPointsFeeder) {
                if (isConnected) {
                    break;
                }
                try {
                    t8 = System.currentTimeMillis();
                    LOG.info("Try to open a Cassandra connexion on {} ", Arrays.asList(toTry));

                    Cluster.Builder builder = Cluster.builder()
                            // No metrics :
                            // https://docs.datastax.com/en/developer/java-driver/3.5/manual/metrics/
                            // Or you can add io.dropwizard.metrics:metrics-jmx:4.0.2 to let the driver have the jmx metrics
                            .withoutJMXReporting()
                            .addContactPoints(toTry);
                    if (cassConfig.getContactPort() != null && cassConfig.getContactPort() > 0) {
                        builder = builder.withPort(cassConfig.getContactPort());
                    }
                    RetryPolicy retryPolicy = DefaultRetryPolicy.INSTANCE;
                    retryPolicy = new LoggingRetryPolicy(retryPolicy);

                    this.cluster = builder
                            .withReconnectionPolicy(new ConstantReconnectionPolicy(cassConfig.getRetryMs()))
                            .withRetryPolicy(retryPolicy)
                            .withLoadBalancingPolicy(
                                    new TokenAwarePolicy(new DCAwareRoundRobinPolicy.Builder().build()))
                            .build();

                    poolingOptions = cluster.getConfiguration().getPoolingOptions();
                    loadBalancingPolicy = cluster.getConfiguration().getPolicies().getLoadBalancingPolicy();

                    cluster.getConfiguration().getCodecRegistry()
                            .register(InstantCodec.instance);

                    t9 = System.currentTimeMillis();


                    Metadata meta = cluster.getMetadata();
                    Set<Host> allHosts = meta.getAllHosts();
                    for (Host host : allHosts) {
                        if (host.isUp()) {
                            this.currentContactPoint = host.getSocketAddress();
                            this.contactPort = host.getSocketAddress().getPort();
                        }
                    }

                    t3 = System.currentTimeMillis();
                    session = cluster.newSession();
                    t4 = System.currentTimeMillis();

                    t10a = System.currentTimeMillis();

                    printMetaData(cluster, cassConfig.getKeyspace(), cassConfig);
                    t10 = System.currentTimeMillis();

                    isConnected = true;

                    break;
                } catch (Exception e) {
                    LOG.error("Can't connect to database with db.contactPoints: {}. Error {}", toTry, e.getMessage());
                    LOG.debug("Can't connect to database.", e);
                }
            }
            nbRetry++;
            if (cassConfig.getMaxRetry() > 0 && nbRetry > cassConfig.getMaxRetry()) {
                String message = "Can't connect cassandra after " + nbRetry + " try.";
                message += " Please check Cassandra is up on hosts : ";
                message += contactPointsFeeder.stream().collect(Collectors.joining(", ", "[", "]"));
                message += "; and port " + cassConfig.getContactPort();

                throw new IOException(message);
            }
            if (!isConnected) {
                LOG.error("Can't connect to database. Wait {}ms before try again ", cassConfig.getRetryMs());
                Thread.sleep(cassConfig.getRetryMs());
            }
        }

        long t6 = System.currentTimeMillis();

        session.execute("use " + cassConfig.getKeyspace() + ";");
        long t7 = System.currentTimeMillis();

        LOG.debug("Cassandra Time _ Init Time: {} s", t2 - t1);
        LOG.debug("Cassandra Time _ Create cluster : {} ms", t9 - t8);
        LOG.debug("Cassandra Time _ Print meta data : {} ms", t10a - t10);
        LOG.debug("Cassandra Time _ Create session : {} ms", t4 - t3);
        LOG.debug("Cassandra Time _ Use keyspace : {} ms", t7 - t6);

    }

    public List<CHostMonit> monitor() {
        ArrayList<CHostMonit> res = new ArrayList<>();
        Session.State state = session.getState();
        for (Host host : state.getConnectedHosts()) {
            HostDistance distance = loadBalancingPolicy.distance(host);
            String hostName = hostName(host);
            int connections = state.getOpenConnections(host);
            int currentLoad = state.getInFlightQueries(host);
            int maxLoad = connections * poolingOptions.getMaxRequestsPerConnection(distance);
            CHostMonit cHostMonit = new CHostMonit(hostName, Instant.now(), connections, currentLoad, maxLoad);
            res.add(cHostMonit);
        }
        return res;
    }

    private String hostName(Host host) {
        InetAddress address = host.getAddress();
        String hostAddress = address.getHostAddress();
        String datacenter = host.getDatacenter();
        String rack = host.getRack();
        return hostAddress + "@dc:" + datacenter + ":rack:" + rack;
    }

    private List<String> getContactPointsFeeder(String[] contactPoints) throws SocketException {
        List<String> contactPointsFeeder = new ArrayList<>();

        if (contactPoints == null || contactPoints.length == 0 || StringUtils.isEmpty(contactPoints[0])) {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = networkInterfaces.nextElement();
                Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
                while (inetAddresses.hasMoreElements()) {
                    InetAddress inetAddress = inetAddresses.nextElement();
                    String hostAddress = inetAddress.getHostAddress();
                    contactPointsFeeder.add(hostAddress);
                }
            }
        } else {
            contactPointsFeeder = Arrays.asList(contactPoints);
        }
        return contactPointsFeeder;
    }


    public String currentContactPoint() {
        Collection<Host> connectedHosts = session.getState().getConnectedHosts();
        for (Host connectedHost : connectedHosts) {
            if (connectedHost.isUp()) {
                return connectedHost.getEndPoint().toString();
            }
        }
        return null;
    }

    private void printMetaData(Cluster cluster, String keyspace, CassandraCnxConfig cassConfig) {
        Metadata meta = cluster.getMetadata();
        LOG.info("Cassandra info _ Cluster Name : {}", meta.getClusterName());
        LOG.info("Cassandra info _ Partitioner  : {}", meta.getPartitioner());
        LOG.info("Cassandra info _ Hosts : {}", meta.getAllHosts());
        LOG.info("Cassandra info _ Connected Host : {}", this.currentContactPoint);
        LOG.info("Cassandra info _ Keyspace : {}", keyspace);
        LOG.info("Cassandra info _ Read Consistency : {}", cassConfig.getReadConsistency());
        LOG.info("Cassandra info _ Write Consistency : {}", cassConfig.getWriteConsistency());
        LOG.info("Cassandra info _ Keyspace info : {}", meta.getKeyspace(keyspace));
    }


    public Cluster getCluster() {
        return cluster;
    }

    public Session getSession() {
        return session;
    }

    public int getContactPort() {
        return contactPort;
    }


    public CassandraCnxConfig getCassConfig() {
        return cassConfig;
    }

    public void closeConnexion() {
        session.close();
        cluster.close();
    }
}
