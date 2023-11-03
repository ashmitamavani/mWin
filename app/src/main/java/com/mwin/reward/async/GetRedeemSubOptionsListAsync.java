package com.mwin.reward.async;

import android.app.Activity;
import android.os.Build;
import android.provider.Settings;

import com.google.firebase.inappmessaging.FirebaseInAppMessaging;
import com.google.gson.Gson;
import com.mwin.reward.R;
import com.mwin.reward.activity.RedeemOptionsSubListActivity;
import com.mwin.reward.async.models.ApisResponse;
import com.mwin.reward.async.models.RedeemOptionsSubListResponseModel;
import com.mwin.reward.network.WebApisClient;
import com.mwin.reward.network.WebApisInterface;
import com.mwin.reward.utils.AESCipher;
import com.mwin.reward.utils.AdsUtil;
import com.mwin.reward.utils.AppLogger;
import com.mwin.reward.utils.CommonMethodsUtils;
import com.mwin.reward.utils.SharePreference;
import com.mwin.reward.value.Constants;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GetRedeemSubOptionsListAsync {
    private Activity activity;
    private JSONObject jObject;
    private AESCipher cipher;

    public GetRedeemSubOptionsListAsync(final Activity activity, String type) {
        this.activity = activity;
        cipher = new AESCipher();
        try {
            CommonMethodsUtils.showProgressLoader(activity);
            jObject = new JSONObject();
            jObject.put("LWIPEI", type);
            jObject.put("ERTERET", SharePreference.getInstance().getString(SharePreference.userId));
            jObject.put("FGFGH", SharePreference.getInstance().getString(SharePreference.userToken));

            jObject.put("SJOR0P", Settings.Secure.getString(activity.getContentResolver(), Settings.Secure.ANDROID_ID));
            jObject.put("VCVDC", SharePreference.getInstance().getString(SharePreference.AdID));
            jObject.put("FGDGD", Build.MODEL);
            jObject.put("KJHJHJ", Build.VERSION.RELEASE);
            jObject.put("MUJUH", SharePreference.getInstance().getString(SharePreference.AppVersion));
            jObject.put("GHJGJH", SharePreference.getInstance().getInt(SharePreference.totalOpen));
            jObject.put("HJTYFG", SharePreference.getInstance().getInt(SharePreference.todayOpen));
            jObject.put("HJGHJ", CommonMethodsUtils.verifyInstallerId(activity));
            int n = CommonMethodsUtils.getRandomNumberBetweenRange(1, 1000000);
            jObject.put("RANDOM", n);
            AppLogger.getInstance().e("Get Withdraw Type ORIGINAL ==>", jObject.toString());

            WebApisInterface apiService = WebApisClient.getClient().create(WebApisInterface.class);
            Call<ApisResponse> call = apiService.getWithdrawTypeList(SharePreference.getInstance().getString(SharePreference.userToken), String.valueOf(n), jObject.toString());
            call.enqueue(new Callback<ApisResponse>() {
                @Override
                public void onResponse(Call<ApisResponse> call, Response<ApisResponse> response) {
                    onPostExecute(response.body());
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
            e.printStackTrace();
            CommonMethodsUtils.dismissProgressLoader();
        }
    }

    private void onPostExecute(ApisResponse response) {
        try {
            CommonMethodsUtils.dismissProgressLoader();
            RedeemOptionsSubListResponseModel responseModel = new Gson().fromJson(new String(cipher.decrypt(response.getEncrypt())), RedeemOptionsSubListResponseModel.class);
            if (responseModel.getStatus().equals(Constants.STATUS_LOGOUT)) {
                CommonMethodsUtils.doLogout(activity);
            } else {
                AdsUtil.adFailUrl = responseModel.getAdFailUrl();
                if (!CommonMethodsUtils.isStringNullOrEmpty(responseModel.getUserToken())) {
                    SharePreference.getInstance().putString(SharePreference.userToken, responseModel.getUserToken());
                }
                if (responseModel.getStatus().equals(Constants.STATUS_SUCCESS)) {
                    if (activity instanceof RedeemOptionsSubListActivity) {
                        ((RedeemOptionsSubListActivity) activity).setData(responseModel);
                    }
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
