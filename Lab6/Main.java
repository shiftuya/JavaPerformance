import java.util.Map;

public class Main {
  static {
    System.loadLibrary("cpuinfo");
  }

  static native Map<String, String> getCpuInfo();

  public static void main(String[] args) {
    Map<String, String> cpuInfo = getCpuInfo();
    //System.out.println(cpuInfo);
    System.out.println("Model name: " + cpuInfo.get("model name"));
    System.out.println("CPU cores: " + cpuInfo.get("cpu cores"));
    System.out.println("CPU MHz: " + cpuInfo.get("cpu MHz"));
  }
}

