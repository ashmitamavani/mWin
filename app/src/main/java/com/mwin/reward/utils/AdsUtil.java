package com.mwin.reward.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;

import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxAdListener;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.MaxReward;
import com.applovin.mediation.MaxRewardedAdListener;
import com.applovin.mediation.ads.MaxAppOpenAd;
import com.applovin.mediation.ads.MaxInterstitialAd;
import com.applovin.mediation.ads.MaxRewardedAd;
import com.google.gson.Gson;
import com.mwin.reward.ApplicationController;
import com.mwin.reward.async.models.MainResponseModel;
import com.mwin.reward.value.Constants;

public class AdsUtil {
    public static String adFailUrl;
    private static boolean isShowingAds = false;
    private static Activity currentActivity;
    private static AdShownListener adShownListener;
    private static AdShownListener adShownListenerInterstitial;
    private static MaxAppOpenAd appOpenAdAppLovin;
    private static MaxInterstitialAd interstitialAdAppLovin;
    private static MaxRewardedAd rewardedAppLovinAd;
    private static AdShownListener adShownListenerRewarded;
    private static VideoAdShownListener videoAdShownListenerInterstitial;
    private static VideoAdShownListener videoAdShownListenerRewarded;

    public static void showAppLovinInterstitialAd(Activity activity, VideoAdShownListener adShownListener1, boolean isFromVideoList) {
        try {
            currentActivity = activity;
            videoAdShownListenerInterstitial = adShownListener1;
            if (interstitialAdAppLovin != null && interstitialAdAppLovin.isReady() && !isIsShowingAds() && (currentActivity != null && !currentActivity.isFinishing()) && ActivityManager.appInForeground) {
                CommonMethodsUtils.showAdLoader(activity, "Loading video...");
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        CommonMethodsUtils.dismissAdLoader();
                        isShowingAds = true;
                        interstitialAdAppLovin.showAd();
                    }
                }, 1000);
            } else {
                if (interstitialAdAppLovin == null && CommonMethodsUtils.isLoadAppLovinInterstitialAds()) {
                    CommonMethodsUtils.showAdLoader(activity, "Loading video...");
                    loadAdMobInterstitialAds(true);
                } else {
                    if (videoAdShownListenerInterstitial != null) {
                        videoAdShownListenerInterstitial.onAdDismiss(false);
                        videoAdShownListenerInterstitial = null;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showAppLovinInterstitialAd(Activity activity, AdShownListener adShownListener1) {
        try {
            currentActivity = activity;
            adShownListenerInterstitial = adShownListener1;
            if (interstitialAdAppLovin != null && interstitialAdAppLovin.isReady() && !isIsShowingAds() && (currentActivity != null && !currentActivity.isFinishing()) && ActivityManager.appInForeground) {
                CommonMethodsUtils.showAdLoader(activity, "Loading video ads...");
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        CommonMethodsUtils.dismissAdLoader();
                        isShowingAds = true;
                        interstitialAdAppLovin.showAd();
                    }
                }, 1000);
            } else {
                if (interstitialAdAppLovin == null && CommonMethodsUtils.isLoadAppLovinInterstitialAds()) {
                    CommonMethodsUtils.showAdLoader(activity, "Loading video ads...");
                    loadAdMobInterstitialAds(true);
                } else {
                    if (adShownListenerInterstitial != null) {
                        adShownListenerInterstitial.onAdDismiss();
                        adShownListenerInterstitial = null;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void loadAdMobInterstitialAds(boolean isShowAdAfterLoading) {
        MainResponseModel responseMain = new Gson().fromJson(SharePreference.getInstance().getString(SharePreference.HomeData), MainResponseModel.class);
        if (CommonMethodsUtils.isLoadAppLovinInterstitialAds()) {
            interstitialAdAppLovin = new MaxInterstitialAd(CommonMethodsUtils.getRandomAdUnitId(responseMain.getLovinInterstitialID()), getCurrentActivity());
            interstitialAdAppLovin.setListener(new MaxAdListener() {
                @Override
                public void onAdLoaded(MaxAd ad) {
                    CommonMethodsUtils.dismissAdLoader();
                    if (isShowAdAfterLoading && interstitialAdAppLovin != null && interstitialAdAppLovin.isReady() && !isIsShowingAds() && (currentActivity != null && !currentActivity.isFinishing()) && ActivityManager.appInForeground) {
                        isShowingAds = true;
                        interstitialAdAppLovin.showAd();
                    }
                }

                @Override
                public void onAdDisplayed(MaxAd ad) {

                }

                @Override
                public void onAdHidden(MaxAd ad) {
                    CommonMethodsUtils.dismissAdLoader();
                    isShowingAds = false;
                    if (adShownListenerInterstitial != null) {
                        adShownListenerInterstitial.onAdDismiss();
                        adShownListenerInterstitial = null;
                    }
                    if (videoAdShownListenerInterstitial != null) {
                        videoAdShownListenerInterstitial.onAdDismiss(true);
                        videoAdShownListenerInterstitial = null;
                    }
                    interstitialAdAppLovin = null;
                    loadAdMobInterstitialAds(false);
                }

                @Override
                public void onAdClicked(MaxAd ad) {

                }

                @Override
                public void onAdLoadFailed(String adUnitId, MaxError error) {
                    CommonMethodsUtils.dismissAdLoader();
                    isShowingAds = false;
                    if (adShownListenerInterstitial != null) {
                        adShownListenerInterstitial.onAdDismiss();
                        adShownListenerInterstitial = null;
                    }
                    if (videoAdShownListenerInterstitial != null) {
                        videoAdShownListenerInterstitial.onAdDismiss(false);
                        videoAdShownListenerInterstitial = null;
                    }
                    interstitialAdAppLovin = null;
                    if (isShowAdAfterLoading) {
                        CommonMethodsUtils.openUrl(currentActivity, adFailUrl);
                    }
                }

                @Override
                public void onAdDisplayFailed(MaxAd ad, MaxError error) {
                    isShowingAds = false;
                    if (adShownListenerInterstitial != null) {
                        adShownListenerInterstitial.onAdDismiss();
                        adShownListenerInterstitial = null;
                    }
                    if (videoAdShownListenerInterstitial != null) {
                        videoAdShownListenerInterstitial.onAdDismiss(false);
                        videoAdShownListenerInterstitial = null;
                    }
                    interstitialAdAppLovin = null;
                    if (isShowAdAfterLoading) {
                        CommonMethodsUtils.openUrl(currentActivity, adFailUrl);
                    }
                    loadAdMobInterstitialAds(false);
                }
            });

            // Load the first ad
            interstitialAdAppLovin.loadAd();
        }
    }

    public static void loadAppOpenAdd(final Context context) {
        //AppLogger.getInstance().e("loadAppOpenAdd loadAppOpenAdd STARTED: ", "CALLED===");
        MainResponseModel responseMain = new Gson().fromJson(SharePreference.getInstance().getString(SharePreference.HomeData), MainResponseModel.class);
        if (CommonMethodsUtils.isShowAppLovinAppOpenAds()) {
            appOpenAdAppLovin = new MaxAppOpenAd(CommonMethodsUtils.getRandomAdUnitId(responseMain.getLovinAppOpenID()), ApplicationController.getContext());
            appOpenAdAppLovin.setListener(new MaxAdListener() {
                @Override
                public void onAdLoaded(MaxAd ad) {
                    try {
                        ApplicationController.getContext().sendBroadcast(new Intent(Constants.APP_OPEN_ADD_LOADED).setPackage(ApplicationController.getContext().getPackageName()));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onAdDisplayed(MaxAd ad) {
                    //AppLogger.getInstance().e("Applovin AppOpen onAdDisplayed: ", "CALLED===");
                }

                @Override
                public void onAdHidden(MaxAd ad) {
                    //AppLogger.getInstance().e("Applovin AppOpen onAdHidden: ", "CALLED===");
                    appOpenAdAppLovin = null;
                    isShowingAds = false;
                    dismissAppOpenAdListener();
                    loadAppOpenAdd(ApplicationController.getContext());
                }

                @Override
                public void onAdClicked(MaxAd ad) {
                    //AppLogger.getInstance().e("Applovin AppOpen onAdClicked: ", "CALLED===");
                }

                @Override
                public void onAdLoadFailed(String adUnitId, MaxError error) {
                    //AppLogger.getInstance().e("Applovin AppOpen onAdLoadFailed: ", "CALLED===");
                }

                @Override
                public void onAdDisplayFailed(MaxAd ad, MaxError error) {
                    //AppLogger.getInstance().e("Applovin AppOpen onAdDisplayFailed: ", "CALLED===");
                    dismissAppOpenAdListener();
                    isShowingAds = false;
                    loadAppOpenAdd(ApplicationController.getContext());
                }
            });
            appOpenAdAppLovin.loadAd();
        } else {
            dismissAppOpenAdListener();
            isShowingAds = false;
        }
    }

    private static void dismissAppOpenAdListener() {
        if (adShownListener != null) {
            adShownListener.onAdDismiss();
            adShownListener = null;
        } else {
            // User was not getting moved to main screen if app goes in background while displaying ap open ad as static adShownListener object was getting null when app goes in background
            ApplicationController.getContext().sendBroadcast(new Intent(Constants.APP_OPEN_ADD_DISMISSED).setPackage(ApplicationController.getContext().getPackageName()));
        }
    }

    public static void showAppOpenAdd(Activity activity, AdShownListener adShownListener1) {
        try {
            adShownListener = adShownListener1;
            if (appOpenAdAppLovin != null && appOpenAdAppLovin.isReady() && (activity != null && !activity.isFinishing()) && !isIsShowingAds() && ActivityManager.appInForeground) {
                isShowingAds = true;
                appOpenAdAppLovin.showAd();
            } else {
                if (adShownListener != null) {
                    adShownListener.onAdDismiss();
                    adShownListener = null;
                }
                if (CommonMethodsUtils.isShowAppLovinAppOpenAds() && appOpenAdAppLovin == null) {
                    loadAppOpenAdd(ApplicationController.getContext());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isIsShowingAds() {
        return isShowingAds;
    }

    public static Activity getCurrentActivity() {
        return currentActivity;
    }

    public static void setCurrentActivity(Activity activity) {
//        //AppLogger.getInstance().e("SET ACT1", "" + activity);
        if (!isShowingAds) {
            //AppLogger.getInstance().e("SET ACTIVITY", "=" + activity);
            currentActivity = activity;
        }
    }

    public static void loadRewardedAd(boolean isShowAdAfterLoading) {
        MainResponseModel responseMain = new Gson().fromJson(SharePreference.getInstance().getString(SharePreference.HomeData), MainResponseModel.class);
        if (CommonMethodsUtils.isLoadAppLovinRewardedAds()) {
            rewardedAppLovinAd = MaxRewardedAd.getInstance(CommonMethodsUtils.getRandomAdUnitId(responseMain.getLovinRewardID()), getCurrentActivity());
            rewardedAppLovinAd.setListener(new MaxRewardedAdListener() {
                @Override
                public void onUserRewarded(MaxAd ad, MaxReward reward) {

                }

                @Override
                public void onRewardedVideoStarted(MaxAd ad) {

                }

                @Override
                public void onRewardedVideoCompleted(MaxAd ad) {

                }

                @Override
                public void onAdLoaded(MaxAd ad) {
                    //AppLogger.getInstance().e("APPLOVIN REWARDED", "REWARD :onAdLoaded");
                    CommonMethodsUtils.dismissAdLoader();
                    if (isShowAdAfterLoading && rewardedAppLovinAd != null && rewardedAppLovinAd.isReady() && !isIsShowingAds() && (currentActivity != null && !currentActivity.isFinishing()) && ActivityManager.appInForeground) {
                        //AppLogger.getInstance().e("APPLOVIN REWARDED SHOW WHEN LOADED===", "REWARD :onAdLoaded");
                        isShowingAds = true;
                        rewardedAppLovinAd.showAd();
                    }
                }

                @Override
                public void onAdDisplayed(MaxAd ad) {

                }

                @Override
                public void onAdHidden(MaxAd ad) {
                    //AppLogger.getInstance().e("APPLOVIN REWARDED ", "REWARD INST:onAdHidden");
                    isShowingAds = false;
                    if (adShownListenerRewarded != null) {
                        adShownListenerRewarded.onAdDismiss();
                        adShownListenerRewarded = null;
                    }
                    if (videoAdShownListenerRewarded != null) {
                        videoAdShownListenerRewarded.onAdDismiss(true);
                        videoAdShownListenerRewarded = null;
                    }
                    rewardedAppLovinAd = null;
                    loadRewardedAd(false);
                }

                @Override
                public void onAdClicked(MaxAd ad) {

                }

                @Override
                public void onAdLoadFailed(String adUnitId, MaxError error) {
                    CommonMethodsUtils.dismissAdLoader();
                    //AppLogger.getInstance().e("APPLOVIN REWARDED FAIL", "REWARD INST:onAdLoadFailed");
                    isShowingAds = false;
                    if (adShownListenerRewarded != null) {
                        adShownListenerRewarded.onAdDismiss();
                        adShownListenerRewarded = null;
                    }
                    if (videoAdShownListenerRewarded != null) {
                        videoAdShownListenerRewarded.onAdDismiss(false);
                        videoAdShownListenerRewarded = null;
                    }
                    rewardedAppLovinAd = null;
                    if (isShowAdAfterLoading) {
                        CommonMethodsUtils.openUrl(currentActivity, adFailUrl);
                    }
                }

                @Override
                public void onAdDisplayFailed(MaxAd ad, MaxError error) {
                    //AppLogger.getInstance().e("APPLOVIN REWARDED FAIL", "REWARD INST:onAdDisplayFailed");
                    isShowingAds = false;
                    if (adShownListenerRewarded != null) {
                        adShownListenerRewarded.onAdDismiss();
                        adShownListenerRewarded = null;
                    }
                    if (videoAdShownListenerRewarded != null) {
                        videoAdShownListenerRewarded.onAdDismiss(false);
                        videoAdShownListenerRewarded = null;
                    }
                    rewardedAppLovinAd = null;
                    if (isShowAdAfterLoading) {
                        CommonMethodsUtils.openUrl(currentActivity, adFailUrl);
                    }
                    loadRewardedAd(false);
                }
            });
            rewardedAppLovinAd.loadAd();
        }
    }

    public static void showAppLovinRewardedAd(Activity activity, VideoAdShownListener adShownListener1, boolean isFromVideoList) {
        try {
            currentActivity = activity;
            videoAdShownListenerRewarded = adShownListener1;
            if (rewardedAppLovinAd != null && rewardedAppLovinAd.isReady() && !isIsShowingAds() && (activity != null && !activity.isFinishing()) && ActivityManager.appInForeground) {
                CommonMethodsUtils.showAdLoader(activity, "Loading video...");
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        CommonMethodsUtils.dismissAdLoader();
                        isShowingAds = true;
                        rewardedAppLovinAd.showAd();
                    }
                }, 1000);
            } else {
                if (rewardedAppLovinAd == null && CommonMethodsUtils.isLoadAppLovinRewardedAds()) {
                    CommonMethodsUtils.showAdLoader(activity, "Loading video...");
                    loadRewardedAd(true);
                } else {
                    if (videoAdShownListenerRewarded != null) {
                        videoAdShownListenerRewarded.onAdDismiss(false);
                        videoAdShownListenerRewarded = null;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showAppLovinRewardedAd(Activity activity, AdShownListener adShownListener1) {
        try {
            currentActivity = activity;
            adShownListenerRewarded = adShownListener1;
            if (rewardedAppLovinAd != null && rewardedAppLovinAd.isReady() && !isIsShowingAds() && (activity != null && !activity.isFinishing()) && ActivityManager.appInForeground) {
                CommonMethodsUtils.showAdLoader(activity, "Loading video ads...");
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        CommonMethodsUtils.dismissAdLoader();
                        isShowingAds = true;
                        rewardedAppLovinAd.showAd();
                    }
                }, 1000);
            } else {
                if (rewardedAppLovinAd == null && CommonMethodsUtils.isLoadAppLovinRewardedAds()) {
                    CommonMethodsUtils.showAdLoader(activity, "Loading video ads...");
                    loadRewardedAd(true);
                } else {
                    if (adShownListenerRewarded != null) {
                        adShownListenerRewarded.onAdDismiss();
                        adShownListenerRewarded = null;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public interface AdShownListener {
        void onAdDismiss();
    }

    public interface VideoAdShownListener {
        void onAdDismiss(boolean isAdShown);
    }
}