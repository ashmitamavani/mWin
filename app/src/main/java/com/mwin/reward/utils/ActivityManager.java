package com.mwin.reward.utils;

import android.app.Activity;
import android.app.Application;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

import com.mwin.reward.ApplicationController;
import com.mwin.reward.activity.MySplashActivity;
import com.mwin.reward.value.Constants;

import java.util.List;

import io.adjoe.sdk.AdjoeActivity;

public class ActivityManager implements Application.ActivityLifecycleCallbacks, LifecycleObserver {
    public static boolean appInForeground, isShowAppOpenAd = true;
    public static boolean isStartTimer = false;
    public static int timeToWatchInSeconds;
    private int numberActivitiesStart = 0;
    private String TAG = this.getClass().getSimpleName();
    private long lastTime, newTime;

    /**
     * LifecycleObserver method that shows the app open ad when the app moves to foreground.
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    protected void onMoveToForeground() {
        // Show the ad (if available) when the app moves to foreground.
        try {
            //AppLogger.getInstance().e(TAG, "onMoveToForeground===  app went to foreground");
//            //AppLogger.getInstance().e(TAG, "isStartTimer: " + isStartTimer + " timeSpent : " + timeSpent);
            if (isStartTimer) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        long currentTime = System.currentTimeMillis();
                        UsageStatsManager mUsageStatsManager = (UsageStatsManager) AdsUtil.getCurrentActivity().getSystemService(Context.USAGE_STATS_SERVICE);
                        final List<UsageStats> stats =
                                mUsageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_BEST,
                                        currentTime - (1000 * 25), currentTime);
                        if (stats != null) {
                            final int statCount = stats.size();
                            for (int i = 0; i < statCount; i++) {
                                final android.app.usage.UsageStats pkgStats = stats.get(i);
                                if (pkgStats.getPackageName().equals("com.android.chrome")) {
                                    newTime = pkgStats.getTotalTimeInForeground() / 1000;
                                    break;
                                }
                            }
                        }
                        //AppLogger.getInstance().e(TAG, "OLD: " + lastTime + " == NEW : " + newTime + " DIFF : " + (newTime - lastTime));
                        if (newTime - lastTime >= timeToWatchInSeconds) {
                            ApplicationController.getContext().sendBroadcast(new Intent(Constants.WATCH_WEBSITE_RESULT)
                                    .setPackage(ApplicationController.getContext().getPackageName()).putExtra("status", Constants.STATUS_SUCCESS));
//                            CommonUtils.NotifySuccess(AdsUtils.getCurrentActivity(), "Watch Website", "Congratulations! 20 Seconds completed!", false);
                        } else {
                            ApplicationController.getContext().sendBroadcast(new Intent(Constants.WATCH_WEBSITE_RESULT)
                                    .setPackage(ApplicationController.getContext().getPackageName()).putExtra("status", Constants.STATUS_ERROR));
//                            CommonUtils.Notify(AdsUtils.getCurrentActivity(), "Watch Website", "Oops - 20 Seconds NOT completed!", false);
                        }
                        lastTime = 0;
                        newTime = 0;
                    }
                }, 500);
                isStartTimer = false;
            }
            if (!(AdsUtil.getCurrentActivity() instanceof MySplashActivity) && !(AdsUtil.getCurrentActivity() instanceof AdjoeActivity) && isShowAppOpenAd)
                AdsUtil.showAppOpenAdd(AdsUtil.getCurrentActivity(), null);
            isShowAppOpenAd = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle bundle) {
    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {
        try {
            AdsUtil.setCurrentActivity(activity);
            if (numberActivitiesStart == 0) {
                // The application come from background to foreground
                //AppLogger.getInstance().e(TAG, "AppController status > onActivityStarted:  app went to foreground");
                if (!appInForeground) {
                    appInForeground = true;
                }
            }
            numberActivitiesStart++;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {
        AdsUtil.setCurrentActivity(activity);
    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {

    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {
        try {
            numberActivitiesStart--;
            if (numberActivitiesStart == 0) {
                // The application go from foreground to background
                appInForeground = false;
                //AppLogger.getInstance().e(TAG, "AppController status > onActivityStopped: app went to background");
                if (isStartTimer) {
                    lastTime = 0;
                    newTime = 0;
                    long currentTime = System.currentTimeMillis();
                    UsageStatsManager mUsageStatsManager = (UsageStatsManager) activity.getSystemService(Context.USAGE_STATS_SERVICE);
                    final List<UsageStats> stats =
                            mUsageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_BEST,
                                    currentTime - (1000 * 30), currentTime);
                    if (stats != null) {
                        final int statCount = stats.size();
                        for (int i = 0; i < statCount; i++) {
                            final android.app.usage.UsageStats pkgStats = stats.get(i);
                            if (pkgStats.getPackageName().equals("com.android.chrome")) {
                                lastTime = pkgStats.getTotalTimeInForeground() / 1000;
                                break;
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle bundle) {

    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {

    }
}
