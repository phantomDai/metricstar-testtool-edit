package controller.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public class TestFrameAndMr {
    private Map<String,List<String>> testframeTomr = new HashMap<>();

    public Map<String, List<String>> getTestframeTomr() {
        return testframeTomr;
    }

    public void setTestframeTomr(Map<String, List<String>> testframeTomr) {
        this.testframeTomr = testframeTomr;
    }
    public void writeTestframeAndmr(){
        String path = "C:\\Users\\daihe\\Desktop\\PhoneBillCalculation\\testframeAndmr\\testframeAndmr";
        try {
            File file = new File(path);
            if (!file.exists())
                file.createNewFile();
            PrintWriter pw = new PrintWriter(new FileWriter(path));
            Set<String> keyset = testframeTomr.keySet();
            Iterator<String> it = keyset.iterator();
            while(it.hasNext()){
                String testcasename = it.next();
                List<String> temp = new ArrayList<>();
                temp.clear();
                temp = testframeTomr.get(testcasename);
                String str = testcasename + ":";
                for (int i = 0; i < temp.size(); i++) {
                    str = str + temp.get(i) + ",";
                }
                pw.print(str + "\n");
            }
            pw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        TestFrameAndMr tf = new TestFrameAndMr();
        System.out.println(tf.toString());
    }
}
