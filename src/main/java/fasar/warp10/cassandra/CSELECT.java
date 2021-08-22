package fasar.warp10.cassandra;

import com.datastax.driver.core.*;
import fasar.cassandra.CassandraConnexion;
import io.warp10.script.NamedWarpScriptFunction;
import io.warp10.script.WarpScriptException;
import io.warp10.script.WarpScriptStack;
import io.warp10.script.WarpScriptStackFunction;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

public class CSELECT extends NamedWarpScriptFunction implements WarpScriptStackFunction  {
    private CassandraConnexion cassandraConnexion;

    public CSELECT(String functionName, CassandraConnexion cassandraConnexion) {
        super(functionName);
        this.cassandraConnexion = cassandraConnexion;
    }

    @Override
    public Object apply(WarpScriptStack stack) throws WarpScriptException {
        Object queryObj = stack.pop();
        if (!(queryObj instanceof String)) {
            throw new WarpScriptException("expect the CQL Select Query String on the top of the stack.");
        }
        String query = (String) queryObj;

        Session session = cassandraConnexion.getSession();
        ResultSet execute = session.execute(query);

        for (Row row : execute) {
            String s1 = row.getString(0);
            String s2 = row.getString(1);
            stack.push(Pair.of(s1, s2));
        }
        return stack;
    }
}
