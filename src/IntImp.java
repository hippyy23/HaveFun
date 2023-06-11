import value.*;

import java.util.Arrays;
import java.util.List;

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

    private int visitNatExp(ImpParser.ExpContext ctx) {
        try {
            System.out.println(ctx.getText());
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
    public ComValue visitIf(ImpParser.IfContext ctx) {
        return visitBoolExp(ctx.exp())
                ? visitCom(ctx.com(0))
                : visitCom(ctx.com(1));
    }

    @Override
    public ComValue visitAssign(ImpParser.AssignContext ctx) {
        String id;
        ExpValue<?> v;

        if (ctx.getParent() instanceof ImpParser.FundeclContext) {
            id = ctx.ID().getText();
            v = visitExp(ctx.exp());

            conf.insertFunVar(id, v);
        } else {
            id = ctx.ID().getText();
            v = visitExp(ctx.exp());

            conf.update(id, v);
        }

        return ComValue.INSTANCE;
    }

    @Override
    public Value visitAssignGlobal(ImpParser.AssignGlobalContext ctx) {
        String id = ctx.ID().getText();
        ExpValue<?> v;

        if (!conf.globalExists(id)) {
            conf.updateGlobal(id, visitExp(ctx.exp()));
        } else {
            System.err.println("Variable " + id + " used but never instantiated");
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
    public ComValue visitOut(ImpParser.OutContext ctx) {
        System.out.println(visitExp(ctx.exp()));
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
                        if (!(v.paramExists(formalParam))) {
                            v.initParam(arguments.get(index), index);
                        } else {
                            System.err.println("Parameter '" + formalParam +
                                    "' clashes with previous parameters in " + funName);
                            System.err.println("@" + ctx.start.getLine() + ":" + ctx.start.getCharPositionInLine());

                            System.exit(1);
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

        visitCom(ctx.com(ctx.com().size() - 1));

        return null;
    }

    @Override
    public NatValue visitNat(ImpParser.NatContext ctx) {
        return new NatValue(Integer.parseInt(ctx.NAT().getText()));
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
    public ExpValue<?> visitId(ImpParser.IdContext ctx) {
        String id = ctx.ID().getText();

        if (conf.getInFun()) {
            FunValue fun = conf.getFun(conf.getActiveFun());
            ExpValue<?> v = null;

            if (conf.containsFunVar(id)) {
                v = conf.getFunVar(id);
            } else if (fun.paramExists(id)) {
                v = fun.getParam(id);
            } else {
                System.err.println("Variable " + id + " used but never instantiated");
                System.err.println("@" + ctx.start.getLine() + ":" + ctx.start.getCharPositionInLine());

                System.exit(1);
            }

            return v;
        } else {
            if (!conf.contains(id)) {
                System.err.println("Variable " + id + " used but never instantiated");
                System.err.println("@" + ctx.start.getLine() + ":" + ctx.start.getCharPositionInLine());

                System.exit(1);
            }

            return conf.get(id);
        }
    }

    @Override
    public Value visitFuncall(ImpParser.FuncallContext ctx) {
        String funName = ctx.ID().getText();
        conf.clearFunVars();
        conf.activateFun(funName);

        // inserire i parametri passati

        if (conf.containsFun(funName)) {
            FunValue function = conf.getFun(funName);
            ImpParser.ComContext funBody;
            ImpParser.ExpContext ret = (ImpParser.ExpContext) function.getRet();

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
            System.err.println("Variable " + id + " used but never instantiated");
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
