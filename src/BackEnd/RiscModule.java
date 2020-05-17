package BackEnd;

import BackEnd.Operands.ConstStrings;
import BackEnd.Operands.GlobalVar;

import java.util.ArrayList;
import java.util.List;

public class RiscModule {
    private List<RiscFunction> functionList = new ArrayList<>();
    private List<RiscFunction> externalFunction = new ArrayList<>();
    private ArrayList<GlobalVar> globalVars = new ArrayList<>();
    private ArrayList<ConstStrings> constStrings = new ArrayList<>();

    public RiscModule() { }

    public void addFunction(RiscFunction function) {
        functionList.add(function);
    }

    public void addExtern(RiscFunction function){
        externalFunction.add(function);
    }

    public void addGV(GlobalVar var){globalVars.add(var);}
    public void addString(ConstStrings str){constStrings.add(str);}




    public List<RiscFunction> getFunctionList() {
        return functionList;
    }

    public void setFunctionList(List<RiscFunction> functionList) {
        this.functionList = functionList;
    }

    public ArrayList<GlobalVar> getGlobalVars() {
        return globalVars;
    }

    public void setGlobalVars(ArrayList<GlobalVar> globalVars) {
        this.globalVars = globalVars;
    }

    public ArrayList<ConstStrings> getConstStrings() {
        return constStrings;
    }

    public void setConstStrings(ArrayList<ConstStrings> constStrings) {
        this.constStrings = constStrings;
    }
}
