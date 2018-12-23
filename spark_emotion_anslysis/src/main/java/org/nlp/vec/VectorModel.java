package org.nlp.vec;

import com.huaban.analysis.jieba.JiebaSegmenter;
import common.object.pool.JiebaSegmenterPool;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class VectorModel {

    private static Logger logger = LoggerFactory.getLogger(VectorModel.class.getName());
    public Map<String, float[]> wordMap;
    private int vectorSize; //特征数

    private int topNSize = 40;

    public Map<String, float[]> getWordMap() {
        return wordMap;
    }

    public void setWordMap(Map<String, float[]> wordMap){
        this.wordMap = wordMap;
    }

    /**
     * 获取最相似词的数量
     * @return 最相似词的数量
     */
    public int getTopNSize() {
        return topNSize;
    }

    /**
     * 设置最相似词的数量
     * @param topNSize 数量
     */
    public void setTopNSize(int topNSize) {
        this.topNSize = topNSize;
    }

    public int getVectorSize() {
        return vectorSize;
    }

    public void setVectorSize(int vectorSize) {
        this.vectorSize = vectorSize;
    }

    /**
     * 私有构造函数
     * @param wordMap 词向量哈希表
     * @param vectorSize 词向量长度
     */
    public VectorModel(Map<String, float[]> wordMap, int vectorSize){

        if (wordMap == null || wordMap.isEmpty()){
            throw new IllegalArgumentException("word2vec的词向量为空，请先训练模型。");
        }
        if (vectorSize <= 0){
            throw new IllegalArgumentException("词向量长度（layerSize）应大于0");
        }

        this.wordMap = wordMap;
        this.vectorSize = vectorSize;
    }


    /**
     * 使用Word2Vec保存的模型加载词向量模型
     * @param path 模型文件路径
     * @return 词向量模型
     */
    public static VectorModel loadFromFile(String path){

        if (path == null || path.isEmpty()){
            throw new IllegalArgumentException("模型路径可以为null或空。");
        }

        DataInputStream dis = null;
        int wordCount, layerSizeLoaded = 0;
        Map<String, float[]> wordMapLoaded = new HashMap<>();
        try {
            dis = new DataInputStream(new BufferedInputStream(new FileInputStream(path)));
            wordCount = dis.readInt();
            layerSizeLoaded = dis.readInt();
            float vector;

            String key;
            float[] value;
            for (int i = 0; i < wordCount; i++) {
                key = dis.readUTF();
                value = new float[layerSizeLoaded];
                double len = 0;
                for (int j = 0; j < layerSizeLoaded; j++) {
                    vector = dis.readFloat();
                    len += vector * vector;
                    value[j] = vector;
                }

                len = Math.sqrt(len);

                for (int j = 0; j < layerSizeLoaded; j++) {
                    value[j] /= len;
                }
                wordMapLoaded.put(key, value);
            }

        } catch (IOException ioe){
            ioe.printStackTrace();
        }finally {
            try {
                if (dis != null){
                    dis.close();
                }
            } catch (IOException ioe){
                ioe.printStackTrace();
            }
        }

        return new VectorModel(wordMapLoaded, layerSizeLoaded);

    }

    /**
     * 保存词向量模型
     * @param file 模型存放路径
     */
    public void saveModel(File file) {
        DataOutputStream dataOutputStream = null;
        try {
            dataOutputStream = new DataOutputStream(new BufferedOutputStream(
                    new FileOutputStream(file)));
            dataOutputStream.writeInt(wordMap.size());
            dataOutputStream.writeInt(vectorSize);
            for (Map.Entry<String, float[]> element : wordMap.entrySet()) {
                dataOutputStream.writeUTF(element.getKey());
                for (float d : element.getValue()) {
                    dataOutputStream.writeFloat(d);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (dataOutputStream != null){
                    dataOutputStream.close();
                }
            }catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }

    /**
     * 获取与词word最相近topNSize个词
     * @param queryWord 词
     * @return 相近词集，若模型不包含词word，则返回空集
     */
    public Set<WordScore> similar(String queryWord){

        float[] center = wordMap.get(queryWord);
        if (center == null){
            return Collections.emptySet();
        }

        int resultSize = wordMap.size() < topNSize ? wordMap.size() : topNSize + 1;
        TreeSet<WordScore> result = new TreeSet<>();
        for (int i = 0; i < resultSize; i++){
            result.add(new WordScore("^_^", -Float.MAX_VALUE));
        }
        float minDist = -Float.MAX_VALUE;
        for (Map.Entry<String, float[]> entry : wordMap.entrySet()){
            float[] vector = entry.getValue();
            float dist = 0;
            for (int i = 0; i < vector.length; i++){
                dist += center[i] * vector[i];
            }
            if (dist > minDist){
                result.add(new WordScore(entry.getKey(), dist));
                minDist = result.pollLast().score;
            }
        }
        result.pollFirst();

        return result;
    }

    /**
     * 词迁移，即word1 - word0 + word2 的结果，若三个词中有一个不在模型中，
     * 也就是没有词向量，则返回空集
     * @param word0 词
     * @param word1 词
     * @param word2 词
     * @return 与结果最相近的前topNSize个词
     */
    public TreeSet<WordScore> analogy(String word0, String word1, String word2) {
        float[] wv0 = wordMap.get(word0);
        float[] wv1 = wordMap.get(word1);
        float[] wv2 = wordMap.get(word2);

        if (wv1 == null || wv2 == null || wv0 == null) {
            return null;
        }
        float[] center = new float[vectorSize];
        for (int i = 0; i < vectorSize; i++) {
            center[i] = wv1[i] - wv0[i] + wv2[i];
        }

        int resultSize = wordMap.size() < topNSize ? wordMap.size() : topNSize;
        TreeSet<WordScore> result = new TreeSet<WordScore>();
        for (int i = 0; i < resultSize; i++){
            result.add(new WordScore("^_^", -Float.MAX_VALUE));
        }
        String name;
        float minDist = -Float.MAX_VALUE;
        for (Map.Entry<String, float[]> entry : wordMap.entrySet()){
            name = entry.getKey();
            if (name.equals(word1) || name.equals((word2))){
                continue;
            }
            float[] vector = entry.getValue();
            float dist = 0;
            for (int i = 0; i < vector.length; i++){
                dist += center[i] * vector[i];
            }
            if (dist > minDist){
                result.add(new WordScore(entry.getKey(), dist));
                minDist = result.pollLast().score;
            }
        }
        return result;
    }

    public float[] getWordVector(String word) {
        if (!wordMap.containsKey(word)) {
            return new float[vectorSize];
        }
        return wordMap.get(word);
    }


    public class WordScore implements Comparable<WordScore> {

        public String name;
        public float score;

        public WordScore(String name, float score) {
            this.name = name;
            this.score = score;
        }

        @Override
        public String toString() {
            return this.name + "\t" + score;
        }

        @Override
        public int compareTo(WordScore o) {
            if (this.score < o.score) {
                return 1;
            } else {
                return -1;
            }
        }
    }

    /**
     * 计算向量内积
     * @param vec1
     * @param vec2
     * @return
     */
    public float calDist(float[] vec1, float[] vec2) {
        float dist = 0;
        for (int i = 0; i < vec1.length; i++) {
            dist += vec1[i] * vec2[i];
        }
        return dist;
    }

    /**
     * 向量求和
     * @param sum 和向量
     * @param vec 添加向量
     */
    public void calSum(float[] sum, float[] vec) {
        for (int i = 0; i < sum.length; i++) {
            sum[i] += vec[i];
        }
    }

    /**
     * 计算词相似度
     * @param word1
     * @param word2
     * @return
     */
    public float wordSimilarity(String word1, String word2) {
        float[] word1Vec = getWordVector(word1);
        float[] word2Vec = getWordVector(word2);
        if(word1Vec == null || word2Vec == null) {
            return 0;
        }
        return calDist(word1Vec, word2Vec);
    }

    /**
     * 计算词语与词语列表中所有词语的最大相似度
     * (最小返回0)
     * @param centerWord 词语
     * @param wordList 词语列表
     * @return
     */
    private float calMaxSimilarity(String centerWord, List<String> wordList) {
        float max = -1;
        if (wordList.contains(centerWord)) {
            return 1;
        } else {
            for (String word : wordList) {
                float temp = wordSimilarity(centerWord, word);
                if (temp == 0) continue;
                if (temp > max) {
                    max = temp;
                }
            }
        }
        if (max == -1) return 0;
        return max;
    }

    /**
     * 快速计算句子相似度
     * @param sentence1Words 句子1词语列表
     * @param sentence2Words 句子2词语列表
     * @return 两个句子的相似度
     */
    public float fastSentenceSimilarity(String modelPath, List<String> sentence1Words, List<String> sentence2Words) {
        VectorModel vm = loadFromFile(modelPath);
        if (sentence1Words.isEmpty() || sentence2Words.isEmpty()) {
            return 0;
        }
        float[] sen1vector = new float[vm.getVectorSize()];
        System.out.println("vec.size:"+vm.getVectorSize());
        float[] sen2vector = new float[vm.getVectorSize()];
        double len1 = 0;
        double len2 = 0;
        for (int i = 0; i < sentence1Words.size(); i++) {
            float[] tmp = getWordVector(sentence1Words.get(i));
            System.out.println("tmp.szie():" + tmp.length);
            if (tmp != null) calSum(sen1vector, tmp);
        }
        for (int i = 0; i < sentence2Words.size(); i++) {
            float[] tmp = getWordVector(sentence2Words.get(i));
            if (tmp != null) calSum(sen2vector, tmp);
        }
        for (int i = 0; i < vm.getVectorSize(); i++) {
            len1 += sen1vector[i] * sen1vector[i];
            len2 += sen2vector[i] * sen2vector[i];
        }
        return (float) (calDist(sen1vector, sen2vector) / Math.sqrt(len1 * len2));
    }

    /**
     * 计算句子相似度
     * 所有词语权值设为1
     * @param sentence1Words 句子1词语列表
     * @param sentence2Words 句子2词语列表
     * @return 两个句子的相似度
     */
    public float sentenceSimilarity(List<String> sentence1Words, List<String> sentence2Words) {
        if (sentence1Words.isEmpty() || sentence2Words.isEmpty()) {
            return 0;
        }
        float sum1 = 0;
        float sum2 = 0;
        int count1 = 0;
        int count2 = 0;
        for (int i = 0; i < sentence1Words.size(); i++) {
            if (getWordVector(sentence1Words.get(i)) != null) {
                count1++;
                sum1 += calMaxSimilarity(sentence1Words.get(i), sentence2Words);
            }
        }
        for (int i = 0; i < sentence2Words.size(); i++) {
            if (getWordVector(sentence2Words.get(i)) != null) {
                count2++;
                sum2 += calMaxSimilarity(sentence2Words.get(i), sentence1Words);
            }
        }
        return (sum1 + sum2) / (count1 + count2);
    }
    /**
     * 计算句子相似度(带权值)
     * 每一个词语都有一个对应的权值
     * @param sentence1Words 句子1词语列表
     * @param sentence2Words 句子2词语列表
     * @param weightVector1 句子1权值向量
     * @param weightVector2 句子2权值向量
     * @return 两个句子的相似度
     * @throws Exception 词语列表和权值向量长度不同
     */
    public float sentenceSimilarity(List<String> sentence1Words, List<String> sentence2Words, float[] weightVector1, float[] weightVector2) throws Exception {
        if (sentence1Words.isEmpty() || sentence2Words.isEmpty()) {
            return 0;
        }
        if (sentence1Words.size() != weightVector1.length || sentence2Words.size() != weightVector2.length) {
            throw new Exception("length of word list and weight vector is different");
        }
        float sum1 = 0;
        float sum2 = 0;
        float divide1 = 0;
        float divide2 = 0;
        for (int i = 0; i < sentence1Words.size(); i++) {
            if (getWordVector(sentence1Words.get(i)) != null) {
                float wordMaxSimi = calMaxSimilarity(sentence1Words.get(i), sentence2Words);
                sum1 += wordMaxSimi * weightVector1[i];
                divide1 += weightVector1[i];
            }
        }
        for (int i = 0; i < sentence2Words.size(); i++) {
            if (getWordVector(sentence2Words.get(i)) != null) {
                float wordMaxSimi = calMaxSimilarity(sentence2Words.get(i), sentence1Words);
                sum2 += wordMaxSimi * weightVector2[i];
                divide2 += weightVector2[i];
            }
        }
        return (sum1 + sum2) / (divide1 + divide2);
    }

    /**
     * 得到句子的词向量
     * @param sentenceWords
     * @return
     */
    public float[] getSentenceVector(String modelPath, String sentenceWords) {
        VectorModel vm = loadFromFile(modelPath);
        return getSentenceVector(vm, sentenceWords);
    }

    /**
     * 得到句子的词向量
     * @param sentenceWords
     * @return
     */
    public float[] getSentenceVector(VectorModel vectorModel , String sentenceWords) {
        JiebaSegmenter jiebaSegmenter = null;
        try {
            jiebaSegmenter = JiebaSegmenterPool.jiebaSegmenterPool.borrowObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<String> words = jiebaSegmenter.sentenceProcess(sentenceWords);
        if (sentenceWords.isEmpty()) {
            return new float[vectorModel.getVectorSize()];
        }
        float[] senVec = new float[vectorModel.getVectorSize()];
        for(String word:words) {
            float[] tmp = getWordVector(word);
            if(tmp == null) {
                continue;
            }
            for(int k=0; k<vectorModel.getVectorSize(); k++) {
                senVec[k] += tmp[k];
            }
        }
        return senVec;
    }



    /**
     * implements "字词联合" training.
     * modelPath
     * D:\software\learning_work\eclipse\wp\word2vec_cage\vectors.bin
     * cilinModelPath
     * "D:\\taobao_customer\\word2vec-a13027434702\\data\\data\\哈工大社会计算与信息检索研究中心同义词词林扩展版\\哈工大社会计算与信息检索研究中心同义词词林扩展版\\model"
     * cilinPath
     * D:\taobao_customer\word2vec-a13027434702\data\data\哈工大社会计算与信息检索研究中心同义词词林扩展版\哈工大社会计算与信息检索研究中心同义词词林扩展版\cilin_result.txt
     * @throws IOException
     */
    public static void trainWithCilinFile(String modelPath, String cilinPath, String cilinModelPath) throws IOException {
        logger.info("trainWithCilinFile, modelPath:{}, cilinPath:{}", modelPath, cilinPath);
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        VectorModel vm = loadFromFile(modelPath);
        ArrayList<String> cilinWords = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(cilinPath)))) {
            String temp;
            while ((temp = br.readLine()) != null) {
                String[] words = temp.trim().split(" ");
                for(String word : words) {
                    if(word.trim().equals("")) {
                        continue;
                    }
                    cilinWords.add(word);
                }
            }
        }
        int lineCnt = 0;
        Map<String,float[]> map  = vm.getWordMap();
        Iterator<Map.Entry<String, float[]>> iter = map.entrySet().iterator();
        while(iter.hasNext()) {
            lineCnt ++;
            if (lineCnt % 10000 == 0) {
                logger.info("train with cilin processed:{}%", (float)lineCnt / map.size() * 100);
            }
            Map.Entry<String, float[]> entry = iter.next();
            String word = entry.getKey();
            float[] vector = entry.getValue();
            if(cilinWords.contains(word)) {
                char[] words = word.toCharArray();
                float sumSimi = 0.0f;
                for(char ch : words) {
                    float simi = vm.wordSimilarity(ch+"", word);
                    sumSimi += simi;
                }
//				float[] newVector = 0.5 * ( vector + sumSimi * vector /words.length );
                float factor = sumSimi / words.length;
                float[] newVector = vector;
                for(int i = 0; i < newVector.length; i ++) {
                    newVector[i] = 0.5f * (newVector[i] * factor + vector[i]);
                }
                vm.wordMap.put(word, newVector);
            }
        }
        vm.saveModel(new File(cilinModelPath));
        logger.info("saved model:{}", cilinModelPath);
        logger.info("train with cilin processed over...");
    }


}
