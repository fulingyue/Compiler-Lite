package util;


public class print_tool {
    public static void printSpaceAndStr(int tab, String str) {
        for (int i = 0; i < tab; ++i) System.out.print("    ");
        System.out.println(str);
    }

    public static void printDashAndStr(int tab, String str) {
        for (int i = 0; i <= tab - 1; ++i) System.out.print("    ");
        System.out.print("----");
        System.out.println(str);
    }

    public static String whiteSpace(String str) {
        StringBuilder stringBuilder = new StringBuilder();
        char ch = str.charAt(0);
        if(ch != '\"') throw  new RuntimeException();
        for (int i = 1; i <  str.length()-1; i++){
            ch = str.charAt(i);
            if (ch == '\\') {
                switch (str.charAt(++i)) {
                    case '\\': stringBuilder.append('\\');break;
                    case  'n': stringBuilder.append('\n');break;
                    case '\"': stringBuilder.append('\"');break;
                    case 't' : stringBuilder.append('\t');break;
                }
            }
            else{
                stringBuilder.append(ch);
            }
        }
        ch = str.charAt(str.length() - 1);
        if(ch != '\"') throw new RuntimeException();
        return stringBuilder.toString();
    }

    public static String repeatString(String str, int n) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < n; i++) {
            sb.append(str);
        }
        return sb.toString();
    }

}
