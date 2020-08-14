package com.hitomi.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.hitomi.R;
import com.hitomi.activities.MainActivity;

import androidx.fragment.app.Fragment;

import static com.hitomi.constans.FragmentConstans.FRAGMENT_BASE_SETTING;
import static com.hitomi.constans.FragmentConstans.FRAGMENT_FUNCTION_SETTING;

/**
 * A simple {@link Fragment} subclass.
 */
public class FrmSettingMenu extends BaseFragment implements View.OnClickListener {

    private Button btnSettingBase, btnSettingFunc, btnBack;

    public FrmSettingMenu() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frm_setting_menu, container, false);
        init(view);
        return view;
    }

    private void init(View view) {
        btnSettingBase = view.findViewById(R.id.btnSettingBase);
        btnSettingFunc = view.findViewById(R.id.btnSettingFunc);
        btnBack = view.findViewById(R.id.btnBack);
        //
        btnSettingBase.setOnClickListener(this);
        btnSettingFunc.setOnClickListener(this);
        btnBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btnSettingBase:
                fragmentListener.onFragmentChange(FRAGMENT_BASE_SETTING);
                break;
            case R.id.btnSettingFunc:
                fragmentListener.onFragmentChange(FRAGMENT_FUNCTION_SETTING);
                break;
            case R.id.btnBack:
                if (getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).backStack();
                }
                break;
        }
    }
}
