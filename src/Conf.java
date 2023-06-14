import value.ExpValue;
import value.FunValue;

import java.util.*;

public class Conf {

    private final List<FunValue> activeFuns = new ArrayList<>();
    private boolean inFun = false;
    private final Map<String, ExpValue<?>> map = new HashMap<>();
    private final Map<String, ExpValue<?>> globalVars = new HashMap<>();
    private final Map<String, FunValue> funs = new HashMap<>();
    private final Map<String, ExpValue<?>> arnoldVars = new HashMap<>();
    private String topStack = null;


    public boolean contains(String id) { return map.containsKey(id); }

    public boolean containsFun(String fun) { return funs.containsKey(fun); }

    public void insertFun(String fun, FunValue v) { funs.put(fun, v); }

    public FunValue getFun(String fun) { return funs.get(fun); }

    public boolean globalExists(String id) { return globalVars.containsKey(id); }

    public void updateGlobal(String id, ExpValue<?> v) {
        globalVars.put(id, v);
    }

    public ExpValue<?> get(String id) {
        return map.get(id);
    }

    public ExpValue<?> getGlobal(String id) {
        return globalVars.get(id);
    }

    public void setActivateFun(FunValue fun) {
        this.activeFuns.add(0, fun);
        this.inFun = true;
    }

    public FunValue getActiveFun() {
        if (this.activeFuns.isEmpty()) {
            return null;
        }

        return activeFuns.get(0);
    }

    public void deactivateFun() {
        if (this.activeFuns.size() == 1) {
            this.activeFuns.get(0).clear();
            this.activeFuns.remove(0);
            this.inFun = false;
        } else {
            this.activeFuns.get(0).clear();
            this.activeFuns.remove(0);
        }

    }

    public boolean getInFun() { return this.inFun; }

    public void update(String id, ExpValue<?> v) {
        map.put(id, v);
    }

    public void updateArnold(String id, ExpValue<?> v) { arnoldVars.put(id, v); }

    public ExpValue<?> getArnoldVar(String id) { return arnoldVars.get(id); }

    public boolean arnoldVarExists(String id) { return arnoldVars.containsKey(id); }

    public void setTopStack(String id) { this.topStack = id; }

    public String getTopStack() { return this.topStack; }

}
