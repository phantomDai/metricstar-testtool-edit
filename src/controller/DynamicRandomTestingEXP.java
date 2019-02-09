package controller;

import controller.util.ConfigurationABBS;
import controller.util.ConfigurationEXP;

import java.io.*;
import java.util.*;

public class DynamicRandomTestingEXP {
    private double[] testProfile;

    private double epsilon = 0.05;


    /**
     * 根据测试剖面选择下一个分区
     * @return
     */
    private int nextPartition() {
        int np = -1;
        Random r = new Random();
        double rd = r.nextDouble();
        double sum = 0;
        do {
            ++np;
            if (np == 7)
                break;
            sum += testProfile[np];
        }while (rd >= sum && np < testProfile.length);

        return np;
    }

    public void adjustTestProfile(boolean flag, int prePartition) {
        if (flag) {
            double sum = 0;
            for (int i = 0; i < testProfile.length; ++i) {
                if (i != prePartition) {
                    testProfile[i] -= epsilon / (testProfile.length - 1);
                    if (testProfile[i] < 0)
                        testProfile[i] = 0;
                    sum += testProfile[i];
                }
            }
            testProfile[prePartition] = 1 - sum;
        } else {
            if (testProfile[prePartition] < epsilon) {
                for (int i = 0; i < testProfile.length; ++i) {
                    if (i != prePartition) {
                        testProfile[i] += testProfile[prePartition] / (testProfile.length - 1);
                    }
                }
                testProfile[prePartition] = 0;
            } else {
                for (int i = 0; i < testProfile.length; ++i) {
                    if (i != prePartition) {
                        testProfile[i] += epsilon / (testProfile.length - 1);
                    }
                }
                testProfile[prePartition] -= epsilon;
            }
        }
    }

    /**
     * 原始测试用例与衍生测试用例不在同一个分区时调整测试剖面的策略
     */
    public void metaAdjustTestProfile(boolean flag, int prePartition,int fprePartition){
        if (flag) {
            double sum = 0;
            for (int i = 0; i < testProfile.length; ++i) {
                if (i != prePartition && i != fprePartition) {
                    testProfile[i] -= (2 * epsilon) / (testProfile.length - 2);
                    if (testProfile[i] < 0)
                        testProfile[i] = 0;
                    sum += testProfile[i];
                }
            }
            testProfile[prePartition] = testProfile[prePartition] + (1 - sum - testProfile[prePartition] - testProfile[fprePartition]) / 2;
            testProfile[fprePartition] = testProfile[fprePartition] + (1 - sum - testProfile[prePartition] - testProfile[fprePartition]) / 2;
        } else {
            if (testProfile[prePartition] < epsilon) {
                double oldprePartition = testProfile[prePartition];
                testProfile[prePartition] = 0;
                double oldfprePartition = testProfile[fprePartition];
                if (testProfile[fprePartition] < epsilon)
                    testProfile[fprePartition] = 0;
                else
                    testProfile[fprePartition] = testProfile[fprePartition] - epsilon;

                for (int i = 0; i < testProfile.length; ++i) {
                    if (i != prePartition && i != fprePartition) {
                        testProfile[i] = testProfile[i] + ((oldprePartition - testProfile[prePartition]) +
                                (oldfprePartition - testProfile[fprePartition])) / (testProfile.length - 2);
                    }
                }
            } else {
                double oldfprePartition = testProfile[fprePartition];
                if (testProfile[fprePartition] < epsilon)
                    testProfile[fprePartition] = 0;
                else
                    testProfile[fprePartition] = testProfile[fprePartition] - epsilon;
                double oldprePartition = testProfile[prePartition];
                testProfile[prePartition] = testProfile[prePartition] - epsilon;

                for (int i = 0; i < testProfile.length; ++i) {
                    if (i != prePartition && i != fprePartition) {
                        testProfile[i] = testProfile[i] + ((oldprePartition - testProfile[prePartition]) +
                                (oldfprePartition - testProfile[fprePartition])) / (testProfile.length - 2);
                    }
                }
            }
        }
    }



    /**
     * 以分区为中心的算法，并统计揭示一个故障需要的测试用例数目
     */
    public void testonefaultsforP(){
        ConfigurationEXP configurationEXP = new ConfigurationEXP();
        Map<String,List<String>> mapkilledinfo = configurationEXP.getKilledInfo();
        List<String> mutantlist1 = mapkilledinfo.get("mutant1");
        List<String> mutantlist2 = mapkilledinfo.get("mutant2");
        List<String> mutantlist3 = mapkilledinfo.get("mutant3");
        List<String> mutantlist4 = mapkilledinfo.get("mutant4");

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

        Map<Integer,List<String>> map = new HashMap<>();
        map = configurationEXP.getpartitionInfo();
        //重复执行20次
        List<Double> alltimeforonefault = new ArrayList<>();
        List<Integer> allcoubterforonefault = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            double starttime = System.currentTimeMillis();
            int counter = 0;//记录杀死一个故障需要的测试用例数目
            //创建新的random
            Random random = new Random(i);
            //初始化测试剖面
            testProfile = new double[12];
            for (int k = 0; k < 12; k++) {
                testProfile[k] = 1.0 / 12;
            }
            //获得分区编号
            boolean flag = true;
            while(flag){
                int spartition = nextPartition();
                List<String> templist = map.get(spartition);
                //选择在选中分区中的测试用例
                String testcase = "";
                do{
                    testcase = String.valueOf(random.nextInt(54));
                }while(!templist.contains(testcase));

                //找到与testcase相应的scripts
                List<String> scripts = killedInfo.get(testcase);
                for (int j = 0; j < scripts.size(); j++) {
                    counter++;
                    boolean tempflag = numberofkilledscripts.contains(scripts.get(j));//是否杀死变异体
                    if (tempflag){//杀死了变异体
                        flag = false;
                        allcoubterforonefault.add(counter);
                        double endtime = System.currentTimeMillis();
                        double time = endtime - starttime;
                        alltimeforonefault.add(time);
//						System.out.println(counter);
                        break;
                    }else {//没有杀死变异体，需要调整测试剖面
                        //需要判断是否属于同一个分区，并且判断分区号
                        //获得测试脚本对应的原始与衍生测试用例
                        String[] partitions = new String[3];
                        partitions = isBelongtoOnePartition(scripts.get(j));
                        if (partitions[2].equals("1"))
                            adjustTestProfile(false,Integer.parseInt(partitions[0]));
                        else
                            metaAdjustTestProfile(false,Integer.parseInt(partitions[0]),Integer.parseInt(partitions[1]));
                    }
                }
            }
        }
        //首先求得平均数
        double meanforonefault = 0;
        double meantime = 0;
        double sum = 0.0;
        double sumtime = 0.0;
        for (int i = 0; i < allcoubterforonefault.size(); i++) {
            sum += allcoubterforonefault.get(i);
            sumtime += alltimeforonefault.get(i);
        }
        meanforonefault = sum / 30 ;
        meantime = sumtime /30;
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < allcoubterforonefault.size(); i++) {
            sb.append(String.valueOf(allcoubterforonefault.get(i)) + "\n");
        }
        sb.append("mean：" + String.valueOf(meanforonefault) + "\n");
        sb.append("time information" + "*******************" + "\n");
        for (int i = 0; i < alltimeforonefault.size(); i++) {
            sb.append(String.valueOf(alltimeforonefault.get(i)) + "\n");
        }
        sb.append("mean time" + String.valueOf(meantime));

        //将结果记录在文件中
        String RTonefaultsresults = "C:\\Users\\daihe\\Desktop\\EXP\\onefaultresults" +
                "\\DRTonefaultsresultsforP";
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

    /**
     * 以分区为中心的算法，并统计执行一半的测试用例揭示的故障数目
     */
    public void testhalftestcasesforP(){
        ConfigurationEXP configurationEXP = new ConfigurationEXP();
        Map<String,List<String>> mapkilledinfo = configurationEXP.getKilledInfo();
        List<String> mutantlist1 = mapkilledinfo.get("mutant1");
        List<String> mutantlist2 = mapkilledinfo.get("mutant2");
        List<String> mutantlist3 = mapkilledinfo.get("mutant3");
        List<String> mutantlist4 = mapkilledinfo.get("mutant4");

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
        //得到4个分区中的测试用例编号
        Map<Integer,List<String>> map = new HashMap<>();
        map = configurationEXP.getpartitionInfo();

        //重复执行20次
        List<Double> alltimeforhaltcase = new ArrayList<>();
        List<Integer> allcoubterforhaftcase = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            numberofkilledscripts = configurationEXP.getkilledscripts();
            double starttime = System.currentTimeMillis();
            int counter = 0;//记录杀死一个故障需要的测试用例数目
            int killedmutants = 0;//记录杀死的变异体个数
            //创建新的random
            Random random = new Random(i);
            //初始化测试剖面
            testProfile = new double[12];
            for (int k = 0; k < 12; k++) {
                testProfile[k] = 1.0 / 12;
            }
            //获得分区编号
            boolean flag = true;
            while(flag){
                int spartition = nextPartition();
                List<String> templist = map.get(spartition);
                //选择在选中分区中的测试用例
                String testcase = "";
                do{
                    testcase = String.valueOf(random.nextInt(54));
                }while(!templist.contains(testcase));

                //找到与testcase相应的scripts
                List<String> scripts = killedInfo.get(testcase);
                for (int j = 0; j < scripts.size(); j++) {
                    counter++;
                    if (counter <= 100 && killedmutants < 4){
                        boolean tempflag = numberofkilledscripts.contains(scripts.get(j));//是否杀死变异体
                        if (tempflag){//杀死了变异体
                            killedmutants++;
                            if (mutantlist1.contains(scripts.get(j))){
                                for (int k = 0; k < mutantlist1.size(); k++) {
                                    numberofkilledscripts.remove(mutantlist1.get(k));
                                }
                            }else if(mutantlist2.contains(scripts.get(j))){
                                for (int k = 0; k < mutantlist2.size(); k++) {
                                    numberofkilledscripts.remove(mutantlist2.get(k));
                                }
                            }else if(mutantlist3.contains(scripts.get(j))){
                                for (int k = 0; k < mutantlist3.size(); k++) {
                                    numberofkilledscripts.remove(mutantlist3.get(k));
                                }
                            }else if(mutantlist4.contains(scripts.get(j))){
                                for (int k = 0; k < mutantlist4.size(); k++) {
                                    numberofkilledscripts.remove(mutantlist4.get(k));
                                }
                            }
                            //需要判断是否属于同一个分区，并且判断分区号
                            //获得测试脚本对应的原始与衍生测试用例
                            String[] partitions = new String[3];
                            partitions = isBelongtoOnePartition(scripts.get(j));
                            if (partitions[2].equals("1"))
                                adjustTestProfile(true,Integer.parseInt(partitions[0]));
                            else
                                metaAdjustTestProfile(true,Integer.parseInt(partitions[0]),
                                        Integer.parseInt(partitions[1]));

                        }else {//没有杀死变异体，需要调整测试剖面
                            //需要判断是否属于同一个分区，并且判断分区号
                            //获得测试脚本对应的原始与衍生测试用例
                            String[] testcases = new  String[2];
                            testcases = getNameoftestcases(scripts.get(j));
                            String[] partitions = new String[3];
                            partitions = isBelongtoOnePartition(scripts.get(j));
                            if (partitions[2].equals("1"))
                                adjustTestProfile(false,Integer.parseInt(partitions[0]));
                            else
                                metaAdjustTestProfile(false,Integer.parseInt(partitions[0]),
                                        Integer.parseInt(partitions[1]));
                        }
                    }else{
                        double endtime = System.currentTimeMillis();
                        double time = endtime - starttime;
                        alltimeforhaltcase.add(time);
                        flag = false;
                        break;
                    }
                }
            }
            allcoubterforhaftcase.add(killedmutants);
        }
        //首先求得平均数
        double meanforonefault = 0;
        double meantime = 0 ;
        double sum = 0.0;
        double sumtime = 0.0;
        for (int i = 0; i < allcoubterforhaftcase.size(); i++) {
            sum += allcoubterforhaftcase.get(i);
            sumtime += alltimeforhaltcase.get(i);
        }
        meanforonefault = sum / 30 ;
        meantime = sumtime / 30;
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < allcoubterforhaftcase.size(); i++) {
            sb.append(String.valueOf(allcoubterforhaftcase.get(i)) + "\n");
        }
        sb.append("mean：" + String.valueOf(meanforonefault) + "\n");
        sb.append("time information ******************" + "\n");
        for (int i = 0; i < alltimeforhaltcase.size(); i++) {
            sb.append(String.valueOf(alltimeforhaltcase.get(i)) + "\n");
        }
        sb.append("mean time :" + String.valueOf(meantime));

        //将结果记录在文件中
        String RTonefaultsresults = "C:\\Users\\daihe\\Desktop\\EXP\\onefaultresults" +
                "\\DRThalftestcasesforP";
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

    /**
     * 以MR为中心的算法，并统计揭示一个故障需要的测试用例数目
     */
    public void testOneFaultsforM(){
        ConfigurationEXP configurationEXP = new ConfigurationEXP();
        Map<String,List<String>> mapkilledinfo = configurationEXP.getKilledInfo();
        List<String> mutantlist1 = mapkilledinfo.get("mutant1");
        List<String> mutantlist2 = mapkilledinfo.get("mutant2");
        List<String> mutantlist3 = mapkilledinfo.get("mutant3");
        List<String> mutantlist4 = mapkilledinfo.get("mutant4");

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

        Map<Integer,List<String>> map = new HashMap<>();
        map = configurationEXP.getpartitionInfo();
        //重复执行20次
        List<Integer> allcoubterforonefault = new ArrayList<>();
        List<Double> alltimeforonefault = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            double starttiem = System.currentTimeMillis();//开始测试的时间
            int counter = 0;//记录杀死一个故障需要的测试用例数目
            //创建新的random
            Random random = new Random(i);
            Random randomMR = new Random(i);
            //初始化测试剖面
            testProfile = new double[12];
            for (int k = 0; k < 12; k++) {
                testProfile[k] = 1.0 / 12;
            }
            boolean f = true;
            while(f){
                //获得分区编号
                if (counter == 0){//第一次执行时先随机选一个MR
                    int mr = randomMR.nextInt(1130);
                    counter++;
                    if (numberofkilledscripts.contains(String.valueOf(mr))){//揭示了故障
                        double endtime = System.currentTimeMillis();
                        double time = endtime - starttiem;
                        allcoubterforonefault.add(counter);
                        alltimeforonefault.add(time);
                        break;
                    }else {//没有揭示故障
                        //需要调整分区
                        //首先获取该涉及到的测试用例情况
                        String[] testcases = new  String[2];
                        testcases = getNameoftestcases(String.valueOf(mr));
                        String[] partitions = new String[3];
                        partitions = isBelongtoOnePartition(String.valueOf(mr));
                        if (partitions[2].equals("1"))
                            adjustTestProfile(false,Integer.parseInt(partitions[0]));
                        else
                            metaAdjustTestProfile(false,Integer.parseInt(partitions[0]),
                                    Integer.parseInt(partitions[1]));
                    }
                }else {
                    boolean flag = true;
                    while(flag){
                        int spartition = nextPartition();
                        //选中分区中的测试用例
                        List<String> templist = map.get(spartition);
                        //合并选中分区的测试用例涉及的MRs
                        List<String> partitionMRList = new ArrayList<>();
                        for (int j = 0; j < templist.size(); j++) {
                            List<String> templi = new ArrayList<>();
                            templi = killedInfo.get(templist.get(j));
                            for (int k = 0; k < templi.size(); k++) {
                                partitionMRList.add(templi.get(k));
                            }
                        }
                        String mr = "";
                        do{
                            mr = String.valueOf(randomMR.nextInt(735));
                        }while(partitionMRList.contains(mr));//知道找到分区涉及的MR
                        counter++;
                        //验证该mr是否可以结束软件中的故障
                        if (numberofkilledscripts.contains(mr)){//揭示了故障
                            double endtime = System.currentTimeMillis();
                            double time = endtime - starttiem;
                            allcoubterforonefault.add(counter);
                            alltimeforonefault.add(time);
                            f = false;
                            break;
                        }else {//没有揭示故障则继续执行并更新测试剖面
                            //需要调整分区
                            //首先获取该涉及到的测试用例情况
                            String[] testcases = new  String[2];
                            testcases = getNameoftestcases(String.valueOf(mr));
                            String[] partitions = new String[3];
                            partitions = isBelongtoOnePartition(String.valueOf(mr));
                            if (partitions[2].equals("1"))
                                adjustTestProfile(false,Integer.parseInt(partitions[0]));
                            else
                                metaAdjustTestProfile(false,Integer.parseInt(partitions[0]),
                                        Integer.parseInt(partitions[1]));
                        }
                    }
                }
            }
        }
        //首先求得平均数
        double meanforonefault = 0.0;
        double meantime = 0.0;
        double sum = 0.0;
        double sumtime = 0.0;
        for (int i = 0; i < allcoubterforonefault.size(); i++) {
            sum += allcoubterforonefault.get(i);
            sumtime += alltimeforonefault.get(i);
        }
        meanforonefault = sum / 30 ;
        meantime = sumtime / 30;
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < allcoubterforonefault.size(); i++) {
            sb.append(String.valueOf(allcoubterforonefault.get(i)) + "\n");
        }
        sb.append("mean" + String.valueOf(meanforonefault) + "\n");
        sb.append("time information ***************" + "\n");
        for (int i = 0; i < alltimeforonefault.size(); i++) {
            sb.append(String.valueOf(alltimeforonefault.get(i)) + "\n");
        }
        sb.append("time mean :" + String.valueOf(meantime));

        //将结果记录在文件中
        String RTonefaultsresults = "C:\\Users\\daihe\\Desktop\\EXP\\onefaultresults" +
                "\\DRTonefaultsresultsforM";
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


    /**
     * 以MR为中心的算法，并统计执行一半的测试用例揭示的故障数目
     */
    public void testhalftestcasesforM(){
        ConfigurationEXP configurationEXP = new ConfigurationEXP();
        Map<String,List<String>> mapkilledinfo = configurationEXP.getKilledInfo();
        List<String> mutantlist1 = mapkilledinfo.get("mutant1");
        List<String> mutantlist2 = mapkilledinfo.get("mutant2");
        List<String> mutantlist3 = mapkilledinfo.get("mutant3");
        List<String> mutantlist4 = mapkilledinfo.get("mutant4");

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
        //得到4个分区中的测试用例编号
        Map<Integer,List<String>> map = new HashMap<>();
        map = configurationEXP.getpartitionInfo();
        //重复执行20次
        List<Integer> allcountforhalftestcase = new ArrayList<>();
        List<Double> alltimeforonefault = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            numberofkilledscripts = configurationEXP.getkilledscripts();
            double starttiem = System.currentTimeMillis();//开始测试的时间
            int counter = 0;//记录杀死一个故障需要的测试用例数目
            int killedmutants = 0;
            //创建新的random
            Random random = new Random(i);
            Random randomMR = new Random(i);
            //初始化测试剖面
            testProfile = new double[12];
            for (int k = 0; k < 12; k++) {
                testProfile[k] = 1.0 / 12;
            }
            boolean f = true;
            while(f){
                //获得分区编号
                counter++;
                if (counter == 1){//第一次执行时先随机选一个MR
                    int mr = randomMR.nextInt(1130);

                    if (numberofkilledscripts.contains(String.valueOf(mr))){//揭示了故障
                        killedmutants++;
                        if (mutantlist1.contains(String.valueOf(mr))){
                            for (int k = 0; k < mutantlist1.size(); k++) {
                                numberofkilledscripts.remove(mutantlist1.get(k));
                            }
                        }else if(mutantlist2.contains(String.valueOf(mr))){
                            for (int k = 0; k < mutantlist2.size(); k++) {
                                numberofkilledscripts.remove(mutantlist2.get(k));
                            }
                        }else if(mutantlist3.contains(String.valueOf(mr))){
                            for (int k = 0; k < mutantlist3.size(); k++) {
                                numberofkilledscripts.remove(mutantlist3.get(k));
                            }
                        }else if(mutantlist4.contains(String.valueOf(mr))){
                            for (int k = 0; k < mutantlist4.size(); k++) {
                                numberofkilledscripts.remove(mutantlist4.get(k));
                            }
                        }
                        //需要调整分区
                        //首先获取该涉及到的测试用例情况
                        String[] testcases = new  String[2];
                        testcases = getNameoftestcases(String.valueOf(mr));
                        String[] partitions = new String[3];
                        partitions = isBelongtoOnePartition(String.valueOf(mr));
                        if (partitions[2].equals("1"))
                            adjustTestProfile(true,Integer.parseInt(partitions[0]));
                        else
                            metaAdjustTestProfile(true,Integer.parseInt(partitions[0]),
                                    Integer.parseInt(partitions[1]));
                    }else {//没有揭示故障
                        //需要调整分区
                        //首先获取该涉及到的测试用例情况
                        String[] testcases = new  String[2];
                        testcases = getNameoftestcases(String.valueOf(mr));
                        String[] partitions = new String[3];
                        partitions = isBelongtoOnePartition(String.valueOf(mr));
                        if (partitions[2].equals("1"))
                            adjustTestProfile(false,Integer.parseInt(partitions[0]));
                        else
                            metaAdjustTestProfile(false,Integer.parseInt(partitions[0]),
                                    Integer.parseInt(partitions[1]));
                    }
                }else {
                    boolean flag = true;
                    while(flag){
                        if ( counter <= 100 && killedmutants < 4){
                            int spartition = nextPartition();
                            //选中分区中的测试用例
                            List<String> templist = map.get(spartition);
                            //合并选中分区的测试用例涉及的MRs
                            List<String> partitionMRList = new ArrayList<>();
                            for (int j = 0; j < templist.size(); j++) {
                                List<String> templi = new ArrayList<>();
                                templi = killedInfo.get(templist.get(j));
                                for (int k = 0; k < templi.size(); k++) {
                                    partitionMRList.add(templi.get(k));
                                }
                            }
                            String mr = "";
                            do{
                                mr = String.valueOf(randomMR.nextInt(142));
                            }while(partitionMRList.contains(mr));//知道找到分区涉及的MR
                            counter++;
                            //验证该mr是否可以结束软件中的故障
                            if (numberofkilledscripts.contains(mr)){//揭示了故障
                                //需要调整分区
                                //首先获取该涉及到的测试用例情况
                                killedmutants++;
                                if (mutantlist1.contains(String.valueOf(mr))){
                                    for (int k = 0; k < mutantlist1.size(); k++) {
                                        numberofkilledscripts.remove(mutantlist1.get(k));
                                    }
                                }else if(mutantlist2.contains(String.valueOf(mr))){
                                    for (int k = 0; k < mutantlist2.size(); k++) {
                                        numberofkilledscripts.remove(mutantlist2.get(k));
                                    }
                                }else if(mutantlist3.contains(String.valueOf(mr))){
                                    for (int k = 0; k < mutantlist3.size(); k++) {
                                        numberofkilledscripts.remove(mutantlist3.get(k));
                                    }
                                }else if(mutantlist4.contains(String.valueOf(mr))){
                                    for (int k = 0; k < mutantlist4.size(); k++) {
                                        numberofkilledscripts.remove(mutantlist4.get(k));
                                    }
                                }
                                String[] testcases = new  String[2];
                                testcases = getNameoftestcases(String.valueOf(mr));
                                String[] partitions = new String[3];
                                partitions = isBelongtoOnePartition(String.valueOf(mr));
                                if (partitions[2].equals("1"))
                                    adjustTestProfile(false,Integer.parseInt(partitions[0]));
                                else
                                    metaAdjustTestProfile(false,Integer.parseInt(partitions[0]),
                                            Integer.parseInt(partitions[1]));
                            }else {//没有揭示故障则继续执行并更新测试剖面
                                //需要调整分区
                                //首先获取该涉及到的测试用例情况
                                String[] testcases = new  String[2];
                                testcases = getNameoftestcases(String.valueOf(mr));
                                String[] partitions = new String[3];
                                partitions = isBelongtoOnePartition(String.valueOf(mr));
                                if (partitions[2].equals("1"))
                                    adjustTestProfile(false,Integer.parseInt(partitions[0]));
                                else
                                    metaAdjustTestProfile(false,Integer.parseInt(partitions[0]),
                                            Integer.parseInt(partitions[1]));
                            }
                        }else {
                            double endtime = System.currentTimeMillis();
                            double time = endtime - starttiem;
                            alltimeforonefault.add(time);
                            allcountforhalftestcase.add(killedmutants);
                            f = false;
                            break;
                        }
                    }
                }
            }
        }
        //首先求得平均数
        double meanforonefault = 0.0;
        double meantime = 0.0;
        double sum = 0.0;
        double sumtime = 0.0;
        for (int i = 0; i < allcountforhalftestcase.size(); i++) {
            sum += allcountforhalftestcase.get(i);
            sumtime += alltimeforonefault.get(i);
        }
        meanforonefault = sum / 30 ;
        meantime = sumtime / 30;
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < allcountforhalftestcase.size(); i++) {
            sb.append(String.valueOf(allcountforhalftestcase.get(i)) + "\n");
        }
        sb.append("mean" + String.valueOf(meanforonefault) + "\n");
        sb.append("time information ***************" + "\n");
        for (int i = 0; i < alltimeforonefault.size(); i++) {
            sb.append(String.valueOf(alltimeforonefault.get(i)) + "\n");
        }
        sb.append("time mean :" + String.valueOf(meantime));

        //将结果记录在文件中
        String RTonefaultsresults = "C:\\Users\\daihe\\Desktop\\EXP\\onefaultresults" +
                "\\DRThalftestcasesresultsforM";
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




    /**
     * 根据测试脚本的编号得到里面的原始与衍生测试用例,并判断两个测试用例是否属于同一个分区
     * @param numberofscripts 测试脚本的编号（0-141）
     * @return String[] 两个测试用例属于同一个分区，false不在同一个分区
     */
    private String[] getNameoftestcases(String numberofscripts){
        String path = "C:\\Users\\daihe\\Desktop\\EXP\\MyRandom1" + "\\" +
                numberofscripts + "\\" + "testExpenseReimbursementSystem.java";
        File file = new File(path);
        if (!file.exists())
            System.out.println("文件不存在，请检查测试脚本" + numberofscripts);

        String[] result = new String[2];
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
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
            bufferedReader.close();
            String sourcetestcase = source1 + source2 + source3 + source4 + source5;
            String followtestcase = follow1 + follow2 + follow3 + follow4 + follow5;
            result[0] = sourcetestcase;
            result[1] = followtestcase;
        }catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
    /**
     * 根据测试脚本的编号，判断是否属于同一个分区，并返回结果
     * @return 0：原始测试用例的分区；1，衍生测试用例的分区；3，是否属于同一个分区
     */
    private String[] isBelongtoOnePartition(String numberofscripts){
        String path = "C:\\Users\\daihe\\Desktop\\EXP\\MyRandom1" + "\\" +
                numberofscripts + "\\" + "testExpenseReimbursementSystem.java";
        File file = new File(path);
        if (!file.exists())
            System.out.println("文件不存在，请检查测试脚本" + numberofscripts);

        String[] result = new String[3];
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            String temp = "";
            int counter = 0; //record lines
            String source1 = "";
            String source3 = "";
            String follow1 = "";
            String follow3 = "";

            while ((temp = bufferedReader.readLine()) != null) {
                counter++;
                if (counter >= 17)
                    break;
                if (counter == 5) {
                    String[] strarray = temp.split(": ");
                    String parttwo = strarray[1];
                    String[] strarray2 = parttwo.split("         ");
                    source1 = strarray2[0];
                } else if (counter == 7) {
                    String[] strarray = temp.split(": ");
                    String parttwo = strarray[1];
                    String[] strarray2 = parttwo.split("         ");
                    source3 = strarray2[0];
                }  else if (counter == 12) {
                    String[] strarray = temp.split(": ");
                    String parttwo = strarray[1];
                    String[] strarray2 = parttwo.split("         ");
                    follow1 = strarray2[0];
                } else if (counter == 14) {
                    String[] strarray = temp.split(": ");
                    String parttwo = strarray[1];
                    String[] strarray2 = parttwo.split("         ");
                    follow3 = strarray2[0];
                }
            }
            bufferedReader.close();
            String sourcetestcase = source1 + source3;
            String followtestcase = follow1 + follow3;
            result[0] = String.valueOf(getPartitions(sourcetestcase));
            result[1] = String.valueOf(getPartitions(followtestcase));
            if (result[0].equals(result[1]))
                result[2] = String.valueOf(1);
            else
                result[2] = String.valueOf(0);
        }catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    private int getPartitions(String testcasename){
        int result = 0 ;
        if (testcasename.equals("I-1aI-3a"))
            result = 0;
        else if (testcasename.equals("I-1aI-3b"))
            result = 1;
        else if (testcasename.equals("I-1aI-3c"))
            result = 2;
        else if (testcasename.equals("I-1aI-3d"))
            result = 3;
        else if (testcasename.equals("I-1bI-3a"))
            result = 4;
        else if (testcasename.equals("I-1bI-3b"))
            result = 5;
        else if (testcasename.equals("I-1bI-3c"))
            result = 6;
        else if (testcasename.equals("I-1bI-3d"))
            result = 7;
        else if (testcasename.equals("I-1cI-3a"))
            result = 8;
        else if (testcasename.equals("I-1cI-3b"))
            result = 9;
        else if (testcasename.equals("I-1cI-3c"))
            result = 10;
        else if (testcasename.equals("I-1cI-3d"))
            result = 11;
        return result;
    }

    public static void main(String[] args) {
        DynamicRandomTestingEXP drt = new DynamicRandomTestingEXP();
		drt.testonefaultsforP();
		drt.testhalftestcasesforP();
		drt.testOneFaultsforM();
        drt.testhalftestcasesforM();
    }

}
