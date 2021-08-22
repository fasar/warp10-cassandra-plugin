package fasar.warp10.cassandra;

import com.datastax.driver.core.ConsistencyLevel;
import fasar.cassandra.CassandraCnxConfig;
import fasar.cassandra.CassandraConnexion;
import fasar.utils.StringUtils;
import io.warp10.WarpConfig;
import io.warp10.warp.sdk.WarpScriptExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class CassandraExtension extends WarpScriptExtension {
  private static final Logger LOG = LoggerFactory.getLogger(CassandraExtension.class);

  private static final Map<String,Object> functions;
  
  static {
    functions = new HashMap<String,Object>();

    String name = WarpConfig.getProperty("warpscript.tuto.helloName");
    functions.put("HELLOJ", new HELLOJ("HELLOJ", name));

    CassandraCnxConfig cassConfig = buildCassConfig();
    try {
      CassandraConnexion cassandraConnexion = new CassandraConnexion(cassConfig);
      functions.put("CSTATUS", new CSTATUS("CSTATUS", cassandraConnexion));
      functions.put("CSELECT", new CSELECT("CSELECT", cassandraConnexion));
    } catch (Exception e) {
      throw new CassPluginException(e.getMessage(), e);
    }

  }

  private static CassandraCnxConfig buildCassConfig() {
    ConsistencyLevel readConsistency = fromString(WarpConfig.getProperty("warpscript.cassandradb.readConsistency"));
    ConsistencyLevel writeConsistency = fromString(WarpConfig.getProperty("warpscript.cassandradb.writeConsistency"));
    String keyspace = stringOfOrNull("warpscript.cassandradb.keyspace");
    String contactPointsStr = stringOfOrNull("warpscript.cassandradb.contactPoints");
    String[] contactPoints;
    if (!StringUtils.isBlank(contactPointsStr)) {
      contactPoints = contactPointsStr.split(",");
    } else {
      contactPoints = null;
    }
    String portStr = stringOfOrNull("warpscript.cassandradb.contactPort");
    Integer contactPort = null;
    if (!StringUtils.isBlank(portStr)) {
      contactPort = intOf("warpscript.cassandradb.contactPort");
    }
    Integer retryMs = intOf("warpscript.cassandradb.retry-ms");
    Integer maxRetry = intOf("warpscript.cassandradb.maxRetry");

    return new CassandraCnxConfig(readConsistency, writeConsistency, keyspace, contactPoints, contactPort, retryMs, maxRetry);
  }

  private static String stringOfNonNull(String ppt) {
    String value = WarpConfig.getProperty(ppt);
    if (StringUtils.isBlank(value)) {
      throw new CassPluginException("Property "+ppt+" is null or blank.");
    }
    return value;
  }

  private static String stringOfOrNull(String ppt) {
    String value = WarpConfig.getProperty(ppt);
    if (StringUtils.isBlank(value)) {
      return null;
    }
    return value;
  }

  /**
   * Convert the property value in Integer
   * @param s
   * @return
   */
  private static int intOf(String s) {
    String value = WarpConfig.getProperty(s);
    try {
      return Integer.parseInt(value);
    } catch (Exception e) {
      String msg = "Can't parse the property " + s + ". Value should be compatible with Integer. Try to convert " + value;
      throw new CassPluginException(msg, e);
    }
  }


  private static ConsistencyLevel fromString(String consistencyLevel) {
    final ConsistencyLevel[] values = ConsistencyLevel.values();
    for (int i = 0; i < values.length; i++) {
      if (values[i].toString().equalsIgnoreCase(consistencyLevel)) {
        return values[i];
      }
    }
    return ConsistencyLevel.ONE;
  }


  @Override
  public Map<String, Object> getFunctions() {
    return functions;
  }


}
