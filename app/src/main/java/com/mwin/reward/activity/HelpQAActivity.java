/*
 * Copyright (c) 2021.  Hurricane Development Studios
 */

package com.mwin.reward.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
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
import com.mwin.reward.adapter.HelpQAAdapter;
import com.mwin.reward.adapter.HelpQAChildView;
import com.mwin.reward.adapter.HelpQAParentView;
import com.mwin.reward.async.GetHelpQADataAsync;
import com.mwin.reward.async.models.HelpQAListItem;
import com.mwin.reward.async.models.HelpQAModel;
import com.mwin.reward.async.models.MainResponseModel;
import com.mwin.reward.utils.AdsUtil;
import com.mwin.reward.utils.CommonMethodsUtils;
import com.mwin.reward.utils.SharePreference;

import java.util.ArrayList;
import java.util.List;

public class HelpQAActivity extends AppCompatActivity {
    private RecyclerView rvFAQs;
    private TextView lblLoadingAds;
    private LottieAnimationView ivLottieNoData;
    private MainResponseModel responseMain;
    private MaxAd nativeAd;
    private MaxNativeAdLoader nativeAdLoader;
    private FrameLayout frameLayoutNativeAd;
    private LinearLayout layoutAds;
    private ImageView ivFeedback;
    private HelpQAAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        CommonMethodsUtils.setDayNightTheme(HelpQAActivity.this);
        setContentView(R.layout.activity_help_qa);
        responseMain = new Gson().fromJson(SharePreference.getInstance().getString(SharePreference.HomeData), MainResponseModel.class);

        ivLottieNoData = findViewById(R.id.ivLottieNoData);
        rvFAQs = findViewById(R.id.rvFAQs);

        ivFeedback = findViewById(R.id.ivFeedback);
        ivFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HelpQAActivity.this, ContactUsActivity.class).putExtra("title", "Give Feedback"));
            }
        });

        ImageView imBack = findViewById(R.id.ivBack);
        imBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        new GetHelpQADataAsync(HelpQAActivity.this);
    }
    
    @Override
    public void onBackPressed() {
        try {
            if (responseModel != null && responseModel.getAppLuck() != null ) {
                
                CommonMethodsUtils.dialogShowAppLuck(HelpQAActivity.this, responseModel.getAppLuck());
            } else {
                super.onBackPressed();
            }
        } catch (Exception e) {
            e.printStackTrace();
            super.onBackPressed();
        }
    }

    private HelpQAModel responseModel;

    public void setData(HelpQAModel responseModel1) {
        responseModel = responseModel1;
        if (responseModel.getFAQList() != null && responseModel.getFAQList().size() > 0) {
            AdsUtil.showAppLovinInterstitialAd(HelpQAActivity.this, null);

            List<HelpQAParentView> listFAQsData = new ArrayList<>();
            for (HelpQAListItem objFAQ : responseModel.getFAQList()) {
                List<HelpQAChildView> answers = new ArrayList<>();
                answers.add(new HelpQAChildView(objFAQ.getAnswer()));
                HelpQAParentView obj = new HelpQAParentView(objFAQ.getQuestion(), answers);
                listFAQsData.add(obj);
            }
            mAdapter = new HelpQAAdapter(this, listFAQsData);
            rvFAQs.setAdapter(mAdapter);
            rvFAQs.setLayoutManager(new LinearLayoutManager(this));

            // Show Default AppLuck
            if (!CommonMethodsUtils.isStringNullOrEmpty(responseMain.getIsShowAppluck()) && responseMain.getIsShowAppluck().equals("1") && !CommonMethodsUtils.isStringNullOrEmpty(responseModel.getIsDefaultAppluck()) && responseModel.getIsDefaultAppluck().equals("1")) {
                RelativeLayout relMainFlotAppLuck = findViewById(R.id.relMainFlotAppLuck);
                LinearLayout layoutDefaultAd = findViewById(R.id.layoutDefaultAd);
                CommonMethodsUtils.showDefaultAppLuck(HelpQAActivity.this, layoutDefaultAd, relMainFlotAppLuck, responseMain.getDefaultAppluckID());
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
                    CommonMethodsUtils.loadTopBannerAd(HelpQAActivity.this, layoutTopAds, responseModel.getTopAds());
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
        rvFAQs.setVisibility(responseModel.getFAQList() != null && responseModel.getFAQList().size() > 0 ? View.VISIBLE : View.GONE);
        ivLottieNoData.setVisibility(responseModel.getFAQList() != null && responseModel.getFAQList().size() > 0 ? View.GONE : View.VISIBLE);
        if (responseModel.getFAQList() == null && responseModel.getFAQList().size() == 0)
            ivLottieNoData.playAnimation();
    }

    private void loadAppLovinNativeAds() {
        try {
            nativeAdLoader = new MaxNativeAdLoader(CommonMethodsUtils.getRandomAdUnitId(responseMain.getLovinNativeID()), HelpQAActivity.this);
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