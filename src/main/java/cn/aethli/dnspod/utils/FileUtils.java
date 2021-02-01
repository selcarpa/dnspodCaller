package cn.aethli.dnspod.utils;

import java.io.*;

public class FileUtils {

  public static String readFile(File file) throws IOException {
    try (InputStream inputStream = new FileInputStream(file);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
      byte[] buffer = new byte[1024];
      int f;
      while ((f = inputStream.read(buffer)) != -1) {
        byteArrayOutputStream.write(buffer, 0, f);
      }
      byteArrayOutputStream.flush();
      return byteArrayOutputStream.toString();
    }
  }
}
