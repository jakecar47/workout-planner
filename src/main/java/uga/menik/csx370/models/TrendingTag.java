package uga.menik.csx370.models;

public class TrendingTag {
    private String tag;
    private int postCount;
    private String lastUsed;

    public TrendingTag(String tag, int postCount, String lastUsed) {
        this.tag = tag;
        this.postCount = postCount;
        this.lastUsed = lastUsed;
    }

    public String getTag() {
        return tag;
    }

    public int getPostCount() {
        return postCount;
    }

    public String getLastUsed() {
        return lastUsed;
    }
}
