package fasar.warp10.cassandra;

import io.warp10.WarpConfig;
import io.warp10.script.MemoryWarpScriptStack;
import io.warp10.script.WarpScriptException;
import io.warp10.script.WarpScriptLib;
import org.apache.commons.io.FileUtils;

import java.io.*;

public class TryApp {
    public static void main(String[] args) throws IOException, WarpScriptException {
        StringBuilder properties = new StringBuilder();
        File pptF = new File("fasar.warp10.cassandra-extension.conf");
        System.out.println("Try to load " + pptF.getAbsolutePath());
        properties.append(FileUtils.readFileToString(pptF));
        properties.append("\nwarp.timeunits=us\n");
        WarpConfig.safeSetProperties(new StringReader(properties.toString()));
        WarpScriptLib.register(new CassandraExtension());
        testCFetch();

        System.exit(0);
    }

    private static void testCFetch() throws WarpScriptException {
        MemoryWarpScriptStack stack = new MemoryWarpScriptStack(null, null);
        stack.maxLimits();
        stack.push("'SELECT name, ts, val FROM tag4_2020 limit 1000'");
        stack.push("[ 1 -1 -1 -1 2 ]");
        stack.push("CFETCH");
        stack.progress();
        // printing
        System.out.println(stack.dump(stack.depth()));

        stack.clear();
    }

    private static void testCSelect() throws WarpScriptException {
        MemoryWarpScriptStack stack = new MemoryWarpScriptStack(null, null);
        stack.maxLimits();
        stack.exec("'SELECT * FROM properties' CSELECT");
        // printing
        System.out.println(stack.dump(stack.depth()));

        stack.clear();

    }

    private static void testCSelect2() throws WarpScriptException {
        MemoryWarpScriptStack stack = new MemoryWarpScriptStack(null, null);
        stack.maxLimits();
        stack.exec("'SELECT * FROM tag4_2020 LIMIT 1000' CSELECT");
        // printing
        System.out.println(stack.dump(stack.depth()));

        stack.clear();

    }


    static void testCStatus() throws WarpScriptException {
        MemoryWarpScriptStack stack = new MemoryWarpScriptStack(null, null);
        stack.maxLimits();
        stack.exec("CSTATUS");
        // printing
        System.out.println(stack.dump(stack.depth()));

        stack.clear();
    }
}
