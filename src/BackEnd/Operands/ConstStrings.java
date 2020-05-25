package BackEnd.Operands;

public class ConstStrings extends GlobalVar{
    private String str;

    public ConstStrings( String name, String str) {
        super(null, name);
        this.str = str;
    }



    public String getStr() {
        String build;


        build = str.replace("\\","\\\\");
        build= build.replace("\n","\\n");
        build= build.replace("\t","\\t");

        build = build.replace("\"", "\\\"");

        return build ;
    }


    public void setStr(String str) {
        this.str = str;
    }

    @Override
    public String print() {
        return "\t.asciz\t\"" + getStr() + "\"";
    }
}
