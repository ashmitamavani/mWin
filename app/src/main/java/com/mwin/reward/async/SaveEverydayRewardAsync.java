package com.mwin.reward.async;

import android.app.Activity;
import android.os.Build;
import android.provider.Settings;

import com.google.firebase.inappmessaging.FirebaseInAppMessaging;
import com.google.gson.Gson;
import com.mwin.reward.R;
import com.mwin.reward.activity.EverydayRewardActivity;
import com.mwin.reward.async.models.ApisResponse;
import com.mwin.reward.async.models.EverydayRewardResponseModel;
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

public class SaveEverydayRewardAsync {
    private Activity activity;
    private JSONObject jObject;
    private AESCipher cipher;

    public SaveEverydayRewardAsync(final Activity activity) {
        this.activity = activity;
        cipher = new AESCipher();
        try {
            CommonMethodsUtils.showProgressLoader(activity);
            jObject = new JSONObject();
            jObject.put("OYEWW7", SharePreference.getInstance().getString(SharePreference.userId));
            jObject.put("UFAOZT", SharePreference.getInstance().getString(SharePreference.userToken));
            jObject.put("W7ORH7", SharePreference.getInstance().getString(SharePreference.FCMregId));

            jObject.put("1NOOHN", SharePreference.getInstance().getString(SharePreference.AdID));
            jObject.put("E794EO", Build.MODEL);
            jObject.put("345334", Build.VERSION.RELEASE);
            jObject.put("KQW51A", SharePreference.getInstance().getString(SharePreference.AppVersion));
            jObject.put("1OOWDT", SharePreference.getInstance().getInt(SharePreference.totalOpen));
            jObject.put("GT433O", SharePreference.getInstance().getInt(SharePreference.todayOpen));
            jObject.put("3453455", CommonMethodsUtils.verifyInstallerId(activity));
            jObject.put("W9SWFE", Settings.Secure.getString(activity.getContentResolver(), Settings.Secure.ANDROID_ID));

            int n = CommonMethodsUtils.getRandomNumberBetweenRange(1, 1000000);
            jObject.put("RANDOM", n);
            WebApisInterface apiService = WebApisClient.getClient().create(WebApisInterface.class);
            //AppLogger.getInstance().e("Save Daily Reward ORIGINAL ==>", jObject.toString());
            //AppLogger.getInstance().e("Save Daily Reward ENCRYPTED ==>", new AESCipher().encrypt( jObject.toString()));
            Call<ApisResponse> call = apiService.saveDailyReward(SharePreference.getInstance().getString(SharePreference.userToken), String.valueOf(n), cipher.bytesToHex(cipher.encrypt(jObject.toString())));
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
            EverydayRewardResponseModel responseModel = new Gson().fromJson(new String(cipher.decrypt(response.getEncrypt())), EverydayRewardResponseModel.class);
            //AppLogger.getInstance().e("RESPONSE: ", "" + new Gson().toJson(responseModel));
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
                    ((EverydayRewardActivity) activity).updateData(responseModel);
                } else if (!CommonMethodsUtils.isStringNullOrEmpty(responseModel.getMessage())) {
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
