package com.dunglevan.ai_spirit;

import com.dunglevan.ai_spirit.XBluetooth;

/**
 * Created by dung.levan on 19/07/2017.
 */

public class SendBluetoothMessage {

    public static final String strForward = "Forward";
    public static final String strTurnLeft_Small = "Left_Small";
    public static final String strTurnLeft = "Left";
    public static final String strTurnRight_Small = "Right_Small";
    public static final String strTurnRight = "Right";
    public static final String strTurnBack = "Back";
    public static final String strRewind = "Rewind";
    public static final String strStop = "Stop";

/*
- F: Đi thẳng
- L: Nhích trái (độ càng nhỏ càng tốt)
- Q: Quẹo trái 90
- R: Nhích phải (độ càng nhỏ càng tốt)
- E: Quẹo phải 90
- Z: Quay 180 (trái phải ok)
- B: Đi lui
- S: Dừng
- C: Bắt bóng/ Hút
- P: Đẩy bóng qua lưới

 */
    public static void sendCommnand(XBluetooth xBluetooth, String command)
    {
        switch (command) {
            case strForward:
                xBluetooth.send("F\n");
                break;
            case strTurnLeft_Small:
                xBluetooth.send("L\n");
                break;
            case strTurnLeft:
                xBluetooth.send("Q\n");
                break;
            case strTurnRight_Small:
                xBluetooth.send("R\n");
                break;

        }
    }

}
