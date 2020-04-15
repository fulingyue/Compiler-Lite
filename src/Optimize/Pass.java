package Optimize;

import FrontEnd.IR.Module;

public abstract class Pass {
    protected boolean changed;
    protected Module module;

    public Pass(Module module) {
        this.module = module;
    }

    abstract public boolean run();
}
