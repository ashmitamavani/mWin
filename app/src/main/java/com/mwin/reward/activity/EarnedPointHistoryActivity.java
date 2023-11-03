/*
 * Copyright (c) 2021.  Hurricane Development Studios
 */

package com.mwin.reward.activity;

import android.content.pm.ActivityInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieDrawable;
import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.nativeAds.MaxNativeAdListener;
import com.applovin.mediation.nativeAds.MaxNativeAdLoader;
import com.applovin.mediation.nativeAds.MaxNativeAdView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.google.gson.Gson;
import com.mwin.reward.R;
import com.mwin.reward.adapter.EarnedPointHistoryAdapter;
import com.mwin.reward.adapter.RedeemPointsHistoryAdapter;
import com.mwin.reward.async.GetEarnedPointHistoryAsync;
import com.mwin.reward.async.models.Alphabet_model;
import com.mwin.reward.async.models.EarnedPointHistoryModel;
import com.mwin.reward.async.models.MainResponseModel;
import com.mwin.reward.async.models.WalletListItem;
import com.mwin.reward.utils.AdsUtil;
import com.mwin.reward.utils.CommonMethodsUtils;
import com.mwin.reward.utils.SharePreference;
import com.mwin.reward.value.Constants;

import java.util.ArrayList;
import java.util.List;

public class EarnedPointHistoryActivity extends AppCompatActivity {
    private RecyclerView rvHistoryList;
    private List<WalletListItem> listPointHistory = new ArrayList<>();
    private TextView lblLoadingAds, tvTitle, tvPoints;
    private LottieAnimationView ivLottieNoData;
    private MainResponseModel responseMain;
    private MaxAd nativeAd;
    private MaxNativeAdLoader nativeAdLoader;
    private FrameLayout frameLayoutNativeAd;
    private LinearLayout layoutAds;
    private int pageNo = 1;
    private String type = Constants.HistoryType.ALL;
    private NestedScrollView nestedScrollView;
    private long numOfPage;
    private boolean isAdLoaded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        CommonMethodsUtils.setDayNightTheme(EarnedPointHistoryActivity.this);
        setContentView(R.layout.activity_earned_point_history);
        responseMain = new Gson().fromJson(SharePreference.getInstance().getString(SharePreference.HomeData), MainResponseModel.class);
        tvTitle = findViewById(R.id.tvTitle);
        if (getIntent().getExtras() != null && getIntent().getExtras().containsKey("type")) {
            type = getIntent().getStringExtra("type");
            tvTitle.setText(getIntent().getStringExtra("title"));
        }
        rvHistoryList = findViewById(R.id.rvHistoryList);
        if (type.equals(Constants.HistoryType.WITHDRAW_HISTORY)) {
            rvHistoryList.setAdapter(new RedeemPointsHistoryAdapter(listPointHistory, EarnedPointHistoryActivity.this));
        } else {
            rvHistoryList.setAdapter(new EarnedPointHistoryAdapter(listPointHistory, EarnedPointHistoryActivity.this, type));
        }
        rvHistoryList.setLayoutManager(new LinearLayoutManager(this));

        ivLottieNoData = findViewById(R.id.ivLottieNoData);
        tvPoints = findViewById(R.id.tvPoints);
        tvPoints.setText(SharePreference.getInstance().getEarningPointString());
        ImageView imBack = findViewById(R.id.ivBack);
        imBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        new GetEarnedPointHistoryAsync(EarnedPointHistoryActivity.this, type, String.valueOf(pageNo));

        nestedScrollView = findViewById(R.id.nestedScrollView);
        nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()) {
                    if (pageNo < numOfPage) {
                        new GetEarnedPointHistoryAsync(EarnedPointHistoryActivity.this, type, String.valueOf(pageNo + 1));
                    }
                }
            }
        });
    }
    
    @Override
    public void onBackPressed() {
        try {
            if (responseModel != null && responseModel.getAppLuck() != null ) {
                
                CommonMethodsUtils.dialogShowAppLuck(EarnedPointHistoryActivity.this, responseModel.getAppLuck());
            } else {
                super.onBackPressed();
            }
        } catch (Exception e) {
            e.printStackTrace();
            super.onBackPressed();
        }
    }

    private EarnedPointHistoryModel responseModel;

    public void setData(EarnedPointHistoryModel responseModel1) {
        responseModel = responseModel1;
        if ((responseModel.getWalletList() != null && responseModel.getWalletList().size() > 0) || (responseModel.getData() != null && responseModel.getData().size() > 0)) {
            int prevItemCount = listPointHistory.size();
            if (!isAdLoaded && responseModel.getIsShowInterstitial() != null && responseModel.getIsShowInterstitial().equals(Constants.APPLOVIN_INTERSTITIAL)) {
                AdsUtil.showAppLovinInterstitialAd(EarnedPointHistoryActivity.this, null);
            } else if (!isAdLoaded && responseModel.getIsShowInterstitial() != null && responseModel.getIsShowInterstitial().equals(Constants.APPLOVIN_REWARD)) {
                AdsUtil.showAppLovinRewardedAd(EarnedPointHistoryActivity.this, null);
            }
            if (type.equals(Constants.HistoryType.WITHDRAW_HISTORY) && responseModel.getData() != null && responseModel.getData().size() > 0) {
                listPointHistory.addAll(responseModel.getData());
                if (prevItemCount == 0) {
                    rvHistoryList.getAdapter().notifyDataSetChanged();
                } else {
                    rvHistoryList.getAdapter().notifyItemRangeInserted(prevItemCount, responseModel.getData().size());
                }
            } else {
                listPointHistory.addAll(responseModel.getWalletList());
                if (prevItemCount == 0) {
                    rvHistoryList.getAdapter().notifyDataSetChanged();
                } else {
                    rvHistoryList.getAdapter().notifyItemRangeInserted(prevItemCount, responseModel.getWalletList().size());
                }
            }
            numOfPage = responseModel.getTotalPage();
            pageNo = Integer.parseInt(responseModel.getCurrentPage());
            if (!isAdLoaded) {
                // Show Default AppLuck
                if (!CommonMethodsUtils.isStringNullOrEmpty(responseMain.getIsShowAppluck()) && responseMain.getIsShowAppluck().equals("1") && !CommonMethodsUtils.isStringNullOrEmpty(responseModel.getIsDefaultAppluck()) && responseModel.getIsDefaultAppluck().equals("1")) {
                    RelativeLayout relMainFlotAppLuck = findViewById(R.id.relMainFlotAppLuck);
                    LinearLayout layoutDefaultAd = findViewById(R.id.layoutDefaultAd);
                    CommonMethodsUtils.showDefaultAppLuck(EarnedPointHistoryActivity.this, layoutDefaultAd, relMainFlotAppLuck, responseMain.getDefaultAppluckID());
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
                        CommonMethodsUtils.loadTopBannerAd(EarnedPointHistoryActivity.this, layoutTopAds, responseModel.getTopAds());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (responseModel.getMiniAds() != null && !CommonMethodsUtils.isStringNullOrEmpty(responseModel.getMiniAds().getImage()) && (SharePreference.getInstance().getString(SharePreference.pointHistoryMiniAdShownDate + responseModel.getMiniAds().getId()).length() == 0 || !SharePreference.getInstance().getString(SharePreference.pointHistoryMiniAdShownDate + responseModel.getMiniAds().getId()).equals(CommonMethodsUtils.getCurrentDate()))) {
                    try {
                        RelativeLayout layoutMiniAd = findViewById(R.id.layoutMiniAd);
                        ProgressBar progressBar = findViewById(R.id.progressBar);
                        if (responseModel.getMiniAds() != null) {
                            if (responseModel.getMiniAds().getImage().endsWith(".json")) {
                                LottieAnimationView ivLottieViewMiniAd = findViewById(R.id.ivLottieViewMiniAd);
                                ivLottieViewMiniAd.setVisibility(android.view.View.VISIBLE);
                                CommonMethodsUtils.setLottieAnimation(ivLottieViewMiniAd, responseModel.getMiniAds().getImage());
                                ivLottieViewMiniAd.setRepeatCount(LottieDrawable.INFINITE);
                                ivLottieViewMiniAd.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        CommonMethodsUtils.Redirect(EarnedPointHistoryActivity.this, responseModel.getMiniAds().getScreenNo(), responseModel.getMiniAds().getTitle(), responseModel.getMiniAds().getUrl(), responseModel.getMiniAds().getId(), responseModel.getMiniAds().getTaskId(), responseModel.getMiniAds().getImage());
                                    }
                                });
                                progressBar.setVisibility(View.GONE);
                            } else {
                                ImageView ivMiniAd = findViewById(R.id.ivMiniAd);
                                ivMiniAd.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        CommonMethodsUtils.Redirect(EarnedPointHistoryActivity.this, responseModel.getMiniAds().getScreenNo(), responseModel.getMiniAds().getTitle(), responseModel.getMiniAds().getUrl(), responseModel.getMiniAds().getId(), responseModel.getMiniAds().getTaskId(), responseModel.getMiniAds().getImage());
                                    }
                                });
                                Glide.with(this)
                                        .load(responseModel.getMiniAds().getImage())
                                        .override(getResources().getDimensionPixelSize(com.intuit.sdp.R.dimen._140sdp), getResources().getDimensionPixelSize(com.intuit.sdp.R.dimen._200sdp))
                                        .apply(new RequestOptions().transforms(new CenterCrop(), new RoundedCorners(getResources().getDimensionPixelSize(R.dimen.dim_5))))
                                        .addListener(new RequestListener<Drawable>() {
                                            @Override
                                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                                return false;
                                            }

                                            @Override
                                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                                progressBar.setVisibility(View.GONE);
                                                ivMiniAd.setVisibility(View.VISIBLE);
                                                return false;
                                            }
                                        }).into(ivMiniAd);
                            }
                            ImageView ivCloseMiniAd = findViewById(R.id.ivCloseMiniAd);
                            ivCloseMiniAd.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    layoutMiniAd.setVisibility(View.GONE);
                                }
                            });
                            layoutMiniAd.setVisibility(View.VISIBLE);
                            SharePreference.getInstance().putString(SharePreference.pointHistoryMiniAdShownDate + responseModel.getMiniAds().getId(), CommonMethodsUtils.getCurrentDate());
                        } else {
                            layoutMiniAd.setVisibility(View.GONE);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            isAdLoaded = true;
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
        rvHistoryList.setVisibility(listPointHistory.isEmpty() ? View.GONE : View.VISIBLE);
        ivLottieNoData.setVisibility(listPointHistory.isEmpty() ? View.VISIBLE : View.GONE);
        if (listPointHistory.isEmpty())
            ivLottieNoData.playAnimation();
        // Load Bottom banner ad
        try {
            if (!listPointHistory.isEmpty() && listPointHistory.size() < 5) {
                LinearLayout layoutBannerAdBottom = findViewById(R.id.layoutBannerAdBottom);
                layoutBannerAdBottom.setVisibility(View.VISIBLE);
                TextView lblAdSpaceBottom = findViewById(R.id.lblAdSpaceBottom);
                CommonMethodsUtils.loadBannerAds(EarnedPointHistoryActivity.this, layoutBannerAdBottom, lblAdSpaceBottom);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadAppLovinNativeAds() {
        try {
            nativeAdLoader = new MaxNativeAdLoader(CommonMethodsUtils.getRandomAdUnitId(responseMain.getLovinNativeID()), EarnedPointHistoryActivity.this);
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
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}