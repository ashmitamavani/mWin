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
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
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
import com.mwin.reward.adapter.NumberSortingAdapter;
import com.mwin.reward.async.GetNumberSortingDataAsync;
import com.mwin.reward.async.SaveNumberSortingAsync;
import com.mwin.reward.async.models.MainResponseModel;
import com.mwin.reward.async.models.NumberSortingItem;
import com.mwin.reward.async.models.NumberSortingResponseModel;
import com.mwin.reward.customviews.CountDownAnimation;
import com.mwin.reward.utils.AdsUtil;
import com.mwin.reward.utils.AppLogger;
import com.mwin.reward.utils.CommonMethodsUtils;
import com.mwin.reward.utils.SharePreference;
import com.mwin.reward.value.Constants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NumberStoringActivity extends AppCompatActivity {
    public List<NumberSortingItem> dataList = new ArrayList<>();
    public ArrayList<Integer> temp = new ArrayList<>();
    private MainResponseModel responseMain;
    private RecyclerView rvNumber;
    private ImageView ivHistory;
    private FrameLayout frameLayoutNativeAd;
    private MaxAd nativeAd, nativeAdWin, nativeAdTask;
    private MaxNativeAdLoader nativeAdLoader, nativeAdLoaderWin, nativeAdLoaderTask;
    private String todayDate, lastDate;
    private int nextGameTime, time, selPos = 0;
    private boolean isAscending = true, isTimerSet = false, isFinishGame = false;
    private NumberSortingResponseModel countModel;
    private NumberSortingAdapter countUpAdapter;
    private CountDownTimer timer, mainTimer;
    private LinearLayout layoutAds, llLimit, layoutPoints, layoutCountDownTimer;
    private TextView tvInfo, tvRemaining, tvWinPoints, tvPoints, lblLoadingAds, tvLeftCount, tvNote, tvTimeUp;
    private RelativeLayout layoutMain, layoutData;
    private CountDownAnimation countDownAnimation;
    private TextView textView;
    private Spinner spinner;
    private String betterLuckMessage = "Better luck, next time!";

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        CommonMethodsUtils.setDayNightTheme(NumberStoringActivity.this);
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_number_sorting);
        responseMain = new Gson().fromJson(SharePreference.getInstance().getString(SharePreference.HomeData), MainResponseModel.class);
        setViews();
        new GetNumberSortingDataAsync(NumberStoringActivity.this);
    }

    private void setViews() {
        layoutCountDownTimer = findViewById(R.id.layoutCountDownTimer);
        textView = findViewById(R.id.textView);
        spinner = findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.animations_array,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        layoutData = findViewById(R.id.layoutData);
        layoutData.setVisibility(View.INVISIBLE);
        tvRemaining = findViewById(R.id.tvRemaining);
        rvNumber = findViewById(R.id.rvNumber);
        lblLoadingAds = findViewById(R.id.lblLoadingAds);
        llLimit = findViewById(R.id.llLimit);
        layoutMain = findViewById(R.id.layoutMain);
        tvTimeUp = findViewById(R.id.tvTimeUp);
        layoutAds = findViewById(R.id.layoutAds);
        tvLeftCount = findViewById(R.id.tvLeftCount);
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
                    startActivity(new Intent(NumberStoringActivity.this, EarnedWalletBalanceActivity.class));
                } else {
                    CommonMethodsUtils.NotifyLogin(NumberStoringActivity.this);
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
            layoutAds.setVisibility(View.GONE);
        }
        ivHistory = findViewById(R.id.ivHistory);
        CommonMethodsUtils.startRoundAnimation(NumberStoringActivity.this, ivHistory);
        ivHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SharePreference.getInstance().getBoolean(SharePreference.IS_LOGIN)) {
                    startActivity(new Intent(NumberStoringActivity.this, EarnedPointHistoryActivity.class)
                            .putExtra("type", Constants.HistoryType.NUMBER_SORTING)
                            .putExtra("title", "Number Sorting History"));
                } else {
                    CommonMethodsUtils.NotifyLogin(NumberStoringActivity.this);
                }
            }
        });
        initCountDownAnimation();
    }

    private void loadAppLovinNativeAds() {
        try {
            nativeAdLoader = new MaxNativeAdLoader(CommonMethodsUtils.getRandomAdUnitId(responseMain.getLovinNativeID()), NumberStoringActivity.this);
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
                }

                @Override
                public void onNativeAdLoadFailed(final String adUnitId, final MaxError error) {
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

    private void loadAppLovinNativeAds(FrameLayout frameLayoutNativeAd, TextView lblLoadingAds) {
        try {
            nativeAdLoaderWin = new MaxNativeAdLoader(CommonMethodsUtils.getRandomAdUnitId(responseMain.getLovinNativeID()), NumberStoringActivity.this);
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
                }

                @Override
                public void onNativeAdLoadFailed(final String adUnitId, final MaxError error) {
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

                CommonMethodsUtils.dialogShowAppLuck(NumberStoringActivity.this, responseModel.getAppLuck());
            } else {
                super.onBackPressed();
            }
        } catch (Exception e) {
            e.printStackTrace();
            super.onBackPressed();
        }
    }

    private NumberSortingResponseModel responseModel;

    public void setData(NumberSortingResponseModel responseModel1) {
        try {
            responseModel = responseModel1;
            layoutData.setVisibility(VISIBLE);
            countModel = responseModel;
            if (!CommonMethodsUtils.isStringNullOrEmpty(responseModel.getEarningPoint())) {
                SharePreference.getInstance().putString(SharePreference.EarnedPoints, responseModel.getEarningPoint());
                tvPoints.setText(SharePreference.getInstance().getEarningPointString());
            }
            if (responseModel.getStatus().equals("2")) {
                AdsUtil.showAppLovinInterstitialAd(NumberStoringActivity.this, null);
                llLimit.setVisibility(VISIBLE);
                tvTimeUp.setVisibility(GONE);
                tvNote.setText("You have exhausted today's Number Sorting Game limit, please try again tomorrow.");
            }
            try {
                // Show Default AppLuck
                if (!CommonMethodsUtils.isStringNullOrEmpty(responseMain.getIsShowAppluck()) && responseMain.getIsShowAppluck().equals("1") && !CommonMethodsUtils.isStringNullOrEmpty(responseModel.getIsDefaultAppluck()) && responseModel.getIsDefaultAppluck().equals("1")) {
                    RelativeLayout relMainFlotAppLuck = findViewById(R.id.relMainFlotAppLuck);
                    LinearLayout layoutDefaultAd = findViewById(R.id.layoutDefaultAd);
                    CommonMethodsUtils.showDefaultAppLuck(NumberStoringActivity.this, layoutDefaultAd, relMainFlotAppLuck, responseMain.getDefaultAppluckID());
                }

                if (!CommonMethodsUtils.isStringNullOrEmpty(countModel.getHomeNote())) {
                    WebView webNote = findViewById(R.id.webNote);
                    webNote.getSettings().setJavaScriptEnabled(true);
                    webNote.setVisibility(View.VISIBLE);
                    webNote.loadDataWithBaseURL(null, countModel.getHomeNote(), "text/html", "UTF-8", null);
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
            if (responseModel.getNextGameTime() != null) {
                nextGameTime = Integer.parseInt(responseModel.getNextGameTime());
            }
            AdpaterData(dataList);
            if (Integer.parseInt(responseModel.getRemainGameCount()) > 0) {
                setTimer1(true);
            }
            try {
                LinearLayout layoutCompleteTask = findViewById(R.id.layoutCompleteTask);
                if (Integer.parseInt(responseModel.getRemainGameCount()) > 0 && !isTimerSet && !responseModel.getStatus().equals("2") && !CommonMethodsUtils.isStringNullOrEmpty(countModel.getIsTodayTaskCompleted()) && countModel.getIsTodayTaskCompleted().equals("0")) {
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
                    tvTaskNote.setText(countModel.getTaskNote());

                    Button btnCompleteTask = findViewById(R.id.btnCompleteTask);
                    if (!CommonMethodsUtils.isStringNullOrEmpty(countModel.getTaskButton())) {
                        btnCompleteTask.setText(countModel.getTaskButton());
                    }
                    btnCompleteTask.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            if (!CommonMethodsUtils.isStringNullOrEmpty(countModel.getScreenNo())) {
//                                if (!CommonMethodsUtils.hasUsageAccessPermission(NumberStoringActivity.this)) {
//                                    CommonMethodsUtils.showUsageAccessPermissionDialog(NumberStoringActivity.this);
//                                    return;
//                                } else {
                                CommonMethodsUtils.Redirect(NumberStoringActivity.this, countModel.getScreenNo(), "", "", "", "", "");
//                                }
                            } else if (!CommonMethodsUtils.isStringNullOrEmpty(countModel.getTaskId())) {
                                Intent intent = new Intent(NumberStoringActivity.this, TaskInfoActivity.class);
                                intent.putExtra("taskId", countModel.getTaskId());
                                startActivity(intent);
                            } else {
                                Intent intent = new Intent(NumberStoringActivity.this, TaskListActivity.class);
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
            setGameData();
            if (Integer.parseInt(responseModel.getRemainGameCount()) > 0 && !isTimerSet && !responseModel.getStatus().equals("2") && !CommonMethodsUtils.isStringNullOrEmpty(countModel.getIsTodayTaskCompleted()) && countModel.getIsTodayTaskCompleted().equals("1") && SharePreference.getInstance().getBoolean(SharePreference.IS_LOGIN)) {
                startCountDownAnimation();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void setGameData() {
        if (countModel.getRemainGameCount() != null) {
            tvLeftCount.setText(countModel.getRemainGameCount());
        }
        if (countModel.getGameText() != null) {
            tvInfo.setText(countModel.getGameText());
        } else {
            tvInfo.setVisibility(GONE);
        }
        if (!CommonMethodsUtils.isStringNullOrEmpty(countModel.getGameType()) && countModel.getGameType().equals("0")) {
            isAscending = true;
        } else {
            isAscending = false;
        }

        temp.clear();
        for (int i = 0; i < countModel.getData().size(); i++) {
            if (!countModel.getData().get(i).getValue().isEmpty()) {
                temp.add((Integer.parseInt(countModel.getData().get(i).getValue())));
            }
        }
        if (isAscending) {
            Collections.sort(temp);
        } else {
            Collections.sort(temp, Collections.reverseOrder());
        }

        dataList.clear();
        dataList.addAll(countModel.getData());

        countUpAdapter.notifyDataSetChanged();
        AppLogger.getInstance().e("asdas", "SORTED LIST : " + temp.toString());
        if (countModel.getPoints() != null) {
            tvWinPoints.setText(countModel.getPoints());
        }
        tvRemaining.setText(CommonMethodsUtils.updateTimeRemainingLuckyNumber(Integer.parseInt(countModel.getGameTime()) * 1000L));
    }

    private void loadAppLovinNativeAdsTask(LinearLayout layoutAds, FrameLayout frameLayoutNativeAd, TextView lblLoadingAds) {
        try {
            nativeAdLoaderTask = new MaxNativeAdLoader(CommonMethodsUtils.getRandomAdUnitId(responseMain.getLovinNativeID()), NumberStoringActivity.this);
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
                }

                @Override
                public void onNativeAdLoadFailed(final String adUnitId, final MaxError error) {
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

    public void setTimer() {
        try {
            if (timer == null) {
                timer = new CountDownTimer(Integer.parseInt(countModel.getGameTime()) * 1000L, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        tvRemaining.setText(CommonMethodsUtils.updateTimeRemainingLuckyNumber(millisUntilFinished));
                    }

                    @Override
                    public void onFinish() {
                        isFinishGame = true;
                        CommonMethodsUtils.logFirebaseEvent(NumberStoringActivity.this, "FeatureUsabilityItemId", "FeatureUsabilityEvent", "Number_Sorting", "Time Over");
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                betterLuckMessage = "Oops, Time is over.\nBetter luck next time!";
                                new SaveNumberSortingAsync(NumberStoringActivity.this, "0");
                            }
                        }, 100);
                    }
                }.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void changeCountDataValues(NumberSortingResponseModel responseModel) {
        countModel = responseModel;
        SharePreference.getInstance().putString(SharePreference.EarnedPoints, responseModel.getEarningPoint());

        if (responseModel.getTodayDate() != null) {
            todayDate = responseModel.getTodayDate();
        }
        if (responseModel.getLastDate() != null) {
            lastDate = responseModel.getLastDate();
        }
        if (responseModel.getNextGameTime() != null) {
            nextGameTime = Integer.parseInt(responseModel.getNextGameTime());
        }
        if (responseModel.getWinningPoints().equals("0")) {
            CommonMethodsUtils.logFirebaseEvent(NumberStoringActivity.this, "FeatureUsabilityItemId", "FeatureUsabilityEvent", "Number_Sorting", "Better Luck");
            showBetterluckPopup();
        } else {
            CommonMethodsUtils.logFirebaseEvent(NumberStoringActivity.this, "FeatureUsabilityItemId", "FeatureUsabilityEvent", "Number_Sorting", "Number Sorting Got Reward");
            showWinPopup(responseModel.getWinningPoints());
        }
    }

    public void AdpaterData(List<NumberSortingItem> dataList) {
        countUpAdapter = new NumberSortingAdapter(dataList, NumberStoringActivity.this, new NumberSortingAdapter.ClickListener() {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void onItemClick(int position, View v, TextView textView) {
                try {
                    if (SharePreference.getInstance().getBoolean(SharePreference.IS_LOGIN)) {
                        AppLogger.getInstance().e("asdas", "CLICKED :" + dataList.get(position).getValue());
                        if (!isTimerSet && !isFinishGame && !dataList.get(position).isSelected()) {
                            if (temp.get(selPos) == (Integer.parseInt(textView.getText().toString()))) {
                                dataList.get(position).setSelected(true);
                                countUpAdapter.notifyItemChanged(position);
                                temp.remove(selPos);
                                if (temp.isEmpty()) {
                                    isFinishGame = true;
                                    if (timer != null) {
                                        timer.cancel();
                                    }
                                    Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            new SaveNumberSortingAsync(NumberStoringActivity.this, countModel.getPoints());
                                        }
                                    }, 100);
                                }
                            } else {
                                isFinishGame = true;
                                if (timer != null) {
                                    timer.cancel();
                                }
                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        betterLuckMessage = "Oops, Wrong number selected.\nBetter luck next time!";
                                        new SaveNumberSortingAsync(NumberStoringActivity.this, "0");
                                    }
                                }, 100);
                            }
                        }
                    } else {
                        CommonMethodsUtils.NotifyLogin(NumberStoringActivity.this);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        GridLayoutManager mGridLayoutManager = new GridLayoutManager(NumberStoringActivity.this, 5);
        rvNumber.setLayoutManager(mGridLayoutManager);
        rvNumber.setAdapter(countUpAdapter);
    }


    public void showWinPopup(String point) {
        try {
            Dialog dialogWin = new Dialog(NumberStoringActivity.this, android.R.style.Theme_Light);
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
                    AdsUtil.showAppLovinInterstitialAd(NumberStoringActivity.this, new AdsUtil.AdShownListener() {
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
                AdsUtil.showAppLovinInterstitialAd(NumberStoringActivity.this, new AdsUtil.AdShownListener() {
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
                    CommonMethodsUtils.GetCoinAnimation(NumberStoringActivity.this, layoutMain, layoutPoints);
                    tvPoints.setText(SharePreference.getInstance().getEarningPointString());
                    if (!CommonMethodsUtils.isStringNullOrEmpty(countModel.getRemainGameCount()) && countModel.getRemainGameCount().equals("0")) {
                        llLimit.setVisibility(VISIBLE);
                        tvTimeUp.setVisibility(GONE);
                        isTimerSet = true;
                        tvNote.setText("You have exhausted today's Number Sorting Game limit, please try again tomorrow.");
                        tvLeftCount.setText(countModel.getRemainGameCount());
                        tvRemaining.setText(CommonMethodsUtils.updateTimeRemainingLuckyNumber(Integer.parseInt(countModel.getGameTime()) * 1000L));
                    } else {
                        llLimit.setVisibility(VISIBLE);
                        tvNote.setText("Next game will be unlocked in");
                        setGameData();
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

    public void showBetterluckPopup() {
        Dialog dilaogBetterluck = new Dialog(NumberStoringActivity.this, android.R.style.Theme_Light);
        dilaogBetterluck.getWindow().setBackgroundDrawableResource(R.color.black_transparent);
        dilaogBetterluck.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dilaogBetterluck.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        dilaogBetterluck.setCancelable(false);
        dilaogBetterluck.setCanceledOnTouchOutside(false);
        dilaogBetterluck.setContentView(R.layout.popup_not_win);

        TextView tvMessage = dilaogBetterluck.findViewById(R.id.tvMessage);
        tvMessage.setText(betterLuckMessage);

        Button lDone = dilaogBetterluck.findViewById(R.id.btnOk);

        lDone.setOnClickListener(v -> {
            AdsUtil.showAppLovinInterstitialAd(NumberStoringActivity.this, new AdsUtil.AdShownListener() {
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
                if (!CommonMethodsUtils.isStringNullOrEmpty(countModel.getRemainGameCount()) && countModel.getRemainGameCount().equals("0")) {
                    llLimit.setVisibility(VISIBLE);
                    isTimerSet = true;
                    tvTimeUp.setVisibility(GONE);
                    tvNote.setText("You have exhausted today's Number Sorting Game limit, please try again tomorrow.");
                    tvLeftCount.setText(countModel.getRemainGameCount());
                    tvRemaining.setText(CommonMethodsUtils.updateTimeRemainingLuckyNumber(Integer.parseInt(countModel.getGameTime()) * 1000L));
                } else {
                    llLimit.setVisibility(VISIBLE);
                    tvNote.setText("Next game will be unlocked in");
                    setGameData();
                    setTimer1(false);
                }
            }
        });

        if (!isFinishing() && !dilaogBetterluck.isShowing()) {
            dilaogBetterluck.show();
        }
    }

    public void setTimer1(boolean isFromOnCreate) {
        if (CommonMethodsUtils.timeDiffSeconds(todayDate, lastDate) > (nextGameTime * 60)) {
        } else {
            isTimerSet = true;
            llLimit.setVisibility(VISIBLE);

            if (mainTimer != null) {
                mainTimer.cancel();
            }
            time = CommonMethodsUtils.timeDiffSeconds(todayDate, lastDate);
            mainTimer = new CountDownTimer(((nextGameTime * 60L) - time) * 1000L, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    isTimerSet = true;
                    tvTimeUp.setText(CommonMethodsUtils.updateTimeRemainingLuckyNumber(millisUntilFinished));
                }

                @Override
                public void onFinish() {
                    selPos = 0;
                    isFinishGame = false;
                    timer = null;
                    llLimit.setVisibility(GONE);
                    isTimerSet = false;
                    if (!isShowingAd) {
                        startCountDownAnimation();
                    }
                }
            }.start();
            if (isFromOnCreate) {
                isShowingAd = true;
                AdsUtil.showAppLovinInterstitialAd(NumberStoringActivity.this, new AdsUtil.AdShownListener() {
                    @Override
                    public void onAdDismiss() {
                        isShowingAd = false;
                        if (!isTimerSet) {
                            startCountDownAnimation();
                        }
                    }
                });
            }
        }
    }
    private boolean isShowingAd = false;
    @Override
    protected void onStop() {
        super.onStop();
        if (isFinishing()) {
            try {
                cancelCountDownAnimation();
                if (timer != null) {
                    timer.cancel();
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
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void initCountDownAnimation() {
        countDownAnimation = new CountDownAnimation(textView, Constants.countDownTimerCount);
        countDownAnimation.setCountDownListener(new CountDownAnimation.CountDownListener() {
            @Override
            public void onCountDownEnd(CountDownAnimation animation) {
                layoutCountDownTimer.setVisibility(GONE);
                setTimer();
            }
        });
    }

    private void startCountDownAnimation() {
        layoutCountDownTimer.setVisibility(VISIBLE);
        // Customizable animation
        if (spinner.getSelectedItemPosition() == 1) { // Scale
            // Use scale animation
            Animation scaleAnimation = new ScaleAnimation(1.0f, 0.0f, 1.0f,
                    0.0f, Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF, 0.5f);
            countDownAnimation.setAnimation(scaleAnimation);
        } else if (spinner.getSelectedItemPosition() == 2) { // Set (Scale +
            // Alpha)
            // Use a set of animations
            Animation scaleAnimation = new ScaleAnimation(1.0f, 0.0f, 1.0f,
                    0.0f, Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF, 0.5f);
            Animation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);
            AnimationSet animationSet = new AnimationSet(false);
            animationSet.addAnimation(scaleAnimation);
            animationSet.addAnimation(alphaAnimation);
            countDownAnimation.setAnimation(animationSet);
        }
        // Customizable start count
        countDownAnimation.setStartCount(Constants.countDownTimerCount);
        countDownAnimation.start();
    }

    private void cancelCountDownAnimation() {
        countDownAnimation.cancel();
    }
}