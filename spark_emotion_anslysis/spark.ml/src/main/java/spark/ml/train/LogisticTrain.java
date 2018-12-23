package spark.ml.train;

import common.util.DateUtils;
import org.apache.spark.ml.classification.LogisticRegression;
import org.apache.spark.ml.classification.LogisticRegressionModel;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.nlp.vec.VectorModel;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.security.SecureRandom;
import java.util.Date;
import java.util.Iterator;

/**
 * Created by cage
 */
public class LogisticTrain {


    private static String saveModelPathPrefix = "F:\\taobao-code\\nlp\\model\\lr_%s";
    private static String saveModelPath = String.format(saveModelPathPrefix, DateUtils.dateToString(new Date(), DateUtils.FILE_PATTERN));
    /**
     * step2 训练逻辑回归与预测
     * @throws IOException
     */
    public static void trainLG() throws IOException {
        // 使用本地所有可用线程local[*]
        SparkSession spark = SparkSession.builder().master("local[*]").appName("LR").getOrCreate();
        Dataset<Row> training = null;
        try {
            training = spark.read().format("libsvm")
                    .load("F:\\taobao-code\\nlp\\segs\\level_and_common_seg_libsvm_checked_2018_12_22_16_48_43.txt");
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 按比例随机拆分数据
        Dataset<Row>[] splits = training.randomSplit(new double[] { 0.8, 0.2 });
        Dataset<Row> part1 = splits[0]; //训练集
        Dataset<Row> part2 = splits[1];//测试集
        LogisticRegression lr = new LogisticRegression().setMaxIter(30).setRegParam(0.3).setElasticNetParam(0.8);

        // 模型训练
        LogisticRegressionModel lrModel = lr.fit(part1);

        File file = new File(saveModelPath);
        if (file.exists()) {
            file.delete();
        }
        lrModel.save(saveModelPath);
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

//            if((new BigDecimal(prediction).subtract(new BigDecimal(label))).compareTo(new BigDecimal("1")) <= 0) {
            if((new BigDecimal(prediction).compareTo(new BigDecimal(label))) == 0) {
                right ++;
            }else {
                error ++;
            }
        }
        System.out.println("right:"+right);
        System.out.println("error:"+error);
        System.out.println("accure rate："+( (float) right / total * 100));
        System.out.println("error rate："+( (float) error / total * 100));
    }

    public static void main(String[] args) throws IOException {
        trainLG();
    }

}
