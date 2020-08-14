package com.hitomi.fragments;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.hitomi.R;
import com.hitomi.activities.MainActivity;
import com.hitomi.adapters.DeviceAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * A simple {@link Fragment} subclass.
 */
public class FrmListDevice extends BaseFragment implements View.OnClickListener {

    private Button btnBack;
    private RecyclerView rcDevice;
    private DeviceAdapter deviceAdapter;

    public FrmListDevice() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frm_list_device, container, false);
        init(view);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rcDevice.setLayoutManager(new LinearLayoutManager(getActivity(),RecyclerView.VERTICAL,false));
        deviceAdapter = new DeviceAdapter(new DeviceAdapter.OnClickItemListener() {
            @Override
            public void clickItem(BluetoothDevice device) {
                if (getActivity() instanceof MainActivity){
                    Log.d(TAG, "clickItem: "+device.getName()+" "+device.getAddress());
                    ((MainActivity) getActivity()).connectBluetooth(device);
                }
            }
        });
        rcDevice.setAdapter(deviceAdapter);

    }

    private void init(View view) {
        rcDevice = view.findViewById(R.id.rcDevice);
        btnBack = view.findViewById(R.id.btnBack);
        btnBack.setOnClickListener(this);
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

    public void addDeviceToList(){
        if (deviceAdapter!=null){
            deviceAdapter.addDevice();
        }
    }


}
