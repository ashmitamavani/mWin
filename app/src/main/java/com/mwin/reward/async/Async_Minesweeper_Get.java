package com.mwin.reward.async;

import android.app.Activity;
import android.os.Build;
import android.provider.Settings;

import com.google.firebase.inappmessaging.FirebaseInAppMessaging;
import com.google.gson.Gson;
import com.mwin.reward.R;
import com.mwin.reward.activity.MinesweeperActivity;
import com.mwin.reward.async.models.ApisResponse;
import com.mwin.reward.async.models.Model_MinesweeperData;
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

public class Async_Minesweeper_Get {
    private Activity activity;
    private JSONObject jObject;
    private AESCipher cipher;

    public Async_Minesweeper_Get(final Activity activity) {
        this.activity = activity;
        cipher = new AESCipher();
        try {
            CommonMethodsUtils.showProgressLoader(activity);
            jObject = new JSONObject();
            jObject.put("WERYTU", SharePreference.getInstance().getString(SharePreference.userId));
            jObject.put("ERTER", SharePreference.getInstance().getString(SharePreference.userToken));
            jObject.put("DFGDFG", SharePreference.getInstance().getString(SharePreference.FCMregId));
            jObject.put("GHJGH", SharePreference.getInstance().getString(SharePreference.AdID));
            jObject.put("KJLJKL", Build.MODEL);
            jObject.put("JKLJK", Build.VERSION.RELEASE);
            jObject.put("RTYFGH", SharePreference.getInstance().getString(SharePreference.AppVersion));
            jObject.put("HJKHJK", SharePreference.getInstance().getInt(SharePreference.totalOpen));
            jObject.put("BNGHJ", SharePreference.getInstance().getInt(SharePreference.todayOpen));
            jObject.put("BNMBMN", CommonMethodsUtils.verifyInstallerId(activity));
            jObject.put("FGFGH", Settings.Secure.getString(activity.getContentResolver(), Settings.Secure.ANDROID_ID));
            int n = CommonMethodsUtils.getRandomNumberBetweenRange(1, 1000000);
            jObject.put("RANDOM", n);
            WebApisInterface apiService = WebApisClient.getClient().create(WebApisInterface.class);
            AppLogger.getInstance().e("Get Minesweeper ORIGINAL ==>", jObject.toString());
            AppLogger.getInstance().e("Get Minesweeper ENCRYPTED ==>", cipher.bytesToHex(cipher.encrypt(jObject.toString())));
            Call<ApisResponse> call = apiService.getMinesweeper(SharePreference.getInstance().getString(SharePreference.userToken), String.valueOf(n), cipher.bytesToHex(cipher.encrypt(jObject.toString())));
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
            Model_MinesweeperData responseModel = new Gson().fromJson(new String(cipher.decrypt(response.getEncrypt())), Model_MinesweeperData.class);
            AppLogger.getInstance().e("RESPONSE11", "" + new Gson().toJson(responseModel));
            if (responseModel.getStatus().equals(Constants.STATUS_LOGOUT)) {
                CommonMethodsUtils.doLogout(activity);
            } else {
                AdsUtil.adFailUrl = responseModel.getAdFailUrl();
                if (!CommonMethodsUtils.isStringNullOrEmpty(responseModel.getUserToken())) {
                    SharePreference.getInstance().putString(SharePreference.userToken, responseModel.getUserToken());
                }
                ((MinesweeperActivity) activity).setData(responseModel);
                if (!CommonMethodsUtils.isStringNullOrEmpty(responseModel.getTigerInApp())) {
                    FirebaseInAppMessaging.getInstance().triggerEvent(responseModel.getTigerInApp());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
