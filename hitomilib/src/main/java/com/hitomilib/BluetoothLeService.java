/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hitomilib;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

import com.hitomilib.contances.CommonConstance;
import com.hitomilib.data.DataPreferenceManager;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Service for managing connection and data communication with a GATT server hosted on a
 * given Bluetooth LE device.
 */
public class BluetoothLeService extends Service {
    private  String PRE_FIX_DEVICE_NAME = "";

    public static ArrayList<BluetoothDevice> bluetoothDevices;
    private final String TAG = BluetoothLeService.class.getSimpleName();
    public static boolean isConnected = false;
    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private boolean isAutoConnect = true;
    private BluetoothGatt mBluetoothGatt;
    private ArrayList<byte[]> requestList;

    public final static String ACTION_GATT_CONNECTED =
            "com.hitomi.bluetooth.le.ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED =
            "com.hitomi.bluetooth.le.ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_SERVICES_DISCOVERED =
            "com.hitomi.bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED";

    public final static String ACTION_CHARACTERISTIC_CHANGED =
            "com.hitomi.bluetooth.le.ACTION_CHARACTERISTIC_CHANGED";
    public final static String ACTION_CHARACTERISTIC_WRITE =
            "com.hitomi.bluetooth.le.ACTION_CHARACTERISTIC_WRITE";

    public final static String EXTRA_DATA =
            "com.hitomi.bluetooth.le.EXTRA_DATA";
    public final static String EXTRA_TYPE =
            "com.hitomi.bluetooth.le.EXTRA_TYPE";

    public final static String DEVICE_DATA =
            "com.hitomi.bluetooth.le.DEVICE_DATA";
    public final static String ACTION_ADD_DEVICE_SCAN =
            "com.hitomi.bluetooth.le.ACTION_ADD_DEVICE_SCAN";


    private static final UUID UUID_NOTIFY = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");

    // Implements callback methods for GATT events that the app cares about.  For example,
    // connection change and services discovered.
    private BluetoothGattCallback mGattCallback;

    public long getTimeDelayWhenSendRequest() {
        return TIME_DELAY;
    }

    /**
     * @param TIME_DELAY SECONDS
     * DEFAULT = 3
     */
    public void setTimeDelayWhenSendRequest(long TIME_DELAY) {
        this.TIME_DELAY = TIME_DELAY;
    }

    private long TIME_DELAY = 3; //DEFAULT

    public void addRequestBackground(byte[] request){
        if (requestList == null){
            requestList = new ArrayList<>();
        }

        requestList.add(request);
    }

    public String getPreFixDeviceName() {
        return PRE_FIX_DEVICE_NAME;
    }

    public void setPreFixDeviceName(String PRE_FIX_DEVICE_NAME) {
        this.PRE_FIX_DEVICE_NAME = PRE_FIX_DEVICE_NAME;
    }

    private void broadcastUpdate(final String action) {
        final Intent intent = new Intent(action);
        sendBroadcast(intent);
    }

    private void broadcastUpdate(final String action, BluetoothDevice device) {
        final Intent intent = new Intent(action);
        intent.putExtra(DEVICE_DATA, device);
        sendBroadcast(intent);
    }

    private void broadcastUpdate(final String action,
                                 final byte[] data) {
        final Intent intent = new Intent(action);
        intent.putExtra(EXTRA_DATA, data);
        sendBroadcast(intent);
    }

    private void broadcastUpdate( String action, String type,
                                  byte[] data) {
        final Intent intent = new Intent(action);
        intent.putExtra(EXTRA_TYPE, type);
        intent.putExtra(EXTRA_DATA, data);
        sendBroadcast(intent);
    }

    private ScheduledFuture executors;

    public class LocalBinder extends Binder {
        BluetoothLeService getService() {

            mGattCallback = new BluetoothGattCallback() {
                @Override
                public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                    Log.d(TAG, "onConnectionStateChange: " + status + " " + newState);
                    String intentAction;

                    if (newState == BluetoothProfile.STATE_CONNECTED) {
                        isConnected = true;
                        isAutoConnect = true;
                        String deviceName = gatt.getDevice().getName();
                        DataPreferenceManager.getInstance(BluetoothLeService.this).writeStringData(DataPreferenceManager.PREFS_DEVICE_NAME, deviceName == null ? "" : deviceName);
                        scanLeDevice(!isConnected);
                        intentAction = ACTION_GATT_CONNECTED;
                        broadcastUpdate(intentAction);
                        Log.d(TAG, "Connected to GATT server.");
                        // Attempts to discover services after successful connection.
                        mBluetoothGatt.discoverServices();
                    } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                        if (status == 133) {
                            mBluetoothGatt.close();
                        }
                        isConnected = false;
                        intentAction = ACTION_GATT_DISCONNECTED;
                        Log.d(TAG, "Disconnected from GATT server.");
                        broadcastUpdate(intentAction);
                        scanLeDevice(!isConnected);
                    }


                }

                @Override
                public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                    Log.d(TAG, "onServicesDiscovered: " + status + " " + gatt.getDevice().getName());
                    if (status == BluetoothGatt.GATT_SUCCESS) {
                        broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED);
                        List<BluetoothGattService> gattServices = getSupportedGattServices();
                        for (BluetoothGattService service : gattServices) {
                            Log.d(TAG, "onServicesDiscovered: " + service.getUuid() + " " + UUID.fromString(CommonConstance.GATT_SERVICE_UUID));
                            Log.d(TAG, "onServicesDiscovered: " + (service.getUuid().toString().equals(CommonConstance.GATT_SERVICE_UUID)));
                            if (service.getUuid().toString().equals(CommonConstance.GATT_SERVICE_UUID)) {
                                BluetoothGattCharacteristic characteristic = service.getCharacteristic(UUID.fromString(CommonConstance.GATT_ATTRIBUTE_DATA_UUID));
                                if (characteristic != null) {
                                    //BaseActivity.repo.setGattCharacteristic(characteristic);
                                    //setCharacteristicNotification(UUID.fromString(GATT_SERVICE_UUID), UUID.fromString(GATT_ATTRIBUTE_DATA_UUID), true);
                                    if (executors == null) { // SEND REQUEST
                                        Log.d(TAG, "onReceive: ");
                                        executors = Executors.newSingleThreadScheduledExecutor().scheduleWithFixedDelay(new Runnable() {
                                            @Override
                                            public void run() {
                                                if (isConnected && requestList.size()!=0) {
                                                    for (int j = 0; j < requestList.size(); j++) {
                                                        sendData(requestList.get(j));
                                                    }
                                                }
                                            }
                                        }, 0, TIME_DELAY, TimeUnit.SECONDS);
                                    }
                                }
                            }
                        }
                    } else {
                        Log.w(TAG, "onServicesDiscovered received: " + status);
                    }
                }

                @Override
                public void onCharacteristicRead(BluetoothGatt gatt,
                                                 BluetoothGattCharacteristic characteristic,
                                                 int status) {
                    if (UUID.fromString(CommonConstance.GATT_ATTRIBUTE_DATA_UUID).equals(characteristic.getUuid())) {
                        // キャラクタリスティック１：データサイズは、2バイト（数値を想定。0～65,535）
                        byte[] byteChara = characteristic.getValue();
                        ByteBuffer bb = ByteBuffer.wrap(byteChara);
                        final String strChara = String.valueOf(bb.getShort());
                    }
                }

                @Override
                public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                    super.onCharacteristicWrite(gatt, characteristic, status);
                    //
                    if (UUID.fromString(CommonConstance.GATT_ATTRIBUTE_DATA_UUID).equals(characteristic.getUuid())) {
                        broadcastUpdate(ACTION_CHARACTERISTIC_WRITE,"tx",characteristic.getValue());
                    }
                }

                @Override
                public void onCharacteristicChanged(BluetoothGatt gatt,
                                                    BluetoothGattCharacteristic characteristic) {
                    Log.d(TAG, "onCharacteristicChanged: ");

                    broadcastUpdate(ACTION_CHARACTERISTIC_CHANGED,"rx",characteristic.getValue());
                    // キャラクタリスティックごとに個別の処理
                    if (UUID.fromString(CommonConstance.GATT_ATTRIBUTE_DATA_UUID).equals(characteristic.getUuid())) {
                        // キャラクタリスティック１：データサイズは、2バイト（数値を想定。0～65,535）
                        byte[] byteChara = characteristic.getValue();
//                        ByteBuffer bb = ByteBuffer.wrap(byteChara);
//                        final String strChara = String.valueOf(bb.getShort());

                        Log.d(TAG, "onCharacteristicChanged_UUID: " + characteristic.getUuid());
                        Log.d(TAG, "onCharacteristicChanged_ID: " + byteChara[0]);

                        final int responseID = byteChara[0];

                        switch (responseID) {
                            case 48:
                                Log.d(TAG, "receive_48_DSM_information: ");
                                broadcastUpdate(ACTION_CHARACTERISTIC_CHANGED,"48",byteChara);
                                break;
                            case 49:
                                Log.d(TAG, "receive_49_DSM_detection_data: ");
                                broadcastUpdate(ACTION_CHARACTERISTIC_CHANGED,"49",byteChara);
                                break;
                            case 50:
                                Log.d(TAG, "receive_50_volume_level_response: ");
                                break;
                            case 51:
                                Log.d(TAG, "receive_51_sensitivity_level_response: ");
                                break;
                            case 52:
                                Log.d(TAG, "receive_52_setup_data: ");
                                break;
                        }
                        return;
                    }
                }

                @Override
                public void onPhyUpdate(BluetoothGatt gatt, int txPhy, int rxPhy, int status) {
                    super.onPhyUpdate(gatt, txPhy, rxPhy, status);
                    Log.d(TAG, "onPhyUpdate: ");
                }

                @Override
                public void onPhyRead(BluetoothGatt gatt, int txPhy, int rxPhy, int status) {
                    super.onPhyRead(gatt, txPhy, rxPhy, status);
                    Log.d(TAG, "onPhyRead: ");
                }

                @Override
                public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
                    super.onDescriptorRead(gatt, descriptor, status);
                    Log.d(TAG, "onDescriptorRead: ");
                }

                @Override
                public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
                    super.onDescriptorWrite(gatt, descriptor, status);
                    Log.d(TAG, "onDescriptorWrite: ");
                }

                @Override
                public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {
                    super.onReliableWriteCompleted(gatt, status);
                    Log.d(TAG, "onReliableWriteCompleted: ");
                }

                @Override
                public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
                    super.onReadRemoteRssi(gatt, rssi, status);
                    Log.d(TAG, "onReadRemoteRssi: ");
                }

                @Override
                public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
                    super.onMtuChanged(gatt, mtu, status);
                    Log.d(TAG, "onMtuChanged: ");
                }
            };
            return BluetoothLeService.this;
        }
    }

//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//        Log.d(TAG, "onStartCommand: ");
//
//
//        //do heavy work on a background thread
//        //stopSelf();
//
//        return START_NOT_STICKY;
//    }

    @Override
    public IBinder onBind(Intent intent) {
        bluetoothDevices = new ArrayList<>();
        requestList = new ArrayList<>();
        return mBinder;
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
        Log.d(TAG, "onRebind: ");
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "onUnbind: ");
        if (executors != null) {
            executors.cancel(true);
        }
        isConnected = false;
        scanLeDevice(false);
        close();
        return super.onUnbind(intent);
    }


    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy: ");
        super.onDestroy();
    }

    private final IBinder mBinder = new LocalBinder();

    /**
     * Initializes a reference to the local Bluetooth adapter.
     *
     * @return Return true if the initialization is successful.
     */
    public boolean initialize() {
        // For API level 18 and above, get a reference to BluetoothAdapter through
        // BluetoothManager.
        if (mBluetoothManager == null) {
            mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            if (mBluetoothManager == null) {
                Log.e(TAG, "Unable to initialize BluetoothManager.");
                return false;
            }
        }

        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            Log.e(TAG, "Unable to obtain a BluetoothAdapter.");
            return false;
        }

//        if (!isGrantPermissionLocation()) {
//            return false;
//        }

        return true;
    }

    public boolean connect(final BluetoothDevice device) {
        if (mBluetoothAdapter == null || device == null) {
            Log.d(TAG, "BluetoothAdapter not initialized or unspecified address.");
            return false;
        }

        Log.d(TAG, "connect: " + mBluetoothGatt);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mBluetoothGatt = device.connectGatt(this, false, mGattCallback, BluetoothDevice.TRANSPORT_LE);
        } else {
            mBluetoothGatt = device.connectGatt(this, false, mGattCallback);
        }

        Log.d(TAG, "Trying to create a new device connection.");
        return true;
    }

    /**
     * Disconnects an existing connection or cancel a pending connection. The disconnection result
     * is reported asynchronously through the
     * {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     * callback.
     */
    public void disconnect() {

        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }

        close();
    }

    /**
     * After using a given BLE device, the app must call this method to ensure resources are
     * released properly.
     */
    public void close() {
        Log.d(TAG, "close: ");
        if (mBluetoothGatt == null) {
            return;
        }
        mBluetoothGatt.close();
        mBluetoothGatt = null;
    }

    public void writeCharacteristic(UUID uuid_service, UUID uuid_characteristic, byte[] request) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        BluetoothGattService service = mBluetoothGatt.getService(uuid_service);
        if (service != null) {
            BluetoothGattCharacteristic blechar = service.getCharacteristic(uuid_characteristic);
            if (blechar != null) {
                blechar.setValue(request);
                final int responseID = request[0];
                Log.d(TAG, "receive: " + responseID);
                mBluetoothGatt.writeCharacteristic(blechar);
            }
        }
    }

    public void writeCharacteristic(BluetoothGattCharacteristic characteristic) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.writeCharacteristic(characteristic);
//        mBluetoothGatt.requestMtu()
    }

    // キャラクタリスティック通知の設定
    public void setCharacteristicNotification(UUID uuid_service, UUID uuid_characteristic, boolean enable) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            return;
        }
        BluetoothGattService service = mBluetoothGatt.getService(uuid_service);
        if (service != null) {
            BluetoothGattCharacteristic blechar = service.getCharacteristic(uuid_characteristic);
            if (blechar != null) {
                mBluetoothGatt.setCharacteristicNotification(blechar, enable);
                BluetoothGattDescriptor descriptor = blechar.getDescriptor(UUID_NOTIFY);
                if (descriptor != null) {
                    descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                    mBluetoothGatt.writeDescriptor(descriptor);
                }
            }
        }
    }

    public void readCharacteristic(UUID uuid_service, UUID uuid_characteristic) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        BluetoothGattService service = mBluetoothGatt.getService(uuid_service);
        if (service != null) {
            BluetoothGattCharacteristic blechar = service.getCharacteristic(uuid_characteristic);
            if (blechar != null) {
                mBluetoothGatt.readCharacteristic(blechar);
            }
        }
    }

    /**
     * Retrieves a list of supported GATT services on the connected device. This should be
     * invoked only after {@code BluetoothGatt#discoverServices()} completes successfully.
     *
     * @return A {@code List} of supported services.
     */
    public List<BluetoothGattService> getSupportedGattServices() {
        if (mBluetoothGatt == null) return null;

        return mBluetoothGatt.getServices();
    }

    private boolean sendCheck(int rID) {
        switch (rID) {
            case 48:
                Log.d(TAG, "error_48_DSM_information: ");
                return true;
            case 49:
                Log.d(TAG, "error_49_DSM_detection_data: ");
                return true;
            case 50:
                Log.d(TAG, "error_50_volume_level_response: ");
                return true;
            case 51:
                Log.d(TAG, "error_51_sensitivity_level_response: ");
                return true;
            case 52:
                Log.d(TAG, "error_52_setup_data: ");
                return true;
            default:
                return false;
        }
    }

    public void sendData(byte[] request) {

        Log.d(TAG, "send:" + Arrays.toString(request) + " " + isConnected);
        //characteristic.setValue(request);
        final int responseID = request[0];
        //Log.d(TAG, "receive: "+responseID);
        if (sendCheck(responseID)) {
            return;
        }
        writeCharacteristic(UUID.fromString(CommonConstance.GATT_SERVICE_UUID), UUID.fromString(CommonConstance.GATT_ATTRIBUTE_DATA_UUID), request);
        try {
            Thread.sleep(500);
            //mBluetoothLeService.readCharacteristic(UUID.fromString(GATT_SERVICE_UUID),UUID.fromString(GATT_ATTRIBUTE_DATA_UUID));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //SCAN


    public void scanLeDevice(final boolean enable) {
        if (enable) {
            mBluetoothAdapter.startLeScan(mLeScanCallback);
        } else {
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }
        //SCAN
    }


    int i = 0;
    // Device scan callback.
    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {
                @Override
                public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
                    String DeivceName = DataPreferenceManager.getInstance(BluetoothLeService.this).getDataStringFromHolder(DataPreferenceManager.PREFS_DEVICE_NAME);
                    Log.d(TAG, "onLeScan: " + device.getName() + " " + i++ + " " + DeivceName + " " + isAutoConnect+" "+PRE_FIX_DEVICE_NAME);
                    if (device.getName() != null && device.getName().contains(PRE_FIX_DEVICE_NAME)) {
                        addDevice(device);
                        broadcastUpdate(ACTION_ADD_DEVICE_SCAN, device);
                    }

                    if (!TextUtils.isEmpty(device.getName()) && device.getName().equals(DeivceName) && isAutoConnect) {
                        connect(device);
                        scanLeDevice(false);
                    }
                }
            };

    public void addDevice(BluetoothDevice device) {
        if (!bluetoothDevices.contains(device)) {
            Log.d("TAG", "addDevice: " + device.getName() + "-" + device.getAddress());
            bluetoothDevices.add(device);
        }
    }

    public void startScanIfNeed() {
        scanLeDevice(false);
        scanLeDevice(true);
    }

    public void setAutoConnect(boolean isAutoConnect) {
        this.isAutoConnect = isAutoConnect;
    }
}
