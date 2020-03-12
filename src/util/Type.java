package util;

public class Type {
    public enum types {
        VOID, INT, BOOL, STRING, CLASS, ARRAY, NULL
    }
    protected types type;

    public Type(){
        type = types.NULL;
    }

    public Type(String str) {
        switch (str) {
            case "void":
                type = types.VOID;
                break;
            case "int":
                type = types.INT;
                break;
            case "bool":
                type = types.BOOL;
                break;
            case "string":
                type = types.STRING;
                break;
            case "class":
                type = types.CLASS;
                break;
            case "array":
                type = types.ARRAY;
                break;
            case "null":
                type = types.NULL;
            default:
                type = types.NULL;
                break;
        }
    }

    public void setType(types type) {
        this.type = type;
    }
    public types getType() {
        return type;
    }

    public String getTypeStr() {
        switch (type) {
            case VOID:
                return "void";
            case INT:
                return "int";
            case BOOL:
                return "bool";
            case NULL:
                return "null";
            case ARRAY:
                return "array";
            case CLASS:
                return "class";
            case STRING:
                return "string";
            default:
                return "undefined";
        }
    }

    public boolean equal(Type other) {
        return type == other.getType();
    }

    public boolean isString() {
        return type == types.STRING;
    }

    public boolean isInt() {
        return type == types.INT;
    }
    public boolean isBool() {
        return type == types.BOOL;
    }

    public boolean isVoid() {
        return type == types.VOID;
    }

    public int getMemSize() {
        return Defines.REG_SIZE;
    }
}
