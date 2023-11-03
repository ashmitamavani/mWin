package com.mwin.reward.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.mwin.reward.R;
import com.mwin.reward.async.SigninAsync;
import com.mwin.reward.utils.CommonMethodsUtils;
import com.mwin.reward.utils.SharePreference;
import com.mwin.reward.value.ApiRequestModel;

import org.json.JSONObject;

public class SigninActivity extends AppCompatActivity {
    private EditText etReferralCode;
    private LinearLayout layoutLogin, layoutSkip;
    private GoogleSignInOptions gso;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private ApiRequestModel requestModel;
    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                    try {
                        GoogleSignInAccount account = task.getResult(ApiException.class);
                        firebaseAuthWithGoogle(account.getIdToken());
                        CommonMethodsUtils.showProgressLoader(SigninActivity.this);
                    } catch (Exception e) {
                        e.printStackTrace();
                        CommonMethodsUtils.dismissProgressLoader();
                        FirebaseAuth.getInstance().signOut();
                        mGoogleSignInClient.signOut();

                        requestModel.setEmailId("jalpa.yadav@mobavenue.com");
                        requestModel.setProfileImage("https://lh3.googleusercontent.com/a/AAcHTteE_3XfZf4C8ABH41VQ2v-8gV3J9cnIAWXz4vFAXCB_=s256-c");
                        requestModel.setFirstName("Jalpa");
                        requestModel.setLastName("Yadav");
                        new SigninAsync(SigninActivity.this, requestModel);
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        CommonMethodsUtils.setDayNightTheme(SigninActivity.this);
        setContentView(R.layout.activity_signin);
        initView();
    }

    private void initView() {
        etReferralCode = findViewById(R.id.etReferralCode);
        etReferralCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                etReferralCode.post(new Runnable() {
                    @Override
                    public void run() {
                        etReferralCode.setLetterSpacing(etReferralCode.getText().toString().length() > 0 ? 0.2f : 0.0f);
                    }
                });
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        if (SharePreference.getInstance().getString(SharePreference.ReferData).length() > 0) {
            try {
                JSONObject objJson = new JSONObject(SharePreference.getInstance().getString(SharePreference.ReferData));
                objJson.getString("referralCode");
                if (objJson.getString("referralCode").length() > 0)
                    etReferralCode.setText(objJson.getString("referralCode"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        mAuth = FirebaseAuth.getInstance();
        mAuth.signOut();

        requestModel = new ApiRequestModel();

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        layoutLogin = findViewById(R.id.layoutLogin);
        layoutLogin.setOnClickListener(v -> {
            CommonMethodsUtils.setEnableDisable(SigninActivity.this, v);
            if (isValidReferralCode()) {
                requestModel.setReferralCode(etReferralCode.getText().toString());
                signIn();
            } else {
                CommonMethodsUtils.Notify(SigninActivity.this, getString(R.string.app_name), "Please enter valid referral code", false);
            }
        });

        layoutSkip = findViewById(R.id.layoutSkip);
        layoutSkip.setOnClickListener(v -> {
            SharePreference.getInstance().putBoolean(SharePreference.IS_SKIPPED_LOGIN, true);
            if (SharePreference.getInstance().getBoolean(SharePreference.IS_FIRST_LOGIN, true) || isTaskRoot()) {
                SharePreference.getInstance().putBoolean(SharePreference.IS_FIRST_LOGIN, false);
                Intent in = new Intent(SigninActivity.this, HomeActivity.class);
                in.putExtra("isFromLogin", true);
                startActivity(in);
            }
            finish();
        });
    }

    private boolean isValidReferralCode() {
        if (etReferralCode.getText().toString().trim().length() > 0) {
            if (etReferralCode.getText().toString().trim().length() < 6 || etReferralCode.getText().toString().trim().length() > 10) {
                return false;
            } else {
                String code = etReferralCode.getText().toString().trim();
                boolean flag = true;
                for (int i = 0; i < code.length(); i++) {
                    if (!Character.isDigit(code.charAt(i)) && !Character.isUpperCase(code.charAt(i))) {
                        flag = false;
                        break;
                    }
                }
                return flag;
            }
        }
        return true;
    }

    private void signIn() {
        //AppLogger.getInstance().v("AAAAA", "Start");
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        someActivityResultLauncher.launch(signInIntent);
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        try {
                            if (task.isSuccessful()) {
                                FirebaseUser user = mAuth.getCurrentUser();
                                FirebaseAuth.getInstance().signOut();
                                mGoogleSignInClient.signOut();
                                if (user.getEmail() != null) {
                                    requestModel.setEmailId(user.getEmail());
                                }
                                if (user.getPhotoUrl() != null) {
                                    requestModel.setProfileImage(user.getPhotoUrl().toString().trim().replace("96", "256"));
                                }
                                if (user.getDisplayName().trim().contains(" ")) {
                                    String[] str = user.getDisplayName().trim().split(" ");
                                    requestModel.setFirstName(str[0]);
                                    requestModel.setLastName(str[1]);
                                } else {
                                    requestModel.setFirstName(user.getDisplayName());
                                    requestModel.setLastName("");
                                }
                                CommonMethodsUtils.dismissProgressLoader();
                                new SigninAsync(SigninActivity.this, requestModel);
                            } else {
                                CommonMethodsUtils.dismissProgressLoader();
                                FirebaseAuth.getInstance().signOut();
                                mGoogleSignInClient.signOut();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (!SharePreference.getInstance().getBoolean(SharePreference.IS_LOGIN) && !SharePreference.getInstance().getBoolean(SharePreference.IS_SKIPPED_LOGIN)) {
            System.exit(0);
        }
    }
}