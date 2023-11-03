package com.mwin.reward.async;

import android.app.Activity;
import android.os.Build;
import android.provider.Settings;

import com.google.gson.Gson;
import com.mwin.reward.R;
import com.mwin.reward.activity.EarningOptionsActivity;
import com.mwin.reward.activity.HomeActivity;
import com.mwin.reward.async.models.ApisResponse;
import com.mwin.reward.async.models.Model_MinesweeperData;
import com.mwin.reward.network.WebApisClient;
import com.mwin.reward.network.WebApisInterface;
import com.mwin.reward.utils.AESCipher;
import com.mwin.reward.utils.AppLogger;
import com.mwin.reward.utils.CommonMethodsUtils;
import com.mwin.reward.utils.SharePreference;
import com.mwin.reward.value.Constants;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GetWalletBalanceAsync {
    private Activity activity;
    private JSONObject jObject;
    private AESCipher cipher;

    public GetWalletBalanceAsync(final Activity activity) {
        this.activity = activity;
        cipher = new AESCipher();
        try {
            jObject = new JSONObject();
            jObject.put("RGFVBNH", SharePreference.getInstance().getString(SharePreference.userId));
            jObject.put("ERTERVBV", SharePreference.getInstance().getString(SharePreference.userToken));
            jObject.put("DFGDFGDFGD", SharePreference.getInstance().getString(SharePreference.FCMregId));
            jObject.put("GHJGHERTE", SharePreference.getInstance().getString(SharePreference.AdID));
            jObject.put("KJLJKLDFG", Build.MODEL);
            jObject.put("JKLJKDFG", Build.VERSION.RELEASE);
            jObject.put("RTYFGHCVB", SharePreference.getInstance().getString(SharePreference.AppVersion));
            jObject.put("HJKHJKXV", SharePreference.getInstance().getInt(SharePreference.totalOpen));
            jObject.put("BNGHJXV", SharePreference.getInstance().getInt(SharePreference.todayOpen));
            jObject.put("BNMBMNXV", CommonMethodsUtils.verifyInstallerId(activity));
            jObject.put("FGFGHXV", Settings.Secure.getString(activity.getContentResolver(), Settings.Secure.ANDROID_ID));
            int n = CommonMethodsUtils.getRandomNumberBetweenRange(1, 1000000);
            jObject.put("RANDOM", n);
            WebApisInterface apiService = WebApisClient.getClient().create(WebApisInterface.class);
            AppLogger.getInstance().e("Get Wallet Balance ORIGINAL ==>", jObject.toString());
            AppLogger.getInstance().e("Get Wallet Balance ENCRYPTED ==>", cipher.bytesToHex(cipher.encrypt(jObject.toString())));
            Call<ApisResponse> call = apiService.getWalletBalance(SharePreference.getInstance().getString(SharePreference.userToken), String.valueOf(n), cipher.bytesToHex(cipher.encrypt(jObject.toString())));
            call.enqueue(new Callback<ApisResponse>() {
                @Override
                public void onResponse(Call<ApisResponse> call, Response<ApisResponse> response) {
                    onPostExecute(response.body());
                }

                @Override
                public void onFailure(Call<ApisResponse> call, Throwable t) {
                    if (!call.isCanceled()) {
                        CommonMethodsUtils.Notify(activity, activity.getString(R.string.app_name), Constants.msg_Service_Error, false);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void onPostExecute(ApisResponse response) {
        try {
            Model_MinesweeperData responseModel = new Gson().fromJson(new String(cipher.decrypt(response.getEncrypt())), Model_MinesweeperData.class);
            AppLogger.getInstance().e("RESPONSE11", "" + new Gson().toJson(responseModel));
            if (responseModel.getStatus().equals(Constants.STATUS_LOGOUT)) {
                CommonMethodsUtils.doLogout(activity);
            } else if (!CommonMethodsUtils.isStringNullOrEmpty(responseModel.getEarningPoint())) {
                SharePreference.getInstance().putString(SharePreference.EarnedPoints, responseModel.getEarningPoint());
                if (activity instanceof HomeActivity) {
                    ((HomeActivity) activity).onUpdateWalletBalance();
                }
                if (activity instanceof EarningOptionsActivity) {
                    ((EarningOptionsActivity) activity).onUpdateWalletBalance();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
