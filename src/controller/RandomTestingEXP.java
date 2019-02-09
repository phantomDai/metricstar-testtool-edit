package controller;

import controller.util.ConfigurationABBS;
import controller.util.ConfigurationEXP;

import java.io.*;
import java.util.*;

public class RandomTestingEXP {
    private int number; //测试用例的数目
    private int value; // 返回的下一个执行的测试用例
    private boolean flag; //上一个测试用例是否杀死了故障

    private Random random = new Random(number);


    public void testPBConefaults(){

        ConfigurationEXP configurationEXP = new ConfigurationEXP();
        Map<String,List<String>> map = configurationEXP.getKilledInfo();
        List<String> mutantlist1 = map.get("mutant1");
        List<String> mutantlist2 = map.get("mutant2");
        List<String> mutantlist3 = map.get("mutant3");
        List<String> mutantlist4 = map.get("mutant4");

        //初始化numberofkilledscripts
        List<String> numberofkilledscripts = new ArrayList<>();
        numberofkilledscripts = configurationEXP.getkilledscripts();

        //初始化killedInfo
        Map<String,List<String>> killedInfo = new HashMap<>();
        String killedInfoPath = "C:\\Users\\daihe\\Desktop\\EXP\\testframeAndmr\\test";
        File killedInfoFile = new File(killedInfoPath);
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(killedInfoFile));
            String tempstr = "";
            while ((tempstr = bufferedReader.readLine()) != null){
                String[] strarray = tempstr.split(":");
                String[] scrits = strarray[1].split(",");
                List<String> templist = new ArrayList<>();
                for (int i = 0; i < scrits.length; i++) {
                    templist.add(scrits[i]);
                }
                killedInfo.put(strarray[0],templist);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //首先测试揭示第一个故障需要的测试用例个数重复20次
        List<Double> alltimeforonefault = new ArrayList<>();
        List<Integer> allcounterforonefault = new ArrayList<>() ;
        for (int j = 0; j < 30; j++) {
            double starttime = System.currentTimeMillis();
            Random r = new Random(j);
            //记录测试的总个数
            int counter = 0 ;
            boolean flag = true;
            while (flag){
                //首先获得一个testframe的编号

                int testframe = r.nextInt(54);
                List<String> scripts = killedInfo.get(String.valueOf(testframe));

                for (int i = 0; i < scripts.size(); i++) {
                    counter++; //测试用例数目+1
                    if (numberofkilledscripts.contains(scripts.get(i))){
                        double endtime = System.currentTimeMillis();
                        double time = endtime - starttime;
                        alltimeforonefault.add(time);
                        flag = false;
                        break;
                    }
                }
            }

            allcounterforonefault.add(counter);
        }
        //首先求得平均数
        double meanforonefault = 0;
        double meantime = 0;
        double sum = 0.0;
        double sumtime = 0.0;
        for (int i = 0; i < allcounterforonefault.size(); i++) {
            sum += allcounterforonefault.get(i);
            sumtime += alltimeforonefault.get(i);
        }
        meanforonefault = sum / 30 ;
        meantime = sumtime /30;
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < allcounterforonefault.size(); i++) {
            sb.append(String.valueOf(allcounterforonefault.get(i)) + "\n");
        }
        sb.append("mean" + String.valueOf(meanforonefault) + "\n");
        sb.append("time information**************" + "\n");
        for (int i = 0; i < alltimeforonefault.size(); i++) {
            sb.append(String.valueOf(alltimeforonefault.get(i)) + "\n");
        }
        sb.append("mean time" + String.valueOf(meantime));

        //将结果记录在文件中
        String RTonefaultsresults = "C:\\Users\\daihe\\Desktop\\EXP\\onefaultresults" +
                "\\RTonefaultsresults";
        File onefaultsresults = new File(RTonefaultsresults);
        try {
            if (!onefaultsresults.exists())
                onefaultsresults.createNewFile();
            PrintWriter pw = new PrintWriter(new FileWriter(onefaultsresults));
            pw.write(sb.toString());
            pw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public double[] testPBCHalfTestCases(int seed){
        ConfigurationEXP configurationEXP = new ConfigurationEXP();
        Map<String,List<String>> map = configurationEXP.getKilledInfo();
        List<String> mutantlist1 = map.get("mutant1");
        List<String> mutantlist2 = map.get("mutant2");
        List<String> mutantlist3 = map.get("mutant3");
        List<String> mutantlist4 = map.get("mutant4");

        //初始化numberofkilledscripts
        List<String> numberofkilledscripts = new ArrayList<>();
        numberofkilledscripts = configurationEXP.getkilledscripts();

        //初始化killedInfo
        Map<String,List<String>> killedInfo = new HashMap<>();
        String killedInfoPath = "C:\\Users\\daihe\\Desktop\\EXP\\testframeAndmr\\test";
        File killedInfoFile = new File(killedInfoPath);
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(killedInfoFile));
            String tempstr = "";
            while ((tempstr = bufferedReader.readLine()) != null){
                String[] strarray = tempstr.split(":");
                String[] scrits = strarray[1].split(",");
                List<String> templist = new ArrayList<>();
                for (int i = 0; i < scrits.length; i++) {
                    templist.add(scrits[i]);
                }
                killedInfo.put(strarray[0],templist);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //然后测试72个测试用例揭示的故障数目,重复20遍
        double starttime = System.currentTimeMillis();
        double time = 0.0;
        Random r = new Random(seed);
        //记录测试的总个数
        int counter = 0 ;
        //记录杀死的故障数目
        int killedmutants = 0 ;
        boolean flag = true;
        while (flag){
            //首先获得一个testframe的编号
            int testframe = r.nextInt(54);
            List<String> scripts = killedInfo.get(String.valueOf(testframe));
            for (int j = 0; j < scripts.size(); j++) {
                counter++; //测试用例数目+1
                String s = scripts.get(j);
                if (counter <= 80 && killedmutants < 4){
                    if (numberofkilledscripts.contains(scripts.get(j))){
                        killedmutants++;
                        //移除杀死的故障
                        if (mutantlist1.contains(scripts.get(j))){

                            for (int i = 0; i < mutantlist1.size(); i++) {
                                numberofkilledscripts.remove(mutantlist1.get(i));
                            }
                        }else if(mutantlist2.contains(scripts.get(j))){
                            for (int i = 0; i < mutantlist2.size(); i++) {
                                numberofkilledscripts.remove(mutantlist2.get(i));
                            }
                        }else if(mutantlist3.contains(scripts.get(j))){
                            for (int i = 0; i < mutantlist3.size(); i++) {
                                numberofkilledscripts.remove(mutantlist3.get(i));
                            }
                        }else if(mutantlist4.contains(scripts.get(j))){
                            for (int i = 0; i < mutantlist4.size(); i++) {
                                numberofkilledscripts.remove(mutantlist4.get(i));
                            }
                        }
                    }
                }else {
                    double endtime = System.currentTimeMillis();
                    time = endtime = starttime;
                    flag = false;
                    break;
                }
            }
        }
        double[] re = new double[2];
        re[0] = killedmutants;
        re[1] = time;
        return re;
    }

    public static void main(String[] args) {
        RandomTestingEXP rt = new RandomTestingEXP();
        rt.testPBConefaults();
        List<Double> timelist = new ArrayList<>();
        List<Double> list = new ArrayList<>();
        double sum = 0.0;
        double sumtime = 0.0;
        for (int i = 0; i < 30; i++) {
            double[] re = new double[2];
            re = rt.testPBCHalfTestCases(i);
            sum += re[0];
            sumtime += re[1];
            list.add(re[0]);
            timelist.add(re[1]);
        }
        double mean = sum / 30;
        double meantime = sumtime / 30;
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < list.size(); i++) {
            sb.append(String.valueOf(list.get(i)) + "\n");
        }
        sb.append("mean"+String.valueOf(mean) + "\n");
        sb.append("time information ***************");
        for (int i = 0; i < timelist.size(); i++) {
            sb.append(String.valueOf(timelist.get(i)) + "\n");
        }
        sb.append("mean time " + String.valueOf(meantime));

        //将结果记录在文件中
        String RTonefaultsresults = "C:\\Users\\daihe\\Desktop\\EXP\\onefaultresults" +
                "\\RThalftestcasesresults";
        File halftestcasesresults = new File(RTonefaultsresults);
        try {
            if (!halftestcasesresults.exists())
                halftestcasesresults.createNewFile();
            PrintWriter pw = new PrintWriter(new FileWriter(halftestcasesresults));
            pw.write(sb.toString());
            pw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }



    }
}
