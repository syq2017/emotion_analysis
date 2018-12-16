package corpus.train;

import common.util.MyTestIgnore;
import org.apache.commons.lang3.time.StopWatch;
import org.nlp.util.Tokenizer;
import org.nlp.vec.VectorModel;
import org.nlp.vec.Word2Vec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.Set;

/**
 * Created by cage
 */
public class TrainWord2Vec {

    private static Logger logger = LoggerFactory.getLogger(TrainWord2Vec.class.getName());

    public static void readByJava(String textFilePath, String modelFilePath, int threadNum, Word2Vec.Method method){
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        Word2Vec wv = new Word2Vec.Factory()
                .setMethod(method)
                .setNumOfThread(threadNum).build();

        try (BufferedReader br = new BufferedReader(new FileReader(textFilePath))) {
            int linesCount = 0;
            String line;
            while ((line = br.readLine()) != null) {
                line = Tokenizer.filterStopWordAndSwap(line);
                wv.readTokens(new Tokenizer(line, " "));
                linesCount ++;
                if (linesCount % 10000 == 0) {
                    logger.info("read lines:{}", linesCount);
                }
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        wv.training();
        wv.saveModel(new File(modelFilePath));
        logger.info("training is over.cost:{}ms", stopWatch.getTime());
    }


    /**
     * 测试
     * @param modelFilePath
     * @param word
     */
    public static void testVector(String modelFilePath, String word){
        VectorModel vm = VectorModel.loadFromFile(modelFilePath);
        Set<VectorModel.WordScore> result1 = Collections.emptySet();
        result1 = vm.similar(word);
        for (VectorModel.WordScore we : result1){
            System.out.println(we.name + " :\t" + we.score);
        }
    }

    public static VectorModel getWord2VecModel(String modelFilePath) {
        return VectorModel.loadFromFile(modelFilePath);
    }

    public static float[] getWordVector(VectorModel vm, String word) {
        return vm.getWordVector(word);
    }

    public static float[] getWordVector(String modelFilePath, String word) {
        VectorModel vm = VectorModel.loadFromFile(modelFilePath);
        return vm.getWordVector(word);
    }

    /**
     * 训练词向量
     * @param textFilePath
     * @param modelFilePath
     * @param threadNum
     * @param method
     */
    public static void train(String textFilePath, String modelFilePath, int threadNum, Word2Vec.Method method){
        readByJava(textFilePath, modelFilePath, threadNum, method);
    }


    @MyTestIgnore
    public static void main(String[] args) {
        VectorModel word2VecModel = getWord2VecModel("F:\\taobao-code\\nlp\\segs\\seg_result_merge-2018_12_15_23_32_52.model");
        float[] vec = getWordVector(word2VecModel, "中国");
        for (float ff : vec) {
            System.out.print(ff);
        }
        System.out.println("-----------------------------------");
        float[] vec1= getWordVector(word2VecModel, "美好");
        for (float ff : vec1) {
            System.out.print(ff);
        }
    }
}
