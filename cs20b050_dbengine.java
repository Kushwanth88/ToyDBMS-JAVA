import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.lang.Math;
import java.util.Random;
import java.util.*;
import java.util.stream.*;
import java.io.BufferedWriter;
import java.io.FileWriter;

public class cs20b050_dbengine {
    public static void main(String[] args)
            throws IOException {
        List<String> words = new ArrayList<String>();
        BufferedReader reader = new BufferedReader(new FileReader("cs20b050_table.query"));
        String line;
        while ((line = reader.readLine()) != null) {
            words.add(line);
        }
        String[] array = words.toArray(new String[0]);
        reader.close();
        ICG icg1 = new ICG();
        String[] allTableNames = icg1.tableNames(array);
        int[] tableposn = icg1.tablePositions(array);
        String[] icg = icg1.intermediateCode(array);
        // for(int i=0;i<tableposn.length;i++){
        // System.out.println(tableposn[i]);
        // }
        int a[] = icg1.attributesCount(array);
        BufferedWriter writer2 = new BufferedWriter(new FileWriter("intermediateCode.txt", false));
        for (int i = 0; i < icg.length; i++) {
            writer2.write(icg[i].toString());
            writer2.newLine();
        }
        writer2.flush();
        System.out.println("Call sequence is loaded into the file intermediateCode.txt successfully");
        for (int i = 0; i < icg.length; i++) {

        }
        // System.out.println(icg1.position(array,tableposn,15));
        String[] attrib = icg1.attributesNamesTypes(array);
        // for(int i=0;i<attrib.length;i++)
        // {
        // System.out.println(attrib[i]);
        // }
        tableValue tv = new tableValue();
        String[] s1 = tv.tableValues(array);
        String[] tv1 = tv.fillRandomValues(array, s1);
        // for(int i=0;i<tv1.length;i++){
        // System.out.println(tv1[i]);
        // }
        int count = 0;
        for (int i = 0; i < allTableNames.length; i++) {
            String s11 = allTableNames[i];
            String s10 = icg1.attrValuesForString(array, s11);
            String s33 = new String(".csv");
            BufferedWriter writer = new BufferedWriter(new FileWriter(s11 + s33, false));
            writer.write(s10.toString());
            writer.newLine();
            for (int j = 0; j < tv1.length; j++) {
                String[] s2 = tv1[j].split(" ");
                if (s2[0].equals(s11)) {
                    writer.write(s2[1].toString());
                    writer.newLine();
                }
                writer.flush();
            }
            System.out.println("Data of the table " + s11 + " is added into the file " + s11 + s33 + " successfully");
        }
        BufferedWriter writer1 = new BufferedWriter(new FileWriter("myFile.txt", false));
        for (int i = 0; i < tv1.length; i++) {
            writer1.write(tv1[i].toString());
            writer1.newLine();
        }
        writer1.flush();
        System.out.println("Data Entered in to the file successfully");

    }
}

class ICG {
    public static String[] tableNames(String s[]) {
        boolean flag = false;
        int j = 0;
        String s1 = new String("create");
        String s2 = new String("table");
        for (int i = 0; i < s.length; i++) {
            String[] sA = s[i].split(" ");
            if (sA[0].equals(s1) && sA[1].equals(s2)) {
                j++;
            }
        }
        String[] tableNames = new String[j];
        j = 0;
        for (int i = 0; i < s.length; i++) {
            String[] sA = s[i].split(" ");
            if (sA[0].equals(s1) && sA[1].equals(s2)) {
                tableNames[j] = sA[2];
                j++;
            }
        }
        return tableNames;
    }

    public static int[] attributesCount(String inputArray[]) {
        List<Integer> attr = new ArrayList<Integer>();
        for (int i = 0; i < inputArray.length; i++) {
            String[] str1 = inputArray[i].split(" ");
            if (str1[0].equals("create") && str1[1].equals("table")) {
                attr.add(Integer.valueOf(str1[3]));
            }
        }
        int[] array = attr.stream().mapToInt(Integer::intValue).toArray();
        return array;
    }

    public static int[] tablePositions(String s[]) {
        boolean flag = false;
        int j = 0;
        String s1 = new String("create");
        String s2 = new String("table");
        for (int i = 0; i < s.length; i++) {
            String[] sA = s[i].split(" ");
            if (sA[0].equals(s1) && sA[1].equals(s2)) {
                j++;
            }
        }
        int[] tablePositions = new int[j];
        j = 0;
        for (int i = 0; i < s.length; i++) {
            String[] sA = s[i].split(" ");
            if (sA[0].equals(s1) && sA[1].equals(s2)) {
                tablePositions[j] = i;
                j++;
            }
        }
        return tablePositions;
    }

    public static boolean isTable(String[] tableName, String s1) {
        for (int i = 0; i < tableName.length; i++) {
            if (s1.equals(tableName[i])) {
                return true;
            }
        }
        return false;
    }

    public static String[] intermediateCode(String[] inputArray) {
        int tIndex = 0;
        List<String> code = new ArrayList<String>();
        String s1 = new String("create");
        String s2 = new String("table");
        String s3 = new String("load_table(");
        String s4 = new String(")");
        String s5 = new String("insert");
        String s6 = new String("into");
        String s7 = new String("\"");
        String s8 = new String("add_attribute(t,");

        String[] allTableNames = tableNames(inputArray);
        for (int i = 0; i < inputArray.length; i++) {
            String[] icg = inputArray[i].split(" ");
            if (icg.length == 4) {
                if (icg[0].equals(s1) && icg[1].equals(s2)) {
                    String loadTable = "create_table(" + icg[2] + s4;
                    code.add(loadTable);
                } else if (icg[0].equals(s5) && icg[1].equals(s6) && isTable(allTableNames, icg[2])) {
                    String t1 = String.valueOf(tIndex);
                    String loadTable = "t" + t1 + " = " + s3 + icg[2] + s4;
                    String insertTable = "insert_into(t," + s7 + icg[3] + s7 + s4;
                    String saveTable = "save_table(t)";
                    code.add(loadTable);
                    code.add(insertTable);
                    code.add("Fill_with_random_data(t" + t1 + "," + 100 + s4);
                    code.add(saveTable);
                    tIndex++;
                }
            } else if (icg.length == 2) {
                String attributesAdd = s8 + icg[0] + "," + icg[1] + s4;
                code.add(attributesAdd);
            }
        }
        String[] array = code.toArray(new String[0]);
        return array;
    }

    public static String[] attributesNamesTypes(String[] inputArray) {
        int tIndex = 0;
        List<String> code = new ArrayList<String>();
        String[] allTableNames = tableNames(inputArray);
        int[] tablepo = tablePositions(inputArray);
        for (int i = 0; i < inputArray.length; i++) {
            String[] icg = inputArray[i].split(" ");
            if (icg.length == 2) {
                String s1 = position(inputArray, tablepo, i);
                code.add(s1 + " " + icg[0] + " " + icg[1]);
            }
        }
        String[] array = code.toArray(new String[0]);
        return array;
    }

    public static String position(String[] inputArray, int[] tablepo, int t) {
        String[] allTableNames = tableNames(inputArray);
        // System.out.println(t);
        for (int i = 0; i < tablepo.length - 1; i++) {
            // System.out.println(tablepo[i]);
            if ((t < tablepo[i + 1]) && (t > tablepo[i]))
                return allTableNames[i];
        }
        return allTableNames[allTableNames.length - 1];
    }

    public static String attrValuesForString(String[] inputArray, String s1) {
        String s10 = new String("");
        String[] s22 = attributesNamesTypes(inputArray);
        for (int i = 0; i < s22.length; i++) {
            String[] s111 = s22[i].split(" ");
            if (s111[0].equals(s1)) {
                for (int j = 2; j < s111.length; j++) {
                    s10 += s111[j] + "\t";
                }
            }
        }
        return s10;
    }

}

class tableValue {
    public static String[] tableValues(String[] inputArray) {
        int tIndex = 0;
        ICG icg1 = new ICG();
        List<String> code = new ArrayList<String>();
        String s5 = new String("insert");
        String s6 = new String("into");
        String[] allTableNames = icg1.tableNames(inputArray);
        for (int i = 0; i < inputArray.length; i++) {
            String[] icg = inputArray[i].split(" ");
            if (icg.length == 4) {
                if (icg[0].equals(s5) && icg[1].equals(s6) && icg1.isTable(allTableNames, icg[2])) {
                    String temp = icg[3].replace("(", "");
                    String value = temp.replace(")", "");
                    // String value1 = value.replace(","," ");
                    code.add(icg[2] + " " + value);
                }
            }

        }
        String[] array = code.toArray(new String[0]);
        return array;
    }

    public static String getAlphaNumericString(int n) {
        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ" + "0123456789" + "abcdefghijklmnopqrstuvxyz";
        StringBuilder sb = new StringBuilder(n);
        for (int i = 0; i < n; i++) {
            int index = (int) (AlphaNumericString.length() * Math.random());
            sb.append(AlphaNumericString.charAt(index));
        }
        return sb.toString();
    }

    public static String[] fillRandomValues(String[] inputArray, String[] code) {
        List<String> datalist = new ArrayList<String>();
        for (int i = 0; i < code.length; i++) {
            datalist.add(code[i]);
        }
        ICG icg1 = new ICG();
        String[] allTableNames = icg1.tableNames(inputArray);
        int[] tableposn = icg1.tablePositions(inputArray);
        int a[] = icg1.attributesCount(inputArray);
        String[] attributesInfo = icg1.attributesNamesTypes(inputArray);
        int[] attrcnt = icg1.attributesCount(inputArray);
        String s11 = new String("int");
        String s21 = new String("float");
        String s31 = new String("char");
        String s41 = new String("string");
        String s51 = new String("date");
        double a1 = 0;
        int data = 100;
        while (data >= 0) {
            data -= 1;
            for (int i = 0; i < allTableNames.length; i++) {
                String s0 = "";
                int p = attrcnt[i];
                int checker = 0;
                for (int j = 0; j < attributesInfo.length; j++) {
                    String[] s = attributesInfo[j].split(" ");
                    if (allTableNames[i].equals(s[0])) {
                        if (s[1].equals(s11)) {
                            Random random = new Random();
                            int x = random.nextInt(500);
                            String s1 = String.valueOf(x);
                            s0 += s1;
                            if (checker <= p)
                                s0 += ",";
                            checker++;
                        } else if (s[1].equals(s21)) {
                            Random random = new Random();
                            int x = random.nextInt(500);
                            String s1 = String.valueOf(x);
                            int y = random.nextInt(5);
                            String s2 = String.valueOf(y);
                            s0 += s1 + "." + s2;
                            checker++;
                            if (checker <= p)
                                s0 += ",";
                            checker++;
                        } else if (s[1].equals(s31)) {
                            String s1 = getAlphaNumericString(1);
                            s0 += s1;
                            if (checker <= p)
                                s0 += ",";
                            checker++;

                        } else if (s[1].equals(s41)) {
                            String s1 = getAlphaNumericString(5);
                            s0 += s1 + "\t";
                            if (checker <= p)
                                s0 += ",";
                            checker++;
                        } else if (s[1].equals(s51)) {
                            Random random = new Random();
                            int x = random.nextInt(30);
                            String s1 = String.valueOf(x);
                            int y = random.nextInt(12);
                            String s2 = String.valueOf(x);
                            int z = random.nextInt(30);
                            String s3 = String.valueOf(1990 + z);
                            s0 += s1 + "-" + s2 + "-" + s3 + "\t";

                            if (checker <= p)
                                s0 += ",";
                            checker++;
                        }
                    }
                }
                String temp = allTableNames[i] + " " + s0;
                // System.out.println(temp);
                datalist.add(temp);
            }

        }
        // for(String s : datalist)System.out.println(s);
        String[] array1 = datalist.toArray(new String[0]);
        // for(int i=0;i<array1.length;i++)
        // {
        // System.out.println(array1[i]);
        // }
        return array1;
    }
}
