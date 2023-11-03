package com.mwin.reward.network;

import com.google.gson.JsonObject;
import com.mwin.reward.async.models.ApisResponse;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface WebApisInterface {

    @FormUrlEncoded
    @POST("PGPNLDFKSBM")
    Call<ApisResponse> getHomeData(@Header("Token") String token, @Header("Secret") String random, @Field("details") String details);

    @FormUrlEncoded
    @POST("UJGHNXCDFG")
    Call<ApisResponse> getMoreAppsData(@Header("Token") String token, @Header("Secret") String random, @Field("details") String details);

    @FormUrlEncoded
    @POST("BATETAVADA")
    Call<ApisResponse> loginUser(@Header("Token") String token, @Header("Secret") String random, @Field("details") String details);

    @FormUrlEncoded
    @POST("PRFGTBNHKUJ")
    Call<ApisResponse> getUserProfile(@Header("Token") String token, @Header("Secret") String random, @Field("details") String details);

    @FormUrlEncoded
    @POST("FRONGHNJ")
    Call<ApisResponse> editUserProfile(@Header("Token") String token, @Header("Secret") String random, @Field("details") String details);

    @FormUrlEncoded
    @POST("YHMJKIOPIK")
    Call<ApisResponse> getNotificationData(@Header("Token") String token, @Header("Secret") String random, @Field("details") String details);

    @FormUrlEncoded
    @POST("faqData")
    Call<ApisResponse> getFAQ(@Header("Token") String token, @Header("Secret") String random, @Field("details") String details);

    @Multipart
    @POST("DEGNJLJUJK")
    Call<ApisResponse> submitFeedback(@Header("Token") String token, @Header("Secret") String random, @Part("details") RequestBody details, @Part MultipartBody.Part Image);

    @FormUrlEncoded
    @POST("YHNBMJUHD")
    Call<ApisResponse> getPointHistory(@Header("Token") String token, @Header("Secret") String random, @Field("details") String details);

    @FormUrlEncoded
    @POST("UJAQXCBNLPPO")
    Call<ApisResponse> getInviteAsync(@Header("Token") String token, @Header("Secret") String random, @Field("details") String details);

    @FormUrlEncoded
    @POST("GBBNMHJNKF")
    Call<ApisResponse> getTaskOfferList(@Header("Token") String token, @Header("Secret") String random, @Field("details") String details);

    @FormUrlEncoded
    @POST("ROADPARTR")
    Call<ApisResponse> getTaskDetails(@Header("Token") String token, @Header("Secret") String random, @Field("details") String details);

    @Multipart
    @POST("YBOLPJKMNY")
    Call<ApisResponse> taskImageUpload(@Header("Token") String token, @Header("Secret") String random, @Part("details") RequestBody details, @Part MultipartBody.Part Image);

    @FormUrlEncoded
    @POST("DYFHFDGSERERT")
    Call<ApisResponse> getWithdrawalType(@Header("Token") String token, @Header("Secret") String random, @Field("details") String details);

    @FormUrlEncoded
    @POST("FGHFGHFGHYGB")
    Call<ApisResponse> getWithdrawTypeList(@Header("Token") String token, @Header("Secret") String random, @Field("details") String details);

    @FormUrlEncoded
    @POST("FGKVBMKVBSAAJK")
    Call<ApisResponse> redeemPoints(@Header("Token") String token, @Header("Secret") String random, @Field("details") String details);

    @FormUrlEncoded
    @POST("SADCVBNHY")
    Call<ApisResponse> saveDailyBonus(@Header("Token") String token, @Header("Secret") String random, @Field("details") String details);

    @GET
    Call<JsonObject> callApi(@Url String Value,//http://surgex.media-412.com/click
                             @Query("pid") String pid,//9
                             @Query("offer_id") String o_id,//1548
                             @Query("sub5") String installed_package_id,
                             @Query("sub3") String gaid,
                             @Query("sub2") String current_app_package_id,
                             @Query("sub1") String unique_click_id,//device_id
                             @Query("sub7") String ip_address);

    @FormUrlEncoded
    @POST("PAKGHBNHMVRFVB")
    Call<JsonObject> savePackageTracking(@Header("Token") String token, @Header("Secret") String random, @Field("details") String details);

    @FormUrlEncoded
    @POST("REWYUMZXAQWED")
    Call<ApisResponse> getRewardScreenData(@Header("Token") String token, @Header("Secret") String random, @Field("details") String details);

    @FormUrlEncoded
    @POST("WATYHNFRQASW")
    Call<ApisResponse> getWatchVideoList(@Header("Token") String token, @Header("Secret") String random, @Field("details") String details);

    @FormUrlEncoded
    @POST("YUJNMKIOPLF")
    Call<ApisResponse> saveWatchVideo(@Header("Token") String token, @Header("Secret") String random, @Field("details") String details);

    @FormUrlEncoded
    @POST("SDYNFDFGDFS")
    Call<ApisResponse> getGiveAwayList(@Header("Token") String token, @Header("Secret") String random, @Field("details") String details);

    @FormUrlEncoded
    @POST("SAVYHNTVOP")
    Call<ApisResponse> saveGiveAway(@Header("Token") String token, @Header("Secret") String random, @Field("details") String details);

    @FormUrlEncoded
    @POST("YJMNVOPLDTB")
    Call<ApisResponse> getScratchcard(@Header("Token") String token, @Header("Secret") String random, @Field("details") String details);

    @FormUrlEncoded
    @POST("UJMVRTYHJ")
    Call<ApisResponse> saveScratchcard(@Header("Token") String token, @Header("Secret") String random, @Field("details") String details);

    @FormUrlEncoded
    @POST("FGHFGHBIKJMM")
    Call<ApisResponse> getLuckyNumber(@Header("Token") String token, @Header("Secret") String random, @Field("details") String details);

    @FormUrlEncoded
    @POST("GBNHMUJKHKG")
    Call<ApisResponse> saveLuckyNumberContest(@Header("Token") String token, @Header("Secret") String random, @Field("details") String details);

    @FormUrlEncoded
    @POST("FVBNHJML")
    Call<ApisResponse> getLuckyNumberHistory(@Header("Token") String token, @Header("Secret") String random, @Field("details") String details);

    @FormUrlEncoded
    @POST("DSANBNURFD")
    Call<ApisResponse> getDailyRewardList(@Header("Token") String token, @Header("Secret") String random, @Field("details") String details);

    @FormUrlEncoded
    @POST("SCYQPOOOBG")
    Call<ApisResponse> saveDailyReward(@Header("Token") String token, @Header("Secret") String random, @Field("details") String details);

    @FormUrlEncoded
    @POST("DERFGGFHFHFGHHGHEE")
    Call<ApisResponse> deleteAccount(@Header("Token") String token, @Header("Secret") String random, @Field("details") String details);

    @FormUrlEncoded
    @POST("GHFDVVBYTG")
    Call<ApisResponse> getTextTypingData(@Header("Token") String token, @Header("Secret") String random, @Field("details") String details);

    @FormUrlEncoded
    @POST("DCTJHVFGJFFGH")
    Call<ApisResponse> saveTextTyping(@Header("Token") String token, @Header("Secret") String random, @Field("details") String details);

    @FormUrlEncoded
    @POST("RFDTYHNBJHG")
    Call<ApisResponse> getMilestonesData(@Header("Token") String token, @Header("Secret") String random, @Field("details") String details);

    @FormUrlEncoded
    @POST("RFERFBCCDF")
    Call<ApisResponse> saveMilestone(@Header("Token") String token, @Header("Secret") String random, @Field("details") String details);

    @FormUrlEncoded
    @POST("RFVBQYGGFGASW")
    Call<ApisResponse> getSingleMilestoneData(@Header("Token") String token, @Header("Secret") String random, @Field("details") String details);

    @FormUrlEncoded
    @POST("BOUJMBGTF")
    Call<ApisResponse> getCountData(@Header("Token") String token, @Header("Secret") String random, @Field("details") String details);

    @FormUrlEncoded
    @POST("TGBNMJHJU")
    Call<ApisResponse> saveCountData(@Header("Token") String token, @Header("Secret") String random, @Field("details") String details);

    @FormUrlEncoded
    @POST("GOYNNOOM")
    Call<ApisResponse> getWordSorting(@Header("Token") String token, @Header("Secret") String random, @Field("details") String details);

    @FormUrlEncoded
    @POST("SOOSTGGGTOO")
    Call<ApisResponse> saveWordSorting(@Header("Token") String token, @Header("Secret") String random, @Field("details") String details);

    @FormUrlEncoded
    @POST("YHKJHGDFDFG")
    Call<ApisResponse> getAdjoeLeaderboardData(@Header("Token") String token, @Header("Secret") String random, @Field("details") String details);

    @FormUrlEncoded
    @POST("RYUNNGFGFF")
    Call<ApisResponse> getAdjoeLeaderboardHistory(@Header("Token") String token, @Header("Secret") String random, @Field("details") String details);

    @FormUrlEncoded
    @POST("PANIPOORI")
    Call<ApisResponse> getMinesweeper(@Header("Token") String token, @Header("Secret") String random, @Field("details") String details);

    @FormUrlEncoded
    @POST("BHELPOORI")
    Call<ApisResponse> saveMinesweeper(@Header("Token") String token, @Header("Secret") String random, @Field("details") String details);

    @FormUrlEncoded
    @POST("RFHYEEEEDGF")
    Call<ApisResponse> getAlphabetData(@Header("Token") String token, @Header("Secret") String secret, @Field("details") String details);

    @FormUrlEncoded
    @POST("SDGASRDHGWER")
    Call<ApisResponse> saveAlphabetData(@Header("Token") String token, @Header("Secret") String secret, @Field("details") String details);
    @FormUrlEncoded
    @POST("REFERTASKGETVADAPAV")
    Call<ApisResponse> shareTaskOffer(@Header("Token") String token, @Header("Secret") String secret, @Field("details") String details);

    @FormUrlEncoded
    @POST("SFGDFGDFHD")
    Call<ApisResponse> saveQuickTask(@Header("Token") String token, @Header("Secret") String secret, @Field("details") String details);
    @FormUrlEncoded
    @POST("DYJHGNMKLO")
    Call<ApisResponse> getWalletBalance(@Header("Token") String token, @Header("Secret") String secret, @Field("details") String details);
    @FormUrlEncoded
    @POST("CHEESEPALAKDOSA")
    Call<ApisResponse> saveDailyTarget(@Header("Token") String token, @Header("Secret") String secret, @Field("details") String details);

}
