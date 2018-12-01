package common.beans;

/**
 * 每个类型抓取时的参数封装类
 * Created by cage on 2018-11-25
 */
public class TypeRankBase {
    private String type;
    private String intervalId;
    private String action;
    private int start;
    private int limit;

    public TypeRankBase(String type, String intervalId, int start, int limit) {
        this.type = type;
        this.intervalId = intervalId;
        this.action = "";
        this.start = start;
        this.limit = limit;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getIntervalId() {
        return intervalId;
    }

    public void setIntervalId(String intervalId) {
        this.intervalId = intervalId;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    @Override
    public String toString() {
        return "TypeRankBase{" +
                "type='" + type + '\'' +
                ", intervalId='" + intervalId + '\'' +
                ", action='" + action + '\'' +
                ", start=" + start +
                ", limit=" + limit +
                '}';
    }
}
