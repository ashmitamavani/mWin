/*
 * Copyright (c) 2021.  Hurricane Development Studios
 */

package com.mwin.reward.activity;

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
import com.mwin.reward.adapter.ReferScreenHistoryTabAdapter;
import com.mwin.reward.async.models.EarnedPointHistoryModel;
import com.mwin.reward.async.models.MainResponseModel;
import com.mwin.reward.utils.CommonMethodsUtils;
import com.mwin.reward.utils.SharePreference;
import com.mwin.reward.value.Constants;

import java.util.ArrayList;

public class ReferPointHistoryActivity extends AppCompatActivity {
    private TextView tvPoints;
    private TabLayout tabLayout;
    private ArrayList<String> mFragmentItems;
    private ViewPager viewpager;
    private ReferScreenHistoryTabAdapter customTabAdapter;
    private boolean isAppluckLoaded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        CommonMethodsUtils.setDayNightTheme(ReferPointHistoryActivity.this);
        setContentView(R.layout.activity_refer_point_history);

        tvPoints = findViewById(R.id.tvPoints);
        tvPoints.setText(SharePreference.getInstance().getEarningPointString());

        ImageView imBack = findViewById(R.id.ivBack);
        imBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        viewpager = findViewById(R.id.viewpager);
        tabLayout = findViewById(R.id.tabLayout);

        setupViewPager(viewpager);
        tabLayout.setupWithViewPager(viewpager);
        viewpager.setCurrentItem(0);
    }

    private void setupViewPager(ViewPager viewPager) {
        mFragmentItems = new ArrayList<>();
        mFragmentItems.add("Refer Income");
        mFragmentItems.add("Referred Users");

        customTabAdapter = new ReferScreenHistoryTabAdapter(getSupportFragmentManager(), mFragmentItems);
        viewPager.setAdapter(customTabAdapter);
        viewPager.setOffscreenPageLimit(1);
        customTabAdapter.notifyDataSetChanged();
    }
    
    @Override
    public void onBackPressed() {
        try {
            if (responseModel != null && responseModel.getAppLuck() != null ) {
                
                CommonMethodsUtils.dialogShowAppLuck(ReferPointHistoryActivity.this, responseModel.getAppLuck());
            } else {
                super.onBackPressed();
            }
        } catch (Exception e) {
            e.printStackTrace();
            super.onBackPressed();
        }
    }

    private EarnedPointHistoryModel responseModel;

    public void setData(String type, EarnedPointHistoryModel responseModel1) {
        if (type.equals(Constants.HistoryType.REFER_POINT)) {
            responseModel = responseModel1;
            customTabAdapter.getReferPointHistoryFragment().setData(responseModel1);
        } else {
            customTabAdapter.getReferUserHistoryFragment().setData(responseModel1);
        }
        tvPoints.setText(SharePreference.getInstance().getEarningPointString());
        // Show Default AppLuck
        if (!isAppluckLoaded) {
            MainResponseModel responseMain = new Gson().fromJson(SharePreference.getInstance().getString(SharePreference.HomeData), MainResponseModel.class);
            if (!CommonMethodsUtils.isStringNullOrEmpty(responseMain.getIsShowAppluck()) && responseMain.getIsShowAppluck().equals("1") && !CommonMethodsUtils.isStringNullOrEmpty(responseModel.getIsDefaultAppluck()) && responseModel.getIsDefaultAppluck().equals("1")) {
                RelativeLayout relMainFlotAppLuck = findViewById(R.id.relMainFlotAppLuck);
                LinearLayout layoutDefaultAd = findViewById(R.id.layoutDefaultAd);
                CommonMethodsUtils.showDefaultAppLuck(ReferPointHistoryActivity.this, layoutDefaultAd, relMainFlotAppLuck, responseMain.getDefaultAppluckID());
            }
            isAppluckLoaded = true;
        }
    }
}