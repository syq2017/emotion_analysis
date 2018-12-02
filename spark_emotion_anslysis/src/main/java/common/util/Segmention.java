package common.util;

import com.huaban.analysis.jieba.JiebaSegmenter;
import com.huaban.analysis.jieba.SegToken;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * jieba分词
 */
public class Segmention {
    private static ExecutorService segmentTask;

    /**
     * 分词(单线程分词)
     */
    public static void segmention(String filePath, String dstFile) throws IOException {
        long startTime = System.currentTimeMillis() / 1000;
        JiebaSegmenter segmenter = new JiebaSegmenter();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath),"GB18030"));
        BufferedWriter bufferedWriter =  new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dstFile,true),"GB18030"));
        String line = null;
        int tmp = 0;
        while ((line = bufferedReader.readLine()) != null) {
            if (tmp++ % 100000 == 0){
                System.out.println("lines:" + tmp);
            }
            List<SegToken> words = segmenter.process(line, JiebaSegmenter.SegMode.SEARCH);
            StringBuilder stringBuilder = new StringBuilder(50);
            for(SegToken sg : words){
                stringBuilder.append(sg.word + " ");
            }
            bufferedWriter.write(stringBuilder.toString().trim() + "\r\n");
        }
        bufferedWriter.flush();
        bufferedReader.close();
        bufferedWriter.close();
        long endTime = System.currentTimeMillis() / 1000;
        System.out.println("total time : " + (endTime - startTime) + " s");
    }

    /**
     * Step2 : 分词(多线程分词)
     * @param filePaths  待分词文件的父目录
     * @param dstPath  分词后的文件目录
     */
    public static void segmentionMultiThread(String filePaths,String dstPath) throws IOException {
        long startTime = System.currentTimeMillis() / 1000;
        File files = new File(filePaths);
        if(files.isDirectory()){
            File[] ffs = files.listFiles();
            segmentTask = Executors.newFixedThreadPool(OSUtils.getCpuCores());
            for(int i = 0; i < files.length(); i ++) {
                SegThread segThread = new SegThread();
                segThread.filePath = ffs[i];
                segThread.dstPath = dstPath + i + ".txt";
                segmentTask.submit(segThread);
            }
        }
        long endTime = System.currentTimeMillis() / 1000;
        System.out.println("total time : " + (endTime - startTime) + " s");
    }


    private static class SegThread implements Runnable{
        public File filePath;
        public String dstPath ;
        @Override
        public void run() {
            JiebaSegmenter segmenter = new JiebaSegmenter();
            BufferedReader bufferedReader = null;
            try {
                bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath),"GB18030"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            BufferedWriter bufferedWriter = null;
            try {
                bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dstPath,true),"GB18030"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            String line = null;
            try {
                line = bufferedReader.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
            int tmp = 0;
            while (line != null) {
                if (tmp++ % 100000 == 0){
                    System.out.println("lines:" + tmp);
                }
                List<SegToken> words = segmenter.process(line, JiebaSegmenter.SegMode.SEARCH);
                StringBuilder stringBuilder = new StringBuilder(50);
                for(SegToken sg : words){
                    stringBuilder.append(sg.word + " ");
                }
                try {
                    bufferedWriter.write(stringBuilder.toString().trim() + "\r\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    line = bufferedReader.readLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try {
                bufferedWriter.flush();
                bufferedReader.close();
                bufferedWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws IOException {
//        String filePath = "F:\\learn\\dataset\\multi-files";
//        String dstFile = "F:\\learn\\dataset\\multi-segs\\";
//        segmentionMultiThread(filePath,dstFile);

        String filePath = "F:\\learn\\dataset\\dstFilePath\\0.txt";
        String dstFile = "F:\\learn\\dataset\\dstFilePath\\1.txt";
        segmention(filePath,dstFile);
    }
}
