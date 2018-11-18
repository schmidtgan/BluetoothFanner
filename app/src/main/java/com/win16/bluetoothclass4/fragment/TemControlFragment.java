package com.win16.bluetoothclass4.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.win16.bluetoothclass4.Main2Activity;
import com.win16.bluetoothclass4.R;

/**
 * Created by Administrator on 2018/3/21 0021.
 */

public class TemControlFragment extends Fragment {
    private EditText oneText,twoText,threeText;
    private Button confirm;
    Main2Activity activity;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_temcontrol, container, false);
        oneText = (EditText)view.findViewById(R.id.editText1);
        twoText = (EditText)view.findViewById(R.id.editText2);
        threeText = (EditText)view.findViewById(R.id.editText3);
        confirm = (Button)view.findViewById(R.id.button);
        activity = (Main2Activity)getActivity();
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tem1= oneText.getText().toString();
                String tem2 = twoText.getText().toString();
                String tem3 = threeText.getText().toString();
                String jsonData = JsonArrayPackage(tem1,tem2,tem3);
                activity.say(jsonData);

            }
        });
        return view;
    }
    private String JsonArrayPackage(String tem1,String tem2,String tem3){
        String jsondata = "{'tem1':" + tem1 + ",'tem2':" + tem2 + ",'tem3':" + tem3 + "}";
        return jsondata;
    }
}
