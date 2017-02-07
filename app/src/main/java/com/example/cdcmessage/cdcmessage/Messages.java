package com.example.cdcmessage.cdcmessage;

import android.util.Log;

import com.example.cdcmessage.cdcmessage.model.MessageForm;
import com.example.cdcmessage.cdcmessage.model.StatusForm;
import com.example.cdcmessage.cdcmessage.model.StatusMessage;

import java.util.ArrayList;

/**
 * Created by Eagle on 1/31/2017.
 */

public final class Messages {
    static int PC_STATUS;

    public static ArrayList<MessageForm> msgQueue;
    public static int queueIndex = 0;
    public static int msgCountForDevice = 0;

    public static UsbService usbService = null;

    public static final void initObject(UsbService usbService) {
        Messages.usbService = usbService;
        Messages.PC_STATUS = Consts.INIT_STATUS;
        Messages.msgQueue = new ArrayList<MessageForm>();
    }
    public static final int send_init(UsbService usbService) {
        Messages.usbService = usbService;
        Messages.queueIndex = 0;
        return sendMessage( new MessageForm(Consts.INIT_MESSAGE, null) );
    }

    public static final int sendInterrogationMessage() {
        return sendMessage( new MessageForm(Consts.INTERROGATION_MESSAGE, null) );
    }

    public static final boolean sendProgramMessages(ArrayList<int[]> datas) {
        //Messages.usbService = usbService;

        for (int i=0; i<datas.size(); i++) {
            Messages.msgQueue.add(new MessageForm(Consts.PROGRAM_MESSAGE, datas.get(i)));
        }
        if (Messages.PC_STATUS == Consts.INIT_STATUS) {

            return false;
        }
        if (Messages.PC_STATUS == Consts.INIT_ACCEPTED) {
            Messages.msgCountForDevice = 0;
            start();
        }
        return true;
    }

    private static int start() {
        if (!(Messages.PC_STATUS == Consts.INIT_ACCEPTED || Messages.PC_STATUS == Consts.WAITING_FOR_PROGRAMMING_RESPONSE))
            return Consts.NOT_READY_TO_SEND;
        Messages.queueIndex = 0;
        if (Messages.msgQueue.size() == 0)
            return 0;
        return sendMessage(Messages.msgQueue.get(Messages.queueIndex));
    }

    //public static int checkMessageType(String data) {
    public static int checkMessageType(byte[] data) {
        //StatusForm msg = unpack_data(data.getBytes());
        Log.e(Consts.LOGMESSAGE, String.format("%d bytes received", data.length));
        StatusForm msg = unpack_data(data);
        if (msg == null) { return Consts.NULL_MESSAGE; }
        if (msg.cmd == 0b00001110) { //Status Device Message
            return Consts.STATUS_DEVICE_MESSAGE;
        } else if ( msg.cmd == 0b00001111) { //Matrix A Message
            return Consts.MATRIX_A_MESSAGE;
        }
        return Consts.GENERAL_MESSAGE;
    }

//    public static StatusMessage getStatusMessage(String data) {
    public static StatusMessage getStatusMessage(byte[] data) {
//        StatusForm msg = unpack_data(data.getBytes());
        StatusForm msg = unpack_data(data);
        if (msg == null) return null;
        StatusMessage statusMessage = new StatusMessage();
        //msg.data : 5 bytes
        statusMessage.id = msg.addr;
        statusMessage.JE =      ((msg.data[0] & 0b10000000)>>7)==1?true:false;
        statusMessage.IP =      ((msg.data[0] & 0b01000000)>>6)==1?true:false;
        statusMessage.PWEN =    ((msg.data[0] & 0b00100000)>>5)==1?true:false;
        statusMessage.IST =     ((msg.data[0] & 0b00010000)>>4)==1?true:false;
        statusMessage.Res =     ((msg.data[0] & 0b00001000)>>3)==1?true:false;
        statusMessage.CAN =     ((msg.data[0] & 0b00000100)>>2)==1?true:false;
        statusMessage.ST =      msg.data[0] & 0b00000011;
        statusMessage.PM =      ((msg.data[1] & 0b10000000)>>7)==1?true:false;
        statusMessage.GPS =     (msg.data[1] & 0b01100000)>>5;
        statusMessage.NU =      msg.data[1] & 0b00011111;
        statusMessage.BIST =    (msg.data[2] & 0b11110000)>>4;
        statusMessage.BEST =    (msg.data[2] & 0b00001111)*256+msg.data[3];
        statusMessage.WIR =     msg.data[4];
        return statusMessage;
    }

    //public static StatusMessage getMatrixAMessage(String data) {
    public static StatusMessage getMatrixAMessage(byte[] data) {
        //StatusForm msg = unpack_data(data.getBytes());
        StatusForm msg = unpack_data(data);
        if (msg == null) return null;
        StatusMessage statusMessage = new StatusMessage();
        statusMessage.id = msg.addr;
        int i;
        for (i=0; i<Consts.STATUS_CHANNEL_COUNT; i++) {
            byte A1 = msg.data[i*2];
            statusMessage.matrixA1[i][0] = (byte)((A1 & 0b11000000) >> 6);
            statusMessage.matrixA1[i][1] = (byte)((A1 & 0b00110000) >> 4);
            statusMessage.matrixA1[i][2] = (byte)((A1 & 0b00001100) >> 2);
            statusMessage.matrixA1[i][3] = (byte)((A1 & 0b00000011) >> 0);

            byte A2 = msg.data[i*2+1];
            statusMessage.matrixA2[i] = A2;

            //Log.e(Consts.LOGMESSAGE, String.format("%d : %x (%x %x %x %x) %d ", i, A1,
                    //(A1 & 0x11000000),(A1 & 0x00110000), (A1 & 0x00001100), (A1 & 0x00000011), statusMessage.matrixA2[i]));
            //Log.e(Consts.LOGMESSAGE, String.format("%d : %x (%d %d %d %d) %d ", i, A1,
                    //      statusMessage.matrixA1[i][0], statusMessage.matrixA1[i][1], statusMessage.matrixA1[i][2], statusMessage.matrixA1[i][3], statusMessage.matrixA2[i]));
        }
        return statusMessage;
    }

    //public static void onMessage(String data) {
    public static void onMessage(byte[] data) {
        StatusForm status = unpack_data(data);
        Log.e(Consts.LOGMESSAGE, "Messages : <- STATUS : " + Consts.description[Messages.PC_STATUS] + "  Index : " + String.format("%d/%d", Messages.queueIndex, Messages.msgQueue.size()));
        switch(Messages.PC_STATUS) {
            case Consts.WAITING_FOR_INIT_RESPONSE:
                if (status.cmd == 0b00001110) {
                    setStatus(Consts.INIT_ACCEPTED);
                }
                break;
            case Consts.WAITING_FOR_PROGRAMMING_RESPONSE:
                if (status.cmd == 0b00001110) {
                    MessageForm msg;
                    if (Messages.queueIndex < Messages.msgQueue.size()) {
                        MessageForm prev, cur;
                        prev = Messages.msgQueue.get(Messages.queueIndex-1);
                        cur =  Messages.msgQueue.get(Messages.queueIndex);
                        if ( cur.msg_type == Consts.PROGRAM_MESSAGE && prev.datas[0] == cur.datas[0] ) {
                            msg = new MessageForm(Consts.PROGRAM_MESSAGE, Messages.msgQueue.get(Messages.queueIndex).datas);
                            setStatus(Consts.SENDING_PROGRAMMING_RESPONSE);
                            sendMessage(msg);
                        } else if ( cur.msg_type == Consts.PROGRAM_MESSAGE && prev.datas[0] != cur.datas[0]) {
                            int[] finishmsgdata = new int[2];
                            finishmsgdata[0] =  Messages.msgQueue.get(Messages.queueIndex-1).datas[0];
                            finishmsgdata[1] = Messages.msgCountForDevice;

                            msg = new MessageForm(Consts.PROGRAM_FINISH_MESSAGE, finishmsgdata);
                            setStatus(Consts.SENDING_FINISH_RESPONSE);
                            sendMessage(msg);
                        }
                    } else {
                        int[] finishmsgdata = new int[2];
                        finishmsgdata[0] =  Messages.msgQueue.get(Messages.queueIndex-1).datas[0];
                        finishmsgdata[1] = Messages.msgCountForDevice;

                        msg = new MessageForm(Consts.PROGRAM_FINISH_MESSAGE, finishmsgdata);
                        setStatus(Consts.SENDING_FINISH_RESPONSE);
                        sendMessage(msg);
                    }

                }
                break;
            case Consts.WAITING_FOR_PROGRAMMING_FINISH_RESPONSE:
                if (status.cmd == 0b00001110) {
                    MessageForm msg;
                    if (Messages.queueIndex < Messages.msgQueue.size()) {
                        msg = Messages.msgQueue.get(Messages.queueIndex);
                        setStatus(Consts.SENDING_PROGRAMMING_RESPONSE);
                        sendMessage(msg);
                    } else {
                        Messages.msgQueue.clear();
                        Messages.queueIndex = 0;
                        setStatus(Consts.INIT_ACCEPTED);
                        Log.e(Consts.LOGMESSAGE, "Messages : Message Sending Complete");
                    }

                }
                break;
        }
    }

    private static int sendMessage(MessageForm msg) {

        if (msg.datas != null)
            Log.e(Consts.LOGMESSAGE, "Messages : -> TYPE : " + Consts.msgTypeDesc[msg.msg_type-100] + "  Id:" + String.format("%d", msg.datas[0]) + " Count:" + String.format("%d", Messages.msgCountForDevice));
        else
            Log.e(Consts.LOGMESSAGE, "Messages : -> TYPE : " + Consts.msgTypeDesc[msg.msg_type-100]);
        if (msg.msg_type == Consts.INTERROGATION_MESSAGE &&( getStatus() == Consts.INIT_STATUS || getStatus() == Consts.WAITING_FOR_INIT_RESPONSE) ) {
            Log.e(Consts.LOGMESSAGE, "Messages : Not allowed to send interrogation message");
            return Consts.USBSERVICE_ERROR;
        }
        byte cmd, addr;
        byte[] data;
        int nextStatus;
        switch(msg.msg_type) {
            case Consts.INIT_MESSAGE:
                cmd = 0b00000001;
                addr = (byte) 209;
                data = String.valueOf(170).getBytes();
                nextStatus = Consts.WAITING_FOR_INIT_RESPONSE;
                Messages.msgCountForDevice = 0;
                break;
            case Consts.INTERROGATION_MESSAGE:
                cmd = 0b00001010;
                addr = (byte) 202;
                data = new byte[1];
                data[0] = (byte)0x00;
                nextStatus = Messages.PC_STATUS;
                break;
            case Consts.JOIN_ENABLE_MESSAGE:
                cmd = 0b00001101;
                addr = (byte) 210;
                data = new byte[1];
                data[0] = 1;
                nextStatus = Messages.PC_STATUS;
                break;
            case Consts.JOIN_DISABLE_MESSAGE:
                cmd = 0b00001101;
                addr = (byte) 210;
                data = new byte[1];
                data[0] = 0;
                nextStatus = Messages.PC_STATUS;
                break;
            case Consts.POWER_ENABLE_MESSAGE:
                cmd = 0b00000011;
                addr = (byte) 203;
                data = new byte[1];
                data[0] = 1;
                nextStatus = Messages.PC_STATUS;
                break;
            case Consts.POWER_DISABLE_MESSAGE:
                cmd = 0b00000011;
                addr = (byte) 203;
                data = new byte[1];
                data[0] = 0;
                nextStatus = Messages.PC_STATUS;
                break;
            case Consts.START_MESSAGE:
                cmd = 0b00000100;
                addr = (byte) 204;
                data = new byte[1];
                data[0] = 0x00;
                nextStatus = Messages.PC_STATUS;
                break;
            case Consts.STOP_MESSAGE:
                cmd = 0b00000101;
                addr = (byte) 206;
                data = new byte[1];
                data[0] = 0x00;
                nextStatus = Messages.PC_STATUS;
                break;
            case Consts.PAUSE_MESSAGE:
                cmd = 0b00000110;
                addr = (byte) 205;
                data = new byte[1];
                data[0] = 0x00;
                nextStatus = Messages.PC_STATUS;
                break;
            case Consts.PROGRAM_MESSAGE:
                cmd = 0b00000111;
                addr = (byte) msg.datas[0];  //Id
                data = new byte[8];
                int cid, msec;
                if (msg.datas[2] < 99) cid = (msg.datas[2] - 1) * 8 + msg.datas[1];
                else cid = msg.datas[1];
                data[0] = (byte) cid;
                msec = msg.datas[2];
                data[1] = (byte) ((msec & 0xFF000000) >> (3 * 8));
                data[2] = (byte) ((msec & 0x00FF0000) >> (2 * 8));
                data[3] = (byte) ((msec & 0x0000FF00) >> (1 * 8));
                data[4] = (byte) (msec & 0x000000FF);
                data[5] = (byte) msg.datas[3];
                data[6] = (byte) msg.datas[4];
                data[7] = (byte) msg.datas[5];
                nextStatus = Consts.WAITING_FOR_PROGRAMMING_RESPONSE;
                break;
            case Consts.PROGRAM_FINISH_MESSAGE:
                cmd = 0b00001000;
                addr = (byte) msg.datas[0];
                data = new byte[6];
                int sc, nrmu;
                sc = (msg.datas[1] & 0x0000ff00) >> 8;
                nrmu = msg.datas[1] & 0x000000ff;
                data[0] = (byte) sc;
                data[1] = (byte) nrmu;
                data[2] = data[3] = data[4] = data[5] = 0;
                nextStatus = Consts.WAITING_FOR_PROGRAMMING_FINISH_RESPONSE;
                break;
            default:
                return Consts.MESSAGE_TYPE_ERROR;
        }
        byte[] res = pack_data(addr, cmd, data);
        Log.e(Consts.LOGMESSAGE, "Messages : UsbService = "+Messages.usbService==null?"null":"notnull");
        if (Messages.usbService != null) { // if UsbService was correctly binded, Send data
            if(msg.msg_type == Consts.PROGRAM_MESSAGE) {
                Messages.msgCountForDevice++;
                Messages.queueIndex++;
            }
            else if(msg.msg_type == Consts.PROGRAM_FINISH_MESSAGE) Messages.msgCountForDevice=0;
            Log.e(Consts.LOGMESSAGE, "Messages Sent : "+ Consts.msgTypeDesc[msg.msg_type-100]);
            Messages.usbService.write(res);
            setStatus(nextStatus);
            return Consts.SUCCESS_SEND;
        } else {
            return Consts.USBSERVICE_ERROR;
        }
    }

    public static int sendJoinEnableMessage() {
        return sendMessage( new MessageForm(Consts.JOIN_ENABLE_MESSAGE, null) );
    }

    public static int sendJoinDisableMessage() {
        return sendMessage( new MessageForm(Consts.JOIN_DISABLE_MESSAGE, null) );
    }

    public static int sendPowerEnableMessage() {
        return sendMessage( new MessageForm(Consts.POWER_ENABLE_MESSAGE, null) );
    }

    public static int sendPowerDisableMessage() {
        return sendMessage( new MessageForm(Consts.POWER_DISABLE_MESSAGE, null) );
    }

    public static int sendStartMessage() {
        return sendMessage( new MessageForm(Consts.START_MESSAGE, null) );
    }

    public static int sendStopMessage() {
        return sendMessage( new MessageForm(Consts.STOP_MESSAGE, null) );
    }

    public static int sendPauseMessage() {
        return sendMessage( new MessageForm(Consts.PAUSE_MESSAGE, null) );
    }

    private static void setStatus(int status) {
        Messages.PC_STATUS = status;
        Log.e(Consts.LOGMESSAGE, "Messages : " + Consts.description[Messages.PC_STATUS]);
    }

    private static int getStatus() {
        return Messages.PC_STATUS;
    }

    private static final byte compute_cs(byte addr, byte cmd, byte[] data) {
        byte cs = (byte)0xFF;
        cs ^= addr;
        cs ^= cmd;
        for (int i=0; i<data.length; i++) {
            cs ^= data[i];
        }
        return cs;
    }

    private static final byte[] pack_data(byte addr, byte cmd, byte[] data) {
        byte[] result;
        result = new byte[data.length + 5];

        result[0] = (byte)Consts.SOM;
        result[1] = (byte)(2+data.length);
        result[2] = addr;
        result[3] = cmd;
        int i;
        for (i=0; i<data.length; i++) {
            result[4+i] = data[i];
        }
        result[4+i] = compute_cs(addr, cmd, data);
        return result;
    }

    private static final StatusForm unpack_data(byte[] bytes) {
/*        if (bytes[0] != Consts.SOM) {
            return null;
        }

        StatusForm result = new StatusForm();
        result.dlen = (int)bytes[1];
        result.addr = (int)bytes[2];
        result.cmd = (int)bytes[3];
        int realLen = ((bytes.length-5)<(result.dlen-3))?bytes.length-5:(result.dlen-3);
        result.data = new byte[realLen];
        for (int i=0; i<realLen; i++) {
            result.data[i] = bytes[i+4];
        }
        result.cs = (int)bytes[bytes.length-1];
        Log.e(Consts.LOGMESSAGE, String.format("UnPacked Data : dlen:%d addr:%d cmd:%d data:%d cs:%d", result.dlen, result.addr, result.cmd, result.data.length, result.cs));
        return result;*/

        /*
        StatusForm result = new StatusForm();
        int i, msgXOR;
        for (i=0; i<bytes.length; i++) {
            if (bytes[i] == 0x3C) {

                Log.e(Consts.LOGMESSAGE, String.format("Length : %d", bytes.length));
                msgXOR = 0xFF;
                result.dlen = bytes[i+1] & 0xFF;
                if (bytes.length < i+result.dlen+3) return null;
                result.addr = bytes[i+2] & 0xFF;
                result.cmd = bytes[i+3] & 0xFF;
                msgXOR = msgXOR ^ result.addr ^ result.cmd;
                Log.e(Consts.LOGMESSAGE, String.format("dlen:%x  addr:%x  cmd:%x", result.dlen, result.addr, result.cmd));

                result.data = new byte[result.dlen];
                for (int j=0; j<result.dlen-2; j++) {
                    result.data[j] = bytes[i+j+4];
                    Log.e(Consts.LOGMESSAGE, String.format("%x", bytes[i+j+4]));
                    msgXOR = msgXOR ^ result.data[j];
                }
                result.cs = bytes[i+result.dlen+2] & 0xFF;

                Log.e(Consts.LOGMESSAGE, String.format("CS1 : %x", result.cs));
                Log.e(Consts.LOGMESSAGE, String.format("CS2 : %x", msgXOR));
                if (result.cs == msgXOR) return result;
                else return null;
                */

        StatusForm result = new StatusForm();
        int i, msgXOR, k;

        byte[] testmsgb = new byte[40];


        for (i=0; i<bytes.length; i++) {
            if (bytes[i] == 0x3C) {
                msgXOR = 0xFF;
                result.dlen = bytes[i+1] & 0xFF;
                //Log.e(Consts.LOGMESSAGE, String.format("Length : i/%d len/%d dlen/%d", i, bytes.length, result.dlen));
                if (bytes.length < i+result.dlen+2)
                    return null;
                result.addr = bytes[i+2] & 0xFF;
                result.cmd = bytes[i+3] & 0xFF;
                msgXOR = msgXOR ^ result.addr ^ result.cmd;
                /*for (k=0; k< (result.dlen + 2); k++) {
                    testmsgb [k] = bytes[i + k];
                    Log.e(Consts.LOGMESSAGE, String.format("Byte: %d  Value: %d", k, testmsgb [k]));
                }*/

                //Log.e(Consts.LOGMESSAGE, String.format("dlen:%x  addr:%x  cmd:%x", result.dlen, result.addr, result.cmd));

                result.data = new byte[result.dlen];
                for (int j=0; j<result.dlen-3; j++) {
                    result.data[j] = bytes[i+j+4];
                    Log.e(Consts.LOGMESSAGE, String.format("%x", bytes[i+j+4]));
                    msgXOR = msgXOR ^ result.data[j];
                }
                result.cs = bytes[i+result.dlen+1] & 0xFF;

                /*Log.e(Consts.LOGMESSAGE, String.format("CS1 : %x", result.cs));
                Log.e(Consts.LOGMESSAGE, String.format("CS2 : %x", msgXOR));*/
                if (result.cs == msgXOR) return result;
                else return null;
            }
        }
        return null;
    }
}
