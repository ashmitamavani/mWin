package com.mwin.reward.async;

import android.app.Activity;
import android.os.Build;
import android.provider.Settings;

import com.google.firebase.inappmessaging.FirebaseInAppMessaging;
import com.google.gson.Gson;
import com.mwin.reward.R;
import com.mwin.reward.activity.AdjoeLeaderboardHistoryActivity;
import com.mwin.reward.async.models.AdjoeLeaderboardResponseModel;
import com.mwin.reward.async.models.ApisResponse;
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

public class GetAdjoeLeaderboardHistoryAsync {
    private Activity activity;
    private JSONObject jObject;
    private AESCipher cipher;

    public GetAdjoeLeaderboardHistoryAsync(final Activity activity, String pageNo) {
        this.activity = activity;
        cipher = new AESCipher();
        try {
            CommonMethodsUtils.showProgressLoader(activity);
            jObject = new JSONObject();
            jObject.put("WW1VX0", pageNo);
            jObject.put("RWRWER", SharePreference.getInstance().getString(SharePreference.userId));
            jObject.put("SDFSDF", SharePreference.getInstance().getString(SharePreference.userToken));
            jObject.put("ERTET", SharePreference.getInstance().getString(SharePreference.FCMregId));
            jObject.put("ASDAS", Build.VERSION.RELEASE);
            jObject.put("KR3ELO", SharePreference.getInstance().getString(SharePreference.AdID));
            jObject.put("RPUD6H", Build.MODEL);
            jObject.put("BGCC69", SharePreference.getInstance().getString(SharePreference.AppVersion));
            jObject.put("THTXK5", SharePreference.getInstance().getInt(SharePreference.totalOpen));
            jObject.put("AXXHSK", SharePreference.getInstance().getInt(SharePreference.todayOpen));
            jObject.put("ASDASD", CommonMethodsUtils.verifyInstallerId(activity));
            jObject.put("844RD2", Settings.Secure.getString(activity.getContentResolver(), Settings.Secure.ANDROID_ID));

            int n = CommonMethodsUtils.getRandomNumberBetweenRange(1, 1000000);
            jObject.put("RANDOM", n);
            WebApisInterface apiService = WebApisClient.getClient().create(WebApisInterface.class);
            AppLogger.getInstance().e("Get ADJOE LEADERBOARD HISTORY ORIGINAL ==>", jObject.toString());
            AppLogger.getInstance().e("Get ADJOE LEADERBOARD HISTORY ENCRYPTED ==>", cipher.bytesToHex(cipher.encrypt(jObject.toString())));
            Call<ApisResponse> call = apiService.getAdjoeLeaderboardHistory(SharePreference.getInstance().getString(SharePreference.userToken), String.valueOf(n), cipher.bytesToHex(cipher.encrypt(jObject.toString())));
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
            AdjoeLeaderboardResponseModel responseModel = new Gson().fromJson(new String(cipher.decrypt(response.getEncrypt())), AdjoeLeaderboardResponseModel.class);
            AppLogger.getInstance().e("RESPONSE", "" + new Gson().toJson(responseModel));
            if (responseModel.getStatus().equals(Constants.STATUS_LOGOUT)) {
                CommonMethodsUtils.doLogout(activity);
            } else {
                AdsUtil.adFailUrl = responseModel.getAdFailUrl();
                if (!CommonMethodsUtils.isStringNullOrEmpty(responseModel.getUserToken())) {
                    SharePreference.getInstance().putString(SharePreference.userToken, responseModel.getUserToken());
                }
                if (responseModel.getStatus().equals(Constants.STATUS_SUCCESS) || responseModel.getStatus().equals("2")) {
                    ((AdjoeLeaderboardHistoryActivity) activity).setData(responseModel);
                } else {
                    CommonMethodsUtils.Notify(activity, activity.getString(R.string.app_name), responseModel.getMessage(), true);
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
