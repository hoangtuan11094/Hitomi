package com.hitomi.activities;

import android.app.Notification;
import android.app.PendingIntent;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.hitomi.R;
import com.hitomi.WriteLog;
import com.hitomi.data.DSM_Data;
import com.hitomi.fragments.BaseFragment;
import com.hitomi.fragments.FragmentListener;
import com.hitomi.fragments.FrmFunctionSetting;
import com.hitomi.fragments.FrmInitSetting;
import com.hitomi.fragments.FrmListDevice;
import com.hitomi.fragments.FrmMain;
import com.hitomi.fragments.FrmSettingMenu;
import com.hitomi.views.DialogIndicatorLoading;
import com.hitomi.views.DialogNoticeConfirm;
import com.hitomilib.BaseActivity;
import com.hitomilib.BluetoothLeService;
import com.hitomi.constans.CommonConstance;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;

import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import static com.hitomi.HTMApplication.CHANNEL_ID;
import static com.hitomi.constans.FragmentConstans.FRAGMENT_BASE_SETTING;
import static com.hitomi.constans.FragmentConstans.FRAGMENT_FUNCTION_SETTING;
import static com.hitomi.constans.FragmentConstans.FRAGMENT_LIST_DEVICE;
import static com.hitomi.constans.FragmentConstans.FRAGMENT_MEMU_SETTING;

public class MainActivity extends BaseActivity implements FragmentListener {
    private SimpleDateFormat dfm = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    public static DSM_Data repo = new DSM_Data();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //
        initFragmentMain();
    }


    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public void handleServiceCallBack(Intent intentCallBack) {
        Fragment currentFrm = getCurrentFragment();
        final String action = intentCallBack.getAction();
        Log.d(TAG, "handleServiceCallBack: "+action);
       if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
            dismissLoading();
            if (currentFrm instanceof FrmMain) {
                ((FrmMain) currentFrm).setStatus();
            } else if (currentFrm instanceof FrmListDevice) {
                backStack();
            }
        } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
            dismissLoading();
            if (currentFrm instanceof FrmMain) {
                ((FrmMain) currentFrm).setStatus();
            }
        } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
            // Show all the supported services and characteristics on the user interface.
        } else if (BluetoothLeService.ACTION_CHARACTERISTIC_WRITE.equals(action)) {
            //
            Calendar calendar = Calendar.getInstance();
            String today = dfm.format(calendar.getTime());
            //
           Log.d(TAG, "handleServiceCallBack: "+intentCallBack.getStringExtra(BluetoothLeService.EXTRA_TYPE));
            String type = intentCallBack.getStringExtra(BluetoothLeService.EXTRA_TYPE);
            byte[] data = intentCallBack.getByteArrayExtra(BluetoothLeService.EXTRA_DATA);

            if (type.equals("tx")) {
                new WriteLog(today + " [tx]:" + Arrays.toString(data));
            }
        } else if (BluetoothLeService.ACTION_CHARACTERISTIC_CHANGED.equals(action)) {
            //
            Calendar calendar = Calendar.getInstance();
            String today = dfm.format(calendar.getTime());
            //
            String type = intentCallBack.getStringExtra(BluetoothLeService.EXTRA_TYPE);
            byte[] data = intentCallBack.getByteArrayExtra(BluetoothLeService.EXTRA_DATA);

            switch (type) {
                case "rx":
                    new WriteLog(today + " [rx]:" + Arrays.toString(data));
                    break;
                case "48":
                    setReportData(data);
                    break;
                case "49":
                    setReportData(data);
                    if (currentFrm instanceof FrmMain) {
                        ((FrmMain) currentFrm).setDsmInfo();
                    }
                    break;
            }

        } else if (BluetoothLeService.ACTION_ADD_DEVICE_SCAN.equals(action)) {
            if (currentFrm instanceof FrmListDevice) {
                ((FrmListDevice) currentFrm).addDeviceToList();
            }
        }

    }

    @Override
    public void handleServiceConnection() {

        Intent notificationIntent = new Intent(this, BluetoothLeService.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Hitomi Service")
                .setContentText("Hitomi is running!")
                .setSmallIcon(R.mipmap.hud_loading_01) //Change
                .setContentIntent(pendingIntent)
                .build();

        mBluetoothLeService.startForeground(1, notification);
        mBluetoothLeService.setPreFixDeviceName("dsmbt");//Change to dsmbt
        mBluetoothLeService.setTimeDelayWhenSendRequest(3); //SECONDS
        //
        mBluetoothLeService.addRequestBackground(CommonConstance.rq_dsm_info);
        mBluetoothLeService.addRequestBackground(CommonConstance.rq_dsm_detection);
    }


    @Override
    public void handleBack() {
        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
            finish();
            System.exit(0);
        } else {
            getSupportFragmentManager().popBackStack();
        }

    }

    public void backStack() {
        getSupportFragmentManager().popBackStack();
    }

    private void initFragmentMain() {
        try {
            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction ft = manager.beginTransaction();
            BaseFragment contentFragment = new FrmMain();
            contentFragment.fragmentListener = this;
            ft.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left,
                    R.anim.slide_out_right);
            ft.add(R.id.fmContainer, contentFragment, contentFragment.getClass().getSimpleName());
            ft.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onFragmentChange(int fragmentType) {
        changeFragment(fragmentType);
    }

    private void changeFragment(int fragmentType) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();

        BaseFragment nextFragment = getFragmentByType(fragmentType);
        nextFragment.fragmentListener = this;

        transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left,
                R.anim.slide_out_right);
        transaction.replace(R.id.fmContainer, nextFragment, nextFragment.getClass().getSimpleName());
        transaction.addToBackStack(nextFragment.getClass().getSimpleName());
        transaction.commit();


    }

    private BaseFragment getFragmentByType(int fragmentType) {
        BaseFragment fragment = null;
        switch (fragmentType) {
            case FRAGMENT_LIST_DEVICE:
                fragment = new FrmListDevice();
                break;
            case FRAGMENT_MEMU_SETTING:
                fragment = new FrmSettingMenu();
                break;
            case FRAGMENT_BASE_SETTING:
                fragment = new FrmInitSetting();
                break;
            case FRAGMENT_FUNCTION_SETTING:
                fragment = new FrmFunctionSetting();
                break;
        }
        return fragment;
    }


    private Fragment getCurrentFragment() {
        FragmentManager manager = getSupportFragmentManager();
        return manager.findFragmentById(R.id.fmContainer);
    }

    /**
     * Show Dialog Loading
     */
    public void showLoading() {
        if (indicatorLoading == null) {
            indicatorLoading = DialogIndicatorLoading.getInstance(mActivity).cancelable(false);
        }
        indicatorLoading.showDialog(R.string.msgLoading);
    }

    /**
     * Dismiss Dialog Loading
     */
    public void dismissLoading() {
        if (indicatorLoading == null) {
            indicatorLoading = DialogIndicatorLoading.getInstance(mActivity);
        }
        indicatorLoading.dismiss();
    }

    private DialogNoticeConfirm customDialogNotice;

    public void showDialogNotice() {
        if (customDialogNotice == null) {
            customDialogNotice = DialogNoticeConfirm.getInstance(this).cancelable(false);
        }
        customDialogNotice.showDialog("右上の「接続」ボタンを押して外付けデバイスを選択してください。", "OK", null);
    }

    //DMS DATA
    public static void parceData(int[] reportData) {
        int TYPE = reportData[0];
        switch (TYPE) {
            case 48:
                repo.setVolumeLv(Integer.parseInt(Integer.toHexString(reportData[1]), 16));
                repo.setSensitivityLv(Integer.parseInt(Integer.toHexString(reportData[2]), 16));
                repo.setAtt_check_val(Integer.parseInt(Integer.toHexString(reportData[3]), 16));

                if (repo.getAtt_check_val() == 1) {
                    repo.setAtt_check_on_off(true);
                } else {
                    repo.setAtt_check_on_off(false);
                }

                repo.setSpeed_check_val(Integer.parseInt(Integer.toHexString(reportData[4]), 16));
                if (repo.getSpeed_check_val() == 1) {
                    repo.setSpeed_check_on_off(true);
                } else {
                    repo.setSpeed_check_on_off(false);
                }

                repo.setVer_arr(convertAppVer(5, 19, reportData));
//                Log.d(TAG, "parceData:48 " +
//                        "\nvolumeLv:" + volumeLv
//                        + " \nsensitivityLv: " + sensitivityLv + " \natt_check_on_off: "
//                        + att_check_on_off + " \nspeed_check_on_off: "
//                        + speed_check_on_off + " \nver_arr: "
//                        + ver_arr);
                break;
            case 49:
                int drowsy_check_val = Integer.parseInt(Integer.toHexString(reportData[1]), 16);
                if (drowsy_check_val == 1) {
                    repo.setDrowsy_check(true);
                } else {
                    repo.setDrowsy_check(false);
                }

                int attention_check_val = Integer.parseInt(Integer.toHexString(reportData[2]), 16);
                if (attention_check_val == 1) {
                    repo.setAttention_check(true);
                } else {
                    repo.setAttention_check(false);
                }

                repo.setFace_area_x_potion(positionDetection(Integer.toHexString(reportData[3]), Integer.toHexString(reportData[4])));
                repo.setFace_area_y_potion(positionDetection(Integer.toHexString(reportData[5]), Integer.toHexString(reportData[6])));
                repo.setFace_area_widht(positionDetection(Integer.toHexString(reportData[7]), Integer.toHexString(reportData[8])));
                repo.setFace_area_height(positionDetection(Integer.toHexString(reportData[9]), Integer.toHexString(reportData[10])));

                byte direction_left_right = (byte) reportData[11];
                repo.setFace_direction_left_right(String.valueOf(direction_left_right));

                byte direction_up_down = (byte) reportData[12];
                repo.setFace_direction_up_down(String.valueOf(direction_up_down));

//                Log.d(TAG, "parceData:49 " +
//                        "\ndrowsy_check:" + drowsy_check
//                        + " \nattention_check: " + attention_check + " \nface_area_x_potion: "
//                        + face_area_x_potion + " \nface_area_y_potion: "
//                        + face_area_y_potion + " \nface_area_widht: "
//                        + face_area_widht + " \nface_area_height: "
//                        + face_area_height + " \nface_direction_left_right: "
//                        + face_direction_left_right + " \nface_direction_up_down: "
//                        + face_direction_up_down + " ");

                break;
        }
    }

    private static String convertAppVer(int i, int i1, int[] arrData) {
        String data;
        byte[] bData = new byte[i1 - i];
        int count = 0;
        if (i1 < arrData.length) {
            for (int j = i; j < i1; j++) {
                bData[count] = (byte) arrData[j];
                count++;
            }
        }
        data = new String(bData);
        return data;
    }

    private static int positionDetection(String reportData_1, String reportData_2) {
        String potion;
        int iPotion = -1;
        try {
            potion = reportData_2 + reportData_1;
            iPotion = Integer.parseInt(potion, 16);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return iPotion;
    }

    public static void setReportData(byte[] setdata) {
        final int[] reportData = new int[setdata.length];
        for (int i = 0; i < reportData.length; ++i) {
            reportData[i] = setdata[i];
        }
        parceData(reportData);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (customDialogNotice != null) {
            customDialogNotice.dismiss();
        }
    }

    private DialogIndicatorLoading indicatorLoading;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (customDialogNotice != null) {
            customDialogNotice.release();
            customDialogNotice = null;
        }
        if (indicatorLoading != null) {
            indicatorLoading.release();
            indicatorLoading = null;
        }
    }

    public void connectBluetooth(BluetoothDevice device) {
        showLoading();
        mBluetoothLeService.connect(device);
    }
}
