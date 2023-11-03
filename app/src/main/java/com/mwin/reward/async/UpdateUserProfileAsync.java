package com.mwin.reward.async;

import android.app.Activity;
import android.os.Build;
import android.provider.Settings;

import com.google.firebase.inappmessaging.FirebaseInAppMessaging;
import com.google.gson.Gson;
import com.mwin.reward.R;
import com.mwin.reward.async.models.ApisResponse;
import com.mwin.reward.async.models.UserProfileModel;
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

public class UpdateUserProfileAsync {
    private Activity activity;
    private JSONObject jObject;
    private AESCipher cipher;

    public UpdateUserProfileAsync(final Activity activity, String fName, String lName, String countryCode, String countryName) {
        this.activity = activity;
        cipher = new AESCipher();
        try {
            CommonMethodsUtils.showProgressLoader(activity);
            jObject = new JSONObject();
            jObject.put("P3GLQT", SharePreference.getInstance().getString(SharePreference.userId));
            jObject.put("SDFDFG", SharePreference.getInstance().getString(SharePreference.userToken));
            jObject.put("DU539L", fName);
            jObject.put("2GDUUR", lName);
            jObject.put("HHDHH7", countryCode);
            jObject.put("U49PZU", countryName);

            jObject.put("WERWWE", SharePreference.getInstance().getString(SharePreference.FCMregId));
            jObject.put("2WWTHH", SharePreference.getInstance().getString(SharePreference.AdID));
            jObject.put("UBOOPW", Build.MODEL);
            jObject.put("QASWDE", Build.VERSION.RELEASE);
            jObject.put("4HEAGL", SharePreference.getInstance().getString(SharePreference.AppVersion));
            jObject.put("WBBWTN", SharePreference.getInstance().getInt(SharePreference.totalOpen));
            jObject.put("GTRVDH", SharePreference.getInstance().getInt(SharePreference.todayOpen));
            jObject.put("FFTDFGF", CommonMethodsUtils.verifyInstallerId(activity));
            jObject.put("IT779G", Settings.Secure.getString(activity.getContentResolver(), Settings.Secure.ANDROID_ID));

            //AppLogger.getInstance().e("JSONRegisterProfile", "" + jObject.toString());
            //AppLogger.getInstance().e("JSONRegisterProfile1", "" + new AESCipher().encrypt( jObject.toString()));
            int n = CommonMethodsUtils.getRandomNumberBetweenRange(1, 1000000);
            jObject.put("RANDOM", n);
            WebApisInterface apiService = WebApisClient.getClient().create(WebApisInterface.class);
            Call<ApisResponse> call = apiService.editUserProfile(SharePreference.getInstance().getString(SharePreference.userToken), String.valueOf(n), cipher.bytesToHex(cipher.encrypt(jObject.toString())));
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
            UserProfileModel responseModel = new Gson().fromJson(new String(cipher.decrypt(response.getEncrypt())), UserProfileModel.class);
            if (responseModel.getStatus().equals(Constants.STATUS_LOGOUT)) {
                CommonMethodsUtils.doLogout(activity);
            } else {
                AdsUtil.adFailUrl = responseModel.getAdFailUrl();
                if (!CommonMethodsUtils.isStringNullOrEmpty(responseModel.getUserToken())) {
                    SharePreference.getInstance().putString(SharePreference.userToken, responseModel.getUserToken());
                }
                if (responseModel.getStatus().equals(Constants.STATUS_SUCCESS)) {
                    CommonMethodsUtils.logFirebaseEvent(activity, "FeatureUsabilityItemId", "FeatureUsabilityEvent", "Profile", "User Profile Edit -> Success");
                    SharePreference.getInstance().putString(SharePreference.UserDetails, new Gson().toJson(responseModel.getUserDetails()));
                    SharePreference.getInstance().putString(SharePreference.EarnedPoints, responseModel.getUserDetails().getEarningPoint());
                    CommonMethodsUtils.NotifySuccess(activity, activity.getString(R.string.app_name), responseModel.getMessage(), true);
                } else if (responseModel.getStatus().equals(Constants.STATUS_ERROR)) {
                    CommonMethodsUtils.logFirebaseEvent(activity, "FeatureUsabilityItemId", "FeatureUsabilityEvent", "Profile", "User Profile Edit -> Fail");
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
