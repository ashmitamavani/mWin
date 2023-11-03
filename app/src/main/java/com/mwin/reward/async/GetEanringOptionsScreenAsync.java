package com.mwin.reward.async;

import android.app.Activity;
import android.os.Build;
import android.provider.Settings;

import com.google.firebase.inappmessaging.FirebaseInAppMessaging;
import com.google.gson.Gson;
import com.mwin.reward.R;
import com.mwin.reward.activity.EarningOptionsActivity;
import com.mwin.reward.async.models.ApisResponse;
import com.mwin.reward.async.models.EarningOptionsScreenModel;
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

public class GetEanringOptionsScreenAsync {
    private Activity activity;
    private JSONObject jObject;
    private AESCipher cipher;

    public GetEanringOptionsScreenAsync(final Activity activity) {
        this.activity = activity;
        cipher = new AESCipher();
        try {
            CommonMethodsUtils.showProgressLoader(activity);
            jObject = new JSONObject();
            jObject.put("HJOD65", SharePreference.getInstance().getString(SharePreference.userId));
            jObject.put("ASDASD", SharePreference.getInstance().getString(SharePreference.userToken));
            jObject.put("GSENH5", Settings.Secure.getString(activity.getContentResolver(), Settings.Secure.ANDROID_ID));
            jObject.put("KBN8PD", SharePreference.getInstance().getString(SharePreference.FCMregId));
            jObject.put("KUDWD5", SharePreference.getInstance().getString(SharePreference.AdID));
            jObject.put("0YGTGF", Build.MODEL);
            jObject.put("BNVBN", Build.VERSION.RELEASE);
            jObject.put("EQFCCP", SharePreference.getInstance().getString(SharePreference.AppVersion));
            jObject.put("9OZOSC", SharePreference.getInstance().getInt(SharePreference.totalOpen));
            jObject.put("RWQAPJ", SharePreference.getInstance().getInt(SharePreference.todayOpen));
            jObject.put("DFGDFG", CommonMethodsUtils.verifyInstallerId(activity));
            AppLogger.getInstance().e("Reward Screen DATA --)", "" + jObject.toString());
            AppLogger.getInstance().e("Reward Screen DATA ENCRYPTED --)", "" + cipher.bytesToHex(cipher.encrypt(jObject.toString())));
            int n = CommonMethodsUtils.getRandomNumberBetweenRange(1, 1000000);
            jObject.put("RANDOM", n);
            WebApisInterface apiService = WebApisClient.getClient().create(WebApisInterface.class);
            Call<ApisResponse> call = apiService.getRewardScreenData(SharePreference.getInstance().getString(SharePreference.userToken), String.valueOf(n), cipher.bytesToHex(cipher.encrypt(jObject.toString())));
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
            CommonMethodsUtils.dismissProgressLoader();
            e.printStackTrace();
        }
    }

    private void onPostExecute(ApisResponse response) {
        try {
            CommonMethodsUtils.dismissProgressLoader();
            EarningOptionsScreenModel responseModel = new Gson().fromJson(new String(cipher.decrypt(response.getEncrypt())), EarningOptionsScreenModel.class);
            //AppLogger.getInstance().e("RESPONSE", "" + new Gson().toJson(responseModel));
            if (responseModel.getStatus().equals(Constants.STATUS_LOGOUT)) {
                CommonMethodsUtils.doLogout(activity);
            } else {
                AdsUtil.adFailUrl = responseModel.getAdFailUrl();
                if (!CommonMethodsUtils.isStringNullOrEmpty(responseModel.getUserToken())) {
                    SharePreference.getInstance().putString(SharePreference.userToken, responseModel.getUserToken());
                }
                if (!CommonMethodsUtils.isStringNullOrEmpty(responseModel.getEarningPoint())) {
                    SharePreference.getInstance().putString(SharePreference.EarnedPoints, responseModel.getEarningPoint());
                }
                if (responseModel.getStatus().equals(Constants.STATUS_SUCCESS)) {
                    if (activity instanceof EarningOptionsActivity) {
                        ((EarningOptionsActivity) activity).setData(responseModel);
                    }
                } else if (responseModel.getStatus().equals(Constants.STATUS_ERROR)) {
                    CommonMethodsUtils.Notify(activity, activity.getString(R.string.app_name), responseModel.getMessage(), false);
                } else if (responseModel.getStatus().equals("2")) { // not login
                    if (activity instanceof EarningOptionsActivity) {
                        ((EarningOptionsActivity) activity).setData(responseModel);
                    }
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
