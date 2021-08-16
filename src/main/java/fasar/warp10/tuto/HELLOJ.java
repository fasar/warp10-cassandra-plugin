package fasar.warp10.tuto;

import io.warp10.script.NamedWarpScriptFunction;
import io.warp10.script.WarpScriptException;
import io.warp10.script.WarpScriptStack;
import io.warp10.script.WarpScriptStackFunction;

public class HELLOJ extends NamedWarpScriptFunction implements WarpScriptStackFunction {

  private final String name;

  public HELLOJ(String helloj, String name) {
    super(name);
    this.name = name;
  }

  @Override
  public Object apply(WarpScriptStack stack) throws WarpScriptException {

    //
    // Insert your function code here
    // 
    stack.push("Hello from Java " + name );
    return stack;
  }
}
