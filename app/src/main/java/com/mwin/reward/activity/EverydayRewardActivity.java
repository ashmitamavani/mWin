/*
 * Copyright (c) 2021.  Hurricane Development Studios
 */

package com.mwin.reward.activity;

import static android.view.View.VISIBLE;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.Html;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.nativeAds.MaxNativeAdListener;
import com.applovin.mediation.nativeAds.MaxNativeAdLoader;
import com.applovin.mediation.nativeAds.MaxNativeAdView;
import com.google.gson.Gson;
import com.mwin.reward.R;
import com.mwin.reward.adapter.DailyRewardAdapter;
import com.mwin.reward.async.GetEverydayRewardAsync;
import com.mwin.reward.async.SaveEverydayRewardAsync;
import com.mwin.reward.async.models.EarnedPointHistoryModel;
import com.mwin.reward.async.models.EverydayRewardDataItem;
import com.mwin.reward.async.models.EverydayRewardResponseModel;
import com.mwin.reward.async.models.MainResponseModel;
import com.mwin.reward.utils.AdsUtil;
import com.mwin.reward.utils.CommonMethodsUtils;
import com.mwin.reward.utils.SharePreference;
import com.mwin.reward.value.Constants;

import java.util.ArrayList;
import java.util.List;

public class EverydayRewardActivity extends AppCompatActivity {
    private RecyclerView rvList;
    private List<EverydayRewardDataItem> list = new ArrayList<>();
    private TextView lblLoadingAds, tvPoints, tvNote, tvTimer;
    private LottieAnimationView ivLottieNoData;
    private MainResponseModel responseMain;
    private MaxAd nativeAd, nativeAdWin;
    private MaxNativeAdLoader nativeAdLoader, nativeAdLoaderWin;
    private FrameLayout frameLayoutNativeAd;
    private LinearLayout layoutAds, layoutData, layoutPoints, layoutTimer;
    private ImageView ivHistory;
    private AppCompatButton btnClaim;
    private RelativeLayout layoutMain;
    private EverydayRewardResponseModel objDailyReward;
    private String todayDate, endDate;
    private CountDownTimer timer;
    private int time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        CommonMethodsUtils.setDayNightTheme(EverydayRewardActivity.this);
        setContentView(R.layout.activity_everyday_reward);
        responseMain = new Gson().fromJson(SharePreference.getInstance().getString(SharePreference.HomeData), MainResponseModel.class);

        rvList = findViewById(R.id.rvList);
        rvList.setAdapter(new DailyRewardAdapter(list, EverydayRewardActivity.this, new DailyRewardAdapter.ClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                try {
                    if (list.get(position).getIsCompleted().equals("0")) {
                        finish();
                        CommonMethodsUtils.Redirect(EverydayRewardActivity.this, list.get(position).getScreenNo(), list.get(position).getTitle(), list.get(position).getUrl(), list.get(position).getId(), list.get(position).getTaskId(), list.get(position).getImage());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }));
        rvList.setLayoutManager(new LinearLayoutManager(this));
        layoutMain = findViewById(R.id.layoutMain);

        layoutTimer = findViewById(R.id.layoutTimer);
        tvTimer = findViewById(R.id.tvTimer);

        layoutData = findViewById(R.id.layoutData);
        layoutData.setVisibility(View.INVISIBLE);
        ivLottieNoData = findViewById(R.id.ivLottieNoData);

        tvNote = findViewById(R.id.tvNote);

        tvPoints = findViewById(R.id.tvPoints);
        tvPoints.setText(SharePreference.getInstance().getEarningPointString());
        ImageView imBack = findViewById(R.id.ivBack);
        imBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        layoutPoints = findViewById(R.id.layoutPoints);
        layoutPoints.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SharePreference.getInstance().getBoolean(SharePreference.IS_LOGIN)) {
                    startActivity(new Intent(EverydayRewardActivity.this, EarnedWalletBalanceActivity.class));
                } else {
                    CommonMethodsUtils.NotifyLogin(EverydayRewardActivity.this);
                }
            }
        });

        btnClaim = findViewById(R.id.btnClaim);
        btnClaim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnClaim.setEnabled(false);
                new SaveEverydayRewardAsync(EverydayRewardActivity.this);
            }
        });

        ivHistory = findViewById(R.id.ivHistory);
        CommonMethodsUtils.startRoundAnimation(EverydayRewardActivity.this, ivHistory);
        ivHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SharePreference.getInstance().getBoolean(SharePreference.IS_LOGIN)) {
                    startActivity(new Intent(EverydayRewardActivity.this, EarnedPointHistoryActivity.class)
                            .putExtra("type", Constants.HistoryType.DAILY_REWARD)
                            .putExtra("title", "Daily Reward History"));
                } else {
                    CommonMethodsUtils.NotifyLogin(EverydayRewardActivity.this);
                }
            }
        });

        new GetEverydayRewardAsync(EverydayRewardActivity.this);
    }
    
    @Override
    public void onBackPressed() {
        try {
            if (objDailyReward != null && objDailyReward.getAppLuck() != null ) {
                
                CommonMethodsUtils.dialogShowAppLuck(EverydayRewardActivity.this, objDailyReward.getAppLuck());
            } else {
                super.onBackPressed();
            }
        } catch (Exception e) {
            e.printStackTrace();
            super.onBackPressed();
        }
    }
    public void setData(EverydayRewardResponseModel responseModel) {
        if (responseModel != null) {
            objDailyReward = responseModel;
            layoutData.setVisibility(View.VISIBLE);
            tvNote.setText(Html.fromHtml(objDailyReward.getNote()));
            if ((responseModel.getData() != null && responseModel.getData().size() > 0) || (responseModel.getData() != null && responseModel.getData().size() > 0)) {
                if (responseModel.getIsShowInterstitial() != null && responseModel.getIsShowInterstitial().equals(Constants.APPLOVIN_INTERSTITIAL)) {
                    AdsUtil.showAppLovinInterstitialAd(EverydayRewardActivity.this, null);
                } else if (responseModel.getIsShowInterstitial() != null && responseModel.getIsShowInterstitial().equals(Constants.APPLOVIN_REWARD)) {
                    AdsUtil.showAppLovinRewardedAd(EverydayRewardActivity.this, null);
                }
                list.addAll(responseModel.getData());
                rvList.getAdapter().notifyDataSetChanged();
                boolean isClaimedAll = true;
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).getIsCompleted().equals("0")) {
                        isClaimedAll = false;
                        break;
                    }
                }
                btnClaim.setEnabled(isClaimedAll);
                if (!isClaimedAll) {
                    if (objDailyReward.getTodayDate() != null) {
                        todayDate = objDailyReward.getTodayDate();
                    }
                    if (objDailyReward.getEndDate() != null) {
                        endDate = objDailyReward.getEndDate();
                    }
                    layoutTimer.setVisibility(VISIBLE);
                    setTimer();
                } else {
                    tvNote.setText("⭐ Wow! You have completed today's reward list ⭐");
                    layoutTimer.setVisibility(View.GONE);
                }
                if (!CommonMethodsUtils.isStringNullOrEmpty(responseModel.getIsClaimedDailyReward()) && responseModel.getIsClaimedDailyReward().equals("1")) {
                    btnClaim.setText("Claimed!");
                    btnClaim.setEnabled(false);
                }
                // Show Default AppLuck
                if (!CommonMethodsUtils.isStringNullOrEmpty(responseMain.getIsShowAppluck()) && responseMain.getIsShowAppluck().equals("1") && !CommonMethodsUtils.isStringNullOrEmpty(responseModel.getIsDefaultAppluck()) && responseModel.getIsDefaultAppluck().equals("1")) {
                    RelativeLayout relMainFlotAppLuck = findViewById(R.id.relMainFlotAppLuck);
                    LinearLayout layoutDefaultAd = findViewById(R.id.layoutDefaultAd);
                    CommonMethodsUtils.showDefaultAppLuck(EverydayRewardActivity.this, layoutDefaultAd, relMainFlotAppLuck, responseMain.getDefaultAppluckID());
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
                        CommonMethodsUtils.loadTopBannerAd(EverydayRewardActivity.this, layoutTopAds, responseModel.getTopAds());
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
            rvList.setVisibility(list.isEmpty() ? View.GONE : View.VISIBLE);
            ivLottieNoData.setVisibility(list.isEmpty() ? View.VISIBLE : View.GONE);
            if (list.isEmpty())
                ivLottieNoData.playAnimation();
            // Load Bottom banner ad
            try {
                if (!list.isEmpty()) {
                    LinearLayout layoutBannerAdBottom = findViewById(R.id.layoutBannerAdBottom);
                    layoutBannerAdBottom.setVisibility(View.VISIBLE);
                    TextView lblAdSpaceBottom = findViewById(R.id.lblAdSpaceBottom);
                    CommonMethodsUtils.loadBannerAds(EverydayRewardActivity.this, layoutBannerAdBottom, lblAdSpaceBottom);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void setTimer() {
        try {
            if (timer != null) {
                timer.cancel();
            }
            time = CommonMethodsUtils.timeDiff(endDate, todayDate);
            timer = new CountDownTimer(time * 60000L, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    tvTimer.setText(CommonMethodsUtils.updateTimeRemainingLuckyNumber(millisUntilFinished));
                }

                @Override
                public void onFinish() {
                    tvTimer.setText("");
                    layoutTimer.setVisibility(View.GONE);
                }
            }.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadAppLovinNativeAds() {
        try {
            nativeAdLoader = new MaxNativeAdLoader(CommonMethodsUtils.getRandomAdUnitId(responseMain.getLovinNativeID()), EverydayRewardActivity.this);
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
                if (nativeAd != null && nativeAdLoader != null) {
                    nativeAdLoader.destroy(nativeAd);
                    nativeAd = null;
                    frameLayoutNativeAd = null;
                }
                if (nativeAdWin != null && nativeAdLoaderWin != null) {
                    nativeAdLoaderWin.destroy(nativeAdWin);
                    nativeAdWin = null;
                }
                if (timer != null) {
                    timer.cancel();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void loadAppLovinNativeAds(FrameLayout frameLayoutNativeAd, TextView lblLoadingAds) {
        try {
            nativeAdLoaderWin = new MaxNativeAdLoader(CommonMethodsUtils.getRandomAdUnitId(responseMain.getLovinNativeID()), EverydayRewardActivity.this);
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

    public void showWinPopup(String point, EverydayRewardResponseModel responseModel) {
        try {
            Dialog dialogWin = new Dialog(EverydayRewardActivity.this, android.R.style.Theme_Light);
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

            TextView lblPoints = dialogWin.findViewById(R.id.lblPoints);
            try {
                int pt = Integer.parseInt(point);
                lblPoints.setText((pt <= 1 ? "Point" : "Points"));
            } catch (NumberFormatException e) {
                e.printStackTrace();
                lblPoints.setText("Points");
            }

            AppCompatButton btnOk = dialogWin.findViewById(R.id.btnOk);
            ImageView ivClose = dialogWin.findViewById(R.id.ivClose);
            ivClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AdsUtil.showAppLovinRewardedAd(EverydayRewardActivity.this, new AdsUtil.AdShownListener() {
                        @Override
                        public void onAdDismiss() {
                            if (dialogWin != null) {
                                dialogWin.dismiss();
                            }
                        }
                    });
                }
            });

            if (!CommonMethodsUtils.isStringNullOrEmpty(responseModel.getBtnName())) {
                btnOk.setText(responseModel.getBtnName());
            }

            if (!CommonMethodsUtils.isStringNullOrEmpty(responseModel.getBtnColor())) {
                Drawable mDrawable = ContextCompat.getDrawable(EverydayRewardActivity.this, R.drawable.ic_btn_rounded_corner);
                mDrawable.setColorFilter(new PorterDuffColorFilter(Color.parseColor(responseModel.getBtnColor()), PorterDuff.Mode.SRC_IN));
                btnOk.setBackground(mDrawable);
            }

            btnOk.setOnClickListener(v -> {
                AdsUtil.showAppLovinRewardedAd(EverydayRewardActivity.this, new AdsUtil.AdShownListener() {
                    @Override
                    public void onAdDismiss() {
                        if (dialogWin != null) {
                            dialogWin.dismiss();
                        }
                    }
                });
            });
            dialogWin.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialogInterface) {
                    CommonMethodsUtils.GetCoinAnimation(EverydayRewardActivity.this, layoutMain, layoutPoints);
                    tvPoints.setText(SharePreference.getInstance().getEarningPointString());
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

    public void updateData(EverydayRewardResponseModel responseModel) {
        try {
            btnClaim.setText("Claimed!");
            CommonMethodsUtils.logFirebaseEvent(EverydayRewardActivity.this, "FeatureUsabilityItemId", "FeatureUsabilityEvent", "Daily_Rewards_Challenge", "Got Reward");
            showWinPopup(objDailyReward.getTotalPoints(), responseModel);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}