package com.mwin.reward.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatCheckBox;

import com.google.gson.Gson;
import com.mwin.reward.R;
import com.mwin.reward.async.models.MainResponseModel;
import com.mwin.reward.utils.CommonMethodsUtils;
import com.mwin.reward.utils.SharePreference;

public class UserAgreementActivity extends AppCompatActivity {
    private AppCompatCheckBox cbAgree;
    private TextView tvPrivacyPolicy, tvTerms;
    private AppCompatButton btnIAgree, btnQuit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        CommonMethodsUtils.setDayNightTheme(UserAgreementActivity.this);
        setContentView(R.layout.activity_user_agreement);

        btnIAgree = findViewById(R.id.btnIAgree);
        btnIAgree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharePreference.getInstance().putBoolean(SharePreference.IS_USER_CONSENT_ACCEPTED, true);
                if (!SharePreference.getInstance().getBoolean(SharePreference.IS_LOGIN) && !SharePreference.getInstance().getBoolean(SharePreference.IS_SKIPPED_LOGIN)) {
                    startActivity(new Intent(UserAgreementActivity.this, SigninActivity.class));
                } else {
                    Intent i = new Intent(UserAgreementActivity.this, HomeActivity.class);
                    startActivity(i);
                }
                finish();
            }
        });

        btnQuit = findViewById(R.id.btnQuit);
        btnQuit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        cbAgree = findViewById(R.id.cbAgree);
        cbAgree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((CompoundButton) v).isChecked()) {
                    btnIAgree.setEnabled(true);
                } else {
                    btnIAgree.setEnabled(false);
                }
            }
        });

        tvTerms = findViewById(R.id.tvTerms);
        tvTerms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent i = new Intent(UserAgreementActivity.this, WebViewActivity.class);
                    i.putExtra("URL", new Gson().fromJson(SharePreference.getInstance().getString(SharePreference.HomeData), MainResponseModel.class).getTermsConditionUrl());
                    i.putExtra("Title", getResources().getString(R.string.app_terms));
                    i.putExtra("Source", "UserConsent");
                    startActivity(i);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        tvPrivacyPolicy = findViewById(R.id.tvPrivacyPolicy);
        tvPrivacyPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent i = new Intent(UserAgreementActivity.this, WebViewActivity.class);
                    i.putExtra("URL", new Gson().fromJson(SharePreference.getInstance().getString(SharePreference.HomeData), MainResponseModel.class).getPrivacyPolicy());
                    i.putExtra("Title", getResources().getString(R.string.privacy_policy));
                    i.putExtra("Source", "UserConsent");
                    startActivity(i);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        System.exit(0);
    }
}