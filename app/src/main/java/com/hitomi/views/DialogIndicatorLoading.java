package com.hitomi.views;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.hitomilib.R;

import java.util.Objects;

import androidx.annotation.NonNull;


public class DialogIndicatorLoading extends Dialog {

    private final String TAG = "LOADING";
    @SuppressLint("StaticFieldLeak")
    private static DialogIndicatorLoading _instance;
    private Activity mActivity;
    private TextView tvMessage;
    private String messageLoading = "";
    //
    private ImageView ivIndicatorLoading;
    private AnimationDrawable animationDrawable;
    private Runnable animationRunnable = new Runnable() {

        @Override
        public void run() {
            if (animationDrawable != null) {
                animationDrawable.start();
            }
        }
    };

    private DialogIndicatorLoading(@NonNull Activity activity) {
        super(activity);
        mActivity = activity;
    }

    @NonNull
    public static DialogIndicatorLoading getInstance(@NonNull Activity activity) {
        if (_instance == null || _instance.mActivity == null || !_instance.mActivity.getClass()
                .getSimpleName().equals(activity.getClass().getSimpleName())) {
            Log.d("LOADING", "getInstance: ");
            _instance = new DialogIndicatorLoading(activity);
        }
        return _instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCancelable(false);
        try {
            Objects.requireNonNull(getWindow()).requestFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.dlg_indicator_loading);
            getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            //
            tvMessage = findViewById(R.id.tvMessage);
            if (TextUtils.isEmpty(messageLoading)) {
                tvMessage.setVisibility(View.GONE);
            } else {
                tvMessage.setText(messageLoading);
                tvMessage.setVisibility(View.VISIBLE);
            }
            //
            ivIndicatorLoading = findViewById(R.id.ivIndicatorLoading);
            animationDrawable = (AnimationDrawable) ivIndicatorLoading.getDrawable();
            ivIndicatorLoading.post(animationRunnable);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showDialog(String messageLoading) {
        try {
            Log.d(TAG, "showDialog: messageLoading " + messageLoading);
            refreshData(messageLoading);
            show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showDialog(int resMessageLoading) {
        try {
            Log.d(TAG, "showDialog: resMessageLoading " + resMessageLoading);
            refreshData(mActivity.getResources().getString(resMessageLoading));
            show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void refreshData(String messageLoading) {
        try {
            this.messageLoading = messageLoading;
            Log.d(TAG, "refreshData: " + messageLoading + " " + tvMessage);
            if (tvMessage != null) {
                if (TextUtils.isEmpty(messageLoading)) {
                    tvMessage.setVisibility(View.GONE);
                } else {
                    tvMessage.setText(Html.fromHtml(messageLoading));
                    tvMessage.setVisibility(View.VISIBLE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void stopAnimationDrawable() {
        try {
            if (animationDrawable != null) {
                animationDrawable.stop();
                animationDrawable.setCallback(null);
                animationDrawable = null;
            }
            if (ivIndicatorLoading != null) {
                ivIndicatorLoading.setImageDrawable(null);
                ivIndicatorLoading = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public DialogIndicatorLoading cancelable(boolean allowCancel) {
        setCancelable(allowCancel);
        return this;
    }

    public void release() {
        try {
            if (ivIndicatorLoading != null) {
                ivIndicatorLoading.removeCallbacks(animationRunnable);
            }
            stopAnimationDrawable();
            mActivity = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
