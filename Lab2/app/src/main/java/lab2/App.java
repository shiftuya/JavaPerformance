/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package lab2;

import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.ACC_STATIC;
import static org.objectweb.asm.Opcodes.ACC_SUPER;
import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.ASTORE;
import static org.objectweb.asm.Opcodes.BIPUSH;
import static org.objectweb.asm.Opcodes.DUP;
import static org.objectweb.asm.Opcodes.GETSTATIC;
import static org.objectweb.asm.Opcodes.GOTO;
import static org.objectweb.asm.Opcodes.IFEQ;
import static org.objectweb.asm.Opcodes.IF_ICMPGE;
import static org.objectweb.asm.Opcodes.IF_ICMPLE;
import static org.objectweb.asm.Opcodes.ILOAD;
import static org.objectweb.asm.Opcodes.INVOKESPECIAL;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;
import static org.objectweb.asm.Opcodes.ISTORE;
import static org.objectweb.asm.Opcodes.NEW;
import static org.objectweb.asm.Opcodes.RETURN;
import static org.objectweb.asm.Opcodes.V15;

import java.nio.file.Files;
import java.nio.file.Paths;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class App {

  public static void main(String[] args) throws Exception {
    String className = "Main.class";
    Files.write(Paths.get(className), dump());
    System.out.println("Output: " + Paths.get(className).toAbsolutePath());
  }

  public static byte[] dump() throws Exception {

    ClassWriter classWriter = new ClassWriter(0);
    MethodVisitor methodVisitor;

    classWriter.visit(V15, ACC_PUBLIC | ACC_SUPER, "Main", null, "java/lang/Object", null);

    classWriter.visitSource("Main.java", null);

    {
      methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
      methodVisitor.visitCode();
      Label label0 = new Label();
      methodVisitor.visitLabel(label0);
      methodVisitor.visitLineNumber(4, label0);
      methodVisitor.visitVarInsn(ALOAD, 0);
      methodVisitor.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
      methodVisitor.visitInsn(RETURN);
      methodVisitor.visitMaxs(1, 1);
      methodVisitor.visitEnd();
    }
    {
      methodVisitor = classWriter
          .visitMethod(ACC_PUBLIC | ACC_STATIC, "main", "([Ljava/lang/String;)V", null, null);
      methodVisitor.visitCode();
      Label label0 = new Label();
      methodVisitor.visitLabel(label0);
      methodVisitor.visitLineNumber(7, label0);
      methodVisitor.visitTypeInsn(NEW, "java/util/Random");
      methodVisitor.visitInsn(DUP);
      methodVisitor.visitMethodInsn(INVOKESPECIAL, "java/util/Random", "<init>", "()V", false);
      methodVisitor.visitIntInsn(BIPUSH, 100);
      methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/util/Random", "nextInt", "(I)I", false);
      methodVisitor.visitVarInsn(ISTORE, 1);
      Label label1 = new Label();
      methodVisitor.visitLabel(label1);
      methodVisitor.visitLineNumber(9, label1);
      methodVisitor.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
      methodVisitor.visitLdcInsn("I've thought a number, try to guess!");
      methodVisitor
          .visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V",
              false);
      Label label2 = new Label();
      methodVisitor.visitLabel(label2);
      methodVisitor.visitLineNumber(11, label2);
      methodVisitor.visitTypeInsn(NEW, "java/util/Scanner");
      methodVisitor.visitInsn(DUP);
      methodVisitor.visitFieldInsn(GETSTATIC, "java/lang/System", "in", "Ljava/io/InputStream;");
      methodVisitor
          .visitMethodInsn(INVOKESPECIAL, "java/util/Scanner", "<init>", "(Ljava/io/InputStream;)V",
              false);
      methodVisitor.visitVarInsn(ASTORE, 2);
      Label label3 = new Label();
      methodVisitor.visitLabel(label3);
      methodVisitor.visitLineNumber(13, label3);
      methodVisitor
          .visitFrame(Opcodes.F_APPEND, 2, new Object[]{Opcodes.INTEGER, "java/util/Scanner"}, 0,
              null);
      methodVisitor.visitVarInsn(ALOAD, 2);
      methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/util/Scanner", "hasNextInt", "()Z", false);
      Label label4 = new Label();
      methodVisitor.visitJumpInsn(IFEQ, label4);
      Label label5 = new Label();
      methodVisitor.visitLabel(label5);
      methodVisitor.visitLineNumber(14, label5);
      methodVisitor.visitVarInsn(ALOAD, 2);
      methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/util/Scanner", "nextInt", "()I", false);
      methodVisitor.visitVarInsn(ISTORE, 3);
      Label label6 = new Label();
      methodVisitor.visitLabel(label6);
      methodVisitor.visitLineNumber(15, label6);
      methodVisitor.visitVarInsn(ILOAD, 3);
      methodVisitor.visitVarInsn(ILOAD, 1);
      Label label7 = new Label();
      methodVisitor.visitJumpInsn(IF_ICMPGE, label7);
      Label label8 = new Label();
      methodVisitor.visitLabel(label8);
      methodVisitor.visitLineNumber(16, label8);
      methodVisitor.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
      methodVisitor.visitLdcInsn("Greater");
      methodVisitor
          .visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V",
              false);
      Label label9 = new Label();
      methodVisitor.visitJumpInsn(GOTO, label9);
      methodVisitor.visitLabel(label7);
      methodVisitor.visitLineNumber(17, label7);
      methodVisitor.visitFrame(Opcodes.F_APPEND, 1, new Object[]{Opcodes.INTEGER}, 0, null);
      methodVisitor.visitVarInsn(ILOAD, 3);
      methodVisitor.visitVarInsn(ILOAD, 1);
      Label label10 = new Label();
      methodVisitor.visitJumpInsn(IF_ICMPLE, label10);
      Label label11 = new Label();
      methodVisitor.visitLabel(label11);
      methodVisitor.visitLineNumber(18, label11);
      methodVisitor.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
      methodVisitor.visitLdcInsn("Lower");
      methodVisitor
          .visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V",
              false);
      methodVisitor.visitJumpInsn(GOTO, label9);
      methodVisitor.visitLabel(label10);
      methodVisitor.visitLineNumber(20, label10);
      methodVisitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
      methodVisitor.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
      methodVisitor.visitLdcInsn("Exactly! Good bye!");
      methodVisitor
          .visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V",
              false);
      Label label12 = new Label();
      methodVisitor.visitLabel(label12);
      methodVisitor.visitLineNumber(21, label12);
      methodVisitor.visitInsn(RETURN);
      methodVisitor.visitLabel(label9);
      methodVisitor.visitLineNumber(23, label9);
      methodVisitor.visitFrame(Opcodes.F_CHOP, 1, null, 0, null);
      methodVisitor.visitJumpInsn(GOTO, label3);
      methodVisitor.visitLabel(label4);
      methodVisitor.visitLineNumber(25, label4);
      methodVisitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
      methodVisitor.visitInsn(RETURN);
      methodVisitor.visitMaxs(3, 4);
      methodVisitor.visitEnd();
    }
    classWriter.visitEnd();

    return classWriter.toByteArray();
  }


}
