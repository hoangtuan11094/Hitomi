package com.hitomilib.data;

import android.content.Context;
import android.content.SharedPreferences;

public class DataPreferenceManager {

    private static DataPreferenceManager _instance;

    public static final String PREFS_NAME = "_pref_app-Hitomi_";

    public static final String PREFS_DEVICE_NAME = "device_name";

    private static SharedPreferences settings;

    public static DataPreferenceManager getInstance() {
        if (_instance == null) {
            _instance = new DataPreferenceManager();

        }
        return _instance;
    }

    public static DataPreferenceManager getInstance(Context context) {
        settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        if (_instance == null) {
            _instance = new DataPreferenceManager();
        }
        return _instance;
    }

    public DataPreferenceManager setPreferences(Context context) {
        settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        if (_instance == null) {
            _instance = new DataPreferenceManager();
        }
        return _instance;
    }

    public DataPreferenceManager setPreferences(SharedPreferences sharedPreference) {
        settings = sharedPreference;
        if (_instance == null) {
            _instance = new DataPreferenceManager();
        }
        return _instance;
    }

    public SharedPreferences getPreferences() {
        return settings;
    }

    public void writeBooleanData(String holderKey, boolean holderData) {
        try {
            SharedPreferences.Editor ed = settings.edit();
            ed.putBoolean(holderKey, holderData);
            ed.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void writeLongData(String holderKey, long holderData) {
        try {
            SharedPreferences.Editor ed = settings.edit();
            ed.putLong(holderKey, holderData);
            ed.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void writeStringData(String holderKey, String holderData) {
        try {
            SharedPreferences.Editor ed = settings.edit();
            ed.putString(holderKey, holderData);
            ed.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void writeIntData(String key, int holder) {
        try {
            SharedPreferences.Editor ed = settings.edit();
            ed.putInt(key, holder);
            ed.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getIntData(String key) {
        try {
            return settings.getInt(key, 0);
        } catch (ClassCastException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int getIntData(String key, int defaultValues) {
        int value = 0;

        try {
            value = settings.getInt(key, defaultValues);
        } catch (ClassCastException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }

    public String getDataStringFromHolder(String holderString) {
        try {
            return settings.getString(holderString, "");
        } catch (ClassCastException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getDataStringFromHolder(String holderString, String defaultVal) {
        try {
            return settings.getString(holderString, defaultVal);
        } catch (ClassCastException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return defaultVal;
    }

    public boolean getDataBooleanFromHolder(String holderString) {
        try {
            return settings.getBoolean(holderString, false);
        } catch (ClassCastException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    public boolean getDataBooleanFromHolder(String holderString, boolean defaultVal) {
        try {
            return settings.getBoolean(holderString, defaultVal);
        } catch (ClassCastException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * get long data from stored
     *
     * @param holderString key to holder data
     * @return default or error occur return -1 else return data
     * @author Atula
     */
    public long getDataLongFromHolder(String holderString) {
        try {
            return settings.getLong(holderString, -1);
        } catch (ClassCastException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }


    public long getDataLongFromHolder(String holderString, long defaultVal) {
        try {
            return settings.getLong(holderString, defaultVal);
        } catch (ClassCastException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return defaultVal;
    }


    public void releaseMemoryFromKey(String key) {
        SharedPreferences.Editor ed = settings.edit();
        ed.remove(key);
        ed.commit();
    }


    public void releaseMemory() {
        SharedPreferences.Editor ed = settings.edit();
        ed.clear();
        ed.commit();
    }
}