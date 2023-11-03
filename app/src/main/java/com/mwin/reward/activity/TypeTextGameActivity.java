/*
 * Copyright (c) 2021.  Hurricane Development Studios
 */

package com.mwin.reward.activity;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.airbnb.lottie.LottieAnimationView;
import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.nativeAds.MaxNativeAdListener;
import com.applovin.mediation.nativeAds.MaxNativeAdLoader;
import com.applovin.mediation.nativeAds.MaxNativeAdView;
import com.google.gson.Gson;
import com.mwin.reward.R;
import com.mwin.reward.async.GetTypeTextDataAsync;
import com.mwin.reward.async.SaveTypeTextGameAsync;
import com.mwin.reward.async.models.MainResponseModel;
import com.mwin.reward.async.models.TaskListResponseModel;
import com.mwin.reward.async.models.TypeTextDataModel;
import com.mwin.reward.utils.AdsUtil;
import com.mwin.reward.utils.CommonMethodsUtils;
import com.mwin.reward.utils.SharePreference;
import com.mwin.reward.value.Constants;

public class TypeTextGameActivity extends AppCompatActivity {
    private TextView tvDailyLimit, tvText, tvRemainCount, tvRemainingTime, tvWinningPoints, tvPoints, lblLoadingAds, tvMainTimer, lblTimer, tvAttemptsLeft;
    private EditText etText;
    private Button btnClaimNow;
    private MainResponseModel responseMain;
    private LinearLayout layoutPoints, layoutTimer, layoutContent, layoutCompleteTask;
    private CountDownTimer mainTimer, textTypingTimer;
    private int remainCount, time, lifeline, textTypingTime;
    private String todayDate, lastDate, mainTimerTime;
    private TypeTextDataModel objTextTypingData;
    private ImageView ivHistory, ivHelp;
    private RelativeLayout layoutMain;
    private MaxAd nativeAd, nativeAdWin, nativeAdTask, nativeAdTimer;
    private MaxNativeAdLoader nativeAdLoader, nativeAdLoaderWin, nativeAdLoaderTask, nativeAdLoaderTimer;
    private boolean isTimerSet = false, isTimerOver = false;
    private LinearLayout layoutAds;
    private FrameLayout frameLayoutNativeAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        CommonMethodsUtils.setDayNightTheme(TypeTextGameActivity.this);
        setContentView(R.layout.activity_type_text_game);

        responseMain = new Gson().fromJson(SharePreference.getInstance().getString(SharePreference.HomeData), MainResponseModel.class);
        layoutMain = findViewById(R.id.layoutMain);
        layoutContent = findViewById(R.id.layoutContent);
        layoutContent.setVisibility(View.INVISIBLE);
        ivHistory = findViewById(R.id.ivHistory);
        layoutTimer = findViewById(R.id.layoutTimer);
        layoutPoints = findViewById(R.id.layoutPoints);
        layoutPoints.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SharePreference.getInstance().getBoolean(SharePreference.IS_LOGIN)) {
                    startActivity(new Intent(TypeTextGameActivity.this, EarnedWalletBalanceActivity.class));
                } else {
                    CommonMethodsUtils.NotifyLogin(TypeTextGameActivity.this);
                }
            }
        });
        ivHelp = findViewById(R.id.ivHelp);
        tvPoints = findViewById(R.id.tvPoints);
        tvPoints.setText(SharePreference.getInstance().getEarningPointString());
        tvWinningPoints = findViewById(R.id.tvWinningPoints);
        tvDailyLimit = findViewById(R.id.tvDailyLimit);
        tvRemainCount = findViewById(R.id.tvRemainCount);
        tvRemainingTime = findViewById(R.id.tvRemainingTime);
        tvMainTimer = findViewById(R.id.tvMainTimer);
        lblTimer = findViewById(R.id.lblTimer);
        tvAttemptsLeft = findViewById(R.id.tvAttemptsLeft);
        tvText = findViewById(R.id.tvText);
        etText = findViewById(R.id.etText);
        etText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                try {
                    if (etText.getText().toString().length() > 0) {
                        setTimer();
                    }
                    if (tvText.getText().toString().trim().length() > 0 && etText.getText().toString().length() >= tvText.getText().toString().trim().length()) {
                        btnClaimNow.setEnabled(true);
                    } else {
                        btnClaimNow.setEnabled(false);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        etText.setCustomSelectionActionModeCallback(new ActionMode.Callback() {

            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            public void onDestroyActionMode(ActionMode mode) {
            }

            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                return false;
            }
        });
        etText.setLongClickable(false);
        etText.setTextIsSelectable(false);

        btnClaimNow = findViewById(R.id.btnClaimNow);
        btnClaimNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    CommonMethodsUtils.setEnableDisable(TypeTextGameActivity.this, v);
                    if (SharePreference.getInstance().getBoolean(SharePreference.IS_LOGIN)) {
                        if (remainCount > 0) {
                            if (lifeline > 0 && !tvText.getText().toString().trim().equals(etText.getText().toString())) {
                                lifeline = lifeline - 1;
                                tvAttemptsLeft.setText(String.valueOf(lifeline));
                                CommonMethodsUtils.NotifyMessage(TypeTextGameActivity.this, "Text Didn't Match!", "Sorry, typed text did not match with given text, please try again.\n\n" + lifeline + " attempt is left.", false);
                            } else {
                                if (!isTimerOver) {
                                    if (textTypingTimer != null) {
                                        textTypingTimer.cancel();
                                        textTypingTimer = null;
                                    }
                                    new SaveTypeTextGameAsync(TypeTextGameActivity.this, objTextTypingData.getData().getPoints(), objTextTypingData.getData().getId(), etText.getText().toString());
                                } else {
                                    CommonMethodsUtils.Notify(TypeTextGameActivity.this, getString(R.string.app_name), "Time is over. Better luck, next time!", false);
                                }

                            }
                        } else {
                            CommonMethodsUtils.Notify(TypeTextGameActivity.this, "Text Typing Limit Over", "You have exhausted your text typing daily limit, please try again tomorrow.", false);
                        }
                    } else {
                        CommonMethodsUtils.NotifyLogin(TypeTextGameActivity.this);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        ImageView imBack = findViewById(R.id.ivBack);
        imBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        CommonMethodsUtils.startRoundAnimation(TypeTextGameActivity.this, ivHistory);
        ivHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SharePreference.getInstance().getBoolean(SharePreference.IS_LOGIN)) {
                    startActivity(new Intent(TypeTextGameActivity.this, EarnedPointHistoryActivity.class)
                            .putExtra("type", Constants.HistoryType.TYPE_TEXT_TYPING)
                            .putExtra("title", "Text Typing History"));
                } else {
                    CommonMethodsUtils.NotifyLogin(TypeTextGameActivity.this);
                }
            }
        });
        new GetTypeTextDataAsync(TypeTextGameActivity.this);
    }

    public void changeTextTypingDataValues(TypeTextDataModel responseModel) {
        SharePreference.getInstance().putString(SharePreference.EarnedPoints, responseModel.getEarningPoint());

        tvDailyLimit.setText(responseModel.getTotalCount());

        if (responseModel.getTodayDate() != null) {
            todayDate = responseModel.getTodayDate();
        }
        if (responseModel.getLastDate() != null) {
            lastDate = responseModel.getLastDate();
        }

        if (responseModel.getMainTimer() != null) {
            mainTimerTime = responseModel.getMainTimer();
        }
        if (responseModel.getRemainCount() != null) {
            tvRemainCount.setText(responseModel.getRemainCount());
            remainCount = Integer.parseInt(responseModel.getRemainCount());
        }
        objTextTypingData.setData(responseModel.getData());
        if (responseModel.getWinningPoints().equals("0")) {
            CommonMethodsUtils.logFirebaseEvent(TypeTextGameActivity.this, "FeatureUsabilityItemId", "FeatureUsabilityEvent", "Text_Typing", "Better Luck");
            showBetterluckPopup();
        } else {
            CommonMethodsUtils.logFirebaseEvent(TypeTextGameActivity.this, "FeatureUsabilityItemId", "FeatureUsabilityEvent", "Text_Typing", "Text Typing Got Reward");
            showWinPopup(responseModel.getWinningPoints());
        }
    }

    public void setTimer() {
        try {
            if (textTypingTimer == null) {
                isTimerOver = false;
                textTypingTimer = new CountDownTimer(textTypingTime * 1000L, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        isTimerOver = false;
                        tvRemainingTime.setText(CommonMethodsUtils.updateTimeRemainingLuckyNumber(millisUntilFinished));
                    }

                    @Override
                    public void onFinish() {
                        isTimerOver = true;
                        CommonMethodsUtils.logFirebaseEvent(TypeTextGameActivity.this, "FeatureUsabilityItemId", "FeatureUsabilityEvent", "Text_Typing", "Time Over");
                        notifyTimeOver(TypeTextGameActivity.this, getString(R.string.app_name), "Oops, time is over. Better luck, next time!", true);
                        if (textTypingTimer != null) {
                            textTypingTimer.cancel();
                            textTypingTimer = null;
                        }
                    }
                }.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void notifyTimeOver(final Activity activity, String title, String message, boolean isFinish) {
        try {
            if (activity != null) {
                final Dialog dialog1 = new Dialog(activity, android.R.style.Theme_Light);
                dialog1.getWindow().setBackgroundDrawableResource(R.color.black_transparent);
                dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog1.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                dialog1.setContentView(R.layout.popup_message_notify);
                dialog1.setCancelable(false);

                Button btnOk = dialog1.findViewById(R.id.btnOk);
                TextView tvTitle = dialog1.findViewById(R.id.tvTitle);
                tvTitle.setText(title);
                TextView tvMessage = dialog1.findViewById(R.id.tvMessage);
                tvMessage.setText(message);
                btnOk.setOnClickListener(v -> {
                    AdsUtil.showAppLovinInterstitialAd(TypeTextGameActivity.this, new AdsUtil.AdShownListener() {
                        @Override
                        public void onAdDismiss() {
                            try {
                                dialog1.dismiss();
                                if (isFinish && !activity.isFinishing()) {
                                    activity.finish();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                });
                if (!activity.isFinishing()) {
                    dialog1.show();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void showBetterluckPopup() {
        Dialog dilaogBetterluck = new Dialog(TypeTextGameActivity.this, android.R.style.Theme_Light);
        dilaogBetterluck.getWindow().setBackgroundDrawableResource(R.color.black_transparent);
        dilaogBetterluck.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dilaogBetterluck.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        dilaogBetterluck.setCancelable(false);
        dilaogBetterluck.setCanceledOnTouchOutside(false);
        dilaogBetterluck.setContentView(R.layout.popup_not_win);

        TextView tvMessage = dilaogBetterluck.findViewById(R.id.tvMessage);
        tvMessage.setText("Sorry, text didn't match.\nBetter luck next time!");
        Button lDone = dilaogBetterluck.findViewById(R.id.btnOk);

        lDone.setOnClickListener(v -> {
            AdsUtil.showAppLovinInterstitialAd(TypeTextGameActivity.this, new AdsUtil.AdShownListener() {
                @Override
                public void onAdDismiss() {
                    if (dilaogBetterluck != null) {
                        dilaogBetterluck.dismiss();
                    }
                }
            });
        });
        dilaogBetterluck.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                tvText.setText(objTextTypingData.getData().getText());
                tvWinningPoints.setText(objTextTypingData.getData().getPoints());
                if (objTextTypingData.getData().getTimer() != null) {
                    textTypingTime = Integer.parseInt(objTextTypingData.getData().getTimer());// minutes
                }
                tvRemainingTime.setText(CommonMethodsUtils.updateTimeRemainingLuckyNumber(textTypingTime * 1000L));
                etText.setText("");
                lifeline = Integer.parseInt(objTextTypingData.getLifeline());
                tvAttemptsLeft.setText(String.valueOf(lifeline));
                setMainTimer(false);
            }
        });
        if (!isFinishing() && !dilaogBetterluck.isShowing()) {
            dilaogBetterluck.show();
        }
    }

    public void showWinPopup(String point) {
        try {
            Dialog dialogWin = new Dialog(TypeTextGameActivity.this, android.R.style.Theme_Light);
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
                    AdsUtil.showAppLovinRewardedAd(TypeTextGameActivity.this, new AdsUtil.AdShownListener() {
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
                AdsUtil.showAppLovinRewardedAd(TypeTextGameActivity.this, new AdsUtil.AdShownListener() {
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
                    CommonMethodsUtils.GetCoinAnimation(TypeTextGameActivity.this, layoutMain, layoutPoints);
                    tvPoints.setText(SharePreference.getInstance().getEarningPointString());
                    tvText.setText(objTextTypingData.getData().getText());
                    tvWinningPoints.setText(objTextTypingData.getData().getPoints());
                    if (objTextTypingData.getData().getTimer() != null) {
                        textTypingTime = Integer.parseInt(objTextTypingData.getData().getTimer());// minutes
                    }
                    tvRemainingTime.setText(CommonMethodsUtils.updateTimeRemainingLuckyNumber(textTypingTime * 1000L));
                    etText.setText("");
                    lifeline = Integer.parseInt(objTextTypingData.getLifeline());
                    tvAttemptsLeft.setText(String.valueOf(lifeline));
                    setMainTimer(false);
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
            nativeAdLoaderWin = new MaxNativeAdLoader(CommonMethodsUtils.getRandomAdUnitId(responseMain.getLovinNativeID()), TypeTextGameActivity.this);
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

    @Override
    protected void onStop() {
        super.onStop();
        if (isFinishing()) {
            try {
                if (textTypingTimer != null) {
                    textTypingTimer.cancel();
                }
                if (mainTimer != null) {
                    mainTimer.cancel();
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
                if (nativeAdTimer != null && nativeAdLoaderTimer != null) {
                    nativeAdLoaderTimer.destroy(nativeAdTimer);
                    nativeAdTimer = null;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    @Override
    public void onBackPressed() {
        try {
            if (objTextTypingData != null && objTextTypingData.getAppLuck() != null ) {
                
                CommonMethodsUtils.dialogShowAppLuck(TypeTextGameActivity.this, objTextTypingData.getAppLuck());
            } else {
                super.onBackPressed();
            }
        } catch (Exception e) {
            e.printStackTrace();
            super.onBackPressed();
        }
    }

    public void setData(TypeTextDataModel responseModel) {
        objTextTypingData = responseModel;
        if (responseModel.getData() != null) {
            layoutContent.setVisibility(View.VISIBLE);
            try {
                if (objTextTypingData.getTodayDate() != null) {
                    todayDate = objTextTypingData.getTodayDate();
                }
                if (objTextTypingData.getLastDate() != null) {
                    lastDate = objTextTypingData.getLastDate();
                }

                if (objTextTypingData.getMainTimer() != null) {
                    mainTimerTime = objTextTypingData.getMainTimer();
                }

                if (objTextTypingData.getRemainCount() != null) {
                    tvRemainCount.setText(objTextTypingData.getRemainCount());
                    remainCount = Integer.parseInt(objTextTypingData.getRemainCount());
                }
                if (objTextTypingData.getData().getTimer() != null) {
                    textTypingTime = Integer.parseInt(objTextTypingData.getData().getTimer());// minutes
                }
                tvRemainingTime.setText(CommonMethodsUtils.updateTimeRemainingLuckyNumber(textTypingTime * 1000L));
                tvDailyLimit.setText(objTextTypingData.getTotalCount());
                tvText.setText(objTextTypingData.getData().getText());
                tvWinningPoints.setText(objTextTypingData.getData().getPoints());
                if (objTextTypingData.getLifeline() != null) {
                    lifeline = Integer.parseInt(objTextTypingData.getLifeline());
                    tvAttemptsLeft.setText(String.valueOf(lifeline));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            setMainTimer(true);
            checkForTaskCompletion();
            // Show Default AppLuck
            if (!CommonMethodsUtils.isStringNullOrEmpty(responseMain.getIsShowAppluck()) && responseMain.getIsShowAppluck().equals("1") && !CommonMethodsUtils.isStringNullOrEmpty(responseModel.getIsDefaultAppluck()) && responseModel.getIsDefaultAppluck().equals("1")) {
                RelativeLayout relMainFlotAppLuck = findViewById(R.id.relMainFlotAppLuck);
                LinearLayout layoutDefaultAd = findViewById(R.id.layoutDefaultAd);
                CommonMethodsUtils.showDefaultAppLuck(TypeTextGameActivity.this, layoutDefaultAd, relMainFlotAppLuck, responseMain.getDefaultAppluckID());
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
                    CommonMethodsUtils.loadTopBannerAd(TypeTextGameActivity.this, layoutTopAds, responseModel.getTopAds());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (!CommonMethodsUtils.isStringNullOrEmpty(objTextTypingData.getHelpVideoUrl())) {
                ivHelp.setVisibility(VISIBLE);
                ivHelp.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CommonMethodsUtils.openUrl(TypeTextGameActivity.this, objTextTypingData.getHelpVideoUrl());
                    }
                });
            }
            loadNativeAdInMainScreen();
        }
    }

    private void loadNativeAdInMainScreen() {
        layoutAds = findViewById(R.id.layoutAds);
        frameLayoutNativeAd = findViewById(R.id.fl_adplaceholder);
        lblLoadingAds = findViewById(R.id.lblLoadingAds);
        if (layoutTimer.getVisibility() == GONE && layoutCompleteTask.getVisibility() == GONE) {
            if (CommonMethodsUtils.isShowAppLovinNativeAds()) {
                lblLoadingAds.setVisibility(VISIBLE);
                loadAppLovinNativeAds();
            } else {
                layoutAds.setVisibility(GONE);
            }
        } else {
            layoutAds.setVisibility(GONE);
        }
    }

    private void checkForTaskCompletion() {
        try {
            layoutCompleteTask = findViewById(R.id.layoutCompleteTask);
            if (remainCount > 0 && !isTimerSet && !CommonMethodsUtils.isStringNullOrEmpty(objTextTypingData.getIsTodayTaskCompleted()) && objTextTypingData.getIsTodayTaskCompleted().equals("0")) {
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
                tvTaskNote.setText(objTextTypingData.getTaskNote());
                Button btnCompleteTask = findViewById(R.id.btnCompleteTask);
                if (!CommonMethodsUtils.isStringNullOrEmpty(objTextTypingData.getTaskButton())) {
                    btnCompleteTask.setText(objTextTypingData.getTaskButton());
                }
                btnCompleteTask.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!CommonMethodsUtils.isStringNullOrEmpty(objTextTypingData.getScreenNo())) {
//                            if (!CommonMethodsUtils.hasUsageAccessPermission(TypeTextGameActivity.this)) {
//                                CommonMethodsUtils.showUsageAccessPermissionDialog(TypeTextGameActivity.this);
//                                return;
//                            } else {
                                CommonMethodsUtils.Redirect(TypeTextGameActivity.this, objTextTypingData.getScreenNo(), "", "", "", "", "");
//                            }
                        } else if (!CommonMethodsUtils.isStringNullOrEmpty(objTextTypingData.getTaskId())) {
                            Intent intent = new Intent(TypeTextGameActivity.this, TaskInfoActivity.class);
                            intent.putExtra("taskId", objTextTypingData.getTaskId());
                            startActivity(intent);
                        } else {
                            Intent intent = new Intent(TypeTextGameActivity.this, TaskListActivity.class);
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
    }

    private void loadAppLovinNativeAds() {
        try {
            nativeAdLoader = new MaxNativeAdLoader(CommonMethodsUtils.getRandomAdUnitId(responseMain.getLovinNativeID()), TypeTextGameActivity.this);
            nativeAdLoader.setNativeAdListener(new MaxNativeAdListener() {
                @Override
                public void onNativeAdLoaded(final MaxNativeAdView nativeAdView, final MaxAd ad) {
                    frameLayoutNativeAd.setVisibility(View.VISIBLE);
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
                    layoutAds.setVisibility(View.VISIBLE);
                    lblLoadingAds.setVisibility(View.GONE);
                    //AppLogger.getInstance().e("AppLovin Loaded: ", "===");
                }

                @Override
                public void onNativeAdLoadFailed(final String adUnitId, final MaxError error) {
                    layoutAds.setVisibility(View.GONE);
                    //AppLogger.getInstance().e("AppLovin Failed: ", error.getMessage());
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

    private void loadAppLovinNativeAdsTask(LinearLayout layoutAds, FrameLayout frameLayoutNativeAd, TextView lblLoadingAds) {
        try {
            nativeAdLoaderTask = new MaxNativeAdLoader(CommonMethodsUtils.getRandomAdUnitId(responseMain.getLovinNativeID()), TypeTextGameActivity.this);
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

    private void loadAppLovinNativeAdsTimer(LinearLayout layoutAds, FrameLayout frameLayoutNativeAd, TextView lblLoadingAds) {
        try {
            nativeAdLoaderTimer = new MaxNativeAdLoader(CommonMethodsUtils.getRandomAdUnitId(responseMain.getLovinNativeID()), TypeTextGameActivity.this);
            nativeAdLoaderTimer.setNativeAdListener(new MaxNativeAdListener() {
                @Override
                public void onNativeAdLoaded(final MaxNativeAdView nativeAdView, final MaxAd ad) {
                    if (nativeAdTimer != null) {
                        nativeAdLoaderTimer.destroy(nativeAdTimer);
                    }
                    nativeAdTimer = ad;
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
            nativeAdLoaderTimer.loadAd();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setMainTimer(boolean isFromOnCreate) {
        if (remainCount == 0) {
            isTimerSet = false;
            showTimerView(true);
            if (isFromOnCreate) {
                AdsUtil.showAppLovinInterstitialAd(TypeTextGameActivity.this, null);
            }
            return;
        }
        if (CommonMethodsUtils.timeDiff(todayDate, lastDate) > Integer.parseInt(mainTimerTime)) {
            isTimerSet = false;
        } else {
            isTimerSet = true;
            showTimerView(false);
            if (mainTimer != null) {
                mainTimer.cancel();
            }
            time = CommonMethodsUtils.timeDiff(todayDate, lastDate);
            mainTimer = new CountDownTimer((Integer.parseInt(mainTimerTime) - time) * 60000L, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    tvMainTimer.setText(CommonMethodsUtils.updateTimeRemaining(millisUntilFinished));
                }

                @Override
                public void onFinish() {
                    try {
                        isTimerSet = false;
                        layoutTimer.setVisibility(View.GONE);
                        tvMainTimer.setText("");
                        checkForTaskCompletion();
                        if (layoutAds.getVisibility() == GONE) {
                            loadNativeAdInMainScreen();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }.start();
            if (isFromOnCreate) {
                AdsUtil.showAppLovinInterstitialAd(TypeTextGameActivity.this, null);
            }
        }
    }

    private void showTimerView(boolean isLimitOver) {
        try {
            if (nativeAd != null && nativeAdLoader != null) {
                nativeAdLoader.destroy(nativeAd);
                nativeAd = null;
                frameLayoutNativeAd = null;
                layoutAds.setVisibility(GONE);
            }
            layoutTimer.setVisibility(VISIBLE);
            if (isLimitOver) {
                tvMainTimer.setVisibility(GONE);
                lblTimer.setText("You have exhausted your text typing daily limit, please try again tomorrow.");
            } else {
                tvMainTimer.setVisibility(VISIBLE);
                lblTimer.setText("Please wait, Text Typing will get unlock in ");
            }
            LinearLayout layoutAdsTimer = findViewById(R.id.layoutAdsTimer);
            TextView lblLoadingAdsTimer = findViewById(R.id.lblLoadingAdsTimer);
            FrameLayout nativeAdTimer = findViewById(R.id.fl_adplaceholder_timer);

            if (CommonMethodsUtils.isShowAppLovinNativeAds()) {
                loadAppLovinNativeAdsTimer(layoutAdsTimer, nativeAdTimer, lblLoadingAdsTimer);
            } else {
                layoutAdsTimer.setVisibility(GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}