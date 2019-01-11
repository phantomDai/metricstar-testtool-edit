package controller.util;

import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

import java.io.*;
import java.util.*;

public class ConfigurationEXP {
    public Map<String,String> numberofTestScripts(){
        String path = "C:\\Users\\daihe\\Desktop\\EXP\\MyRandom1";
        File file = new File(path);
        Map<String,String> map = new HashMap<>();
        if (!file.exists())
            System.out.println("文件不存在，请检查");
        String[] scriptnames = file.list();
        for (int i = 0; i < scriptnames.length; i++) {
            map.put(scriptnames[i],String.valueOf(i));
        }
        return map;
    }

    public Map<String,Set<String>> combineTestFrameAndScripts() {
        //获得测试帧的名字以及对应的编号
        String path = "C:\\Users\\daihe\\Desktop\\EXP\\MyRandom1";
        File file = new File(path);
        String[] scriptsname = file.list();
        //记录测试帧涉及的测试脚本编号
        Map<String, Set<String>> map = new HashMap<>();
        for (int i = 0; i < scriptsname.length; i++) {
            System.out.println(scriptsname[i]);
            String scriptsPath = path + "\\" + scriptsname[i] +
                    "\\" + "testExpenseReimbursementSystem.java";
            try {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(scriptsPath)));
                String temp = "";
                int counter = 0; //record lines
                String source1 = "";
                String source2 = "";
                String source3 = "";
                String source4 = "";
                String source5 = "";
                String follow1 = "";
                String follow2 = "";
                String follow3 = "";
                String follow4 = "";
                String follow5 = "";
                while ((temp = bufferedReader.readLine()) != null) {
                    counter++;
                    if (counter >= 17)
                        break;
                    if (counter == 5) {
                        String[] strarray = temp.split(": ");
                        String parttwo = strarray[1];
                        String[] strarray2 = parttwo.split("         ");
                        source1 = strarray2[0];
                    } else if (counter == 6) {
                        String[] strarray = temp.split(": ");
                        String parttwo = strarray[1];
                        if (parttwo.contains("NULL")){
                            String[] strarray2 = parttwo.split("        ");
                            source2 = strarray2[0];
                        }else {
                            String[] strarray2 = parttwo.split("         ");
                            source2 = strarray2[0];
                        }
                    } else if (counter == 7) {
                        String[] strarray = temp.split(": ");
                        String parttwo = strarray[1];
                        String[] strarray2 = parttwo.split("         ");
                        source3 = strarray2[0];
                    } else if (counter == 8) {
                        String[] strarray = temp.split(": ");
                        String parttwo = strarray[1];
                        String[] strarray2 = parttwo.split("         ");
                        source4 = strarray2[0];
                    } else if (counter == 9) {
                        String[] strarray = temp.split(": ");
                        String parttwo = strarray[1];
                        String[] strarray2 = parttwo.split("        ");
                        source5 = strarray2[0];
                    } else if (counter == 12) {
                        String[] strarray = temp.split(": ");
                        String parttwo = strarray[1];
                        String[] strarray2 = parttwo.split("         ");
                        follow1 = strarray2[0];
                    } else if (counter == 13) {
                        String[] strarray = temp.split(": ");
                        String parttwo = strarray[1];
                        if (parttwo.contains("NULL")){
                            String[] strarray2 = parttwo.split("        ");
                            follow2 = strarray2[0];
                        }else {
                            String[] strarray2 = parttwo.split("         ");
                            follow2 = strarray2[0];
                        }
                    } else if (counter == 14) {
                        String[] strarray = temp.split(": ");
                        String parttwo = strarray[1];
                        String[] strarray2 = parttwo.split("         ");
                        follow3 = strarray2[0];
                    } else if (counter == 15) {
                        String[] strarray = temp.split(": ");
                        String parttwo = strarray[1];
                        String[] strarray2 = parttwo.split("         ");
                        follow4 = strarray2[0];
                    } else if (counter == 16) {
                        String[] strarray = temp.split(": ");
                        String parttwo = strarray[1];
                        String[] strarray2 = parttwo.split("        ");
                        follow5 = strarray2[0];
                    }
                }
                //得到了一个测试脚本中的测试用例
                String sourcetestcase = source1 + source2 + source3 + source4 + source5;
                String followtestcase = follow1 + follow2 + follow3 + follow4 + follow5;
                //判断map中是否包含该测试用例如果包含则在对应的list中添加测试脚本编号
                //如果不包含则需要新建list
                if (!map.containsKey(sourcetestcase)) {
                    Set<String> tempsourceset = new HashSet<>();
                    tempsourceset.add(String.valueOf(i));
                    map.put(sourcetestcase, tempsourceset);
                } else {
                    Set<String> tempsourceset = map.get(sourcetestcase);
                    tempsourceset.add(String.valueOf(i));
                    map.put(sourcetestcase, tempsourceset);
                }
                if (!map.containsKey(followtestcase)) {

                    Set<String> tempfollowset = new HashSet<>();
                    tempfollowset.add(String.valueOf(i));
                    map.put(followtestcase, tempfollowset);
                } else {
                    Set<String> tempfollowset = map.get(followtestcase);
                    tempfollowset.add(String.valueOf(i));
                    map.put(followtestcase, tempfollowset);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return map;
    }

    public void writetestframeAndMr(){
        Map<String,Set<String>> map = combineTestFrameAndScripts();
        StringBuffer sb = new StringBuffer();
        StringBuffer sb1 = new StringBuffer();
        Set<String> set = new HashSet<>();
        set = map.keySet();
        Iterator<String> it = set.iterator();
        int counter = 0;
        while (it.hasNext()){
            String testframename = it.next();
            sb1.append(String.valueOf(counter) + ":");
            sb.append(testframename + ":");
            Set<String> tempnumber = map.get(testframename);
            String tempstr = "";
            Iterator<String> tempit = tempnumber.iterator();
            while(tempit.hasNext()){
                tempstr += tempit.next() + ",";
            }
            sb.append(tempstr + "\n");
            sb1.append(tempstr + "\n");
            counter++;
        }
        String pathtest = "C:\\Users\\daihe\\Desktop\\EXP\\testframeAndmr\\test";
        String testframeAndMr = "C:\\Users\\daihe\\Desktop\\EXP\\testframeAndmr\\testframeAndNumber";
        File file1 = new File(pathtest);
        File file2 = new File(testframeAndMr);
        try {
            if (!file1.exists())
                file1.createNewFile();
            if (!file2.exists())
                file2.createNewFile();
            PrintWriter pw1 = new PrintWriter(new FileWriter(file1));
            PrintWriter pw2 = new PrintWriter(new FileWriter(file2));
            pw1.write(sb1.toString());
            pw2.write(sb.toString());
            pw1.close();
            pw2.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }


    /**
     * 将文件的名字用用标号代替
     */
    public void transformScriptstoNumber(){
        String path = "C:\\Users\\daihe\\Desktop\\EXP\\MyRandom1"; //文件的父目录
        String[] filenamelist; //存放目录的名字

        File parentfile = new File(path);

        if (parentfile.exists()){
            filenamelist = new String[parentfile.list().length];
            filenamelist = parentfile.list();
            for (int i = 0; i < filenamelist.length; i++) {
                String oldpath = path + "\\" + filenamelist[i] ;
                String newpath = path + "\\" + String.valueOf(i);
                File oldfile = new File(oldpath);
                File newfile = new File(newpath);
                oldfile.renameTo(newfile);
            }
        }
    }

    /**
     * 划分测试帧到各个分区内
     */
    public void creatTestFrameToPartition(){
        String[] s0 = {"5","13","22","28","45"};
        String[] s1 = {"0","18","38","41","50"};
        String[] s2 = {"6","11","12","15","26"};
        String[] s3 = {"25","29","33","47","48","49","51","52","53"};
        String[] s4 = {"1","24","32","39","42","46"};
        String[] s5 = {"10","20","30","43"};
        String[] s6 = {"2","16","31","36"};
        String[] s7 = {"4","7","8","9","19","23"};
        String[] s8 = {"3","44"};
        String[] s9 = {"27","40"};
        String[] s10 = {"21","34"};
        String[] s11 = {"14","17","35","37"};

        String path = "C:\\Users\\daihe\\Desktop\\EXP\\partitions";
        for (int i = 0; i < s0.length; i++) {
            String str0 = path + "\\p0\\" + s0[i];
            File file = new File(str0);
            file.mkdir();
        }
        for (int i = 0; i < s1.length; i++) {
            String str0 = path + "\\p1\\" + s1[i];
            File file = new File(str0);
            file.mkdir();
        }

        for (int i = 0; i < s2.length; i++) {
            String str0 = path + "\\p2\\" + s2[i];
            File file = new File(str0);
            file.mkdir();
        }
        for (int i = 0; i < s3.length; i++) {
            String str0 = path + "\\p3\\" + s3[i];
            File file = new File(str0);
            file.mkdir();
        }
        for (int i = 0; i < s4.length; i++) {
            String str0 = path + "\\p4\\" + s4[i];
            File file = new File(str0);
            file.mkdir();
        }
        for (int i = 0; i < s5.length; i++) {
            String str0 = path + "\\p5\\" + s5[i];
            File file = new File(str0);
            file.mkdir();
        }
        for (int i = 0; i < s6.length; i++) {
            String str0 = path + "\\p6\\" + s6[i];
            File file = new File(str0);
            file.mkdir();
        }
        for (int i = 0; i < s7.length; i++) {
            String str0 = path + "\\p7\\" + s7[i];
            File file = new File(str0);
            file.mkdir();
        }
        for (int i = 0; i < s8.length; i++) {
            String str0 = path + "\\p8\\" + s8[i];
            File file = new File(str0);
            file.mkdir();
        }
        for (int i = 0; i < s9.length; i++) {
            String str0 = path + "\\p9\\" + s9[i];
            File file = new File(str0);
            file.mkdir();
        }
        for (int i = 0; i < s10.length; i++) {
            String str0 = path + "\\p10\\" + s10[i];
            File file = new File(str0);
            file.mkdir();
        }
        for (int i = 0; i < s11.length; i++) {
            String str0 = path + "\\p11\\" + s11[i];
            File file = new File(str0);
            file.mkdir();
        }











    }


    /**
     * 该方法获取每一个变异体可以被哪些测试脚本检测
     * @return 每一个对应的变异体以及对应的检测它们的测试脚本
     */
    public Map<String,List<String>> getKilledInfo(){
        Map<String,List<String>> map = new HashMap<>();
        String path = "C:\\Users\\daihe\\Desktop\\EXP\\info.xls";
        File file = new File(path);
        try {
            Workbook workbook = Workbook.getWorkbook(file);
            Sheet sheet = workbook.getSheet(0);
            int rows = sheet.getRows();
            List<String> mutant1 = new ArrayList<>();
            List<String> mutant2 = new ArrayList<>();
            List<String> mutant3 = new ArrayList<>();
            List<String> mutant4 = new ArrayList<>();
            for (int i = 0; i < rows; i++) {
                if (sheet.getCell(0,i).getContents().equals("1"))
                    mutant1.add(String.valueOf(i));
            }
            for (int i = 0; i < rows; i++) {
                if (sheet.getCell(1,i).getContents().equals("1"))
                    mutant2.add(String.valueOf(i));
            }
            for (int i = 0; i < rows; i++) {
                if (sheet.getCell(2,i).getContents().equals("1"))
                    mutant3.add(String.valueOf(i));
            }
            for (int i = 0; i < rows; i++) {
                if (sheet.getCell(3,i).getContents().equals("1"))
                    mutant4.add(String.valueOf(i));
            }
            map.put("mutant1",mutant1);
            map.put("mutant2",mutant2);
            map.put("mutant3",mutant3);
            map.put("mutant4",mutant4);
            StringBuffer sb = new StringBuffer();
            sb.append("AOIS_45:");
            for (int i = 0; i < mutant1.size(); i++) {
                sb.append(mutant1.get(i) + ",");
            }
            sb.append("\n");
            sb.append("ROR_25:");
            for (int i = 0; i < mutant2.size(); i++) {
                sb.append(mutant2.get(i) + ",");
            }
            sb.append("\n");
            sb.append("SDL_6:");
            for (int i = 0; i < mutant3.size(); i++) {
                sb.append(mutant3.get(i) + ",");
            }
            sb.append("\n");
            sb.append("VDL_13:");
            for (int i = 0; i < mutant4.size(); i++) {
                sb.append(mutant4.get(i) + ",");
            }
            String file1 = "C:\\Users\\daihe\\Desktop\\EXP\\killednumber.txt";
            File file2 = new File(file1);
            PrintWriter pw = new PrintWriter(new FileWriter(file2));
            pw.write(sb.toString());
            pw.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (BiffException e) {
            e.printStackTrace();
        }


        return map;
    }

    /**
     * 该方法获取能够杀死变异体的所有测试脚本编号
     * @return list 存测测试脚本的编号
     */
    public List<String> getkilledscripts(){
        Map<String,List<String>> map = getKilledInfo();
        List<String> list0 = map.get("mutant1");
        List<String> list1 = map.get("mutant2");
        List<String> list2 = map.get("mutant3");
        List<String> list3 = map.get("mutant4");
        list0.addAll(list1);
        list0.addAll(list2);
        list0.addAll(list3);
        return list0;
    }


    public Map<Integer,List<String>> getpartitionInfo(){
        Map<Integer,List<String>> map = new HashMap<>();
        String path0 = "C:\\Users\\daihe\\Desktop\\EXP\\partitions\\p0";
        String path1 = "C:\\Users\\daihe\\Desktop\\EXP\\partitions\\p1";
        String path2 = "C:\\Users\\daihe\\Desktop\\EXP\\partitions\\p2";
        String path3 = "C:\\Users\\daihe\\Desktop\\EXP\\partitions\\p3";
        String path4 = "C:\\Users\\daihe\\Desktop\\EXP\\partitions\\p4";
        String path5 = "C:\\Users\\daihe\\Desktop\\EXP\\partitions\\p5";
        String path6 = "C:\\Users\\daihe\\Desktop\\EXP\\partitions\\p6";
        String path7 = "C:\\Users\\daihe\\Desktop\\EXP\\partitions\\p7";
        String path8 = "C:\\Users\\daihe\\Desktop\\EXP\\partitions\\p8";
        String path9 = "C:\\Users\\daihe\\Desktop\\EXP\\partitions\\p9";
        String path10 = "C:\\Users\\daihe\\Desktop\\EXP\\partitions\\p10";
        String path11 = "C:\\Users\\daihe\\Desktop\\EXP\\partitions\\p11";

        File file0 = new File(path0);
        File file1 = new File(path1);
        File file2 = new File(path2);
        File file3 = new File(path3);
        File file4 = new File(path4);
        File file5 = new File(path5);
        File file6 = new File(path6);
        File file7 = new File(path7);
        File file8 = new File(path8);
        File file9 = new File(path9);
        File file10 = new File(path10);
        File file11 = new File(path11);

        String[] names0 = file0.list();
        String[] names1 = file1.list();
        String[] names2 = file2.list();
        String[] names3 = file3.list();
        String[] names4 = file4.list();
        String[] names5 = file5.list();
        String[] names6 = file6.list();
        String[] names7 = file7.list();
        String[] names8 = file8.list();
        String[] names9 = file9.list();
        String[] names10 = file10.list();
        String[] names11 = file11.list();

        List<String> testframesofpartition0 = new ArrayList<>();
        List<String> testframesofpartition1 = new ArrayList<>();
        List<String> testframesofpartition2 = new ArrayList<>();
        List<String> testframesofpartition3 = new ArrayList<>();
        List<String> testframesofpartition4 = new ArrayList<>();
        List<String> testframesofpartition5 = new ArrayList<>();
        List<String> testframesofpartition6 = new ArrayList<>();
        List<String> testframesofpartition7 = new ArrayList<>();
        List<String> testframesofpartition8 = new ArrayList<>();
        List<String> testframesofpartition9 = new ArrayList<>();
        List<String> testframesofpartition10 = new ArrayList<>();
        List<String> testframesofpartition11 = new ArrayList<>();

        for (int i = 0; i < names0.length; i++) {
            testframesofpartition0.add(names0[i]);
            testframesofpartition1.add(names1[i]);
            testframesofpartition2.add(names2[i]);
        }
        map.put(0,testframesofpartition0);
        map.put(1,testframesofpartition1);
        map.put(2,testframesofpartition2);
        for (int i = 0; i < names4.length; i++) {
            testframesofpartition7.add(names7[i]);
            testframesofpartition4.add(names4[i]);
        }
        map.put(4,testframesofpartition4);
        map.put(7,testframesofpartition7);
        for (int i = 0; i < names5.length; i++) {
            testframesofpartition5.add(names5[i]);
            testframesofpartition6.add(names6[i]);
            testframesofpartition11.add(names11[i]);
        }
        map.put(5,testframesofpartition5);
        map.put(6,testframesofpartition6);
        map.put(11,testframesofpartition11);
        for (int i = 0; i < names8.length; i++) {
            testframesofpartition8.add(names8[i]);
            testframesofpartition9.add(names9[i]);
            testframesofpartition10.add(names10[i]);
        }
        map.put(8,testframesofpartition8);
        map.put(9,testframesofpartition9);
        map.put(10,testframesofpartition10);
        for (int i = 0; i < names3.length; i++) {
            testframesofpartition3.add(names3[i]);
        }
        map.put(3,testframesofpartition3);
        return map;
    }



    public static void main(String[] args) {
        ConfigurationEXP con = new ConfigurationEXP();
//        con.numberofTestScripts();
//        con.writetestframeAndMr();
//        List<String> temp = con.getKilledscripts();
//        System.out.println(temp.size());
        con.transformScriptstoNumber();
//        con.creatTestFrameToPartition();
//        con.getKilledInfo();
//        con.getkilledscripts();
    }


}
