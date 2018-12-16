package common.util;

import javafx.scene.paint.Stop;
import org.apache.commons.lang3.time.StopWatch;
import org.mozilla.intl.chardet.nsDetector;
import org.mozilla.intl.chardet.nsICharsetDetectionObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.util.parsing.combinator.testing.Str;

import javax.annotation.concurrent.NotThreadSafe;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.WeakHashMap;

@NotThreadSafe
public class FileUtils {
    private static Logger logger = LoggerFactory.getLogger(FileUtils.class.getName());
    public static final String ENCODE_GB18030 = "GB18030";
    public static final String ENCODE_UTF8 = "UTF-8";
    private static boolean found = false;
    private static String encoding = null;

    /**
     * 构造 writer
     * @param fileName
     * @return
     */
    public static BufferedWriter buildWriter(String fileName) {
        try {
            BufferedWriter writer =  new BufferedWriter(
                    new OutputStreamWriter(new FileOutputStream(fileName, true), ENCODE_UTF8));
            return writer;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static BufferedWriter buildWriter(String fileName, String encode) {
        try {
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(new FileOutputStream(fileName, true), encode));
            return writer;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 构造 reader
     * @param fileName
     * @return
     */
    public static BufferedReader buildReader(String fileName) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    new FileInputStream(fileName), ENCODE_UTF8));
            return reader;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static BufferedReader buildReader(String fileName, String encode) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    new FileInputStream(fileName), encode));
            return reader;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 释放资源
     * @param writer
     */
    public static void releaseWriter (BufferedWriter writer) {
        try {
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 释放资源
     * @param reader
     */
    public static void releaseReader (BufferedReader reader) {
        try {
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 将content 写入 targetFile
     *
     * @param content
     */
    public static void write(String content, BufferedWriter writer) {
        try {
            writer.write(content + "\r");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 读取文件中的一行，需要自行判断是否为空（文件为空或者已读完）
     * @param reader
     * @return
     */
    public static String readLine(BufferedReader reader) {
        try {
            return reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 传入一个文件(File)对象，检查文件编码
     *
     * @param file
     *            File对象实例
     * @return 文件编码，若无，则返回null
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static String guestFileEncoding(File file) throws FileNotFoundException,
            IOException {
        return guestFileEncoding(file, new nsDetector());
    }

    /**
     * 获取文件的编码
     * @param file  File对象实例
     * @param languageHint
     *            语言提示区域代码 eg：1 : Japanese; 2 : Chinese; 3 : Simplified Chinese;
     *            4 : Traditional Chinese; 5 : Korean; 6 : Dont know (default)
     * @return 文件编码，eg：UTF-8,GBK,GB2312形式，若无，则返回null
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static String guestFileEncoding(File file, int languageHint)
            throws FileNotFoundException, IOException {
        return guestFileEncoding(file, new nsDetector(languageHint));
    }

    /**
     * 获取文件的编码
     *
     * @param path
     *            文件路径
     * @return 文件编码，eg：UTF-8,GBK,GB2312形式，若无，则返回null
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static String guestFileEncoding(String path) throws FileNotFoundException,
            IOException {
        return guestFileEncoding(new File(path));
    }

    /**
     * 获取文件的编码
     *
     * @param path
     *            文件路径
     * @param languageHint
     *            语言提示区域代码 eg：1 : Japanese; 2 : Chinese; 3 : Simplified Chinese;
     *            4 : Traditional Chinese; 5 : Korean; 6 : Dont know (default)
     * @return
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static String guestFileEncoding(String path, int languageHint)
            throws FileNotFoundException, IOException {
        return guestFileEncoding(new File(path), languageHint);
    }

    /**
     * 获取文件的编码
     * @param file
     * @param det
     * @return
     * @throws FileNotFoundException
     * @throws IOException
     */
    private static String guestFileEncoding(File file, nsDetector det)
            throws FileNotFoundException, IOException {
        det.Init(new nsICharsetDetectionObserver() {
            public void Notify(String charset) {
                found = true;
                encoding = charset;
            }
        });

        BufferedInputStream imp = new BufferedInputStream(new FileInputStream(file));
        byte[] buf = new byte[1024];
        int len;
        boolean done = false;
        boolean isAscii = true;
        int tryTimes = 3;
        while ((len = imp.read(buf, 0, buf.length)) != -1) {
            if (isAscii) {
                isAscii = det.isAscii(buf, len);
            }
            if (!isAscii && !done) {
                done = det.DoIt(buf, len, false);
            }
            if (tryTimes-- < 0) {
                break;
            }
        }
        det.DataEnd();
        if (isAscii) {
            encoding = "ASCII";
            found = true;
        }

        if (!found) {
            String prob[] = det.getProbableCharsets();
            if (prob.length > 0) {
                // 在没有发现情况下，则取第一个可能的编码
                encoding = prob[0];
            } else {
                return null;
            }
        }
        return encoding;
    }

    /**
     * 合并文件
     * @param paths 多个待合并文件的路径
     * @param dstPath 合并结果路径
     */
    public static void mergeFile(List<String> paths, String dstPath) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        int linesCnt = 0;
        BufferedWriter writer = buildWriter(dstPath, ENCODE_GB18030);
        for (String path : paths) {
            BufferedReader reader = buildReader(path,ENCODE_GB18030);
            String line;
            while ((line = readLine(reader)) != null) {
                write(line, writer);
                linesCnt ++;
                if (linesCnt % 100000 == 0) {
                    logger.info("writed: {} lines", linesCnt);
                }
            }
            releaseReader(reader);
        }
        releaseWriter(writer);
        long time = stopWatch.getTime();
        logger.info("writed: {} lines, cost:{} ms", linesCnt, time);
    }

    /**
     * 文件拷贝
     * @param rawFile
     * @param dstFile
     */
    public static void fileCopy(String rawFile, String dstFile) {
        logger.info("file copy from {} to {} .....", rawFile, dstFile);
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        try {
            org.apache.commons.io.FileUtils.copyFile(new File(rawFile), new File(dstFile));
        } catch (IOException e) {
            e.printStackTrace();
        }
        logger.info("file copy done, cost: {}ms", stopWatch.getTime());
    }

    @MyTestIgnore
    public static void main(String[] args) {
        String rawFilePath = "F:\\taobao-code\\nlp\\全网新闻数据(SogouCA)\\2012年6月—7月news_tensite_xml.full\\news_tensite_xml.dat";
        try {
            String encoding = guestFileEncoding(rawFilePath);
            System.out.println(encoding);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
