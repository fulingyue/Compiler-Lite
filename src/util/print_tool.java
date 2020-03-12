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
        for (int i = 0; i <  str.length(); i++){
            char ch = str.charAt(i);
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
        return stringBuilder.toString();
    }
}
