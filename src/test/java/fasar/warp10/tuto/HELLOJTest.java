package fasar.warp10.tuto;

import io.warp10.WarpConfig;
import io.warp10.script.MemoryWarpScriptStack;
import io.warp10.script.WarpScriptLib;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.StringReader;

public class HELLOJTest {
    @BeforeClass
    public static void beforeClass() throws Exception {
        StringBuilder properties = new StringBuilder();
        properties.append("warp.timeunits=us\n");
        properties.append("warpscript.tuto.helloName=Mec\n");
        WarpConfig.safeSetProperties(new StringReader(properties.toString()));
        WarpScriptLib.register(new TutoExtension());
    }

    @Test
    public void RANDOMSTRING_test() throws Exception {
        MemoryWarpScriptStack stack = new MemoryWarpScriptStack(null, null);
        stack.maxLimits();
        //
        // For this example, we only check that the functions run without error, and that the sizes are correct
        //
        stack.exec("HELLOJ");
        stack.exec("'Hello from Java Mec' == ASSERT");
        // printing
        System.out.println(stack.dump(stack.depth()));
    }
}