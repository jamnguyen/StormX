package com.jamnguyen.stormx;

public class XConfig
{
    //System
    public static final boolean     USE_TRANSPOSE_MODE          = true;
    public static final boolean     isTEAM_STORMX               = true;
    public static final boolean     USE_ROTATION_VECTOR         = false;
    public static final boolean     USE_GYROSCOPE               = false;
    public static final boolean     DETECT_COLOR_WITH_CIRCLE    = true;


    //Value
    public static final int TIME_FOR_BLOW_OUT                   = 1500;
    public static final int TIME_FOR_BLOW_IN                    = 1500;
    public static final int TIME_FOR_SERVO1_UP                  = 200;
    public static final int TIME_FOR_CAR_BACKWARD               = 600;
    public static final int TIME_FOR_ROTATE                     = 200;
    //Spirit
    public static final int SPIRIT_TIME_FRONT                   = 1000;
    public static final int SPIRIT_TIME_THROW                   = 1000;
    public static final int NUM_OF_BALL = 1;//Số banh cần bắt

    public static final double      BALL_AREA_RATIO             = 0.11;
    public static final double      SPIRIT_BALL_AREA_RATIO      = 0.78;
    public static final double      GOAL_AREA_RATIO             = 0.20;
    public static final double      MIDDLE_DELTA                = 40;
    public static final int         MIDDLE_OFFSET               = 55;       //XPERIA XA
    public static final double      XA_DISTANCE_FACTOR          = 3300;
    public static final double      BALL_CATCH_DISTANCE         = 28;

    //Test color
    //Pink: 233.0625, 183.109375, 225.0
    //Orange: 13.640625, 193.3125, 231.578125
    //Green: 101.0625, 162.921875, 110.390625

    //StormX color
    //Pink: 240.25, 201.96875, 192.65625
    //Orange: 13.640625, 193.3125, 231.578125
    //Green: 101.0625, 162.921875, 110.390625

    //Spirit color
    //Pink: 245.8125, 177.328125, 218.0
    //Orange: 14.0, 209.71875, 212.96875
    //Green: 91.140625, 207.734375, 74.8125

    //XA Color
    //Pink: 236.0, 198.53125, 210.15625
    //Orange: 22.0, 238.6875, 216.015625
    //Green: 104.453125, 252.3125, 120.234375

    public static final double       COLOR_PINK[] = {236.0, 198.53125, 210.15625};
    public static final double       COLOR_ORANGE[] = {22.0, 238.6875, 216.015625};
    public static final double       COLOR_GREEN[] = {104.453125, 252.3125, 120.234375};
}
