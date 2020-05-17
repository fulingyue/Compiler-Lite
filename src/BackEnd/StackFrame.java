package BackEnd;

import BackEnd.Operands.StackOffset;
import BackEnd.Operands.VirtualReg;

import java.util.ArrayList;
import java.util.HashMap;

public class StackFrame {
    private RiscFunction function;

    int size;
    private HashMap<VirtualReg, StackOffset> temporaryVar;
    private ArrayList<StackOffset> formalPara;
    private HashMap<RiscFunction,ArrayList<StackOffset>> paras;

    public StackFrame(RiscFunction function) {
        this.function = function;
        size = 0;
        temporaryVar = new HashMap<>();
        formalPara = new ArrayList<>();
        paras =  new HashMap<>();
    }

    public void addFormalPara(StackOffset stackOffset){
        formalPara.add(stackOffset);
    }

    public void compute(){
        int maxCallSize = 0;
        int spillSize = temporaryVar.size();
        for(ArrayList<StackOffset> call: paras.values()){
            maxCallSize = Integer.max(call.size(), maxCallSize);
        }

        size = maxCallSize + spillSize;

        for(int i = 0;i < formalPara.size(); i++){
            StackOffset paraLocation = formalPara.get(i);
            paraLocation.setOffset(size * 4 + i * 4);
        }

        int cnt = 0;
        for(StackOffset spillVars: temporaryVar.values()){
            spillVars.setOffset((cnt + maxCallSize) * 4);
            cnt ++ ;
        }
        for(ArrayList<StackOffset> funcCallParas: paras.values()){
            for(int i = 0; i< funcCallParas.size(); ++i){
                StackOffset para = funcCallParas.get(i);
                para.setOffset(i * 4);
            }
        }

    }


    public RiscFunction getFunction() {
        return function;
    }

    public void setFunction(RiscFunction function) {
        this.function = function;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public HashMap<VirtualReg, StackOffset> getTemporaryVar() {
        return temporaryVar;
    }

    public ArrayList<StackOffset> getFormalPara() {
        return formalPara;
    }


    public HashMap<RiscFunction, ArrayList<StackOffset>> getParas() {
        return paras;
    }

    public void setTemporaryVar(HashMap<VirtualReg, StackOffset> temporaryVar) {
        this.temporaryVar = temporaryVar;
    }

    public void setFormalPara(ArrayList<StackOffset> formalPara) {
        this.formalPara = formalPara;
    }

    public void setParas(HashMap<RiscFunction, ArrayList<StackOffset>> paras) {
        this.paras = paras;
    }
}
