import value.ExpValue;
import value.FunValue;

import java.util.*;

public class Conf {

    private String activeFun;
    private boolean inFun = false;
    private final Map<String, ExpValue<?>> map = new HashMap<>();
    private final Map<String, ExpValue<?>> globalVars = new HashMap<>();
    private final Map<String, FunValue> funs = new HashMap<>();
    private final Map<String, ExpValue<?>> funVars = new HashMap<>();

    public boolean contains(String id) { return map.containsKey(id); }

    public boolean containsFun(String fun) { return funs.containsKey(fun); }

    public void insertFun(String fun, FunValue v) { funs.put(fun, v); }

    public FunValue getFun(String fun) { return funs.get(fun); }

    public boolean globalExists(String id) { return globalVars.containsKey(id); }

//    // da rifare
//    public boolean funExists(String fun) { return funParams.containsKey(fun); }

    public ExpValue<?> get(String id) {
        return map.get(id);
    }

    public ExpValue<?> getGlobal(String id) {
        return globalVars.get(id);
    }

    public void insertFunVar(String id, ExpValue<?> v) { funVars.put(id, v); }

    public ExpValue<?> getFunVar(String id) { return funVars.get(id); }

    public boolean containsFunVar(String id) { return funVars.containsKey(id); }

    public void clearFunVars() { funVars.clear(); }

    public void activateFun(String funName) {
        this.activeFun = funName;
        this.inFun = true;
    }

    public String getActiveFun() { return activeFun; }

    public void deactivateFun() { this.inFun = false; }

    public boolean getInFun() { return this.inFun; }

    public void update(String id, ExpValue<?> v) {
        map.put(id, v);
    }

    public void updateGlobal(String id, ExpValue<?> v) {
        globalVars.put(id, v);
    }

}
