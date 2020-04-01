package FrontEnd.IR;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SymbolTable extends HashMap<String, ArrayList<Object>> {
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
            for(IRNode item: list) {
               if(item.getName().equals(name)){
                   return item;
               }
            }
            return null;
        }
        else{
            throw new RuntimeException();
        }
    }


}
