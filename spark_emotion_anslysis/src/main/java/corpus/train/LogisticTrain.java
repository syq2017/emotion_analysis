package corpus.train;

import org.apache.spark.ml.classification.LogisticRegression;
import org.apache.spark.ml.classification.LogisticRegressionModel;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.nlp.vec.VectorModel;
import org.nlp.vec.Word2Vec;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Iterator;

/**
 * Created by cage on 2018-12-16
 */
public class LogisticTrain {

    /**
     * step1 得到带训练样本的词向量
     *
     * @throws IOException
     */
    public static void getAllSampleVec(String modelPath) throws IOException {
        VectorModel vm = VectorModel.loadFromFile(modelPath);
        BufferedWriter bw = new BufferedWriter(new FileWriter(
                new File("D:\\software\\learning_work\\eclipse\\wp\\word2vec_cage\\src\\emotion\\parse\\train.txt")));
        BufferedReader br = new BufferedReader(new FileReader(
                new File("D:\\software\\learning_work\\eclipse\\wp\\word2vec_cage\\src\\emotion\\parse\\pos.txt")));
        String line = null;
        while ((line = br.readLine()) != null) {
            float[] senVector = vm.getSentenceVector(vm, line);
            String newLine = "1 ";
            for (int i = 0; i < senVector.length; i++) {
                newLine += (i + 1) + ":" + senVector[i] + " ";
            }
            bw.write(newLine.trim() + "\r");
        }
        br.close();
        br = new BufferedReader(new FileReader(
                new File("D:\\software\\learning_work\\eclipse\\wp\\word2vec_cage\\src\\emotion\\parse\\neg.txt")));
        String line1 = null;
        while ((line1 = br.readLine()) != null) {
            float[] senVector = vm.getSentenceVector(vm, line1);
            String newLine = "0 ";
            for (int i = 0; i < senVector.length; i++) {
                newLine += (i + 1) + ":" + senVector[i] + " ";
            }
            bw.write(newLine.trim() + "\r");
        }
        br.close();
        bw.flush();
        bw.close();
    }

    /**
     * step2 训练逻辑回归
     * @throws IOException
     */
    public static void trainLG() throws IOException {

        // 使用本地所有可用线程local[*]
        SparkSession spark = SparkSession.builder().master("local[*]").appName("LG").getOrCreate();
        Dataset<Row> training = spark.read().format("libsvm")
                .load("D:\\software\\learning_work\\eclipse\\wp\\word2vec_cage\\src\\emotion\\parse\\train.txt");

        // 按比例随机拆分数据
        Dataset<Row>[] splits = training.randomSplit(new double[] { 0.8, 0.2 });
        Dataset<Row> part1 = splits[0];
        Dataset<Row> part2 = splits[1];
        LogisticRegression lr = new LogisticRegression().setMaxIter(30).setRegParam(0.3).setElasticNetParam(0.8);

        // 模型训练
        LogisticRegressionModel lrModel = lr.fit(training);
        lrModel.save("D:\\software\\learning_work\\eclipse\\wp\\word2vec_cage\\src\\emotion\\parse\\train.model");
        Dataset<Row> predictions = lrModel.transform(part2);

        // 获取预测值与真实值
        Dataset<Row> predictionsAndLabel = predictions.select("prediction","label");
        // 遍历预测结果，计算正确率
        long total = predictionsAndLabel.count();
        long right = 0;
        long error = 0;
        Iterator<Row> iter = predictionsAndLabel.toLocalIterator();
        while (iter.hasNext()) {
            Row row = iter.next();
            double prediction = (double) row.get(0);
            double label = (double) row.get(1);
            System.out.println("prediction:"+prediction);
            System.out.println("label:"+label);

            if((new BigDecimal(prediction).subtract(new BigDecimal(label))).compareTo(new BigDecimal("0.01")) < 0) {
                right ++;
            }else {
                error ++;
            }
        }
        System.out.println("right:"+right);
        System.out.println("error:"+error);
        System.out.println("错误率："+(error/total * 100));
    }

    public static void main(String[] args) throws IOException {
        trainLG();
    }

}
