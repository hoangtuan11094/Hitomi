package com.hitomi.adapters;

import android.bluetooth.BluetoothDevice;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hitomilib.BluetoothLeService;
import com.hitomi.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class DeviceAdapter extends RecyclerView.Adapter<DeviceAdapter.DeviceViewHolder> {


    private OnClickItemListener onClickItemListener;

    public DeviceAdapter(OnClickItemListener onClickItemListener) {
        this.onClickItemListener = onClickItemListener;
    }

    public void addDevice() {
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public DeviceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.i_device, parent, false);
        return new DeviceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DeviceViewHolder holder, int position) {
        BluetoothDevice bluetoothDevice = BluetoothLeService.bluetoothDevices.get(position);
        final String deviceName = bluetoothDevice.getName();
        if (deviceName != null && deviceName.length() > 0)
            holder.tvName.setText(bluetoothDevice.getName());
        else
            holder.tvName.setText(R.string.unknown_device);

        holder.tvMac.setText(bluetoothDevice.getAddress());
        holder.llRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onClickItemListener != null) {
                    onClickItemListener.clickItem(bluetoothDevice);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return BluetoothLeService.bluetoothDevices == null ? 0 : BluetoothLeService.bluetoothDevices.size();
    }

    public class DeviceViewHolder extends RecyclerView.ViewHolder {
        private TextView tvName, tvMac;
        private LinearLayout llRoot;

        public DeviceViewHolder(@NonNull View itemView) {
            super(itemView);
            llRoot = itemView.findViewById(R.id.llRoot);
            tvName = itemView.findViewById(R.id.tvName);
            tvMac = itemView.findViewById(R.id.tvMac);
        }
    }

    public interface OnClickItemListener {
        void clickItem(BluetoothDevice device);
    }
}
