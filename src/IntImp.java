import value.*;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class IntImp extends ImpBaseVisitor<Value> {

    private final Conf conf;

    public IntImp(Conf conf) {
        this.conf = conf;
    }

    private ComValue visitCom(ImpParser.ComContext ctx) {
        return (ComValue) visit(ctx);
    }

    private FunValue visitFun(ImpParser.FunContext ctx) {
        return (FunValue) visit(ctx);
    }

    private ExpValue<?> visitExp(ImpParser.ExpContext ctx) {
        return (ExpValue<?>) visit(ctx);
    }

    private ExpValue<?> visitArnoldExp(ImpParser.ArnoldExpContext ctx) { return (ExpValue<?>) visit(ctx); }

    private ComValue visitArnoldCom(ImpParser.ArnoldComContext ctx) { return (ComValue) visit(ctx); }

    private ComValue visitArnoldStmnt(ImpParser.ArnoldStmntContext ctx) { return (ComValue) visit(ctx); }

    private int visitNatExp(ImpParser.ExpContext ctx) {
        try {
            return ((NatValue) visitExp(ctx)).toJavaValue();
        } catch (ClassCastException e) {
            System.err.println("Type mismatch exception!");
            System.err.println("@" + ctx.start.getLine() + ":" + ctx.start.getCharPositionInLine());
            System.err.println(">>>>>>>>>>>>>>>>>>>>>>>>");
            System.err.println(ctx.getText());
            System.err.println("<<<<<<<<<<<<<<<<<<<<<<<<");
            System.err.println("> Natural expression expected.");
            System.exit(1);
        }

        return 0; // unreachable code
    }

    private Float visitArnoldFloatExp(ImpParser.ArnoldExpContext ctx) {
        try {
            return ((FloatValue) visitArnoldExp(ctx)).toJavaValue();
        } catch (ClassCastException e) {
            System.err.println("Type mismatch exception!");
            System.err.println("@" + ctx.start.getLine() + ":" + ctx.start.getCharPositionInLine());
            System.err.println(">>>>>>>>>>>>>>>>>>>>>>>>");
            System.err.println(ctx.getText());
            System.err.println("<<<<<<<<<<<<<<<<<<<<<<<<");
            System.err.println("> Float expression expected.");
            System.exit(1);
        }

        return 0f; // unreachable code
    }

    private boolean visitBoolExp(ImpParser.ExpContext ctx) {
        try {
            return ((BoolValue) visitExp(ctx)).toJavaValue();
        } catch (ClassCastException e) {
            System.err.println("Type mismatch exception!");
            System.err.println("@" + ctx.start.getLine() + ":" + ctx.start.getCharPositionInLine());
            System.err.println(">>>>>>>>>>>>>>>>>>>>>>>>");
            System.err.println(ctx.getText());
            System.err.println("<<<<<<<<<<<<<<<<<<<<<<<<");
            System.err.println("> Boolean expression expected.");
            System.exit(1);
        }

        return false; // unreachable code
    }

    @Override
    public FunValue visitProg(ImpParser.ProgContext ctx) {
        return visitFun(ctx.fun());
    }

    @Override
    public Value visitArnold(ImpParser.ArnoldContext ctx) { return visit(ctx.arnoldProg()); }

    @Override
    public ComValue visitIf(ImpParser.IfContext ctx) {
        return visitBoolExp(ctx.exp())
                ? visitCom(ctx.com(0))
                : visitCom(ctx.com(1));
    }

    @Override
    public Value visitIfelse(ImpParser.IfelseContext ctx) {
        return visitArnoldFloatExp(ctx.arnoldExp()) > 0f
                ? visitArnoldStmnt(ctx.arnoldStmnt(0))
                : visitArnoldStmnt(ctx.arnoldStmnt(1));
    }

    @Override
    public ComValue visitAssign(ImpParser.AssignContext ctx) {
        String id = ctx.ID().getText();
        ExpValue<?> v = visitExp(ctx.exp());

        if (conf.getInFun()) {
            conf.getActiveFun().insertFunVar(id, v);
        } else {
            conf.update(id, v);
        }

        return ComValue.INSTANCE;
    }

    @Override
    public Value visitVardecl(ImpParser.VardeclContext ctx) {
        String id = ctx.ID().getText();
        ExpValue<?> v = visitArnoldExp(ctx.arnoldExp());

        conf.updateArnold(id, v);

        return ComValue.INSTANCE;
    }

    @Override
    public Value visitVarassign(ImpParser.VarassignContext ctx) {
        String id = ctx.ID().getText();
        ExpValue<?> v = visitArnoldExp(ctx.arnoldExp());

        conf.updateArnold(id, v);
        conf.setTopStack(id);

        for (int i = 0; i < ctx.arnoldStmnt().size(); i++) {
            visitArnoldStmnt(ctx.arnoldStmnt(i));
        }

        return ComValue.INSTANCE;
    }

    @Override
    public Value visitAssignGlobal(ImpParser.AssignGlobalContext ctx) {
        String id = ctx.ID().getText();

        if (conf.globalExists(id)) {
            conf.updateGlobal(id, visitExp(ctx.exp()));
        } else {
            System.err.println("Global variable " + id + " used but never instantiated");
            System.err.println("@" + ctx.start.getLine() + ":" + ctx.start.getCharPositionInLine());
            System.exit(1);
        }

        return ComValue.INSTANCE;
    }

    @Override
    public Value visitInitGlobal(ImpParser.InitGlobalContext ctx) {
        String id = ctx.ID().getText();
        ExpValue<?> v = visitExp(ctx.exp());

        conf.updateGlobal(id, v);

        return ComValue.INSTANCE;
    }

    @Override
    public ComValue visitSkip(ImpParser.SkipContext ctx) {
        return ComValue.INSTANCE;
    }

    @Override
    public ComValue visitSeq(ImpParser.SeqContext ctx) {
        visitCom(ctx.com(0));
        return visitCom(ctx.com(1));
    }

    @Override
    public ComValue visitWhile(ImpParser.WhileContext ctx) {
        if (!visitBoolExp(ctx.exp()))
            return ComValue.INSTANCE;

        visitCom(ctx.com());

        return visitWhile(ctx);
    }

    @Override
    public Value visitAwhile(ImpParser.AwhileContext ctx) {
        if (visitArnoldFloatExp(ctx.arnoldExp()) == 0f)
            return ComValue.INSTANCE;

        for (int i = 0; i < ctx.arnoldCom().size(); i++) {
            visitArnoldCom(ctx.arnoldCom(i));
        }

        return visitAwhile(ctx);
    }

    @Override
    public ComValue visitOut(ImpParser.OutContext ctx) {
        System.out.println(visitExp(ctx.exp()));
        return ComValue.INSTANCE;
    }

    @Override
    public Value visitPrint(ImpParser.PrintContext ctx) {
        System.out.println(visitArnoldExp(ctx.arnoldExp()));
        return ComValue.INSTANCE;
    }

    @Override
    public Value visitFundecl(ImpParser.FundeclContext ctx) {
        FunValue v;

        for (int i = 0; i < ctx.ID().size(); i++) {
            String funName = ctx.ID(i).getText();
            if (!(conf.containsFun(funName))) {
                v = new FunValue(funName, ctx.exp(i));

                // set body if exists
                if ((ctx.com().size() - 1) > i) {
                    v.setBody(ctx.com(i));
                }

                // set arguments if exists
                if (ctx.arguments().size() > i) {
                    String strArgs = ctx.arguments(i).getText();
                    String formalParam;
                    List<String> arguments = Arrays.asList(strArgs.split("\\s*,\\s*"));
                    for (int index = 0; index < arguments.size(); index++) {
                        formalParam = arguments.get(index);
                        if (v.paramExists(formalParam)) {
                            System.err.println("Parameter '" + formalParam +
                                    "' clashes with previous parameters in " + funName);
                            System.err.println("@" + ctx.start.getLine() + ":" + ctx.start.getCharPositionInLine());

                            System.exit(1);
                        } else {
                            v.initParam(formalParam, index);
                        }
                    }
                }
                conf.insertFun(funName, v);

            } else {
                System.err.println("Function " + funName + " is already defined");
                System.err.println("@" + ctx.start.getLine() + ":" + ctx.start.getCharPositionInLine());

                System.exit(1);
            }
        }

        // declare global vars
        if (!(ctx.ig().isEmpty())) {
            for (int i = 0; i < ctx.ig().size(); i++) {
                visitInitGlobal((ImpParser.InitGlobalContext) ctx.ig(i));
            }
        }

        visitCom(ctx.com(ctx.com().size() - 1));

        return null;
    }

    @Override
    public NatValue visitNat(ImpParser.NatContext ctx) {
        return new NatValue(Integer.parseInt(ctx.NAT().getText()));
    }

    @Override
    public Value visitFloat(ImpParser.FloatContext ctx) {
        return new FloatValue(Float.parseFloat(ctx.FLOAT().getText()));
    }

    @Override
    public BoolValue visitBool(ImpParser.BoolContext ctx) {
        return new BoolValue(Boolean.parseBoolean(ctx.BOOL().getText()));
    }

    @Override
    public ExpValue<?> visitParExp(ImpParser.ParExpContext ctx) {
        return visitExp(ctx.exp());
    }

    @Override
    public NatValue visitPow(ImpParser.PowContext ctx) {
        int base = visitNatExp(ctx.exp(0));
        int exp = visitNatExp(ctx.exp(1));

        return new NatValue((int) Math.pow(base, exp));
    }

    @Override
    public BoolValue visitNot(ImpParser.NotContext ctx) {
        return new BoolValue(!visitBoolExp(ctx.exp()));
    }

    @Override
    public NatValue visitDivMulMod(ImpParser.DivMulModContext ctx) {
        int left = visitNatExp(ctx.exp(0));
        int right = visitNatExp(ctx.exp(1));

        return switch (ctx.op.getType()) {
            case ImpParser.DIV -> new NatValue(left / right);
            case ImpParser.MUL -> new NatValue(left * right);
            case ImpParser.MOD -> new NatValue(left % right);
            default -> null;
        };
    }

    @Override
    public NatValue visitPlusMinus(ImpParser.PlusMinusContext ctx) {
        int left = visitNatExp(ctx.exp(0));
        int right = visitNatExp(ctx.exp(1));

        return switch (ctx.op.getType()) {
            case ImpParser.PLUS -> new NatValue(left + right);
            case ImpParser.MINUS -> new NatValue(Math.max(left - right, 0));
            default -> null;
        };
    }

    @Override
    public Value visitAplus(ImpParser.AplusContext ctx) {
        float left = (float) conf.getArnoldVar(conf.getTopStack()).toJavaValue();
        float right = visitArnoldFloatExp(ctx.arnoldExp());

        float result = left + right;
        conf.updateArnold(conf.getTopStack(), new FloatValue(result));

        return ComValue.INSTANCE;
    }

    @Override
    public Value visitAminus(ImpParser.AminusContext ctx) {
        float left = (float) conf.getArnoldVar(conf.getTopStack()).toJavaValue();
        float right = visitArnoldFloatExp(ctx.arnoldExp());

        float result = left - right;
        conf.updateArnold(conf.getTopStack(), new FloatValue(result));

        return ComValue.INSTANCE;
    }

    @Override
    public Value visitAmul(ImpParser.AmulContext ctx) {
        float left = (float) conf.getArnoldVar(conf.getTopStack()).toJavaValue();
        float right = visitArnoldFloatExp(ctx.arnoldExp());

        float result = left * right;
        conf.updateArnold(conf.getTopStack(), new FloatValue(result));

        return ComValue.INSTANCE;
    }

    @Override
    public Value visitAdiv(ImpParser.AdivContext ctx) {
        float left = (float) conf.getArnoldVar(conf.getTopStack()).toJavaValue();
        float right = visitArnoldFloatExp(ctx.arnoldExp());

        float result = left / right;
        conf.updateArnold(conf.getTopStack(), new FloatValue(result));

        return ComValue.INSTANCE;
    }

    @Override
    public BoolValue visitEqExp(ImpParser.EqExpContext ctx) {
        ExpValue<?> left = visitExp(ctx.exp(0));
        ExpValue<?> right = visitExp(ctx.exp(1));

        return switch (ctx.op.getType()) {
            case ImpParser.EQQ -> new BoolValue(left.equals(right));
            case ImpParser.NEQ -> new BoolValue(!left.equals(right));
            default -> null; // unreachable code
        };
    }

    @Override
    public Value visitNd(ImpParser.NdContext ctx) {
        Random rng = new Random();

        switch (rng.nextInt(2)) {
            case 0 -> visitCom(ctx.com(0));
            case 1 -> visitCom(ctx.com(1));
        }

        return ComValue.INSTANCE;
    }

    @Override
    public ExpValue<?> visitId(ImpParser.IdContext ctx) {
        String id = ctx.ID().getText();

        if (conf.getInFun()) {
            FunValue fun = conf.getActiveFun();
            ExpValue<?> v = null;

            if (fun.containsFunVar(id)) {
                v = fun.getFunVar(id);
            } else if (fun.paramExists(id)) {
                v = fun.getParam(id);
            } else if (conf.globalExists(id)) {
                v = conf.getGlobal(id);
            } else {
                System.err.println("Variable " + id + " used but never instantiated");
                System.err.println("@" + ctx.start.getLine() + ":" + ctx.start.getCharPositionInLine());

                System.exit(1);
            }

            return v;
        } else {
            if (!conf.contains(id)) {
                if (conf.globalExists(id)) {
                    return conf.getGlobal(id);
                }
                System.err.println("Variable " + id + " used but never instantiated");
                System.err.println("@" + ctx.start.getLine() + ":" + ctx.start.getCharPositionInLine());

                System.exit(1);
            }

            return conf.get(id);
        }
    }

    @Override
    public Value visitAid(ImpParser.AidContext ctx) {
        String id = ctx.ID().getText();

        if (!(conf.arnoldVarExists(id))) {
            System.err.println("Variable " + id + " used but never instantiated");
            System.err.println("@" + ctx.start.getLine() + ":" + ctx.start.getCharPositionInLine());

            System.exit(1);
        }

        return conf.getArnoldVar(id);
    }

    @Override
    public Value visitMacro1(ImpParser.Macro1Context ctx) {
        return new FloatValue(0f);
    }

    @Override
    public Value visitMacro2(ImpParser.Macro2Context ctx) {
        return new FloatValue(1f);
    }

    @Override
    public Value visitFuncall(ImpParser.FuncallContext ctx) {
        String funName = ctx.ID().getText();

        if (conf.containsFun(funName)) {
            FunValue function;
            if (conf.getActiveFun() != null && conf.getActiveFun().getFunName().equals(funName)) {
                function = new FunValue(conf.getFun(funName));
            } else {
                function = conf.getFun(funName);
            }

            ImpParser.ComContext funBody;
            ImpParser.ExpContext ret = (ImpParser.ExpContext) function.getRet();

            if (ctx.arguments() != null) {
                ExpValue<?> actualParam;
                if (ctx.arguments().exp().size() == function.numParams()) {
                    for (int index = 0; index < ctx.arguments().exp().size(); index++) {
                        actualParam = visitExp(ctx.arguments().exp(index));
                        function.insertParam(index, actualParam);
                    }
                } else {
                    System.err.println("Function '" + funName + "' called with wrong numbers of arguments.");
                    System.err.println("expected " + function.numParams() + " given " + ctx.arguments().exp().size());
                    System.err.println("@" + ctx.start.getLine() + ":" + ctx.start.getCharPositionInLine());

                    System.exit(1);
                }
            } else if (ctx.arguments() == null && function.numParams() > 0) {
                System.err.println("Function '" + funName + "' called with wrong numbers of arguments.");
                System.err.println("expected " + function.numParams() + " given 0");
                System.err.println("@" + ctx.start.getLine() + ":" + ctx.start.getCharPositionInLine());

                System.exit(1);
            }
            conf.setActivateFun(function);
            if (function.hasBody()) {
                funBody = (ImpParser.ComContext) function.getFunBody();
                visitCom(funBody);
            }
            ExpValue<?> v = visitExp(ret);
            conf.deactivateFun();
            return v;

        } else {
            System.err.println("Function '" + funName + "' used but never declared");
            System.err.println("@" + ctx.start.getLine() + ":" + ctx.start.getCharPositionInLine());

            System.exit(1);
        }

        return null; // unreachable code
    }

    @Override
    public Value visitGlobal(ImpParser.GlobalContext ctx) {
        String id = ctx.ID().getText();

        if (!conf.globalExists(id)) {
            System.err.println("Global variable " + id + " used but never instantiated");
            System.err.println("@" + ctx.start.getLine() + ":" + ctx.start.getCharPositionInLine());

            System.exit(1);
        }

        return conf.getGlobal(id);
    }

    @Override
    public BoolValue visitCmpExp(ImpParser.CmpExpContext ctx) {
        int left = visitNatExp(ctx.exp(0));
        int right = visitNatExp(ctx.exp(1));

        return switch (ctx.op.getType()) {
            case ImpParser.GEQ -> new BoolValue(left >= right);
            case ImpParser.LEQ -> new BoolValue(left <= right);
            case ImpParser.LT  -> new BoolValue(left < right);
            case ImpParser.GT  -> new BoolValue(left > right);
            default -> null;
        };
    }

    @Override
    public Value visitAeqq(ImpParser.AeqqContext ctx) {
        float result = (float) conf.getArnoldVar(conf.getTopStack()).toJavaValue();
        float right = visitArnoldFloatExp(ctx.arnoldExp());

        if (result == right) {
            conf.updateArnold(conf.getTopStack(), new FloatValue(1f));
        } else {
            conf.updateArnold(conf.getTopStack(), new FloatValue(0f));
        }


        return ComValue.INSTANCE;
    }

    @Override
    public Value visitAgt(ImpParser.AgtContext ctx) {
        float result = (float) conf.getArnoldVar(conf.getTopStack()).toJavaValue();
        float right = visitArnoldFloatExp(ctx.arnoldExp());

        if (result > right) {
            conf.updateArnold(conf.getTopStack(), new FloatValue(1f));
        } else {
            conf.updateArnold(conf.getTopStack(), new FloatValue(0f));
        }

        return ComValue.INSTANCE;
    }

    @Override
    public Value visitAor(ImpParser.AorContext ctx) {
        float result = (float) conf.getArnoldVar(conf.getTopStack()).toJavaValue();
        float right = visitArnoldFloatExp(ctx.arnoldExp());

        if (result > 0f || right > 0f) {
            conf.updateArnold(conf.getTopStack(), new FloatValue(1f));
        } else {
            conf.updateArnold(conf.getTopStack(), new FloatValue(0f));
        }

        return ComValue.INSTANCE;
    }

    @Override
    public Value visitAnd(ImpParser.AndContext ctx) {
        float result = (float) conf.getArnoldVar(conf.getTopStack()).toJavaValue();
        float right = visitArnoldFloatExp(ctx.arnoldExp());

        if (result > 0f && right > 0f) {
            conf.updateArnold(conf.getTopStack(), new FloatValue(1f));
        } else {
            conf.updateArnold(conf.getTopStack(), new FloatValue(0f));
        }

        return ComValue.INSTANCE;
    }

    @Override
    public BoolValue visitLogicExp(ImpParser.LogicExpContext ctx) {
        boolean left = visitBoolExp(ctx.exp(0));
        boolean right = visitBoolExp(ctx.exp(1));

        return switch (ctx.op.getType()) {
            case ImpParser.AND -> new BoolValue(left && right);
            case ImpParser.OR -> new BoolValue(left || right);
            default -> null;
        };
    }

}
