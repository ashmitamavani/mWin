package com.mwin.reward.async;

import android.app.Activity;
import android.os.Build;
import android.provider.Settings;

import com.google.firebase.inappmessaging.FirebaseInAppMessaging;
import com.google.gson.Gson;
import com.mwin.reward.R;
import com.mwin.reward.activity.MilestoneTargetDetailsActivity;
import com.mwin.reward.async.models.ApisResponse;
import com.mwin.reward.async.models.MilestonesTargetResponseModel;
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

public class GetMilestoneTargetDetailsAsync {
    private Activity activity;
    private JSONObject jObject;
    private AESCipher cipher;

    public GetMilestoneTargetDetailsAsync(final Activity activity, String milestoneId) {
        this.activity = activity;
        cipher = new AESCipher();
        try {
            CommonMethodsUtils.showProgressLoader(activity);
            jObject = new JSONObject();
            jObject.put("T0P15R", SharePreference.getInstance().getString(SharePreference.userId));
            jObject.put("IPWETM", milestoneId);
            jObject.put("234WER", SharePreference.getInstance().getString(SharePreference.userToken));

            jObject.put("DFG4DD", Settings.Secure.getString(activity.getContentResolver(), Settings.Secure.ANDROID_ID));
            jObject.put("DFG34", SharePreference.getInstance().getString(SharePreference.AdID));
            jObject.put("DFGD44", Build.MODEL);
            jObject.put("GDFG34", Build.VERSION.RELEASE);
            jObject.put("JK7877", SharePreference.getInstance().getString(SharePreference.AppVersion));
            jObject.put("456FGF", SharePreference.getInstance().getInt(SharePreference.totalOpen));
            jObject.put("DG465H", SharePreference.getInstance().getInt(SharePreference.todayOpen));

            int n = CommonMethodsUtils.getRandomNumberBetweenRange(1, 1000000);
            jObject.put("RANDOM", n);
            WebApisInterface apiService = WebApisClient.getClient().create(WebApisInterface.class);
            AppLogger.getInstance().e("Get Active Milestone Details ORIGINAL ==>", jObject.toString());
            AppLogger.getInstance().e("Get Active Milestone Details ENCRYPTED ==>", cipher.bytesToHex(cipher.encrypt(jObject.toString())));
            Call<ApisResponse> call = apiService.getSingleMilestoneData(SharePreference.getInstance().getString(SharePreference.userToken), String.valueOf(n), cipher.bytesToHex(cipher.encrypt(jObject.toString())));
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
            MilestonesTargetResponseModel responseModel = new Gson().fromJson(new String(cipher.decrypt(response.getEncrypt())), MilestonesTargetResponseModel.class);
            //AppLogger.getInstance().e("RESPONSE: " + type, "" + new Gson().toJson(responseModel));
            if (responseModel.getStatus().equals(Constants.STATUS_LOGOUT)) {
                CommonMethodsUtils.doLogout(activity);
            } else {
                AdsUtil.adFailUrl = responseModel.getAdFailUrl();
                if (!CommonMethodsUtils.isStringNullOrEmpty(responseModel.getUserToken())) {
                    SharePreference.getInstance().putString(SharePreference.userToken, responseModel.getUserToken());
                }
                if (responseModel.getStatus().equals(Constants.STATUS_SUCCESS) || responseModel.getStatus().equals("2")) {
                    if (activity instanceof MilestoneTargetDetailsActivity) {
                        ((MilestoneTargetDetailsActivity) activity).setData(responseModel);
                    }
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
