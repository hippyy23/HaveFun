package value;

import org.antlr.v4.runtime.RuleContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FunValue extends Value {
    private final String funName;
    private RuleContext funBody = null;
    private final RuleContext ret;
    private Map<String, Integer> funParams = new HashMap<>();
    private final List<ExpValue<?>> funValues = new ArrayList<>();
    private final Map<String, ExpValue<?>> funVars = new HashMap<>();


    public FunValue(String funName, RuleContext ret) {
        this.funName = funName;
        this.ret = ret;
    }

    public FunValue(FunValue that) {
        this.funName = that.getFunName();
        this.funParams = that.getParams();
        this.ret = that.getRet();
        this.funBody = that.getFunBody();
    }

    public String getFunName() {
        return funName;
    }

    public void setBody(RuleContext funBody) {
        this.funBody = funBody;
    }

    public RuleContext getFunBody() { return funBody; }

    public boolean hasBody() { return funBody != null; }

    public RuleContext getRet() {
        return ret;
    }

    public ExpValue<?> getParam(String id) { return funValues.get(getIndex(id)); }

    public void insertParam(int index, ExpValue<?> v) { funValues.add(index, v); }

    public boolean paramExists(String id) { return funParams.containsKey(id); }

    public void initParam(String id, int index) { funParams.put(id, index); }

    public int numParams() { return funParams.size(); }

    public Map<String, Integer> getParams() { return funParams; }

    private int getIndex(String id) { return funParams.get(id); }

    public void insertFunVar(String id, ExpValue<?> v) { funVars.put(id, v); }

    public ExpValue<?> getFunVar(String id) { return funVars.get(id); }

    public boolean containsFunVar(String id) { return funVars.containsKey(id); }

    public void clear() {
        this.funValues.clear();
        this.funVars.clear();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof FunValue;
    }

    @Override
    public int hashCode() {
        return 0;
    }
}
