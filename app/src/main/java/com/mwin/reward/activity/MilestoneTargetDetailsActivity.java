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
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieDrawable;
import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.nativeAds.MaxNativeAdListener;
import com.applovin.mediation.nativeAds.MaxNativeAdLoader;
import com.applovin.mediation.nativeAds.MaxNativeAdView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.gson.Gson;
import com.mwin.reward.R;
import com.mwin.reward.async.GetMilestoneTargetDetailsAsync;
import com.mwin.reward.async.SaveMilestoneTargetAsync;
import com.mwin.reward.async.models.MainResponseModel;
import com.mwin.reward.async.models.MilestoneTargetDataItem;
import com.mwin.reward.async.models.MilestonesTargetResponseModel;
import com.mwin.reward.utils.AdsUtil;
import com.mwin.reward.utils.CommonMethodsUtils;
import com.mwin.reward.utils.SharePreference;
import com.mwin.reward.value.Constants;

public class MilestoneTargetDetailsActivity extends AppCompatActivity {
    private TextView tvPoints, lblLoadingAds, lblCompleted;
    private ImageView ivBack, ivSmallIcon, ivBanner;
    private TextView txtPoints, txtTitle, txtSubtitle;
    private LottieAnimationView ltSmallIcon, ivLottieView;
    private RelativeLayout layoutButton;
    private Button lInstallBtn;
    private LinearLayout lTaskMain;
    private WebView webDisclaimer;
    private RelativeLayout layoutTaskBanner, layoutMain;
    private LinearLayout layoutPoints, layoutAds, layoutDescription;
    private MilestoneTargetDataItem objMilestone;
    private String milestoneId;
    private View viewShine;
    private MaxAd nativeAd, nativeAdWin;
    private MaxNativeAdLoader nativeAdLoader, nativeAdLoaderWin;
    private FrameLayout frameLayoutNativeAd;
    private CardView cardDisclaimer, cardImage;
    private TextView tvTimer, tvPercentage, tvRequire, tvCompleted, tvTime;
    private ProgressBar progressBarCompletion;
    private View view1, view2;
    private CountDownTimer timer;
    private MainResponseModel responseMain;
    private boolean isClaim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        CommonMethodsUtils.setDayNightTheme(MilestoneTargetDetailsActivity.this);
        setContentView(R.layout.activity_milestone_target_details);
        milestoneId = getIntent().getStringExtra("milestoneId");
        responseMain = new Gson().fromJson(SharePreference.getInstance().getString(SharePreference.HomeData), MainResponseModel.class);
        initView();
        new GetMilestoneTargetDetailsAsync(MilestoneTargetDetailsActivity.this, milestoneId);
    }

    private void initView() {
        lblCompleted = findViewById(R.id.lblCompleted);
        layoutMain = findViewById(R.id.layoutMain);
        tvTimer = findViewById(R.id.tvTimer);
        tvPercentage = findViewById(R.id.tvPercentage);
        tvCompleted = findViewById(R.id.tvCompleted);
        tvRequire = findViewById(R.id.tvRequire);
        tvTime = findViewById(R.id.tvTime);
        progressBarCompletion = findViewById(R.id.progressBarCompletion);
        cardImage = findViewById(R.id.cardImage);
        view1 = findViewById(R.id.view1);
        view2 = findViewById(R.id.view2);
        cardDisclaimer = findViewById(R.id.cardDisclaimer);
        layoutTaskBanner = findViewById(R.id.layoutTaskBanner);
        layoutDescription = findViewById(R.id.layoutDescription);
        layoutPoints = findViewById(R.id.layoutPoints);
        layoutPoints.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SharePreference.getInstance().getBoolean(SharePreference.IS_LOGIN)) {
                    startActivity(new Intent(MilestoneTargetDetailsActivity.this, EarnedWalletBalanceActivity.class));
                } else {
                    CommonMethodsUtils.NotifyLogin(MilestoneTargetDetailsActivity.this);
                }
            }
        });

        tvPoints = findViewById(R.id.tvPoints);
        tvPoints.setText(SharePreference.getInstance().getEarningPointString());

        ivBack = findViewById(R.id.ivBack);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        lInstallBtn = findViewById(R.id.lInstallBtn);
        viewShine = findViewById(R.id.viewShine);
        layoutButton = findViewById(R.id.layoutButton);
        lTaskMain = findViewById(R.id.lTaskMain);
        ivSmallIcon = findViewById(R.id.ivSmallIcon);
        txtPoints = findViewById(R.id.txtPoints);
        txtTitle = findViewById(R.id.txtTitle);
        txtSubtitle = findViewById(R.id.txtSubtitle);
        ivBanner = findViewById(R.id.ivBanner);
        webDisclaimer = findViewById(R.id.webDisclamier);
        ltSmallIcon = findViewById(R.id.ltSmallIcon);
        ivLottieView = findViewById(R.id.ivLottieView);
        lTaskMain.setVisibility(View.INVISIBLE);
        layoutButton.setVisibility(View.GONE);

    }
    
    @Override
    public void onBackPressed() {
        try {
            if (responseModel != null && responseModel.getAppLuck() != null ) {
                
                CommonMethodsUtils.dialogShowAppLuck(MilestoneTargetDetailsActivity.this, responseModel.getAppLuck());
            } else {
                super.onBackPressed();
            }
        } catch (Exception e) {
            e.printStackTrace();
            super.onBackPressed();
        }
    }

    private MilestonesTargetResponseModel responseModel;

    public void setData(MilestonesTargetResponseModel responseModel1) {
        responseModel = responseModel1;
        if (responseModel != null) {
            objMilestone = responseModel.getSingleMilestoneData();
            if (objMilestone.getIsShowInterstitial() != null && objMilestone.getIsShowInterstitial().equals(Constants.APPLOVIN_INTERSTITIAL)) {
                AdsUtil.showAppLovinInterstitialAd(MilestoneTargetDetailsActivity.this, null);
            } else if (objMilestone.getIsShowInterstitial() != null && objMilestone.getIsShowInterstitial().equals(Constants.APPLOVIN_REWARD)) {
                AdsUtil.showAppLovinRewardedAd(MilestoneTargetDetailsActivity.this, null);
            }

            if (CommonMethodsUtils.isShowAppLovinNativeAds()) {
                lblLoadingAds = findViewById(R.id.lblLoadingAds);
                layoutAds = findViewById(R.id.layoutAds);
                frameLayoutNativeAd = findViewById(R.id.fl_adplaceholder);
                loadAppLovinNativeAds();
            }

            // Load task banner
            try {
                if (!CommonMethodsUtils.isStringNullOrEmpty(objMilestone.getBanner())) {
                    layoutTaskBanner.setVisibility(View.VISIBLE);
                    if (objMilestone.getBanner().contains(".json")) {
                        ivBanner.setVisibility(View.GONE);
                        ivLottieView.setVisibility(View.VISIBLE);
                        CommonMethodsUtils.setLottieAnimation(ivLottieView, objMilestone.getBanner());
                        ivLottieView.setRepeatCount(LottieDrawable.INFINITE);
                    } else {
                        ivBanner.setVisibility(View.VISIBLE);
                        ivLottieView.setVisibility(View.GONE);
                        Glide.with(getApplicationContext()).load(objMilestone.getBanner()).listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, com.bumptech.glide.load.DataSource dataSource, boolean isFirstResource) {
                                ivBanner.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.rectangle_white));
                                return false;
                            }
                        }).into(ivBanner);
                    }
                } else {
                    layoutTaskBanner.setVisibility(View.GONE);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Show Default AppLuck
            if (!CommonMethodsUtils.isStringNullOrEmpty(responseMain.getIsShowAppluck()) && responseMain.getIsShowAppluck().equals("1") && !CommonMethodsUtils.isStringNullOrEmpty(responseModel.getIsDefaultAppluck()) && responseModel.getIsDefaultAppluck().equals("1")) {
                RelativeLayout relMainFlotAppLuck = findViewById(R.id.relMainFlotAppLuck);
                LinearLayout layoutDefaultAd = findViewById(R.id.layoutDefaultAd);
                CommonMethodsUtils.showDefaultAppLuck(MilestoneTargetDetailsActivity.this, layoutDefaultAd, relMainFlotAppLuck, responseMain.getDefaultAppluckID());
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
                    CommonMethodsUtils.loadTopBannerAd(MilestoneTargetDetailsActivity.this, layoutTopAds, responseModel.getTopAds());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (!CommonMethodsUtils.isStringNullOrEmpty(objMilestone.getIcon())) {
                if (objMilestone.getIcon().contains(".json")) {
                    ivSmallIcon.setVisibility(View.GONE);
                    ltSmallIcon.setVisibility(View.VISIBLE);
                    CommonMethodsUtils.setLottieAnimation(ltSmallIcon, objMilestone.getIcon());
                    ltSmallIcon.setRepeatCount(LottieDrawable.INFINITE);
                } else {
                    ivSmallIcon.setVisibility(View.VISIBLE);
                    ltSmallIcon.setVisibility(View.GONE);
                    Glide.with(getApplicationContext()).load(objMilestone.getIcon()).listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, com.bumptech.glide.load.DataSource dataSource, boolean isFirstResource) {
                            ivSmallIcon.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.rectangle_white));
                            return false;
                        }
                    }).into(ivSmallIcon);
                }
            } else {
                cardImage.setVisibility(View.GONE);
            }

            lInstallBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isClaim) {
                        if (SharePreference.getInstance().getBoolean(SharePreference.IS_LOGIN)) {
                            new SaveMilestoneTargetAsync(MilestoneTargetDetailsActivity.this, objMilestone.getPoints(), objMilestone.getId());
                        } else {
                            CommonMethodsUtils.NotifyLogin(MilestoneTargetDetailsActivity.this);
                        }
                    } else {
                        CommonMethodsUtils.Redirect(MilestoneTargetDetailsActivity.this, objMilestone.getScreenNo(), objMilestone.getTitle(), "", objMilestone.getId(), objMilestone.getId(), objMilestone.getBanner());
                    }
                }
            });

            if (objMilestone.getTitle() != null) {
                txtTitle.setText(objMilestone.getTitle());
            }

            tvPercentage.setText(objMilestone.getCompletionPercent() + "%");
            if (objMilestone.getType().equals("0"))// number target
            {
                lblCompleted.setText("Completed:");
                tvRequire.setText(objMilestone.getTargetNumber());
                tvCompleted.setText(objMilestone.getNoOfCompleted());
            } else {// points target
                lblCompleted.setText("Earned:");
                tvRequire.setText(objMilestone.getTargetPoints());
                tvCompleted.setText(objMilestone.getEarnedPoints());
            }
            progressBarCompletion.setProgress((int) Double.parseDouble(String.valueOf(objMilestone.getCompletionPercent())));
            view1.setLayoutParams(new LinearLayout.LayoutParams(0, getResources().getDimensionPixelSize(R.dimen.dim_1), Float.parseFloat(String.valueOf(objMilestone.getCompletionPercent()))));
            view2.setLayoutParams(new LinearLayout.LayoutParams(0, getResources().getDimensionPixelSize(R.dimen.dim_1), Float.parseFloat(String.valueOf(100 - Double.parseDouble(objMilestone.getCompletionPercent())))));
            int day = CommonMethodsUtils.getDaysDiff(objMilestone.getEndDate(), objMilestone.getTodayDate());
            tvTime.setVisibility(day > 1 ? View.VISIBLE : View.GONE);
            tvTimer.setVisibility(day > 1 ? View.GONE : View.VISIBLE);
            if (day > 1) {
                tvTime.setText(day + " days");
            } else {
                if (timer != null) {
                    timer.cancel();
                }
                timer = new CountDownTimer(CommonMethodsUtils.timeDiff(objMilestone.getTodayDate(), objMilestone.getEndDate()) * 60000L, 1000) {

                    @Override
                    public void onTick(long millisUntilFinished) {
                        tvTimer.setText(CommonMethodsUtils.updateTimeRemainingLuckyNumber(millisUntilFinished));
                    }

                    @Override
                    public void onFinish() {
                        tvTimer.setText("Time's Up!");
                    }
                }.start();
            }

            if (!CommonMethodsUtils.isStringNullOrEmpty(objMilestone.getDescription())) {
                txtSubtitle.setText(objMilestone.getDescription());
            } else {
                layoutDescription.setVisibility(View.GONE);
            }
            if (!CommonMethodsUtils.isStringNullOrEmpty(objMilestone.getPoints())) {
                try {
                    txtPoints.setText(objMilestone.getPoints());
                    TextView tvTaskRupees = findViewById(R.id.tvTaskRupees);
                    tvTaskRupees.setText(CommonMethodsUtils.convertPointsInINR(objMilestone.getPoints(), new Gson().fromJson(SharePreference.getInstance().getString(SharePreference.HomeData), MainResponseModel.class).getPointValue()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            lTaskMain.setVisibility(View.VISIBLE);
            layoutButton.setVisibility(View.VISIBLE);

            Animation animUpDown = AnimationUtils.loadAnimation(MilestoneTargetDetailsActivity.this, R.anim.left_to_right);
            animUpDown.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    viewShine.startAnimation(animUpDown);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            // start the animation
            viewShine.startAnimation(animUpDown);

            if (!CommonMethodsUtils.isStringNullOrEmpty(objMilestone.getNotes())) {
                webDisclaimer.loadData(objMilestone.getNotes(), "text/html", "UTF-8");
            } else {
                cardDisclaimer.setVisibility(View.GONE);
            }

            if (objMilestone.getButtonColor() != null && objMilestone.getButtonColor().length() > 0) {
                Drawable mDrawable = ContextCompat.getDrawable(MilestoneTargetDetailsActivity.this, R.drawable.ic_btn_rounded_corner);
                mDrawable.setColorFilter(new PorterDuffColorFilter(Color.parseColor(objMilestone.getButtonColor()), PorterDuff.Mode.SRC_IN));
                lInstallBtn.setBackground(mDrawable);
                lInstallBtn.setTextColor(Color.parseColor(objMilestone.getButtonTextColor()));
            }
            if (objMilestone.getButtonName() != null) {
                lInstallBtn.setText(objMilestone.getButtonName());
            }
            isClaim = false;
            try {
                if (objMilestone.getType().equals("0"))// number target
                {
                    if (Integer.parseInt(objMilestone.getNoOfCompleted()) >= Integer.parseInt(objMilestone.getTargetNumber())) {
                        isClaim = true;
                    }
                } else {// points target
                    if (Integer.parseInt(objMilestone.getEarnedPoints()) >= Integer.parseInt(objMilestone.getTargetPoints())) {
                        isClaim = true;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (isClaim) {
                lInstallBtn.setText("CLAIM NOW");
                Drawable mDrawable = ContextCompat.getDrawable(MilestoneTargetDetailsActivity.this, R.drawable.ic_btn_rounded_corner);
                mDrawable.setColorFilter(new PorterDuffColorFilter(getColor(R.color.green), PorterDuff.Mode.SRC_IN));
                lInstallBtn.setBackground(mDrawable);
            }
        }
    }

    private void loadAppLovinNativeAds() {
        try {
            nativeAdLoader = new MaxNativeAdLoader(CommonMethodsUtils.getRandomAdUnitId(new Gson().fromJson(SharePreference.getInstance().getString(SharePreference.HomeData), MainResponseModel.class).getLovinNativeID()), MilestoneTargetDetailsActivity.this);
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
            if (timer != null) {
                timer.cancel();
            }
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
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void changeMilestoneValues(MilestonesTargetResponseModel responseModel) {
        SharePreference.getInstance().putString(SharePreference.EarnedPoints, responseModel.getEarningPoint());

        CommonMethodsUtils.logFirebaseEvent(MilestoneTargetDetailsActivity.this, "FeatureUsabilityItemId", "FeatureUsabilityEvent", "Milestones", "Milestones Got Reward");
        showWinPopup(responseModel.getWinningPoints());
    }

    public void showWinPopup(String point) {
        try {
            Dialog dialogWin = new Dialog(MilestoneTargetDetailsActivity.this, android.R.style.Theme_Light);
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
                    AdsUtil.showAppLovinRewardedAd(MilestoneTargetDetailsActivity.this, new AdsUtil.AdShownListener() {
                        @Override
                        public void onAdDismiss() {
                            if (dialogWin != null) {
                                dialogWin.dismiss();
                            }
                        }
                    });
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
                AdsUtil.showAppLovinRewardedAd(MilestoneTargetDetailsActivity.this, new AdsUtil.AdShownListener() {
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
                    try {
                        CommonMethodsUtils.GetCoinAnimation(MilestoneTargetDetailsActivity.this, layoutMain, layoutPoints);
                        tvPoints.setText(SharePreference.getInstance().getEarningPointString());
                        layoutButton.setVisibility(View.GONE);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            if (!MilestoneTargetDetailsActivity.this.isFinishing() && !dialogWin.isShowing()) {
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
            nativeAdLoaderWin = new MaxNativeAdLoader(CommonMethodsUtils.getRandomAdUnitId(responseMain.getLovinNativeID()), MilestoneTargetDetailsActivity.this);
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
}