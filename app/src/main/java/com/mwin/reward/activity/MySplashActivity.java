package com.mwin.reward.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.mwin.reward.ApplicationController;
import com.mwin.reward.R;
import com.mwin.reward.async.models.ApisResponse;
import com.mwin.reward.async.models.MainResponseModel;
import com.mwin.reward.network.WebApisClient;
import com.mwin.reward.network.WebApisInterface;
import com.mwin.reward.utils.AESCipher;
import com.mwin.reward.utils.AdsUtil;
import com.mwin.reward.utils.AppLogger;
import com.mwin.reward.utils.CommonMethodsUtils;
import com.mwin.reward.utils.SharePreference;
import com.mwin.reward.value.Constants;
import com.pubscale.sdkone.offerwall.OfferWall;
import com.pubscale.sdkone.offerwall.OfferWallConfig;
import com.pubscale.sdkone.offerwall.models.OfferWallInitListener;
import com.pubscale.sdkone.offerwall.models.errors.InitError;

import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MySplashActivity extends AppCompatActivity {
    private Handler handler;
    private BroadcastReceiver appOpenAddLoadedBroadcast;
    private IntentFilter intentFilterActivities;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        CommonMethodsUtils.setDayNightTheme(MySplashActivity.this);
        setContentView(R.layout.activity_my_splash);

        if (SharePreference.getInstance().getString(SharePreference.appOpenDate).length() == 0 || !SharePreference.getInstance().getString(SharePreference.appOpenDate).equals(CommonMethodsUtils.getCurrentDate())) {
            SharePreference.getInstance().putString(SharePreference.appOpenDate, CommonMethodsUtils.getCurrentDate());
            SharePreference.getInstance().putInt(SharePreference.todayOpen, 1);
            SharePreference.getInstance().putInt(SharePreference.totalOpen, (SharePreference.getInstance().getInt(SharePreference.totalOpen) + 1));
        } else {
            SharePreference.getInstance().putInt(SharePreference.todayOpen, (SharePreference.getInstance().getInt(SharePreference.todayOpen) + 1));
        }
        try {
            Bundle extras = getIntent().getExtras();
            if (extras != null && (extras.containsKey("bundle") && extras.getString("bundle").trim().length() > 0)) {
                SharePreference.getInstance().putBoolean(SharePreference.isFromNotification, true);
                SharePreference.getInstance().putString(SharePreference.notificationData, getIntent().getExtras().getString("bundle"));
            } else {
                SharePreference.getInstance().putBoolean(SharePreference.isFromNotification, false);
            }
        } catch (Exception e2) {
            e2.printStackTrace();
            SharePreference.getInstance().putBoolean(SharePreference.isFromNotification, false);
        }
        if (SharePreference.getInstance().getInt(SharePreference.totalOpen) > 1) {
            ImageView ivFooterImage = findViewById(R.id.ivFooterImage);
            ivFooterImage.setVisibility(View.VISIBLE);
        }
        FirebaseDynamicLinks.getInstance()
                .getDynamicLink(getIntent())
                .addOnSuccessListener(this, new OnSuccessListener<PendingDynamicLinkData>() {
                    @Override
                    public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
                        Uri deepLink = null;
                        if (pendingDynamicLinkData != null) {
                            deepLink = pendingDynamicLinkData.getLink();
                            try {
                                if (deepLink != null) {
                                    String str = new Gson().toJson(CommonMethodsUtils.splitQuery(new URL(deepLink.toString())));
                                    SharePreference.getInstance().putString(SharePreference.ReferData, str);
                                } else {
//                                    SharePreference.getInstance().putString(SharePreference.ReferData, "");
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        //AppLogger.getInstance().e("DEEP LINK:", "====" + deepLink);
                        if (SharePreference.getInstance().getInt(SharePreference.totalOpen) == 1) {
                            new GetHomeDataAsync(MySplashActivity.this);
                        }
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
//                        SharePreference.getInstance().putString(SharePreference.ReferData, "");
                        if (SharePreference.getInstance().getInt(SharePreference.totalOpen) == 1) {
                            new GetHomeDataAsync(MySplashActivity.this);
                        }
                    }
                });

        AsyncTask<Void, Void, String> task = new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                AdvertisingIdClient.Info idInfo = null;
                try {
                    idInfo = AdvertisingIdClient.getAdvertisingIdInfo(getApplicationContext());
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                String advertId = null;
                try {
                    advertId = idInfo.getId();
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
                return advertId;
            }

            @Override
            protected void onPostExecute(String advertId) {
                SharePreference.getInstance().putString(SharePreference.AdID, advertId);
            }
        };
        task.execute();

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            return;
                        }
                        String token = task.getResult();
                        SharePreference.getInstance().putString(SharePreference.FCMregId, token);
                    }
                });
        if (SharePreference.getInstance().getInt(SharePreference.totalOpen) != 1) {
            new GetHomeDataAsync(MySplashActivity.this);
        }
    }

    private void gotoMainActivity() {
        try {
//            //AppLogger.getInstance().e("SplashActivity", "Show ad & Go to Main screen");
            removeHandler();
            AdsUtil.showAppOpenAdd(MySplashActivity.this, new AdsUtil.AdShownListener() {
                @Override
                public void onAdDismiss() {
//                    //AppLogger.getInstance().e("SplashActivity", "onAdDismiss");
                    if (appOpenAddLoadedBroadcast != null) {
                        moveToMainScreen();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void moveToMainScreen() {
        try {
            //AppLogger.getInstance().e("SplashActivity", " Move to Main screen");
            unRegisterReceivers();
            if (!SharePreference.getInstance().getBoolean(SharePreference.IS_USER_CONSENT_ACCEPTED)) {
                startActivity(new Intent(MySplashActivity.this, UserAgreementActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
            } else if (!SharePreference.getInstance().getBoolean(SharePreference.IS_LOGIN) && !SharePreference.getInstance().getBoolean(SharePreference.IS_SKIPPED_LOGIN)) {
                startActivity(new Intent(MySplashActivity.this, SigninActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
            } else {
                startActivity(new Intent(MySplashActivity.this, HomeActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
//        //AppLogger.getInstance().e("SplashActivity", "onStart");
        registerBroadcast();
    }

    @Override
    protected void onStop() {
        super.onStop();
//        //AppLogger.getInstance().e("SplashActivity", "onStop");
        unRegisterReceivers();
    }

    private void registerBroadcast() {
        appOpenAddLoadedBroadcast = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //AppLogger.getInstance().e("SplashActivity", "Broadcast Received==" + intent.getAction());
                if (intent.getAction().equals(Constants.APP_OPEN_ADD_DISMISSED)) {
                    removeHandler();
                    moveToMainScreen();
                } else {
                    gotoMainActivity();
                }
            }
        };
        intentFilterActivities = new IntentFilter();
        intentFilterActivities.addAction(Constants.APP_OPEN_ADD_LOADED);
        intentFilterActivities.addAction(Constants.APP_OPEN_ADD_DISMISSED);
        registerReceiver(appOpenAddLoadedBroadcast, intentFilterActivities);
    }

    private void unRegisterReceivers() {
        if (appOpenAddLoadedBroadcast != null) {
//            //AppLogger.getInstance().e("SplashActivity", "Unregister Broadcast");
            unregisterReceiver(appOpenAddLoadedBroadcast);
            appOpenAddLoadedBroadcast = null;
        }
    }

    @Override
    public void onBackPressed() {
//        //AppLogger.getInstance().e("SplashActivity", "onBackPress");
        removeHandler();
        unRegisterReceivers();
        super.onBackPressed();
        System.exit(0);
    }

    private void removeHandler() {
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }

    public class GetHomeDataAsync {
        private Activity activity;
        private JSONObject jObject;
        private AESCipher cipher;

        public GetHomeDataAsync(final Activity activity) {
            this.activity = activity;
            cipher = new AESCipher();
            try {
                //AppLogger.getInstance().e("FCM===", SharePreference.getInstance().getString(SharePreference.FCMregId));
                jObject = new JSONObject();
                jObject.put("GKD6MP", SharePreference.getInstance().getString(SharePreference.FCMregId));
                jObject.put("WX4OGO", SharePreference.getInstance().getString(SharePreference.AdID));
                jObject.put("YDHDTM", Build.MODEL);
                jObject.put("YEH0AC", Build.VERSION.RELEASE);
                jObject.put("VMOJ19", SharePreference.getInstance().getString(SharePreference.AppVersion));
                jObject.put("WVYKWW", SharePreference.getInstance().getInt(SharePreference.totalOpen));
                jObject.put("NB6V3W", SharePreference.getInstance().getInt(SharePreference.todayOpen));
                jObject.put("GO8PLO", CommonMethodsUtils.verifyInstallerId(activity));
                jObject.put("AVW44X", Settings.Secure.getString(activity.getContentResolver(), Settings.Secure.ANDROID_ID));
//                jObject.put("device_id", Settings.Secure.getString(activity.getContentResolver(), Settings.Secure.ANDROID_ID));
                if (SharePreference.getInstance().getBoolean(SharePreference.IS_LOGIN)) {
                    jObject.put("J8LO8H", SharePreference.getInstance().getString(SharePreference.userId));
                    jObject.put("TAMATU", SharePreference.getInstance().getString(SharePreference.userToken));
                }
                int n = CommonMethodsUtils.getRandomNumberBetweenRange(1, 1000000);
                jObject.put("RANDOM", n);
                AppLogger.getInstance().e("DATA ", "===" + jObject.toString());
                AppLogger.getInstance().e("DATA ENCRYPTED", "===" + cipher.bytesToHex(cipher.encrypt(jObject.toString())));

                WebApisInterface apiService = WebApisClient.getClient().create(WebApisInterface.class);
                Call<ApisResponse> call = apiService.getHomeData(SharePreference.getInstance().getString(SharePreference.userToken), String.valueOf(n), cipher.bytesToHex(cipher.encrypt(jObject.toString())));
                call.enqueue(new Callback<ApisResponse>() {
                    @Override
                    public void onResponse(Call<ApisResponse> call, Response<ApisResponse> response) {
                        onPostExecute(response.body());
                    }

                    @Override
                    public void onFailure(Call<ApisResponse> call, Throwable t) {
                        if (!call.isCanceled()) {
                            CommonMethodsUtils.Notify(activity, getString(R.string.app_name), Constants.msg_Service_Error, true);
                        }
                    }
                });
            } catch (Exception e) {

                e.printStackTrace();
            }
        }

        private void onPostExecute(ApisResponse response1) {
            try {
                MainResponseModel response = new Gson().fromJson(new String(cipher.decrypt(response1.getEncrypt())), MainResponseModel.class);
                AdsUtil.adFailUrl = response.getAdFailUrl();
                if (!CommonMethodsUtils.isStringNullOrEmpty(response.getUserToken())) {
                    SharePreference.getInstance().putString(SharePreference.userToken, response.getUserToken());
                }
                if (response.getStatus().equals(Constants.STATUS_LOGOUT)) {
                    CommonMethodsUtils.doLogout(activity);
                } else if (response.getStatus().equals(Constants.STATUS_SUCCESS)) {
                    if (!CommonMethodsUtils.isStringNullOrEmpty(response.getEarningPoint())) {
                        SharePreference.getInstance().putString(SharePreference.EarnedPoints, response.getEarningPoint());
                    }
                    //                    Log.e("HomeData1--)",""+response.getIsshowPlaytimeIcone());
                    if (!CommonMethodsUtils.isStringNullOrEmpty(response.getIsShowSurvey()) && response.getIsShowSurvey().matches("1")) {
                        ApplicationController app = (ApplicationController) getApplication();
                        app.startCPX();
                    }
                    if (!CommonMethodsUtils.isStringNullOrEmpty(response.getIsShowAppluck()) && response.getIsShowAppluck().matches("1")) {
                        ApplicationController app = (ApplicationController) getApplication();
                        app.setUpAppLuckSDK(MySplashActivity.this, response.getDefaultAppluckID(), response.getInterAppluckID(), response.getIncentiveAppluckID());
                    }
                    if (!CommonMethodsUtils.isStringNullOrEmpty(response.getIsShowPubScale()) && response.getIsShowPubScale().matches("1")) {
                        ApplicationController app = (ApplicationController) getApplication();
                        app.initPubScale();
                    }
                    SharePreference.getInstance().putString(SharePreference.isShowWhatsAppAuth, response.getIsShowWhatsAppAuth());
                    SharePreference.getInstance().putString(SharePreference.fakeEarningPoint, response.getFakeEarningPoint());
                    SharePreference.getInstance().putString(SharePreference.HomeData, new Gson().toJson(response));
                    handler = new Handler();
                    if (CommonMethodsUtils.isShowAppLovinAds() && !SharePreference.getInstance().getBoolean(SharePreference.isFromNotification) && SharePreference.getInstance().getBoolean(SharePreference.IS_LOGIN)) {
                        CommonMethodsUtils.InitializeApplovinSDK();
                        handler.postDelayed(MySplashActivity.this::gotoMainActivity, 8000);
                    } else {
                        handler.postDelayed(MySplashActivity.this::moveToMainScreen, 2000);
                    }
                } else if (response.getStatus().equals(Constants.STATUS_ERROR)) {
                    CommonMethodsUtils.Notify(activity, getString(R.string.app_name), response.getMessage(), true);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}