package com.jamnguyen.stormx;

public class XConfig
{
    //System
    public static final boolean     USE_TRANSPOSE_MODE          = true;
    public static final boolean     isTEAM_STORMX               = true;
    public static final boolean     USE_GYROSCOPE               = false;
    public static final boolean     DETECT_COLOR_WITH_CIRCLE    = true;


    //Value
    public static final double      BALL_AREA_RATIO             = 0.11;
    public static final double      SPIRIT_BALL_AREA_RATIO      = 0.78;
    public static final double      GOAL_AREA_RATIO             = 0.20;
    public static final double      MIDDLE_DELTA                = 120;
    public static final double      XA_DISTANCE_FACTOR          = 3300;
    public static final double      BALL_CATCH_DISTANCE         = 21;

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

    public static final double       COLOR_PINK[] = {233.0625, 183.109375, 225.0};
    public static final double       COLOR_ORANGE[] = {13.640625, 193.3125, 231.578125};
    public static final double       COLOR_GREEN[] = {101.0625, 162.921875, 110.390625};
}
