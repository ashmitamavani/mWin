/*
 * Copyright (c) 2021.  Hurricane Development Studios
 */

package com.mwin.reward.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.nativeAds.MaxNativeAdListener;
import com.applovin.mediation.nativeAds.MaxNativeAdLoader;
import com.applovin.mediation.nativeAds.MaxNativeAdView;
import com.google.gson.Gson;
import com.mwin.reward.R;
import com.mwin.reward.async.models.MainResponseModel;
import com.mwin.reward.utils.CommonMethodsUtils;
import com.mwin.reward.utils.SharePreference;
import com.mwin.reward.value.Constants;

public class EarnedWalletBalanceActivity extends AppCompatActivity {
    private AppCompatButton btnWithdraw;
    private View viewShine;
    private TextView lblLoadingAds, tvWalletPoints, tvWalletRupees, tvPoints, tvRupees;
    private MainResponseModel responseMain;
    private MaxAd nativeAd;
    private MaxNativeAdLoader nativeAdLoader;
    private FrameLayout frameLayoutNativeAd;
    private LinearLayout layoutAds, layoutPointHistory, layoutWithdrawalHistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        CommonMethodsUtils.setDayNightTheme(EarnedWalletBalanceActivity.this);
        setContentView(R.layout.activity_earned_wallet_balance);

        responseMain = new Gson().fromJson(SharePreference.getInstance().getString(SharePreference.HomeData), MainResponseModel.class);
        viewShine = findViewById(R.id.viewShine);

        lblLoadingAds = findViewById(R.id.lblLoadingAds);
        layoutAds = findViewById(R.id.layoutAds);
        layoutAds.setVisibility(View.VISIBLE);
        frameLayoutNativeAd = findViewById(R.id.fl_adplaceholder);
        if (CommonMethodsUtils.isShowAppLovinNativeAds()) {
            loadAppLovinNativeAds();
        } else {
            layoutAds.setVisibility(View.GONE);
        }

        btnWithdraw = findViewById(R.id.btnWithdraw);
        btnWithdraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent inSpin = new Intent(EarnedWalletBalanceActivity.this, RedeemOptionsListActivity.class);
                startActivity(inSpin);
            }
        });

        ImageView imBack = findViewById(R.id.ivBack);
        imBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        layoutAds = findViewById(R.id.layoutAds);

        layoutPointHistory = findViewById(R.id.layoutPointHistory);
        layoutPointHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SharePreference.getInstance().getBoolean(SharePreference.IS_LOGIN)) {
                    startActivity(new Intent(EarnedWalletBalanceActivity.this, EarnedPointHistoryActivity.class));
                } else {
                    CommonMethodsUtils.NotifyLogin(EarnedWalletBalanceActivity.this);
                }
            }
        });
        layoutWithdrawalHistory = findViewById(R.id.layoutWithdrawalHistory);
        layoutWithdrawalHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SharePreference.getInstance().getBoolean(SharePreference.IS_LOGIN)) {
                    Intent intent = new Intent(EarnedWalletBalanceActivity.this, EarnedPointHistoryActivity.class);
                    intent.putExtra("type", Constants.HistoryType.WITHDRAW_HISTORY);
                    intent.putExtra("title", "Withdrawal History");
                    startActivity(intent);
                } else {
                    CommonMethodsUtils.NotifyLogin(EarnedWalletBalanceActivity.this);
                }
            }
        });

        tvWalletPoints = findViewById(R.id.tvWalletPoints);
        tvWalletRupees = findViewById(R.id.tvWalletRupees);
        tvPoints = findViewById(R.id.tvPoints);
        tvRupees = findViewById(R.id.tvRupees);
        Animation animUpDown = AnimationUtils.loadAnimation(EarnedWalletBalanceActivity.this, R.anim.left_to_right);
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

        // Show Default AppLuck
        if (!CommonMethodsUtils.isStringNullOrEmpty(responseMain.getIsShowAppluck()) && responseMain.getIsShowAppluck().equals("1") && !CommonMethodsUtils.isStringNullOrEmpty(responseMain.getIsDefaultAppluck()) && responseMain.getIsDefaultAppluck().equals("1")) {
            RelativeLayout relMainFlotAppLuck = findViewById(R.id.relMainFlotAppLuck);
            LinearLayout layoutDefaultAd = findViewById(R.id.layoutDefaultAd);
            CommonMethodsUtils.showDefaultAppLuck(EarnedWalletBalanceActivity.this, layoutDefaultAd, relMainFlotAppLuck, responseMain.getDefaultAppluckID());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            tvWalletPoints.setText(SharePreference.getInstance().getEarningPointString());
            tvRupees.setText("₹ 1");
            if (responseMain.getPointValue() != null) {
                tvPoints.setText(responseMain.getPointValue() + " Points");
                tvWalletRupees.setText(CommonMethodsUtils.convertPointsInINR(SharePreference.getInstance().getEarningPointString(), responseMain.getPointValue()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadAppLovinNativeAds() {
        try {
            nativeAdLoader = new MaxNativeAdLoader(CommonMethodsUtils.getRandomAdUnitId(responseMain.getLovinNativeID()), EarnedWalletBalanceActivity.this);
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
    public void onBackPressed() {
        super.onBackPressed();
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
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}