package com.mwin.reward.async;

import android.app.Activity;
import android.os.Build;
import android.provider.Settings;

import com.google.firebase.inappmessaging.FirebaseInAppMessaging;
import com.google.gson.Gson;
import com.mwin.reward.R;
import com.mwin.reward.activity.LuckyNumberContestActivity;
import com.mwin.reward.async.models.ApisResponse;
import com.mwin.reward.async.models.LuckyNumberDataModel;
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

public class GetLuckyNumberGameAsync {
    private Activity activity;
    private JSONObject jObject;
    private AESCipher cipher;

    public GetLuckyNumberGameAsync(final Activity activity) {
        this.activity = activity;
        cipher = new AESCipher();
        try {
            CommonMethodsUtils.showProgressLoader(activity);
            jObject = new JSONObject();
            jObject.put("I7UC69", SharePreference.getInstance().getString(SharePreference.userId));
            jObject.put("234234", SharePreference.getInstance().getString(SharePreference.userToken));
            jObject.put("WERW3", SharePreference.getInstance().getString(SharePreference.FCMregId));
            jObject.put("WERWR", SharePreference.getInstance().getString(SharePreference.AdID));
            jObject.put("23213EW", Build.MODEL);
            jObject.put("DF432RE", Build.VERSION.RELEASE);
            jObject.put("6576HGH", SharePreference.getInstance().getString(SharePreference.AppVersion));
            jObject.put("ERT454", SharePreference.getInstance().getInt(SharePreference.totalOpen));
            jObject.put("DFGF56", SharePreference.getInstance().getInt(SharePreference.todayOpen));
            jObject.put("DFG5RT", CommonMethodsUtils.verifyInstallerId(activity));
            jObject.put("FGDGD", Settings.Secure.getString(activity.getContentResolver(), Settings.Secure.ANDROID_ID));

            int n = CommonMethodsUtils.getRandomNumberBetweenRange(1, 1000000);
            jObject.put("RANDOM", n);
            WebApisInterface apiService = WebApisClient.getClient().create(WebApisInterface.class);
            //AppLogger.getInstance().e("Get Lucky Number ORIGINAL ==>", jObject.toString());
            //AppLogger.getInstance().e("Get Lucky Number ENCRYPTED ==>", new AESCipher().encrypt( jObject.toString()));
            Call<ApisResponse> call = apiService.getLuckyNumber(SharePreference.getInstance().getString(SharePreference.userToken), String.valueOf(n), cipher.bytesToHex(cipher.encrypt(jObject.toString())));
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
            LuckyNumberDataModel responseModel = new Gson().fromJson(new String(cipher.decrypt(response.getEncrypt())), LuckyNumberDataModel.class);
//            //AppLogger.getInstance().e("RESPONSE", "" + new Gson().toJson(responseModel));
            if (responseModel.getStatus().equals(Constants.STATUS_LOGOUT)) {
                CommonMethodsUtils.doLogout(activity);
            } else {
                AdsUtil.adFailUrl = responseModel.getAdFailUrl();
                if (!CommonMethodsUtils.isStringNullOrEmpty(responseModel.getUserToken())) {
                    SharePreference.getInstance().putString(SharePreference.userToken, responseModel.getUserToken());
                }
                ((LuckyNumberContestActivity) activity).setData(responseModel);
                if (!CommonMethodsUtils.isStringNullOrEmpty(responseModel.getTigerInApp())) {
                    FirebaseInAppMessaging.getInstance().triggerEvent(responseModel.getTigerInApp());
                }
            }
        } catch (
                Exception e) {
            e.printStackTrace();
        }
    }
}
