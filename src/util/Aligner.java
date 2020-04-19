package util;

public class Aligner {
    static public int align(int size, int base) {
        if(base == 0) return 0;
        if(size % base == 0) return size;
        else return size + (base - size % base);
    }
}
