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
import androidx.core.widget.NestedScrollView;
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
import com.mwin.reward.adapter.TaskListAdapter;
import com.mwin.reward.async.GetTasksListAsync;
import com.mwin.reward.async.models.HomeSliderItem;
import com.mwin.reward.async.models.MainResponseModel;
import com.mwin.reward.async.models.TaskListDataItem;
import com.mwin.reward.async.models.TaskListResponseModel;
import com.mwin.reward.customviews.recyclerview_pagers.PagerAdapterSmall;
import com.mwin.reward.customviews.recyclerview_pagers.RecyclerViewPagerSmall;
import com.mwin.reward.utils.CommonMethodsUtils;
import com.mwin.reward.utils.SharePreference;
import com.mwin.reward.value.Constants;

import java.util.ArrayList;
import java.util.List;

public class TaskListActivity extends AppCompatActivity {
    private final List<TaskListDataItem> listTasks = new ArrayList<>();
    private int pageNo = 1;
    private NestedScrollView nestedScrollView;
    private long numOfPage;
    private TextView lblLoadingAds, tvPoints, tvAllTasks, tvHighestPayingTask;
    private LottieAnimationView ivLottieNoData;
    private RecyclerView rvTaskList;
    private RelativeLayout layoutSlider;
    private RecyclerViewPagerSmall rvSlider;
    private ImageView ivBack, ivHistory;
    private MaxAd nativeAd;
    private MaxNativeAdLoader nativeAdLoader;
    private FrameLayout frameLayoutNativeAd;
    private LinearLayout layoutAds, layoutPoints;
    private MainResponseModel responseMain;
    private boolean isAdLoaded;
    private String selectedTaskType = Constants.TASK_TYPE_ALL;
    private TaskListAdapter taskAdapter;
    private View viewAll, viewHighestPaying, viewHighestPayingTaskDot, viewAllDot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        CommonMethodsUtils.setDayNightTheme(TaskListActivity.this);
        setContentView(R.layout.activity_task_list);
        responseMain = new Gson().fromJson(SharePreference.getInstance().getString(SharePreference.HomeData), MainResponseModel.class);
        setViews();
        new GetTasksListAsync(TaskListActivity.this, selectedTaskType, String.valueOf(pageNo));
    }

    private void setViews() {
        viewAll = findViewById(R.id.viewAll);
        viewHighestPaying = findViewById(R.id.viewHighestPaying);
        viewHighestPayingTaskDot = findViewById(R.id.viewHighestPayingTaskDot);
        viewAllDot = findViewById(R.id.viewAllDot);

        layoutAds = findViewById(R.id.layoutAds);
        rvSlider = findViewById(R.id.rvSlider);
        layoutSlider = findViewById(R.id.layoutSlider);

        rvTaskList = findViewById(R.id.rvTaskList);
        rvTaskList.setLayoutManager(new LinearLayoutManager(TaskListActivity.this));
        taskAdapter = new TaskListAdapter(listTasks, TaskListActivity.this, new TaskListAdapter.ClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                if (listTasks.get(position).getIsShowDetails() != null && listTasks.get(position).getIsShowDetails().equals("1")) {
                    Intent intent = new Intent(TaskListActivity.this, TaskInfoActivity.class);
                    intent.putExtra("taskId", listTasks.get(position).getId());
                    startActivity(intent);
                } else {
                    CommonMethodsUtils.Redirect(TaskListActivity.this, listTasks.get(position).getScreenNo(), listTasks.get(position).getTitle()
                            , listTasks.get(position).getUrl(), null, listTasks.get(position).getId(), listTasks.get(position).getIcon());
                }
            }
        });
        rvTaskList.setAdapter(taskAdapter);

        ivLottieNoData = findViewById(R.id.ivLottieNoData);
        nestedScrollView = findViewById(R.id.nestedScrollView);
        nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()) {
                    if (pageNo < numOfPage) {
                        new GetTasksListAsync(TaskListActivity.this, selectedTaskType, String.valueOf(pageNo + 1));
                    }
                }
            }
        });

        tvAllTasks = findViewById(R.id.tvAllTasks);
        tvAllTasks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedTaskType = Constants.TASK_TYPE_ALL;
                viewHighestPaying.setVisibility(View.GONE);
                viewAll.setVisibility(View.VISIBLE);
                viewAllDot.setVisibility(View.VISIBLE);
                viewHighestPayingTaskDot.setVisibility(View.INVISIBLE);
                tvAllTasks.setTextColor(getColor(R.color.white));
                tvHighestPayingTask.setTextColor(getColor(R.color.grey_button_pressed));
                callApi();
            }
        });
        tvHighestPayingTask = findViewById(R.id.tvHighestPayingTask);
        tvHighestPayingTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedTaskType = Constants.TASK_TYPE_HIGHEST_PAYING;
                tvHighestPayingTask.setTextColor(getColor(R.color.white));
                viewHighestPaying.setVisibility(View.VISIBLE);
                viewAll.setVisibility(View.GONE);
                viewAllDot.setVisibility(View.INVISIBLE);
                viewHighestPayingTaskDot.setVisibility(View.VISIBLE);
                tvAllTasks.setTextColor(getColor(R.color.grey_button_pressed));
                callApi();
            }
        });

        layoutPoints = findViewById(R.id.layoutPoints);
        layoutPoints.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SharePreference.getInstance().getBoolean(SharePreference.IS_LOGIN)) {
                    startActivity(new Intent(TaskListActivity.this, EarnedWalletBalanceActivity.class));
                } else {
                    CommonMethodsUtils.NotifyLogin(TaskListActivity.this);
                }
            }
        });

        tvPoints = findViewById(R.id.tvPoints);

        ivBack = findViewById(R.id.ivBack);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        ivHistory = findViewById(R.id.ivHistory);
        CommonMethodsUtils.startRoundAnimation(TaskListActivity.this, ivHistory);
        ivHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SharePreference.getInstance().getBoolean(SharePreference.IS_LOGIN)) {
                    startActivity(new Intent(TaskListActivity.this, EarnedPointHistoryActivity.class)
                            .putExtra("type", Constants.HistoryType.TASK)
                            .putExtra("title", "Task History"));
                } else {
                    CommonMethodsUtils.NotifyLogin(TaskListActivity.this);
                }
            }
        });

    }

    private void callApi() {
        pageNo = 1;
        numOfPage = 0;
        rvTaskList.setVisibility(View.INVISIBLE);
        listTasks.clear();
        taskAdapter.notifyDataSetChanged();
        layoutAds.setVisibility(View.GONE);
        ivLottieNoData.setVisibility(View.GONE);
        nestedScrollView.scrollTo(0, 0);
        new GetTasksListAsync(TaskListActivity.this, selectedTaskType, String.valueOf(pageNo));
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            tvPoints.setText(SharePreference.getInstance().getEarningPointString());
        } catch (Exception e) {
            //e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        try {
            if (responseModel != null && responseModel.getAppLuck() != null) {

                CommonMethodsUtils.dialogShowAppLuck(TaskListActivity.this, responseModel.getAppLuck());
            } else {
                super.onBackPressed();
            }
        } catch (Exception e) {
            e.printStackTrace();
            super.onBackPressed();
        }
    }

    private TaskListResponseModel responseModel;

    public void setData(TaskListResponseModel responseModel1) {
        responseModel = responseModel1;
        try {
            if (responseModel != null && responseModel.getTaskOffers() != null && responseModel.getTaskOffers().size() > 0) {
                int prevItemCount = listTasks.size();
                listTasks.addAll(responseModel.getTaskOffers());
                //AppLogger.getInstance().e("PREV ITEM = ", "===>" + prevItemCount);
                if (prevItemCount == 0) {
                    taskAdapter.notifyDataSetChanged();
                } else {
                    taskAdapter.notifyItemRangeInserted(prevItemCount, responseModel.getTaskOffers().size());
                }
                numOfPage = responseModel.getTotalPage();
                pageNo = Integer.parseInt(responseModel.getCurrentPage());

                if (!isAdLoaded) {
                    // Show Default AppLuck
                    if (!CommonMethodsUtils.isStringNullOrEmpty(responseMain.getIsShowAppluck()) && responseMain.getIsShowAppluck().equals("1") && !CommonMethodsUtils.isStringNullOrEmpty(responseModel.getIsDefaultAppluck()) && responseModel.getIsDefaultAppluck().equals("1")) {
                        RelativeLayout relMainFlotAppLuck = findViewById(R.id.relMainFlotAppLuck);
                        LinearLayout layoutDefaultAd = findViewById(R.id.layoutDefaultAd);
                        CommonMethodsUtils.showDefaultAppLuck(TaskListActivity.this, layoutDefaultAd, relMainFlotAppLuck, responseMain.getDefaultAppluckID());
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
                            //AppLogger.getInstance().e("TOP ADS", "===" + responseModel.getTopAds().getImage());
                            CommonMethodsUtils.loadTopBannerAd(TaskListActivity.this, layoutTopAds, responseModel.getTopAds());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                isAdLoaded = true;
            }
            if (listTasks.isEmpty()) {
                layoutAds.setVisibility(View.VISIBLE);
                frameLayoutNativeAd = findViewById(R.id.fl_adplaceholder);
                lblLoadingAds = findViewById(R.id.lblLoadingAds);
                if (CommonMethodsUtils.isShowAppLovinNativeAds()) {
                    loadAppLovinNativeAds();
                } else {
                    layoutAds.setVisibility(View.GONE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (rvSlider.getListSize() == 0) {
                if (responseModel.getHomeSlider() != null && responseModel.getHomeSlider().size() > 0) {
                    layoutSlider.setVisibility(View.VISIBLE);
                    rvSlider.setClear();
                    rvSlider.addAll((ArrayList<HomeSliderItem>) responseModel.getHomeSlider());
                    rvSlider.start();
                    rvSlider.setOnItemClickListener(new PagerAdapterSmall.OnItemClickListener() {
                        @Override
                        public void onItemClick(int position) {
                            CommonMethodsUtils.Redirect(TaskListActivity.this, responseModel.getHomeSlider().get(position).getScreenNo(), responseModel.getHomeSlider().get(position).getTitle()
                                    , responseModel.getHomeSlider().get(position).getUrl(), responseModel.getHomeSlider().get(position).getId(), null, responseModel.getHomeSlider().get(position).getImage());
                        }
                    });
                } else {
                    layoutSlider.setVisibility(View.GONE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        rvTaskList.setVisibility(listTasks.isEmpty() ? View.GONE : View.VISIBLE);
        ivLottieNoData.setVisibility(listTasks.isEmpty() ? View.VISIBLE : View.GONE);
        if (listTasks.isEmpty())
            ivLottieNoData.playAnimation();
    }


    private void loadAppLovinNativeAds() {
        try {
            nativeAdLoader = new MaxNativeAdLoader(CommonMethodsUtils.getRandomAdUnitId(responseMain.getLovinNativeID()), TaskListActivity.this);
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
            removeAds();
        }
    }

    public void removeAds() {
//        //AppLogger.getInstance().e("removeAds", "removeAds");
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