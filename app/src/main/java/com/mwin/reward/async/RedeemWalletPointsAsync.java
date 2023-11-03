package com.mwin.reward.async;

import android.app.Activity;
import android.os.Build;
import android.provider.Settings;

import com.google.firebase.inappmessaging.FirebaseInAppMessaging;
import com.google.gson.Gson;
import com.mwin.reward.R;
import com.mwin.reward.activity.RedeemOptionsSubListActivity;
import com.mwin.reward.async.models.ApisResponse;
import com.mwin.reward.async.models.RedeemPoints;
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

public class RedeemWalletPointsAsync {
    private Activity activity;
    private JSONObject jObject;
    private AESCipher cipher;

    public RedeemWalletPointsAsync(final Activity activity, String id, String title, String withdrawType, String mobileNumber) {
        this.activity = activity;
        cipher = new AESCipher();
        try {
            CommonMethodsUtils.showProgressLoader(activity);
            jObject = new JSONObject();
            jObject.put("G5XO4P", SharePreference.getInstance().getString(SharePreference.userId));
            jObject.put("SMCHKC", SharePreference.getInstance().getString(SharePreference.userToken));
            jObject.put("QXGHZ6", id);
            jObject.put("HGEJC3", withdrawType);
            jObject.put("0LP5W8", mobileNumber);
            jObject.put("G1T9R2", SharePreference.getInstance().getString(SharePreference.AdID));
            jObject.put("WHDX4T", Build.MODEL);
            jObject.put("WERET", Build.VERSION.RELEASE);
            jObject.put("FNCMYO", SharePreference.getInstance().getString(SharePreference.AppVersion));
            jObject.put("W3BR8D", SharePreference.getInstance().getInt(SharePreference.totalOpen));
            jObject.put("T9FYH3", SharePreference.getInstance().getInt(SharePreference.todayOpen));
            jObject.put("OJNTTR", Settings.Secure.getString(activity.getContentResolver(), Settings.Secure.ANDROID_ID));
            int n = CommonMethodsUtils.getRandomNumberBetweenRange(1, 1000000);
            jObject.put("RANDOM", n);
            WebApisInterface apiService = WebApisClient.getClient().create(WebApisInterface.class);
            //AppLogger.getInstance().e("REDEEM POINTS ORIGINAL ==>", jObject.toString());
            //AppLogger.getInstance().e("REDEEM POINTS ENCRYPTED ==>", new AESCipher().encrypt( jObject.toString()));
            Call<ApisResponse> call = apiService.redeemPoints(SharePreference.getInstance().getString(SharePreference.userToken), String.valueOf(n), cipher.bytesToHex(cipher.encrypt(jObject.toString())));

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
            RedeemPoints responseModel = new Gson().fromJson(new String(cipher.decrypt(response.getEncrypt())), RedeemPoints.class);
            //AppLogger.getInstance().e("RESPONSE", "" + new Gson().toJson(responseModel));
            if (responseModel.getStatus().equals(Constants.STATUS_LOGOUT)) {
                CommonMethodsUtils.doLogout(activity);
            } else {
                AdsUtil.adFailUrl = responseModel.getAdFailUrl();
                if (!CommonMethodsUtils.isStringNullOrEmpty(responseModel.getUserToken())) {
                    SharePreference.getInstance().putString(SharePreference.userToken, responseModel.getUserToken());
                }
                ((RedeemOptionsSubListActivity) activity).checkWithdraw(responseModel);
                if (!CommonMethodsUtils.isStringNullOrEmpty(responseModel.getTigerInApp())) {
                    FirebaseInAppMessaging.getInstance().triggerEvent(responseModel.getTigerInApp());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
