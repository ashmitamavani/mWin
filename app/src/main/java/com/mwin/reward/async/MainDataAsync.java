package com.mwin.reward.async;

import android.app.Activity;
import android.os.Build;
import android.provider.Settings;

import com.google.firebase.inappmessaging.FirebaseInAppMessaging;
import com.google.gson.Gson;
import com.mwin.reward.R;
import com.mwin.reward.activity.HomeActivity;
import com.mwin.reward.async.models.ApisResponse;
import com.mwin.reward.async.models.MainResponseModel;
import com.mwin.reward.network.WebApisClient;
import com.mwin.reward.network.WebApisInterface;
import com.mwin.reward.utils.AESCipher;
import com.mwin.reward.utils.AdsUtil;
import com.mwin.reward.utils.CommonMethodsUtils;
import com.mwin.reward.utils.SharePreference;
import com.mwin.reward.value.Constants;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainDataAsync {
    private Activity activity;
    private JSONObject jObject;
    private AESCipher cipher;

    public MainDataAsync(final Activity activity) {
        this.activity = activity;
        cipher = new AESCipher();
        try {
            CommonMethodsUtils.showProgressLoader(activity);
            jObject = new JSONObject();
            jObject.put("J8LO8H", SharePreference.getInstance().getString(SharePreference.userId));
            jObject.put("TAMATU", SharePreference.getInstance().getString(SharePreference.userToken));
            jObject.put("AVW44X", Settings.Secure.getString(activity.getContentResolver(), Settings.Secure.ANDROID_ID));
            jObject.put("GKD6MP", SharePreference.getInstance().getString(SharePreference.FCMregId));
            jObject.put("WX4OGO", SharePreference.getInstance().getString(SharePreference.AdID));
            jObject.put("YDHDTM", Build.MODEL);
            jObject.put("YEH0AC", Build.VERSION.RELEASE);
            jObject.put("VMOJ19", SharePreference.getInstance().getString(SharePreference.AppVersion));
            jObject.put("WVYKWW", SharePreference.getInstance().getInt(SharePreference.totalOpen));
            jObject.put("NB6V3W", SharePreference.getInstance().getInt(SharePreference.todayOpen));
            jObject.put("GO8PLO", CommonMethodsUtils.verifyInstallerId(activity));
//            jObject.put("deviseId", Settings.Secure.getString(activity.getContentResolver(), Settings.Secure.ANDROID_ID));
            //AppLogger.getInstance().e("HomeDataObject ORIGINAL--)", "" + jObject.toString());
            //AppLogger.getInstance().e("HomeDataObject ENCRYPTED--)", "" + new AESCipher().encrypt( jObject.toString()));
            int n = CommonMethodsUtils.getRandomNumberBetweenRange(1, 1000000);
            jObject.put("RANDOM", n);
            WebApisInterface apiService = WebApisClient.getClient().create(WebApisInterface.class);
            Call<ApisResponse> call = apiService.getHomeData(SharePreference.getInstance().getString(SharePreference.userToken), String.valueOf(n), cipher.bytesToHex(cipher.encrypt(jObject.toString())));
            call.enqueue(new Callback<ApisResponse>() {
                @Override
                public void onResponse(Call<ApisResponse> call, Response<ApisResponse> response) {
                    onPostExecute(response.body(), activity);
                }

                @Override
                public void onFailure(Call<ApisResponse> call, Throwable t) {
                    CommonMethodsUtils.dismissProgressLoader();
                    if (!call.isCanceled()) {
                        CommonMethodsUtils.Notify(activity, activity.getString(R.string.app_name), Constants.msg_Service_Error, false);
                    }
                }
            });
        } catch (Exception e) {
            CommonMethodsUtils.dismissProgressLoader();
            e.printStackTrace();
        }
    }

    private void onPostExecute(ApisResponse response, Activity activity) {
        try {
            CommonMethodsUtils.dismissProgressLoader();
            MainResponseModel responseModel = new Gson().fromJson(new String(cipher.decrypt(response.getEncrypt())), MainResponseModel.class);
            if (responseModel.getStatus().equals(Constants.STATUS_LOGOUT)) {
                CommonMethodsUtils.doLogout(activity);
            } else {
                AdsUtil.adFailUrl = responseModel.getAdFailUrl();
                if (!CommonMethodsUtils.isStringNullOrEmpty(responseModel.getUserToken())) {
                    SharePreference.getInstance().putString(SharePreference.userToken, responseModel.getUserToken());
                }
                if (responseModel.getStatus().equals(Constants.STATUS_SUCCESS)) {
                    SharePreference.getInstance().putString(SharePreference.isShowWhatsAppAuth, responseModel.getIsShowWhatsAppAuth());
                    if (!CommonMethodsUtils.isStringNullOrEmpty(responseModel.getEarningPoint())) {
                        SharePreference.getInstance().putString(SharePreference.EarnedPoints, responseModel.getEarningPoint());
                    }
                    SharePreference.getInstance().putString(SharePreference.fakeEarningPoint, responseModel.getFakeEarningPoint());
                    SharePreference.getInstance().putString(SharePreference.HomeData, new Gson().toJson(responseModel));
                    ((HomeActivity) activity).setHomeData();
                } else if (responseModel.getStatus().equals(Constants.STATUS_ERROR)) {
                    CommonMethodsUtils.Notify(activity, activity.getString(R.string.app_name), responseModel.getMessage(), false);
                } else if (responseModel.getStatus().equals("2")) {
                    CommonMethodsUtils.Notify(activity, activity.getString(R.string.app_name), responseModel.getMessage(), false);
                }
                if (!CommonMethodsUtils.isStringNullOrEmpty(responseModel.getTigerInApp())) {
                    FirebaseInAppMessaging.getInstance().triggerEvent(responseModel.getTigerInApp());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
