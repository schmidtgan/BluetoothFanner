package com.win16.bluetoothclass4;

import android.annotation.TargetApi;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.fe.library.TabContainerView;
import com.win16.bluetoothclass4.connect.ConnectThread;
import com.win16.bluetoothclass4.connect.Constant;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rex on 2015/5/27.
 */
public class MainActivity extends AppCompatActivity{


    public static final int REQUEST_CODE = 0;
    private List<BluetoothDevice> mDeviceList = new ArrayList<>();
    private List<BluetoothDevice> mBondedDeviceList = new ArrayList<>();
    private BlueToothController mController = new BlueToothController();
    private ListView mListView;
    private DeviceAdapter mAdapter;
    private Toast mToast;

    private ConnectThread mConnectThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("ATG","fdasjlkfjkalsdjlfasdjflkjadslkfjaslkdjflkasjdfklsnkfjnskjnc,jbkjdnakjfnkdsjflknlk");

        setContentView(R.layout.activity_main);
        initUI();

        registerBluetoothReceiver();
        mController.turnOnBlueTooth(this, REQUEST_CODE);

    }

    private void registerBluetoothReceiver() {
        IntentFilter filter = new IntentFilter();
        //开始查找
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        //结束查找
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        //查找设备
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        //设备扫描模式改变
        filter.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
        //绑定状态
        filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);

        registerReceiver(mReceiver, filter);
    }

    private Handler mUIHandler = new MyHandler();

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if( BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action) ) {
                setProgressBarIndeterminateVisibility(true);
                //初始化数据列表
                mDeviceList.clear();
                mAdapter.notifyDataSetChanged();
            }
            else if( BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                setProgressBarIndeterminateVisibility(false);
            }
            else if( BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                //找到一个，添加一个
                mDeviceList.add(device);
                mAdapter.notifyDataSetChanged();
            }
            else if( BluetoothAdapter.ACTION_SCAN_MODE_CHANGED.equals(action)) {
               int scanMode = intent.getIntExtra(BluetoothAdapter.EXTRA_SCAN_MODE,0);
                if( scanMode == BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
                    setProgressBarIndeterminateVisibility(true);
                }
                else {
                    setProgressBarIndeterminateVisibility(false);
                }
            }
            else if( BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action) ) {
                BluetoothDevice remoteDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if( remoteDevice == null ) {
                    showToast("no device");
                    return;
                }
                int status = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE,0);
                if( status == BluetoothDevice.BOND_BONDED) {
                    showToast("Bonded " + remoteDevice.getName());
                }
                else if( status == BluetoothDevice.BOND_BONDING){
                    showToast("Bonding " + remoteDevice.getName());
                }
                else if(status == BluetoothDevice.BOND_NONE){
                    showToast("Not bond " + remoteDevice.getName());
                }
            }
        }
    };


    private void initUI() {
        mListView = (ListView) findViewById(R.id.device_list);
        mAdapter = new DeviceAdapter(mDeviceList, this);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(bindDeviceClick);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if( mConnectThread != null) {
            mConnectThread.cancel();
        }

        unregisterReceiver(mReceiver);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        if( id == R.id.find_device) {
            //查找设备
            mAdapter.refresh(mDeviceList);
            mController.findDevice();
            mListView.setOnItemClickListener(bindDeviceClick);
        }

        return super.onOptionsItemSelected(item);
    }

    private AdapterView.OnItemClickListener bindedDeviceClick = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            BluetoothDevice device = mBondedDeviceList.get(i);
            if( mConnectThread != null) {
                mConnectThread.cancel();
            }
            mConnectThread = new ConnectThread(device, mController.getAdapter(), mUIHandler);
            mConnectThread.start();
        }
    };

    private AdapterView.OnItemClickListener bindDeviceClick = new AdapterView.OnItemClickListener() {
        @TargetApi(Build.VERSION_CODES.KITKAT)
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            BluetoothDevice device = mDeviceList.get(i);
            if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                device.createBond();
                Intent intent = new Intent(MainActivity.this,Main2Activity.class);
                intent.putExtra("device",device);
                startActivity(intent);
            }
        }
    };




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


}
