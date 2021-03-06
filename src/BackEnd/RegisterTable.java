package BackEnd;

import BackEnd.Operands.PhysicalReg;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Set;

public class RegisterTable {
    public static PhysicalReg zero = new PhysicalReg("zero");
    public static PhysicalReg ra = new PhysicalReg("ra");
    public static PhysicalReg sp = new PhysicalReg("sp");
    public static PhysicalReg gp = new PhysicalReg("gp");
    public static PhysicalReg tp = new PhysicalReg("tp");
    public static PhysicalReg t0 = new PhysicalReg("t0");
    public static PhysicalReg t1 = new PhysicalReg("t1");
    public static PhysicalReg t2 = new PhysicalReg("t2");
    public static PhysicalReg s0 = new PhysicalReg("s0");
    public static PhysicalReg s1 = new PhysicalReg("s1");
    public static PhysicalReg a0 = new PhysicalReg("a0");
    public static PhysicalReg a1 = new PhysicalReg("a1");
    public static PhysicalReg a2 = new PhysicalReg("a2");
    public static PhysicalReg a3 = new PhysicalReg("a3");
    public static PhysicalReg a4 = new PhysicalReg("a4");
    public static PhysicalReg a5 = new PhysicalReg("a5");
    public static PhysicalReg a6 = new PhysicalReg("a6");
    public static PhysicalReg a7 = new PhysicalReg("a7");
    public static PhysicalReg s2 = new PhysicalReg("s2");
    public static PhysicalReg s3 = new PhysicalReg("s3");
    public static PhysicalReg s4 = new PhysicalReg("s4");
    public static PhysicalReg s5 = new PhysicalReg("s5");
    public static PhysicalReg s6 = new PhysicalReg("s6");
    public static PhysicalReg s7 = new PhysicalReg("s7");
    public static PhysicalReg s8 = new PhysicalReg("s8");
    public static PhysicalReg s9 = new PhysicalReg("s9");
    public static PhysicalReg s10 = new PhysicalReg("s10");
    public static PhysicalReg s11 = new PhysicalReg("s11");
    public static PhysicalReg t3 = new PhysicalReg("t3");
    public static PhysicalReg t4 = new PhysicalReg("t4");
    public static PhysicalReg t5 = new PhysicalReg("t5");
    public static PhysicalReg t6 = new PhysicalReg("t6");
    public static PhysicalReg hi = new PhysicalReg("%hi");
    public static PhysicalReg lo = new PhysicalReg("%lo");


//    public static String[] names = {"zero", "ra", "sp", "gp", "tp", "t0", "t1", "t2", "s0", "s1", "a0", "a1", "a2", "a3", "a4", "a5", "a6", "a7", "s2", "s3", "s4", "s5", "s6", "s7", "s8", "s9", "s10", "s11", "t3", "t4", "t5", "t6" };
//    public static String[] calleeSavedNames = {"s0", "s1", "s2", "s3", "s4", "s5", "s6", "s7", "s8", "s9", "s10", "s11"};
//    public static String[] callerSavedNames = {"ra", "t0", "t1", "t2", "a0", "a1", "a2", "a3", "a4", "a5", "a6", "a7", "t3", "t4", "t5", "t6"};
//    public static String[] argumentNames = {"a0", "a1", "a2", "a3", "a4", "a5", "a6", "a7"};
//    public static String[] allocableNames = {"ra", "t0", "t1", "t2", "s0", "s1", "a0", "a1", "a2", "a3", "a4", "a5", "a6", "a7", "s2", "s3", "s4", "s5", "s6", "s7", "s8", "s9", "s10", "s11", "t3", "t4", "t5", "t6" };

    public static PhysicalReg[] registers = {zero, ra, sp, gp, tp, t0, t1, t2, s0, s1, a0, a1, a2, a3, a4, a5, a6, a7, s2, s3, s4, s5, s6, s7, s8, s9, s10, s11, t3, t4, t5, t6};
    public static PhysicalReg[] argumentRegisters = {a0, a1, a2, a3, a4, a5, a6, a7};
    public static PhysicalReg[] calleeSavedRegisters = {s0, s1, s2, s3, s4, s5, s6, s7, s8, s9, s10, s11}; //12
    public static PhysicalReg[] callerSavedRegisters = {ra, t0, t1, t2, a0, a1, a2, a3, a4, a5, a6, a7, t3, t4, t5, t6}; //16
    public static PhysicalReg[] allocableRegisters = {ra, t0, t1, t2, s0, s1, a0, a1, a2, a3, a4, a5, a6, a7, s2, s3, s4, s5, s6, s7, s8, s9, s10, s11, t3, t4, t5, t6};
    public static int allocableSize = 28;

    public static Set<PhysicalReg> allocSet = new LinkedHashSet<>();
    public static Set<PhysicalReg> calleeSavedSet = new LinkedHashSet<>();

    public static HashMap<String, PhysicalReg> map;

    public RegisterTable() {
        map = new HashMap<>();
        map.put("zero", zero);
        map.put("ra", ra);
        map.put("sp", sp);
        map.put("gp", gp);
        map.put("tp", tp);
        map.put("t0", t0);
        map.put("t1", t1);
        map.put("t2", t2);
        map.put("s0", s0);
        map.put("s1", s1);
        map.put("a0", a0);
        map.put("a1", a1);
        map.put("a2", a2);
        map.put("a3", a3);
        map.put("a4", a4);
        map.put("a5", a5);
        map.put("a6", a6);
        map.put("a7", a7);
        map.put("s2", s2);
        map.put("s3", s3);
        map.put("s4", s4);
        map.put("s5", s5);
        map.put("s6", s6);
        map.put("s7", s7);
        map.put("s8", s8);
        map.put("s9", s9);
        map.put("s10", s10);
        map.put("s11", s11);
        map.put("t3", t3);
        map.put("t4", t4);
        map.put("t5", t5);
        map.put("t6", t6);

        allocSet.add(ra);
        allocSet.add(t0);
        allocSet.add(t1);
        allocSet.add(t2);
        allocSet.add(s0);
        allocSet.add(s1);
        allocSet.add(a0);
        allocSet.add(a1);
        allocSet.add(a2);
        allocSet.add(a3);
        allocSet.add(a4);
        allocSet.add(a5);
        allocSet.add(a6);
        allocSet.add(a7);
        allocSet.add(s2);
        allocSet.add(s3);
        allocSet.add(s4);
        allocSet.add(s5);
        allocSet.add(s6);
        allocSet.add(s7);
        allocSet.add(s8);
        allocSet.add(s9);
        allocSet.add(s10);
        allocSet.add(s11);
        allocSet.add(t3);
        allocSet.add(t4);
        allocSet.add(t5);
        allocSet.add(t6);

        calleeSavedSet.add(s0);
        calleeSavedSet.add(s1);
        calleeSavedSet.add(s2);
        calleeSavedSet.add(s3);
        calleeSavedSet.add(s4);
        calleeSavedSet.add(s5);
        calleeSavedSet.add(s6);
        calleeSavedSet.add(s7);
        calleeSavedSet.add(s8);
        calleeSavedSet.add(s9);
        calleeSavedSet.add(s10);
        calleeSavedSet.add(s11);


    }

    public PhysicalReg getRegister(String name) {
        return map.get(name);
    }
}
