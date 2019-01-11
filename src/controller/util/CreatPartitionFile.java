package controller.util;

import java.io.File;
import java.io.IOException;

/**
 * 该类在指定的位置下生成partition文件
 */
public class CreatPartitionFile {
	
	public static void main(String[] args) {
		String path = "C:\\Users\\daihe\\Desktop\\EXP\\partitions";

		for (int i = 0; i < 12; i++) {
			String filepath = path + "\\p"+ String.valueOf(i);
			File file = new File(filepath);

				if (!file.exists())
					file.mkdir();

		}

	}
}
