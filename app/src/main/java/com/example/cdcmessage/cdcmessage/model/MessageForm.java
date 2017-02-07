package com.example.cdcmessage.cdcmessage.model;

/**
 * Created by admin on 2/3/17.
 */

public class MessageForm {
    public int msg_type;
    public int[] datas;
    public MessageForm(int pMsgType, int[] pDatas) {
        msg_type = pMsgType;
        datas = pDatas;
    }
}