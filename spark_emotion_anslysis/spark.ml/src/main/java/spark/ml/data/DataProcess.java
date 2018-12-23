package spark.ml.data;

import com.google.common.collect.Lists;
import com.huaban.analysis.jieba.JiebaSegmenter;
import common.constants.Constants;
import common.util.DateUtils;
import common.util.FileUtils;
import common.util.MySQLUtils;
import common.util.MyTestIgnore;
import jdk.nashorn.internal.codegen.CompilerConstants;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.log4j.BasicConfigurator;
import org.nlp.util.Tokenizer;
import org.nlp.vec.VectorModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.nio.Buffer;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import static common.object.pool.JiebaSegmenterPool.getJiebaSegmenterPool;

/**
 * 将短评数据转换成libsvm格式：
 *          flag 1:value1 2:value2 ... n:valuen
 * Created by cage
 */
public class DataProcess {
    private static Logger logger = LoggerFactory.getLogger(DataProcess.class.getName());
    private static LinkedBlockingQueue<List<String>> fileLinesContainer = new LinkedBlockingQueue<>();
    //分词线程池 15线程个数，本机逻辑CPU核数为16，使用时建议修改
    private static ExecutorService segExecutorService = Executors.newFixedThreadPool(15);
    //写文件线程池 1线程个数，本机逻辑CPU核数为16，使用时建议修改
    private static ExecutorService writerExecutorService = Executors.newFixedThreadPool(1);
    private static BufferedWriter bufferedWriter;

    //cnt
    private static AtomicInteger atomicLong = new AtomicInteger(0);

    /**
     * @param dstPath 评级与短评（并对短评进行分词）存储目录
     */
    public static void getCommonAndLevel(String dstPath) {
        MySQLUtils.storeAllMovieCommonAndLevel(dstPath);
    }

    public static void getCommonSegWithFlag(String srcPath, String dstPath) {
        bufferedWriter = FileUtils.buildWriter(dstPath, "GB18030");
        BufferedReader bufferedReader = FileUtils.buildReader(srcPath, "GB18030");
        String line;
        while ((line = FileUtils.readLine(bufferedReader)) != null) {
            //每读取1000行提交一次任务，提高效率
            int perTaskLines = 1000;
            List<String> taskLines = Lists.newArrayList();
            while (line != null && perTaskLines > 0) {
                taskLines.add(line);
                line = FileUtils.readLine(bufferedReader);
                perTaskLines --;
            }
            Future<List<String>> submit = segExecutorService.submit(new SegCallTask(taskLines));
            writerExecutorService.submit(new WriterSegsCall(bufferedWriter, submit));
        }

        segExecutorService.shutdown();
        writerExecutorService.shutdown();
        if (segExecutorService.isTerminated() && writerExecutorService.isTerminated()) {
            FileUtils.releaseWriter(bufferedWriter);
            FileUtils.releaseReader(bufferedReader);
        }

    }

    /**
     * 分词线程
     */
    public static class SegCallTask implements Callable<List<String>> {
        private List<String> sentences;
        public SegCallTask(List<String> sentences) {
            this.sentences = sentences;
        }
        @Override
        public List<String> call() {
            List<String> resList = Lists.newArrayList();
            try {
                JiebaSegmenter jiebaSegmenter = getJiebaSegmenterPool().borrowObject();
                for (String sentence : sentences) {
                    if (atomicLong.getAndIncrement()*1000 % 10000 == 0) {
                        logger.info("processed lines:{}, {}%", atomicLong.get(), (float) atomicLong.get() / 1120000);
                    }
                    try {
                        int level = Constants.COMMENT_LEVEL_MAP.get(sentence.split("\t")[0]);
                        String common = sentence.split("\t")[1];
                        List<String> words = jiebaSegmenter.sentenceProcess(common);
                        String result = Tokenizer.filterStopWordAndSwap(words);
                        resList.add(level + "\t" + result);
                    } catch (ArrayIndexOutOfBoundsException ex) {
                        logger.error("error：{}", sentence);
                    }

                }
                getJiebaSegmenterPool().returnObject(jiebaSegmenter);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return resList;
        }
    }

    /**
     * 将分完次后的内容写入文件
     */
    private static class WriterSegsCall implements Runnable {
        private BufferedWriter bufferedWriter;
        private Future<List<String>> segSentences;

        public WriterSegsCall(BufferedWriter bufferedWriter, Future<List<String>> segSentences) {
            this.bufferedWriter = bufferedWriter;
            this.segSentences = segSentences;
        }

        @Override
        public void run() {
            try {
                List<String> segs = segSentences.get(10, TimeUnit.SECONDS);
                segs.stream().forEach(ele -> FileUtils.write(ele, bufferedWriter));
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 得到短评的词向量,写成libsvm格式
     * @param srcPath
     */
    public static void getWordVec(String srcPath, String dstPath) {
        BufferedReader reader = FileUtils.buildReader(srcPath, "GB18030");
        BufferedWriter writer = FileUtils.buildWriter(dstPath, "GB18030");
//        VectorModel vectorModel = VectorModel.loadFromFile("F:\\taobao-code\\nlp\\segs\\seg_result_merge-2018_12_16_17_06_59.model");
        VectorModel vectorModel = VectorModel.loadFromFile("F:\\taobao-code\\nlp\\segs\\seg_result_merge-with-cilin-2018_12_16_18_59_26.model");
        logger.info("vector size:{}", vectorModel.getVectorSize());
        String line;
        int linesCnt = 0;
        while ((line = FileUtils.readLine(reader)) != null) {
            String[] split = line.split("\t");
            if (split.length <= 1) { //短评无具体内容
                continue;
            }
            if (linesCnt ++ % 10000 == 0) {
                logger.info("processed lines:{}, {}%", linesCnt, (float) linesCnt / 1120000 * 100);
            }
            String level = split[0];
            if (StringUtils.isEmpty(level)) {
                continue;
            }
            List<String> words = Arrays.asList(split[1].split(" "));
            if (words.size() < 80) {
                continue;
            }
            float[] commonVec = new float[vectorModel.getVectorSize()];
            for (int i = 0; i < commonVec.length; i++) {
                commonVec[i] = 0.0f;
            }
            for (String word : words) {
                float[] wordVector = vectorModel.getWordVector(word);
                vectorModel.calSum(commonVec, wordVector);
            }

            String[] libsvm = new String[vectorModel.getVectorSize()];
            for (int i = 0; i < libsvm.length; i++) {
                libsvm[i] = (i+1) + ":" + commonVec[i];
            }
            if (libsvm.length != vectorModel.getVectorSize()) {
                continue;
            }
            String join = level + " " + String.join(" ", libsvm);
            FileUtils.write(join, writer);
        }
    }

    /**
     * 检查数据格式是否有误，错误的将被剔除
     * @param srcData
     * @param dstPath
     */
    public static void checkAndRemoveValidData(String srcData, String dstPath) {
        BufferedReader reader = FileUtils.buildReader(srcData, "GB18030");
        BufferedWriter writer = FileUtils.buildWriter(dstPath, "GB18030");
        String line;
        while ((line = FileUtils.readLine(reader)) != null) {
            try {
                String label = line.split(" ")[0];
                String indexAndValue = line.substring(1);
                if (!StringUtils.isEmpty(label) && indexAndValue.trim().split(" ").length == 200) {
                    FileUtils.write(line, writer);
                }
            } catch (ArrayIndexOutOfBoundsException ex) {
                logger.error("error line:{}", line);
            }
        }

        FileUtils.releaseReader(reader);
        FileUtils.releaseWriter(writer);
        logger.info("over...");
    }

    @MyTestIgnore
    public static void main(String[] args) {
        BasicConfigurator.configure();
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        logger.info("task started............");
        String path = "F:\\taobao-code\\nlp\\segs\\level_and_common_%s.txt";
        path = String.format(path, DateUtils.dateToString(new Date(), DateUtils.FILE_PATTERN));
        logger.info("path: {}", path);
//        getCommonAndLevel(path);
        path = "F:\\taobao-code\\nlp\\segs\\level_and_common_2018_12_22_12_41_09.txt";
        String segDstPath = "F:\\taobao-code\\nlp\\segs\\level_and_common_seg_%s.txt";
        segDstPath = String.format(segDstPath, DateUtils.dateToString(new Date(), DateUtils.FILE_PATTERN));
        logger.info("segDstPath :{}", segDstPath);
//        getCommonSegWithFlag(path, segDstPath);
        segDstPath = "F:\\taobao-code\\nlp\\segs\\level_and_common_seg_2018_12_22_14_39_29.txt";
        String libsvmDstPath = "F:\\taobao-code\\nlp\\segs\\level_and_common_seg_libsvm_%s.txt";
        libsvmDstPath = String.format(libsvmDstPath, DateUtils.dateToString(new Date(), DateUtils.FILE_PATTERN));
        logger.info("libsvmDstPath: {}", libsvmDstPath);
        getWordVec(segDstPath, libsvmDstPath);

//        libsvmDstPath = "F:\\taobao-code\\nlp\\segs\\level_and_common_seg_libsvm_2018_12_22_16_42_35.txt";
        String checkedLibsvmDstPath = "F:\\taobao-code\\nlp\\segs\\level_and_common_seg_libsvm_checked_%s.txt";
        checkedLibsvmDstPath = String.format(checkedLibsvmDstPath, DateUtils.dateToString(new Date(), DateUtils.FILE_PATTERN));
        checkAndRemoveValidData(libsvmDstPath, checkedLibsvmDstPath);
        logger.info("cost: {}ms", stopWatch.getTime());

//        String line = "4 1:-0.6848054 2:-0.7467057 3:0.2690989 4:0.27948707 5:0.36069405 6:-0.25360662 7:0.04999393 8:1.2305802 9:1.2044864 10:0.90460724 11:0.25998175 12:0.44372332 13:0.14075464 14:-0.5753156 15:-0.87711716 16:1.0858371 17:0.6962587 18:0.42749196 19:0.12560856 20:-1.3510524 21:0.07328414 22:0.07614841 23:0.6276847 24:0.23124214 25:0.119265184 26:0.912487 27:0.1432106 28:0.14915885 29:1.9182396 30:-0.45337275 31:0.25729257 32:-1.8723311 33:0.6455455 34:-0.14114486 35:0.030159827 36:0.3259678 37:-0.39420357 38:0.50960994 39:0.07822651 40:-0.39839286 41:-0.8088233 42:0.15494244 43:-0.2494722 44:-1.1090463 45:-0.2627193 46:-0.35868353 47:-0.3379104 48:0.09552256 49:-0.09702124 50:-0.51851946 51:-1.3380647 52:1.2359613 53:-1.3536954 54:-1.9007655 55:1.4181995 56:0.45237046 57:0.13772917 58:-0.9812881 59:0.8383371 60:0.047976665 61:0.24107887 62:-0.40580434 63:-0.24647634 64:-0.08043004 65:-0.70486164 66:-1.5529091 67:0.40948498 68:0.41855288 69:0.12922713 70:-0.33212036 71:-0.3866098 72:1.2044332 73:0.1505807 74:0.04738693 75:1.0071206 76:1.1903185 77:0.2121459 78:1.0204321 79:-0.043475024 80:-0.7536425 81:0.787584 82:-0.25654015 83:0.3948642 84:-0.5381961 85:1.9034995 86:0.32103875 87:0.3508474 88:-0.53232723 89:0.014643107 90:0.3269611 91:0.60406387 92:-0.28122893 93:0.7872144 94:-0.9359497 95:0.8536472 96:-1.0377562 97:-0.09788546 98:0.93133104 99:0.743716 100:0.009293713 101:-0.2543017 102:0.917134 103:0.06782543 104:0.35158437 105:-0.8959695 106:0.41035834 107:0.20233074 108:-0.75165194 109:-0.015383424 110:-1.5443926 111:-0.33781126 112:-0.16824378 113:-0.16704893 114:-0.8374699 115:-0.021899734 116:-0.16280448 117:1.4214357 118:2.138772 119:1.5811344 120:0.33763468 121:0.6957136 122:1.5493299 123:1.091232 124:-0.49605536 125:-0.911527 126:0.33488536 127:-0.7274323 128:-0.5404353 129:0.01858541 130:-0.14585602 131:1.9748191 132:-0.7826836 133:-0.18290636 134:-0.45667723 135:1.8099777 136:0.051608674 137:0.009923986 138:-0.3640102 139:-0.39051896 140:-0.17930114 141:0.011238356 142:0.60519266 143:1.0244707 144:0.24366942 145:1.2256178 146:-0.32751322 147:1.5793605 148:-1.2158661 149:-0.74450266 150:0.18080892 151:-1.2920607 152:-0.8386208 153:0.04500699 154:0.74308515 155:-0.020314176 156:-1.5614382 157:-1.6500673 158:-0.5534983 159:-0.44772145 160:-0.35457465 161:0.24817556 162:0.06554167 163:0.20900322 164:-0.46149027 165:-0.40213 166:0.3083824 167:0.82777274 168:-0.25639775 169:-0.3695048 170:-0.84381366 171:1.4118884 172:-0.5821534 173:-1.0199515 174:0.35343966 175:-1.0457779 176:-0.30281958 177:-0.8237017 178:0.907713 179:1.794431 180:0.0038435962 181:-0.62716264 182:-0.63317907 183:1.3126459 184:0.9260581 185:-0.65443134 186:-1.3679678 187:-0.67038196 188:-0.42860082 189:-0.69826967 190:-0.24923234 191:0.38062102 192:-0.9680728 193:-0.52722806 194:-0.67157596 195:-0.22592722 196:-0.81026995 197:0.89848423 198:-0.6097022 199:1.4482048 200:0.6999524";
//        String label = line.split(" ")[0];
//        System.out.println(label);
//        String indexAndValue = line.substring(1);
//        System.out.println(indexAndValue);
//        System.out.println(indexAndValue.trim().split(" ").length);
//        if (!StringUtils.isEmpty(label) && indexAndValue.trim().split(" ").length == 199) {
//            System.out.println("ok");
//        }
    }

}
