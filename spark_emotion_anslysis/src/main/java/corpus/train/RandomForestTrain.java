package corpus.train;

/**
 * Created by cage
 */

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import common.util.MyTestIgnore;
import org.apache.spark.SparkConf;
import org.apache.spark.SparkContext;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.mllib.regression.LabeledPoint;
import org.apache.spark.mllib.tree.RandomForest;
import org.apache.spark.mllib.tree.model.RandomForestModel;
import org.apache.spark.mllib.util.MLUtils;

import org.nlp.vec.VectorModel;
import scala.Tuple2;

/**
 * Random Forest模型
 */
public class RandomForestTrain {
    @MyTestIgnore
    private static String rawFile = "D:\\software\\learning_work\\eclipse\\wp\\word2vec_cage\\src\\emotion\\parse\\train.txt";
    @MyTestIgnore
    private static String modelpath1 = "D:\\env-tools\\spark-2.2.1-bin-hadoop2.7\\mydata\\load\\rfModel2\\";
    private SparkConf conf;
    private SparkContext sc;
    private static JavaRDD<LabeledPoint> trainingData;
    private static JavaRDD<LabeledPoint> testData;
    private static JavaRDD<LabeledPoint> allData;
    /**
     * 配置信息、数据拆分
     */
    public void prepare(String dataPath){
        conf = new SparkConf().setAppName("RF").setMaster("local[*]");
        sc = new SparkContext(conf);
        JavaRDD<LabeledPoint> lpdata = MLUtils.loadLibSVMFile(sc, dataPath).toJavaRDD();
        double[] trainAndTest = {0.7,0.3};
        JavaRDD<LabeledPoint>[] twoPartData = lpdata.randomSplit(trainAndTest);
        allData = lpdata;
        this.trainingData = twoPartData[0];
        this.testData = twoPartData[1];
    }

    /**
     * 模型训练
     * @param trainingData
     * @param modelPath
     */
    public void train(JavaRDD<LabeledPoint> trainingData,String modelPath){
        // Set parameters.
        //  Empty categoricalFeaturesInfo indicates all features are continuous.
        Map<Integer, Integer> categoricalFeaturesInfo = new HashMap<Integer, Integer>();
        int numTrees = 6;
        String impurity = "variance";
        String featureSubsetStrategy = "sqrt";
        Integer maxDepth = 5;
        Integer maxBins = 100;
        int seed = 12345;
        // Train a RandomForest model.
        final RandomForestModel model = RandomForest.trainRegressor(trainingData,
                categoricalFeaturesInfo, numTrees, featureSubsetStrategy,
                impurity, maxDepth, maxBins, seed);

        model.save(sc, modelPath);
    }

    public void predict(JavaRDD<LabeledPoint> testData,String modelPath){
        //加载rf模型
        RandomForestModel model = RandomForestModel.load(sc, modelPath);
        //加载测试文件
        testData.cache();
        //预测数据
        JavaRDD<Tuple2<Integer, Integer>>  predictionAndLabel = testData.map(new Prediction(model)) ;
        List<Tuple2<Integer, Integer>> result = predictionAndLabel.collect();
        long total = testData.count();
        long right = 0;
        long error = 0;
        float mape = 0.0f;
        float percentSum = 0.0f;
        int me = 0;// max error
        for(Tuple2<Integer, Integer> tuple : result){
//			tuple._1
            me = Math.abs(tuple._1()-tuple._2) > me ? Math.abs(tuple._1()-tuple._2) : me;
            percentSum +=  (float)Math.abs(tuple._1()-tuple._2) / tuple._2;
            Integer subResult = (int) Math.abs(tuple._1() - tuple._2());
            if(subResult <= 0.1){
                right += 1;
            }else{
                error += 1;
            }
        }
        mape = percentSum / total * 100;
        System.out.println("total records:" +  total);
        System.out.println("MAPE:"+mape);
        System.out.println("ME:"+me);
        System.out.println("accure rate:" + (float)right / total);
        System.out.println("error rate:" + (float)error / total);
    }


    public void stop(){
        sc.stop();
    }

    /**
     * 得到评论的情感倾向
     * @param sentence
     * @param modelPath
     * @throws IOException
     */
    public void getSentencePosOrNeg(String sentence,String modelPath) throws IOException {
        VectorModel vm = VectorModel.loadFromFile(modelPath);
        float[] senVector = vm.getSentenceVector(modelPath, sentence);
        String newLine = "1 ";//此处标签任意
        for (int i = 0; i < senVector.length; i++) {
            newLine += (i + 1) + ":" + senVector[i] + " ";
        }
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new FileWriter(new File("tmp.txt")));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        bw.write(newLine+"\r");
        bw.flush();
        bw.close();
        //加载rf模型
        RandomForestModel model = RandomForestModel.load(sc, modelPath);
        //加载测试文件
        JavaRDD<LabeledPoint> lpdata = MLUtils.loadLibSVMFile(sc, "tmp.txt").toJavaRDD();
        lpdata.cache();
        //预测数据
        JavaRDD<Tuple2<Integer, Integer>>  predictionAndLabel = lpdata.map(new Prediction(model)) ;
        List<Tuple2<Integer, Integer>> result = predictionAndLabel.collect();
        for(Tuple2<Integer, Integer> tuple : result){
//			tuple._1
            if(tuple._1() == 1){
                System.out.println(tuple._1()+" positive");
                System.out.println();
            }else {
                System.out.println(tuple._1()+ " negative");
            }
        }
    }

    @MyTestIgnore
    public static void main(String[] args) throws IOException{
        RandomForestTrain gb = new RandomForestTrain();
        gb.prepare(rawFile);
//		gb.train(allData, modelpath1);
//		gb.predict(testData, modelpath1);
        String string1 = "电池充完了电连手机都打不开.简直烂的要命.真是金玉其外,败絮其中!连5号电池都不如";
        String string2 = "这手机真棒，从1米高的地方摔下去就坏了";
        gb.getSentencePosOrNeg(string1, modelpath1);
        gb.getSentencePosOrNeg(string2, modelpath1);
        gb.stop();
    }

    static class Prediction implements Function<LabeledPoint, Tuple2<Integer , Integer>> {
        RandomForestModel model;
        public Prediction(RandomForestModel model){
            this.model = model;
        }
        public Tuple2<Integer, Integer> call(LabeledPoint p) throws Exception {
            Integer score = (int) model.predict(p.features());
            return new Tuple2<>(score, (int) p.label());
        }
    }


    static class CountSquareError implements Function<Tuple2<Double, Double>, Double> {
        public Double call (Tuple2<Double, Double> pl) {
            double diff = pl._1() - pl._2();
            return diff * diff;
        }
    }

    static class CountSquarAcurError implements Function<Tuple2<Double, Double>, Double> {
        public Double call (Tuple2<Double, Double> pl) {
            double diff = pl._1() - pl._2();
            return diff * diff;
        }
    }

    static  class ReduceSquareError implements Function2<Double, Double, Double> {
        public Double call(Double a , Double b){
            return a + b ;
        }
    }

}
