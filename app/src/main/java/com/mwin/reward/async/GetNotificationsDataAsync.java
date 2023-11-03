package com.mwin.reward.async;

import android.app.Activity;
import android.os.Build;
import android.provider.Settings;

import com.google.firebase.inappmessaging.FirebaseInAppMessaging;
import com.google.gson.Gson;
import com.mwin.reward.R;
import com.mwin.reward.activity.NotificationListActivity;
import com.mwin.reward.async.models.ApisResponse;
import com.mwin.reward.async.models.NotificationsModel;
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

public class GetNotificationsDataAsync {
    private Activity activity;
    private JSONObject jObject;
    private AESCipher cipher;

    public GetNotificationsDataAsync(final Activity activity) {
        this.activity = activity;
        cipher = new AESCipher();
        try {
            CommonMethodsUtils.showProgressLoader(activity);
            jObject = new JSONObject();
            jObject.put("NN08QX", Settings.Secure.getString(activity.getContentResolver(), Settings.Secure.ANDROID_ID));
            jObject.put("TVW4LO", SharePreference.getInstance().getString(SharePreference.userId));
            jObject.put("IUOPKLK", SharePreference.getInstance().getString(SharePreference.FCMregId));
            jObject.put("GHJGGH", SharePreference.getInstance().getString(SharePreference.AdID));
            jObject.put("EDFRTG", Build.MODEL);
            jObject.put("ADHTHJ", Build.VERSION.RELEASE);
            jObject.put("GRGDDD", SharePreference.getInstance().getString(SharePreference.AppVersion));
            jObject.put("SDFVDS", SharePreference.getInstance().getInt(SharePreference.totalOpen));
            jObject.put("GBDFTR", SharePreference.getInstance().getInt(SharePreference.todayOpen));
            jObject.put("RTYUN", CommonMethodsUtils.verifyInstallerId(activity));
            int n = CommonMethodsUtils.getRandomNumberBetweenRange(1, 1000000);
            jObject.put("RANDOM", n);
            WebApisInterface apiService = WebApisClient.getClient().create(WebApisInterface.class);
//            Log.e("TaskObject", "" + jObject.toString());
            Call<ApisResponse> call = apiService.getNotificationData(SharePreference.getInstance().getString(SharePreference.userToken), String.valueOf(n), jObject.toString());
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
            NotificationsModel responseModel = new Gson().fromJson(new String(cipher.decrypt(response.getEncrypt())), NotificationsModel.class);
            //            Log.e("RESPONSE", "" + new Gson().toJson(NotificationsModel));
            if (responseModel.getStatus().equals(Constants.STATUS_LOGOUT)) {
                CommonMethodsUtils.doLogout(activity);
            } else {
                AdsUtil.adFailUrl = responseModel.getAdFailUrl();
                if (!CommonMethodsUtils.isStringNullOrEmpty(responseModel.getUserToken())) {
                    SharePreference.getInstance().putString(SharePreference.userToken, responseModel.getUserToken());
                }
                if (responseModel.getStatus().equals(Constants.STATUS_SUCCESS)) {
                    ((NotificationListActivity) activity).setData(responseModel);
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
