package com.example.cdcmessage.cdcmessage;

/**
 * Created by Eagle on 2/2/2017.
 */

public final class Consts {
    public final static String LOGMESSAGE = "CDCMessage";

    public final static int MIN_ID = 0;
    public final static int MAX_ID = 99;
    public final static int STATUS_TERMINAL_COUNT = 4;
    public final static int STATUS_CHANNEL_COUNT = 12;
    public final static int STATUS_VALUE_COUNT = 2;

    public final static int STATUS_DEVICE_MESSAGE = -1;
    public final static int MATRIX_A_MESSAGE = -2;
    public final static int GENERAL_MESSAGE = -3;

    /*
     * Constants for States
     */
    final static int SOM = 0x3C;

    final static int INIT_STATUS = 0;
    final static int WAITING_FOR_INIT_RESPONSE = 1;
    final static int INIT_ACCEPTED = 2;
    final static int SENDING_PROGRAMMING_RESPONSE = 3;
    final static int WAITING_FOR_PROGRAMMING_RESPONSE = 4;
    final static int SENDING_FINISH_RESPONSE = 5;
    final static int WAITING_FOR_PROGRAMMING_FINISH_RESPONSE = 6;

    final static String[] description = {
            "INIT_STATUS",
            "WAITING_FOR_INIT_RESPONSE",
            "INIT_ACCEPTED",
            "SENDING_PROGRAMMING_RESPONSE",
            "WAITING_FOR_PROGRAMMING_RESPONSE",
            "SENDING_FINISH_RESPONSE",
            "WAITING_FOR_PROGRAMMING_FINISH_RESPONSE"
    };
    final static String[] msgTypeDesc = {
            "INIT_MESSAGE",
            "INTERROGATION_MESSAGE",
            "PROGRAM_MESSAGE",
            "PROGRAM_FINISH_MESSAGE",
            "NULL_MESSAGE",
            "JOIN_ENABLE_MESSAGE",
            "JOIN_DISABLE_MESSAGE",
            "POWER_ENABLE_MESSAGE",
            "POWER_DISABLE_MESSAGE",
            "START_MESSAGE",
            "STOP_MESSAGE",
            "PAUSE_MESSAGE"
    };

    final static int INIT_MESSAGE = 100;
    final static int INTERROGATION_MESSAGE = 101;
    final static int PROGRAM_MESSAGE = 102;
    final static int PROGRAM_FINISH_MESSAGE = 103;
    final static int NULL_MESSAGE = 104;
    final static int JOIN_ENABLE_MESSAGE = 105;
    final static int JOIN_DISABLE_MESSAGE = 106;
    final static int POWER_ENABLE_MESSAGE = 107;
    final static int POWER_DISABLE_MESSAGE = 108;
    final static int START_MESSAGE = 109;
    final static int STOP_MESSAGE = 110;
    final static int PAUSE_MESSAGE = 111;

    final static int SUCCESS_SEND = 1000;
    final static int MESSAGE_TYPE_ERROR = 1001;
    final static int USBSERVICE_ERROR = 1002;
    final static int NOT_READY_TO_SEND = 1003;

    final static int MACHINESTATE_IDLE = 2000;
    final static int MACHINESTATE_ARM = 2001;
    final static int MACHINESTATE_PLAY = 2002;
    final static int MACHINESTATE_PAUSE = 2003;
    final static int MACHINESTATE_CREATENETWORK = 2004;
    final static int MACHINESTATE_NONE = 2005;

    final static String[] machinestateDesc = {
            "Idle",
            "Arm",
            "Play",
            "Pause",
            "Create Network",
            "None"
    };
}
