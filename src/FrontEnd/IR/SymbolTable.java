package FrontEnd.IR;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SymbolTable {
    private Map<String, ArrayList<IRNode>> symbolTable = new HashMap<>();


    public void put(String key, IRNode val) {
        ArrayList<IRNode> list = symbolTable.get(key);
        int idx;
        if(list== null) {
            list = new ArrayList<>();
            idx = 0;
            symbolTable.put(key,list);
        }
        else {
            idx = list.size();
        }

        val.setName(key + "." + idx);

        list.add(val);
    }


    public IRNode get(String name) {
        String withoutDot;
        if(name.contains(".")){
            withoutDot = name.split("\\.")[0];
            ArrayList<IRNode> list = symbolTable.get(withoutDot);
            if(list == null) return null;
            for(IRNode item: list) {
               if(item.getName().equals(name)){
                   return item;
               }
            }
            return list.get(0);

        }
        else{
            ArrayList<IRNode> list = symbolTable.get(name);
            if(list == null) {
                return null;
            }
            return list.get(0);
        }
    }


}
