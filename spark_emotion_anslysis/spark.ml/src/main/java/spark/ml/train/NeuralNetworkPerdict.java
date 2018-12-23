package spark.ml.train;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import common.util.DateUtils;
import common.util.MyTestIgnore;
import org.apache.commons.math3.optimization.Weight;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.spark.ml.classification.MultilayerPerceptronClassificationModel;
import org.apache.spark.ml.classification.MultilayerPerceptronClassifier;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

public class NeuralNetworkPerdict {

	private static Logger logger = Logger.getLogger(NeuralNetworkPerdict.class);
	private static String rawFile1 = "F:\\taobao-code\\nlp\\segs\\level_and_common_seg_libsvm_checked_2018_12_23_08_11_06.txt";
	private static String modelRootPathPrefix = "F:\\taobao-code\\nlp\\model\\nn_%s\\";
//	private static String modelRootPath = String.format(modelRootPathPrefix, DateUtils.dateToString(new Date(), DateUtils.FILE_PATTERN));
	@MyTestIgnore
	private static String modelRootPath = "F:\\taobao-code\\nlp\\model\\nn_2018_12_23_09_10_16\\";
	private static String nnPerdictData = "F:\\taobao-code\\nlp\\model\\nn_perdict_result\\";

	private static SparkSession sc;
	private static Dataset<Row> trainingData;
	private static Dataset<Row> testData;
	private static int[] layer = new int[] {200, 250, 5};
	private static int[] maxIter = new int[] { 100, 200, 500, 1000};

	/**
	 * 数据准备，环境配置
	 *
	 * @param dataPath
	 */
	public void prepare(String dataPath) {
		sc = SparkSession.builder().appName("MLPC").master("local[15]")
				.getOrCreate();
		// 屏蔽日志
		Logger.getLogger("org.apache.spark").setLevel(Level.INFO);
		Logger.getLogger("org.eclipse.jetty.server").setLevel(Level.OFF);
		Dataset<Row>[] dataSet = sc.read().format("libsvm").load(rawFile1)
				.randomSplit(new double[] { 0.8, 0.2});
		trainingData = dataSet[0];
		testData = dataSet[1];
	}

	/**
	 * 得到原始数据，并封装成Dataset<Row>
	 * @throws IOException
	 */
	public Dataset<Row> getInputData(String filepath){
		Dataset<Row>[] dataSet = sc.read().format("libsvm").load(rawFile1).randomSplit(new double[] {0.0001,0.9999});
		Dataset<Row> inputDataSet = dataSet[0];
		inputDataSet.show();
		return inputDataSet;
	}

	public void train(Dataset<Row> trainingData) throws IOException {
		// 利用如下类似的循环可以很方便的对各种参数进行调优
		//找出最有迭代轮数
		for (int i = 0; i < maxIter.length; i++) {
			MultilayerPerceptronClassifier multilayerPerceptronClassifier =
					new MultilayerPerceptronClassifier()
							.setLabelCol("label").setFeaturesCol("features")
			.setLayers(layer).setMaxIter(maxIter[i]).setBlockSize(128)
			.setSeed(1000);
			MultilayerPerceptronClassificationModel model = multilayerPerceptronClassifier
					.fit(trainingData);
			// 存储
			model.save(modelRootPath + "" + maxIter[i]);
		}
	}

	public void perdict(Dataset<Row> testData) throws IOException {
		// 汇总结果
		ArrayList<String> resultList = new ArrayList<String>();
		//明细结果写入本地文件，以用于作图展示、存入数据库 
		for (int i = 0; i < maxIter.length; i++) {
			MultilayerPerceptronClassificationModel model = 
					MultilayerPerceptronClassificationModel.load(modelRootPath + maxIter[i]);

			Dataset<Row> predictions = model.transform(testData);
			// 获取预测值与真实值 
			Dataset<Row> predictionsAndLabel = predictions.select("prediction","label");
			// 遍历预测结果，计算正确率 
			// MAPE ME等 
			long total = predictionsAndLabel.count();
			long right = 0;
			long error = 0;
			float mape = 0.0f;
			float percentSum = 0.0f;
			double me = 0d;// max error
			Iterator<Row> iter = predictionsAndLabel.toLocalIterator();
			while (iter.hasNext()) {
				Row row = iter.next();
				double predictResult = (double) row.get(0);
				double label = (double) row.get(1);
//				if((new BigDecimal(predictResult).subtract(new BigDecimal(label))).compareTo(new BigDecimal("1")) <= 0) {
				if((new BigDecimal(predictResult).compareTo(new BigDecimal(label))) == 0) {
					right ++;
				}else {
					error ++;
				}
			}
			String tmp = "\rmaxIter:" + maxIter[i] + "\r" + "\t\taccure rate:" + ((float) right / total)
					+ "\t\terror rate:" + ((float) error / total);
			resultList.add(tmp);
			writeToLocal(predictionsAndLabel, maxIter[i]);
		}
		writeSummaryToLocal(resultList);
	}
	
//	/**
//	 * 预测结果并写到本地文件
//	 * @param testData
//	 * @throws IOException
//	 */
//	public void perdictAndWrite(Dataset<Row> testData) throws IOException {
//		// 汇总结果
//		ArrayList<String> resultList = new ArrayList<String>();
//		//明细结果写入本地文件，以用于作图展示、存入数据库
//		for (int i = 0; i < maxIter.length; i++) {
//			MultilayerPerceptronClassificationModel model =
//					MultilayerPerceptronClassificationModel.load(modelRootPath + maxIter[i]);
//			Dataset<Row> predictions = model.transform(testData);
//			// 获取预测值与真实值
//			Dataset<Row> predictionsAndLabel = predictions.select("prediction","label");
//			// 遍历预测结果，计算正确率
//			// MAPE ME等
//			long total = predictionsAndLabel.count();
//			long right = 0;
//			long error = 0;
//			float mape = 0.0f;
//			float percentSum = 0.0f;
//			double me = 0d;// max error
//			Iterator<Row> iter = predictionsAndLabel.toLocalIterator();
//			while (iter.hasNext()) {
//				Row row = iter.next();
//				double prediction = (double) row.get(0);
//				double label = (double) row.get(1);
//				me = Math.abs(prediction - label) > me ? Math.abs(prediction
//						- label) : me;
//				percentSum = (new BigDecimal(Math.abs(prediction - label))
//						.divide(new BigDecimal(label), 3,
//								BigDecimal.ROUND_HALF_UP)).floatValue();
//				Integer subResult = (int) Math.abs(prediction - label);
//				if (subResult <= 3) {
//					right += 1;
//				} else {
//					error += 1;
//				}
//			}
//			mape = percentSum / total * 100;
//			String tmp = "maxIter:" + maxIter[i] + "\r" + " MAPE:" + mape
//					+ " ME:" + me + " accure rate:" + ((float) right / total)
//					+ " error rate:" + ((float) error / total);
//			resultList.add(tmp);
//			writeToLocal(predictionsAndLabel, maxIter[i]);
//		}
//		writeSummaryToLocal(resultList);
//	}

	
	public void stop(){
		sc.stop();
	}
	
	/**
	 * 明细数据到写到本地
	 * @param predictionsAndLabel
	 * @param maxIter
	 * @throws IOException
	 */
	public void writeToLocal(Dataset<Row> predictionsAndLabel,int maxIter) throws IOException{
		String filePath = nnPerdictData+""+maxIter+".txt";
		FileWriter wr = new FileWriter(new File(filePath));
		wr.write("perdict"+"\t"+"real");
		if(predictionsAndLabel == null){
			return;
		}
		String tmp;
		Iterator<Row> iter = predictionsAndLabel.toLocalIterator();
		while (iter.hasNext()) {
			Row row = iter.next();
			double prediction = (double) row.get(0);
			double label = (double) row.get(1);
			tmp = prediction + "\t" + label + "\r";
			wr.write(tmp);
		}
		wr.flush();
		wr.close();
	}
	
	/** 
	 * 汇总数据到写到本地   
	 */
	public void writeSummaryToLocal(ArrayList<String> resultList) throws IOException{ 
		FileWriter wr = new FileWriter(new File(nnPerdictData+"汇总数据.txt")); 
		wr.write("汇总数据到写到本地\r"); 
		for(String str : resultList){
			wr.write(str);
		}
		wr.flush();
		wr.close();
	}
	
	public static void main(String[] args) throws IOException {
		NeuralNetworkPerdict nnp = new NeuralNetworkPerdict();
		nnp.prepare(rawFile1);
//		nnp.train(trainingData);
		nnp.perdict(testData);
		nnp.stop();
		System.out.println("modelRootPath:"+modelRootPath);
	}
}
