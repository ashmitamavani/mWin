/*
 * Copyright (c) 2021.  Hurricane Development Studios
 */

package com.mwin.reward.activity;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.nativeAds.MaxNativeAdListener;
import com.applovin.mediation.nativeAds.MaxNativeAdLoader;
import com.applovin.mediation.nativeAds.MaxNativeAdView;
import com.google.gson.Gson;
import com.mwin.reward.R;
import com.mwin.reward.adapter.ScratchCardsListAdapter;
import com.mwin.reward.adapter.SeeVideoListAdapter;
import com.mwin.reward.async.GetSeeVideoListAsync;
import com.mwin.reward.async.SaveSeeVideoListAsync;
import com.mwin.reward.async.models.MainResponseModel;
import com.mwin.reward.async.models.SeeVideoList;
import com.mwin.reward.async.models.SeeVideoModel;
import com.mwin.reward.utils.AdsUtil;
import com.mwin.reward.utils.CommonMethodsUtils;
import com.mwin.reward.utils.SharePreference;
import com.mwin.reward.value.Constants;

import java.util.ArrayList;
import java.util.List;

public class SeeVideoListActivity extends AppCompatActivity {
    public List<SeeVideoList> listVideos = new ArrayList<>();
    private LottieAnimationView ivLottieNoData;
    private RecyclerView rvVideoList;
    private TextView lblLoadingAds, tvPoints;
    private MainResponseModel responseMain;
    private MaxAd nativeAd, nativeAdWin, nativeAdTask;
    private MaxNativeAdLoader nativeAdLoader, nativeAdLoaderWin, nativeAdLoaderTask;
    private FrameLayout frameLayoutNativeAd;
    private LinearLayout layoutAds;
    private SeeVideoListAdapter mAdapter;
    private ImageView ivHistory;
    private LinearLayout layoutPoints;
    private String todayDate, lastDate, watchTime;
    private CountDownTimer timer;
    private int time, activeVideoPos = -1;
    private boolean isTimerSet = false, isSetTimerValue = false;
    private RelativeLayout layoutMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        CommonMethodsUtils.setDayNightTheme(SeeVideoListActivity.this);
        setContentView(R.layout.activity_see_video_list);
        responseMain = new Gson().fromJson(SharePreference.getInstance().getString(SharePreference.HomeData), MainResponseModel.class);

        layoutMain = findViewById(R.id.layoutMain);

        ivLottieNoData = findViewById(R.id.ivLottieNoData);
        rvVideoList = findViewById(R.id.rvVideoList);

        ivHistory = findViewById(R.id.ivHistory);
        CommonMethodsUtils.startRoundAnimation(SeeVideoListActivity.this, ivHistory);
        ivHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SharePreference.getInstance().getBoolean(SharePreference.IS_LOGIN)) {
                    startActivity(new Intent(SeeVideoListActivity.this, EarnedPointHistoryActivity.class)
                            .putExtra("type", Constants.HistoryType.WATCH_VIDEO)
                            .putExtra("title", "Watch Video History"));
                } else {
                    CommonMethodsUtils.NotifyLogin(SeeVideoListActivity.this);
                }
            }
        });
        layoutPoints = findViewById(R.id.layoutPoints);
        layoutPoints.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SharePreference.getInstance().getBoolean(SharePreference.IS_LOGIN)) {
                    startActivity(new Intent(SeeVideoListActivity.this, EarnedWalletBalanceActivity.class));
                } else {
                    CommonMethodsUtils.NotifyLogin(SeeVideoListActivity.this);
                }
            }
        });
        tvPoints = findViewById(R.id.tvPoints);
        tvPoints.setText(SharePreference.getInstance().getEarningPointString());
        ImageView imBack = findViewById(R.id.ivBack);
        imBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        new GetSeeVideoListAsync(SeeVideoListActivity.this);
    }

    public void changeVideoDataValues(SeeVideoModel responseModel) {
        SharePreference.getInstance().putString(SharePreference.EarnedPoints, responseModel.getEarningPoint());
        if (responseModel.getTodayDate() != null) {
            todayDate = responseModel.getTodayDate();
        }
        if (responseModel.getLastVideoWatchedDate() != null) {
            lastDate = responseModel.getLastVideoWatchedDate();
        }
        if (responseModel.getWatchTime() != null) {
            watchTime = responseModel.getWatchTime();
        }
        CommonMethodsUtils.logFirebaseEvent(SeeVideoListActivity.this, "FeatureUsabilityItemId", "FeatureUsabilityEvent", "Watch_Video", "Watch Video Got Reward");
        showWinPopup(listVideos.get(activeVideoPos).getVideoPoints(), responseModel.getIsShowAds());
    }

    public void showWinPopup(String point, String isShowAds) {
        try {
            Dialog dialogWin = new Dialog(SeeVideoListActivity.this, android.R.style.Theme_Light);
            dialogWin.getWindow().setBackgroundDrawableResource(R.color.black_transparent);
            dialogWin.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialogWin.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            dialogWin.setCancelable(false);
            dialogWin.setCanceledOnTouchOutside(false);
            dialogWin.setContentView(R.layout.popup_win);
            dialogWin.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

            FrameLayout fl_adplaceholder = dialogWin.findViewById(R.id.fl_adplaceholder);
            TextView lblLoadingAds = dialogWin.findViewById(R.id.lblLoadingAds);
            if (CommonMethodsUtils.isShowAppLovinNativeAds()) {
                loadAppLovinNativeAds(fl_adplaceholder, lblLoadingAds);
            } else {
                lblLoadingAds.setVisibility(View.GONE);
            }

            TextView tvPoint = dialogWin.findViewById(R.id.tvPoints);

            LottieAnimationView animation_view = dialogWin.findViewById(R.id.animation_view);
            CommonMethodsUtils.setLottieAnimation(animation_view, responseMain.getCelebrationLottieUrl());
            animation_view.addAnimatorListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation, boolean isReverse) {
                    super.onAnimationStart(animation, isReverse);
                    CommonMethodsUtils.startTextCountAnimation(tvPoint, point);
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    animation_view.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationStart(Animator animation) {
                    super.onAnimationStart(animation);
                }
            });
            ImageView ivClose = dialogWin.findViewById(R.id.ivClose);
            ivClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (isShowAds != null && isShowAds.equals("1")) {
                        AdsUtil.showAppLovinInterstitialAd(SeeVideoListActivity.this, new AdsUtil.AdShownListener() {
                            @Override
                            public void onAdDismiss() {
                                if (dialogWin != null) {
                                    dialogWin.dismiss();
                                }
                            }
                        });
                    } else {
                        if (dialogWin != null) {
                            dialogWin.dismiss();
                        }
                    }
                }
            });

            TextView lblPoints = dialogWin.findViewById(R.id.lblPoints);
            AppCompatButton btnOk = dialogWin.findViewById(R.id.btnOk);
            try {
                int pt = Integer.parseInt(point);
                lblPoints.setText((pt <= 1 ? "Point" : "Points"));
            } catch (NumberFormatException e) {
                e.printStackTrace();
                lblPoints.setText("Points");
            }

            btnOk.setOnClickListener(v -> {
                AdsUtil.showAppLovinInterstitialAd(SeeVideoListActivity.this, new AdsUtil.AdShownListener() {
                    @Override
                    public void onAdDismiss() {
                        if (isShowAds != null && isShowAds.equals("1")) {
                            AdsUtil.showAppLovinInterstitialAd(SeeVideoListActivity.this, new AdsUtil.AdShownListener() {
                                @Override
                                public void onAdDismiss() {
                                    if (dialogWin != null) {
                                        dialogWin.dismiss();
                                    }
                                }
                            });
                        } else {
                            if (dialogWin != null) {
                                dialogWin.dismiss();
                            }
                        }
                    }
                });
            });
            dialogWin.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialogInterface) {
                    try {
                        CommonMethodsUtils.GetCoinAnimation(SeeVideoListActivity.this, layoutMain, layoutPoints);
                        tvPoints.setText(SharePreference.getInstance().getEarningPointString());
                        listVideos.get(activeVideoPos).setWatchedVideoPoints(point);
                        listVideos.get(activeVideoPos).setButtonText(null);
                        mAdapter.updateLastWatchedVideo(Integer.parseInt(listVideos.get(activeVideoPos).getVideoId()));
                        mAdapter.notifyItemChanged(activeVideoPos);
                        if (!CommonMethodsUtils.isStringNullOrEmpty(listVideos.get(activeVideoPos + 1).getVideoId())) { // Check for ads object
                            activeVideoPos = activeVideoPos + 1;
                        } else {
                            activeVideoPos = activeVideoPos + 2;
                        }
                        mAdapter.notifyItemChanged(activeVideoPos);
                        setTimer(false);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            if (!isFinishing() && !dialogWin.isShowing()) {
                dialogWin.show();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        animation_view.setVisibility(View.VISIBLE);
                        animation_view.playAnimation();
                    }
                }, 500);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadAppLovinNativeAds(FrameLayout frameLayoutNativeAd, TextView lblLoadingAds) {
        try {
            nativeAdLoaderWin = new MaxNativeAdLoader(CommonMethodsUtils.getRandomAdUnitId(responseMain.getLovinNativeID()), SeeVideoListActivity.this);
            nativeAdLoaderWin.setNativeAdListener(new MaxNativeAdListener() {
                @Override
                public void onNativeAdLoaded(final MaxNativeAdView nativeAdView, final MaxAd ad) {
                    if (nativeAdWin != null) {
                        nativeAdLoaderWin.destroy(nativeAdWin);
                    }
                    nativeAdWin = ad;
                    frameLayoutNativeAd.removeAllViews();
                    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) frameLayoutNativeAd.getLayoutParams();
                    params.height = getResources().getDimensionPixelSize(R.dimen.dim_300);
                    params.width = LinearLayout.LayoutParams.MATCH_PARENT;
                    frameLayoutNativeAd.setLayoutParams(params);
                    frameLayoutNativeAd.setPadding((int) getResources().getDimension(R.dimen.dim_10), (int) getResources().getDimension(R.dimen.dim_10), (int) getResources().getDimension(R.dimen.dim_10), (int) getResources().getDimension(R.dimen.dim_10));
                    lblLoadingAds.setVisibility(View.GONE);
                    frameLayoutNativeAd.addView(nativeAdView);
                    frameLayoutNativeAd.setVisibility(VISIBLE);
                    //AppLogger.getInstance().e("AppLovin Loaded WIN: ", "===WIN");
                }

                @Override
                public void onNativeAdLoadFailed(final String adUnitId, final MaxError error) {
                    //AppLogger.getInstance().e("AppLovin Failed WIN: ", error.getMessage());
                    frameLayoutNativeAd.setVisibility(View.GONE);
                    lblLoadingAds.setVisibility(View.GONE);
                }

                @Override
                public void onNativeAdClicked(final MaxAd ad) {

                }
            });
            nativeAdLoaderWin.loadAd();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadAppLovinNativeAdsTask(LinearLayout layoutAds, FrameLayout frameLayoutNativeAd, TextView lblLoadingAds) {
        try {
            nativeAdLoaderTask = new MaxNativeAdLoader(CommonMethodsUtils.getRandomAdUnitId(responseMain.getLovinNativeID()), SeeVideoListActivity.this);
            nativeAdLoaderTask.setNativeAdListener(new MaxNativeAdListener() {
                @Override
                public void onNativeAdLoaded(final MaxNativeAdView nativeAdView, final MaxAd ad) {
                    if (nativeAdTask != null) {
                        nativeAdLoaderTask.destroy(nativeAdTask);
                    }
                    nativeAdTask = ad;
                    frameLayoutNativeAd.removeAllViews();
                    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) frameLayoutNativeAd.getLayoutParams();
                    params.height = getResources().getDimensionPixelSize(R.dimen.dim_300);
                    params.width = LinearLayout.LayoutParams.MATCH_PARENT;
                    frameLayoutNativeAd.setLayoutParams(params);
                    frameLayoutNativeAd.setPadding((int) getResources().getDimension(R.dimen.dim_10), (int) getResources().getDimension(R.dimen.dim_10), (int) getResources().getDimension(R.dimen.dim_10), (int) getResources().getDimension(R.dimen.dim_10));
                    lblLoadingAds.setVisibility(View.GONE);
                    frameLayoutNativeAd.addView(nativeAdView);
                    frameLayoutNativeAd.setVisibility(VISIBLE);
                    //AppLogger.getInstance().e("AppLovin Loaded WIN: ", "===WIN");
                }

                @Override
                public void onNativeAdLoadFailed(final String adUnitId, final MaxError error) {
                    //AppLogger.getInstance().e("AppLovin Failed WIN: ", error.getMessage());
                    layoutAds.setVisibility(View.GONE);
                }

                @Override
                public void onNativeAdClicked(final MaxAd ad) {

                }
            });
            nativeAdLoaderTask.loadAd();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void onBackPressed() {
        try {
            if (responseModel != null && responseModel.getAppLuck() != null ) {
                
                CommonMethodsUtils.dialogShowAppLuck(SeeVideoListActivity.this, responseModel.getAppLuck());
            } else {
                super.onBackPressed();
            }
        } catch (Exception e) {
            e.printStackTrace();
            super.onBackPressed();
        }
    }

    private SeeVideoModel responseModel;

    public void setData(SeeVideoModel responseModel1) {
        responseModel = responseModel1;
        if (responseModel.getWatchVideoList() != null && responseModel.getWatchVideoList().size() > 0) {
            listVideos.addAll(responseModel.getWatchVideoList());
            if (responseModel.getTodayDate() != null) {
                todayDate = responseModel.getTodayDate();
            }
            if (responseModel.getLastVideoWatchedDate() != null) {
                lastDate = responseModel.getLastVideoWatchedDate();
            }
            if (responseModel.getWatchTime() != null) {
                watchTime = responseModel.getWatchTime();
            }
            if (CommonMethodsUtils.isShowAppLovinNativeAds() && (!CommonMethodsUtils.isStringNullOrEmpty(responseModel.getIsTodayTaskCompleted()) && responseModel.getIsTodayTaskCompleted().equals("1"))) {
                if (listVideos.size() <= 4) {
                    listVideos.add(listVideos.size(), new SeeVideoList());
                } else {
                    for (int i2 = 0; i2 < this.listVideos.size(); i2++) {
                        if ((i2 + 1) % 5 == 0) {
                            //AppLogger.getInstance().e("POSITION AD", "==================" + i2);
                            listVideos.add(i2, new SeeVideoList());
                        }
                    }
                }
            }
            for (int i = 0; i < listVideos.size(); i++) {
                if (listVideos.get(i).getVideoId() != null) {
                    if (Integer.parseInt(listVideos.get(i).getVideoId()) == (Integer.parseInt(responseModel.getLastWatchedVideoId()) + 1)) {
                        activeVideoPos = i;
                    }
                    if (responseModel.getWatchedVideoList() != null && responseModel.getWatchedVideoList().size() > 0) {
                        for (int j = 0; j < responseModel.getWatchedVideoList().size(); j++) {
                            if (listVideos.get(i).getVideoId().equals(responseModel.getWatchedVideoList().get(j).getVideoId())) {
                                listVideos.get(i).setWatchedVideoPoints(responseModel.getWatchedVideoList().get(j).getWatchedVideoPoints());
                            }
                        }
                    }
                }
            }
            mAdapter = new SeeVideoListAdapter(listVideos, this, Integer.parseInt(responseModel.getLastWatchedVideoId()), new SeeVideoListAdapter.ClickListener() {
                @Override
                public void onItemClick(int position, View v) {
                    if (SharePreference.getInstance().getBoolean(SharePreference.IS_LOGIN)) {
                        try {
                            if (Integer.parseInt(listVideos.get(position).getVideoId()) == (Integer.parseInt(listVideos.get(activeVideoPos).getVideoId())) && !isTimerSet) {
                                if (CommonMethodsUtils.isNetworkAvailable(SeeVideoListActivity.this)) {
                                    if (listVideos.get(position).getAdsType().equals(Constants.APPLOVIN_INTERSTITIAL)) {
                                        AdsUtil.showAppLovinInterstitialAd(SeeVideoListActivity.this, new AdsUtil.VideoAdShownListener() {
                                            @Override
                                            public void onAdDismiss(boolean isAdShown) {
                                                if (isAdShown) {
                                                    // call api
                                                    new SaveSeeVideoListAsync(SeeVideoListActivity.this, listVideos.get(position).getVideoId(), listVideos.get(position).getVideoPoints());
                                                } else {
                                                    CommonMethodsUtils.setToast(SeeVideoListActivity.this, "Problem while displaying video");
                                                }
                                            }
                                        }, true);
                                    } else if (listVideos.get(position).getAdsType().equals(Constants.APPLOVIN_REWARD)) {
                                        AdsUtil.showAppLovinRewardedAd(SeeVideoListActivity.this, new AdsUtil.VideoAdShownListener() {
                                            @Override
                                            public void onAdDismiss(boolean isAdShown) {
                                                if (isAdShown) {
                                                    // call api
                                                    new SaveSeeVideoListAsync(SeeVideoListActivity.this, listVideos.get(position).getVideoId(), listVideos.get(position).getVideoPoints());
                                                } else {
                                                    CommonMethodsUtils.setToast(SeeVideoListActivity.this, "Problem while displaying video");
                                                }
                                            }
                                        }, true);
                                    }
                                } else {
                                    CommonMethodsUtils.setToast(SeeVideoListActivity.this, "No internet connection");
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        CommonMethodsUtils.NotifyLogin(SeeVideoListActivity.this);
                    }
                }
            });
            GridLayoutManager mGridLayoutManager = new GridLayoutManager(SeeVideoListActivity.this, 2);
            mGridLayoutManager.setOrientation(RecyclerView.VERTICAL);
            mGridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    if (mAdapter.getItemViewType(position) == ScratchCardsListAdapter.ITEM_AD) {
                        return 2;
                    }
                    return 1;
                }
            });
            rvVideoList.setLayoutManager(mGridLayoutManager);
            rvVideoList.setAdapter(mAdapter);
            if (!responseModel.getLastWatchedVideoId().equals(responseModel.getWatchVideoList().get(responseModel.getWatchVideoList().size() - 1).getVideoId())) {
                setTimer(true);
                try {
                    LinearLayout layoutCompleteTask = findViewById(R.id.layoutCompleteTask);
                    if (!isTimerSet && !CommonMethodsUtils.isStringNullOrEmpty(responseModel.getIsTodayTaskCompleted()) && responseModel.getIsTodayTaskCompleted().equals("0")) {
                        layoutCompleteTask.setVisibility(VISIBLE);
                        LinearLayout layoutAdsTask = findViewById(R.id.layoutAdsTask);
                        TextView lblLoadingAdsTask = findViewById(R.id.lblLoadingAdsTask);
                        FrameLayout nativeAdTask = findViewById(R.id.fl_adplaceholder_task);

                        if (CommonMethodsUtils.isShowAppLovinNativeAds()) {
                            loadAppLovinNativeAdsTask(layoutAdsTask, nativeAdTask, lblLoadingAdsTask);
                        } else {
                            layoutAdsTask.setVisibility(GONE);
                        }
                        TextView tvTaskNote = findViewById(R.id.tvTaskNote);
                        tvTaskNote.setText(responseModel.getTaskNote());
                        Button btnCompleteTask = findViewById(R.id.btnCompleteTask);
                        if (!CommonMethodsUtils.isStringNullOrEmpty(responseModel.getTaskButton())) {
                            btnCompleteTask.setText(responseModel.getTaskButton());
                        }
                        btnCompleteTask.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (!CommonMethodsUtils.isStringNullOrEmpty(responseModel.getScreenNo())) {
//                                    if (!CommonMethodsUtils.hasUsageAccessPermission(SeeVideoListActivity.this)) {
//                                        CommonMethodsUtils.showUsageAccessPermissionDialog(SeeVideoListActivity.this);
//                                        return;
//                                    } else {
                                        CommonMethodsUtils.Redirect(SeeVideoListActivity.this, responseModel.getScreenNo(), "", "", "", "", "");
//                                    }
                                } else if (!CommonMethodsUtils.isStringNullOrEmpty(responseModel.getTaskId())) {
                                    Intent intent = new Intent(SeeVideoListActivity.this, TaskInfoActivity.class);
                                    intent.putExtra("taskId", responseModel.getTaskId());
                                    startActivity(intent);
                                } else {
                                    Intent intent = new Intent(SeeVideoListActivity.this, TaskListActivity.class);
                                    startActivity(intent);
                                }
                                finish();
                            }
                        });
                    } else {
                        layoutCompleteTask.setVisibility(GONE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                AdsUtil.showAppLovinInterstitialAd(SeeVideoListActivity.this, null);
            }
            // Show Default AppLuck
            if (!CommonMethodsUtils.isStringNullOrEmpty(responseMain.getIsShowAppluck()) && responseMain.getIsShowAppluck().equals("1") && !CommonMethodsUtils.isStringNullOrEmpty(responseModel.getIsDefaultAppluck()) && responseModel.getIsDefaultAppluck().equals("1")) {
                RelativeLayout relMainFlotAppLuck = findViewById(R.id.relMainFlotAppLuck);
                LinearLayout layoutDefaultAd = findViewById(R.id.layoutDefaultAd);
                CommonMethodsUtils.showDefaultAppLuck(SeeVideoListActivity.this, layoutDefaultAd, relMainFlotAppLuck, responseMain.getDefaultAppluckID());
            }
            // Load home note webview top
            try {
                if (!CommonMethodsUtils.isStringNullOrEmpty(responseModel.getHomeNote())) {
                    WebView webNote = findViewById(R.id.webNote);
                    webNote.getSettings().setJavaScriptEnabled(true);
                    webNote.setVisibility(View.VISIBLE);
                    webNote.loadDataWithBaseURL(null, responseModel.getHomeNote(), "text/html", "UTF-8", null);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Load top ad
            try {
                if (responseModel.getTopAds() != null && !CommonMethodsUtils.isStringNullOrEmpty(responseModel.getTopAds().getImage())) {
                    LinearLayout layoutTopAds = findViewById(R.id.layoutTopAds);
                    CommonMethodsUtils.loadTopBannerAd(SeeVideoListActivity.this, layoutTopAds, responseModel.getTopAds());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            lblLoadingAds = findViewById(R.id.lblLoadingAds);
            layoutAds = findViewById(R.id.layoutAds);
            layoutAds.setVisibility(View.VISIBLE);
            frameLayoutNativeAd = findViewById(R.id.fl_adplaceholder);
            if (CommonMethodsUtils.isShowAppLovinNativeAds()) {
                loadAppLovinNativeAds();
            } else {
                layoutAds.setVisibility(View.GONE);
            }
        }
        rvVideoList.setVisibility(responseModel.getWatchVideoList() != null && responseModel.getWatchVideoList().size() > 0 ? View.VISIBLE : View.GONE);
        ivLottieNoData.setVisibility(responseModel.getWatchVideoList() != null && responseModel.getWatchVideoList().size() > 0 ? View.GONE : View.VISIBLE);
        if (responseModel.getWatchVideoList() == null && responseModel.getWatchVideoList().size() == 0)
            ivLottieNoData.playAnimation();
    }

    public void setTimer(boolean isFromOnCreate) {
        try {
            isSetTimerValue = false;
            if (CommonMethodsUtils.timeDiff(todayDate, lastDate) > Integer.parseInt(watchTime)) {
                isTimerSet = false;
                listVideos.get(activeVideoPos).setButtonText("Watch Now");
                mAdapter.notifyItemChanged(activeVideoPos);
            } else {
                isTimerSet = true;
                if (timer != null) {
                    timer.cancel();
                }
                time = CommonMethodsUtils.timeDiff(todayDate, lastDate);
                timer = new CountDownTimer((Integer.parseInt(watchTime) - time) * 60000L, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        try {
                            listVideos.get(activeVideoPos).setButtonText(CommonMethodsUtils.updateTimeRemainingWatchVideo(millisUntilFinished));
                            if (!isSetTimerValue) {
                                mAdapter.notifyItemChanged(activeVideoPos);
                            } else {
                                RecyclerView.ViewHolder v = rvVideoList.findViewHolderForAdapterPosition(activeVideoPos);
                                TextView tv = v.itemView.findViewById(R.id.tvButton);
                                tv.setText(CommonMethodsUtils.updateTimeRemainingWatchVideo(millisUntilFinished));
                            }
                            isSetTimerValue = true;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFinish() {
                        try {
                            isTimerSet = false;
                            listVideos.get(activeVideoPos).setButtonText("Watch Now");
                            mAdapter.notifyItemChanged(activeVideoPos);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
                if (isFromOnCreate) {
                    AdsUtil.showAppLovinInterstitialAd(SeeVideoListActivity.this, null);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadAppLovinNativeAds() {
        try {
            nativeAdLoader = new MaxNativeAdLoader(CommonMethodsUtils.getRandomAdUnitId(responseMain.getLovinNativeID()), SeeVideoListActivity.this);
            nativeAdLoader.setNativeAdListener(new MaxNativeAdListener() {
                @Override
                public void onNativeAdLoaded(final MaxNativeAdView nativeAdView, final MaxAd ad) {
                    frameLayoutNativeAd = findViewById(R.id.fl_adplaceholder);
                    if (nativeAd != null) {
                        nativeAdLoader.destroy(nativeAd);
                    }
                    nativeAd = ad;
                    frameLayoutNativeAd.removeAllViews();
                    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) frameLayoutNativeAd.getLayoutParams();
                    params.height = getResources().getDimensionPixelSize(R.dimen.dim_300);
                    params.width = LinearLayout.LayoutParams.MATCH_PARENT;
                    frameLayoutNativeAd.setLayoutParams(params);
                    frameLayoutNativeAd.setPadding((int) getResources().getDimension(R.dimen.dim_10), (int) getResources().getDimension(R.dimen.dim_10), (int) getResources().getDimension(R.dimen.dim_10), (int) getResources().getDimension(R.dimen.dim_10));
                    frameLayoutNativeAd.addView(nativeAdView);
                    lblLoadingAds.setVisibility(View.GONE);
                    layoutAds.setVisibility(View.VISIBLE);

                    //AppLogger.getInstance().e("AppLovin Loaded: ", "===");
                }

                @Override
                public void onNativeAdLoadFailed(final String adUnitId, final MaxError error) {
                    //AppLogger.getInstance().e("AppLovin Failed: ", error.getMessage());
                    layoutAds.setVisibility(View.GONE);
                }

                @Override
                public void onNativeAdClicked(final MaxAd ad) {

                }
            });
            nativeAdLoader.loadAd();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (isFinishing()) {
            try {
                if (timer != null) {
                    timer.cancel();
                }
                if (nativeAd != null && nativeAdLoader != null) {
                    nativeAdLoader.destroy(nativeAd);
                    nativeAd = null;
                    frameLayoutNativeAd = null;
                }
                if (nativeAdWin != null && nativeAdLoaderWin != null) {
                    nativeAdLoaderWin.destroy(nativeAdWin);
                    nativeAdWin = null;
                }
                if (nativeAdTask != null && nativeAdLoaderTask != null) {
                    nativeAdLoaderTask.destroy(nativeAdTask);
                    nativeAdTask = null;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}