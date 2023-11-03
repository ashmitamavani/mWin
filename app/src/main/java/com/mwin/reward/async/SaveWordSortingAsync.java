package com.mwin.reward.async;

import android.app.Activity;
import android.os.Build;
import android.provider.Settings;

import com.google.firebase.inappmessaging.FirebaseInAppMessaging;
import com.google.gson.Gson;
import com.mwin.reward.R;
import com.mwin.reward.activity.WordStoringActivity;
import com.mwin.reward.async.models.ApisResponse;
import com.mwin.reward.async.models.WordSortingResponseModel;
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

public class SaveWordSortingAsync {
    private Activity activity;
    private JSONObject jObject;
    private AESCipher cipher;

    public SaveWordSortingAsync(final Activity activity, String point) {
        this.activity = activity;
        cipher = new AESCipher();
        try {
            CommonMethodsUtils.showProgressLoader(activity);
            jObject = new JSONObject();
            jObject.put("CRIJQZ", SharePreference.getInstance().getString(SharePreference.userId));
            jObject.put("KH7K18", SharePreference.getInstance().getString(SharePreference.userToken));
            jObject.put("KBB0HY", point);
            jObject.put("D0O4XG", Build.MODEL);
            jObject.put("QT7I9W", SharePreference.getInstance().getString(SharePreference.AdID));
            jObject.put("DFGE4T", CommonMethodsUtils.verifyInstallerId(activity));
            jObject.put("FGHRTY5", SharePreference.getInstance().getString(SharePreference.AppVersion));
            jObject.put("FGHFGH54Y", SharePreference.getInstance().getInt(SharePreference.totalOpen));
            jObject.put("GFGFH", SharePreference.getInstance().getInt(SharePreference.todayOpen));
            jObject.put("4354RTE", Settings.Secure.getString(activity.getContentResolver(), Settings.Secure.ANDROID_ID));
            AppLogger.getInstance().e("Save Word Sorting ORIGINAL11 ==>", jObject.toString());
            AppLogger.getInstance().e("Save Word Sorting ENCRYPTED ==>", cipher.bytesToHex(cipher.encrypt(jObject.toString())));
            int n = CommonMethodsUtils.getRandomNumberBetweenRange(1, 1000000);
            jObject.put("RANDOM", n);
            WebApisInterface apiService = WebApisClient.getClient().create(WebApisInterface.class);
            Call<ApisResponse> call = apiService.saveWordSorting(SharePreference.getInstance().getString(SharePreference.userToken), String.valueOf(n), cipher.bytesToHex(cipher.encrypt(jObject.toString())));
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
            WordSortingResponseModel responseModel = new Gson().fromJson(new String(cipher.decrypt(response.getEncrypt())), WordSortingResponseModel.class);
//            AppLogger.getInstance().e("Save Word Sorting RESPONSE ==>", "" + new Gson().toJson(responseModel));
            if (responseModel.getStatus().equals(Constants.STATUS_LOGOUT)) {
                CommonMethodsUtils.doLogout(activity);
            } else {
                AdsUtil.adFailUrl = responseModel.getAdFailUrl();
                SharePreference.getInstance().putInt(SharePreference.LastSpinIndex, -1);
                if (!CommonMethodsUtils.isStringNullOrEmpty(responseModel.getUserToken())) {
                    SharePreference.getInstance().putString(SharePreference.userToken, responseModel.getUserToken());
                }
                if (responseModel.getStatus().equals(Constants.STATUS_SUCCESS) || responseModel.getStatus().equals("2")) {
                    ((WordStoringActivity) activity).changeCountDataValues(responseModel);
                } else if (responseModel.getStatus().equals(Constants.STATUS_ERROR)) {
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
