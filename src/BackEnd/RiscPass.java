package BackEnd;

public abstract class RiscPass {
    protected RiscModule module;

    public RiscPass(RiscModule module) {
        this.module = module;
    }

    public abstract void run();
}
