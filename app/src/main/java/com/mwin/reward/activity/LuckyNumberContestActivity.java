/*
 * Copyright (c) 2021.  Hurricane Development Studios
 */

package com.mwin.reward.activity;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.DefaultItemAnimator;
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
import com.mwin.reward.adapter.LuckyNumberGameAdapter;
import com.mwin.reward.async.GetLuckyNumberGameAsync;
import com.mwin.reward.async.SaveLuckyNumberGameAsync;
import com.mwin.reward.async.models.HelpQAModel;
import com.mwin.reward.async.models.LuckyNumberDataModel;
import com.mwin.reward.async.models.LuckyNumberGameItem;
import com.mwin.reward.async.models.MainResponseModel;
import com.mwin.reward.utils.AdsUtil;
import com.mwin.reward.utils.CommonMethodsUtils;
import com.mwin.reward.utils.SharePreference;
import com.mwin.reward.value.Constants;

import java.util.ArrayList;

public class LuckyNumberContestActivity extends AppCompatActivity {
    private RecyclerView rvLuckyNumbers;
    private LinearLayout layoutPoints, layoutNoData;
    private RelativeLayout layoutContent;
    private ImageView ivHistory, ivHelp;
    private ArrayList<LuckyNumberGameItem> listData = new ArrayList<>();
    private TextView tvPoints, lblLoadingAds, lblTitle, lblSubTitle, tvWinningPoints, tvSelectedNumbers, tvTimer, tvContestId;
    private String todayDate, endDate, startDate;
    private CountDownTimer timer;
    private int time;
    private MainResponseModel responseMain;
    private MaxAd nativeAd;
    private MaxNativeAdLoader nativeAdLoader;
    private LinearLayout layoutAds;
    private FrameLayout frameLayoutNativeAd;
    private int selectedNumber1 = 0, selectedNumber2 = 0;
    private LuckyNumberGameAdapter luckyNumberAdapter;
    private LuckyNumberDataModel objRewardScreenModel;
    private AppCompatButton btnSubmit;
    private LottieAnimationView ivLottieNoData;
    private boolean isEdit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        CommonMethodsUtils.setDayNightTheme(LuckyNumberContestActivity.this);
        setContentView(R.layout.activity_lucky_number_contest);
        responseMain = new Gson().fromJson(SharePreference.getInstance().getString(SharePreference.HomeData), MainResponseModel.class);

        btnSubmit = findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    //AppLogger.getInstance().e("SELECTED NUMBERS", "===" + selectedNumber1 + "===" + selectedNumber2);
                    if (SharePreference.getInstance().getBoolean(SharePreference.IS_LOGIN)) {
                        if (selectedNumber1 > 0 && selectedNumber2 > 0) {
                            AdsUtil.showAppLovinInterstitialAd(LuckyNumberContestActivity.this, new AdsUtil.AdShownListener() {
                                @Override
                                public void onAdDismiss() {
                                    new SaveLuckyNumberGameAsync(LuckyNumberContestActivity.this, String.valueOf(selectedNumber1), String.valueOf(selectedNumber2), objRewardScreenModel.getContestId());
                                }
                            });
                        } else {
                            CommonMethodsUtils.setToast(LuckyNumberContestActivity.this, "Please select 2 numbers");
                        }
                    } else {
                        CommonMethodsUtils.NotifyLogin(LuckyNumberContestActivity.this);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        rvLuckyNumbers = findViewById(R.id.rvLuckyNumbers);
        ivLottieNoData = findViewById(R.id.ivLottieNoData);
        layoutNoData = findViewById(R.id.layoutNoData);
        layoutContent = findViewById(R.id.layoutContent);
        layoutContent.setVisibility(View.INVISIBLE);
        lblTitle = findViewById(R.id.lblTitle);
        tvSelectedNumbers = findViewById(R.id.tvSelectedNumbers);
        tvWinningPoints = findViewById(R.id.tvWinningPoints);
        lblSubTitle = findViewById(R.id.lblSubTitle);
        tvTimer = findViewById(R.id.tvTimer);
        tvContestId = findViewById(R.id.tvContestId);
        ivHelp = findViewById(R.id.ivHelp);
        layoutAds = findViewById(R.id.layoutAds);
        frameLayoutNativeAd = findViewById(R.id.fl_adplaceholder);
        lblLoadingAds = findViewById(R.id.lblLoadingAds);
        if (CommonMethodsUtils.isShowAppLovinNativeAds()) {
            loadAppLovinNativeAds();
        } else {
            layoutAds.setVisibility(GONE);
        }

        ImageView ivBack = findViewById(R.id.ivBack);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        tvPoints = findViewById(R.id.tvPoints);
        tvPoints.setText(SharePreference.getInstance().getEarningPointString());
        layoutPoints = findViewById(R.id.layoutPoints);
        layoutPoints.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SharePreference.getInstance().getBoolean(SharePreference.IS_LOGIN)) {
                    startActivity(new Intent(LuckyNumberContestActivity.this, EarnedWalletBalanceActivity.class));
                } else {
                    CommonMethodsUtils.NotifyLogin(LuckyNumberContestActivity.this);
                }
            }
        });

        ivHistory = findViewById(R.id.ivHistory);
        CommonMethodsUtils.startRoundAnimation(LuckyNumberContestActivity.this, ivHistory);
        ivHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SharePreference.getInstance().getBoolean(SharePreference.IS_LOGIN)) {
                    startActivity(new Intent(LuckyNumberContestActivity.this, LuckyNumberContestHistoryActivity.class));
                } else {
                    CommonMethodsUtils.NotifyLogin(LuckyNumberContestActivity.this);
                }
            }
        });
        new GetLuckyNumberGameAsync(LuckyNumberContestActivity.this);
    }

    private void loadAppLovinNativeAds() {
        try {
            nativeAdLoader = new MaxNativeAdLoader(CommonMethodsUtils.getRandomAdUnitId(responseMain.getLovinNativeID()), LuckyNumberContestActivity.this);
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
                if (timer != null) {
                    timer.cancel();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void updateDataChanges() {
        try {
            objRewardScreenModel.setSelectedNumber1(String.valueOf(selectedNumber1));
            objRewardScreenModel.setSelectedNumber2(String.valueOf(selectedNumber2));
            lblTitle.setText("Your Numbers:");
            tvSelectedNumbers.setText(objRewardScreenModel.getSelectedNumber1() + ", " + objRewardScreenModel.getSelectedNumber2());
            btnSubmit.setEnabled(false);
            if (!isEdit) {
                CommonMethodsUtils.logFirebaseEvent(LuckyNumberContestActivity.this, "FeatureUsabilityItemId", "FeatureUsabilityEvent", "Lucky_Number", "Submit");
            }
            isEdit = true;
            btnSubmit.setText("Update");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void onBackPressed() {
        try {
            if (objRewardScreenModel != null && objRewardScreenModel.getAppLuck() != null ) {
                
                CommonMethodsUtils.dialogShowAppLuck(LuckyNumberContestActivity.this, objRewardScreenModel.getAppLuck());
            } else {
                super.onBackPressed();
            }
        } catch (Exception e) {
            e.printStackTrace();
            super.onBackPressed();
        }
    }

    public void setData(LuckyNumberDataModel responseModel) {
        objRewardScreenModel = responseModel;
        try {
            if (!CommonMethodsUtils.isStringNullOrEmpty(responseModel.getEarningPoint())) {
                SharePreference.getInstance().putString(SharePreference.EarnedPoints, responseModel.getEarningPoint());
                tvPoints.setText(SharePreference.getInstance().getEarningPointString());
            }
            if (objRewardScreenModel.getStatus().equals("2")) {
                layoutContent.setVisibility(GONE);
                layoutNoData.setVisibility(VISIBLE);
                ivLottieNoData.playAnimation();
                AdsUtil.showAppLovinInterstitialAd(LuckyNumberContestActivity.this, null);
            } else {
                layoutContent.setVisibility(VISIBLE);
                layoutNoData.setVisibility(GONE);
                try {
                    TextView lblNote = findViewById(R.id.lblNote);
                    LinearLayout layoutCompleteTask = findViewById(R.id.layoutCompleteTask);
                    if (!CommonMethodsUtils.isStringNullOrEmpty(objRewardScreenModel.getIsTodayTaskCompleted()) && objRewardScreenModel.getIsTodayTaskCompleted().equals("0")) {
                        layoutCompleteTask.setVisibility(VISIBLE);
                        lblNote.setVisibility(GONE);
                        TextView tvTaskNote = findViewById(R.id.tvTaskNote);
                        tvTaskNote.setText(objRewardScreenModel.getTaskNote());
                        Button btnCompleteTask = findViewById(R.id.btnCompleteTask);
                        if (!CommonMethodsUtils.isStringNullOrEmpty(objRewardScreenModel.getTaskButton())) {
                            btnCompleteTask.setText(objRewardScreenModel.getTaskButton());
                        }
                        btnCompleteTask.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (!CommonMethodsUtils.isStringNullOrEmpty(objRewardScreenModel.getScreenNo())) {
//                                    if (!CommonMethodsUtils.hasUsageAccessPermission(LuckyNumberContestActivity.this)) {
//                                        CommonMethodsUtils.showUsageAccessPermissionDialog(LuckyNumberContestActivity.this);
//                                        return;
//                                    } else {
                                        CommonMethodsUtils.Redirect(LuckyNumberContestActivity.this, objRewardScreenModel.getScreenNo(), "", "", "", "", "");
//                                    }
                                } else if (!CommonMethodsUtils.isStringNullOrEmpty(objRewardScreenModel.getTaskId())) {
                                    Intent intent = new Intent(LuckyNumberContestActivity.this, TaskInfoActivity.class);
                                    intent.putExtra("taskId", objRewardScreenModel.getTaskId());
                                    startActivity(intent);
                                } else {
                                    Intent intent = new Intent(LuckyNumberContestActivity.this, TaskListActivity.class);
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
                tvContestId.setText("Contest Id: " + objRewardScreenModel.getContestId());
                if (!CommonMethodsUtils.isStringNullOrEmpty(objRewardScreenModel.getSelectedNumber1()) && !CommonMethodsUtils.isStringNullOrEmpty(objRewardScreenModel.getSelectedNumber2())
                        && !objRewardScreenModel.getSelectedNumber1().equals("0") && !objRewardScreenModel.getSelectedNumber2().equals("0")) {
                    lblTitle.setText("My Selected Numbers:");
                    tvSelectedNumbers.setText(objRewardScreenModel.getSelectedNumber1() + ", " + objRewardScreenModel.getSelectedNumber2());
                    btnSubmit.setText("Update");
                    isEdit = true;
                } else {
                    lblTitle.setText("Select Any 2 Lucky Numbers");
                }
                tvWinningPoints.setText(objRewardScreenModel.getWiningPoints());
                if (!CommonMethodsUtils.isStringNullOrEmpty(objRewardScreenModel.getMaxLuckyNumber())) {
                    int no = Integer.parseInt(objRewardScreenModel.getMaxLuckyNumber());
                    for (int i = 0; i < no; i++) {
                        boolean isSelected = false;
                        if ((!CommonMethodsUtils.isStringNullOrEmpty(objRewardScreenModel.getSelectedNumber1()) && objRewardScreenModel.getSelectedNumber1().equals(String.valueOf(i + 1)))
                                || (!CommonMethodsUtils.isStringNullOrEmpty(objRewardScreenModel.getSelectedNumber2()) && objRewardScreenModel.getSelectedNumber2().equals(String.valueOf(i + 1)))) {
                            isSelected = true;
                        }
                        listData.add(new LuckyNumberGameItem(i + 1, isSelected));
                    }
                }
                selectedNumber1 = Integer.parseInt(objRewardScreenModel.getSelectedNumber1());
                selectedNumber2 = Integer.parseInt(objRewardScreenModel.getSelectedNumber2());
                luckyNumberAdapter = new LuckyNumberGameAdapter(listData, LuckyNumberContestActivity.this, new LuckyNumberGameAdapter.ClickListener() {
                    @Override
                    public void onItemClick(int position, View v) {
                        if (selectedNumber1 > 0 && selectedNumber2 > 0 && !listData.get(position).getIsSelected()) {
                            CommonMethodsUtils.NotifyMessage(LuckyNumberContestActivity.this, "Lucky Number", "You have already selected 2 numbers, please deselect any selected number first to select another number", false);
                        } else {
                            if (listData.get(position).getIsSelected()) {
                                if (selectedNumber1 == listData.get(position).getNumber()) {
                                    selectedNumber1 = 0;
                                } else if (selectedNumber2 == listData.get(position).getNumber()) {
                                    selectedNumber2 = 0;
                                }
                            } else {
                                if (selectedNumber1 == 0) {
                                    selectedNumber1 = listData.get(position).getNumber();
                                } else if (selectedNumber2 == 0) {
                                    selectedNumber2 = listData.get(position).getNumber();
                                }
                            }
                            listData.get(position).setIsSelected(!listData.get(position).getIsSelected());
                            luckyNumberAdapter.notifyItemChanged(position);
                        }
                        btnSubmit.setEnabled(isButtonEnabled());
                    }
                });

                GridLayoutManager mGridLayoutManager = new GridLayoutManager(LuckyNumberContestActivity.this, 6);
                mGridLayoutManager.setOrientation(RecyclerView.VERTICAL);
                rvLuckyNumbers.setLayoutManager(mGridLayoutManager);
                rvLuckyNumbers.setItemAnimator(new DefaultItemAnimator());
                rvLuckyNumbers.setAdapter(luckyNumberAdapter);

                // Show Default AppLuck
                if (!CommonMethodsUtils.isStringNullOrEmpty(responseMain.getIsShowAppluck()) && responseMain.getIsShowAppluck().equals("1") && !CommonMethodsUtils.isStringNullOrEmpty(responseModel.getIsDefaultAppluck()) && responseModel.getIsDefaultAppluck().equals("1")) {
                    RelativeLayout relMainFlotAppLuck = findViewById(R.id.relMainFlotAppLuck);
                    LinearLayout layoutDefaultAd = findViewById(R.id.layoutDefaultAd);
                    CommonMethodsUtils.showDefaultAppLuck(LuckyNumberContestActivity.this, layoutDefaultAd, relMainFlotAppLuck, responseMain.getDefaultAppluckID());
                }

                // Load home note webview top
                try {
                    if (!CommonMethodsUtils.isStringNullOrEmpty(objRewardScreenModel.getHomeNote())) {
                        WebView webNote = findViewById(R.id.webNote);
                        webNote.getSettings().setJavaScriptEnabled(true);
                        webNote.setVisibility(View.VISIBLE);
                        webNote.loadDataWithBaseURL(null, objRewardScreenModel.getHomeNote(), "text/html", "UTF-8", null);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // Load top ad
                try {
                    if (objRewardScreenModel.getTopAds() != null && !CommonMethodsUtils.isStringNullOrEmpty(objRewardScreenModel.getTopAds().getImage())) {
                        LinearLayout layoutTopAds = findViewById(R.id.layoutTopAds);
                        CommonMethodsUtils.loadTopBannerAd(LuckyNumberContestActivity.this, layoutTopAds, objRewardScreenModel.getTopAds());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (objRewardScreenModel.getTodayDate() != null) {
                    todayDate = objRewardScreenModel.getTodayDate();
                }
                if (objRewardScreenModel.getEndDate() != null) {
                    endDate = objRewardScreenModel.getEndDate();
                }
                if (objRewardScreenModel.getStartDate() != null) {
                    startDate = objRewardScreenModel.getStartDate();
                }
                setTimer();
                if (!CommonMethodsUtils.isStringNullOrEmpty(objRewardScreenModel.getSelectedNumber1()) && !CommonMethodsUtils.isStringNullOrEmpty(objRewardScreenModel.getSelectedNumber2())
                        && !objRewardScreenModel.getSelectedNumber1().equals("0") && !objRewardScreenModel.getSelectedNumber2().equals("0")) {
                    AdsUtil.showAppLovinInterstitialAd(LuckyNumberContestActivity.this, null);
                }
            }
            if (!CommonMethodsUtils.isStringNullOrEmpty(objRewardScreenModel.getHelpVideoUrl())) {
                ivHelp.setVisibility(VISIBLE);
                ivHelp.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CommonMethodsUtils.openUrl(LuckyNumberContestActivity.this, objRewardScreenModel.getHelpVideoUrl());
                    }
                });
            }
            btnSubmit.setEnabled(isButtonEnabled());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isButtonEnabled() {
        if (isEdit) {
            return (selectedNumber1 > 0 && selectedNumber1 != Integer.parseInt(objRewardScreenModel.getSelectedNumber1()))
                    ||
                    (selectedNumber2 > 0 && selectedNumber2 != Integer.parseInt(objRewardScreenModel.getSelectedNumber2()));
        } else {
            return (selectedNumber1 > 0 && selectedNumber1 != Integer.parseInt(objRewardScreenModel.getSelectedNumber1()))
                    &&
                    (selectedNumber2 > 0 && selectedNumber2 != Integer.parseInt(objRewardScreenModel.getSelectedNumber2()));
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
                    lblSubTitle.setText("Contest is over now, check contest result in History!");
                    tvTimer.setText("");
                }
            }.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}