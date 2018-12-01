package common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class FileUtils {
    private static Logger logger = LoggerFactory.getLogger(FileUtils.class.getName());

    /**
     * 构造 writer
     *
     * @param fileName
     * @return
     */
    public static BufferedWriter buildWriter(String fileName) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(new File(fileName)));
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
            BufferedReader reader = new BufferedReader(new FileReader(new File(fileName)));
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
    public static void realeaseWriter (BufferedWriter writer) {
        try {
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 释放资源
     * @param reader
     */
    public static void realeaseWriter (BufferedReader reader) {
        try {
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
///Users/shiyuquan/Downloads/spark_emotion_anslysis
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



}