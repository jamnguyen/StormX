package com.jamnguyen.stormx;

public class XCommander
{
    //DEFINES---------------------------------------------------------------------------------------
    //To Arduino
    public static final String MOVE_FORWARD     = "F\n";
    public static final String TURN_LEFT        = "L\n";
    public static final String TURN_LEFT_90     = "Q\n";
    public static final String TURN_RIGHT       = "R\n";
    public static final String TURN_RIGHT_90    = "E\n";
    public static final String TURN_AROUND      = "Z\n";
    public static final String MOVE_BACKWARD    = "B\n";
    public static final String MOVE_BACKWARD_1S = "V\n";
    public static final String STOP             = "S\n";
    public static final String CATCH_BALL       = "C\n";
    public static final String PUSH_BALL        = "P\n";

    //From Arduino
    public static final String CANNOT_MOVE      = "K\n";
    //----------------------------------------------------------------------------------------------

    private XBluetooth m_Bluetooth;

    public XCommander(XBluetooth BT)
    {
        m_Bluetooth = BT;
    }

    public void sendCommnand(String command)
    {
        m_Bluetooth.send(command);
    }


}
