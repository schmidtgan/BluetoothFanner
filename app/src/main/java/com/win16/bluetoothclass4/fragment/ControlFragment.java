package com.win16.bluetoothclass4.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.win16.bluetoothclass4.Main2Activity;
import com.win16.bluetoothclass4.R;

/**
 * Created by Administrator on 2018/3/21 0021.
 */

public class ControlFragment  extends Fragment implements View.OnClickListener {
    private Button zeroBut,oneBut,twoBut,threeBut;
    Main2Activity activity;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_control, container, false);
        zeroBut= (Button) view.findViewById(R.id.zero);
        oneBut= (Button) view.findViewById(R.id.one);
        twoBut= (Button) view.findViewById(R.id.two);
        threeBut= (Button) view.findViewById(R.id.three);
        zeroBut.setOnClickListener(this);
        oneBut.setOnClickListener(this);
        twoBut.setOnClickListener(this);
        threeBut.setOnClickListener(this);
        activity = (Main2Activity)getActivity();
        return view;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id){
            case R.id.zero:
                activity.say("a");
                break;
            case R.id.one:
                activity.say("b");
                break;
            case R.id.two:
                activity.say("c");
                break;
            case R.id.three:
                activity.say("d");
                break;
            default:
                break;
        }
    }
}
