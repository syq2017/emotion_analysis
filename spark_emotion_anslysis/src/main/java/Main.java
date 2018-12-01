import corpus.process.extract.raw.step1.pennyjobsharing.Pennyjobsharing;

public class Main {

    public static void main(String[] args) throws Exception {
        System.setProperty("log4j.configuration","file:/Users/shiyuquan/Downloads/spark_emotion_anslysis/src/main/resources/slf4j.properties");
        //抽取样例数据
        String rawDataPath = "F:\\dataset\\pennyjobsharing-unzip\\part-r-00000\\part-r-00000";
        String sampleDataPath = "F:\\dataset\\pennyjobsharing-unzip\\part-r-00000\\part-r-00000-sanple-2000";
        int line = 2000;
        Pennyjobsharing.getSampleData(rawDataPath,sampleDataPath,line);
    }
}
