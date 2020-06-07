package Optimize;

import FrontEnd.IR.Module;

public class LoopAnalysis extends Pass {

    public LoopAnalysis(Module module) {
        super(module);
    }

    @Override
    public boolean run() {
        return false;
    }
}
