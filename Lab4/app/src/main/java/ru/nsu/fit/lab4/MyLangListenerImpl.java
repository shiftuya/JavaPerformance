package ru.nsu.fit.lab4;

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

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import ru.nsu.fit.lab4.generated.MyLangParser.AssignmentStatementContext;
import ru.nsu.fit.lab4.generated.MyLangParser.BinaryArighmeticContext;
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
import ru.nsu.fit.lab4.generated.MyLangParser.PrintStringContext;
import ru.nsu.fit.lab4.generated.MyLangParser.StringAssignmentContext;
import ru.nsu.fit.lab4.generated.MyLangParser.StringConcatContext;
import ru.nsu.fit.lab4.generated.MyLangParser.StringDeclarationContext;
import ru.nsu.fit.lab4.generated.MyLangParser.StringLiteralContext;
import ru.nsu.fit.lab4.generated.MyLangParser.VarReferenceContext;
import ru.nsu.fit.lab4.generated.MyLangParserBaseListener;
import ru.nsu.fit.lab4.generated.MyLangParserListener;

public class MyLangListenerImpl implements MyLangParserListener {

  private ClassWriter classWriter;
  private MethodVisitor methodVisitor;

  private Map<Integer, Label> declaredLabels = new HashMap<>();

  public MyLangListenerImpl() {
    classWriter = new ClassWriter(0);
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


    methodVisitor = classWriter
        .visitMethod(ACC_PUBLIC | ACC_STATIC, "main", "([Ljava/lang/String;)V", null, null);
    methodVisitor.visitCode();
  }

  public byte[] getBytes() {
    return classWriter.toByteArray();
  }

  private Map<CodeContext, Set<String>> localVariableMap = new HashMap<>();
  private Stack<CodeContext> codeBlockStack = new Stack<>();

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
      methodVisitor.visitMaxs(300, 400); // todo think
      methodVisitor.visitEnd();
      classWriter.visitEnd();
    }
  }

  @Override
  public void enterCodeBlock(CodeBlockContext ctx) {

  }

  @Override
  public void exitCodeBlock(CodeBlockContext ctx) {

  }

  @Override
  public void enterIfclause(IfclauseContext ctx) {

  }

  @Override
  public void exitIfclause(IfclauseContext ctx) {

  }

  @Override
  public void enterLabelDeclaration(LabelDeclarationContext ctx) {

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

  }

  @Override
  public void enterStringAssignment(StringAssignmentContext ctx) {

  }

  @Override
  public void exitStringAssignment(StringAssignmentContext ctx) {

  }

  @Override
  public void enterPrintString(PrintStringContext ctx) {
    methodVisitor.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
  }

  @Override
  public void exitPrintString(PrintStringContext ctx) {
    methodVisitor
        .visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V",
            false);
  }

  @Override
  public void enterPrintArithmetic(PrintArithmeticContext ctx) {

  }

  @Override
  public void exitPrintArithmetic(PrintArithmeticContext ctx) {

  }

  @Override
  public void enterPrintLogical(PrintLogicalContext ctx) {

  }

  @Override
  public void exitPrintLogical(PrintLogicalContext ctx) {

  }

  @Override
  public void enterGotoExpression(GotoExpressionContext ctx) {

  }

  @Override
  public void exitGotoExpression(GotoExpressionContext ctx) {

  }

  @Override
  public void enterBinaryArighmetic(BinaryArighmeticContext ctx) {

  }

  @Override
  public void exitBinaryArighmetic(BinaryArighmeticContext ctx) {

  }

  @Override
  public void enterIntLiteral(IntLiteralContext ctx) {

  }

  @Override
  public void exitIntLiteral(IntLiteralContext ctx) {

  }

  @Override
  public void enterVarReference(VarReferenceContext ctx) {

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
    System.out.println("enter string literal");
    methodVisitor.visitLdcInsn(
        ctx.STRINGLIT().toString().substring(1, ctx.STRINGLIT().toString().length() - 1));
  }

  @Override
  public void exitStringLiteral(StringLiteralContext ctx) {
    System.out.println("exit string literal");
  }

  @Override
  public void enterStringConcat(StringConcatContext ctx) {
    System.out.println("enter string concat");
    System.out.println(ctx.getChildCount());
    for(var ch : ctx.children) {
      System.out.println(ch.getClass());
    }
    methodVisitor.visitLdcInsn(
        ctx.getChild(0).getText().substring(1, ctx.getChild(0).getText().length() - 1));
    System.out.println(ctx.getChild(0).getText());
  }

  @Override
  public void exitStringConcat(StringConcatContext ctx) {
    // stack contains 2 strings
    methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "concat",
        "(Ljava/lang/String;)Ljava/lang/String;", false);
    // stack contains 1 string

    System.out.println("exit string concat");
  }

  @Override
  public void enterComparison(ComparisonContext ctx) {

  }

  @Override
  public void exitComparison(ComparisonContext ctx) {

  }

  @Override
  public void enterBinaryLogic(BinaryLogicContext ctx) {

  }

  @Override
  public void exitBinaryLogic(BinaryLogicContext ctx) {

  }

  @Override
  public void enterNegation(NegationContext ctx) {

  }

  @Override
  public void exitNegation(NegationContext ctx) {

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
}
