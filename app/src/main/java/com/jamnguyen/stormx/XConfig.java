package com.jamnguyen.stormx;
public class XConfig
{
    //System
    public static final boolean     USE_TRANSPOSE_MODE          = true;
    public static final boolean     USE_GAMEPLAY_MODE           = true;
    public static final boolean     isTEAM_STORMX               = true;
    public static final boolean     USE_ROTATION_VECTOR         = false;
    public static final boolean     DETECT_CIRCLE               = false;
    public static final boolean     USE_GYROSCOPE               = false;
    public static final boolean     DETECT_COLOR_WITH_CIRCLE    = true;

    //Value
	public static final int     DEGREE_DELTA               = 40;
    public static final int     DEGREE_DELTA_SMALL               = 20;
	
    public static final int TIME_FOR_BLOW_OUT                   = 1200;
    public static final int TIME_FOR_BLOW_IN                    = 1500;
    public static final int TIME_FOR_SERVO1_UP                  = 200;
    public static final int TIME_FOR_CAR_BACKWARD               = 1500;
    public static final int TIME_FOR_ROTATE                     = 200;
    public static final int TIME_FOR_ASK                        = 200;
    public static final int TIME_FOR_CLEAN_LONG                 = 500;
    public static final int TIME_FOR_CLEAN_SHORT                = 400;
    public static final int TIME_FOR_CAR_FORWARD_INIT                = 50;
    public static final int TIME_FOR_SERVO1_DOWN_CHEAT                = 3500;
    public static final int TIME_FOR_INIT_CHEAT_BLOW_IN                = 6*1000;
    public static final int TIME_FOR_INIT_CHEAT                = 8*1000;
	
    public static final int TIME_OUT                			= 400;
    //Spirit
    public static final int SPIRIT_TIME_FRONT                   = 1000;
    public static final int SPIRIT_TIME_THROW                   = 1000;
    public static final int NUM_OF_BALL = 1;//Số banh cần bắt

    public static final double      BALL_AREA_RATIO             = 0.11;
    public static final double      SPIRIT_BALL_AREA_RATIO      = 0.78;
    public static final double      GOAL_AREA_RATIO             = 0.20;
    public static final double      MISS_DETECT_RATIO           = 0.75;     //In case 2 balls lying closely
    public static final double      MIDDLE_DELTA                = 40;
    public static final int         MIDDLE_OFFSET               = 55;       //XPERIA XA
    public static final double      XA_DISTANCE_FACTOR          = 3300;
    public static final double      BALL_CATCH_DISTANCE         = 30;
    public static final double      BALL_CATCH_DISTANCE_DELTA   = 0;
    public static final double      GOAL_THRESHOLD_RADIUS       = 250;

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
    //Pink: 230.03125, 196.640625, 222.53125
    //Orange: 16.0, 251.09375, 215.234375
    //Green: 102.6875, 209.203125, 118.28125
    //Green Room 1: 93.296875, 255.0, 79.625
    //Green Room 1 (f): 106.234375, 255.0, 124.484375

    public static final double       COLOR_PINK[] = {230.03125, 196.640625, 222.53125};
    public static final double       COLOR_ORANGE[] = {16.0, 251.09375, 215.234375};
    public static final double       COLOR_GREEN[] = {102.6875, 209.203125, 118.28125};
}
