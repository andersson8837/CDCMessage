
package com.example.cdcmessage.cdcmessage;

import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.AndroidRuntimeException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.content.Intent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cdcmessage.cdcmessage.*;
import com.example.cdcmessage.cdcmessage.customviews.MySheet;
import com.example.cdcmessage.cdcmessage.model.StatusMessage;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    /*
     * Notifications from UsbService will be received here.
     */
    private UsbService usbService;
    private MyHandler mHandler;
    private Timer statusTimer;
    private StatusRequestTimerTask statusTask;

    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case UsbService.ACTION_USB_PERMISSION_GRANTED: // USB PERMISSION GRANTED
//                    statusTimer.schedule(statusTask, 0, 10000);
                    Toast.makeText(context, "USB Ready", Toast.LENGTH_SHORT).show();
                    break;
                case UsbService.ACTION_USB_PERMISSION_NOT_GRANTED: // USB PERMISSION NOT GRANTED
                    Toast.makeText(context, "USB Permission not granted", Toast.LENGTH_SHORT).show();
                    break;
                case UsbService.ACTION_NO_USB: // NO USB CONNECTED
                    Toast.makeText(context, "No USB connected", Toast.LENGTH_SHORT).show();
                    break;
                case UsbService.ACTION_USB_DISCONNECTED: // USB DISCONNECTED
                    Toast.makeText(context, "USB disconnected", Toast.LENGTH_SHORT).show();
                    break;
                case UsbService.ACTION_USB_NOT_SUPPORTED: // USB NOT SUPPORTED
                    Toast.makeText(context, "USB device not supported", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    private final ServiceConnection usbConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName arg0, IBinder arg1) {
            usbService = ((UsbService.UsbBinder) arg1).getService();
            usbService.setHandler(mHandler);
            Messages.initObject(usbService);
            Messages.send_init(usbService);
//            Log.e("Messages", usbService==null?"11null":"notnull");
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            usbService = null;
        }
    };

    /////////////////////View///////////////////////////////
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    /*private static TableFragment tableFragment = null;
    private static MessagesFragment messagesFragment = null;*/
    private static final int PICKFILE_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mHandler = new MyHandler(this);
        statusTimer = new Timer("StatusRequestTimer");
        statusTask = new StatusRequestTimerTask();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        /*tableFragment = (TableFragment)getSupportFragmentManager().getFragments().get(0);
        messagesFragment = (MessagesFragment)getSupportFragmentManager().getFragments().get(1);*/
        statusTimer.schedule(statusTask, 0, 10000);
        /////////////////////////////////////////////////
        Log.e(Consts.LOGMESSAGE, "MainActivity : onCreate");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();;
        Log.e(Consts.LOGMESSAGE, "MainActivity : onDestroy");
        statusTask.cancel();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_loadcsv) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_GET_CONTENT);
            Uri uri = Uri.parse(Environment.getExternalStorageDirectory().getPath());
            intent.setDataAndType(uri, "file/csv");
            try {
                startActivityForResult(intent, PICKFILE_REQUEST_CODE);
            } catch (ActivityNotFoundException e) {
                Log.e(Consts.LOGMESSAGE, "MainActivity : No activity can handle picking a file. Showing alternatives.");
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Fix no activity available
        if (data == null)
            return;
        switch (requestCode) {
            case PICKFILE_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    Uri uri = data.getData();
                    try {
                        TableFragment tableFragment = (TableFragment)this.getSupportFragmentManager().getFragments().get(0);
                        tableFragment.setData(uri);
                    } catch(NullPointerException e) {
                        Log.e(Consts.LOGMESSAGE, "MainActivity : TableFragment is Null");
                    }
                }
        }
    }

    private class StatusRequestTimerTask extends TimerTask {
        public void run() {
            if ( Messages.sendInterrogationMessage() == Consts.USBSERVICE_ERROR ) {
                Messages.send_init(usbService);
            }
            //Log.e(Consts.LOGMESSAGE, "MainActivity : InterrogationMessage");

        }
    }
    @Override
    public void onResume() {
        super.onResume();
        setFilters();  // Start listening notifications from UsbService
        startService(UsbService.class, usbConnection, null); // Start UsbService(if it was not started before) and Bind it
        /*Messages.initObject(usbService);
        Messages.send_init(usbService);*/
        Log.e(Consts.LOGMESSAGE, "MainActivity : onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(mUsbReceiver);
        unbindService(usbConnection);
        Log.e(Consts.LOGMESSAGE, "MainActivity : onPause");
    }

    private void startService(Class<?> service, ServiceConnection serviceConnection, Bundle extras) {
        if (!UsbService.SERVICE_CONNECTED) {
            Intent startService = new Intent(this, service);
            if (extras != null && !extras.isEmpty()) {
                Set<String> keys = extras.keySet();
                for (String key : keys) {
                    String extra = extras.getString(key);
                    startService.putExtra(key, extra);
                }
            }
            startService(startService);
        }
        Intent bindingIntent = new Intent(this, service);
        bindService(bindingIntent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    private void setFilters() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(UsbService.ACTION_USB_PERMISSION_GRANTED);
        filter.addAction(UsbService.ACTION_NO_USB);
        filter.addAction(UsbService.ACTION_USB_DISCONNECTED);
        filter.addAction(UsbService.ACTION_USB_NOT_SUPPORTED);
        filter.addAction(UsbService.ACTION_USB_PERMISSION_NOT_GRANTED);
        registerReceiver(mUsbReceiver, filter);
    }

    /*
      * This handler will be passed to UsbService. Data received from serial port is displayed through this handler
      */
    private static class MyHandler extends Handler {
        private final WeakReference<MainActivity> mActivity;

        public MyHandler(MainActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UsbService.MESSAGE_FROM_SERIAL_PORT:
                    //String data = (String) msg.obj;
                    byte[] data = (byte[]) msg.obj;
                    MessagesFragment messagesFragment = (MessagesFragment)mActivity.get().getSupportFragmentManager().getFragments().get(1);
                    switch(Messages.checkMessageType(data)) {
                        case Consts.NULL_MESSAGE:
                            Log.e(Consts.LOGMESSAGE, "NULL_MESSAGE");
                            break;
                        case Consts.STATUS_DEVICE_MESSAGE:
                            Log.e(Consts.LOGMESSAGE, "STATUS_DEVICE_MESSAGE");
                            StatusMessage statusMessage = Messages.getStatusMessage(data);
                            messagesFragment.onMessage(statusMessage, false);
                            Messages.onMessage(data);
                            break;
                        case Consts.MATRIX_A_MESSAGE:
                            Log.e(Consts.LOGMESSAGE, "MATRIX_A_MESSAGE");
                            StatusMessage statusMessage1 = Messages.getMatrixAMessage(data);
                            messagesFragment.onMessage(statusMessage1, true);
                            Messages.onMessage(data);
                            break;
                        default:
                            Messages.onMessage(data);
                            break;
                    }
                    break;
            }
        }
    }

    public void sendProgramMessages() {
        TableFragment tableFragment = (TableFragment)getSupportFragmentManager().getFragments().get(0);
        ArrayList<int[]> messages = tableFragment.getProgramMessages();
        int id, i, j=0;
        for (id=Consts.MIN_ID; id<Consts.MAX_ID; id++) {
            for (i = j; i < messages.size(); i++) {
                if (messages.get(i)[0] == id) {
                    int[] temp = messages.get(j);
                    messages.set(j, messages.get(i));
                    messages.set(i, temp);
                    j++;
                }
            }
        }
        //Log.e("SendProgramMessages", String.format("%d", messages.size()));
        Messages.sendProgramMessages(messages);
        //Toast.makeText(this, tableFragment.getProgramMessages(), Toast.LENGTH_SHORT).show();
    }



}
