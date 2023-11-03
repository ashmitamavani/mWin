package com.mwin.reward.async;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;

import com.google.firebase.inappmessaging.FirebaseInAppMessaging;
import com.google.gson.Gson;
import com.mwin.reward.ApplicationController;
import com.mwin.reward.R;
import com.mwin.reward.activity.EarningOptionsActivity;
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

public class SaveQuickTaskAsync {
    private Activity activity;
    private JSONObject jObject;
    private String ids;
    private AESCipher cipher;

    public SaveQuickTaskAsync(final Activity activity, String points, String ids) {
        this.activity = activity;
        this.ids = ids;
        cipher = new AESCipher();
        try {
            CommonMethodsUtils.showProgressLoader(activity);
            jObject = new JSONObject();
            jObject.put("YHNBNGGHJ", points);
            jObject.put("AWEWERERFF", ids);
            jObject.put("RHFGHDD", SharePreference.getInstance().getString(SharePreference.userId));
            jObject.put("SDFSDFS", SharePreference.getInstance().getString(SharePreference.userToken));
            jObject.put("BVNVFVNV", Settings.Secure.getString(activity.getContentResolver(), Settings.Secure.ANDROID_ID));
            jObject.put("FGDFG", SharePreference.getInstance().getString(SharePreference.FCMregId));
            jObject.put("YUIYIYUI", SharePreference.getInstance().getString(SharePreference.AdID));
            jObject.put("WERWER", Build.MODEL);
            jObject.put("QWEQDAS", Build.VERSION.RELEASE);
            jObject.put("ASDAWER", SharePreference.getInstance().getString(SharePreference.AppVersion));
            jObject.put("KHJKHJJ", SharePreference.getInstance().getInt(SharePreference.totalOpen));
            jObject.put("VBNFGHF", SharePreference.getInstance().getInt(SharePreference.todayOpen));
            jObject.put("ASDAWEQ", CommonMethodsUtils.verifyInstallerId(activity));
            jObject.put("HKJHKH", Settings.Secure.getString(activity.getContentResolver(), Settings.Secure.ANDROID_ID));
            int n = CommonMethodsUtils.getRandomNumberBetweenRange(1, 1000000);
            jObject.put("RANDOM", n);
            WebApisInterface apiService = WebApisClient.getClient().create(WebApisInterface.class);
//            AppLogger.getInstance().e("Save Quick Task ORIGINAL ==>", jObject.toString());
//            AppLogger.getInstance().e("Save Quick Task ENCRYPTED ==>", cipher.bytesToHex(cipher.encrypt(jObject.toString())));

            Call<ApisResponse> call = apiService.saveQuickTask(SharePreference.getInstance().getString(SharePreference.userToken), String.valueOf(n), cipher.bytesToHex(cipher.encrypt(jObject.toString())));
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
//            AppLogger.getInstance().e("RESPONSE", "" + new Gson().toJson(responseModel));
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
                    CommonMethodsUtils.setToast(activity, "Congratulations! Your quick tasks points are credited in your wallet.");
                    if (activity instanceof EarningOptionsActivity) {
                        ((EarningOptionsActivity) activity).updateQuickTask(true, ids);
                    }
                    ApplicationController.getContext().sendBroadcast(new Intent(Constants.QUICK_TASK_RESULT)
                            .setPackage(ApplicationController.getContext().getPackageName())
                            .putExtra("id", ids)
                            .putExtra("status", Constants.STATUS_SUCCESS));
                } else {
                    if (activity instanceof EarningOptionsActivity) {
                        ((EarningOptionsActivity) activity).updateQuickTask(false, ids);
                    }
                    ApplicationController.getContext().sendBroadcast(new Intent(Constants.QUICK_TASK_RESULT)
                            .setPackage(ApplicationController.getContext().getPackageName())
                            .putExtra("id", ids)
                            .putExtra("status", Constants.STATUS_ERROR));
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
