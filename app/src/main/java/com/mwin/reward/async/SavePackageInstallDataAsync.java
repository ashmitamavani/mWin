package com.mwin.reward.async;

import android.provider.Settings;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mwin.reward.ApplicationController;
import com.mwin.reward.async.models.MainResponseModel;
import com.mwin.reward.network.WebApisClient;
import com.mwin.reward.network.WebApisInterface;
import com.mwin.reward.utils.AESCipher;
import com.mwin.reward.utils.CommonMethodsUtils;
import com.mwin.reward.utils.SharePreference;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SavePackageInstallDataAsync {
    private JSONObject jObject;
    private AESCipher cipher;

    public SavePackageInstallDataAsync(String packageId) {
        try {
            jObject = new JSONObject();
            MainResponseModel responseMain = new Gson().fromJson(SharePreference.getInstance().getString(SharePreference.HomeData), MainResponseModel.class);
            String url = responseMain.getPackageInstallTrackingUrl()
                    + "?pid=" + responseMain.getPid()
                    + "&offer_id=" + responseMain.getOffer_id()
                    + "&sub1=" + packageId
                    + "&sub2=" + ApplicationController.getContext().getPackageName()
                    + "&sub3=" + SharePreference.getInstance().getString(SharePreference.AdID);
            jObject.put("PBKNNW", url);
            jObject.put("WT8KFM", SharePreference.getInstance().getString(SharePreference.userId).length() == 0 ? "0" : SharePreference.getInstance().getString(SharePreference.userId));
            jObject.put("ERTYGT", SharePreference.getInstance().getString(SharePreference.userToken));
            jObject.put("42NNCM", SharePreference.getInstance().getString(SharePreference.AdID));
            jObject.put("5F0G11", packageId);
            try {
                jObject.put("HJHJY", Settings.Secure.getString(ApplicationController.getContext().getContentResolver(), Settings.Secure.ANDROID_ID));
            } catch (Exception e) {
                e.printStackTrace();
            }
            int n = CommonMethodsUtils.getRandomNumberBetweenRange(1, 1000000);
            jObject.put("RANDOM", n);
            WebApisInterface apiService = WebApisClient.getClient().create(WebApisInterface.class);
            Call<JsonObject> call = apiService.savePackageTracking(SharePreference.getInstance().getString(SharePreference.userToken), String.valueOf(n), cipher.bytesToHex(cipher.encrypt(jObject.toString())));
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    onPostExecute(response.body());
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void onPostExecute(JsonObject responseModel) {
        try {
            //AppLogger.getInstance().e("RESPONSE", "" + new Gson().toJson(responseModel));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
