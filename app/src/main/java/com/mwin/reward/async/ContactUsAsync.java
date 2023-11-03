package com.mwin.reward.async;

import android.app.Activity;
import android.os.Build;
import android.provider.Settings;

import com.google.firebase.inappmessaging.FirebaseInAppMessaging;
import com.google.gson.Gson;
import com.mwin.reward.R;
import com.mwin.reward.async.models.ApisResponse;
import com.mwin.reward.async.models.HelpQAModel;
import com.mwin.reward.network.WebApisClient;
import com.mwin.reward.network.WebApisInterface;
import com.mwin.reward.utils.AESCipher;
import com.mwin.reward.utils.AdsUtil;
import com.mwin.reward.utils.CommonMethodsUtils;
import com.mwin.reward.utils.SharePreference;
import com.mwin.reward.value.Constants;

import org.json.JSONObject;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ContactUsAsync {
    private Activity activity;
    private JSONObject jObject;
    private AESCipher cipher;

    public ContactUsAsync(final Activity activity, String emailId, String message, String mobileNumber, String image) {
        this.activity = activity;
        cipher = new AESCipher();
        try {
            CommonMethodsUtils.showProgressLoader(activity);
            jObject = new JSONObject();

            jObject.put("PHEHC6", SharePreference.getInstance().getInt(SharePreference.totalOpen));
            jObject.put("9ETAPF", SharePreference.getInstance().getInt(SharePreference.todayOpen));
            jObject.put("LU6QQV", SharePreference.getInstance().getString(SharePreference.AdID));
            jObject.put("CC577M", SharePreference.getInstance().getString(SharePreference.AppVersion));
            jObject.put("T6RTK8", Build.MODEL);
            jObject.put("WQ3DU4", SharePreference.getInstance().getString(SharePreference.userId));
            jObject.put("ERRTYU", SharePreference.getInstance().getString(SharePreference.userToken));
            jObject.put("21YDT9", emailId);
            jObject.put("HJEA2S", mobileNumber);
            jObject.put("BJ5YVL", message);
            jObject.put("GM2HOW", Settings.Secure.getString(activity.getContentResolver(), Settings.Secure.ANDROID_ID));
            MultipartBody.Part body = null;

            // Send extra params as part
            RequestBody requestBodyDetails =
                    RequestBody.create(
                            MediaType.parse("multipart/form-data"), jObject.toString());
            try {
                if (image != null) {
                    File file = new File(image);
                    RequestBody requestFile =
                            RequestBody.create(MediaType.parse("multipart/form-data"), file);
                    // MultipartBody.Part is used to send also the actual file name
                    body = MultipartBody.Part.createFormData("image1", file.getName(), requestFile);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            int n = CommonMethodsUtils.getRandomNumberBetweenRange(1, 1000000);
            jObject.put("RANDOM", n);
            WebApisInterface apiService = WebApisClient.getClient().create(WebApisInterface.class);
            Call<ApisResponse> call = apiService.submitFeedback(SharePreference.getInstance().getString(SharePreference.userToken), String.valueOf(n), requestBodyDetails, body);
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
            HelpQAModel responseModel = new Gson().fromJson(new String(cipher.decrypt(response.getEncrypt())), HelpQAModel.class);
            //AppLogger.getInstance().e("RESPONSE", "" + new Gson().toJson(responseModel));
            if (responseModel.getStatus().equals(Constants.STATUS_LOGOUT)) {
                CommonMethodsUtils.doLogout(activity);
            } else {
                AdsUtil.adFailUrl = responseModel.getAdFailUrl();
                if (!CommonMethodsUtils.isStringNullOrEmpty(responseModel.getUserToken())) {
                    SharePreference.getInstance().putString(SharePreference.userToken, responseModel.getUserToken());
                }
                if (responseModel.getStatus().equals(Constants.STATUS_SUCCESS)) {
                    CommonMethodsUtils.logFirebaseEvent(activity, "FeatureUsabilityItemId", "FeatureUsabilityEvent", "Feedback", "Submit Feedback -> Success");
                    CommonMethodsUtils.NotifySuccess(activity, activity.getString(R.string.app_name), responseModel.getMessage(), true);
                } else if (responseModel.getStatus().equals(Constants.STATUS_ERROR)) {
                    CommonMethodsUtils.logFirebaseEvent(activity, "FeatureUsabilityItemId", "FeatureUsabilityEvent", "Feedback", "Submit Feedback -> Fail");
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
