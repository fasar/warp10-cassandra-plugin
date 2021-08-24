package fasar.warp10.cassandra;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.geoxp.GeoXPLib;
import fasar.cassandra.CassandraConnexion;
import io.warp10.continuum.gts.GTSHelper;
import io.warp10.continuum.gts.GeoTimeSerie;
import io.warp10.script.NamedWarpScriptFunction;
import io.warp10.script.WarpScriptException;
import io.warp10.script.WarpScriptStack;
import io.warp10.script.WarpScriptStackFunction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CFETCH extends NamedWarpScriptFunction implements WarpScriptStackFunction {
    private CassandraConnexion cassandraConnexion;

    public CFETCH(
            String functionName,
            CassandraConnexion cassandraConnexion
    ) {
        super(functionName);
        this.cassandraConnexion = cassandraConnexion;
    }

    @Override
    public Object apply(WarpScriptStack stack) throws WarpScriptException {
        // Get the CQL mapping position of the result of the query to create a GTS
        Object listParams = stack.pop();
        if (!(listParams instanceof List)) {
            throw new WarpScriptException(getName() + " expect a list of 5 numbers or NaN mapping the GTS ts, elevation, longitude, latitude and value on top of the stack.");
        }
        List<Object> gtpMappingNumTmp = (List<Object>) listParams;
        if (gtpMappingNumTmp.size() != 5) {
            throw new WarpScriptException(getName() + " expect a list of 5 numbers or NaN mapping the GTS ts, elevation, longitude, latitude and value on top of the stack.");
        }
        int[] gtsMappingNum = new int[5];
        for (int i = 0; i < 5; i++) {
            Object o = gtpMappingNumTmp.get(i);
            if (!(o instanceof Number)) {
                throw new WarpScriptException(getName() + " expect a list of 5 numbers or NaN mapping the GTS ts, elevation, longitude, latitude and value on top of the stack. " +
                        "Got parameter number " + i + " with value of " + o);
            }
            if (o instanceof Double && Double.isNaN((Double)o)) {
                gtsMappingNum[i] = -1;
            } else if (o instanceof Integer) {
                gtsMappingNum[i] = (int) o;
            } else {
                gtsMappingNum[i] = ((Number)o).intValue();
            }
        }
        if (gtsMappingNum[0] < 0) {
            throw new WarpScriptException(getName() + " expect timestamp mapping as >=0");
        }

        // Get the Query
        Object queryObj = stack.pop();
        if (!(queryObj instanceof String)) {
            throw new WarpScriptException(getName() + " expect the CQL Select Query String at top -1 of the stack.");
        }
        String query = (String) queryObj;
        if (!query.toLowerCase().contains("select")) {
            throw new WarpScriptException(getName() + " expect the CQL Select Query String at top -1 of the stack.");
        }

        // Create the result GTS
        GeoTimeSerie gts = new GeoTimeSerie();
        gts.setName("");

        // Execute the query
        Session session = cassandraConnexion.getSession();
        ResultSet execute = session.execute(query);

        for (Row row : execute) {
            long timestamp = row.getTimestamp(gtsMappingNum[0]).getTime();
            long location;
            if (gtsMappingNum[1] >= 0 && gtsMappingNum[2] >= 0) {
                long lon = row.getLong(gtsMappingNum[1]);
                long lat = row.getLong(gtsMappingNum[2]);
                location = GeoXPLib.toGeoXPPoint(lat, lon);
            } else {
                location = GeoTimeSerie.NO_LOCATION;
            }

            long elevation;
            if(gtsMappingNum[3] >= 0) {
                elevation = row.getLong(gtsMappingNum[3]);
            } else {
                elevation = GeoTimeSerie.NO_ELEVATION;
            }
            Object value = row.getObject(gtsMappingNum[4]);
            GTSHelper.setValue(gts, timestamp, location, elevation, value, false);
        }

        stack.push(gts);


        return stack;
    }
}
