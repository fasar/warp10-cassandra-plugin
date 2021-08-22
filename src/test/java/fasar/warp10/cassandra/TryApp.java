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
        testCStatus();
        testCSelect();

        System.exit(0);
    }

    private static void testCSelect() throws WarpScriptException {
        MemoryWarpScriptStack stack = new MemoryWarpScriptStack(null, null);
        stack.maxLimits();
        stack.exec("'SELECT * FROM ifis.properties' CSELECT");
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
