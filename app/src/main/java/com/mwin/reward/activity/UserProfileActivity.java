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
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.mwin.reward.R;
import com.mwin.reward.async.GetProfileAsync;
import com.mwin.reward.async.models.MainResponseModel;
import com.mwin.reward.async.models.TaskListResponseModel;
import com.mwin.reward.async.models.UserProfileDetails;
import com.mwin.reward.async.models.UserProfileModel;
import com.mwin.reward.utils.CommonMethodsUtils;
import com.mwin.reward.utils.SharePreference;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfileActivity extends AppCompatActivity {
    private View viewShine, viewShineLogin;
    private CircleImageView ivProfilePic;
    private TextView tvName, tvEmail, tvRupees, tvPoints, tvTotalPoints, tvExpendPoints;
    private MainResponseModel responseMain;
    private UserProfileDetails userDetails;
    private ImageView ivBack;
    private AppCompatButton btnWithdraw, btnLogin;
    private LinearLayout layoutFAQ, layoutFeedback, layoutAboutUs, layoutLogout, layoutLogin, layoutDeleteAccount;
    private RelativeLayout layoutProfile;
    private UserProfileModel responseModel;
    private Animation animUpDown;
    private CardView cardWallet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        CommonMethodsUtils.setDayNightTheme(UserProfileActivity.this);
        setContentView(R.layout.activity_user_profile);
        responseMain = new Gson().fromJson(SharePreference.getInstance().getString(SharePreference.HomeData), MainResponseModel.class);
        setViews();
        if (SharePreference.getInstance().getBoolean(SharePreference.IS_LOGIN)) {
            new GetProfileAsync(UserProfileActivity.this);
        }
    }

    private void setViews() {
        try {
            cardWallet = findViewById(R.id.cardWallet);
            viewShineLogin = findViewById(R.id.viewShineLogin);
            viewShine = findViewById(R.id.viewShine);
            layoutProfile = findViewById(R.id.layoutProfile);
            layoutLogin = findViewById(R.id.layoutLogin);
            ivProfilePic = findViewById(R.id.ivProfilePic);
            tvName = findViewById(R.id.tvName);
            tvEmail = findViewById(R.id.tvEmail);

            ivBack = findViewById(R.id.ivBack);
            ivBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onBackPressed();
                }
            });

            tvRupees = findViewById(R.id.tvRupees);
            tvPoints = findViewById(R.id.tvPoints);

            tvRupees.setText("₹ 1");
            tvTotalPoints = findViewById(R.id.tvTotalPoints);
            tvExpendPoints = findViewById(R.id.tvExpendPoints);

            layoutFAQ = findViewById(R.id.layoutFAQ);
            layoutFAQ.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(UserProfileActivity.this, HelpQAActivity.class));
                }
            });

            layoutFeedback = findViewById(R.id.layoutFeedback);
            layoutFeedback.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(UserProfileActivity.this, ContactUsActivity.class).putExtra("title", "Give Feedback"));
                }
            });

            layoutAboutUs = findViewById(R.id.layoutAboutUs);
            layoutAboutUs.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        Intent i = new Intent(UserProfileActivity.this, WebViewActivity.class);
                        i.putExtra("URL", responseMain.getAboutUsUrl());
                        i.putExtra("Title", "About Us");
                        startActivity(i);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            btnWithdraw = findViewById(R.id.btnWithdraw);
            btnWithdraw.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(UserProfileActivity.this, RedeemOptionsListActivity.class));
                }
            });

            btnLogin = findViewById(R.id.btnLogin);
            btnLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        startActivity(new Intent(UserProfileActivity.this, SigninActivity.class));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            layoutLogout = findViewById(R.id.layoutLogout);
            layoutLogout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        CommonMethodsUtils.NotifyLogout(UserProfileActivity.this, null);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            layoutDeleteAccount = findViewById(R.id.layoutDeleteAccount);
            layoutDeleteAccount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        CommonMethodsUtils.NotifyDeleteAccount(UserProfileActivity.this);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            animUpDown = AnimationUtils.loadAnimation(UserProfileActivity.this, R.anim.left_to_right);
            animUpDown.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    if (SharePreference.getInstance().getBoolean(SharePreference.IS_LOGIN)) {
                        viewShine.startAnimation(animUpDown);
                    } else {
                        viewShineLogin.startAnimation(animUpDown);
                    }
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            if (SharePreference.getInstance().getBoolean(SharePreference.IS_LOGIN)) {
                cardWallet.setVisibility(View.VISIBLE);
                layoutProfile.setVisibility(View.VISIBLE);
                layoutLogout.setVisibility(View.VISIBLE);
                layoutLogin.setVisibility(View.GONE);
                if (!CommonMethodsUtils.isStringNullOrEmpty(responseMain.getIsShowAccountDeleteOption()) && responseMain.getIsShowAccountDeleteOption().equals("1")) {
                    layoutDeleteAccount.setVisibility(View.VISIBLE);
                }
            } else {
                layoutProfile.setVisibility(View.GONE);
                cardWallet.setVisibility(View.GONE);
                layoutLogout.setVisibility(View.GONE);
                layoutLogin.setVisibility(View.VISIBLE);
                layoutDeleteAccount.setVisibility(View.GONE);
                viewShineLogin.startAnimation(animUpDown);
            }
            tvPoints.setText(responseMain.getPointValue());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void onBackPressed() {
        try {
            if (responseModel != null && responseModel.getAppLuck() != null ) {
                
                CommonMethodsUtils.dialogShowAppLuck(UserProfileActivity.this, responseModel.getAppLuck());
            } else {
                super.onBackPressed();
            }
        } catch (Exception e) {
            e.printStackTrace();
            super.onBackPressed();
        }
    }

    public void setData(UserProfileModel responseModel) {
        this.responseModel = responseModel;
        // load ads based on data
        userDetails = responseModel.getUserDetails();
        setData();
    }

    public void setData() {
        try {
            // Show Default AppLuck
            if (!CommonMethodsUtils.isStringNullOrEmpty(responseMain.getIsShowAppluck()) && responseMain.getIsShowAppluck().equals("1") && !CommonMethodsUtils.isStringNullOrEmpty(responseModel.getIsDefaultAppluck()) && responseModel.getIsDefaultAppluck().equals("1")) {
                RelativeLayout relMainFlotAppLuck = findViewById(R.id.relMainFlotAppLuck);
                LinearLayout layoutDefaultAd = findViewById(R.id.layoutDefaultAd);
                CommonMethodsUtils.showDefaultAppLuck(UserProfileActivity.this, layoutDefaultAd, relMainFlotAppLuck, responseMain.getDefaultAppluckID());
            }
            // Load home note webview top
            try {
                if (responseModel != null && !CommonMethodsUtils.isStringNullOrEmpty(responseModel.getHomeNote())) {
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
                if (responseModel != null && responseModel.getTopAds() != null && !CommonMethodsUtils.isStringNullOrEmpty(responseModel.getTopAds().getImage())) {
                    LinearLayout layoutTopAds = findViewById(R.id.layoutTopAds);
                    CommonMethodsUtils.loadTopBannerAd(UserProfileActivity.this, layoutTopAds, responseModel.getTopAds());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            // Load Bottom banner ad
            try {
                    LinearLayout layoutBannerAdBottom = findViewById(R.id.layoutBannerAdBottom);
                    layoutBannerAdBottom.setVisibility(View.VISIBLE);
                    TextView lblAdSpaceBottom = findViewById(R.id.lblAdSpaceBottom);
                    CommonMethodsUtils.loadBannerAds(UserProfileActivity.this, layoutBannerAdBottom, lblAdSpaceBottom);
            } catch (Exception e) {
                e.printStackTrace();
            }
            tvTotalPoints.setText(SharePreference.getInstance().getEarningPointString());
            tvEmail.setText(userDetails.getEmailId());
            tvName.setText(userDetails.getFirstName() + " " + userDetails.getLastName());
            if (userDetails.getProfileImage() != null) {
                Glide.with(UserProfileActivity.this).load(userDetails.getProfileImage()).override(getResources().getDimensionPixelSize(R.dimen.dim_80), getResources().getDimensionPixelSize(R.dimen.dim_80)).into(ivProfilePic);
            }
            tvExpendPoints.setText(userDetails.getWithdrawPoint());
            // start the animation
            viewShine.startAnimation(animUpDown);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        updateUserPoints();
    }

    private void updateUserPoints() {
        try {
            tvTotalPoints.setText(SharePreference.getInstance().getEarningPointString());
            tvName.setText(userDetails.getFirstName() + " " + userDetails.getLastName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}