package com.mwin.reward.async;

import android.app.Activity;
import android.os.Build;
import android.provider.Settings;

import com.google.firebase.inappmessaging.FirebaseInAppMessaging;
import com.google.gson.Gson;
import com.mwin.reward.R;
import com.mwin.reward.activity.TypeTextGameActivity;
import com.mwin.reward.async.models.ApisResponse;
import com.mwin.reward.async.models.TypeTextDataModel;
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

public class SaveTypeTextGameAsync {
    private Activity activity;
    private JSONObject jObject;
    private AESCipher cipher;

    public SaveTypeTextGameAsync(final Activity activity, String point, String id, String text) {
        this.activity = activity;
        cipher = new AESCipher();
        try {
            CommonMethodsUtils.showProgressLoader(activity);
            jObject = new JSONObject();
            jObject.put("HKKCLJ", SharePreference.getInstance().getString(SharePreference.userId));
            jObject.put("6XK9NG", SharePreference.getInstance().getString(SharePreference.userToken));
            jObject.put("WQH6TE", point);
            jObject.put("P9KZCH", text);
            jObject.put("VNMW5W", id);
            jObject.put("UQNWC8", SharePreference.getInstance().getString(SharePreference.AdID));
            jObject.put("IHHNH3", Build.MODEL);
            jObject.put("DF435EF", SharePreference.getInstance().getInt(SharePreference.totalOpen));
            jObject.put("ASDF34", SharePreference.getInstance().getInt(SharePreference.todayOpen));
            jObject.put("SDFS333", SharePreference.getInstance().getString(SharePreference.AppVersion));
            jObject.put("SDFW3", Settings.Secure.getString(activity.getContentResolver(), Settings.Secure.ANDROID_ID));
            //AppLogger.getInstance().e("Save Spin ORIGINAL ==>", jObject.toString());
            int n = CommonMethodsUtils.getRandomNumberBetweenRange(1, 1000000);
            jObject.put("RANDOM", n);
            AppLogger.getInstance().e("Text Typing ENCRYPTED ==>", cipher.bytesToHex(cipher.encrypt(jObject.toString())));
            WebApisInterface apiService = WebApisClient.getClient().create(WebApisInterface.class);
            Call<ApisResponse> call = apiService.saveTextTyping(SharePreference.getInstance().getString(SharePreference.userToken), String.valueOf(n), cipher.bytesToHex(cipher.encrypt(jObject.toString())));
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
            TypeTextDataModel responseModel = new Gson().fromJson(new String(cipher.decrypt(response.getEncrypt())), TypeTextDataModel.class);
            //AppLogger.getInstance().e("Save Spin RESPONSE ==>", "" + new Gson().toJson(responseModel));
            if (responseModel.getStatus().equals(Constants.STATUS_LOGOUT)) {
                CommonMethodsUtils.doLogout(activity);
            } else {
                AdsUtil.adFailUrl = responseModel.getAdFailUrl();
                if (!CommonMethodsUtils.isStringNullOrEmpty(responseModel.getUserToken())) {
                    SharePreference.getInstance().putString(SharePreference.userToken, responseModel.getUserToken());
                }
                if (responseModel.getStatus().equals(Constants.STATUS_SUCCESS)) {
                    ((TypeTextGameActivity) activity).changeTextTypingDataValues(responseModel);
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
