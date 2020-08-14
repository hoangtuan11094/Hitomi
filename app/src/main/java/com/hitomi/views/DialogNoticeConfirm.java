package com.hitomi.views;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.hitomilib.BaseActivity;
import com.hitomilib.R;

import java.util.Objects;

import androidx.annotation.NonNull;


public class DialogNoticeConfirm extends Dialog {

    @SuppressLint("StaticFieldLeak")
    private static DialogNoticeConfirm _instance;
    private BaseActivity mActivity;
    //
    private TextView tvHeader;
    private TextView tvNoteCode;
    private TextView tvNoteMsg;
    private TextView btnOk;
    //
    private String header;
    private String code;
    private String message;
    private String labelButtonConfirm;
    private OnClickConfirmListener onClickConfirmListener;

    private DialogNoticeConfirm(@NonNull BaseActivity activity) {
        super(activity);
        mActivity = activity;
    }

    @NonNull
    public static DialogNoticeConfirm getInstance(@NonNull BaseActivity activity) {
        if (_instance == null || _instance.mActivity == null || !_instance.mActivity.getClass()
                .getSimpleName().equals(activity.getClass().getSimpleName())) {
            _instance = new DialogNoticeConfirm(activity);
        }
        return _instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.dialog_notice_confirm);
            getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            //

            tvNoteMsg = findViewById(R.id.tvNoteMsg);
            btnOk = findViewById(R.id.btnOk);
            //

            if (!TextUtils.isEmpty(message)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    tvNoteMsg.setText(Html.fromHtml(message, Html.FROM_HTML_MODE_LEGACY));
                } else {
                    tvNoteMsg.setText(Html.fromHtml(message));
                }
                tvNoteMsg.setVisibility(View.VISIBLE);
            } else {
                tvNoteMsg.setVisibility(View.GONE);
            }
            if (!TextUtils.isEmpty(labelButtonConfirm)) {
                btnOk.setText(labelButtonConfirm);
                btnOk.setVisibility(View.VISIBLE);
            } else {
                btnOk.setVisibility(View.GONE);
            }
            findViewById(R.id.btnOk).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        resize();
    }

    private void resize() {
        try {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
            DisplayMetrics displayMetrics = new DisplayMetrics();
            mActivity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int displayWidth = displayMetrics.widthPixels;
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.copyFrom(getWindow().getAttributes());
            layoutParams.width = (int) (displayWidth * 0.65f);
            getWindow().setAttributes(layoutParams);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showDialog(String message, String labelButtonConfirm,
                           OnClickConfirmListener onClickConfirmListener) {
        try {
            refreshData(message, labelButtonConfirm);
            this.onClickConfirmListener = onClickConfirmListener;
            show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showDialog(int resMessage, int resLabelButtonConfirm,
                           OnClickConfirmListener onClickConfirmListener) {
        try {
            refreshData(
                    mActivity.getResources().getString(resMessage),
                    mActivity.getResources().getString(resLabelButtonConfirm));
            this.onClickConfirmListener = onClickConfirmListener;
            show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void refreshData(String message, String labelButtonConfirm) {

        this.message = message;

        this.labelButtonConfirm = labelButtonConfirm;

        if (tvNoteMsg != null) {
            if (!TextUtils.isEmpty(message)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    tvNoteMsg.setText(Html.fromHtml(message, Html.FROM_HTML_MODE_LEGACY));
                } else {
                    tvNoteMsg.setText(Html.fromHtml(message));
                }
                tvNoteMsg.setVisibility(View.VISIBLE);
            } else {
                tvNoteMsg.setVisibility(View.GONE);
            }
        }
        if (btnOk != null) {
            if (!TextUtils.isEmpty(labelButtonConfirm)) {
                btnOk.setText(labelButtonConfirm);
                btnOk.setVisibility(View.VISIBLE);
            } else {
                btnOk.setVisibility(View.GONE);
            }
        }
    }

    public DialogNoticeConfirm cancelable(boolean allowCancel) {
        setCancelable(allowCancel);
        return this;
    }

    public void release() {
        mActivity = null;
    }

    public interface OnClickConfirmListener {

        void clickedConfirm();
    }

}
