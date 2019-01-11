package controller.util;

import java.io.*;
import java.util.*;

/**
 * 该类对ABBS的文件进行整理以便下一步测试
 */
public class ConfigurationABBS {

    /**
     * 该方法将测试脚本编号
     * @return map 是测试脚本与编号的键值对：键是测试脚本的名字，值是编号
     */
    public Map<String,String> numberofTestScripts(){
        String path = "C:\\Users\\daihe\\Desktop\\ABBS\\MyRandom1";
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
        String path = "C:\\Users\\daihe\\Desktop\\ABBS\\MyRandom1";
        File file = new File(path);
        String[] scriptsname = file.list();
        //记录测试帧涉及的测试脚本编号
        Map<String, Set<String>> map = new HashMap<>();
        for (int i = 0; i < scriptsname.length; i++) {
            String scriptsPath = path + "\\" + scriptsname[i] +
                    "\\" + "testAirlinesBaggageBillingService.java";
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
                    if (counter == 5) {
                        String[] strarray = temp.split(": ");
                        String parttwo = strarray[1];
                        String[] strarray2 = parttwo.split("         ");
                        source1 = strarray2[0];
                    } else if (counter == 6) {
                        String[] strarray = temp.split(": ");
                        String parttwo = strarray[1];
                        String[] strarray2 = parttwo.split("         ");
                        source2 = strarray2[0];
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
                        String[] strarray2 = parttwo.split("         ");
                        source5 = strarray2[0];
                    } else if (counter == 13) {
                        String[] strarray = temp.split(": ");
                        String parttwo = strarray[1];
                        String[] strarray2 = parttwo.split("         ");
                        follow1 = strarray2[0];
                    } else if (counter == 14) {
                        String[] strarray = temp.split(": ");
                        String parttwo = strarray[1];
                        String[] strarray2 = parttwo.split("         ");
                        follow2 = strarray2[0];
                    } else if (counter == 15) {
                        String[] strarray = temp.split(": ");
                        String parttwo = strarray[1];
                        String[] strarray2 = parttwo.split("         ");
                        follow3 = strarray2[0];
                    } else if (counter == 16) {
                        String[] strarray = temp.split(": ");
                        String parttwo = strarray[1];
                        String[] strarray2 = parttwo.split("         ");
                        follow4 = strarray2[0];
                    } else if (counter == 17) {
                        String[] strarray = temp.split(": ");
                        String parttwo = strarray[1];
                        String[] strarray2 = parttwo.split("         ");
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
                    System.out.println(followtestcase);
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
        String pathtest = "C:\\Users\\daihe\\Desktop\\ABBS\\testframeAndmr\\test";
        String testframeAndMr = "C:\\Users\\daihe\\Desktop\\ABBS\\testframeAndmr\\testframeAndNumber";
        File file1 = new File(pathtest);
        File file2 = new File(testframeAndMr);
        try {
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


    public List<String> getKilledscripts(){
        String path = "C:\\Users\\daihe\\Desktop\\ABBS\\killednumber.txt";
        File file = new File(path);
        List<String> result = new ArrayList<>();
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            String temp = "";
            while ((temp = bufferedReader.readLine()) != null){
                result.add(temp);
            }
            bufferedReader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public void transformScriptstoNumber(){
        String path = "C:\\Users\\daihe\\Desktop\\ABBS\\MyRandom1"; //文件的父目录
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
    


    public static void main(String[] args) {
        ConfigurationABBS con = new ConfigurationABBS();
//        con.numberofTestScripts();
//        con.writetestframeAndMr();
//        List<String> temp = con.getKilledscripts();
//        System.out.println(temp.size());
        con.transformScriptstoNumber();
    }

}
