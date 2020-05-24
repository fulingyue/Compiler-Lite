package BackEnd.Operands;


import BackEnd.Instruction.Move;
import BackEnd.Instruction.RiscInstruction;

import java.util.HashSet;
import java.util.LinkedHashSet;

public class RiscRegister extends RiscOperand {
    public String name;
    private HashSet<RiscInstruction> use, def;

    public void addUse(RiscInstruction instruction){
        use.add(instruction);
    }

    public void addDef(RiscInstruction instruction){
        def.add(instruction);
    }
    public RiscRegister(String name) {
        this.name = name;
        use  = new HashSet<>();
        def = new HashSet<>();
        spilledCost = 0;
    }

    public HashSet<RiscInstruction> getUse() {
        return use;
    }

    public void setUse(HashSet<RiscInstruction> use) {
        this.use = use;
    }

    public HashSet<RiscInstruction> getDef() {
        return def;
    }

    public void setDef(HashSet<RiscInstruction> def) {
        this.def = def;
    }


    @Override
    public String toString(){
        if(color == null)
            return name;
        else  return color.getName();
    }
    @Override
    public String print() {
        assert color != null;
//        return name;
        return color.getName();
    }

    private LinkedHashSet<Move> moveList = new LinkedHashSet<>();
    private LinkedHashSet<RiscRegister> adjList = new LinkedHashSet<>();
    private int degree;
    private RiscRegister alias = this;
    private  boolean colorFixed = false;
    private PhysicalReg color =  null;
    private double spilledCost;


    public void clearColor(){
        moveList = new LinkedHashSet<>();
        adjList = new LinkedHashSet<>();
        degree = 0;
        alias = this;
        color= null;
    }
    public double getSpilledCost(){//TODO change spilled cost
//        return spilledCost/degree;
        return 10.0/degree;
     }

     public void  addMove(Move move){
        moveList.add(move);
     }

     public void addAdj(RiscRegister reg){
        adjList.add(reg);
     }

     public void addDegree(){
        degree++;
     }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LinkedHashSet<Move> getMoveList() {
        return moveList;
    }

    public void setMoveList(LinkedHashSet<Move> moveList) {
        this.moveList = moveList;
    }

    public LinkedHashSet<RiscRegister> getAdjList() {
        return adjList;
    }

    public void setAdjList(LinkedHashSet<RiscRegister> adjList) {
        this.adjList = adjList;
    }

    public int getDegree() {
        return degree;
    }

    public void setDegree(int degree) {
        this.degree = degree;
    }

    public RiscRegister getAlias() {
        return alias;
    }

    public void setAlias(RiscRegister alias) {
        this.alias = alias;
    }

    public boolean isColorFixed() {
        return colorFixed;
    }

    public void setColorFixed(boolean colorFixed) {
        this.colorFixed = colorFixed;
    }

    public PhysicalReg getColor() {
        return color;
    }

    public void setColor(PhysicalReg color) {
        this.color = color;
    }

    public void setSpilledCost(double spilledCost) {
        this.spilledCost = spilledCost;
    }
}
