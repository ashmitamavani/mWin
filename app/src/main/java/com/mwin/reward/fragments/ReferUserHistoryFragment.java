/*
 * Copyright (c) 2021.  Hurricane Development Studios
 */

package com.mwin.reward.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
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
import com.mwin.reward.adapter.ReferredUsersListHistoryAdapter;
import com.mwin.reward.async.GetEarnedPointHistoryAsync;
import com.mwin.reward.async.models.EarnedPointHistoryModel;
import com.mwin.reward.async.models.MainResponseModel;
import com.mwin.reward.async.models.WalletListItem;
import com.mwin.reward.utils.CommonMethodsUtils;
import com.mwin.reward.utils.SharePreference;
import com.mwin.reward.value.Constants;

import java.util.ArrayList;
import java.util.List;

public class ReferUserHistoryFragment extends Fragment {
    private final List<WalletListItem> listPointHistory = new ArrayList<>();
    private View view;
    private int pageNo = 1;
    private NestedScrollView nestedScrollView;
    private long numOfPage;
    private TextView lblLoadingAds;
    private LottieAnimationView ivLottieNoData;
    private RecyclerView rvHistoryList;
    private MaxAd nativeAd;
    private MaxNativeAdLoader nativeAdLoader;
    private FrameLayout frameLayoutNativeAd;
    private LinearLayout layoutAds;
    private MainResponseModel responseMain;
    private boolean isAdLoaded = false;

    public ReferUserHistoryFragment() {
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_points_history, container, false);
        }
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        responseMain = new Gson().fromJson(SharePreference.getInstance().getString(SharePreference.HomeData), MainResponseModel.class);

        rvHistoryList = view.findViewById(R.id.rvHistoryList);
        rvHistoryList.setAdapter(new ReferredUsersListHistoryAdapter(listPointHistory, getActivity()));
        rvHistoryList.setLayoutManager(new LinearLayoutManager(getActivity()));

        ivLottieNoData = view.findViewById(R.id.ivLottieNoData);
        nestedScrollView = view.findViewById(R.id.nestedScrollView);
        nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()) {
                    if (pageNo < numOfPage) {
                        new GetEarnedPointHistoryAsync(getActivity(), Constants.HistoryType.REFER_USERS, String.valueOf(pageNo + 1));
                    }
                }
            }
        });
        new GetEarnedPointHistoryAsync(getActivity(), Constants.HistoryType.REFER_USERS, String.valueOf(pageNo));
    }

    public void setData(EarnedPointHistoryModel responseModel) {
        if (responseModel != null && responseModel.getData() != null && responseModel.getData().size() > 0) {
            int prevItemCount = listPointHistory.size();
            listPointHistory.addAll(responseModel.getData());
            if (prevItemCount == 0) {
                rvHistoryList.getAdapter().notifyDataSetChanged();
            } else {
                rvHistoryList.getAdapter().notifyItemRangeInserted(prevItemCount, responseModel.getData().size());
            }

            numOfPage = responseModel.getTotalPage();
            pageNo = Integer.parseInt(responseModel.getCurrentPage());
            if (!isAdLoaded) {
                // Load home note webview top
                try {
                    if (!CommonMethodsUtils.isStringNullOrEmpty(responseModel.getHomeNote())) {
                        WebView webNote = view.findViewById(R.id.webNote);
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
                        LinearLayout layoutTopAds = view.findViewById(R.id.layoutTopAds);
                        CommonMethodsUtils.loadTopBannerAd(getActivity(), layoutTopAds, responseModel.getTopAds());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            isAdLoaded = true;
        }
        if (listPointHistory.isEmpty()) {
            layoutAds = view.findViewById(R.id.layoutAds);
            layoutAds.setVisibility(View.VISIBLE);
            frameLayoutNativeAd = view.findViewById(R.id.fl_adplaceholder);
            lblLoadingAds = view.findViewById(R.id.lblLoadingAds);
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
                LinearLayout layoutBannerAdBottom = view.findViewById(R.id.layoutBannerAdBottom);
                layoutBannerAdBottom.setVisibility(View.VISIBLE);
                TextView lblAdSpaceBottom = view.findViewById(R.id.lblAdSpaceBottom);
                CommonMethodsUtils.loadBannerAds(getActivity(), layoutBannerAdBottom, lblAdSpaceBottom);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void loadAppLovinNativeAds() {
        try {
            nativeAdLoader = new MaxNativeAdLoader(CommonMethodsUtils.getRandomAdUnitId(responseMain.getLovinNativeID()), getActivity());
            nativeAdLoader.setNativeAdListener(new MaxNativeAdListener() {
                @Override
                public void onNativeAdLoaded(final MaxNativeAdView nativeAdView, final MaxAd ad) {
                    frameLayoutNativeAd = view.findViewById(R.id.fl_adplaceholder);
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
    public void onDestroyView() {
        super.onDestroyView();
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
