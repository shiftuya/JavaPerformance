package ru.nsu.fit.lab4;

import static org.objectweb.asm.Opcodes.ASTORE;
import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.ACC_STATIC;
import static org.objectweb.asm.Opcodes.ACC_SUPER;
import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.GETSTATIC;
import static org.objectweb.asm.Opcodes.GOTO;
import static org.objectweb.asm.Opcodes.IADD;
import static org.objectweb.asm.Opcodes.IAND;
import static org.objectweb.asm.Opcodes.ICONST_0;
import static org.objectweb.asm.Opcodes.ICONST_1;
import static org.objectweb.asm.Opcodes.IDIV;
import static org.objectweb.asm.Opcodes.IFEQ;
import static org.objectweb.asm.Opcodes.IFNE;
import static org.objectweb.asm.Opcodes.IF_ICMPEQ;
import static org.objectweb.asm.Opcodes.IF_ICMPGE;
import static org.objectweb.asm.Opcodes.IF_ICMPGT;
import static org.objectweb.asm.Opcodes.IF_ICMPLE;
import static org.objectweb.asm.Opcodes.IF_ICMPLT;
import static org.objectweb.asm.Opcodes.IF_ICMPNE;
import static org.objectweb.asm.Opcodes.ILOAD;
import static org.objectweb.asm.Opcodes.IMUL;
import static org.objectweb.asm.Opcodes.INTEGER;
import static org.objectweb.asm.Opcodes.INVOKESPECIAL;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;
import static org.objectweb.asm.Opcodes.IOR;
import static org.objectweb.asm.Opcodes.ISTORE;
import static org.objectweb.asm.Opcodes.ISUB;
import static org.objectweb.asm.Opcodes.RETURN;
import static org.objectweb.asm.Opcodes.V15;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import ru.nsu.fit.lab4.generated.MyLangParser.AssignmentStatementContext;
import ru.nsu.fit.lab4.generated.MyLangParser.BinaryArithmeticContext;
import ru.nsu.fit.lab4.generated.MyLangParser.BinaryArithmeticSignContext;
import ru.nsu.fit.lab4.generated.MyLangParser.BinaryLogicContext;
import ru.nsu.fit.lab4.generated.MyLangParser.BinaryLogicSignContext;
import ru.nsu.fit.lab4.generated.MyLangParser.CodeBlockContext;
import ru.nsu.fit.lab4.generated.MyLangParser.CodeContext;
import ru.nsu.fit.lab4.generated.MyLangParser.ComparisonContext;
import ru.nsu.fit.lab4.generated.MyLangParser.ComparisonSignContext;
import ru.nsu.fit.lab4.generated.MyLangParser.DeclarationStatementContext;
import ru.nsu.fit.lab4.generated.MyLangParser.EmptyLineContext;
import ru.nsu.fit.lab4.generated.MyLangParser.GotoExpressionContext;
import ru.nsu.fit.lab4.generated.MyLangParser.GotoStatementContext;
import ru.nsu.fit.lab4.generated.MyLangParser.IfStatementContext;
import ru.nsu.fit.lab4.generated.MyLangParser.IfclauseContext;
import ru.nsu.fit.lab4.generated.MyLangParser.IntAssignmentContext;
import ru.nsu.fit.lab4.generated.MyLangParser.IntDeclarationContext;
import ru.nsu.fit.lab4.generated.MyLangParser.IntLiteralContext;
import ru.nsu.fit.lab4.generated.MyLangParser.LabelDeclarationContext;
import ru.nsu.fit.lab4.generated.MyLangParser.LabelStatementContext;
import ru.nsu.fit.lab4.generated.MyLangParser.MinusExpressionContext;
import ru.nsu.fit.lab4.generated.MyLangParser.NegationContext;
import ru.nsu.fit.lab4.generated.MyLangParser.NonEmptyLineContext;
import ru.nsu.fit.lab4.generated.MyLangParser.ParenExpressionContext;
import ru.nsu.fit.lab4.generated.MyLangParser.PrintArithmeticContext;
import ru.nsu.fit.lab4.generated.MyLangParser.PrintLogicalContext;
import ru.nsu.fit.lab4.generated.MyLangParser.PrintStatementContext;
import ru.nsu.fit.lab4.generated.MyLangParser.PrintStrStatementContext;
import ru.nsu.fit.lab4.generated.MyLangParser.PrintStringContext;
import ru.nsu.fit.lab4.generated.MyLangParser.PrintstrContext;
import ru.nsu.fit.lab4.generated.MyLangParser.StringAssignmentContext;
import ru.nsu.fit.lab4.generated.MyLangParser.StringConcatContext;
import ru.nsu.fit.lab4.generated.MyLangParser.StringDeclarationContext;
import ru.nsu.fit.lab4.generated.MyLangParser.StringLiteralContext;
import ru.nsu.fit.lab4.generated.MyLangParser.VarReferenceContext;
import ru.nsu.fit.lab4.generated.MyLangParserListener;

public class MyLangListenerImpl implements MyLangParserListener {

  private static class MyStack<T> extends Stack<T> {
    private int maxSize = 0;
    @Override
    public T push(T item) {
      T res = super.push(item);
      if (size() > maxSize) {
        maxSize = size();
      }
      return res;
    }

    public int getMaxSize() {
      return maxSize;
    }
  }

  private final ClassWriter classWriter;
  private MethodVisitor methodVisitor;
  private final Stack<Label> currentLabels = new Stack<>();

  private final Map<Integer, Label> declaredLabels = new HashMap<>();

  private final Map<CodeContext, Set<String>> localVariableMap = new HashMap<>();
  private final Stack<CodeContext> codeBlockStack = new Stack<>();
  private final Map<String, Integer> localVariableNameMap = new HashMap<>();
  private final MyStack<Object> stackTypes = new MyStack<>();
  private final Map<Integer, Object> varTypes = new HashMap<>();


  public MyLangListenerImpl(String className) {
    classWriter = new ClassWriter(0);
    classWriter.visit(V15, ACC_PUBLIC | ACC_SUPER, className, null, "java/lang/Object", null);
    classWriter.visitSource(className + ".java", null);

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

    methodVisitor = classWriter
        .visitMethod(ACC_PUBLIC | ACC_STATIC, "main", "([Ljava/lang/String;)V", null, null);
    methodVisitor.visitCode();
  }

  public byte[] getBytes() {
    return classWriter.toByteArray();
  }


  @Override
  public void enterCode(CodeContext ctx) {
    codeBlockStack.push(ctx);
    localVariableMap.put(ctx, new HashSet<>());
  }

  @Override
  public void exitCode(CodeContext ctx) {
    codeBlockStack.pop();
    localVariableMap.remove(ctx);
    if (codeBlockStack.empty()) {
      methodVisitor.visitInsn(RETURN);
      methodVisitor.visitMaxs(stackTypes.getMaxSize(),100);
      methodVisitor.visitEnd();
      classWriter.visitEnd();
    }
  }

  @Override
  public void enterCodeBlock(CodeBlockContext ctx) {
    Label falseLabel = new Label();
    methodVisitor.visitJumpInsn(IFEQ, falseLabel);
    stackTypes.pop();

    currentLabels.push(falseLabel);
  }

  @Override
  public void exitCodeBlock(CodeBlockContext ctx) {

  }

  @Override
  public void enterIfclause(IfclauseContext ctx) {

  }

  @Override
  public void exitIfclause(IfclauseContext ctx) {
    Label falseLabel = currentLabels.pop();
    methodVisitor.visitLabel(falseLabel);
    methodVisitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
  }

  @Override
  public void enterLabelDeclaration(LabelDeclarationContext ctx) {
    int index = Integer.parseInt(ctx.INTLIT().toString());
    Label label;
    if (declaredLabels.containsKey(index)) {
      label = declaredLabels.get(index);
    } else {
      label = new Label();
      declaredLabels.put(index, label);
    }
    methodVisitor.visitLabel(label);

    visitFrame();
  }

  @Override
  public void exitLabelDeclaration(LabelDeclarationContext ctx) {
  }

  @Override
  public void enterNonEmptyLine(NonEmptyLineContext ctx) {

  }

  @Override
  public void exitNonEmptyLine(NonEmptyLineContext ctx) {

  }

  @Override
  public void enterEmptyLine(EmptyLineContext ctx) {

  }

  @Override
  public void exitEmptyLine(EmptyLineContext ctx) {

  }

  @Override
  public void enterIntDeclaration(IntDeclarationContext ctx) {
  }

  @Override
  public void exitIntDeclaration(IntDeclarationContext ctx) {

  }

  @Override
  public void enterStringDeclaration(StringDeclarationContext ctx) {

  }

  @Override
  public void exitStringDeclaration(StringDeclarationContext ctx) {

  }

  @Override
  public void enterIntAssignment(IntAssignmentContext ctx) {

  }

  @Override
  public void exitIntAssignment(IntAssignmentContext ctx) {
    int number;
    if (localVariableNameMap.containsKey(ctx.getChild(0).getText())) {
      number = localVariableNameMap.get(ctx.getChild(0).getText());
    } else {
      number = localVariableNameMap.keySet().size();
      localVariableNameMap.put(ctx.getChild(0).getText(), number);
      localVariableMap.get(codeBlockStack.peek()).add(ctx.getChild(0).getText());
    }
    methodVisitor.visitVarInsn(ISTORE, number);
    stackTypes.pop();
    varTypes.put(number, Opcodes.INTEGER);
  }

  @Override
  public void enterStringAssignment(StringAssignmentContext ctx) {

  }

  @Override
  public void exitStringAssignment(StringAssignmentContext ctx) {
    int number;
    if (localVariableNameMap.containsKey(ctx.getChild(0).getText())) {
      number = localVariableNameMap.get(ctx.getChild(0).getText());
    } else {
      number = localVariableNameMap.keySet().size();
      localVariableNameMap.put(ctx.getChild(0).getText(), number);
      localVariableMap.get(codeBlockStack.peek()).add(ctx.getChild(0).getText());
    }
    methodVisitor.visitVarInsn(ASTORE, number);
    stackTypes.pop();
    varTypes.put(number, "java/lang/String");
  }

  @Override
  public void enterPrintString(PrintStringContext ctx) {
    methodVisitor.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
    stackTypes.push("java/io/PrintStream");
  }

  @Override
  public void exitPrintString(PrintStringContext ctx) {
    methodVisitor
        .visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V",
            false);
    stackTypes.pop(); // remove arg
    stackTypes.pop(); // remove reference

  }

  @Override
  public void enterPrintArithmetic(PrintArithmeticContext ctx) {
    methodVisitor.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
    stackTypes.push("java/io/PrintStream");
  }

  @Override
  public void exitPrintArithmetic(PrintArithmeticContext ctx) {
    methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(I)V", false);
    stackTypes.pop(); // remove arg
    stackTypes.pop(); // remove reference

  }

  @Override
  public void enterPrintLogical(PrintLogicalContext ctx) {
    methodVisitor.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
    stackTypes.push("java/io/PrintStream");
  }

  @Override
  public void exitPrintLogical(PrintLogicalContext ctx) {
    methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Z)V", false);
    stackTypes.pop(); // remove arg
    stackTypes.pop(); // remove reference

  }

  @Override
  public void enterPrintstr(PrintstrContext ctx) {
    if (!localVariableNameMap.containsKey(ctx.ID().getText())) {
      throw new IllegalStateException("Unknown variable: " + ctx.ID().getText());
    }
    int index = localVariableNameMap.get(ctx.ID().getText());
    methodVisitor.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
    methodVisitor.visitVarInsn(ALOAD, index);
    methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
  }

  @Override
  public void exitPrintstr(PrintstrContext ctx) {

  }

  @Override
  public void enterGotoExpression(GotoExpressionContext ctx) {
    int index = Integer.parseInt(ctx.INTLIT().toString());

    Label label;
    if (declaredLabels.containsKey(index)) {
      label = declaredLabels.get(index);
    } else {
      label = new Label();
      declaredLabels.put(index, label);
    }
    methodVisitor.visitJumpInsn(GOTO, label);
    methodVisitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
  }

  @Override
  public void exitGotoExpression(GotoExpressionContext ctx) {

  }

  @Override
  public void enterBinaryArithmetic(BinaryArithmeticContext ctx) {

  }

  @Override
  public void exitBinaryArithmetic(BinaryArithmeticContext ctx) {
    methodVisitor.visitInsn(switch (ctx.children.get(2).getText()) {
      case "+" -> IADD;
      case "-" -> ISUB;
      case "/" -> IDIV;
      case "*" -> IMUL;
      default -> throw new IllegalStateException(
          "Unexpected value: " + ctx.children.get(2).getText());
    });
    stackTypes.pop(); // leave result instead of 2 operands
  }

  @Override
  public void enterIntLiteral(IntLiteralContext ctx) {
    methodVisitor.visitLdcInsn(Integer.parseInt(ctx.getText()));
    stackTypes.push(INTEGER);
  }

  @Override
  public void exitIntLiteral(IntLiteralContext ctx) {
// ignore
  }

  @Override
  public void enterVarReference(VarReferenceContext ctx) {
    if (!localVariableNameMap.containsKey(ctx.ID().toString())) {
      throw new IllegalStateException("Local variable not declared: " + ctx.ID().toString());
    }
    methodVisitor.visitVarInsn(ILOAD, localVariableNameMap.get(ctx.ID().toString()));
    stackTypes.push(varTypes.get(localVariableNameMap.get(ctx.ID().toString())));
  }

  @Override
  public void exitVarReference(VarReferenceContext ctx) {

  }

  @Override
  public void enterParenExpression(ParenExpressionContext ctx) {

  }

  @Override
  public void exitParenExpression(ParenExpressionContext ctx) {

  }

  @Override
  public void enterMinusExpression(MinusExpressionContext ctx) {

  }

  @Override
  public void exitMinusExpression(MinusExpressionContext ctx) {

  }

  @Override
  public void enterStringLiteral(StringLiteralContext ctx) {
    methodVisitor.visitLdcInsn(
        ctx.STRINGLIT().toString().substring(1, ctx.STRINGLIT().toString().length() - 1));
    stackTypes.push("java/lang/String");
  }

  @Override
  public void exitStringLiteral(StringLiteralContext ctx) {
  }

  @Override
  public void enterStringConcat(StringConcatContext ctx) {
    methodVisitor.visitLdcInsn(
        ctx.getChild(0).getText().substring(1, ctx.getChild(0).getText().length() - 1));
    stackTypes.push("java/lang/String");
  }

  @Override
  public void exitStringConcat(StringConcatContext ctx) {
    // stack contains 2 strings
    methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "concat",
        "(Ljava/lang/String;)Ljava/lang/String;", false);
    // stack contains 1 string
    stackTypes.pop();

  }

  @Override
  public void enterComparison(ComparisonContext ctx) {
// ignore
  }

  @Override
  public void exitComparison(ComparisonContext ctx) {
    Label trueLabel = new Label();
    Label falseLabel = new Label();
    methodVisitor.visitJumpInsn(
        switch (ctx.children.get(1).getText()) {
          case ">" -> IF_ICMPGT;
          case "<" -> IF_ICMPLT;
          case "==" -> IF_ICMPEQ;
          case "!=" -> IF_ICMPNE;
          case ">=" -> IF_ICMPGE;
          case "<=" -> IF_ICMPLE;
          default -> throw new IllegalStateException(
              "Unexpected value: " + ctx.children.get(1).getText());
        }, trueLabel);
    stackTypes.pop();
    stackTypes.pop();

    methodVisitor.visitInsn(ICONST_0);

    methodVisitor.visitJumpInsn(GOTO, falseLabel);
    methodVisitor.visitLabel(trueLabel);

    visitFrame();

    methodVisitor.visitInsn(ICONST_1);
    methodVisitor.visitLabel(falseLabel);
    stackTypes.push(INTEGER);

    visitFrame();
  }

  @Override
  public void enterBinaryLogic(BinaryLogicContext ctx) {

  }

  @Override
  public void exitBinaryLogic(BinaryLogicContext ctx) {
    Label trueLabel = new Label();
    Label falseLabel = new Label();
    methodVisitor.visitInsn(
        switch (ctx.children.get(2).getText()) {
          case "&&" -> IAND;
          case "||" -> IOR;
          default -> throw new IllegalStateException(
              "Unexpected value: " + ctx.children.get(2).getText());
        });
    stackTypes.pop();

    methodVisitor.visitJumpInsn(IFNE, trueLabel);
    stackTypes.pop();


    methodVisitor.visitInsn(ICONST_0);

    methodVisitor.visitJumpInsn(GOTO, falseLabel);
    methodVisitor.visitLabel(trueLabel);

    visitFrame();

    methodVisitor.visitInsn(ICONST_1);
    methodVisitor.visitLabel(falseLabel);
    stackTypes.push(INTEGER);

    visitFrame();
  }

  @Override
  public void enterNegation(NegationContext ctx) {

  }

  @Override
  public void exitNegation(NegationContext ctx) {
    Label trueLabel = new Label();
    Label falseLabel = new Label();
    methodVisitor.visitJumpInsn(IFEQ, trueLabel);
    stackTypes.pop();


    methodVisitor.visitInsn(ICONST_0);

    methodVisitor.visitJumpInsn(GOTO, falseLabel);
    methodVisitor.visitLabel(trueLabel);

    visitFrame();

    methodVisitor.visitInsn(ICONST_1);
    methodVisitor.visitLabel(falseLabel);
    stackTypes.push(INTEGER);

    visitFrame();
  }

  @Override
  public void enterAssignmentStatement(AssignmentStatementContext ctx) {

  }

  @Override
  public void exitAssignmentStatement(AssignmentStatementContext ctx) {

  }

  @Override
  public void enterDeclarationStatement(DeclarationStatementContext ctx) {

  }

  @Override
  public void exitDeclarationStatement(DeclarationStatementContext ctx) {

  }

  @Override
  public void enterPrintStatement(PrintStatementContext ctx) {

  }

  @Override
  public void exitPrintStatement(PrintStatementContext ctx) {

  }

  @Override
  public void enterPrintStrStatement(PrintStrStatementContext ctx) {

  }

  @Override
  public void exitPrintStrStatement(PrintStrStatementContext ctx) {

  }

  @Override
  public void enterGotoStatement(GotoStatementContext ctx) {

  }

  @Override
  public void exitGotoStatement(GotoStatementContext ctx) {

  }

  @Override
  public void enterIfStatement(IfStatementContext ctx) {

  }

  @Override
  public void exitIfStatement(IfStatementContext ctx) {

  }

  @Override
  public void enterLabelStatement(LabelStatementContext ctx) {
// ignore
  }

  @Override
  public void exitLabelStatement(LabelStatementContext ctx) {

  }

  @Override
  public void enterBinaryArithmeticSign(BinaryArithmeticSignContext ctx) {

  }

  @Override
  public void exitBinaryArithmeticSign(BinaryArithmeticSignContext ctx) {

  }

  @Override
  public void enterComparisonSign(ComparisonSignContext ctx) {

  }

  @Override
  public void exitComparisonSign(ComparisonSignContext ctx) {

  }

  @Override
  public void enterBinaryLogicSign(BinaryLogicSignContext ctx) {

  }

  @Override
  public void exitBinaryLogicSign(BinaryLogicSignContext ctx) {

  }

  @Override
  public void visitTerminal(TerminalNode node) {
  }

  @Override
  public void visitErrorNode(ErrorNode node) {

  }

  @Override
  public void enterEveryRule(ParserRuleContext ctx) {

  }

  @Override
  public void exitEveryRule(ParserRuleContext ctx) {

  }

  private void visitFrame() {
    Object[] objects = new Object[varTypes.size()];
    for (int i = 0; i < varTypes.size(); ++i) {
      objects[i] = varTypes.get(i);
    }
    methodVisitor.visitFrame(Opcodes.F_FULL, varTypes.size(), objects, stackTypes.size(), stackTypes.toArray());

  }
}
