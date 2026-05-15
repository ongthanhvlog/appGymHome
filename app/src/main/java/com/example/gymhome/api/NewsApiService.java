package com.example.gymhome.api;

import com.example.gymhome.model.NewsResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface NewsApiService {
    @GET("v2/top-headlines")
    Call<NewsResponse> getHealthNews(
        @Query("category") String category,
        @Query("apiKey") String apiKey,
        @Query("pageSize") int pageSize,
        @Query("country") String country
    );
}
