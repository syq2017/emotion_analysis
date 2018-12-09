package step1.corpus.process;

import com.huaban.analysis.jieba.JiebaSegmenter;
import com.huaban.analysis.jieba.SegToken;
import common.util.JiebaSegmenterFactory;
import common.util.StringBuilderFactory;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by cage
 */
public class CorpusSegUtils {

    private static Logger logger = LoggerFactory.getLogger(CorpusSegUtils.class.getName());

    private static final String contenttitleStart = "<contenttitle>";
    private static final String contenttitleEnd = "</contenttitle>";
    private static final String contentStart = "<content>";
    private static final String contentEnd = "</content>";
    private static String segsFilePath1 = "F:\\taobao-code\\nlp\\segs\\segs-1.txt";
    private static String segsFilePath2 = "F:\\taobao-code\\nlp\\segs\\segs-2.txt";
    private static final String encode = "GB18030";
    private static AtomicLong segsLinesCount = new AtomicLong(0L);
    private static AtomicLong writedLinesCount = new AtomicLong(0L);

    private static LinkedBlockingQueue<String> fileLinesContainer = new LinkedBlockingQueue<>();
    private static LinkedBlockingQueue<String> segContainer = new LinkedBlockingQueue<>();
    private static BufferedWriter bufferedWriter1;
    private static BufferedWriter bufferedWriter2;
    public static final Set<String>  stopWords = new HashSet<>();
    static {
        String filePath = "F:\\taobao-code\\nlp\\stop-words.txt";
        try (BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(new FileInputStream(filePath), encode))) {
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                stopWords.add(line.trim());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    static {
        try {
            bufferedWriter1 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(segsFilePath1, true), encode));
            bufferedWriter2 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(segsFilePath2, true), encode));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    //JiebaSegmenter 对象池
    public static ObjectPool<JiebaSegmenter> jiebaSegmenterPool =
            new GenericObjectPool<>(new JiebaSegmenterFactory());

    //StringBuilder对象池
    public static ObjectPool<StringBuilder> stringBuilderPool =
            new GenericObjectPool<>(new StringBuilderFactory());


    //分词线程池 12最大线程个数，本机逻辑CPU核数为16，使用时建议修改
    private static ExecutorService segExecutorService = Executors.newFixedThreadPool(12);
    // 将分词结果写入文件线程池
    private static ExecutorService writerExecutorService = Executors.newFixedThreadPool(2);



    /**
     *抽取样例数据观察
     * @param rawFilePath  原始数据文件路径
     * @param sampleDataPath 样例数据文件存储路径
     * @param sampleLinesCnt 抽取样例数据行数
     */
    private static void getSampleData(String rawFilePath,String sampleDataPath,int sampleLinesCnt) throws Exception {
//        BufferedReader bufferedReader = new BufferedReader(new FileReader(new File(rawFilePath)));
        BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(new FileInputStream(rawFilePath), encode));
        BufferedWriter bufferedWriter = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(sampleDataPath), encode));
        String line = null;
        for(int i=0;i<sampleLinesCnt;i++){
            line = bufferedReader.readLine();
            line += new Date().toString();
            if(line != null){
                bufferedWriter.write(line+"\r\n");
            }
        }
        bufferedWriter.flush();
        bufferedWriter.close();
        bufferedReader.close();
    }

    /**
     * 对语料进行分词,采用线程池与对象池实现
     * @param fileParentPaths 带分词文件路径数组
     *      <contenttitle>页面标题</contenttitle>
     *      <content>页面内容</content>
     * @return 一共处理的文件行数
     */
    private static int corpusSegProcess(List<String> fileParentPaths) throws IOException {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        for (String path : fileParentPaths) {
            File parentPath = new File(path);
            if (parentPath.isDirectory()) {
                File[] corpusFiles = parentPath.listFiles();
                StopWatch singleWatch = new StopWatch();
                singleWatch.start();
                for (File corpus : corpusFiles) {
                    logger.info("filename:{}", corpus.getAbsolutePath());
                    try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(
                            new FileInputStream(corpus), encode))) {
                        String lineContent = null;
                        while ((lineContent = bufferedReader.readLine()) != null) {
                            if (lineContent.contains(contenttitleStart) && lineContent.contains(contenttitleEnd)) {
                                lineContent = lineContent.substring(14, lineContent.length() - 15);
                                fileLinesContainer.put(lineContent);
                            } else if (lineContent.contains(contentStart) && lineContent.contains(contentEnd)) {
                                lineContent = lineContent.substring(9, lineContent.length() - 10);
                                fileLinesContainer.put(lineContent);
                            }
                        }
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                singleWatch.stop();
                long singleFileTime = singleWatch.getTime();
                logger.info("seg file:{}, cost:{}ms", parentPath, singleFileTime);
            }
        }
        long time = stopWatch.getTime();
        stopWatch.stop();
        logger.info("cost:{}ms", time);
        logger.info("seg lines:{},writed lines:{}", segsLinesCount.get(), writedLinesCount.get());
        return 0;
    }

    /**
     * 任务执行线程： 分词
     */
    private static class LineProcessCall implements Callable<String> {
        public LineProcessCall() { }

        @Override
        public String call() throws Exception {
            while (true) {
                JiebaSegmenter jiebaSegmenter = jiebaSegmenterPool.borrowObject();
                StringBuilder stringBuilder = stringBuilderPool.borrowObject();
                String newsContent = fileLinesContainer.take();
                List<SegToken> segsTitle = jiebaSegmenter.process(newsContent, JiebaSegmenter.SegMode.SEARCH);
                segsTitle.removeIf(seg -> stopWords.contains(seg.word));
                segsTitle.stream().forEach(ele -> stringBuilder.append(ele.word + " "));
                String result = stringBuilder.toString() + "\r\n";
                jiebaSegmenterPool.returnObject(jiebaSegmenter);
                stringBuilderPool.returnObject(stringBuilder);
                if (segsLinesCount.incrementAndGet() % 10000 == 0) {
                    logger.info("segs:{}", segsLinesCount.get());
                }
                segContainer.put(result);
            }
        }
    }

    /**
     * 任务执行线程： 将分完次后的内容写入文件
     */
    private static class WriterSegsCall implements Runnable {
        private BufferedWriter bufferedWriter;
        public WriterSegsCall(BufferedWriter bufferedWriter) {
            this.bufferedWriter = bufferedWriter;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    String segResult = segContainer.take();
                    if (writedLinesCount.incrementAndGet() % 10000 == 0) {
                        logger.info("writed lines:{}", writedLinesCount.get());
                        bufferedWriter.flush();
                    }
                    bufferedWriter.write(segResult);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 对搜狗语料进行分词处理
     * @param datas
     */
    public static void segSougouNews(List<String> datas) {
        //消费者
        WriterSegsCall call1 = new WriterSegsCall(bufferedWriter1);
        WriterSegsCall call2 = new WriterSegsCall(bufferedWriter2);
        writerExecutorService.submit(call1);
        writerExecutorService.submit(call2);
        for (int i=0; i<12; i++) {
            LineProcessCall call = new LineProcessCall();
            segExecutorService.submit(call);
        }
        //生产者
        try {
            corpusSegProcess(datas);
        } catch (IOException e) {
            e.printStackTrace();
        }
        segExecutorService.shutdown();
        writerExecutorService.shutdown();
        while (true) {
            if (segExecutorService.isTerminated() && writerExecutorService.isTerminated()) {
                logger.info("all task over....");
                break;
            }
        }
    }
}
