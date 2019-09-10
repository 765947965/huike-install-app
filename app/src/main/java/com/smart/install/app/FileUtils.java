package com.smart.install.app;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @ProjectName: huike-iot-andriod
 * @Package: com.smart.gc.util
 * @ClassName: FileUtils
 * @Description: java类作用描述
 * @Author: 谢文良
 * @CreateDate: 2019/6/21 14:51
 * @UpdateUser: 更新者
 * @UpdateDate: 2019/6/21 14:51
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class FileUtils {
    public static void downFile(InputStream inputString, File file) {
        if (file.exists()) {
            file.delete();
        }
        try {
            FileOutputStream outputStream = new FileOutputStream(file);
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream);
            BufferedInputStream bufferedInputStream = new BufferedInputStream(inputString);
            byte[] b = new byte[4 * 1024];
            int len;
            while ((len = bufferedInputStream.read(b)) != -1) {
                bufferedOutputStream.write(b, 0, len);
                bufferedOutputStream.flush();
            }
            bufferedOutputStream.flush();
            bufferedOutputStream.close();
            outputStream.close();
            bufferedInputStream.close();
            inputString.close();
        } catch (FileNotFoundException e) {
            file.delete();
        } catch (IOException e) {
            file.delete();
        }
    }

    /**
     * 删除文件或文件夹下的所有文件
     *
     * @param file 文件或文件夹
     */
    public static void DeleteFile(File file) {
        if (file.exists()) {
            if (file.isDirectory()) {
                File[] childFile = file.listFiles();
                if (childFile != null) {
                    for (File f : childFile) {
                        DeleteFile(f);
                    }
                }
            }
            file.delete();
        }
    }
}
