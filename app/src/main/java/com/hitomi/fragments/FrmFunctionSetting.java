package com.hitomi.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.hitomi.R;
import com.hitomi.activities.MainActivity;

import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import static com.hitomi.activities.MainActivity.mBluetoothLeService;
import static com.hitomi.activities.MainActivity.repo;
import static com.hitomilib.contances.CommonConstance.GATT_ATTRIBUTE_DATA_UUID;
import static com.hitomilib.contances.CommonConstance.GATT_SERVICE_UUID;

/**
 * A simple {@link Fragment} subclass.
 */
public class FrmFunctionSetting extends BaseFragment implements View.OnClickListener {

    private Button btnBack;
    private TextView tvNoticeBell, tvNoticeVibrate, tvNoticeSpeed;
    private SwitchCompat swBell, swVibrate, swSpeed;

    public FrmFunctionSetting() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frm_function_setting, container, false);
        init(view);
        return view;
    }

    private void init(View view) {

        tvNoticeBell = view.findViewById(R.id.tvNoticeBell);
        tvNoticeVibrate = view.findViewById(R.id.tvNoticeVibrate);
        tvNoticeSpeed = view.findViewById(R.id.tvNoticeSpeed);

        swVibrate = view.findViewById(R.id.swVibrate);
        swSpeed = view.findViewById(R.id.swSpeed);
        swBell = view.findViewById(R.id.swBell);

        btnBack = view.findViewById(R.id.btnBack);
        btnBack.setOnClickListener(this);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initStatusSwitchs();
        handleSwitchCheckedChange();
    }

    private void initStatusSwitchs() {
        if (getActivity() == null) return;
        boolean isBellChecked = false; //Change when use
        boolean isVibrateChecked = false;  //Change when use
        boolean isSpeedChecked = repo.getSpeed_check_on_off();

        swBell.setChecked(isBellChecked);
        swVibrate.setChecked(isVibrateChecked);
        swSpeed.setChecked(isSpeedChecked);

        setNoticeForBell(isBellChecked);
        setNoticeForSwitchVibrate(isVibrateChecked);
        setNoticeForSwitchSpeed(isSpeedChecked);
    }

    private void setNoticeForBell(boolean isBellChecked) {
        if (isBellChecked) {
            tvNoticeBell.setText(requireActivity().getResources().getString(R.string.msgBellON));
        } else {
            tvNoticeBell.setText(requireActivity().getResources().getString(R.string.msgBellOFF));
        }
    }

    private void setNoticeForSwitchVibrate(boolean isVibrateChecked) {
        if (isVibrateChecked) {
            tvNoticeVibrate.setText(requireActivity().getResources().getString(R.string.msgVibrateON));
        } else {
            tvNoticeVibrate.setText(requireActivity().getResources().getString(R.string.msgVibrateOFF));
        }
    }

    private void setNoticeForSwitchSpeed(boolean isSpeedChecked) {
        if (isSpeedChecked) {
            tvNoticeSpeed.setText(requireActivity().getResources().getString(R.string.msgSpeedON));
        } else {
            tvNoticeSpeed.setText(requireActivity().getResources().getString(R.string.msgSpeedOFF));
        }
    }

    //
    private void handleSwitchCheckedChange() {
        swBell.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setNoticeForBell(isChecked);
            }
        });

        swVibrate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setNoticeForSwitchVibrate(isChecked);
            }
        });

        swSpeed.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setNoticeForSwitchSpeed(isChecked);
                settingSpeedCheck(isChecked);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnBack:
                if (getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).backStack();
                }
                break;
        }
    }

    private void settingSpeedCheck(boolean state) {
        int speedState;
        if (state) {
            speedState = 1;
        } else {
            speedState = 0;
        }
        int[] rq_set_speed_int = {62, repo.getAtt_check_val(), speedState, 0, 0, 0, 0, 0};
        final byte[] rq_set_speed = new byte[rq_set_speed_int.length];
        for (int i = 0; i < rq_set_speed.length; ++i) {
            rq_set_speed[i] = (byte) rq_set_speed_int[i];
        }
        Log.d(TAG, "test_character:" + repo.getCharacteristic()+" "+rq_set_speed);
        //repo.getCharacteristic().setValue(rq_set_speed);
        //mBluetoothLeService.writeCharacteristic(repo.getCharacteristic());
        if (mBluetoothLeService!=null){
            mBluetoothLeService.writeCharacteristic(UUID.fromString(GATT_SERVICE_UUID), UUID.fromString(GATT_ATTRIBUTE_DATA_UUID), rq_set_speed);
        }
    }
}
