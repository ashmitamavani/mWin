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

public class GetEverydayRewardAsync {
    private Activity activity;
    private JSONObject jObject;
    private AESCipher cipher;

    public GetEverydayRewardAsync(final Activity activity) {
        this.activity = activity;
        cipher = new AESCipher();
        try {
            CommonMethodsUtils.showProgressLoader(activity);
            jObject = new JSONObject();
            jObject.put("3WGY5S", SharePreference.getInstance().getString(SharePreference.userId));
            jObject.put("QWEQD", SharePreference.getInstance().getString(SharePreference.userToken));
            jObject.put("5Z8HCP", SharePreference.getInstance().getString(SharePreference.FCMregId));

            jObject.put("WMWZ3L", SharePreference.getInstance().getString(SharePreference.AdID));
            jObject.put("DZJOHH", Build.MODEL);
            jObject.put("WERTE45", Build.VERSION.RELEASE);
            jObject.put("VEEZWC", SharePreference.getInstance().getString(SharePreference.AppVersion));
            jObject.put("O862OY", SharePreference.getInstance().getInt(SharePreference.totalOpen));
            jObject.put("OHCZ71", SharePreference.getInstance().getInt(SharePreference.todayOpen));
            jObject.put("567HFG", CommonMethodsUtils.verifyInstallerId(activity));
            jObject.put("0PQ807", Settings.Secure.getString(activity.getContentResolver(), Settings.Secure.ANDROID_ID));

            int n = CommonMethodsUtils.getRandomNumberBetweenRange(1, 1000000);
            jObject.put("RANDOM", n);
            WebApisInterface apiService = WebApisClient.getClient().create(WebApisInterface.class);
            //AppLogger.getInstance().e("Get Daily Reward ORIGINAL ==>", jObject.toString());
            Call<ApisResponse> call = apiService.getDailyRewardList(SharePreference.getInstance().getString(SharePreference.userToken), String.valueOf(n), jObject.toString());
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
                    ((EverydayRewardActivity) activity).setData(responseModel);
                } else if (responseModel.getStatus().equals(Constants.STATUS_ERROR)) {
                    CommonMethodsUtils.Notify(activity, activity.getString(R.string.app_name), responseModel.getMessage(), true);
                } else if (responseModel.getStatus().equals("2")) {
                    ((EverydayRewardActivity) activity).setData(responseModel);
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
