package agent;

import java.lang.instrument.Instrumentation;

public class Agent {

  public static void premain(String args, Instrumentation inst) {
    Runtime.getRuntime().addShutdownHook(new Thread(
        () -> System.out.println("Total loaded classes: " + inst.getAllLoadedClasses().length)));

    try {
      Class<?> clazz = Class.forName("nsu.fit.javaperf.TransactionProcessor");
      inst.addTransformer(
          new TransactionProcessorTransformer(clazz), true);
      inst.retransformClasses(clazz);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
