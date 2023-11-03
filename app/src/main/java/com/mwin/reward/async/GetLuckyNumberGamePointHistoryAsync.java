package com.mwin.reward.async;

import android.app.Activity;
import android.os.Build;
import android.provider.Settings;

import com.google.firebase.inappmessaging.FirebaseInAppMessaging;
import com.google.gson.Gson;
import com.mwin.reward.R;
import com.mwin.reward.activity.LuckyNumberContestHistoryActivity;
import com.mwin.reward.async.models.ApisResponse;
import com.mwin.reward.async.models.EarnedPointHistoryModel;
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

public class GetLuckyNumberGamePointHistoryAsync {
    private Activity activity;
    private JSONObject jObject;
    private AESCipher cipher;
    private String type;

    public GetLuckyNumberGamePointHistoryAsync(final Activity activity, String type, String pageNo) {
        this.activity = activity;
        cipher = new AESCipher();
        this.type = type;
        try {
            CommonMethodsUtils.showProgressLoader(activity);
            jObject = new JSONObject();
            jObject.put("I55YAN", SharePreference.getInstance().getString(SharePreference.userId));
            jObject.put("WERSDF", SharePreference.getInstance().getString(SharePreference.userToken));
            jObject.put("XXFBHH", pageNo);
            jObject.put("6THHNJ", type);

            jObject.put("DFGERE", SharePreference.getInstance().getString(SharePreference.AdID));
            jObject.put("342SDFS", Build.MODEL);
            jObject.put("FGH546", Build.VERSION.RELEASE);
            jObject.put("FGHF5", SharePreference.getInstance().getString(SharePreference.AppVersion));
            jObject.put("DFGD4RT", SharePreference.getInstance().getInt(SharePreference.totalOpen));
            jObject.put("GFG454", SharePreference.getInstance().getInt(SharePreference.todayOpen));
            jObject.put("DFG45", CommonMethodsUtils.verifyInstallerId(activity));
            jObject.put("DFGD42", Settings.Secure.getString(activity.getContentResolver(), Settings.Secure.ANDROID_ID));

            int n = CommonMethodsUtils.getRandomNumberBetweenRange(1, 1000000);
            jObject.put("RANDOM", n);
            WebApisInterface apiService = WebApisClient.getClient().create(WebApisInterface.class);
            //AppLogger.getInstance().e("Get Lucky Number Point History ORIGINAL ==>", jObject.toString());
            //AppLogger.getInstance().e("Get Lucky Number Point History ENCRYPTED --)", "" + new AESCipher().encrypt( jObject.toString()));
            Call<ApisResponse> call = apiService.getLuckyNumberHistory(SharePreference.getInstance().getString(SharePreference.userToken), String.valueOf(n), cipher.bytesToHex(cipher.encrypt(jObject.toString())));
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
            EarnedPointHistoryModel responseModel = new Gson().fromJson(new String(cipher.decrypt(response.getEncrypt())), EarnedPointHistoryModel.class);
            //AppLogger.getInstance().e("RESPONSE: " + type, "" + new Gson().toJson(responseModel));
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
                    ((LuckyNumberContestHistoryActivity) activity).setData(type, responseModel);
                } else if (responseModel.getStatus().equals(Constants.STATUS_ERROR)) {
                    CommonMethodsUtils.Notify(activity, activity.getString(R.string.app_name), responseModel.getMessage(), false);
                } else if (responseModel.getStatus().equals("2")) {
                    ((LuckyNumberContestHistoryActivity) activity).setData(type, responseModel);
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
