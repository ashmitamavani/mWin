package com.mwin.reward.async;

import android.app.Activity;
import android.os.Build;
import android.provider.Settings;

import com.google.firebase.inappmessaging.FirebaseInAppMessaging;
import com.google.gson.Gson;
import com.mwin.reward.R;
import com.mwin.reward.activity.SeeVideoListActivity;
import com.mwin.reward.async.models.ApisResponse;
import com.mwin.reward.async.models.SeeVideoModel;
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

public class GetSeeVideoListAsync {
    private Activity activity;
    private JSONObject jObject;
    private AESCipher cipher;

    public GetSeeVideoListAsync(final Activity activity) {
        this.activity = activity;
        cipher = new AESCipher();
        try {
            CommonMethodsUtils.showProgressLoader(activity);
            jObject = new JSONObject();
            jObject.put("GY8PV0", SharePreference.getInstance().getString(SharePreference.userId));
            jObject.put("FHGHJU", SharePreference.getInstance().getString(SharePreference.userToken));
            jObject.put("HGHTNB", Settings.Secure.getString(activity.getContentResolver(), Settings.Secure.ANDROID_ID));
            jObject.put("HJKHK", SharePreference.getInstance().getString(SharePreference.FCMregId));
            jObject.put("ASZXCS", SharePreference.getInstance().getString(SharePreference.AdID));
            jObject.put("SDFSDF", Build.MODEL);
            jObject.put("MUJUH", Build.VERSION.RELEASE);
            jObject.put("WERWR", SharePreference.getInstance().getString(SharePreference.AppVersion));
            jObject.put("XCVDFD", SharePreference.getInstance().getInt(SharePreference.totalOpen));
            jObject.put("XCXVV", SharePreference.getInstance().getInt(SharePreference.todayOpen));
            jObject.put("NJKHJKJ", CommonMethodsUtils.verifyInstallerId(activity));
            //AppLogger.getInstance().e("Watch Video List DATA ORIGINAL--)", "" + jObject.toString());
            //AppLogger.getInstance().e("Watch Video List DATA ENCRYPTED--)", "" + new AESCipher().encrypt( jObject.toString()));
            int n = CommonMethodsUtils.getRandomNumberBetweenRange(1, 1000000);
            jObject.put("RANDOM", n);
            WebApisInterface apiService = WebApisClient.getClient().create(WebApisInterface.class);
            Call<ApisResponse> call = apiService.getWatchVideoList(SharePreference.getInstance().getString(SharePreference.userToken), String.valueOf(n), cipher.bytesToHex(cipher.encrypt(jObject.toString())));
//            Call<WatchVideoModel> call = apiService.getWatchVideoList(jObject.toString());
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
            SeeVideoModel responseModel = new Gson().fromJson(new String(cipher.decrypt(response.getEncrypt())), SeeVideoModel.class);
            //AppLogger.getInstance().e("RESPONSE", "" + new Gson().toJson(responseModel));
            if (responseModel.getStatus().equals(Constants.STATUS_LOGOUT)) {
                CommonMethodsUtils.doLogout(activity);
            } else {
                AdsUtil.adFailUrl = responseModel.getAdFailUrl();
                if (!CommonMethodsUtils.isStringNullOrEmpty(responseModel.getUserToken())) {
                    SharePreference.getInstance().putString(SharePreference.userToken, responseModel.getUserToken());
                }
                if (responseModel.getStatus().equals(Constants.STATUS_SUCCESS)) {
                    if (activity instanceof SeeVideoListActivity) {
                        ((SeeVideoListActivity) activity).setData(responseModel);
                    }
                } else if (responseModel.getStatus().equals(Constants.STATUS_ERROR)) {
                    CommonMethodsUtils.Notify(activity, activity.getString(R.string.app_name), responseModel.getMessage(), false);
                } else if (responseModel.getStatus().equals("2")) { // not login
                    if (activity instanceof SeeVideoListActivity) {
                        ((SeeVideoListActivity) activity).setData(responseModel);
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
