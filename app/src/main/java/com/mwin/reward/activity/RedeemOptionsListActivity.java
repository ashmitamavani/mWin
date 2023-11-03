/*
 * Copyright (c) 2021.  Hurricane Development Studios
 */

package com.mwin.reward.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
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
import com.mwin.reward.adapter.RedeemOptionsAdapter;
import com.mwin.reward.async.GetRedeemOptionsAsync;
import com.mwin.reward.async.models.HomeSliderItem;
import com.mwin.reward.async.models.MainResponseModel;
import com.mwin.reward.async.models.RedeemOptionsResponseModel;
import com.mwin.reward.async.models.WithdrawType;
import com.mwin.reward.customviews.recyclerview_pagers.PagerAdapter;
import com.mwin.reward.customviews.recyclerview_pagers.RecyclerViewPager;
import com.mwin.reward.utils.CommonMethodsUtils;
import com.mwin.reward.utils.SharePreference;
import com.mwin.reward.value.Constants;

import java.util.ArrayList;
import java.util.List;

public class RedeemOptionsListActivity extends AppCompatActivity {
    private RecyclerView rvList;
    private List<WithdrawType> listWithdrawTypes = new ArrayList<>();
    private TextView tvPoints, lblLoadingAds;
    private LottieAnimationView ivLottieNoData;
    private LinearLayout layoutPoints;
    private ImageView ivHistory;
    private RelativeLayout layoutSlider;
    private RecyclerViewPager rvSlider;
    private MainResponseModel responseMain;
    private MaxAd nativeAd;
    private MaxNativeAdLoader nativeAdLoader;
    private FrameLayout frameLayoutNativeAd;
    private LinearLayout layoutAds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        CommonMethodsUtils.setDayNightTheme(RedeemOptionsListActivity.this);
        setContentView(R.layout.activity_redeem_options_list);
        responseMain = new Gson().fromJson(SharePreference.getInstance().getString(SharePreference.HomeData), MainResponseModel.class);

        layoutPoints = findViewById(R.id.layoutPoints);
        layoutPoints.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SharePreference.getInstance().getBoolean(SharePreference.IS_LOGIN)) {
                    startActivity(new Intent(RedeemOptionsListActivity.this, EarnedWalletBalanceActivity.class));
                } else {
                    CommonMethodsUtils.NotifyLogin(RedeemOptionsListActivity.this);
                }
            }
        });

        ivHistory = findViewById(R.id.ivHistory);
        CommonMethodsUtils.startRoundAnimation(RedeemOptionsListActivity.this, ivHistory);
        ivHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SharePreference.getInstance().getBoolean(SharePreference.IS_LOGIN)) {
                    Intent intent = new Intent(RedeemOptionsListActivity.this, EarnedPointHistoryActivity.class);
                    intent.putExtra("type", Constants.HistoryType.WITHDRAW_HISTORY);
                    intent.putExtra("title", "Withdrawal History");
                    startActivity(intent);
                } else {
                    CommonMethodsUtils.NotifyLogin(RedeemOptionsListActivity.this);
                }
            }
        });

        tvPoints = findViewById(R.id.tvPoints);
        tvPoints.setText(SharePreference.getInstance().getEarningPointString());

        rvList = findViewById(R.id.rvList);
        ivLottieNoData = findViewById(R.id.ivLottieNoData);

        rvSlider = findViewById(R.id.rvSlider);
        layoutSlider = findViewById(R.id.layoutSlider);

        ImageView imBack = findViewById(R.id.ivBack);
        imBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        new GetRedeemOptionsAsync(RedeemOptionsListActivity.this);
    }

    
    @Override
    public void onBackPressed() {
        try {
            if (responseModel != null && responseModel.getAppLuck() != null ) {
                
                CommonMethodsUtils.dialogShowAppLuck(RedeemOptionsListActivity.this, responseModel.getAppLuck());
            } else {
                super.onBackPressed();
            }
        } catch (Exception e) {
            e.printStackTrace();
            super.onBackPressed();
        }
    }

    private RedeemOptionsResponseModel responseModel;

    public void setData(RedeemOptionsResponseModel responseModel1) {
        responseModel = responseModel1;
        if (responseModel.getType() != null && responseModel.getType().size() > 0) {
            listWithdrawTypes.addAll(responseModel.getType());
            if (CommonMethodsUtils.isShowAppLovinNativeAds()) {
                if (listWithdrawTypes.size() <= 4) {
                    listWithdrawTypes.add(listWithdrawTypes.size(), new WithdrawType());
                } else {
                    for (int i2 = 0; i2 < this.listWithdrawTypes.size(); i2++) {
                        if ((i2 + 1) % 5 == 0) {
                            listWithdrawTypes.add(i2, new WithdrawType());
                            break; // add only 1 ad view
                        }
                    }
                }
            }
            RedeemOptionsAdapter adapter = new RedeemOptionsAdapter(listWithdrawTypes, RedeemOptionsListActivity.this, new RedeemOptionsAdapter.ClickListener() {
                @Override
                public void onItemClick(int position, View v) {
                    if (listWithdrawTypes.get(position).getIsActive() != null && listWithdrawTypes.get(position).getIsActive().equals("1")) {
                        startActivity(new Intent(RedeemOptionsListActivity.this, RedeemOptionsSubListActivity.class)
                                .putExtra("type", listWithdrawTypes.get(position).getType())
                                .putExtra("title", listWithdrawTypes.get(position).getTitle()));
                    }
                }
            });

            GridLayoutManager mGridLayoutManager = new GridLayoutManager(RedeemOptionsListActivity.this, 2);
            mGridLayoutManager.setOrientation(RecyclerView.VERTICAL);
            mGridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    if (adapter.getItemViewType(position) == RedeemOptionsAdapter.ITEM_AD) {
                        return 2;
                    }
                    return 1;
                }
            });
            rvList.setLayoutManager(mGridLayoutManager);
            rvList.setAdapter(adapter);
        }
        try {
            if (responseModel.getHomeSlider() != null && responseModel.getHomeSlider().size() > 0) {
                layoutSlider.setVisibility(View.VISIBLE);
                rvSlider.setClear();
                rvSlider.addAll((ArrayList<HomeSliderItem>) responseModel.getHomeSlider());
                rvSlider.start();
                rvSlider.setOnItemClickListener(new PagerAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        CommonMethodsUtils.Redirect(RedeemOptionsListActivity.this, responseModel.getHomeSlider().get(position).getScreenNo(), responseModel.getHomeSlider().get(position).getTitle()
                                , responseModel.getHomeSlider().get(position).getUrl(), responseModel.getHomeSlider().get(position).getId(), null, responseModel.getHomeSlider().get(position).getImage());
                    }
                });
            } else {
                layoutSlider.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        layoutAds = findViewById(R.id.layoutAds);
        layoutAds.setVisibility(View.VISIBLE);
        frameLayoutNativeAd = findViewById(R.id.fl_adplaceholder);
        lblLoadingAds = findViewById(R.id.lblLoadingAds);
        if (listWithdrawTypes.isEmpty() && CommonMethodsUtils.isShowAppLovinNativeAds()) {
            loadAppLovinNativeAds();
        } else {
            layoutAds.setVisibility(View.GONE);
        }

        rvList.setVisibility(listWithdrawTypes.isEmpty() ? View.GONE : View.VISIBLE);
        ivLottieNoData.setVisibility(listWithdrawTypes.isEmpty() ? View.VISIBLE : View.GONE);
        if (listWithdrawTypes.isEmpty())
            ivLottieNoData.playAnimation();

        // Show Default AppLuck
        if (!CommonMethodsUtils.isStringNullOrEmpty(responseMain.getIsShowAppluck()) && responseMain.getIsShowAppluck().equals("1") && !CommonMethodsUtils.isStringNullOrEmpty(responseModel.getIsDefaultAppluck()) && responseModel.getIsDefaultAppluck().equals("1")) {
            RelativeLayout relMainFlotAppLuck = findViewById(R.id.relMainFlotAppLuck);
            LinearLayout layoutDefaultAd = findViewById(R.id.layoutDefaultAd);
            CommonMethodsUtils.showDefaultAppLuck(RedeemOptionsListActivity.this, layoutDefaultAd, relMainFlotAppLuck, responseMain.getDefaultAppluckID());
        }
    }

    private void loadAppLovinNativeAds() {
        try {
            nativeAdLoader = new MaxNativeAdLoader(CommonMethodsUtils.getRandomAdUnitId(responseMain.getLovinNativeID()), RedeemOptionsListActivity.this);
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