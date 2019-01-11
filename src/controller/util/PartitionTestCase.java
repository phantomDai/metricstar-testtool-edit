package controller.util;



import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 将测试用例划分分区，名字是choices_蜕变关系编号
 */
public class PartitionTestCase {
    /**
     * 将测试用例划分到对应的分区之中
     * @param one 第一个选型
     * @param three 第三个选项
     * @param index 测试用例的编号
     * @param testcasename 测试用例的名字
     */
    public static void writeToPartition(String one, String three, int index,String testcasename){
        String targe = one + three;
        String path = "C:\\Users\\daihe\\Desktop\\PhoneBillCalculation\\Partitions";
        if (targe.equals("I-1aI-3a")){
            String parentpath = path + "\\p0\\" + String.valueOf(index);
            File parentDir = new File(parentpath);
            if (!parentDir.exists())
                parentDir.mkdir();
            String targetpath = path + "\\p0\\" + String.valueOf(index) + "\\" + testcasename;
            File targetDir = new File(targetpath);
            if (!targetDir.exists())
                targetDir.mkdir();
        }else if (targe.equals("I-1aI-3b")){
            String parentpath = path + "\\p1\\" + String.valueOf(index);
            File parentDir = new File(parentpath);
            if (!parentDir.exists())
                parentDir.mkdir();
            String targetpath = path + "\\p1\\" + String.valueOf(index) + "\\" + testcasename;
            File targetDir = new File(targetpath);
            if (!targetDir.exists())
                targetDir.mkdir();
        }else if (targe.equals("I-1bI-3a")){
            String parentpath = path + "\\p2\\" + String.valueOf(index);
            File parentDir = new File(parentpath);
            if (!parentDir.exists())
                parentDir.mkdir();
            String targetpath = path + "\\p2\\" + String.valueOf(index) + "\\" + testcasename;
            File targetDir = new File(targetpath);
            if (!targetDir.exists())
                targetDir.mkdir();
        }else if (targe.equals("I-1bI-3b")){
            String parentpath = path + "\\p3\\" + String.valueOf(index);
            File parentDir = new File(parentpath);
            if (!parentDir.exists())
                parentDir.mkdir();
            String targetpath = path + "\\p3\\" + String.valueOf(index) + "\\" + testcasename;
            File targetDir = new File(targetpath);
            if (!targetDir.exists())
                targetDir.mkdir();
        }

    }

    public static void main(String[] args) {
        String path = "C:\\Users\\daihe\\Desktop\\PhoneBillCalculation\\MyRandom1"; //test script
        String randompath = "C:\\Users\\daihe\\Desktop\\PhoneBillCalculation\\randomtestcase";
        File parentDir = new File(path);
        String[] kidsDir = new String[142];
        for (int i = 0; i < kidsDir.length; i++) {
            kidsDir[i] = String.valueOf(i);
        }
        List<String> testcasename = new ArrayList<>();
        Map<String,List<String>> testframeTomr = new HashMap<>(); //存放每一个testframe涉及的mr
        for (int i = 0; i < kidsDir.length; i++) {
            //read file one by one
            String targetfile = path + "\\" + kidsDir[i] + "\\" + "testBillCalculation.java" ; //get complete path of test script
            //read file
            try {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(targetfile)));
                String temp = "";
                int counter = 0; //record lines
                String source1 = "";
                String source2 = "";
                String source3 = "";
                String source4 = "";
                String follow1 = "";
                String follow2 = "";
                String follow3 = "";
                String follow4 = "";
                while((temp = bufferedReader.readLine()) != null){
                    counter++;
                    if (counter == 5){
                        String[] strarray = temp.split(": ");
                        String parttwo = strarray[1];
                        String[] strarray2 = parttwo.split("         ");
                        source1 = strarray2[0];
                    }else if (counter == 6){
                        String[] strarray = temp.split(": ");
                        String parttwo = strarray[1];
                        String[] strarray2 = parttwo.split("         ");
                        source2 = strarray2[0];
                    }else if (counter == 7){
                        String[] strarray = temp.split(": ");
                        String parttwo = strarray[1];
                        String[] strarray2 = parttwo.split("         ");
                        source3 = strarray2[0];
                    }else if (counter == 8){
                        String[] strarray = temp.split(": ");
                        String parttwo = strarray[1];
                        String[] strarray2 = parttwo.split("         ");
                        source4 = strarray2[0];
                    } else if (counter == 12){
                        String[] strarray = temp.split(": ");
                        String parttwo = strarray[1];
                        String[] strarray2 = parttwo.split("         ");
                        follow1 = strarray2[0];
                    }else if (counter == 13){
                        String[] strarray = temp.split(": ");
                        String parttwo = strarray[1];
                        String[] strarray2 = parttwo.split("         ");
                        follow2 = strarray2[0];
                    }else if (counter == 14){
                        String[] strarray = temp.split(": ");
                        String parttwo = strarray[1];
                        String[] strarray2 = parttwo.split("         ");
                        follow3 = strarray2[0];
                    }else if (counter == 15){
                        String[] strarray = temp.split(": ");
                        String parttwo = strarray[1];
                        String[] strarray2 = parttwo.split("         ");
                        follow4 = strarray2[0];
                    }
                }
                bufferedReader.close();
                String sourcetestcase = source1 + source2 + source3 + source4;
                String followtestcase = follow1 + follow2 + follow3 + follow4;

                if (!testcasename.contains(sourcetestcase)){
                    testcasename.add(sourcetestcase);
                    String parentrandomtestcase = randompath + "\\" + String.valueOf(testcasename.size()-1);
                    File parentrandomtestcasedir = new File(parentrandomtestcase);
                    if (!parentrandomtestcasedir.exists())
                        parentrandomtestcasedir.mkdir();
                    String RTC = parentrandomtestcase + "\\" + sourcetestcase;
                    File RTCDir = new File(RTC);
                    if (!RTCDir.exists())
                        RTCDir.mkdir();
                    PartitionTestCase.writeToPartition(source1,source3,testcasename.size()-1,sourcetestcase);
                    //将testframe对应的mr信息存入map中
                    List<String> mrname = new ArrayList<>();
                    mrname.add(String.valueOf(i));
                    testframeTomr.put(sourcetestcase,mrname);
                }else {//包含该键
                    //找到该键的位置
                    List<String> mrname = testframeTomr.get(sourcetestcase);
                    mrname.add(String.valueOf(i));
                    testframeTomr.put(sourcetestcase,mrname);
                }
                if (!testcasename.contains(followtestcase)){
                    testcasename.add(followtestcase);
                    String parentrandomtestcase = randompath + "\\" + String.valueOf(testcasename.size()-1);
                    File parentrandomtestcasedir = new File(parentrandomtestcase);
                    if (!parentrandomtestcasedir.exists())
                        parentrandomtestcasedir.mkdir();
                    String RTC = parentrandomtestcase + "\\" + followtestcase;
                    File RTCDir = new File(RTC);
                    if (!RTCDir.exists())
                        RTCDir.mkdir();
                    PartitionTestCase.writeToPartition(follow1,follow3,testcasename.size()-1,followtestcase);
                    List<String> fmrname = new ArrayList<>();
                    fmrname.add(String.valueOf(i));
                    testframeTomr.put(followtestcase,fmrname);
                }else {
                    //找到该键的位置
                    List<String> fmrname = testframeTomr.get(followtestcase);
                    fmrname.add(String.valueOf(i));
                    testframeTomr.put(followtestcase,fmrname);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        TestFrameAndMr tfm = new TestFrameAndMr();
        tfm.setTestframeTomr(testframeTomr);
        tfm.writeTestframeAndmr();
    }
}
