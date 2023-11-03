package com.mwin.reward;

import android.app.Activity;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.ProcessLifecycleOwner;

import com.appluck.sdk.AppLuckSDK;
import com.google.firebase.messaging.FirebaseMessaging;
import com.makeopinion.cpxresearchlib.CPXResearch;
import com.makeopinion.cpxresearchlib.models.CPXConfiguration;
import com.makeopinion.cpxresearchlib.models.CPXConfigurationBuilder;
import com.makeopinion.cpxresearchlib.models.CPXStyleConfiguration;
import com.makeopinion.cpxresearchlib.models.SurveyPosition;
import com.mwin.reward.utils.ActivityManager;
import com.mwin.reward.utils.AppLogger;
import com.mwin.reward.utils.SharePreference;
import com.onesignal.OneSignal;
import com.onesignal.debug.LogLevel;
import com.pubscale.sdkone.offerwall.OfferWall;
import com.pubscale.sdkone.offerwall.OfferWallConfig;
import com.pubscale.sdkone.offerwall.models.OfferWallInitListener;
import com.pubscale.sdkone.offerwall.models.errors.InitError;

import io.adjoe.sdk.Adjoe;


public class ApplicationController extends Application {
    private static final String ONESIGNAL_APP_ID = "dcf36a4a-ee8b-4512-9343-dd5f7dc4ed79";
    public static Context mContext;
    public static BroadcastReceiver packageInstallBroadcast;

    static {
        System.loadLibrary("reward");
    }

    private CPXResearch cpxResearch;

    public static Context getContext() {
        return mContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (Adjoe.isAdjoeProcess()) {
            // the method is executed on the adjoe process
            return;
        }
        mContext = this;

        ActivityManager activityManager = new ActivityManager();
        registerActivityLifecycleCallbacks(activityManager);
        ProcessLifecycleOwner.get().getLifecycle().addObserver(activityManager);

        FirebaseMessaging.getInstance().subscribeToTopic("global");
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            String version = pInfo.versionName;
            FirebaseMessaging.getInstance().subscribeToTopic("globalV" + version);
            SharePreference.getInstance().putString(SharePreference.AppVersion, version);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        // Enable verbose OneSignal logging to debug issues if needed.
        OneSignal.getDebug().setLogLevel(LogLevel.NONE);
        OneSignal.initWithContext(this, ONESIGNAL_APP_ID);
    }

    @NonNull
    public CPXResearch getCpxResearch() {
        return cpxResearch;
    }

    public void setUpAppLuckSDK(Activity activity, String defaultAdUnit, String interstitialAdUnit, String incentiveAdUnit) {
        AppLuckSDK.setListener(new AppLuckSDK.AppLuckSDKListener() {
            @Override
            public void onInitSuccess() {
                AppLuckSDK.loadPlacement(defaultAdUnit, "icon", 150, 150);
                AppLuckSDK.loadPlacement(interstitialAdUnit, "icon", 150, 150);
                AppLuckSDK.loadPlacement(incentiveAdUnit, "icon", 150, 150);
            }

            @Override
            public void onInitFailed(Error error) {
                Log.e("AppLuckSDK", "Init Failed.", error);
            }

            @Override
            public void onPlacementLoadSuccess(String placementId) {
            }

            @Override
            public void onInteractiveAdsHidden(String placementId, int i) {
                // 0 = ordinary close, 1 = finish the goal of times
//                if (placementId.equals(incentiveAdUnit)) {
//                    if (i == 1)
//                        Toast.makeText(MyApplication.this, placementId + " WIN", Toast.LENGTH_SHORT).show();
//                    else
//                        Toast.makeText(MyApplication.this, placementId + " BETTER LUCK", Toast.LENGTH_SHORT).show();
//                }
            }

            @Override
            public void onInteractiveAdsDisplayed(String placementId) {

            }

            @Override
            public void onUserInteraction(String placementId, String interaction) {
            }
        });
        AppLuckSDK.init(defaultAdUnit);
    }

    public void startCPX() {
        CPXStyleConfiguration style = new CPXStyleConfiguration(SurveyPosition.CornerBottomRight,
                "Earn up to<br> 3 Points",
                18,
                "#ffffff",
                "#ffaf20",
                true);

        String userID = !SharePreference.getInstance().getBoolean(SharePreference.IS_LOGIN) ? "0" : SharePreference.getInstance().getString(SharePreference.userId);

        CPXConfiguration config = new CPXConfigurationBuilder("20057",
                userID,
                "ElSQq5sGSlzIgLVkgi6U6gDO18mSfoBk",
                style)
                .build();

        cpxResearch = CPXResearch.Companion.init(config);
    }
    public void initPubScale() {
        String userID = !SharePreference.getInstance().getBoolean(SharePreference.IS_LOGIN) ? "0" : SharePreference.getInstance().getString(SharePreference.userId);
        Bitmap bg = Bitmap.createBitmap(600, 300, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bg);
        canvas.drawColor(getColor(R.color.colorPrimaryDark));
        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher_foreground);
        OfferWallConfig offerWallConfig =
                new OfferWallConfig.Builder(getContext(), "68055479")
                        .setUniqueId(userID) //optional, used to represent the user of your application
                        .setLoaderBackgroundBitmap(bg)//optional
                        .setLoaderForegroundBitmap(icon)//optional
                        .setFullscreenEnabled(false)//optional
                        .build();
        try {
            OfferWall.destroy();
        } catch (Exception e) {
            e.printStackTrace();
        }
        OfferWall.init(offerWallConfig, new OfferWallInitListener() {
            @Override
            public void onInitSuccess() {
                AppLogger.getInstance().e("INIT", "PUBSCALE SUCCESS==========");
            }

            @Override
            public void onInitFailed(InitError initError) {
                AppLogger.getInstance().e("INIT", "PUBSCALE FAIL==========");
            }
        });
    }
}
