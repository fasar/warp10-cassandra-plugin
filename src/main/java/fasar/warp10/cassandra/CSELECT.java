package fasar.warp10.cassandra;

import com.datastax.driver.core.ColumnDefinitions;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import fasar.cassandra.CassandraConnexion;
import io.warp10.script.NamedWarpScriptFunction;
import io.warp10.script.WarpScriptException;
import io.warp10.script.WarpScriptStack;
import io.warp10.script.WarpScriptStackFunction;

import java.util.ArrayList;
import java.util.List;

public class CSELECT extends NamedWarpScriptFunction implements WarpScriptStackFunction {
    private CassandraConnexion cassandraConnexion;

    public CSELECT(
            String functionName,
            CassandraConnexion cassandraConnexion
    ) {
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
        if (!query.toLowerCase().contains("select")) {
            throw new WarpScriptException("expect the CQL Select Query String on the top of the stack.");
        }

        Session session = cassandraConnexion.getSession();
        ResultSet execute = session.execute(query);

        ColumnDefinitions columnDefinitions = execute.getColumnDefinitions();
        List<ColumnDefinitions.Definition> definitions = columnDefinitions.asList();
        int nbElement = definitions.size();
        List<String> headers = new ArrayList<>(nbElement);
        for (int i = 0; i < nbElement; i++) {
            headers.add(definitions.get(i).getName());
        }

        List<List<? extends Object>> values = new ArrayList<>();
        values.add(headers);

        for (Row row : execute) {
            List<Object> resValues = new ArrayList<>(nbElement);
            for (int i = 0; i < nbElement; i++) {
                Object s1 = row.getObject(i);
                resValues.add(s1);
            }
            values.add(resValues);
        }

        stack.push(values);
        return stack;
    }
}
