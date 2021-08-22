package fasar.warp10.cassandra;

import fasar.cassandra.CHostMonit;
import fasar.cassandra.CassandraConnexion;
import io.warp10.script.NamedWarpScriptFunction;
import io.warp10.script.WarpScriptException;
import io.warp10.script.WarpScriptStack;
import io.warp10.script.WarpScriptStackFunction;

import java.util.List;

public class CSTATUS extends NamedWarpScriptFunction implements WarpScriptStackFunction {

    private CassandraConnexion cassandraConnexion;

    public CSTATUS(String functionName, CassandraConnexion cassandraConnexion) {
        super(functionName);
        this.cassandraConnexion = cassandraConnexion;
    }

    @Override
    public Object apply(WarpScriptStack stack) throws WarpScriptException {
        List<CHostMonit> monitor = cassandraConnexion.monitor();
        stack.push("Hello CSTATUS");
        stack.push(monitor);
        return stack;
    }
}
