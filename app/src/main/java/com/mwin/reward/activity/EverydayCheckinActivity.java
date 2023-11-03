/*
 * Copyright (c) 2021.  Hurricane Development Studios
 */

package com.mwin.reward.activity;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
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
import androidx.core.content.ContextCompat;
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
import com.mwin.reward.adapter.EverydayCheckinAdapter;
import com.mwin.reward.async.SaveEverydayCheckinAsync;
import com.mwin.reward.async.models.EarningOptionsScreenModel;
import com.mwin.reward.async.models.EverydayBonus;
import com.mwin.reward.async.models.EverydayBonusDataModel;
import com.mwin.reward.async.models.EverydayBonusItem;
import com.mwin.reward.async.models.MainResponseModel;
import com.mwin.reward.utils.AdsUtil;
import com.mwin.reward.utils.CommonMethodsUtils;
import com.mwin.reward.utils.SharePreference;
import com.mwin.reward.value.Constants;

import java.util.ArrayList;

public class EverydayCheckinActivity extends AppCompatActivity {
    private RecyclerView rvDailyLoginList;
    private LinearLayout layoutPoints;
    private ImageView ivHistory;
    private ArrayList<EverydayBonusItem> listData = new ArrayList<>();
    private TextView tvPoints, lblDailyLogin, lblLoadingAds;
    private MainResponseModel responseMain;
    private MaxAd nativeAd, nativeAdWin;
    private MaxNativeAdLoader nativeAdLoader, nativeAdLoaderWin;
    private LinearLayout layoutAds;
    private FrameLayout frameLayoutNativeAd;
    private int selectedPos = -1, lastClaimDay;
    private EverydayCheckinAdapter dailyLoginAdapter;
    private RelativeLayout layoutMain;
    private EarningOptionsScreenModel objRewardScreenModel;
    private BroadcastReceiver watchWebsiteBroadcast;
    private IntentFilter intentFilter;
    private String bgColor="#4530b3";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        CommonMethodsUtils.setDayNightTheme(EverydayCheckinActivity.this);
        setContentView(R.layout.activity_everyday_checkin);
        responseMain = new Gson().fromJson(SharePreference.getInstance().getString(SharePreference.HomeData), MainResponseModel.class);
        objRewardScreenModel = (EarningOptionsScreenModel) getIntent().getSerializableExtra("objRewardScreenModel");
//        bgColor = getIntent().getStringExtra("bgColor");

        TextView lblTitle = findViewById(R.id.lblTitle);
        TextView lblDailyLoginSubTitle = findViewById(R.id.lblSubTitle);
        lblTitle.setText(getIntent().getStringExtra("title"));
        lblDailyLoginSubTitle.setText(getIntent().getStringExtra("subTitle"));

        layoutMain = findViewById(R.id.layoutMain);
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

        lblDailyLogin = findViewById(R.id.lblDailyLogin);
        tvPoints = findViewById(R.id.tvPoints);
        tvPoints.setText(SharePreference.getInstance().getEarningPointString());
        layoutPoints = findViewById(R.id.layoutPoints);
        layoutPoints.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SharePreference.getInstance().getBoolean(SharePreference.IS_LOGIN)) {
                    startActivity(new Intent(EverydayCheckinActivity.this, EarnedWalletBalanceActivity.class));
                } else {
                    CommonMethodsUtils.NotifyLogin(EverydayCheckinActivity.this);
                }
            }
        });

        ivHistory = findViewById(R.id.ivHistory);
        CommonMethodsUtils.startRoundAnimation(EverydayCheckinActivity.this, ivHistory);
        ivHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SharePreference.getInstance().getBoolean(SharePreference.IS_LOGIN)) {
                    startActivity(new Intent(EverydayCheckinActivity.this, EarnedPointHistoryActivity.class)
                            .putExtra("type", Constants.HistoryType.DAILY_LOGIN)
                            .putExtra("title", "15-Days Streak History"));
                } else {
                    CommonMethodsUtils.NotifyLogin(EverydayCheckinActivity.this);
                }
            }
        });

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
//                            if (!CommonMethodsUtils.hasUsageAccessPermission(EverydayCheckinActivity.this)) {
//                                CommonMethodsUtils.showUsageAccessPermissionDialog(EverydayCheckinActivity.this);
//                                return;
//                            } else {
                            CommonMethodsUtils.Redirect(EverydayCheckinActivity.this, objRewardScreenModel.getScreenNo(), "", "", "", "", "");
//                            }
                        } else if (!CommonMethodsUtils.isStringNullOrEmpty(objRewardScreenModel.getTaskId())) {
                            Intent intent = new Intent(EverydayCheckinActivity.this, TaskInfoActivity.class);
                            intent.putExtra("taskId", objRewardScreenModel.getTaskId());
                            startActivity(intent);
                        } else {
                            Intent intent = new Intent(EverydayCheckinActivity.this, TaskListActivity.class);
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

        try {
            listData.addAll(objRewardScreenModel.getDailyBonus().getData());
            lastClaimDay = Integer.parseInt(objRewardScreenModel.getDailyBonus().getLastClaimedDay());
            if (lastClaimDay > 0) {
                lblDailyLogin.setText("Checked in for " + lastClaimDay + (lastClaimDay == 1 ? " consecutive day" : " consecutive days"));
            } else {
                lblDailyLogin.setText("Check in for the first day to get reward points!");
            }
            rvDailyLoginList = findViewById(R.id.rvDailyLoginList);
            dailyLoginAdapter = new EverydayCheckinAdapter(listData, EverydayCheckinActivity.this, lastClaimDay, Integer.parseInt(objRewardScreenModel.getDailyBonus().getIsTodayClaimed()), bgColor, new EverydayCheckinAdapter.ClickListener() {
                @Override
                public void onItemClick(int position, View v) {
                    if (SharePreference.getInstance().getBoolean(SharePreference.IS_LOGIN)) {
                        if ((Integer.parseInt(listData.get(position).getDay_id())) <= lastClaimDay) {
                            CommonMethodsUtils.setToast(EverydayCheckinActivity.this, "You have already collected reward for day " + listData.get(position).getDay_id());
                        } else if ((objRewardScreenModel.getDailyBonus().getIsTodayClaimed() != null && objRewardScreenModel.getDailyBonus().getIsTodayClaimed().equals("1"))) {
                            CommonMethodsUtils.setToast(EverydayCheckinActivity.this, "You have already collected reward for today");
                        } else if (Integer.parseInt(listData.get(position).getDay_id()) > (lastClaimDay + 1)) {
                            CommonMethodsUtils.setToast(EverydayCheckinActivity.this, "Please claim reward for day " + (lastClaimDay + 1));
                        } else {
                            selectedPos = position;
                            if (!CommonMethodsUtils.isStringNullOrEmpty(objRewardScreenModel.getDailyBonus().getIsWatchWebsite()) && objRewardScreenModel.getDailyBonus().getIsWatchWebsite().equals("1")) {
                                try {
                                    CommonMethodsUtils.showAdLoader(EverydayCheckinActivity.this, "Please wait...");
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            CommonMethodsUtils.dismissAdLoader();
                                            CommonMethodsUtils.showWatchWebDialog(EverydayCheckinActivity.this, objRewardScreenModel.getDailyBonus().getWatchWebsiteUrl(), Integer.parseInt(objRewardScreenModel.getDailyBonus().getWatchWebsiteTime()), listData.get(position).getDay_points());
                                        }
                                    }, 500);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else {
                                AdsUtil.showAppLovinInterstitialAd(EverydayCheckinActivity.this, new AdsUtil.AdShownListener() {
                                    @Override
                                    public void onAdDismiss() {
                                        dailyLoginAdapter.setClicked();
                                        new SaveEverydayCheckinAsync(EverydayCheckinActivity.this, listData.get(position).getDay_points(), listData.get(position).getDay_id());
                                    }
                                });
                            }
                        }
                    } else {
                        CommonMethodsUtils.NotifyLogin(EverydayCheckinActivity.this);
                    }
                }
            });

            GridLayoutManager mGridLayoutManager = new GridLayoutManager(EverydayCheckinActivity.this, 5);
            mGridLayoutManager.setOrientation(RecyclerView.VERTICAL);
            rvDailyLoginList.setLayoutManager(mGridLayoutManager);
            rvDailyLoginList.setItemAnimator(new DefaultItemAnimator());
            rvDailyLoginList.setAdapter(dailyLoginAdapter);

            // Show Default AppLuck
            if (!CommonMethodsUtils.isStringNullOrEmpty(responseMain.getIsShowAppluck()) && responseMain.getIsShowAppluck().equals("1") && !CommonMethodsUtils.isStringNullOrEmpty(objRewardScreenModel.getIsDefaultAppluck()) && objRewardScreenModel.getIsDefaultAppluck().equals("1")) {
                RelativeLayout relMainFlotAppLuck = findViewById(R.id.relMainFlotAppLuck);
                LinearLayout layoutDefaultAd = findViewById(R.id.layoutDefaultAd);
                CommonMethodsUtils.showDefaultAppLuck(EverydayCheckinActivity.this, layoutDefaultAd, relMainFlotAppLuck, responseMain.getDefaultAppluckID());
            }

            // Load home note webview top
            try {
                if (!CommonMethodsUtils.isStringNullOrEmpty(objRewardScreenModel.getDailyBonus().getHomeNote())) {
                    WebView webNote = findViewById(R.id.webNote);
                    webNote.getSettings().setJavaScriptEnabled(true);
                    webNote.setVisibility(View.VISIBLE);
                    webNote.loadDataWithBaseURL(null, objRewardScreenModel.getDailyBonus().getHomeNote(), "text/html", "UTF-8", null);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Load top ad
            try {
                if (objRewardScreenModel.getDailyBonus().getTopAds() != null && !CommonMethodsUtils.isStringNullOrEmpty(objRewardScreenModel.getDailyBonus().getTopAds().getImage())) {
                    LinearLayout layoutTopAds = findViewById(R.id.layoutTopAds);
                    CommonMethodsUtils.loadTopBannerAd(EverydayCheckinActivity.this, layoutTopAds, objRewardScreenModel.getDailyBonus().getTopAds());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (objRewardScreenModel.getDailyBonus().getIsTodayClaimed() != null && objRewardScreenModel.getDailyBonus().getIsTodayClaimed().equals("1")) {
                AdsUtil.showAppLovinInterstitialAd(EverydayCheckinActivity.this, null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        try {
            if (objRewardScreenModel != null && objRewardScreenModel.getAppLuck() != null) {

                CommonMethodsUtils.dialogShowAppLuck(EverydayCheckinActivity.this, objRewardScreenModel.getAppLuck());
            } else {
                super.onBackPressed();
            }
        } catch (Exception e) {
            e.printStackTrace();
            super.onBackPressed();
        }
    }

    public void onDailyLoginDataChanged(EverydayBonusDataModel responseModel) {
        if (!CommonMethodsUtils.isStringNullOrEmpty(responseModel.getEarningPoint())) {
            SharePreference.getInstance().putString(SharePreference.EarnedPoints, responseModel.getEarningPoint());
        }
        if (responseModel.getStatus().equals(Constants.STATUS_SUCCESS) || responseModel.getStatus().equals("3")) {
            CommonMethodsUtils.logFirebaseEvent(EverydayCheckinActivity.this, "FeatureUsabilityItemId", "FeatureUsabilityEvent", "Daily_Login", "Got Reward");
            showWinPopup(listData.get(selectedPos).getDay_points(), responseModel);
        } else if (responseModel.getStatus().equals("2")) {// missed login
            CommonMethodsUtils.logFirebaseEvent(EverydayCheckinActivity.this, "FeatureUsabilityItemId", "FeatureUsabilityEvent", "Daily_Login", "Missed Daily Login");
            showMissedLoginDialog("You missed a Day Streak", responseModel);
        }
        lastClaimDay = Integer.parseInt(responseModel.getLastClaimedDay());
        dailyLoginAdapter.setLastClaimedData(lastClaimDay, Integer.parseInt(responseModel.getIsTodayClaimed()));
        EverydayBonus obj = objRewardScreenModel.getDailyBonus();
        obj.setLastClaimedDay(responseModel.getLastClaimedDay());
        obj.setIsTodayClaimed(responseModel.getIsTodayClaimed());
        objRewardScreenModel.setDailyBonus(obj);
        EarningOptionsActivity.objRewardScreenModel = objRewardScreenModel;
    }

    public void showErrorMessage(String title, String message) {
        try {
            final Dialog dialog1 = new Dialog(EverydayCheckinActivity.this, android.R.style.Theme_Light);
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
                AdsUtil.showAppLovinInterstitialAd(EverydayCheckinActivity.this, new AdsUtil.AdShownListener() {
                    @Override
                    public void onAdDismiss() {
                        if (dialog1 != null) {
                            dialog1.dismiss();
                        }
                    }
                });
            });
            if (!isFinishing()) {
                dialog1.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showMissedLoginDialog(String title, EverydayBonusDataModel responseModel) {
        try {
            final Dialog dialog1 = new Dialog(EverydayCheckinActivity.this, android.R.style.Theme_Light);
            dialog1.getWindow().setBackgroundDrawableResource(R.color.black_transparent);
            dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog1.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            dialog1.setContentView(R.layout.popup_message_notify);
            dialog1.setCancelable(false);

            Button btnOk = dialog1.findViewById(R.id.btnOk);
            TextView tvTitle = dialog1.findViewById(R.id.tvTitle);
            tvTitle.setText(title);

            TextView tvMessage = dialog1.findViewById(R.id.tvMessage);
            tvMessage.setText(responseModel.getMessage());
            btnOk.setOnClickListener(v -> {
                AdsUtil.showAppLovinInterstitialAd(EverydayCheckinActivity.this, new AdsUtil.AdShownListener() {
                    @Override
                    public void onAdDismiss() {
                        if (dialog1 != null) {
                            dialog1.dismiss();
                        }
                    }
                });
            });
            if (!isFinishing()) {
                dialog1.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void showWinPopup(String point, EverydayBonusDataModel responseModel) {
        Dialog dialogWin = new Dialog(EverydayCheckinActivity.this, android.R.style.Theme_Light);
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
                if (dialogWin != null) {
                    dialogWin.dismiss();
                }
            }
        });

        if (!CommonMethodsUtils.isStringNullOrEmpty(responseModel.getBtnName())) {
            btnOk.setText(responseModel.getBtnName());
        }

        if (!CommonMethodsUtils.isStringNullOrEmpty(responseModel.getBtnColor())) {
            Drawable mDrawable = ContextCompat.getDrawable(EverydayCheckinActivity.this, R.drawable.ic_btn_rounded_corner);
            mDrawable.setColorFilter(new PorterDuffColorFilter(Color.parseColor(responseModel.getBtnColor()), PorterDuff.Mode.SRC_IN));
            btnOk.setBackground(mDrawable);
        }

        btnOk.setOnClickListener(v -> {
            AdsUtil.showAppLovinRewardedAd(EverydayCheckinActivity.this, new AdsUtil.AdShownListener() {
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
                selectedPos = -1;
                CommonMethodsUtils.GetCoinAnimation(EverydayCheckinActivity.this, layoutMain, layoutPoints);
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
    }

    private void loadAppLovinNativeAds(FrameLayout frameLayoutNativeAd, TextView lblLoadingAds) {
        try {
            nativeAdLoaderWin = new MaxNativeAdLoader(CommonMethodsUtils.getRandomAdUnitId(responseMain.getLovinNativeID()), EverydayCheckinActivity.this);
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

    private void loadAppLovinNativeAds() {
        try {
            nativeAdLoader = new MaxNativeAdLoader(CommonMethodsUtils.getRandomAdUnitId(responseMain.getLovinNativeID()), EverydayCheckinActivity.this);
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
                if (nativeAdWin != null && nativeAdLoaderWin != null) {
                    nativeAdLoaderWin.destroy(nativeAdWin);
                    nativeAdWin = null;
                }
                unRegisterReceivers();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void registerBroadcast() {
        watchWebsiteBroadcast = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                unRegisterReceivers();
                //AppLogger.getInstance().e("WATCH WEBSITE", "Broadcast Received==" + intent.getAction());
                if (intent.getAction().equals(Constants.WATCH_WEBSITE_RESULT)) {
                    if (intent.getExtras().getString("status").equals(Constants.STATUS_SUCCESS)) {
                        dailyLoginAdapter.setClicked();
                        new SaveEverydayCheckinAsync(EverydayCheckinActivity.this, listData.get(selectedPos).getDay_points(), listData.get(selectedPos).getDay_id());
                    } else {
                        CommonMethodsUtils.Notify(AdsUtil.getCurrentActivity(), "Visit Website", "Oops - " + objRewardScreenModel.getDailyBonus().getWatchWebsiteTime() + " Seconds NOT completed!", false);
                    }
                }
            }
        };
        intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.WATCH_WEBSITE_RESULT);
        registerReceiver(watchWebsiteBroadcast, intentFilter);
    }

    private void unRegisterReceivers() {
        if (watchWebsiteBroadcast != null) {
//            //AppLogger.getInstance().e("SplashActivity", "Unregister Broadcast");
            unregisterReceiver(watchWebsiteBroadcast);
            watchWebsiteBroadcast = null;
        }
    }
}