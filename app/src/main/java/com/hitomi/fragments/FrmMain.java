package com.hitomi.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.hitomi.R;
import com.hitomi.WriteLog;
import com.hitomi.activities.MainActivity;
import com.hitomi.constans.FragmentConstans;
import com.hitomilib.data.DataPreferenceManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import static com.hitomilib.BluetoothLeService.isConnected;
import static com.hitomi.activities.MainActivity.mBluetoothLeService;
import static com.hitomi.activities.MainActivity.repo;

/**
 * A simple {@link Fragment} subclass.
 */
public class FrmMain extends BaseFragment implements View.OnClickListener {

    @SuppressLint("SimpleDateFormat")
    private SimpleDateFormat dfm = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    private Button btnDevice, btnSetting;
    private TextView tvStatus;
    private TextView tvContent;

    public FrmMain() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frm_main, container, false);
        init(view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() instanceof MainActivity && isAdded()){
            String DeivceName = DataPreferenceManager.getInstance(getActivity()).getDataStringFromHolder(DataPreferenceManager.PREFS_DEVICE_NAME);
            Log.d(TAG, "onResume1: "+DeivceName);
            if (TextUtils.isEmpty(DeivceName)){
                Log.d(TAG, "onResume2: "+DeivceName);
                ((MainActivity)getActivity()).showDialogNotice();
            }
        }
        if (mBluetoothLeService!=null){
            mBluetoothLeService.setAutoConnect(true);
        }
        //
        setStatus();
        //
        setDsmInfo();
    }

    private void init(View view) {
        MainActivity.parceData(data48);
        MainActivity.parceData(data49);
        tvStatus = view.findViewById(R.id.tvStatus);
        btnDevice = view.findViewById(R.id.btnDevice);
        btnSetting = view.findViewById(R.id.btnSetting);
        tvContent = view.findViewById(R.id.tvContent);
        //
        btnDevice.setOnClickListener(this);
        btnSetting.setOnClickListener(this);
        //
        tvContent.setMovementMethod(new ScrollingMovementMethod());
        //
    }

    private final int[] data49 = {49, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    private final int[] data48 = {48, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};





    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btnDevice:
                if (mBluetoothLeService != null) {
                    mBluetoothLeService.disconnect();
                    mBluetoothLeService.setAutoConnect(false);
                    isConnected = false;
                    mBluetoothLeService.scanLeDevice(!isConnected);
                    setStatus();
                }
                fragmentListener.onFragmentChange(FragmentConstans.FRAGMENT_LIST_DEVICE);
                break;
            case R.id.btnSetting:
                fragmentListener.onFragmentChange(FragmentConstans.FRAGMENT_MEMU_SETTING);
                break;
        }
    }

    public void setStatus() {
        if (isConnected) {
            tvStatus.setText(R.string.lblConnecting);
            tvStatus.setTextColor(requireActivity().getResources().getColor(R.color.colorGreen));
        } else {
            tvStatus.setText(R.string.lblDisconnected);
            tvStatus.setTextColor(requireActivity().getResources().getColor(R.color.colorRed));
        }
    }

    public void setDsmInfo(){
        WriteLog wlog;
        String content =
                "音量レベル：" + repo.getVolumeLv() + "\n" +
                        "居眠り運転の検出感度レベル：" + repo.getSensitivityLv() + "\n" +
                        "わき見運転検出機能：" + (repo.getAtt_check_on_off() ? "ON" : "OFF") + "\n" +
                        "車速連動機能：" + (repo.getSpeed_check_on_off() ? "ON" : "OFF") + "\n" +
                        "バージョン：" + repo.getVer_arr() + "\n" +
                        //49
                        "眠気検出：" + (repo.getDrowsy_check() ? "眠っている" : "眠っていない") + "\n" +
                        "わき見運転検出：" + (repo.getAttention_check() ? "わき見している" : "わき見していない") + "\n" +
                        "顔の面積X位置：" + repo.getFace_area_x_potion() + "\n" +
                        "顔の面積Y位置：" + repo.getFace_area_y_potion() + "\n" +
                        "顔面の幅：" + repo.getFace_area_widht() + "\n" +
                        "顔面の高さ：" + repo.getFace_area_height() + "\n" +
                        "顔の向き（左右）度の形式：" + repo.getFace_direction_left_right() + "\n" +
                        "顔の向き（上下）度の形式：" + repo.getFace_direction_up_down();
        tvContent.setText(content);
        wlog = new WriteLog("------------------------------------------------------------");
        wlog = new WriteLog("● モニタリング");

        Calendar calendar = Calendar.getInstance();
        String today = dfm.format(calendar.getTime());

        wlog = new WriteLog("検査日時: "+today);
        wlog = new WriteLog(content);
        wlog = new WriteLog("------------------------------------------------------------");
    }
}
