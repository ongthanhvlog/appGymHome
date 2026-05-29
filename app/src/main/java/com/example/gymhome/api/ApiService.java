package com.example.gymhome.api;

import com.example.gymhome.model.BaiViet;
import com.example.gymhome.model.ThongBao;
import com.example.gymhome.model.GenericResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiService {

    @POST("themBaiVietTuLink")
    Call<BaiViet> themBaiVietTuLink(@Body BaiViet request);

    @POST("guiThongBaoHeThong")
    Call<GenericResponse> guiThongBaoHeThong(@Body ThongBao request);

    @GET("triggerCapNhatBaiVietMoi")
    Call<GenericResponse> triggerCapNhatBaiVietMoi();

    @GET("triggerXoaBaiVietCu")
    Call<GenericResponse> triggerXoaBaiVietCu();

    @GET("triggerXoaBaiVietChuaLuu")
    Call<GenericResponse> triggerXoaBaiVietChuaLuu();
}
