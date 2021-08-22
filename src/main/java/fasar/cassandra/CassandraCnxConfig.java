package fasar.cassandra;

import com.datastax.driver.core.ConsistencyLevel;

import java.util.Arrays;
import java.util.Objects;

public class CassandraCnxConfig {

    private final ConsistencyLevel readConsistency;
    private final ConsistencyLevel writeConsistency;
    private final String keyspace;
    private String[] contactPoints;
    private Integer contactPort;
    private final Integer retryMs;
    private final Integer maxRetry;

    public CassandraCnxConfig(ConsistencyLevel readConsistency, ConsistencyLevel writeConsistency, String keyspace, String[] contactPoints, Integer contactPort, Integer retryMs, Integer maxRetry) {
        this.readConsistency = readConsistency;
        this.writeConsistency = writeConsistency;
        this.keyspace = keyspace;
        this.contactPoints = contactPoints;
        this.contactPort = contactPort;
        this.retryMs = retryMs;
        this.maxRetry = maxRetry;
    }

    public ConsistencyLevel getReadConsistency() {
        return readConsistency;
    }

    public ConsistencyLevel getWriteConsistency() {
        return writeConsistency;
    }

    public String getKeyspace() {
        return keyspace;
    }

    public String[] getContactPoints() {
        return contactPoints;
    }

    public Integer getContactPort() {
        return contactPort;
    }

    public Integer getRetryMs() {
        return retryMs;
    }

    public Integer getMaxRetry() {
        return maxRetry;
    }

    public void setContactPoints(String[] contactPoints) {
        this.contactPoints = contactPoints;
    }

    public void setContactPort(Integer contactPort) {
        this.contactPort = contactPort;
    }

    @Override
    public String toString() {
        return "CassandraCnxConfig{" +
                "readConsistency=" + readConsistency +
                ", writeConsistency=" + writeConsistency +
                ", keyspace='" + keyspace + '\'' +
                ", contactPoints=" + Arrays.toString(contactPoints) +
                ", contactPort=" + contactPort +
                ", retryMs=" + retryMs +
                ", maxRetry=" + maxRetry +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CassandraCnxConfig that = (CassandraCnxConfig) o;
        return readConsistency == that.readConsistency && writeConsistency == that.writeConsistency && Objects.equals(keyspace, that.keyspace) && Arrays.equals(contactPoints, that.contactPoints) && Objects.equals(contactPort, that.contactPort) && Objects.equals(retryMs, that.retryMs) && Objects.equals(maxRetry, that.maxRetry);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(readConsistency, writeConsistency, keyspace, contactPort, retryMs, maxRetry);
        result = 31 * result + Arrays.hashCode(contactPoints);
        return result;
    }
}
