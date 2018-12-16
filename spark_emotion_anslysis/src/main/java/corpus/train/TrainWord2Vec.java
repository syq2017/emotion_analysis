package corpus.train;

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
        Word2Vec wv = new Word2Vec.Factory()
                .setMethod(method)
                .setNumOfThread(threadNum).build();

        try (BufferedReader br =
                     new BufferedReader(new FileReader(textFilePath))) {
            int linesCount = 0;
            String line;
            while ((line = br.readLine()) != null) {
                line = Tokenizer.filterStopAndSwap(line);
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
    }

    public static void testVector(String modelFilePath){
        VectorModel vm = VectorModel.loadFromFile(modelFilePath);
        Set<VectorModel.WordScore> result1 = Collections.emptySet();
        result1 = vm.similar("亲");
        for (VectorModel.WordScore we : result1){
            System.out.println(we.name + " :\t" + we.score);
        }
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
        testVector(modelFilePath);
    }
}
