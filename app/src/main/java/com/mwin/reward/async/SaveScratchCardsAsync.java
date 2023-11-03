package com.mwin.reward.async;

import android.app.Activity;
import android.os.Build;
import android.provider.Settings;

import com.google.firebase.inappmessaging.FirebaseInAppMessaging;
import com.google.gson.Gson;
import com.mwin.reward.R;
import com.mwin.reward.activity.ScratchCardListActivity;
import com.mwin.reward.async.models.ApisResponse;
import com.mwin.reward.async.models.ScratchCardModel;
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

public class SaveScratchCardsAsync {
    private Activity activity;
    private JSONObject jObject;
    private AESCipher cipher;

    public SaveScratchCardsAsync(final Activity activity, String id, String points) {
        this.activity = activity;
        cipher = new AESCipher();
        try {
            CommonMethodsUtils.showProgressLoader(activity);
            jObject = new JSONObject();
            jObject.put("NRHNZH", SharePreference.getInstance().getString(SharePreference.userId));
            jObject.put("12WERT", SharePreference.getInstance().getString(SharePreference.userToken));
            jObject.put("QWE22", points);
            jObject.put("S5AIJJ", id);
            jObject.put("DSD334", SharePreference.getInstance().getInt(SharePreference.totalOpen));
            jObject.put("232EWE", SharePreference.getInstance().getInt(SharePreference.todayOpen));
            jObject.put("VLP7GD", SharePreference.getInstance().getString(SharePreference.AdID));
            jObject.put("SDF32RF", SharePreference.getInstance().getString(SharePreference.AppVersion));
            jObject.put("SDF321", Build.MODEL);
            jObject.put("SDFS3P", Build.VERSION.RELEASE);
            jObject.put("SDFS332", Settings.Secure.getString(activity.getContentResolver(), Settings.Secure.ANDROID_ID));
            //AppLogger.getInstance().e("Save ScratchCard ORIGINAL ==>", jObject.toString());
            //AppLogger.getInstance().e("Save ScratchCard ENCRYPTED ==>", new AESCipher().encrypt( jObject.toString()));
            int n = CommonMethodsUtils.getRandomNumberBetweenRange(1, 1000000);
            jObject.put("RANDOM", n);
            WebApisInterface apiService = WebApisClient.getClient().create(WebApisInterface.class);
            Call<ApisResponse> call = apiService.saveScratchcard(SharePreference.getInstance().getString(SharePreference.userToken), String.valueOf(n), cipher.bytesToHex(cipher.encrypt(jObject.toString())));
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
            ScratchCardModel responseModel = new Gson().fromJson(new String(cipher.decrypt(response.getEncrypt())), ScratchCardModel.class);
            if (responseModel.getStatus().equals(Constants.STATUS_LOGOUT)) {
                CommonMethodsUtils.doLogout(activity);
            } else {
                AdsUtil.adFailUrl = responseModel.getAdFailUrl();
                if (!CommonMethodsUtils.isStringNullOrEmpty(responseModel.getUserToken())) {
                    SharePreference.getInstance().putString(SharePreference.userToken, responseModel.getUserToken());
                }
                if (responseModel.getStatus().equals(Constants.STATUS_SUCCESS)) {
                    ((ScratchCardListActivity) activity).changeScratchCardDataValues(responseModel);
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
