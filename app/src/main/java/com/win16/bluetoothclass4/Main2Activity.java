package com.win16.bluetoothclass4;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.fe.library.TabContainerView;
import com.fe.library.adapter.DefaultAdapter;
import com.win16.bluetoothclass4.connect.ConnectThread;
import com.win16.bluetoothclass4.connect.Constant;
import com.win16.bluetoothclass4.fragment.ControlFragment;
import com.win16.bluetoothclass4.fragment.TemControlFragment;

import java.io.UnsupportedEncodingException;


public class Main2Activity extends AppCompatActivity {

    private TabContainerView tabContainerView;
    private int[] iconImageArray,selectedIconImageArray;
    private Fragment[] fragments;
    private int[][] mIconArray;
    private ConnectThread mConnectThread;
    private Toast mToast;
    private BlueToothController mController = new BlueToothController();
    private Handler mUIHandler = new MyHandler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Intent intent = getIntent();
        BluetoothDevice device = intent.getParcelableExtra("device");
        if( mConnectThread != null) {
            mConnectThread.cancel();
        }
        mConnectThread = new ConnectThread(device, mController.getAdapter(), mUIHandler);
        mConnectThread.start();
        initView();
    }
    private void initView(){

        mIconArray = new int[][]{{R.drawable.icon_main, R.drawable.icon_main_selected},
                {R.drawable.icon_app, R.drawable.icon_app_selected}};
        iconImageArray = new int[]{R.drawable.icon_main,R.drawable.icon_app};
        selectedIconImageArray = new int[]{R.drawable.icon_main_selected,R.drawable.icon_app_selected};
        fragments = new Fragment[]{new ControlFragment(),new TemControlFragment()};
        tabContainerView = (TabContainerView)findViewById(R.id.tab_containerview_main);
        tabContainerView.setAdapter(new DefaultAdapter(this,fragments,getSupportFragmentManager()
                ,getResources().getStringArray(R.array.titleArray),getResources().getColor(R.color.colorPrimary),iconImageArray,selectedIconImageArray));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if( mConnectThread != null) {
            mConnectThread.cancel();
        }
    }
    private void showToast(String text) {

        if( mToast == null) {
            mToast = Toast.makeText(this, text, Toast.LENGTH_LONG);
        }
        else {
            mToast.setText(text);
        }
        mToast.show();
    }





    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case Constant.MSG_START_LISTENING:
                    setProgressBarIndeterminateVisibility(true);
                    break;
                case Constant.MSG_FINISH_LISTENING:
                    setProgressBarIndeterminateVisibility(false);
                    break;
                case Constant.MSG_GOT_DATA:
                    showToast("data: "+String.valueOf(msg.obj));
                    break;
                case Constant.MSG_ERROR:
                    showToast("error: "+String.valueOf(msg.obj));
                    break;
                case Constant.MSG_CONNECTED_TO_SERVER:
                    showToast("Connected to Server");
                    break;
                case Constant.MSG_GOT_A_CLINET:
                    showToast("Got a Client");
                    break;
            }
        }
    }
    public void say(String word) {
        if( mConnectThread != null) {
            try {
                mConnectThread.sendData(word.getBytes("utf-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }
}
