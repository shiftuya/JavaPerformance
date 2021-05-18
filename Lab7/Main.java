import org.graalvm.polyglot.*;
import java.io.File;
import java.util.HashMap;

public class Main {
  public static void main(String[] args) throws Exception {
    Context context = Context.newBuilder()
      .allowAllAccess(true)
      .build();

    File file = new File("main.bc");
    Source source = Source.newBuilder("llvm", file).build();
    Value cpart = context.eval(source);
    Value function = cpart.getMember("getCpuInfo");
    var cpuInfo = function.execute().as(HashMap.class);
    System.out.println("Model name: " + cpuInfo.get("model name"));
    System.out.println("CPU cores: " + cpuInfo.get("cpu cores"));
    System.out.println("CPU MHz: " + cpuInfo.get("cpu MHz"));
  }

}
