package com.example.gymhome.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class NewsResponse {
    @SerializedName("status")
    private String status;
    
    @SerializedName("totalResults")
    private int totalResults;
    
    @SerializedName("articles")
    private List<BaiViet> articles;

    public List<BaiViet> getArticles() {
        return articles;
    }

    public void setArticles(List<BaiViet> articles) {
        this.articles = articles;
    }
}
