package util;

public class Pair<Type1, Type2> {
    private Type1 key;
    private Type2 value;

    public Pair(Type1 first, Type2 second) {
        this.key = first;
        this.value = second;
    }

    public Type1 getKey() {
        return key;
    }

    public Type1 getFirst(){return key;}

    public void setKey(Type1 key) {
        this.key = key;
    }

    public Type2 getSecond(){return value;}

    public Type2 getValue() {
        return value;
    }

    public void setValue(Type2 value) {
        this.value = value;
    }
}
