package corpus.process.extract.raw.step1.pennyjobsharing;

import java.io.*;

/**
 *
 * 处理pennyjobsharing 语料
 */
public class Pennyjobsharing {

    /**
     *抽取样例数据观察
     * @param rawFilePath  原始数据文件路径
     * @param sampleDataPath 样例数据文件存储路径
     * @param sampleLinesCnt 抽取样例数据行数
     */
    public static void getSampleData(String rawFilePath,String sampleDataPath,int sampleLinesCnt) throws Exception {
        BufferedReader bufferedReader = new BufferedReader(new FileReader(new File(rawFilePath)));
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(new File(sampleDataPath)));
        String line = null;
        for(int i=0;i<sampleLinesCnt;i++){
            line = bufferedReader.readLine();
            if(line != null){
                bufferedWriter.write(line+"\r\n");
            }
        }
        bufferedWriter.flush();
        bufferedWriter.close();
        bufferedReader.close();
    }

}
