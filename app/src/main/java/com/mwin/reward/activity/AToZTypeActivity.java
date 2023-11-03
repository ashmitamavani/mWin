package com.mwin.reward.activity;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.SystemClock;
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
import com.mwin.reward.adapter.AlphabetAdapter;
import com.mwin.reward.async.Get_Alphabet_Async;
import com.mwin.reward.async.Save_Alphabet_Async;
import com.mwin.reward.async.models.Alphabet_Item;
import com.mwin.reward.async.models.Alphabet_model;
import com.mwin.reward.async.models.MainResponseModel;
import com.mwin.reward.utils.AdsUtil;
import com.mwin.reward.utils.CommonMethodsUtils;
import com.mwin.reward.utils.SharePreference;
import com.mwin.reward.value.Constants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import io.reactivex.annotations.NonNull;

public class AToZTypeActivity extends AppCompatActivity {
    public ArrayList<String> temp = new ArrayList<>();
    public List<Alphabet_Item> data;
    int gameTime, nextGameTime;
    Alphabet_model alphaModel;
    int time, selPos = 0;
    boolean isWrongSelect = false;
    private MainResponseModel responseMain;
    private RecyclerView rvAlphabet;
    private ImageView ivHistory;
    private FrameLayout frameLayoutNativeAd;
    private MaxAd nativeAd, nativeAdWin, nativeAdTask;
    private MaxNativeAdLoader nativeAdLoader, nativeAdLoaderWin, nativeAdLoaderTask;
    private String todayDate, lastDate;
    private boolean isTimerOn = false;
    private long lastClickTime = 0;
    private AlphabetAdapter alphabetAdapter;
    private CountDownTimer timer, Maintimer;
    private LottieAnimationView ltStartTimer;
    private LinearLayout layoutAds, llLimit, layoutPoints, layoutNoData, llRecycle, layoutCompleteTask;
    private TextView tvInfo, tvRemaining, tvWinPoints, tvPoints, lblLoadingAds, tvLeftCount, tvNote, tvTimeUp;
    private RelativeLayout ilAttempt, layoutMain, relStartTimer;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        CommonMethodsUtils.setDayNightTheme(AToZTypeActivity.this);
        setContentView(R.layout.activity_a_to_ztype);
        responseMain = new Gson().fromJson(SharePreference.getInstance().getString(SharePreference.HomeData), MainResponseModel.class);
        tvRemaining = findViewById(R.id.tvRemaining);
        rvAlphabet = findViewById(R.id.rvAlphabet);
        lblLoadingAds = findViewById(R.id.lblLoadingAds);
        llLimit = findViewById(R.id.llLimit);
        relStartTimer = findViewById(R.id.relStartTimer);
        ltStartTimer = findViewById(R.id.ltStartTimer);
        ilAttempt = findViewById(R.id.ilAttempt);
        llRecycle = findViewById(R.id.llRecycle);
        layoutMain = findViewById(R.id.layoutMain);
        layoutCompleteTask = findViewById(R.id.layoutCompleteTask);
        tvTimeUp = findViewById(R.id.tvTimeUp);
        ivHistory = findViewById(R.id.ivHistory);
        tvLeftCount = findViewById(R.id.tvLeftCount);
        layoutAds = findViewById(R.id.layoutAds);
        tvInfo = findViewById(R.id.tvInfo);
        tvNote = findViewById(R.id.tvNote);
        tvPoints = findViewById(R.id.tvPoints);
        layoutPoints = findViewById(R.id.layoutPoints);
        tvWinPoints = findViewById(R.id.tvWinPoints);

        tvPoints.setText(SharePreference.getInstance().getEarningPointString());

        layoutPoints.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SharePreference.getInstance().getBoolean(SharePreference.IS_LOGIN)) {
                    startActivity(new Intent(AToZTypeActivity.this, EarnedWalletBalanceActivity.class));
                } else {
                    CommonMethodsUtils.NotifyLogin(AToZTypeActivity.this);
                }
            }
        });
        ImageView ivBack = findViewById(R.id.ivBack);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        if (CommonMethodsUtils.isShowAppLovinNativeAds()) {
            loadAppLovinNativeAds();
        } else {
            layoutAds.setVisibility(GONE);
        }
        ivHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SharePreference.getInstance().getBoolean(SharePreference.IS_LOGIN)) {
                    startActivity(new Intent(AToZTypeActivity.this, EarnedPointHistoryActivity.class)
                            .putExtra("type", Constants.HistoryType.ALPHABET_GAME)
                            .putExtra("title", "Alphabet Game History"));
                } else {
                    CommonMethodsUtils.NotifyLogin(AToZTypeActivity.this);
                }
            }
        });

        new Get_Alphabet_Async(AToZTypeActivity.this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (isFinishing()) {
            try {
                if (timer != null) {
                    timer.cancel();
                }
                if (Maintimer != null) {
                    Maintimer.cancel();
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
                if (nativeAdTask != null && nativeAdTask != null) {
                    nativeAdLoaderTask.destroy(nativeAdTask);
                    nativeAdTask = null;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void setGameStartTimer() {
        if (layoutCompleteTask.getVisibility() == GONE) {
            relStartTimer.setVisibility(VISIBLE);
            ltStartTimer.setMinFrame(60);
            ltStartTimer.playAnimation();
            ltStartTimer.addAnimatorListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(@NonNull Animator animation) {

                }

                @Override
                public void onAnimationEnd(@NonNull Animator animation) {
                    relStartTimer.setVisibility(GONE);
                    setTimer();

                }

                @Override
                public void onAnimationCancel(@NonNull Animator animation) {

                }

                @Override
                public void onAnimationRepeat(@NonNull Animator animation) {

                }
            });
        }
    }

    private void loadAppLovinNativeAds() {
        try {
            nativeAdLoader = new MaxNativeAdLoader(CommonMethodsUtils.getRandomAdUnitId(responseMain.getLovinNativeID()), AToZTypeActivity.this);
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

    private void loadAppLovinNativeAds(FrameLayout frameLayoutNativeAd, TextView lblLoadingAds) {
        try {
            nativeAdLoaderWin = new MaxNativeAdLoader(CommonMethodsUtils.getRandomAdUnitId(responseMain.getLovinNativeID()), AToZTypeActivity.this);
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
    public void onBackPressed() {
        try {
            if (responseModel != null && responseModel.getAppLuck() != null) {

                CommonMethodsUtils.dialogShowAppLuck(AToZTypeActivity.this, responseModel.getAppLuck());
            } else {
                super.onBackPressed();
            }
        } catch (Exception e) {
            e.printStackTrace();
            super.onBackPressed();
        }
    }

    private Alphabet_model responseModel;

    @SuppressLint("SetTextI18n")
    public void setData(Alphabet_model responseModel1) {
        responseModel = responseModel1;
        try {
            if (responseModel.getStatus().equals("2")) {
                AdsUtil.showAppLovinInterstitialAd(AToZTypeActivity.this, null);
                llLimit.setVisibility(VISIBLE);
                if (responseModel.getGameText() != null) {
                    tvInfo.setText(responseModel.getGameText());
                }
                if (responseModel.getRemainGameCount() != null) {
                    tvLeftCount.setText("Today's " + " " + responseModel.getRemainGameCount());
                }
                isTimerOn = true;
                tvNote.setText("You have exhausted today's Alphabet Game limit, please try again tomorrow.");
                data = new ArrayList<>();
                data.addAll(responseModel.getData());
                AdpaterData(data);
                if (responseModel.getPoints() != null) {
                    tvWinPoints.setText(responseModel.getPoints());
                }
                tvRemaining.setText(CommonMethodsUtils.updateTimeRemainingLuckyNumber(Integer.parseInt(alphaModel.getGameTime()) * 1000L));
            } else {
                alphaModel = responseModel;
                if (!CommonMethodsUtils.isStringNullOrEmpty(responseModel.getEarningPoint())) {
                    SharePreference.getInstance().putString(SharePreference.EarnedPoints, responseModel.getEarningPoint());
                    tvPoints.setText(SharePreference.getInstance().getEarningPointString());
                }
                // Show Default AppLuck
                if (!CommonMethodsUtils.isStringNullOrEmpty(responseMain.getIsShowAppluck()) && responseMain.getIsShowAppluck().equals("1") && !CommonMethodsUtils.isStringNullOrEmpty(responseModel.getIsDefaultAppluck()) && responseModel.getIsDefaultAppluck().equals("1")) {
                    RelativeLayout relMainFlotAppLuck = findViewById(R.id.relMainFlotAppLuck);
                    LinearLayout layoutDefaultAd = findViewById(R.id.layoutDefaultAd);
                    CommonMethodsUtils.showDefaultAppLuck(AToZTypeActivity.this, layoutDefaultAd, relMainFlotAppLuck, responseMain.getDefaultAppluckID());
                }
                try {
                    if (!CommonMethodsUtils.isStringNullOrEmpty(alphaModel.getHomeNote())) {
                        WebView webNote = findViewById(R.id.webNote);
                        webNote.getSettings().setJavaScriptEnabled(true);
                        webNote.setVisibility(View.VISIBLE);
                        webNote.loadDataWithBaseURL(null, alphaModel.getHomeNote(), "text/html", "UTF-8", null);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    LinearLayout layoutCompleteTask = findViewById(R.id.layoutCompleteTask);
                    if (!CommonMethodsUtils.isStringNullOrEmpty(alphaModel.getIsTodayTaskCompleted()) && alphaModel.getIsTodayTaskCompleted().equals("0")) {
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
                        tvTaskNote.setText(alphaModel.getTaskNote());

                        Button btnCompleteTask = findViewById(R.id.btnCompleteTask);
                        if (!CommonMethodsUtils.isStringNullOrEmpty(alphaModel.getTaskButton())) {
                            btnCompleteTask.setText(alphaModel.getTaskButton());
                        }
                        btnCompleteTask.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                if (!CommonMethodsUtils.isStringNullOrEmpty(alphaModel.getScreenNo())) {
//                                    if (!CommonMethodsUtils.hasUsageAccessPermission(AToZTypeActivity.this)) {
//                                        CommonMethodsUtils.showUsageAccessPermissionDialog(AToZTypeActivity.this);
//                                        return;
//                                    } else {
                                    CommonMethodsUtils.Redirect(AToZTypeActivity.this, alphaModel.getScreenNo(), "", "", "", "", "");
//                                    }
                                } else if (!CommonMethodsUtils.isStringNullOrEmpty(alphaModel.getTaskId())) {
                                    Intent intent = new Intent(AToZTypeActivity.this, TaskInfoActivity.class);
                                    intent.putExtra("taskId", alphaModel.getTaskId());
                                    startActivity(intent);
                                } else {
                                    Intent intent = new Intent(AToZTypeActivity.this, TaskListActivity.class);
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

                if (responseModel.getTodayDate() != null) {
                    todayDate = responseModel.getTodayDate();
                }
                if (responseModel.getLastDate() != null) {
                    lastDate = responseModel.getLastDate();
                }
                if (responseModel.getGameTime() != null) {
                    gameTime = Integer.parseInt(responseModel.getGameTime());
                }
                if (responseModel.getNextGameTime() != null) {
                    nextGameTime = Integer.parseInt(responseModel.getNextGameTime());
                }

                if (responseModel.getRemainGameCount() != null) {
                    tvLeftCount.setText("Today's " + " " + responseModel.getRemainGameCount());
                }
                if (responseModel.getGameText() != null) {
                    tvInfo.setText(responseModel.getGameText());
                }
                data = new ArrayList<>();
                data.addAll(responseModel.getData());
                temp = new ArrayList<>();
                for (int i = 0; i < responseModel.getData().size(); i++) {

                    temp.add((String.valueOf(responseModel.getData().get(i).getValue())));
                }

                Collections.sort(temp);
                if (SharePreference.getInstance().getBoolean(SharePreference.IS_LOGIN)) {
                    setTimer1(true);
                }

                AdpaterData(data);
                if (responseModel.getPoints() != null) {
                    tvWinPoints.setText(responseModel.getPoints());
                }
                tvRemaining.setText(CommonMethodsUtils.updateTimeRemainingLuckyNumber(Integer.parseInt(alphaModel.getGameTime()) * 1000L));
            }
        } catch (Exception e) {

        }
    }

    private void loadAppLovinNativeAdsTask(LinearLayout layoutAds, FrameLayout frameLayoutNativeAd, TextView lblLoadingAds) {
        try {
            nativeAdLoaderTask = new MaxNativeAdLoader(CommonMethodsUtils.getRandomAdUnitId(responseMain.getLovinNativeID()), AToZTypeActivity.this);
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

    public void AdpaterData(List<Alphabet_Item> data) {
        alphabetAdapter = new AlphabetAdapter(data, AToZTypeActivity.this, new AlphabetAdapter.ClickListener() {
            @SuppressLint({"ResourceAsColor", "UseCompatLoadingForDrawables"})
            @Override
            public void onItemClick(int position, View v, TextView textView) {
                if (SystemClock.elapsedRealtime() - lastClickTime < 400) {
                    return;
                }

                lastClickTime = SystemClock.elapsedRealtime();
                if (relStartTimer.getVisibility() == GONE) {
                    if (!isTimerOn) {
                        if (SharePreference.getInstance().getBoolean(SharePreference.IS_LOGIN)) {
                            setTimer();
                            if (temp.get(selPos).equals((textView.getText().toString()))) {
                                textView.setBackground(getDrawable(R.drawable.bg_lucky_number_game_selected));
                                textView.setTextColor(getColor(R.color.white));
                                v.setEnabled(false);
                                temp.remove(selPos);
                                if (temp.isEmpty()) {
                                    timer.cancel();
                                    new Save_Alphabet_Async(AToZTypeActivity.this, alphaModel.getPoints());
                                }
                            } else {
                                isWrongSelect = true;
                                timer.cancel();
                                new Save_Alphabet_Async(AToZTypeActivity.this, "0");
                            }
                        } else {
                            CommonMethodsUtils.NotifyLogin(AToZTypeActivity.this);
                        }
                    }
                }

            }
        });
        GridLayoutManager mGridLayoutManager = new GridLayoutManager(AToZTypeActivity.this, 5);
        rvAlphabet.setLayoutManager(mGridLayoutManager);
        rvAlphabet.setAdapter(alphabetAdapter);
    }

    public void setTimer() {
        try {
            if (timer == null) {
                timer = new CountDownTimer(Integer.parseInt(alphaModel.getGameTime()) * 1000L, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        tvRemaining.setText(CommonMethodsUtils.updateTimeRemainingLuckyNumber(millisUntilFinished));
                    }

                    @Override
                    public void onFinish() {
                        CommonMethodsUtils.logFirebaseEvent(AToZTypeActivity.this, "FeatureUsabilityItemId", "FeatureUsabilityEvent", "Alphabet", "Time Over");
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                new Save_Alphabet_Async(AToZTypeActivity.this, "0");
                            }
                        }, 100);
                    }
                }.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setTimer1(boolean isFromOnCreate) {

        if (CommonMethodsUtils.timeDiff(todayDate, lastDate) > nextGameTime) {
            setGameStartTimer();
        } else {
            isTimerOn = true;
            tvTimeUp.setText("Try After Some Time");
            llLimit.setVisibility(VISIBLE);

            if (Maintimer != null) {
                Maintimer.cancel();
            }
            time = CommonMethodsUtils.timeDiff(todayDate, lastDate);

            Maintimer = new CountDownTimer((nextGameTime - time) * 60000L, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    isTimerOn = true;
                    tvTimeUp.setText(updateTimeRemaining(millisUntilFinished));
                }

                @Override
                public void onFinish() {
                    selPos = 0;
                    timer = null;
                    isTimerOn = false;
                    if (!isShowingAd) {
                        setGameStartTimer();
                    }
                    llLimit.setVisibility(GONE);
                    temp = new ArrayList<>();
                    for (int i = 0; i < alphaModel.getData().size(); i++) {
                        temp.add((String.valueOf(alphaModel.getData().get(i).getValue())));
                    }
                    Collections.sort(temp);

                    data = new ArrayList<>();
                    data.addAll(alphaModel.getData());
                    AdpaterData(data);
                }
            }.start();
            if (isFromOnCreate && relStartTimer.getVisibility() == GONE) {
                isShowingAd = true;
                AdsUtil.showAppLovinInterstitialAd(AToZTypeActivity.this, new AdsUtil.AdShownListener() {
                    @Override
                    public void onAdDismiss() {
                        isShowingAd = false;
                        if (!isTimerOn) {
                            setGameStartTimer();
                        }
                    }
                });
            }
        }
    }
    private boolean isShowingAd = false;
    public String updateTimeRemaining(long timeDiff) {
        if (timeDiff > 0) {
            int seconds = (int) (timeDiff / 1000) % 60;
            int minutes = (int) ((timeDiff / (1000 * 60)) % 60);
            int hours = (int) ((timeDiff / (1000 * 60 * 60)) % 24);
            int days = (int) (timeDiff / (1000 * 60 * 60 * 24));
            if (days > 3) {
                return String.format(Locale.getDefault(), "%02d days left", days);
            } else {
                return String.format(Locale.getDefault(), "%02d:%02d:%02d", hours + (days * 24), minutes, seconds);
            }
        } else {
            return "Time's up!!";
        }
    }

    public void showWinPopup(String point) {
        try {
            Dialog dialogWin = new Dialog(AToZTypeActivity.this, android.R.style.Theme_Light);
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
                    AdsUtil.showAppLovinInterstitialAd(AToZTypeActivity.this, new AdsUtil.AdShownListener() {
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

                AdsUtil.showAppLovinInterstitialAd(AToZTypeActivity.this, new AdsUtil.AdShownListener() {
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
                    tvLeftCount.setText("Today's " + " " + alphaModel.getRemainGameCount());
                    tvRemaining.setText(CommonMethodsUtils.updateTimeRemainingLuckyNumber(Integer.parseInt(alphaModel.getGameTime()) * 1000));

                    CommonMethodsUtils.GetCoinAnimation(AToZTypeActivity.this, layoutMain, layoutPoints);
                    tvPoints.setText(SharePreference.getInstance().getEarningPointString());
                    if (!CommonMethodsUtils.isStringNullOrEmpty(alphaModel.getRemainGameCount()) && alphaModel.getRemainGameCount().equals("0")) {
                        isTimerOn = true;
                        llLimit.setVisibility(VISIBLE);
                        tvTimeUp.setVisibility(GONE);
                        tvNote.setText("You have exhausted today's Alphabet Game limit, please try again tomorrow.");
                    } else {
                        llLimit.setVisibility(VISIBLE);
                        tvNote.setText("Try After Some Time");
                        setTimer1(false);
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

    public void showBetterluckPopup(String message) {
        try {
            final Dialog dilaogBetterluck = new Dialog(AToZTypeActivity.this, android.R.style.Theme_Light);
            dilaogBetterluck.getWindow().setBackgroundDrawableResource(R.color.black_transparent);
            dilaogBetterluck.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dilaogBetterluck.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            dilaogBetterluck.setContentView(R.layout.popup_not_win);
            dilaogBetterluck.setCancelable(false);

            Button btnOk = dilaogBetterluck.findViewById(R.id.btnOk);

            TextView tvMessage = dilaogBetterluck.findViewById(R.id.tvMessage);
            tvMessage.setText(message);
            btnOk.setOnClickListener(v -> {
                AdsUtil.showAppLovinInterstitialAd(AToZTypeActivity.this, new AdsUtil.AdShownListener() {
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
                    tvLeftCount.setText("Today's " + " " + alphaModel.getRemainGameCount());
                    tvRemaining.setText(CommonMethodsUtils.updateTimeRemainingLuckyNumber(Integer.parseInt(alphaModel.getGameTime()) * 1000));
                    if (!CommonMethodsUtils.isStringNullOrEmpty(alphaModel.getRemainGameCount()) && alphaModel.getRemainGameCount().equals("0")) {
                        isTimerOn = true;
                        llLimit.setVisibility(VISIBLE);
                        tvTimeUp.setVisibility(GONE);
                        tvNote.setText("You have exhausted today's Alphabet Game limit, please try again tomorrow.");
                    } else {
                        llLimit.setVisibility(VISIBLE);
                        tvNote.setText("Try After Some Time");
                        setTimer1(false);
                    }
                }
            });

            if (!isFinishing() && !dilaogBetterluck.isShowing()) {
                dilaogBetterluck.show();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void changeCountDataValues(Alphabet_model responseModel) {
        alphaModel = responseModel;
        SharePreference.getInstance().putString(SharePreference.EarnedPoints, responseModel.getEarningPoint());

        if (responseModel.getTodayDate() != null) {
            todayDate = responseModel.getTodayDate();
        }
        if (responseModel.getLastDate() != null) {
            lastDate = responseModel.getLastDate();
        }
        if (responseModel.getGameTime() != null) {
            gameTime = Integer.parseInt(responseModel.getGameTime());
        }
        if (responseModel.getNextGameTime() != null) {
            nextGameTime = Integer.parseInt(responseModel.getNextGameTime());
        }

        if (responseModel.getGameText() != null) {
            tvInfo.setText(responseModel.getGameText());
        }


        if (responseModel.getWinningPoints().equals("0")) {
            CommonMethodsUtils.logFirebaseEvent(AToZTypeActivity.this, "FeatureUsabilityItemId", "FeatureUsabilityEvent", "Alphabet", "Better Luck");
            if (isWrongSelect) {
                showBetterluckPopup("Oops, You chose wrong alphabet. Better luck next time!");
            } else {
                showBetterluckPopup("Oops, Time is over. Better luck next time!");
            }
        } else {
            CommonMethodsUtils.logFirebaseEvent(AToZTypeActivity.this, "FeatureUsabilityItemId", "FeatureUsabilityEvent", "Alphabet", "Alphabet Got Reward");
            showWinPopup(responseModel.getWinningPoints());
        }
        isWrongSelect = false;
    }
}