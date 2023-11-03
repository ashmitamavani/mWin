package com.mwin.reward.async;

import android.content.Context;
import android.provider.Settings;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mwin.reward.ApplicationController;
import com.mwin.reward.async.models.MainResponseModel;
import com.mwin.reward.network.WebApisClient;
import com.mwin.reward.network.WebApisInterface;
import com.mwin.reward.utils.CommonMethodsUtils;
import com.mwin.reward.utils.SharePreference;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SendNewAppPackageDataAsync {
    private Context context;

    public SendNewAppPackageDataAsync(Context c, String p_id) {
        this.context = c;
        try {
            WebApisInterface apiService = WebApisClient.getClient().create(WebApisInterface.class);
            //AppLogger.getInstance().e("SEND_PACKAGE_INSTALL DATA==>", " =========== " + p_id);
            MainResponseModel responseMain = new Gson().fromJson(SharePreference.getInstance().getString(SharePreference.HomeData), MainResponseModel.class);
            Call<JsonObject> call = apiService.callApi(responseMain.getPackageInstallTrackingUrl(), responseMain.getPid(), responseMain.getOffer_id(), p_id, SharePreference.getInstance().getString(SharePreference.AdID), ApplicationController.getContext().getPackageName(), Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID), CommonMethodsUtils.getIpAddress(context));
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
            CommonMethodsUtils.dismissProgressLoader();
            e.printStackTrace();
        }
    }

    private void onPostExecute(JsonObject responseModel) {
        try {
            CommonMethodsUtils.logFirebaseEvent(context, "FeatureUsabilityItemId", "FeatureUsabilityEvent", "Package_Install", "Package Installed");
            //AppLogger.getInstance().e("RESPONSE", "" + responseModel.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
