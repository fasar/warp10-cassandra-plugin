package fasar.warp10.cassandra;

import fasar.utils.StringUtils;
import io.warp10.WarpConfig;
import io.warp10.script.MemoryWarpScriptStack;
import io.warp10.script.WarpScriptLib;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.StringReader;

public class CSTATUSTest {
    @BeforeClass
    public static void beforeClass() throws Exception {
        StringBuilder properties = new StringBuilder();
        File pptF = new File("81-cassandra-plugin-extension.conf");
        System.out.println("Try to load " + pptF.getAbsolutePath());
        properties.append(FileUtils.readFileToString(pptF));
        properties.append("\nwarp.timeunits=us\n");
        WarpConfig.safeSetProperties(new StringReader(properties.toString()));
        WarpScriptLib.register(new CassandraExtension());
    }

    @Test
    public void simpleTest() throws Exception {
        MemoryWarpScriptStack stack = new MemoryWarpScriptStack(null, null);
        stack.maxLimits();
        //
        // For this example, we only check that the functions run without error, and that the sizes are correct
        //
        stack.exec("CSTATUS");
        // printing
        String dump = stack.dump(stack.depth());
        Assert.assertFalse(StringUtils.isBlank(dump));
        System.out.println(dump);
    }
}