package com.hitomi.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioGroup;
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
public class FrmInitSetting extends BaseFragment implements View.OnClickListener, RadioGroup.OnCheckedChangeListener, CompoundButton.OnCheckedChangeListener {

    private Button btnBack;
    private RadioGroup rgVolume, rgSensitivity;
    private SwitchCompat swAtt;
    private TextView tvAttCheck, tvVer;

    public FrmInitSetting() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frm_init_setting, container, false);
        init(view);
        return view;
    }

    private void init(View view) {
        swAtt = view.findViewById(R.id.swAtt);
        tvAttCheck = view.findViewById(R.id.tvAttCheck);
        tvVer = view.findViewById(R.id.tvVer);

        btnBack = view.findViewById(R.id.btnBack);
        btnBack.setOnClickListener(this);

        rgVolume = view.findViewById(R.id.rgVolume);
        rgSensitivity = view.findViewById(R.id.rgSensitivity);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        handleVolumeChecked(repo.getVolumeLv());
        handleSensiChecked(repo.getSensitivityLv());

        swAtt.setChecked(repo.getAtt_check_on_off());
        setNoticeForAttCheckOnOff(repo.getAtt_check_on_off());

        rgVolume.setOnCheckedChangeListener(this);
        rgSensitivity.setOnCheckedChangeListener(this);

        swAtt.setOnCheckedChangeListener(this);

        tvVer.setText("→  " + repo.getVer_arr());
    }


    private void setNoticeForAttCheckOnOff(boolean isChecked) {
        if (isChecked) {
            tvAttCheck.setText(requireActivity().getResources().getString(R.string.msgDrivingDetectionON));
        } else {
            tvAttCheck.setText(requireActivity().getResources().getString(R.string.msgDrivingDetectionOFF));
        }
    }


    private void handleSensiChecked(int locSensiChecked) {
        Log.d(TAG, "sensitivityLv:" + repo.getSensitivityLv());
        switch (locSensiChecked) {
            case 1:
                rgSensitivity.check(R.id.rbS1);
                break;
            case 2:
                rgSensitivity.check(R.id.rbS2);
                break;
            case 3:
                rgSensitivity.check(R.id.rbS3);
                break;
        }
    }

    private void handleVolumeChecked(int locVolumeChecked) {
        Log.d(TAG, "settingVolumeLv:" + repo.getVolumeLv());
        switch (locVolumeChecked) {
            case 0:
                rgVolume.check(R.id.rbV0);
                break;
            case 1:
                rgVolume.check(R.id.rbV1);
                break;
            case 2:
                rgVolume.check(R.id.rbV2);
                break;
            case 3:
                rgVolume.check(R.id.rbV3);
                break;
            case 4:
                rgVolume.check(R.id.rbV4);
                break;
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if (group == rgVolume) {
            switch (checkedId) {
                case R.id.rbV0:
                    settingVolume(0);
                    break;
                case R.id.rbV1:
                    settingVolume(1);
                    break;
                case R.id.rbV2:
                    settingVolume(2);
                    break;
                case R.id.rbV3:
                    settingVolume(3);
                    break;
                case R.id.rbV4:
                    settingVolume(4);
                    break;
            }
        } else if (group == rgSensitivity) {
            switch (checkedId) {
                case R.id.rbS1:
                    settingSensitivity(1);
                    break;
                case R.id.rbS2:
                    settingSensitivity(2);
                    break;
                case R.id.rbS3:
                    settingSensitivity(3);
                    break;
            }
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(buttonView == swAtt){
            setNoticeForAttCheckOnOff(isChecked);
            settingAttCheck(isChecked);
        }
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

    private void settingVolume(int volume) {
        int[] rq_set_volume_int = {60, volume, 0, 0, 0, 0, 0, 0};
        final byte[] rq_set_volume = new byte[rq_set_volume_int.length];
        for (int i = 0; i < rq_set_volume.length; ++i) {
            rq_set_volume[i] = (byte) rq_set_volume_int[i];
        }
        Log.d(TAG, "test_server:" + mBluetoothLeService);
        //repo.getCharacteristic().setValue(rq_set_volume);
        //mBluetoothLeService.writeCharacteristic(repo.getCharacteristic());
        mBluetoothLeService.writeCharacteristic(UUID.fromString(GATT_SERVICE_UUID), UUID.fromString(GATT_ATTRIBUTE_DATA_UUID), rq_set_volume);
    }

    private void settingSensitivity(int sensitivity) {
        int[] rq_set_sensitivity_int = {61, sensitivity, 0, 0, 0, 0, 0, 0};
        final byte[] rq_set_sensitivity = new byte[rq_set_sensitivity_int.length];
        for (int i = 0; i < rq_set_sensitivity.length; ++i) {
            rq_set_sensitivity[i] = (byte) rq_set_sensitivity_int[i];
        }
        Log.d(TAG, "test_character:" + repo.getCharacteristic());
        //repo.getCharacteristic().setValue(rq_set_sensitivity);
        //mBluetoothLeService.writeCharacteristic(repo.getCharacteristic());
        mBluetoothLeService.writeCharacteristic(UUID.fromString(GATT_SERVICE_UUID), UUID.fromString(GATT_ATTRIBUTE_DATA_UUID), rq_set_sensitivity);
    }

    private void settingAttCheck(boolean state) {
        int attState;
        if (state) {
            attState = 1;
        } else {
            attState = 0;
        }
        int[] rq_set_att_int = {62, attState, repo.getSpeed_check_val(), 0, 0, 0, 0, 0};
        final byte[] rq_set_att = new byte[rq_set_att_int.length];
        for (int i = 0; i < rq_set_att.length; ++i) {
            rq_set_att[i] = (byte) rq_set_att_int[i];
        }
        Log.d(TAG, "test_character:" + repo.getCharacteristic());
        //repo.getCharacteristic().setValue(rq_set_sensitivity);
        //mBluetoothLeService.writeCharacteristic(repo.getCharacteristic());
        mBluetoothLeService.writeCharacteristic(UUID.fromString(GATT_SERVICE_UUID), UUID.fromString(GATT_ATTRIBUTE_DATA_UUID), rq_set_att);
    }
}
