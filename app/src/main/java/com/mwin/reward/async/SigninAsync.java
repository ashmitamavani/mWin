package com.mwin.reward.async;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;

import com.google.gson.Gson;
import com.mwin.reward.ApplicationController;
import com.mwin.reward.R;
import com.mwin.reward.activity.HomeActivity;
import com.mwin.reward.async.models.ApisResponse;
import com.mwin.reward.async.models.MainResponseModel;
import com.mwin.reward.async.models.SigninResponseModel;
import com.mwin.reward.network.WebApisClient;
import com.mwin.reward.network.WebApisInterface;
import com.mwin.reward.utils.AESCipher;
import com.mwin.reward.utils.AdsUtil;
import com.mwin.reward.utils.AppLogger;
import com.mwin.reward.utils.CommonMethodsUtils;
import com.mwin.reward.utils.SharePreference;
import com.mwin.reward.value.ApiRequestModel;
import com.mwin.reward.value.Constants;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SigninAsync {
    private Activity activity;
    private JSONObject jObject;
    private AESCipher cipher;

    public SigninAsync(final Activity activity, ApiRequestModel requestModel) {
        this.activity = activity;
        cipher = new AESCipher();
        try {
            CommonMethodsUtils.showProgressLoader(activity);
            jObject = new JSONObject();

            jObject.put("GWUOJ1", requestModel.getFirstName().trim());
            jObject.put("HW1J1Q", requestModel.getLastName().trim());
            jObject.put("BJHWJ0", requestModel.getEmailId().trim());
            jObject.put("8GBSJQ", requestModel.getProfileImage().trim());
            jObject.put("TEWOWE", requestModel.getReferralCode().trim());

            jObject.put("HHWO1P", Settings.Secure.getString(activity.getContentResolver(), Settings.Secure.ANDROID_ID));
            jObject.put("0NRH3T", SharePreference.getInstance().getString(SharePreference.FCMregId));
            jObject.put("WJTCYH", SharePreference.getInstance().getString(SharePreference.AdID));
            if (SharePreference.getInstance().getString(SharePreference.ReferData).length() > 0) {
                jObject.put("3JAFZZ", new JSONObject(SharePreference.getInstance().getString(SharePreference.ReferData)));
            } else {
                jObject.put("3JAFZZ", "");
            }
            jObject.put("5R7GHT", "1");// 1 = android
            jObject.put("16TMPD", Build.MODEL);
            jObject.put("YEH0AC", Build.VERSION.RELEASE);
            jObject.put("62PL7W", SharePreference.getInstance().getString(SharePreference.AppVersion));
            jObject.put("FOHHOK", SharePreference.getInstance().getInt(SharePreference.totalOpen));
            jObject.put("X4WK2C", SharePreference.getInstance().getInt(SharePreference.todayOpen));
            jObject.put("GO8PLO", CommonMethodsUtils.verifyInstallerId(activity));
            jObject.put("ZRQB3A", Settings.Secure.getString(activity.getContentResolver(), Settings.Secure.ANDROID_ID));

            AppLogger.getInstance().e("Login DATA ORIGINAL==", "" + jObject.toString());
            AppLogger.getInstance().e("LOGIN DATA ENCRYPTED==", "" + cipher.bytesToHex(cipher.encrypt(jObject.toString())));

            int n = CommonMethodsUtils.getRandomNumberBetweenRange(1, 1000000);
            jObject.put("RANDOM", n);
            WebApisInterface apiService = WebApisClient.getClient().create(WebApisInterface.class);
            Call<ApisResponse> call = apiService.loginUser(SharePreference.getInstance().getString(SharePreference.userToken), String.valueOf(n), cipher.bytesToHex(cipher.encrypt(jObject.toString())));
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
            SigninResponseModel responseModel = new Gson().fromJson(new String(cipher.decrypt(response.getEncrypt())), SigninResponseModel.class);
            if (responseModel.getStatus().equals(Constants.STATUS_LOGOUT)) {
                CommonMethodsUtils.doLogout(activity);
            } else {
                AdsUtil.adFailUrl = responseModel.getAdFailUrl();
//            SharePreference.getInstance().putString(SharePreference.ReferData, "");
                if (responseModel.getStatus().equals(Constants.STATUS_SUCCESS)) {
                    SharePreference.getInstance().putString(SharePreference.UserDetails, new Gson().toJson(responseModel.getUserDetails()));
                    SharePreference.getInstance().putString(SharePreference.userId, responseModel.getUserDetails().getUserId());
                    SharePreference.getInstance().putString(SharePreference.userToken, responseModel.getUserDetails().getUserToken());
                    SharePreference.getInstance().putBoolean(SharePreference.IS_LOGIN, true);
                    SharePreference.getInstance().putString(SharePreference.EarnedPoints, responseModel.getUserDetails().getEarningPoint());
                    Intent in = new Intent(activity, HomeActivity.class).putExtra("isFromLogin", true);
                    try {
                        MainResponseModel responseMain = new Gson().fromJson(SharePreference.getInstance().getString(SharePreference.HomeData), MainResponseModel.class);
                        if (!CommonMethodsUtils.isStringNullOrEmpty(responseMain.getIsShowSurvey()) && responseMain.getIsShowSurvey().matches("1")) {
                            ApplicationController app = (ApplicationController) activity.getApplication();
                            app.startCPX();
                        }
                        if (!CommonMethodsUtils.isStringNullOrEmpty(responseMain.getIsShowPubScale()) && responseMain.getIsShowPubScale().matches("1")) {
                            ApplicationController app = (ApplicationController)  activity.getApplication();
                            app.initPubScale();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    activity.startActivity(in);
                    activity.finishAffinity();
                } else if (responseModel.getStatus().equals(Constants.STATUS_ERROR)) {
//                String msg = "ORIGINAL: \n\n" + jObject.toString() + "\n\n\nCAPTCHA:\n\n" + captcha + "\n\n\nENCRYPTED: \n\n" + new AESCipher().encrypt( jObject.toString());
                    CommonMethodsUtils.Notify(activity, activity.getString(R.string.app_name), responseModel.getMessage(), false);
                } else if (responseModel.getStatus().equals("2")) {
                    CommonMethodsUtils.Notify(activity, activity.getString(R.string.app_name), responseModel.getMessage(), false);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
