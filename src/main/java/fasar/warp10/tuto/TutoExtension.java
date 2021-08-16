package fasar.warp10.tuto;

import io.warp10.WarpConfig;
import io.warp10.warp.sdk.WarpScriptExtension;

import java.util.HashMap;
import java.util.Map;

public class TutoExtension extends WarpScriptExtension {
  
  private static final Map<String,Object> functions;
  
  static {
    functions = new HashMap<String,Object>();

    String name = WarpConfig.getProperty("warpscript.tuto.helloName");
    functions.put("HELLOJ", new HELLOJ("HELLOJ", name));

  }
  
  @Override
  public Map<String, Object> getFunctions() {
    return functions;
  }


}
