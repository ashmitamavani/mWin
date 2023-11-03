package com.mwin.reward.async;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;

import com.google.firebase.inappmessaging.FirebaseInAppMessaging;
import com.google.gson.Gson;
import com.mwin.reward.ApplicationController;
import com.mwin.reward.R;
import com.mwin.reward.activity.EarningOptionsActivity;
import com.mwin.reward.async.models.ApisResponse;
import com.mwin.reward.async.models.MilestonesTargetResponseModel;
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

public class SaveDailyTargetAsync {
    private Activity activity;
    private JSONObject jObject;
    private AESCipher cipher;
    private String milestoneId;

    public SaveDailyTargetAsync(final Activity activity, String point, String milestoneId) {
        this.activity = activity;
        this.milestoneId = milestoneId;
        cipher = new AESCipher();
        try {
            CommonMethodsUtils.showProgressLoader(activity);
            jObject = new JSONObject();
            jObject.put("ASDAS", SharePreference.getInstance().getString(SharePreference.userId));
            jObject.put("234SDF", SharePreference.getInstance().getString(SharePreference.userToken));
            jObject.put("NBMGJGH", point);
            jObject.put("WERWERWR", milestoneId);

            jObject.put("SDAFSDF", SharePreference.getInstance().getInt(SharePreference.totalOpen));
            jObject.put("JKLJJJK", SharePreference.getInstance().getInt(SharePreference.todayOpen));
            jObject.put("FGHFGHF", SharePreference.getInstance().getString(SharePreference.AdID));
            jObject.put("Q234423", SharePreference.getInstance().getString(SharePreference.AppVersion));
            jObject.put("BNBMNBNMB", Build.MODEL);
            jObject.put("UIYIUYI", Settings.Secure.getString(activity.getContentResolver(), Settings.Secure.ANDROID_ID));

            int n = CommonMethodsUtils.getRandomNumberBetweenRange(1, 1000000);
            jObject.put("RANDOM", n);

            AppLogger.getInstance().e("Save Spin ORIGINAL ==>", jObject.toString());
            AppLogger.getInstance().e("DAILY TARGET ENCRYPTED ==>", cipher.bytesToHex(cipher.encrypt(jObject.toString())));
            WebApisInterface apiService = WebApisClient.getClient().create(WebApisInterface.class);
            Call<ApisResponse> call = apiService.saveDailyTarget(SharePreference.getInstance().getString(SharePreference.userToken), String.valueOf(n), cipher.bytesToHex(cipher.encrypt(jObject.toString())));
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
            MilestonesTargetResponseModel responseModel = new Gson().fromJson(new String(cipher.decrypt(response.getEncrypt())), MilestonesTargetResponseModel.class);
            AppLogger.getInstance().e("Save DAILY TARGET RESPONSE ==>", "" + new Gson().toJson(responseModel));
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
                        ((EarningOptionsActivity) activity).changeDailyTargetValues(true);
                    }
                    ApplicationController.getContext().sendBroadcast(new Intent(Constants.DAILY_TARGET_RESULT)
                            .setPackage(ApplicationController.getContext().getPackageName())
                            .putExtra("id", milestoneId)
                            .putExtra("status", Constants.STATUS_SUCCESS));
                } else if (responseModel.getStatus().equals(Constants.STATUS_ERROR) || responseModel.getStatus().equals("2")) {
                    CommonMethodsUtils.Notify(activity, activity.getString(R.string.app_name), responseModel.getMessage(), false);
                    ApplicationController.getContext().sendBroadcast(new Intent(Constants.DAILY_TARGET_RESULT)
                            .setPackage(ApplicationController.getContext().getPackageName())
                            .putExtra("id", milestoneId)
                            .putExtra("status", Constants.STATUS_ERROR));
                    if (activity instanceof EarningOptionsActivity) {
                        ((EarningOptionsActivity) activity).changeDailyTargetValues(false);
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
