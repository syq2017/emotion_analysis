package org.nlp.util;

/**
 * Created by cage
 * 哈夫曼树结点接口
 */
public interface HuffmanNode extends Comparable<HuffmanNode> {

    public void setCode(int c);

    public void setFrequency(int freq);

    public int getFrequency();

    public void setParent(HuffmanNode parent);

    public HuffmanNode getParent();

    public HuffmanNode merge(HuffmanNode sibling);
}
