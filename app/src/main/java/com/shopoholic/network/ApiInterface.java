package com.shopoholic.network;

import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.QueryMap;


/**
 * ApiInterface.java
 * This class act as an interface between Retrofit and Classes used using Retrofit in Application
 *
 * @author Appinvetiv
 * @version 1.0
 * @since 1.0
 */

public interface ApiInterface {

    //  POST queries

    @FormUrlEncoded
    @POST("login")
    Call<ResponseBody> hitLoginApi(@FieldMap HashMap<String, String> map);

    @FormUrlEncoded
    @POST("sociallogin")
    Call<ResponseBody> hitSocialLoginApi(@FieldMap HashMap<String, String> map);

    @FormUrlEncoded
    @POST("signup")
    Call<ResponseBody> hitSignUpApi(@FieldMap HashMap<String, String> map);

    @FormUrlEncoded
    @POST("forgot")
    Call<ResponseBody> hitForgotPasswordApi(@FieldMap HashMap<String, String> map);

    @FormUrlEncoded
    @POST("otpgenerate")
    Call<ResponseBody> hitResendOtpApi(@FieldMap HashMap<String, String> map);

    @FormUrlEncoded
    @POST("resetpass")
    Call<ResponseBody> hitResetPasswordApi(@FieldMap HashMap<String, String> map);

    @FormUrlEncoded
    @POST("changepassword")
    Call<ResponseBody> hitChangePasswordApi(@FieldMap HashMap<String, String> map);

    @FormUrlEncoded
    @POST("otpverify")
    Call<ResponseBody> hitVerifyOtpApi(@FieldMap HashMap<String, String> map);

    @FormUrlEncoded
    @POST("saveotherdetails")
    Call<ResponseBody> hitSaveInformationApi(@FieldMap HashMap<String, String> map);

    @FormUrlEncoded
    @POST("deals")
    Call<ResponseBody> hitLikeDealsApi(@FieldMap HashMap<String, String> map);

    @FormUrlEncoded
    @POST("edit")
    Call<ResponseBody> hitEditProfileDataApi(@FieldMap HashMap<String, String> map);

    @FormUrlEncoded
    @POST("savelocation")
    Call<ResponseBody> hitSaveAddressApi(@FieldMap HashMap<String, String> map);

    @FormUrlEncoded
    @POST("createorder")
    Call<ResponseBody> hitCreateOrderApi(@FieldMap HashMap<String, String> map);

    @FormUrlEncoded
    @POST("follow")
    Call<ResponseBody> hitBuddyFollowApi(@FieldMap HashMap<String, String> map);

    @FormUrlEncoded
    @POST("sharedealrequest")
    Call<ResponseBody> hitShareDealApi(@FieldMap HashMap<String, String> map);

    @FormUrlEncoded
    @POST("blockdeal")
    Call<ResponseBody> hitBlockDealApi(@FieldMap HashMap<String, String> map);

    @FormUrlEncoded
    @POST("reportdeal")
    Call<ResponseBody> hitReportDealApi(@FieldMap HashMap<String, String> map);

    @FormUrlEncoded
    @POST("friend")
    Call<ResponseBody> hitRequestFriendApi(@FieldMap HashMap<String, String> map);

    @FormUrlEncoded
    @POST("removefriend")
    Call<ResponseBody> hitRemoveFriendApi(@FieldMap HashMap<String, String> map);

    @FormUrlEncoded
    @POST("friendrequest")
    Call<ResponseBody> hitAcceptRejectApi(@FieldMap HashMap<String, String> map);

    @FormUrlEncoded
    @POST("sendbuddyrequest")
    Call<ResponseBody> hitRequestBuddyApi(@FieldMap HashMap<String, String> map);

    @FormUrlEncoded
    @POST("saveratereview")
    Call<ResponseBody> hitRatingAndReviewApi(@FieldMap HashMap<String, String> map);

    @FormUrlEncoded
    @POST("savefeedback")
    Call<ResponseBody> hitFeedbackApi(@FieldMap HashMap<String, String> map);

    @FormUrlEncoded
    @POST("orderupdatebuddy")
    Call<ResponseBody> hitUpdateOrderStatusApi(@FieldMap HashMap<String, String> map);

    @FormUrlEncoded
    @POST("producthunt")
    Call<ResponseBody> hitProductHuntApi(@FieldMap HashMap<String, String> map);

    @FormUrlEncoded
    @POST("editproducthunt")
    Call<ResponseBody> hitEditProductHuntApi(@FieldMap HashMap<String, String> map);

    @FormUrlEncoded
    @POST("sendemaillink")
    Call<ResponseBody> hitResendLinkApi(@FieldMap HashMap<String, String> map);

    @FormUrlEncoded
    @POST("generatechecksum")
    Call<ResponseBody> hitGenerateChecksumApi(@FieldMap HashMap<String, String> map);

    @FormUrlEncoded
    @POST("updatereadcount")
    Call<ResponseBody> hitUpdateCountApi(@FieldMap HashMap<String, String> map);

    @FormUrlEncoded
    @POST("deletehunt")
    Call<ResponseBody> hitDeleteHuntApi(@FieldMap HashMap<String, String> map);

    @FormUrlEncoded
    @POST("closehunt")
    Call<ResponseBody> hitCloseHuntApi(@FieldMap HashMap<String, String> map);

    @FormUrlEncoded
    @POST("checkversion")
    Call<ResponseBody> hitCheckVersionApi(@FieldMap HashMap<String, String> map);

    @FormUrlEncoded
    @POST("sendinvitesms")
    Call<ResponseBody> hitSendMessageApi(@FieldMap HashMap<String, String> map);



    //PUT queries

    @FormUrlEncoded
    @PUT("logout")
    Call<ResponseBody> hitLogoutApi(@FieldMap HashMap<String, String> map);



    //  GET queries

    @GET("banner")
    Call<ResponseBody> hitBannerApi(@QueryMap HashMap<String, String> map);

    @GET("categories")
    Call<ResponseBody> hitCategoriesListApi(@QueryMap HashMap<String, String> map);

    @GET("deals")
    Call<ResponseBody> hitProductsDealsApi(@QueryMap HashMap<String, String> map);

    @GET("deals")
    Call<ResponseBody> hitProductServiceDetailsApi(@QueryMap HashMap<String, String> map);

    @GET("Myprofile")
    Call<ResponseBody> hitProfileDataApi(@QueryMap HashMap<String, String> map);

    @GET("getlocation")
    Call<ResponseBody> hitGetAddressesApi(@QueryMap HashMap<String, String> map);

    @GET("getshopperbuddy")
    Call<ResponseBody> hitBuddyListApi(@QueryMap HashMap<String, String> map);

    @GET("getshopperorder")
    Call<ResponseBody> hitOrdersListApi(@QueryMap HashMap<String, String> map);

    @GET("contacts")
    Call<ResponseBody> contacts(@QueryMap HashMap<String, String> map);

    @GET("getstore")
    Call<ResponseBody> hitStoreListApi(@QueryMap HashMap<String, String> map);

    @GET("getbuddydetail")
    Call<ResponseBody> hitBuddyDetailsListApi(@QueryMap HashMap<String, String> map);

    @GET("getdealbuddy")
    Call<ResponseBody> hitBuddyDealsApi(@QueryMap HashMap<String, String> map);

    @GET("searches")
    Call<ResponseBody> hitSearchListApi(@QueryMap HashMap<String, String> map);

    @GET("getuserfriend")
    Call<ResponseBody> hitMyFriendsListApi(@QueryMap HashMap<String, String> map);

    @GET("getwalletdetail")
    Call<ResponseBody> hitWalletDetailsApi(@QueryMap HashMap<String, String> map);

    @GET("subcategory")
    Call<ResponseBody> hitGetSubCategoryListApi(@QueryMap HashMap<String, String> map);

    @GET("getusernotification")
    Call<ResponseBody> hitGetNotificationListApi(@QueryMap HashMap<String, String> map);

    @GET("categories")
    Call<ResponseBody> hitGetPreferredCategoryListApi(@QueryMap HashMap<String, String> map);

    @GET("clearsearch")
    Call<ResponseBody> hitClearListApi(@QueryMap HashMap<String, String> map);

    @GET("getdistance")
    Call<ResponseBody> hitGetRangeApi(@QueryMap HashMap<String, String> map);

    @GET("chekuseremail")
    Call<ResponseBody> hitEmailValidateApi(@QueryMap HashMap<String, String> map);

    @GET("getblockdeals")
    Call<ResponseBody> hitBlockDealsApi(@QueryMap HashMap<String, String> map);

    @GET("getshopperhunt")
    Call<ResponseBody> hitProductHuntListApi(@QueryMap HashMap<String, String> map);

    @GET("getorderdetail")
    Call<ResponseBody> hitOrderDetailsApi(@QueryMap HashMap<String, String> map);

    @GET("getratereview")
    Call<ResponseBody> hitGetRatingAndReviewApi(@QueryMap HashMap<String, String> map);

    @GET("gethuntdetail")
    Call<ResponseBody> hitGetHuntDetailsApi(@QueryMap HashMap<String, String> map);

    @GET("gethuntbuddy")
    Call<ResponseBody> hitGetHuntBuddiesApi(@QueryMap HashMap<String, String> map);

    @GET("getnotificationcount")
    Call<ResponseBody> hitGetUnreadCountApi(@QueryMap HashMap<String, String> map);

    @GET("getorderquantity")
    Call<ResponseBody> hitGetProductQuantityApi(@QueryMap HashMap<String, String> map);


}
