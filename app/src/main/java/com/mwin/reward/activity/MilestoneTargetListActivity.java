/*
 * Copyright (c) 2021.  Hurricane Development Studios
 */

package com.mwin.reward.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.mwin.reward.R;
import com.mwin.reward.adapter.MilestonesTargetTabAdapter;
import com.mwin.reward.async.models.EarnedPointHistoryModel;
import com.mwin.reward.async.models.MainResponseModel;
import com.mwin.reward.async.models.MilestonesTargetResponseModel;
import com.mwin.reward.utils.CommonMethodsUtils;
import com.mwin.reward.utils.SharePreference;

import java.util.ArrayList;

public class MilestoneTargetListActivity extends AppCompatActivity {
    private TextView tvPoints;
    private TabLayout tabLayout;
    private ArrayList<String> mFragmentItems;
    private ViewPager viewpager;
    private MilestonesTargetTabAdapter customTabAdapter;
    private ImageView ivHelp;
    private String helpUrl = "";
    private LinearLayout layoutPoints;
    private RelativeLayout layoutMain;
    private boolean isAppluckLoaded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        CommonMethodsUtils.setDayNightTheme(MilestoneTargetListActivity.this);
        setContentView(R.layout.activity_milestone_target_list);

        layoutMain = findViewById(R.id.layoutMain);
        layoutPoints = findViewById(R.id.layoutPoints);
        layoutPoints.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SharePreference.getInstance().getBoolean(SharePreference.IS_LOGIN)) {
                    startActivity(new Intent(MilestoneTargetListActivity.this, EarnedWalletBalanceActivity.class));
                } else {
                    CommonMethodsUtils.NotifyLogin(MilestoneTargetListActivity.this);
                }
            }
        });

        tvPoints = findViewById(R.id.tvPoints);

        ImageView imBack = findViewById(R.id.ivBack);
        imBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        ivHelp = findViewById(R.id.ivHelp);
        ivHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!CommonMethodsUtils.isStringNullOrEmpty(helpUrl)) {
                    CommonMethodsUtils.openUrl(MilestoneTargetListActivity.this, helpUrl);
                }
            }
        });


        viewpager = findViewById(R.id.viewpager);
        tabLayout = findViewById(R.id.tabLayout);

        setupViewPager(viewpager);
        tabLayout.setupWithViewPager(viewpager);
        viewpager.setCurrentItem(0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        tvPoints.setText(SharePreference.getInstance().getEarningPointString());
    }

    private void setupViewPager(ViewPager viewPager) {
        mFragmentItems = new ArrayList<>();
        mFragmentItems.add("Active");
        mFragmentItems.add("Completed");

        customTabAdapter = new MilestonesTargetTabAdapter(getSupportFragmentManager(), mFragmentItems);
        viewPager.setAdapter(customTabAdapter);
        viewPager.setOffscreenPageLimit(1);
        customTabAdapter.notifyDataSetChanged();
    }
    
    @Override
    public void onBackPressed() {
        try {
            if (responseModel != null && responseModel.getAppLuck() != null ) {
                
                CommonMethodsUtils.dialogShowAppLuck(MilestoneTargetListActivity.this, responseModel.getAppLuck());
            } else {
                super.onBackPressed();
            }
        } catch (Exception e) {
            e.printStackTrace();
            super.onBackPressed();
        }
    }

    private MilestonesTargetResponseModel responseModel;

    public void setActiveMilestonesData(MilestonesTargetResponseModel responseModel1) {
        responseModel = responseModel1;
        tvPoints.setText(SharePreference.getInstance().getEarningPointString());
        helpUrl = responseModel.getHelpVideoUrl();
        ivHelp.setVisibility(CommonMethodsUtils.isStringNullOrEmpty(helpUrl) ? View.GONE : View.VISIBLE);
        customTabAdapter.getActiveMilestonesFragment().setData(responseModel);
        // Show Default AppLuck
        if (!isAppluckLoaded) {
            MainResponseModel responseMain = new Gson().fromJson(SharePreference.getInstance().getString(SharePreference.HomeData), MainResponseModel.class);
            if (!CommonMethodsUtils.isStringNullOrEmpty(responseMain.getIsShowAppluck()) && responseMain.getIsShowAppluck().equals("1") && !CommonMethodsUtils.isStringNullOrEmpty(responseModel.getIsDefaultAppluck()) && responseModel.getIsDefaultAppluck().equals("1")) {
                RelativeLayout relMainFlotAppLuck = findViewById(R.id.relMainFlotAppLuck);
                LinearLayout layoutDefaultAd = findViewById(R.id.layoutDefaultAd);
                CommonMethodsUtils.showDefaultAppLuck(MilestoneTargetListActivity.this, layoutDefaultAd, relMainFlotAppLuck, responseMain.getDefaultAppluckID());
            }
            isAppluckLoaded = true;
        }
    }

    public void setCompletedMilestonesData(EarnedPointHistoryModel responseModel) {
        tvPoints.setText(SharePreference.getInstance().getEarningPointString());
        customTabAdapter.getCompletedMilestonesFragment().setData(responseModel);
    }

    public void changeMilestoneValues(MilestonesTargetResponseModel responseModel) {
        customTabAdapter.getActiveMilestonesFragment().changeMilestoneValues(responseModel);
    }

    public void updateBalance() {
        CommonMethodsUtils.GetCoinAnimation(MilestoneTargetListActivity.this, layoutMain, layoutPoints);
        tvPoints.setText(SharePreference.getInstance().getEarningPointString());
    }

    public void updateHistoryScreen() {
        customTabAdapter.getCompletedMilestonesFragment().clearDataAndRefreshData();
    }
}