package com.mwin.reward.async;

import android.app.Activity;
import android.os.Build;
import android.provider.Settings;

import com.google.firebase.inappmessaging.FirebaseInAppMessaging;
import com.google.gson.Gson;
import com.mwin.reward.R;
import com.mwin.reward.activity.AToZTypeActivity;
import com.mwin.reward.async.models.Alphabet_model;
import com.mwin.reward.async.models.ApisResponse;
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

public class Save_Alphabet_Async {
    private Activity activity;
    private JSONObject jObject;
    private AESCipher pocAesCipher;


    public Save_Alphabet_Async(final Activity activity, String point) {
        this.activity = activity;
        pocAesCipher = new AESCipher();

        try {
            CommonMethodsUtils.showProgressLoader(activity);
            jObject = new JSONObject();
            jObject.put("FBFBSDBFG", SharePreference.getInstance().getString(SharePreference.userId));
            jObject.put("TXFGHTRFGH", SharePreference.getInstance().getInt(SharePreference.totalOpen));
            jObject.put("SFGCS", SharePreference.getInstance().getInt(SharePreference.todayOpen));
            jObject.put("GFJGGHJ", SharePreference.getInstance().getString(SharePreference.userToken));
            jObject.put("FGUJHGF", point);
            jObject.put("SDFGJF", SharePreference.getInstance().getString(SharePreference.AdID));
            jObject.put("SGJGDUF", CommonMethodsUtils.verifyInstallerId(activity));
            jObject.put("DFHSDF", SharePreference.getInstance().getString(SharePreference.AppVersion));
            jObject.put("DGJFGBV", Build.MODEL);
            jObject.put("DJGDJHD", Settings.Secure.getString(activity.getContentResolver(), Settings.Secure.ANDROID_ID));
            jObject.put("DFHZFBDS", Settings.Secure.getString(activity.getContentResolver(), Settings.Secure.ANDROID_ID));
//            POC_AppLogger.getInstance().e("Save Alphabet ORIGINAL11 ==>", jObject.toString());
//            POC_AppLogger.getInstance().e("Save Alphabet ENCRYPTED ==>", pocAesCipher.bytesToHex(pocAesCipher.encrypt(jObject.toString())));
            int n = CommonMethodsUtils.getRandomNumberBetweenRange(1, 1000000);
            jObject.put("RANDOM", n);
            WebApisInterface apiService = WebApisClient.getClient().create(WebApisInterface.class);
            Call<ApisResponse> call = apiService.saveAlphabetData(SharePreference.getInstance().getString(SharePreference.userToken), String.valueOf(n), pocAesCipher.bytesToHex(pocAesCipher.encrypt(jObject.toString())));
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
            Alphabet_model responseModel = new Gson().fromJson(new String(pocAesCipher.decrypt(response.getEncrypt())), Alphabet_model.class);
//            POC_AppLogger.getInstance().e("Save Alphabet RESPONSE ==>", "" + new Gson().toJson(responseModel));
            if (responseModel.getStatus().equals(Constants.STATUS_LOGOUT)) {
                CommonMethodsUtils.doLogout(activity);
            } else {
                AdsUtil.adFailUrl = responseModel.getAdFailUrl();
                if (!CommonMethodsUtils.isStringNullOrEmpty(responseModel.getUserToken())) {
                    SharePreference.getInstance().putString(SharePreference.userToken, responseModel.getUserToken());
                }
                if (responseModel.getStatus().equals(Constants.STATUS_SUCCESS)) {
                    //AppLogger.getInstance().e("Save Alphabet RESPONSE ==>", "" + new Gson().toJson(responseModel));
                    ((AToZTypeActivity) activity).changeCountDataValues(responseModel);
                } else if (responseModel.getStatus().equals(Constants.STATUS_ERROR)) {
                    CommonMethodsUtils.Notify(activity, activity.getString(R.string.app_name), responseModel.getMessage(), false);
                } else if (responseModel.getStatus().equals("2")) {
                    ((AToZTypeActivity) activity).changeCountDataValues(responseModel);
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
