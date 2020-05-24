package BackEnd.Instruction;

import BackEnd.Operands.*;
import BackEnd.RiscBB;

public class ILoad extends RiscInstruction {

    public enum LoadType{
        lb,lw
    }
    private RiscRegister rd;
    private Address addr;
    private LoadType op;


    public ILoad(RiscBB parentBB, RiscRegister rd, Address addr, LoadType op) {
        super(parentBB);
        this.rd = rd;
        this.addr = addr;
        this.op = op;
    }

    @Override
    public void add() {
        addDef(rd);
        rd.addDef(this);
        if(addr.getUse()!=null){
            addUse(addr.getUse());
            (addr.getUse()).addUse(this);
        }
    }


    @Override
    public String print() {
        return "\t" + op.name() + "\t" + rd.print() + ", " + addr.print();
    }

    @Override
    public void replaceUse(RiscRegister old, RiscRegister newUse) {
        if(addr.getUse()!= null && old == addr.getUse()){
            assert addr instanceof AddrWithOffset;
            old.getUse().remove(this);
            getUsages().remove(old);
            ((AddrWithOffset) addr).setBase(newUse);
            newUse.addUse(this);
            addUse(newUse);
        }
    }

    @Override
    public void replaceDef(RiscRegister old, RiscRegister newDef) {
        if(old == rd){
            old.getDef().remove(this);
            getDef().remove(old);
            rd = newDef;
            newDef.addDef(this);
            addDef(newDef);
        }
    }


    public RiscRegister getRd() {
        return rd;
    }

    public void setRd(RiscRegister rd) {
        this.rd = rd;
    }

    public Address getAddr() {
        return addr;
    }

    public void setAddr(Address addr) {
        this.addr = addr;
    }

    public LoadType getOp() {
        return op;
    }

    public void setOp(LoadType op) {
        this.op = op;
    }
}
