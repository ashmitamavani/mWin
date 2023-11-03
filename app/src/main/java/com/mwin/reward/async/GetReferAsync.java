package com.mwin.reward.async;

import android.app.Activity;
import android.os.Build;
import android.provider.Settings;

import com.google.firebase.inappmessaging.FirebaseInAppMessaging;
import com.google.gson.Gson;
import com.mwin.reward.R;
import com.mwin.reward.activity.ReferUsersActivity;
import com.mwin.reward.async.models.ApisResponse;
import com.mwin.reward.async.models.ReferResponseModel;
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

public class GetReferAsync {
    private Activity activity;
    private JSONObject jObject;
    private AESCipher cipher;

    public GetReferAsync(final Activity activity) {
        this.activity = activity;
        cipher = new AESCipher();
        try {
            CommonMethodsUtils.showProgressLoader(activity);
            jObject = new JSONObject();
            jObject.put("NK7THO", SharePreference.getInstance().getString(SharePreference.userId));
            jObject.put("WEWERE", SharePreference.getInstance().getString(SharePreference.userToken));
            jObject.put("JA0C6W", SharePreference.getInstance().getString(SharePreference.AdID));
            jObject.put("HW23CH", Build.MODEL);
            jObject.put("ASDADA", Build.VERSION.RELEASE);
            jObject.put("OYWKW7", SharePreference.getInstance().getString(SharePreference.AppVersion));
            jObject.put("3H2GK7", SharePreference.getInstance().getInt(SharePreference.totalOpen));
            jObject.put("J0HAPQ", SharePreference.getInstance().getInt(SharePreference.todayOpen));
            jObject.put("H3QPG4", Settings.Secure.getString(activity.getContentResolver(), Settings.Secure.ANDROID_ID));

            int n = CommonMethodsUtils.getRandomNumberBetweenRange(1, 1000000);
            jObject.put("RANDOM", n);
            WebApisInterface apiService = WebApisClient.getClient().create(WebApisInterface.class);
            //AppLogger.getInstance().e("Get Invite ORIGINAL ==>", jObject.toString());
//            //AppLogger.getInstance().e("Get Invite ENCRYPTED ==>", new AESCipher().encrypt( jObject.toString()));
            Call<ApisResponse> call = apiService.getInviteAsync(SharePreference.getInstance().getString(SharePreference.userToken), String.valueOf(n), jObject.toString());
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
            ReferResponseModel responseModel = new Gson().fromJson(new String(cipher.decrypt(response.getEncrypt())), ReferResponseModel.class);
            //AppLogger.getInstance().e("RESPONSE", "" + new Gson().toJson(responseModel));
            if (responseModel.getStatus().equals(Constants.STATUS_LOGOUT)) {
                CommonMethodsUtils.doLogout(activity);
            } else {
                AdsUtil.adFailUrl = responseModel.getAdFailUrl();
                if (!CommonMethodsUtils.isStringNullOrEmpty(responseModel.getUserToken())) {
                    SharePreference.getInstance().putString(SharePreference.userToken, responseModel.getUserToken());
                }
                if (responseModel.getStatus().equals(Constants.STATUS_SUCCESS)) {
                    if (activity instanceof ReferUsersActivity) {
                        ((ReferUsersActivity) activity).setData(responseModel);
                    }
                } else if (responseModel.getStatus().equals(Constants.STATUS_ERROR)) {
                    CommonMethodsUtils.Notify(activity, activity.getString(R.string.app_name), responseModel.getMessage(), false);
                } else if (responseModel.getStatus().equals("2")) { // not login
                    if (activity instanceof ReferUsersActivity) {
                        ((ReferUsersActivity) activity).setData(responseModel);
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
