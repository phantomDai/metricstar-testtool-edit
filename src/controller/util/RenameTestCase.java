package controller.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 改文件将原始的测试用例重新命名1， 2， 。。。
 */
public class RenameTestCase {
    public static void main(String[] args) {
        String path = "C:\\Users\\Administrator\\Desktop\\object program\\PBC\\MyRandom1"; //文件的父目录
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
}
