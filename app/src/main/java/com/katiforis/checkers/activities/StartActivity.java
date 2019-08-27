package com.katiforis.checkers.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.WindowManager;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.katiforis.checkers.R;
import com.katiforis.checkers.fragment.HomeFragment;
import com.katiforis.checkers.stomp.Client;
import com.katiforis.checkers.util.AudioPlayer;
import com.katiforis.checkers.util.LocalCache;

import cn.pedant.SweetAlert.SweetAlertDialog;
import info.hoang8f.widget.FButton;

import static android.content.ContentValues.TAG;
import static com.katiforis.checkers.util.CachedObjectProperties.TOKEN;
import static com.katiforis.checkers.util.CachedObjectProperties.USER_ID;

public class StartActivity extends AppCompatActivity {
    private FButton loginWithGoogle;
    private FButton loginAsGuest;
    private AudioPlayer audioPlayer;
    private SweetAlertDialog noInternetDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        audioPlayer = new AudioPlayer(this);
        initialize();
    }

    private void initialize() {
        getSupportActionBar().hide();
        setContentView(R.layout.activity_start_layout);
        loginAsGuest = findViewById(R.id.login_as_guest);
        loginWithGoogle = findViewById(R.id.login_with_google);
        loginWithGoogle.setButtonColor(getResources().getColor(R.color.fbutton_color_pomegranate));
        loginAsGuest.setButtonColor(getResources().getColor(R.color.fbutton_color_silver2));
        loginAsGuest.setOnClickListener(p -> {
            audioPlayer.playClickButton();
            LocalCache.getInstance().saveString(TOKEN, null, this);
            intentToMenuActivity();
        });
        loginWithGoogle.setOnClickListener(p -> {
            audioPlayer.playClickButton();
            signInIntent();
        });
        noInternetDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
                .setTitleText("No Internet Connection!")
                .setContentText("Trying to reconnect. . . ");
        noInternetDialog.setCanceledOnTouchOutside(false);
    }

    public void handleReconnection() {
        showNoInternetDialog(false);
    }

    private void intentToMenuActivity() {
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.setClass(this, MenuActivity.class);
        startActivity(intent);
    }

    private void signInIntent() {
        Intent intent = HomeFragment.signInClient.getSignInIntent();
        startActivityForResult(intent, 0);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if (account != null) {
                    onLogin(account);
                } else {
                    //TODO repost exception to usesr
                }
            } catch (ApiException e) {
                Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            } catch (Exception e) {
                //TODO repost exception to usesr
                Log.w(TAG, "signInResult:failed ", e);
            }
        }
    }

    private void onLogin(GoogleSignInAccount account) {
        LocalCache.getInstance().saveString(TOKEN, account.getIdToken(), this);
        if (Client.isConnected()) {
            Client.getInstance().disconnect();
            Client.getInstance();
        } else {
            Client.getInstance();
        }
        intentToMenuActivity();
    }

    public void showNoInternetDialog(boolean show) {
        this.runOnUiThread(() -> {

                if (noInternetDialog == null) return;
                if (!show) {
                    noInternetDialog.dismiss();
                } else if (show && !noInternetDialog.isShowing()) {
                    noInternetDialog.show();
                }

        });
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//		audioPlayer.release();
    }
}
