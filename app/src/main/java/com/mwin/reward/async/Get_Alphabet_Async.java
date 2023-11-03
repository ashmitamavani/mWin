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


public class Get_Alphabet_Async {
    private Activity activity;
    private JSONObject jObject;
    private AESCipher pocAesCipher;

    public Get_Alphabet_Async(final Activity activity) {
        this.activity = activity;
        pocAesCipher = new AESCipher();

        try {
            CommonMethodsUtils.showProgressLoader(activity);
            jObject = new JSONObject();
            jObject.put("AHBTK", SharePreference.getInstance().getString(SharePreference.userId));
            jObject.put("BZDFUJHY", SharePreference.getInstance().getString(SharePreference.userToken));
            jObject.put("HHTYKW", Settings.Secure.getString(activity.getContentResolver(), Settings.Secure.ANDROID_ID));
            jObject.put("ERGTJUDYH", SharePreference.getInstance().getString(SharePreference.FCMregId));
            jObject.put("DFHGDFDG", SharePreference.getInstance().getString(SharePreference.AdID));
            jObject.put("LIOYADS", Build.MODEL);
            jObject.put("NDTYNNTED", Build.VERSION.RELEASE);
            jObject.put("DFGSDGHA", SharePreference.getInstance().getString(SharePreference.AppVersion));
            jObject.put("DFHDH", SharePreference.getInstance().getInt(SharePreference.totalOpen));
            jObject.put("SDFAGDFG", SharePreference.getInstance().getInt(SharePreference.todayOpen));
            jObject.put("WYKRD", CommonMethodsUtils.verifyInstallerId(activity));
            jObject.put("HHTYKW", Settings.Secure.getString(activity.getContentResolver(), Settings.Secure.ANDROID_ID));

            int n = CommonMethodsUtils.getRandomNumberBetweenRange(1, 1000000);
            jObject.put("RANDOM", n);
            WebApisInterface apiService = WebApisClient.getClient().create(WebApisInterface.class);
//            POC_AppLogger.getInstance().e("Get Alphabet ORIGINAL ==>", jObject.toString());
//            POC_AppLogger.getInstance().e("Get Alphabet ENCRYPTED ==>",  pocAesCipher.bytesToHex(pocAesCipher.encrypt(jObject.toString())));
            Call<ApisResponse> call = apiService.getAlphabetData(SharePreference.getInstance().getString(SharePreference.userToken), String.valueOf(n),
                    pocAesCipher.bytesToHex(pocAesCipher.encrypt(jObject.toString())));
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
            Alphabet_model responseModel = new Gson().fromJson(new String(pocAesCipher.decrypt(response.getEncrypt())), Alphabet_model.class);
            if (responseModel.getStatus().equals(Constants.STATUS_LOGOUT)) {
                CommonMethodsUtils.doLogout(activity);
            } else {
                AdsUtil.adFailUrl = responseModel.getAdFailUrl();
                if (!CommonMethodsUtils.isStringNullOrEmpty(responseModel.getUserToken())) {
                    SharePreference.getInstance().putString(SharePreference.userToken, responseModel.getUserToken());
                }
                if (responseModel.getStatus().equals(Constants.STATUS_SUCCESS)) {
                    ((AToZTypeActivity) activity).setData(responseModel);
                } else if (responseModel.getStatus().equals(Constants.STATUS_ERROR)) {
                    ((AToZTypeActivity) activity).setData(responseModel);
                } else if (responseModel.getStatus().equals("2")) {
                    ((AToZTypeActivity) activity).setData(responseModel);
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
