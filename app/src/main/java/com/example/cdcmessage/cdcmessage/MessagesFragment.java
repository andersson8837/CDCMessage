package com.example.cdcmessage.cdcmessage;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cdcmessage.cdcmessage.model.StatusMessage;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Set;

/**
 * Created by Administrator on 1/30/2017.
 */

public class MessagesFragment extends Fragment {
    /*
     * View Controls
     */
    ListView mMsglist;
    Button mSendbtn;

    StatusDeviceAdapter mAdapter;
    ArrayList<StatusMessage> mStatusMessageList = new ArrayList<StatusMessage>();

    Button mLeftbtn, mRightbtn;
    int mCurState;

    public MessagesFragment() {
        super();
    }
    public static MessagesFragment newInstance() {
        MessagesFragment fragment = new MessagesFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*StatusMessage temp = new StatusMessage();
        temp.id = 12;
        temp.JE = true;
        temp.IP = false;
        temp.PWEN = true;
        temp.IST = false;
        temp.Res = true;
        temp.CAN = true;
        temp.ST = 0;
        temp.PM = false;
        temp.GPS = 1;
        temp.NU = 100;
        temp.BIST = 8;
        temp.BEST = 250;
        temp.WIR = 80;
        mStatusMessageList.add(temp);*/

        mCurState = Consts.MACHINESTATE_NONE;
        mAdapter = new StatusDeviceAdapter();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_message, container, false);
        mMsglist = (ListView) rootView.findViewById(R.id.inbox);
        mMsglist.setAdapter(mAdapter);

        /////UsbConnection/////
        mSendbtn = (Button) rootView.findViewById(R.id.sendmsgbtn);
        mSendbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //        ((MainActivity)getActivity()).sendInitMessage();
            ((MainActivity)getActivity()).sendProgramMessages();
            }
        });
        mLeftbtn = (Button) rootView.findViewById(R.id.leftbtn);
        mLeftbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //        ((MainActivity)getActivity()).sendInitMessage();
                //Log.e(Consts.LOGMESSAGE, Consts.machinestateDesc[mCurState-2000]);
                switch (mCurState) {
                    case Consts.MACHINESTATE_CREATENETWORK:
                        break;
                    case Consts.MACHINESTATE_IDLE:

                        setState(Consts.MACHINESTATE_CREATENETWORK);
                        Messages.sendJoinEnableMessage();
                        break;
                    case Consts.MACHINESTATE_ARM:

                        setState(Consts.MACHINESTATE_IDLE);
                        Messages.sendPowerDisableMessage();
                        break;
                    case Consts.MACHINESTATE_PLAY:

                        setState(Consts.MACHINESTATE_IDLE);
                        Messages.sendStopMessage();
                        break;
                    case Consts.MACHINESTATE_PAUSE:

                        setState(Consts.MACHINESTATE_IDLE);
                        Messages.sendStopMessage();
                        break;
                    case Consts.MACHINESTATE_NONE:
                        break;
                }
            }
        });

        mRightbtn = (Button) rootView.findViewById(R.id.rightbtn);
        mRightbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //        ((MainActivity)getActivity()).sendInitMessage();
                switch (mCurState) {
                    case Consts.MACHINESTATE_CREATENETWORK:
                        setState(Consts.MACHINESTATE_CREATENETWORK);
                        Messages.sendJoinDisableMessage();
                        break;
                    case Consts.MACHINESTATE_IDLE:
                        setState(Consts.MACHINESTATE_ARM);
                        Messages.sendPowerEnableMessage();
                        break;
                    case Consts.MACHINESTATE_ARM:
                        setState(Consts.MACHINESTATE_PLAY);
                        Messages.sendStartMessage();
                        break;
                    case Consts.MACHINESTATE_PLAY:
                        setState(Consts.MACHINESTATE_PAUSE);
                        Messages.sendPauseMessage();
                        break;
                    case Consts.MACHINESTATE_PAUSE:
                        setState(Consts.MACHINESTATE_PLAY);
                        Messages.sendStartMessage();
                        break;
                    case Consts.MACHINESTATE_NONE:
                        break;
                }
            }
        });

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        Log.e(Consts.LOGMESSAGE, "MessagesFragment : onSaveInstanceState");
        outState.putInt("State", mCurState);
    }

    @Override
    public void onViewStateRestored(Bundle inState){
        super.onViewStateRestored(inState);
        Log.e(Consts.LOGMESSAGE, "MessagesFragment : onViewStateRestored");
        try {
            mCurState = inState.getInt("State");
        } catch (NullPointerException e) {
            mCurState = Consts.MACHINESTATE_NONE;
        }
        setState(mCurState);
    }

    public void onMessage(StatusMessage msg, boolean flag) {
        if (msg == null) {
            Log.e(Consts.LOGMESSAGE, "MessagesFragment : StatusMessage is null");
            return;
        }
        int i;
        for (i=0; i<mStatusMessageList.size(); i++) {
            if (msg.id == mStatusMessageList.get(i).id) break;
        }
        if (i<mStatusMessageList.size()) {
            if (flag) {
                mStatusMessageList.get(i).matrixA1 = msg.matrixA1; //matrixA1
                mStatusMessageList.get(i).matrixA2 = msg.matrixA2; //matrixA2
            }
            else {  //Device Status Message
                msg.matrixA1 = mStatusMessageList.get(i).matrixA1;
                msg.matrixA2 = mStatusMessageList.get(i).matrixA2;
                mStatusMessageList.set(i, msg);
            }
        } else {
            mStatusMessageList.add(msg);
        }
        /*Log.e(Consts.LOGMESSAGE, "Matrix A1:");
        StatusMessage temp = mStatusMessageList.get(i);
        String logee = "";
        for (int j=0; j<temp.matrixA1.length; j++) {
            logee = "";
            for (int k=0; k<temp.m status mesage after atrixA1[j].length; k++)
                logee += String.format("%d ", temp.matrixA1[j][k]);
            Log.e(Consts.LOGMESSAGE, logee);
        }

        Log.e(Consts.LOGMESSAGE, "Matrix A2:");
        logee = "";
        for (int j=0; j<temp.matrixA2.length; j++) {
            logee += String.format("%d ", temp.matrixA2[j]);
        }
        Log.e(Consts.LOGMESSAGE, logee);*/

        mAdapter.notifyDataSetChanged();
        //Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }

    public void setState(int newState) {
        mCurState = newState;
        switch (mCurState) {
            case Consts.MACHINESTATE_CREATENETWORK:
                mLeftbtn.setEnabled(false);
                mLeftbtn.setEnabled(true);
                mRightbtn.setText("IDLE");
                break;
            case Consts.MACHINESTATE_IDLE:
                mLeftbtn.setEnabled(true);
                mLeftbtn.setText("Create Network");
                mRightbtn.setEnabled(true);
                mRightbtn.setText("Arm");
                break;
            case Consts.MACHINESTATE_ARM:
                mLeftbtn.setEnabled(true);
                mLeftbtn.setText("IDLE");
                mRightbtn.setEnabled(true);
                mRightbtn.setText("Play");
                break;
            case Consts.MACHINESTATE_PLAY:
                mLeftbtn.setEnabled(true);
                mLeftbtn.setText("IDLE");
                mRightbtn.setEnabled(true);
                mRightbtn.setText("Pause");
                break;
            case Consts.MACHINESTATE_PAUSE:
                mLeftbtn.setEnabled(true);
                mLeftbtn.setText("IDLE");
                mRightbtn.setEnabled(true);
                mRightbtn.setText("Play");
                break;
            case Consts.MACHINESTATE_NONE:
                mLeftbtn.setEnabled(false);
                mRightbtn.setEnabled(false);
                break;
        }
    }

    public class StatusDeviceAdapter extends BaseAdapter {
        public StatusDeviceAdapter() { super(); }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.statusrow, parent, false);
            }
            StatusMessage row = mStatusMessageList.get(position);

            TextView tmp;
            // Set Interface Name according to its ID.
            tmp = (TextView)convertView.findViewById(R.id.interface_name);
            tmp.setText(row.id==0?"FTM-99":"FTH-48");

            // ID
            tmp = (TextView)convertView.findViewById(R.id.identifier);
            tmp.setText(String.format("%d", row.id));

            // PRG
            tmp = (TextView)convertView.findViewById(R.id.prg);
            tmp.setText(row.IP?"PRG":"");

            // Int Battery
            ImageView imgtmp = (ImageView) convertView.findViewById(R.id.intbattery);
            int[] batteryList = new int[6];
            batteryList[0] = R.drawable.level0;
            batteryList[1] = R.drawable.level1;
            batteryList[2] = R.drawable.level2;
            batteryList[3] = R.drawable.level3;
            batteryList[4] = R.drawable.level4;
            batteryList[5] = R.drawable.level5;

            if (row.BIST==0) {
                imgtmp.setImageDrawable(getResources().getDrawable(batteryList[0]));
            }
            else {
                imgtmp.setImageDrawable(getResources().getDrawable(batteryList[row.BIST/2+1]));
            }

            // Ext Battery
            tmp = (TextView) convertView.findViewById(R.id.extbattery);
            if (row.BEST < 5) {
                tmp.setText("");
                tmp.setBackgroundDrawable(getResources().getDrawable(R.drawable.level0));
            } else {
                tmp.setText(String.format("%.1fV", (float)row.BEST/10));
                tmp.setBackgroundDrawable(getResources().getDrawable(R.drawable.battery));
            }

            // Two Wire
            imgtmp = (ImageView) convertView.findViewById(R.id.twowire);
            if (row.CAN) imgtmp.setImageDrawable(getResources().getDrawable(R.drawable.wifi_connected));
            else imgtmp.setImageDrawable(getResources().getDrawable(R.drawable.wifi_disconnected));

            // Wir Icon
            imgtmp = (ImageView) convertView.findViewById(R.id.wiricon);
            int imgId = -1;
            if (row.WIR == 111) imgId = R.drawable.wifi4;
            else if (row.WIR == 0) imgId = R.drawable.wifi0;
            else if (row.WIR>0 && row.WIR<=100){
                int[] wifiList = new int[5] ;
                wifiList[0] = R.drawable.wifi0;
                wifiList[1] = R.drawable.wifi1;
                wifiList[2] = R.drawable.wifi2;
                wifiList[3] = R.drawable.wifi3;
                wifiList[4] = R.drawable.wifi4;
                if (row.WIR == 100) row.WIR = 99;
                imgId = wifiList[row.WIR/20];
            }
            if (imgId>=0) imgtmp.setImageDrawable(getResources().getDrawable(imgId));

            // ST
            if (row.JE && row.IST) {
                setState(Consts.MACHINESTATE_CREATENETWORK);
                Log.e(Consts.LOGMESSAGE, "Machine State : " + Consts.machinestateDesc[Consts.MACHINESTATE_CREATENETWORK-2000]);
            } else if (row.IST) {
                setState(2000+row.ST);
                Log.e(Consts.LOGMESSAGE, "Machine State : " + Consts.machinestateDesc[row.ST]);
            }

            String state = Consts.machinestateDesc[row.ST];
            tmp = (TextView) convertView.findViewById(R.id.state);
            tmp.setText(state);

            // GPS Distance
            imgtmp = (ImageView) convertView.findViewById(R.id.gpsstatus);
            imgtmp.setImageDrawable(getResources().getDrawable(R.drawable.gps));

            //More button
            final LinearLayout matrixa = (LinearLayout) convertView.findViewById(R.id.matrixa);
            Button btn = (Button) convertView.findViewById(R.id.morebtn);
            btn.setTag("More");
            matrixa.setVisibility(View.GONE);
            btn.setBackgroundDrawable(getResources().getDrawable(android.R.drawable.arrow_down_float));

            btn.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (v.getTag() == "More") {
                        matrixa.setVisibility(View.VISIBLE);
                        v.setBackgroundDrawable(getResources().getDrawable(android.R.drawable.arrow_up_float));
                        v.setTag("Less");
                    } else {
                        matrixa.setVisibility(View.GONE);
                        v.setBackgroundDrawable(getResources().getDrawable(android.R.drawable.arrow_down_float));
                        v.setTag("More");
                    }
                }
            });

            //Matrix A

            if (row.ST == 0) {
                //Log.e("ddd", String.format("%d", row.ST));
                int[] tIdList = {R.id.T1, R.id.T2, R.id.T3, R.id.T4};
                for (int i=0; i<4; i++) {
                    LinearLayout terminalLayout = (LinearLayout)convertView.findViewById(tIdList[i]);
                    for (int j=0; j<12; j++) {
                        TextView chStatus = (TextView)terminalLayout.getChildAt(2 + j);
                        int imgId1 = 0;
                        switch(row.matrixA1[j][i]) {
                            case 0: imgId1 = row.matrixA2[j]==0?R.drawable.grey_o:R.drawable.grey_v; break;
                            case 1: imgId1 = row.matrixA2[j]==0?R.drawable.red_star:R.drawable.red_v; break;
                            case 2: imgId1 = row.matrixA2[j]==0?R.drawable.yellow_rect:R.drawable.yellow_rect; break;
                            case 3: imgId1 = row.matrixA2[j]==0?R.drawable.green_rect:R.drawable.green_rect; break;
                        }
                        //Log.e("ddd", String.format("%d", row.matrixA1[j][i]));
                        if (imgId1 != 0) chStatus.setBackgroundDrawable(getResources().getDrawable(imgId1));
                    }
                }
            }

            return convertView;
        }

        @Override
        public int getCount() {
            return mStatusMessageList.size();
        }

        @Override
        public String getItem(int position) {
            return String.format("%d", position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }
    }
};
