package Optimize;

import FrontEnd.IR.Module;

public class GlobalVarLocalization extends Pass {

    public GlobalVarLocalization(Module module) {
        super(module);
    }

    @Override
    public boolean run() {
        return false;
    }
}
