package com.example.cdcmessage.cdcmessage.model;

import com.example.cdcmessage.cdcmessage.Consts;

/**
 * Created by admin on 2/3/17.
 */

public final class StatusMessage {
    public int id;
    public boolean JE;
    public boolean IP;
    public boolean PWEN;
    public boolean IST;
    public boolean Res;
    public boolean CAN;
    public int ST;
    public boolean PM;
    public int GPS;
    public int NU;
    public int BIST;
    public int BEST;
    public int WIR;
    public byte[][] matrixA1;
    public byte[] matrixA2;
    /*public byte[][] matrixA1 =
            {{0, 1, 2, 3}, {0, 2, 2, 3}, {0, 1, 2, 3}, {3, 1, 2, 0}, {0, 1, 2, 3}, {0, 1, 2, 3}, {0, 1, 2, 3}, {0, 1, 2, 3}, {0, 1, 2, 3}, {0, 1, 2, 3}, {0, 1, 2, 3}, {0, 1, 2, 3}};
    public byte[] matrixA2 =
            {1, 0, 0, 0, 1, 0, 0, 1, 1, 1, 1, 0, 0};*/
    public StatusMessage() {
        matrixA1 = new byte[Consts.STATUS_CHANNEL_COUNT][Consts.STATUS_TERMINAL_COUNT];
        matrixA2 = new byte[Consts.STATUS_CHANNEL_COUNT];
        for (int i=0; i<Consts.STATUS_CHANNEL_COUNT; i++) {
            matrixA2[i] = -1;
            for (int j = 0; j < Consts.STATUS_TERMINAL_COUNT; j++)
                matrixA1[i][j] = -1;
        }
    }
};
