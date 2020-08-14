package com.hitomi.data;

import android.bluetooth.BluetoothGattCharacteristic;
import android.util.Log;

import static android.content.ContentValues.TAG;

public class DSM_Data {

    private BluetoothGattCharacteristic gatt_characteristic;

    private int volumeLv;
    private int sensitivityLv;
    private int att_check_val;
    private int speed_check_val;
    private boolean att_check_on_off;
    private boolean speed_check_on_off;
    private String ver_arr;
    //
    private boolean drowsy_check;
    private boolean attention_check;
    private int face_area_x_potion;
    private int face_area_y_potion;
    private int face_area_widht;
    private int face_area_height;
    private String face_direction_left_right = "";
    private String face_direction_up_down = "";

    public BluetoothGattCharacteristic getCharacteristic(){
        return gatt_characteristic;
    }
    public int getVolumeLv() {
        return volumeLv;
    }
    public int getSensitivityLv(){
        return sensitivityLv;
    }
    public int getAtt_check_val(){
        return  att_check_val;
    }
    public int getSpeed_check_val(){
        return speed_check_val;
    }
    public boolean getAtt_check_on_off(){
        return att_check_on_off;
    }
    public boolean getSpeed_check_on_off(){
        return speed_check_on_off;
    }
    public String getVer_arr(){
        return ver_arr;
    }

    public boolean getDrowsy_check(){
        return drowsy_check;
    }
    public boolean getAttention_check(){
        return attention_check;
    }
    public int getFace_area_x_potion(){
        return face_area_x_potion;
    }
    public int getFace_area_y_potion(){
        return face_area_y_potion;
    }
    public int getFace_area_widht(){
        return face_area_widht;
    }
    public int getFace_area_height(){
        return face_area_height;
    }
    public String getFace_direction_left_right(){
        return face_direction_left_right;
    }
    public String getFace_direction_up_down(){
        return face_direction_up_down;
    }

    public void setGattCharacteristic(BluetoothGattCharacteristic setChara){
        Log.d(TAG, "setCharacteristic:"+setChara);
        this.gatt_characteristic = setChara;
    }
    public void setVolumeLv(int setValue) {
        Log.d(TAG, "setVolumeLv:"+setValue);
        volumeLv = setValue;
    }
    public void setSensitivityLv(int setValue) {
        Log.d(TAG, "setSensitivityLv:"+setValue);
        sensitivityLv = setValue;
    }
    public void setAtt_check_val(int setValue) {
        Log.d(TAG, "setAtt_check_val:"+setValue);
        att_check_val = setValue;
    }
    public void setSpeed_check_val(int setValue) {
        Log.d(TAG, "setSpeed_check_val:"+setValue);
        speed_check_val = setValue;
    }
    public void setAtt_check_on_off(boolean setBool) {
        Log.d(TAG, "setAtt_check_on_off:"+setBool);
        att_check_on_off = setBool;
    }
    public void setSpeed_check_on_off(boolean setBool){
        Log.d(TAG, "setSpeed_check_on_off:"+setBool);
        speed_check_on_off = setBool;
    }
    public void setVer_arr(String setString) {
        Log.d(TAG, "setVer_arr:"+setString);
        ver_arr = setString;
    }

    public void setDrowsy_check(boolean setBool) {
        Log.d(TAG, "setDrowsy_check:"+setBool);
        drowsy_check = setBool;
    }
    public void setAttention_check(boolean setBool){
        Log.d(TAG, "setAttention_check:"+setBool);
        attention_check = setBool;
    }
    public void setFace_area_x_potion(int setValue) {
        Log.d(TAG, "setFace_area_x_potion:"+setValue);
        face_area_x_potion = setValue;
    }
    public void setFace_area_y_potion(int setValue) {
        Log.d(TAG, "setFace_area_y_potion:"+setValue);
        face_area_y_potion = setValue;
    }
    public void setFace_area_widht(int setValue) {
        Log.d(TAG, "setFace_area_widht:"+setValue);
        face_area_widht = setValue;
    }
    public void setFace_area_height(int setValue) {
        Log.d(TAG, "setFace_area_height:"+setValue);
        face_area_height = setValue;
    }
    public void setFace_direction_left_right(String setString) {
        Log.d(TAG, "setFace_direction_left_right:"+setString);
        face_direction_left_right = setString;
    }
    public void setFace_direction_up_down(String setString) {
        Log.d(TAG, "setFace_direction_up_down:"+setString);
        face_direction_up_down = setString;
    }

}
