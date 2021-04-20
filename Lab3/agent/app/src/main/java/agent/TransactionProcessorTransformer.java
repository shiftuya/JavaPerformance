package agent;


import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

public class TransactionProcessorTransformer implements ClassFileTransformer {

  private final Class<?> clazz;

  TransactionProcessorTransformer(Class<?> clazz) {
    this.clazz = clazz;
  }

  @Override
  public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
      ProtectionDomain protectionDomain, byte[] classfileBuffer) {

    if (!className.equals(clazz.getName().replaceAll("\\.", "/"))) {
      return classfileBuffer;
    }

    ClassPool classPool = ClassPool.getDefault();

    try {
      CtClass ctClass = classPool.get(clazz.getName());

      CtMethod tx = ctClass.getDeclaredMethod("processTransaction");
      tx.insertBefore("txNum += 99;");

      CtMethod main = ctClass.getDeclaredMethod("main");
      main.addLocalVariable("max", CtClass.intType);
      main.addLocalVariable("min", CtClass.intType);
      main.addLocalVariable("sum", CtClass.intType);
      main.insertBefore(
          "min = Integer.MAX_VALUE;"
              + "max = 0;"
              + "sum = 0;"
      );
      main.insertAfter(
          "System.out.println();"
              + "System.out.println(\"Max: \" + max + \"ms\");"
              + "System.out.println(\"Min: \" + min + \"ms\");"
              + "System.out.println(\"Average: \" + (sum / 10) + \"ms\");"
      );
      main.instrument(new ExprEditor() {
        public void edit(MethodCall methodCall) throws CannotCompileException {
          if (methodCall.getClassName().equals(clazz.getName()) &&
              methodCall.getMethodName().equals("processTransaction")) {
            methodCall.replace(
                "int startTime = System.currentTimeMillis();"
                    + "tp.processTransaction(i);"
                    + "int time = System.currentTimeMillis() - startTime;"
                    + "if (time > max) max = time;"
                    + "if (time < min) min = time;"
                    + "sum += time;"
            );
          }
        }
      });

      return ctClass.toBytecode();
    } catch (Exception e) {
      e.printStackTrace();
    }

    return classfileBuffer;
  }
}
