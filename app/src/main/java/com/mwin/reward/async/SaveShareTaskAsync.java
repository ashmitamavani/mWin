package com.mwin.reward.async;

import android.app.Activity;
import android.os.Build;
import android.provider.Settings;

import com.google.firebase.inappmessaging.FirebaseInAppMessaging;
import com.google.gson.Gson;
import com.mwin.reward.R;
import com.mwin.reward.activity.TaskInfoActivity;
import com.mwin.reward.async.models.ApisResponse;
import com.mwin.reward.async.models.ReferResponseModel;
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

public class SaveShareTaskAsync {
    private Activity activity;
    private JSONObject jObject;
    private AESCipher cipher;
    private String type;

    public SaveShareTaskAsync(final Activity activity, String taskId, String type) {
        this.activity = activity;
        cipher = new AESCipher();
        this.type = type;
        try {
            CommonMethodsUtils.showProgressLoader(activity);
            jObject = new JSONObject();
            jObject.put("JKHJKH", taskId);
            jObject.put("XCVXC", SharePreference.getInstance().getString(SharePreference.userId));
            jObject.put("FHGFGH", SharePreference.getInstance().getString(SharePreference.userToken));
            jObject.put("IOIUOU", SharePreference.getInstance().getString(SharePreference.FCMregId));
            jObject.put("SERSRF", SharePreference.getInstance().getInt(SharePreference.totalOpen));
            jObject.put("3WRWER", SharePreference.getInstance().getInt(SharePreference.todayOpen));
            jObject.put("JKLJL", SharePreference.getInstance().getString(SharePreference.AdID));
            jObject.put("VBNTTY", SharePreference.getInstance().getString(SharePreference.AppVersion));
            jObject.put("NBMBNM", Build.MODEL);
            jObject.put("SDF343S", Settings.Secure.getString(activity.getContentResolver(), Settings.Secure.ANDROID_ID));
            int n = CommonMethodsUtils.getRandomNumberBetweenRange(1, 1000000);
            jObject.put("RANDOM", n);

            AppLogger.getInstance().e("Get User Profile ORIGINAL ==>", jObject.toString());
            AppLogger.getInstance().e("Get User Profile ENCRYPTED ==>", cipher.bytesToHex(cipher.encrypt(jObject.toString())));

            WebApisInterface apiService = WebApisClient.getClient().create(WebApisInterface.class);
            Call<ApisResponse> call = apiService.shareTaskOffer(SharePreference.getInstance().getString(SharePreference.userToken), String.valueOf(n), cipher.bytesToHex(cipher.encrypt(jObject.toString())));
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
            ReferResponseModel responseModel = new Gson().fromJson(new String(cipher.decrypt(response.getEncrypt())), ReferResponseModel.class);
            if (responseModel.getStatus().equals(Constants.STATUS_LOGOUT)) {
                CommonMethodsUtils.doLogout(activity);
            } else {
                AdsUtil.adFailUrl = responseModel.getAdFailUrl();
                if (!CommonMethodsUtils.isStringNullOrEmpty(responseModel.getUserToken())) {
                    SharePreference.getInstance().putString(SharePreference.userToken, responseModel.getUserToken());
                }
                if (responseModel.getStatus().equals(Constants.STATUS_SUCCESS)) {
                    if (activity instanceof TaskInfoActivity) {
                        responseModel.setType(type);
                        ((TaskInfoActivity) activity).saveShareTaskOffer(responseModel);
                    }
                } else {
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
