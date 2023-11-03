/*
 * Copyright (c) 2021.  Hurricane Development Studios
 */

package com.mwin.reward.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.mwin.reward.R;
import com.mwin.reward.async.ContactUsAsync;
import com.mwin.reward.async.models.UserProfileDetails;
import com.mwin.reward.utils.ActivityManager;
import com.mwin.reward.utils.AdsUtil;
import com.mwin.reward.utils.CommonMethodsUtils;
import com.mwin.reward.utils.SharePreference;

import java.io.File;

public class ContactUsActivity extends AppCompatActivity {
    public final int GALLERY_REQUEST_CODE = 105, Request_Storage_resize = 200;
    private ImageView ivFAQ, ivImage;
    private AppCompatButton btnSubmit;
    private LinearLayout layoutImage;
    private UserProfileDetails userDetails;
    private EditText etFeedback;
    private String selectedImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        CommonMethodsUtils.setDayNightTheme(ContactUsActivity.this);
        setContentView(R.layout.activity_contact_us);

        userDetails = new Gson().fromJson(SharePreference.getInstance().getString(SharePreference.UserDetails), UserProfileDetails.class);
        TextView tvTitle = findViewById(R.id.tvTitle);
        tvTitle.setText(getIntent().getStringExtra("title"));
        etFeedback = findViewById(R.id.etFeedback);
        ivImage = findViewById(R.id.ivImage);

        ivFAQ = findViewById(R.id.ivFAQ);
        ivFAQ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ContactUsActivity.this, HelpQAActivity.class));
            }
        });

        btnSubmit = findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isValidData()) {
                    AdsUtil.showAppLovinInterstitialAd(ContactUsActivity.this, new AdsUtil.AdShownListener() {
                        @Override
                        public void onAdDismiss() {
                            if (userDetails == null) {
                                new ContactUsAsync(ContactUsActivity.this,
                                        "", etFeedback.getText().toString().trim(), "", selectedImage);
                            } else {
                                new ContactUsAsync(ContactUsActivity.this,
                                        userDetails.getEmailId(), etFeedback.getText().toString().trim(), userDetails.getMobileNumber(), selectedImage);
                            }
                        }
                    });
                }
            }
        });

        ImageView imBack = findViewById(R.id.ivBack);
        imBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        layoutImage = findViewById(R.id.layoutImage);
        layoutImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(ContactUsActivity.this.getApplicationContext(), Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU ? Manifest.permission.READ_MEDIA_IMAGES : Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(ContactUsActivity.this.getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU ? Manifest.permission.READ_MEDIA_IMAGES : Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, Request_Storage_resize);
                } else {
                    selectImage(GALLERY_REQUEST_CODE);
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Request_Storage_resize) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                selectImage(GALLERY_REQUEST_CODE);
            } else {
                CommonMethodsUtils.setToast(ContactUsActivity.this, "Allow permission for storage access!");
            }
            return;
        }
    }

    private void selectImage(int requestCode) {
        ActivityManager.isShowAppOpenAd = false;
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        // pass the constant to compare it
        // with the returned requestCode
        startActivityForResult(Intent.createChooser(i, "Select Picture"), requestCode);
    }

    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            try {
                if (data != null) {
                    Uri uriforloadImage = data.getData();
                    if (uriforloadImage != null) {
                        selectedImage = CommonMethodsUtils.getPathFromURI(ContactUsActivity.this, uriforloadImage);
                        Glide.with(ContactUsActivity.this)
                                .load(new File(selectedImage))
                                .override(getResources().getDimensionPixelSize(R.dimen.dim_90), getResources().getDimensionPixelSize(R.dimen.dim_90))
                                .apply(new RequestOptions().transforms(new CenterCrop(), new RoundedCorners(getResources().getDimensionPixelSize(R.dimen.dim_5))))
                                .into(ivImage);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (requestCode == Request_Storage_resize) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {
                    selectImage(GALLERY_REQUEST_CODE);
                } else {
                    CommonMethodsUtils.setToast(ContactUsActivity.this, "Allow permission for storage access!");
                }
            }
        }
    }


    private boolean isValidData() {
        if (etFeedback.getText().toString().trim().length() == 0) {
            CommonMethodsUtils.setToast(ContactUsActivity.this, "Please enter feedback");
            return false;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}