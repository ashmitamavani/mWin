package com.mwin.reward.async;

import android.app.Activity;
import android.os.Build;
import android.provider.Settings;

import com.google.firebase.inappmessaging.FirebaseInAppMessaging;
import com.google.gson.Gson;
import com.mwin.reward.R;
import com.mwin.reward.activity.EverydayCheckinActivity;
import com.mwin.reward.async.models.ApisResponse;
import com.mwin.reward.async.models.EverydayBonusDataModel;
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

public class SaveEverydayCheckinAsync {
    private Activity activity;
    private JSONObject jObject;
    private AESCipher cipher;

    public SaveEverydayCheckinAsync(final Activity activity, String point, String day) {
        this.activity = activity;
        cipher = new AESCipher();
        try {
            CommonMethodsUtils.showProgressLoader(activity);
            jObject = new JSONObject();
            jObject.put("UGW388", SharePreference.getInstance().getString(SharePreference.userId));
            jObject.put("5P7W7W", SharePreference.getInstance().getString(SharePreference.userToken));
            jObject.put("WBBWTN", point);
            jObject.put("6WO7OL", day);
            jObject.put("IPP4PH", SharePreference.getInstance().getString(SharePreference.AdID));
            jObject.put("WERTV", SharePreference.getInstance().getInt(SharePreference.totalOpen));
            jObject.put("YHUOOP", SharePreference.getInstance().getInt(SharePreference.todayOpen));
            jObject.put("60CMST", SharePreference.getInstance().getString(SharePreference.AppVersion));
            jObject.put("KXWIDG", Build.MODEL);
            jObject.put("3I8T59", Build.VERSION.RELEASE);
            jObject.put("GQ0OGF", Settings.Secure.getString(activity.getContentResolver(), Settings.Secure.ANDROID_ID));
            int n = CommonMethodsUtils.getRandomNumberBetweenRange(1, 1000000);
            jObject.put("RANDOM", n);
//            AppLogger.getInstance().e("Save Daily Bonus ORIGINAL ==>", jObject.toString());
//            AppLogger.getInstance().e("Save Daily Bonus LIST ENCRYPTED ==>", cipher.bytesToHex(cipher.encrypt(jObject.toString())));
            WebApisInterface apiService = WebApisClient.getClient().create(WebApisInterface.class);
            Call<ApisResponse> call = apiService.saveDailyBonus(SharePreference.getInstance().getString(SharePreference.userToken), String.valueOf(n), cipher.bytesToHex(cipher.encrypt(jObject.toString())));
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
            EverydayBonusDataModel responseModel = new Gson().fromJson(new String(cipher.decrypt(response.getEncrypt())), EverydayBonusDataModel.class);
//            AppLogger.getInstance().e("DAILY LOGIN DADA===", new Gson().toJson(responseModel));
            if (responseModel.getStatus().equals(Constants.STATUS_LOGOUT)) {
                CommonMethodsUtils.doLogout(activity);
            } else {
                AdsUtil.adFailUrl = responseModel.getAdFailUrl();
                if (!CommonMethodsUtils.isStringNullOrEmpty(responseModel.getUserToken())) {
                    SharePreference.getInstance().putString(SharePreference.userToken, responseModel.getUserToken());
                }
                if (responseModel.getStatus().equals(Constants.STATUS_SUCCESS) || responseModel.getStatus().equals("2") || responseModel.getStatus().equals("3")) {
                    ((EverydayCheckinActivity) activity).onDailyLoginDataChanged(responseModel);
                } else if (responseModel.getStatus().equals(Constants.STATUS_ERROR)) {
                    ((EverydayCheckinActivity) activity).showErrorMessage("15-Days Streak", responseModel.getMessage());
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
