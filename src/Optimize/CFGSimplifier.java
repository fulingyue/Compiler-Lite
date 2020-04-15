package Optimize;

import FrontEnd.IR.IRFunction;
import FrontEnd.IR.Module;

public class CFGSimplifier extends Pass{

    public CFGSimplifier(Module module) {
        super(module);
        changed = false;
    }

    @Override
    public boolean run() {
        for(IRFunction function: module.getFunctionMap().values()) {
            changed  |= FuncSimplifier(function);
        }
        return changed;
    }

    private boolean FuncSimplifier(IRFunction function) {
        boolean changed = removeUnreachableBlocks(function);
        changed  |= mergeEmptyReturnBlocks(function);


        return changed;
    }

    private boolean removeUnreachableBlocks(IRFunction function) {
        boolean changed = false;

        return changed;
    }

    private boolean mergeEmptyReturnBlocks(IRFunction function) {
        boolean changed = false;

        return changed;
    }

}
