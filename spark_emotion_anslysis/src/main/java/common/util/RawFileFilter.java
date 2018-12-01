package common.util;

import java.io.*;

/**
 * Step1 原始文档语料处理:大文件拆分，字符编码集
 */
public class RawFileFilter {

    public static final String DOC_START_FLAG = "<doc>";
    public static final String DOC_END_FLAG = "</doc>";
    public static final String URL_START_FLAG = "<url>";
    public static final String URL_END_FLAG = "</url>";
    public static final String DOC_NO_START_FLAG = "<docno>";
    public static final String DOC_NO_END_FLAG = "</docno>";
    public static final String TITLE_START_FLAG = "<contenttitle>";
    public static final String TITLE_END_FLAG = "</contenttitle>";
    public static final String CONTENT_START_FLAG = "<content>";
    public static final String CONTENT_END_FLAG = "</content>";

    /**
     * 文件拆分：将一个大文件拆分为多个小文件( 200MB左右 )
     */
    public static void bigFileSplit(String bigFilePath,String dstFilePath) throws IOException {
        int fileNo = 0 ; //被切分的每个小文件名字，从0开始递增
        int fileMaxSize = 200 * 1024 * 1024;
        int currentBytesCount = 0;
        int tmp = 0 ;
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(bigFilePath),"GB18030"));//,"GB2312")
        File file = new File(dstFilePath + fileNo +".txt");
        if(!file.exists()){
            file.createNewFile();
        }
        BufferedWriter bufferedWriter =  new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file,true),"GB18030")); //,"GB2312"
        String line = null;
        long startTime = System.currentTimeMillis() / 1000;
        while ((line = bufferedReader.readLine()) != null){
//            System.out.println(line);
            tmp ++;
            if(tmp % 100000 == 0){
                System.out.println("lines: " + tmp);
            }
            if(line.contains(DOC_END_FLAG) || line.contains(DOC_START_FLAG) || line.contains(URL_START_FLAG) || line.contains(URL_END_FLAG)){
                continue;
            }
            if(line.contains(DOC_NO_START_FLAG) || line.contains(DOC_NO_END_FLAG)){
                continue;
            }
            if(line.contains(TITLE_START_FLAG)){
                line = line.split(TITLE_START_FLAG)[1];
                if(line == null){
                    continue;
                }
            }
            if(line.contains(TITLE_END_FLAG)){
                line = line.split(TITLE_END_FLAG).length > 0 ? line.split(TITLE_END_FLAG)[0] : null; // 可能会出现<contenttitle></contenttitle>
                if(line == null){
                    continue;
                }
            }
            if(line.contains(CONTENT_START_FLAG)){
                line = line.split(CONTENT_START_FLAG)[1];
                if(line == null){
                    continue;
                }
            }
            if(line.contains(CONTENT_END_FLAG)){
                line = line.split(CONTENT_END_FLAG).length > 0 ? line.split(CONTENT_END_FLAG)[0] : null;// <content></content>
                if(line == null){
                    continue;
                }
            }
            line = clearNotChinese(line);
            bufferedWriter.write(line + "\r\n");
            currentBytesCount += line.getBytes().length;
            if(currentBytesCount >= fileMaxSize){
                currentBytesCount = 0;
                bufferedWriter.flush();
                fileNo ++;
                bufferedWriter.close();
                file = new File(dstFilePath + fileNo +".txt");
                file.createNewFile();
                bufferedWriter =  new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file),"GB2312"));
            }
        }
        bufferedWriter.flush();
        bufferedWriter.close();
        bufferedReader.close();
        long endTime = System.currentTimeMillis() / 1000;
        System.out.println("processed lines: "+ tmp);
        System.out.println( "total time : " + (endTime - startTime));
    }


    /**

     * 去除“第”之前的所有非汉字内容

     */

    /**
     * 保留文本中的汉字
     * @param buff
     * @return
     */
    public static String clearNotChinese(String buff){
        String tmpString =buff.replaceAll("(?i)[^a-zA-Z0-9\u4E00-\u9FA5]", "");//去掉所有中英文符号
        char[] carr = tmpString.toCharArray();
        for(int i = 0; i<tmpString.length();i++){
            if(carr[i] < 0xFF){
                carr[i] = ' ' ;//过滤掉非汉字内容
            }
        }
        return String.copyValueOf(carr).trim();
    }

 /**
     * Unicode转中文   
     */
   public static String decodeUnicode( String dataStr) {
       int start = 0;
       int end = 0;
        StringBuffer buffer = new StringBuffer();
         while (start > -1) {
            end = dataStr.indexOf("\\u", start + 2);
            String charStr = "";
            if (end == -1) {
                charStr = dataStr.substring(start + 2, dataStr.length());
             } else {
               charStr = dataStr.substring(start + 2, end);
             }
             char letter = (char) Integer.parseInt(charStr, 16); // 16进制parse整形字符串。     
             buffer.append(new Character(letter).toString());
             start = end;
        }
        return buffer.toString();
   }


    /**
     * step1
     * @param args
     */
    public static void main(String[] args) {
        String filePath = "F:\\learn\\dataset\\news_tensite_xml.full\\news_tensite_xml.dat";
        String dstPath = "F:\\learn\\dataset\\multi-files\\";
        try {
            bigFileSplit(filePath,dstPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
