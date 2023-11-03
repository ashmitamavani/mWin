package com.mwin.reward.async;

import android.app.Activity;
import android.os.Build;
import android.provider.Settings;

import com.google.firebase.inappmessaging.FirebaseInAppMessaging;
import com.google.gson.Gson;
import com.mwin.reward.R;
import com.mwin.reward.activity.MilestoneTargetListActivity;
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

public class GetMilestoneTargetListAsync {
    private Activity activity;
    private JSONObject jObject;
    private AESCipher cipher;
    private String type;

    public GetMilestoneTargetListAsync(final Activity activity) {
        this.activity = activity;
        cipher = new AESCipher();
        this.type = type;
        try {
            CommonMethodsUtils.showProgressLoader(activity);
            jObject = new JSONObject();
            jObject.put("ORTTME", SharePreference.getInstance().getString(SharePreference.userId));
            jObject.put("345DFDF", SharePreference.getInstance().getString(SharePreference.userToken));

            jObject.put("DSF344", SharePreference.getInstance().getString(SharePreference.AdID));
            jObject.put("SDF345", Build.MODEL);
            jObject.put("232424", Build.VERSION.RELEASE);
            jObject.put("FGH5665", SharePreference.getInstance().getString(SharePreference.AppVersion));
            jObject.put("FGH675", SharePreference.getInstance().getInt(SharePreference.totalOpen));
            jObject.put("FGHFG6", SharePreference.getInstance().getInt(SharePreference.todayOpen));
            jObject.put("FTHR755", Settings.Secure.getString(activity.getContentResolver(), Settings.Secure.ANDROID_ID));

            int n = CommonMethodsUtils.getRandomNumberBetweenRange(1, 1000000);
            jObject.put("RANDOM", n);
            WebApisInterface apiService = WebApisClient.getClient().create(WebApisInterface.class);
            AppLogger.getInstance().e("Get Active Milestone LIST ORIGINAL ==>", jObject.toString());
            AppLogger.getInstance().e("Get Active Milestone LIST ENCRYPTED ==>", cipher.bytesToHex(cipher.encrypt(jObject.toString())));
            Call<ApisResponse> call = apiService.getMilestonesData(SharePreference.getInstance().getString(SharePreference.userToken), String.valueOf(n), cipher.bytesToHex(cipher.encrypt(jObject.toString())));
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
                    if (activity instanceof MilestoneTargetListActivity) {
                        ((MilestoneTargetListActivity) activity).setActiveMilestonesData(responseModel);
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
