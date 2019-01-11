package controller.util;

import java.io.File;

public class Partition {
    public static void main(String[] args) {
        int[] p0 = {4,5,7,8,15,21,23,29};
        int[] p1 = {12,17,18,19,20,22,24,30};
        int[] p2 = {0,1,2,3,6,9,25,26};
        int[] p3 = {10,11,13,14,16,27,28,31};
        String path = "C:\\Users\\daihe\\Desktop\\PhoneBillCalculation\\Partitions";
        for (int i = 0; i < 4; i++) {
            String temp = "";
            temp = path + "\\" + "p" + String.valueOf(i);
            if (i == 0){
                for (int j = 0; j < p0.length; j++) {
                    String ttemp = temp ;
                    String testcasename = ttemp + "\\" + String.valueOf(p0[j]);
                    File file = new File(testcasename);
                    if (!file.exists())
                        file.mkdir();
                }
            }else if (i == 1){
                for (int j = 0; j < p1.length; j++) {
                    String ttemp = temp ;
                    String testcasename = ttemp + "\\" + String.valueOf(p1[j]);
                    File file = new File(testcasename);
                    if (!file.exists())
                        file.mkdir();
                }
            }else if (i == 2){
                for (int j = 0; j < p2.length; j++) {
                    String ttemp = temp ;
                    String testcasename = ttemp + "\\" + String.valueOf(p2[j]);
                    File file = new File(testcasename);
                    if (!file.exists())
                        file.mkdir();
                }
            }else if (i == 3){
                for (int j = 0; j < p3.length; j++) {
                    String ttemp = temp ;
                    String testcasename = ttemp + "\\" + String.valueOf(p3[j]);
                    File file = new File(testcasename);
                    if (!file.exists())
                        file.mkdir();
                }
            }
        }
    }
}
