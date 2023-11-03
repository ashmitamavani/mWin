/*
 * Copyright (c) 2021.  Hurricane Development Studios
 */

package com.mwin.reward.activity;

import android.Manifest;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.mwin.reward.R;
import com.mwin.reward.adapter.ReferGuideListAdapter;
import com.mwin.reward.async.DownloadShareImageAsync;
import com.mwin.reward.async.GetReferAsync;
import com.mwin.reward.async.models.HomeSliderItem;
import com.mwin.reward.async.models.MainResponseModel;
import com.mwin.reward.async.models.ReferResponseModel;
import com.mwin.reward.customviews.recyclerview_pagers.PagerAdapterLoginWA;
import com.mwin.reward.customviews.recyclerview_pagers.RecyclerViewPagerLoginWA;
import com.mwin.reward.utils.CommonMethodsUtils;
import com.mwin.reward.utils.SharePreference;
import com.mwin.reward.value.Constants;
import com.skydoves.balloon.ArrowOrientation;
import com.skydoves.balloon.ArrowPositionRules;
import com.skydoves.balloon.Balloon;
import com.skydoves.balloon.BalloonAnimation;
import com.skydoves.balloon.BalloonSizeSpec;
import com.skydoves.balloon.OnBalloonClickListener;

import java.io.File;
import java.util.ArrayList;

public class ReferUsersActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_STORAGE = 1000;
    private ImageView ivBack, ivHistory;
    private ImageView ivHelp, ivCopy;
    private LinearLayout layoutLogin, layoutInvite, layoutPoints, layoutTelegram, layoutMore, layoutWhatsApp, layoutShareOptions, layoutInvitePoints, layoutCopyLink;
    private AppCompatButton btnLogin;
    private TextView tvPoints, tvEarning, tvInviteNo, tvInviteIncome, tvInviteCode, lblInviteCode, tvInvite, tvLoginText, lblInviteNo, lblIncome;
    private ReferResponseModel objInvite;
    private FrameLayout layoutBtnInvite;
    private RelativeLayout layoutSlider, layoutMain;
    private RecyclerViewPagerLoginWA rvSlider;
    private Balloon balloon;
    private ScrollView scroll;
    private String shareMessage, shareType = "0";//0=general,1=telegram,2=whatsapp

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        CommonMethodsUtils.setDayNightTheme(ReferUsersActivity.this);
        setContentView(R.layout.activity_refer_users);
        setViews();
        new GetReferAsync(ReferUsersActivity.this);
    }

    private void setViews() {
        try {

            layoutCopyLink = findViewById(R.id.layoutCopyLink);
            layoutCopyLink.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    copyCode(getString(R.string.app_name) + " Referral Link: " + objInvite.getReferralLink());
                }
            });
            layoutInvitePoints = findViewById(R.id.layoutInvitePoints);
            layoutInvitePoints.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (SharePreference.getInstance().getBoolean(SharePreference.IS_LOGIN)) {
                        startActivity(new Intent(ReferUsersActivity.this, ReferPointHistoryActivity.class));
                    } else {
                        CommonMethodsUtils.NotifyLogin(ReferUsersActivity.this);
                    }
                }
            });
            layoutPoints = findViewById(R.id.layoutPoints);
            layoutPoints.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (SharePreference.getInstance().getBoolean(SharePreference.IS_LOGIN)) {
                        startActivity(new Intent(ReferUsersActivity.this, EarnedWalletBalanceActivity.class));
                    } else {
                        CommonMethodsUtils.NotifyLogin(ReferUsersActivity.this);
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
            CommonMethodsUtils.startRoundAnimation(ReferUsersActivity.this, ivHistory);
            ivHistory.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (SharePreference.getInstance().getBoolean(SharePreference.IS_LOGIN)) {
                        startActivity(new Intent(ReferUsersActivity.this, ReferPointHistoryActivity.class));
                    } else {
                        CommonMethodsUtils.NotifyLogin(ReferUsersActivity.this);
                    }
                }
            });

            rvSlider = findViewById(R.id.rvSlider);
            layoutMain = findViewById(R.id.layoutMain);
            layoutSlider = findViewById(R.id.layoutSlider);

            tvLoginText = findViewById(R.id.tvLoginText);
            tvEarning = findViewById(R.id.tvEarning);
            tvInviteNo = findViewById(R.id.tvInviteNo);
            tvInviteIncome = findViewById(R.id.tvInviteIncome);
            ivCopy = findViewById(R.id.ivCopy);
            ivCopy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    copyCode(getString(R.string.app_name) + " Invite Code: " + objInvite.getReferralCode());
                }
            });
            lblInviteCode = findViewById(R.id.lblInviteCode);
            tvInviteCode = findViewById(R.id.tvInviteCode);
            tvInviteCode.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    copyCode(getString(R.string.app_name) + " Invite Code: " + objInvite.getReferralCode());
                }
            });
            tvInvite = findViewById(R.id.tvInvite);
            lblInviteNo = findViewById(R.id.lblInviteNo);
            lblIncome = findViewById(R.id.lblIncome);

            layoutBtnInvite = findViewById(R.id.layoutBtnInvite);
            layoutBtnInvite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    shareType = "0";
                    callInviteFriends(objInvite.getShareMessage());
                }
            });

            layoutLogin = findViewById(R.id.layoutLogin);
            layoutInvite = findViewById(R.id.layoutInvite);

            btnLogin = findViewById(R.id.btnLogin);
            btnLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        startActivity(new Intent(ReferUsersActivity.this, SigninActivity.class));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            ivHelp = findViewById(R.id.ivHelp);
            ivHelp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showRulesBottomSheet();
                }
            });
            layoutTelegram = findViewById(R.id.layoutTelegram);
            layoutTelegram.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    shareType = "1";
                    callInviteFriends(objInvite.getShareMessageTelegram());
                }
            });
            layoutMore = findViewById(R.id.layoutMore);
            layoutMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    shareType = "0";
                    callInviteFriends(objInvite.getShareMessage());
                }
            });
            layoutWhatsApp = findViewById(R.id.layoutWhatsApp);
            layoutWhatsApp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    shareType = "2";
                    callInviteFriends(objInvite.getShareMessageWhatsApp());
                }
            });
            layoutShareOptions = findViewById(R.id.layoutShareOptions);
            layoutShareOptions.setVisibility(View.INVISIBLE);
            scroll = findViewById(R.id.scroll);
            scroll.setVisibility(View.INVISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void copyCode(String copiedText) {
        try {
            ClipboardManager clipboard = (ClipboardManager) ReferUsersActivity.this.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Copied Text", copiedText);
            clipboard.setPrimaryClip(clip);
            CommonMethodsUtils.setToast(ReferUsersActivity.this, "Copied!");
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
            tvPoints.setText(SharePreference.getInstance().getEarningPointString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showRulesBottomSheet() {
        try {
            BottomSheetDialog dialog = new BottomSheetDialog(ReferUsersActivity.this, android.R.style.Theme_Light);
            dialog.getWindow().setBackgroundDrawableResource(R.color.black_transparent);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            dialog.setCancelable(true);
            dialog.setCanceledOnTouchOutside(true);
            dialog.setContentView(R.layout.popup_refer_user);

            AppCompatButton btnInvite = dialog.findViewById(R.id.btnInvite);
            btnInvite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                    shareType = "0";
                    callInviteFriends(objInvite.getShareMessage());
                }
            });
            layoutLogin.setVisibility(SharePreference.getInstance().getBoolean(SharePreference.IS_LOGIN) ? View.GONE : View.VISIBLE);
            RecyclerView rvRules = dialog.findViewById(R.id.rvRules);
            rvRules.setAdapter(new ReferGuideListAdapter(ReferUsersActivity.this, objInvite.getHowToWork(), new ReferGuideListAdapter.ClickListener() {
                @Override
                public void onInfoButtonClick(int position, View v) {
                    try {
                        balloon = new Balloon.Builder(ReferUsersActivity.this)
                                .setArrowSize(10)
                                .setArrowOrientation(ArrowOrientation.TOP)
                                .setArrowPositionRules(ArrowPositionRules.ALIGN_ANCHOR)
                                .setArrowPosition(0.5f)
                                .setWidth(BalloonSizeSpec.WRAP)
                                .setHeight(65)
                                .setTextSize(15f)
                                .setCornerRadius(4f)
                                .setAlpha(0.9f)
                                .setText(objInvite.getHowToWork().get(position).getDescription())
                                .setTextColor(ContextCompat.getColor(ReferUsersActivity.this, R.color.white))
                                .setTextIsHtml(true)
                                .setBackgroundColor(ContextCompat.getColor(ReferUsersActivity.this, R.color.black_transparent))
                                .setOnBalloonClickListener(new OnBalloonClickListener() {
                                    @Override
                                    public void onBalloonClick(@NonNull View view) {
                                        balloon.dismiss();
                                    }
                                })
                                .setBalloonAnimation(BalloonAnimation.FADE)
                                .setLifecycleOwner(ReferUsersActivity.this)
                                .build();
                        balloon.showAlignBottom(v);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }));
            rvRules.setLayoutManager(new LinearLayoutManager(ReferUsersActivity.this));
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void callInviteFriends(String message) {
        try {
            if (!SharePreference.getInstance().getBoolean(SharePreference.IS_LOGIN)) {
                CommonMethodsUtils.NotifyLogin(ReferUsersActivity.this);
            } else {
                if (!CommonMethodsUtils.isStringNullOrEmpty(objInvite.getShareImage())) {
                    if (ContextCompat.checkSelfPermission(ReferUsersActivity.this, Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU ? Manifest.permission.READ_MEDIA_IMAGES : Manifest.permission.READ_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(ReferUsersActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {
                        shareMessage = message;
                        requestPermissions(new String[]{Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU ? Manifest.permission.READ_MEDIA_IMAGES : Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_STORAGE);
                    } else {
                        shareImageData(ReferUsersActivity.this, "", message);
                    }
                } else {
                    shareImageData(ReferUsersActivity.this, "", message);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void onBackPressed() {
        try {
            if (responseModel != null && responseModel.getAppLuck() != null ) {
                
                CommonMethodsUtils.dialogShowAppLuck(ReferUsersActivity.this, responseModel.getAppLuck());
            } else {
                super.onBackPressed();
            }
        } catch (Exception e) {
            e.printStackTrace();
            super.onBackPressed();
        }
    }

    private ReferResponseModel responseModel;

    public void setData(ReferResponseModel responseModel1) {
        responseModel = responseModel1;
        try {
            if (responseModel.getInviteSlider() != null && responseModel.getInviteSlider().size() > 0) {
                layoutSlider.setVisibility(View.VISIBLE);
                rvSlider.setClear();
                rvSlider.addAll((ArrayList<HomeSliderItem>) responseModel.getInviteSlider());
                rvSlider.start();
                rvSlider.setOnItemClickListener(new PagerAdapterLoginWA.OnItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        CommonMethodsUtils.Redirect(ReferUsersActivity.this, responseModel.getInviteSlider().get(position).getScreenNo(), responseModel.getInviteSlider().get(position).getTitle()
                                , responseModel.getInviteSlider().get(position).getUrl(), responseModel.getInviteSlider().get(position).getId(), null, responseModel.getInviteSlider().get(position).getImage());
                    }
                });
            } else {
                layoutSlider.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        layoutLogin.setVisibility(SharePreference.getInstance().getBoolean(SharePreference.IS_LOGIN) ? View.GONE : View.VISIBLE);
        layoutInvite.setVisibility(SharePreference.getInstance().getBoolean(SharePreference.IS_LOGIN) ? View.VISIBLE : View.GONE);
        if (responseModel == null) {
            return;
        }
        objInvite = responseModel;
        layoutShareOptions.setVisibility(View.VISIBLE);
        scroll.setVisibility(View.VISIBLE);
        // Show Default AppLuck
        MainResponseModel responseMain = new Gson().fromJson(SharePreference.getInstance().getString(SharePreference.HomeData), MainResponseModel.class);
        if (!CommonMethodsUtils.isStringNullOrEmpty(responseMain.getIsShowAppluck()) && responseMain.getIsShowAppluck().equals("1") && !CommonMethodsUtils.isStringNullOrEmpty(responseModel.getIsDefaultAppluck()) && responseModel.getIsDefaultAppluck().equals("1")) {
            RelativeLayout relMainFlotAppLuck = findViewById(R.id.relMainFlotAppLuck);
            LinearLayout layoutDefaultAd = findViewById(R.id.layoutDefaultAd);
            CommonMethodsUtils.showDefaultAppLuck(ReferUsersActivity.this, layoutDefaultAd, relMainFlotAppLuck, responseMain.getDefaultAppluckID());
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
                CommonMethodsUtils.loadTopBannerAd(ReferUsersActivity.this, layoutTopAds, responseModel.getTopAds());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        ivHelp.setVisibility(responseModel.getHowToWork() != null && responseModel.getHowToWork().size() > 0 ? View.VISIBLE : View.GONE);
        layoutMain.setBackgroundColor(Color.parseColor(responseModel.getBackgroundColor()));
        tvInvite.setText(responseModel.getBtnName());
        tvInvite.setTextColor(Color.parseColor(responseModel.getBtnTextColor()));

        tvEarning.setText(responseModel.getReferIncome());

        tvInviteNo.setText(responseModel.getTotalReferrals());
        tvInviteNo.setTextColor(Color.parseColor(responseModel.getInviteNoTextColor()));

        tvInviteIncome.setText(responseModel.getTotalReferralIncome());
        tvInviteIncome.setTextColor(Color.parseColor(responseModel.getInviteNoTextColor()));

//        lblInviteCode.setTextColor(Color.parseColor(responseModel.getInviteLabelTextColor()));

        tvInviteCode.setText(responseModel.getReferralCode());
//        tvInviteCode.setTextColor(Color.parseColor(responseModel.getInviteLabelTextColor()));

        ivCopy.setImageTintList(ColorStateList.valueOf(Color.parseColor(responseModel.getInviteLabelTextColor())));

        lblInviteNo.setTextColor(Color.parseColor(responseModel.getInviteLabelTextColor()));
        lblIncome.setTextColor(Color.parseColor(responseModel.getInviteLabelTextColor()));
        tvLoginText.setTextColor(Color.parseColor(responseModel.getTextColor()));
    }

    public void shareImageData(Activity activity, String img, String msg) {
        Intent share;
        if (img.trim().length() > 0) {
            String[] str = img.trim().split("/");
            String extension = "";
            if (str[str.length - 1].contains(".")) {
                extension = str[str.length - 1].substring(str[str.length - 1].lastIndexOf("."));
            }
            if (extension.equals(".png") || extension.equals(".jpg") || extension.equals(".gif")) {
                extension = "";
            } else {
                extension = ".png";
            }
            File dir = new File(String.valueOf(Environment.getExternalStoragePublicDirectory((Environment.DIRECTORY_PICTURES) + File.separator)));
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File file = new File(dir, str[str.length - 1] + extension);
            if (file.exists()) {
                try {
                    share = new Intent(Intent.ACTION_SEND);
                    Uri uri = null;
                    if (Build.VERSION.SDK_INT >= 24) {
                        share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        uri = FileProvider.getUriForFile(activity.getApplicationContext(), activity.getPackageName() + ".provider", file);
                    } else {
                        uri = Uri.fromFile(file);
                    }
                    share.setType("image/*");
                    if (img.contains(".gif")) {
                        share.setType("image/gif");
                    } else {
                        share.setType("image/*");
                    }
                    share.putExtra(Intent.EXTRA_STREAM, uri);
                    share.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
                    share.putExtra(Intent.EXTRA_TEXT, msg);
                    if (shareType.equals("1")) {
                        share.setPackage(Constants.telegramPackageName);
                        activity.startActivity(share);
                    } else if (shareType.equals("2")) {
                        share.setPackage(Constants.whatsappPackageName);
                        activity.startActivity(share);
                    } else {
                        activity.startActivity(Intent.createChooser(share, "Share"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (CommonMethodsUtils.isNetworkAvailable(ReferUsersActivity.this)) {
                new DownloadShareImageAsync(activity, file, img, msg, shareType).execute();
            }
        } else {
            try {
                share = new Intent(Intent.ACTION_SEND);
                share.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
                share.putExtra(Intent.EXTRA_TEXT, msg);
                share.setType("text/plain");
                if (shareType.equals("1")) {
                    share.setPackage(Constants.telegramPackageName);
                    activity.startActivity(share);
                } else if (shareType.equals("2")) {
                    share.setPackage(Constants.whatsappPackageName);
                    activity.startActivity(share);
                } else {
                    activity.startActivity(Intent.createChooser(share, "Share"));
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        try {
            if (requestCode == REQUEST_CODE_STORAGE) {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    shareImageData(ReferUsersActivity.this, objInvite.getShareImage(), shareMessage == null ? objInvite.getShareMessage() : shareMessage);
                } else {
                    CommonMethodsUtils.setToast(ReferUsersActivity.this, "Allow storage permissions!");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}