package com.jamnguyen.stormx;
import android.content.Context;
import android.util.Log;

import org.opencv.core.Point;
public class Gameplay
{
	// private static Gameplay instance = null;
	public static final String MESSEAGE_FORWARDFAST   =     "F";
	public static final String MESSEAGE_FORWARDSLOW        = "A";
	public static final String MESSEAGE_BACKWARD           = "B";


	public static final String MESSEAGE_ROTATELEFT           = "L";
	public static final String MESSEAGE_TURNLEFT            = "K";
	public static final String MESSEAGE_ROTATERIGHT          = "R";
	public static final String MESSEAGE_TURNRIGHT           = "T";
	public static final String MESSEAGE_STOP               = "S";

	public static final String MESSEAGE_MOTOR_BLOW_IN            = "C";
	public static final String MESSEAGE_MOTOR_BLOW_OUT            = "D";
	public static final String MESSEAGE_MOTOR_STOP               = "E";

	public static final String MESSEAGE_SERVO1_UP                = "G";
	public static final String MESSEAGE_SERVO1_DOWN              = "H";
	public static final String MESSEAGE_SERVO1_DOWN_RELEASE_BALL              = "M";
	public static final String MESSEAGE_SERVO2_OPEN              = "I";
	public static final String MESSEAGE_SERVO12_CLOSE            = "J";
	public static final String MESSAGE_ASK_BALL_STATUS			= "Z";

	public static final int MESSAGE_HAVEBALL = 4;
	public static final int MESSAGE_NOBALL = 5;
    public static final int SWITCH_LEFT = 0X10;
	public static final int SWITCH_RIGHT = 0X01;
    public static final int NUM_BALL = 1;
	public static final int ANGLE_DIFF = 6;
	public static final int ANGLE_DIFF_SMALL = 4;
	private XDetector m_Detector;
    private XBluetooth m_Bluetooth;
	private XVectorDetection m_VectorDetect = null;
	private int[] orientations;
	public static final double DEGREE_ESP = 5.0;
	private int m_SwitchMessage = 0;
	private int m_ColorMessage = 0;
	public static final int COLOR_ZERO = 0;//Ko nhin thay ball
	public static final int COLOR_NEAR = 1;//ball o gan, co the lay
	public static final int COLOR_LEFT = 2;//ball ben trai
	public static final int COLOR_RIGHT = 3;//ball ben phai
	public static final int COLOR_MIDDLE = 4;//ball ben phai
	public static final int COLOR_TOO_NEAR = 5;//ball qua gan
	public static final int COLOR_TOO_FAR = 5;//ball qua xa

	private int m_State = 0;
	public static final int STATE_INIT = 0;
	public static final int STATE_FIND_BALL = 1;//xoay tim ball cho den khi nao thay ball se dung
	public static final int STATE_FOLLOW_BALL = 2;//
	public static final int STATE_CATCH_BALL = 3;//
	public static final int STATE_FIND_GOAL = 4;
	public static final int STATE_GO_GOAL = 5;
	public static final int STATE_RELEASE_BALL = 6;
	public static final int STATE_CLEAN_PATH = 7;
	public static final int STATE_CENTER_BALL = 8;
	
	public long m_CurrentTime = 0;
	public boolean m_isHoldingBall = false;

	public static boolean ANDROID_STARTED = false;
	public static boolean ANDROID_INITIALIZED = false;
	public static int m_BallCount = 0;//đếm số banh hiện tại
	/// Gyroscope
	public static Gyroscope m_Gyroscope = null;


	public Gameplay(){
		m_Bluetooth = null;
	}
	public Gameplay(XBluetooth BT, XDetector DT, Context context)
    {
        m_Bluetooth = BT;
        m_Detector = DT;
		if(XConfig.USE_ROTATION_VECTOR)
		{
			m_VectorDetect = new XVectorDetection(context);
		}
		if(XConfig.USE_GYROSCOPE)
		{
			m_Gyroscope = new Gyroscope(context);
		}
		//m_VectorInit = null;
		m_State = STATE_INIT;
		orientations = new int[3];
        m_BallCount = 0;
    }
	// public static Gameplay getInstance()
	// {
		// if(instance == null)
		// {
			// return new Gameplay();
		// }
		// instance.m_State = STATE_INIT;
		// return instance;
	// }
	public void Switch_State(int state)
	{
		m_Detector.setDetectBall(true);
		if(state == STATE_FIND_GOAL || state == STATE_GO_GOAL)
		{
			m_Detector.setDetectBall(false);

            //Check if catching ball successful
            Car_Stop();
            askHoldingBallStatus();
            Game_Sleep(XConfig.TIME_FOR_ASK);
            if(!m_isHoldingBall)
            {
                Switch_State(STATE_FOLLOW_BALL);
            }
		}
		m_State = state;
	}
	public void SetColorMessage(int message)
	{
		m_ColorMessage = message;
	}
	public void SetSwitchMessage(int message)
	{
		m_SwitchMessage = message;
	}
	
	public void STATE_INIT_func()
	{
		m_CurrentTime = 0;
		m_ColorMessage = 0;
		m_Detector.init();
        //Set GOAL vector
        Log.d("dung.levan","STATE_INIT_func");
		if(XConfig.USE_ROTATION_VECTOR)
		{
			orientations[0] = (int) m_VectorDetect.getX();
			orientations[1] = (int)m_VectorDetect.getY();
			orientations[2] = (int)m_VectorDetect.getZ();
			Log.d("dung.levan","orientations " + orientations[0] + " - " + orientations[1] + " - " + orientations[2]);
		}
		Switch_State(STATE_FIND_BALL);
		//Switch_State(STATE_FOLLOW_BALL);
//		Switch_State(STATE_FIND_GOAL); // Đang test về khung lưới
		ANDROID_INITIALIZED = true;
	}
	public void Game_Sleep(long time)
    {
		try {
            for (int i = 0; i < time/10; i++){
                Thread.sleep(10);
            }
            Thread.sleep(time - (time/10));
		}catch (Exception ex)
		{}
		// m_CurrentTime = System.currentTimeMillis();
		// while(System.currentTimeMillis() - m_CurrentTime < time)
		// {}
	}
	public void STATE_FIND_BALL_func()
	{
		if(m_ColorMessage == COLOR_MIDDLE || m_ColorMessage == COLOR_NEAR)
		{
			Car_Stop();
			Switch_State(STATE_FOLLOW_BALL);
		}
		else
		{
			if((m_SwitchMessage & SWITCH_LEFT) != 0)
			{
				Car_Rotate_Right();
			}
			else
			{
				Car_Rotate_Left();
			}
		}
	}
	public void STATE_FOLLOW_BALL_func()
	{
		switch(m_ColorMessage)
		{
			case COLOR_ZERO:// dang follow ball ma bi mat focus 
			Car_Stop();
			Switch_State(STATE_FIND_BALL);
			break;
			case COLOR_NEAR:
			Car_Stop();
            if(m_Detector.getMissRatio() < XConfig.MISS_DETECT_RATIO)
                Switch_State(STATE_CLEAN_PATH);
            else
                Switch_State(STATE_CENTER_BALL);
			break;
			case COLOR_LEFT:
			Car_TurnLeft();
			break;
			case COLOR_RIGHT:
			Car_TurnRight();
			break;
			case COLOR_MIDDLE:
			Car_Forward();
			break;
		}
	}
    public void STATE_CLEAN_PATH_func()
    {
		Motor_Blow_In();
		Servo1_Down();
		Game_Sleep(XConfig.TIME_FOR_SERVO1_UP);
		Car_Forward();
		Game_Sleep(XConfig.TIME_FOR_CLEAN_LONG);
//		Car_Backward();
//		Game_Sleep(XConfig.TIME_FOR_SERVO1_UP);
		Motor_Stop();
		Servo1_Up();
		Game_Sleep(XConfig.TIME_FOR_SERVO1_UP);
		Car_Stop();

		askHoldingBallStatus();
		Game_Sleep(XConfig.TIME_FOR_ASK);

		if(m_isHoldingBall)
			Switch_State(STATE_FIND_GOAL);
		else
        	Switch_State(STATE_FOLLOW_BALL);
    }
	public void STATE_CENTER_BALL_func()
	{
		int tX = m_Detector.getTransposedX((int) m_Detector.getBallCenter().y);
		double distance = m_Detector.getBallDistance();

		if (tX < (m_Detector.getMiddleLine()) && ((m_Detector.getMiddleLine()) - tX) > XConfig.MIDDLE_DELTA) {
			Car_Rotate_Left();
		} else if (tX > m_Detector.getMiddleLine() && (tX - m_Detector.getMiddleLine()) > XConfig.MIDDLE_DELTA) {
			Car_Rotate_Right();
		}
		//Too close
		else if (distance < XConfig.BALL_CATCH_DISTANCE - XConfig.BALL_CATCH_DISTANCE_DELTA)
		{
			Car_Backward();
		}
		//Too far
//		else if (distance > XConfig.BALL_CATCH_DISTANCE + XConfig.BALL_CATCH_DISTANCE_DELTA)
//		{
//			Car_Forward();
//		}
		else {
			Car_Stop();
			Switch_State(STATE_CATCH_BALL);
		}
	}
	public void STATE_CATCH_BALL_func()
	{
		if (XConfig.isTEAM_STORMX) {
			Motor_Blow_In();
			Servo1_Down();
			Game_Sleep(XConfig.TIME_FOR_SERVO1_UP);
			Car_Forward();
			Game_Sleep(XConfig.TIME_FOR_CLEAN_SHORT);
			Car_Backward();
			Game_Sleep(XConfig.TIME_FOR_SERVO1_UP);
			Servo1_Up();
			Motor_Stop();
			Game_Sleep(XConfig.TIME_FOR_SERVO1_UP);
			Car_Stop();

            Switch_State(STATE_FIND_GOAL);
		} else
		{
            Car_Stop();
			Servo1_Down();
			Game_Sleep(XConfig.SPIRIT_TIME_FRONT);
			Servo1_Up();
			Game_Sleep(XConfig.SPIRIT_TIME_FRONT);
            m_BallCount++;
			//Car_Stop();
			if (m_BallCount >= XConfig.NUM_OF_BALL ) Switch_State(STATE_FIND_GOAL);
		}
	}

	public void STATE_FIND_GOAL_func()
	{
        /*m_BallCount = 0;
		 Log.d("dung.levan","STATE_GO_GOAL_func orientations " + orientations[0] + " - " + orientations[1] + " - " + orientations[2]);
         Log.d("dung.levan","STATE_GO_GOAL_func orientations " + m_VectorDetect.getX() + " - " + m_VectorDetect.getY() + " - " + m_VectorDetect.getZ());
        if (m_VectorDetect.getX() < orientations[0] - ANGLE_DIFF ){ //ANGLE_DIFF: góc lệch, hằng số
             Log.d("dung.levan","right");
             Car_Rotate_Right(); // Xoay xe qua phải
        } else if (m_VectorDetect.getX() > orientations[0] + ANGLE_DIFF) {
             Log.d("dung.levan","left");
             Car_Rotate_Left(); // Xoay xe qua trái
        } 
		else
        {
             Log.d("dung.levan","forward");//Đã xoay đúng hướng chuyển qua STATE_GO_GOAL
			 Car_Stop();
			 // Car_Forward(); // NOTE: Xe chạy thẳng về khung, khi đụng vật cản mới chuyển state.
			 				// Xin đừng chuyển state quá sớm, xe mất khả năng tự chỉnh góc.
			Switch_State(STATE_GO_GOAL);//qua STATE_GO_GOAL mới chạy thẳng
        }*/

		if(m_ColorMessage == COLOR_ZERO)
		{
			if((m_SwitchMessage & SWITCH_LEFT) != 0)
			{
				Car_Rotate_Right();
			}
			else
			{
				Car_Rotate_Left();
			}
		}
		else
		{
			switch (m_ColorMessage)
			{
				case COLOR_NEAR:
					Car_Stop();
					Switch_State(STATE_GO_GOAL);
					break;
				case COLOR_LEFT:
					Car_TurnLeft();
					break;
				case COLOR_RIGHT:
					Car_TurnRight();
					break;
				case COLOR_MIDDLE:
					Switch_State(STATE_GO_GOAL);
					break;
			}
			Car_Stop();
			Switch_State(STATE_GO_GOAL);
		}
	}
	public void STATE_GO_GOAL_func()
	{
		if (((m_SwitchMessage & (SWITCH_LEFT | SWITCH_RIGHT)) != 0))
		{
			Switch_State(STATE_RELEASE_BALL);
		}

		if (XConfig.USE_ROTATION_VECTOR)
		{
			if (m_ColorMessage == COLOR_NEAR) {
				//xe gần Goal nhưng góc lệch quá nhiều phải xoay lại
				if (m_VectorDetect.getX() < orientations[0] - ANGLE_DIFF) { //ANGLE_DIFF: góc lệch, hằng số
					Log.d("dung.levan", "right");
					Car_Rotate_Right(); // Xoay xe qua phải
				} else if (m_VectorDetect.getX() > orientations[0] + ANGLE_DIFF) {
					Log.d("dung.levan", "left");
					Car_Rotate_Left(); // Xoay xe qua trái
				} else//xe gần Goal và đã đúng góc chuyển state nhả banh
				{
					Car_Stop();
					Switch_State(STATE_RELEASE_BALL);
					return;
				}
			} else {
				//Xe vừa chạy vừa điều chỉnh khi chạy về goal
				if (m_VectorDetect.getX() < orientations[0] - ANGLE_DIFF_SMALL) {
					Car_TurnRight();
				} else if (m_VectorDetect.getX() > orientations[0] + ANGLE_DIFF_SMALL) {
					Car_TurnLeft();
				} else {
					Car_Forward();
				}
			}
		}

//		Car_Forward();

//        if(m_Detector.getBallRadius() < XConfig.GOAL_THRESHOLD_RADIUS)
//        {
//            Car_Backward();
//            Game_Sleep(XConfig.TIME_FOR_CLEAN_SHORT);
//        }

		if(m_Detector.getBallRadius() >= XConfig.GOAL_THRESHOLD_RADIUS)
		{
			Car_Forward();
		}
		else
		{
			switch (m_ColorMessage) {
				case COLOR_ZERO:// dang follow ball ma bi mat focus
					Car_Stop();
					Switch_State(STATE_FIND_GOAL);
					break;
				case COLOR_NEAR:
//				Car_Stop();
//				Switch_State(STATE_RELEASE_BALL);
					break;
				case COLOR_LEFT:
					Car_Rotate_Left();
					break;
				case COLOR_RIGHT:
					Car_Rotate_Right();
					break;
				case COLOR_MIDDLE:
					Car_Forward();
					break;
			}
		}
	}
	public void STATE_RELEASE_BALL_func() {
		Car_Stop();
        if (XConfig.isTEAM_STORMX) {
			Motor_Blow_Out();
			Servo1_Down();
			Game_Sleep(XConfig.TIME_FOR_BLOW_OUT);
			Motor_Stop();
			Servo1_Up();
			Game_Sleep(XConfig.TIME_FOR_SERVO1_UP);
			Car_Backward();
			Game_Sleep(XConfig.TIME_FOR_CAR_BACKWARD);
            Car_Stop();
            Switch_State(STATE_FIND_BALL);
        } else {
            //Chạy motor nghiêng khung

            //Chạy luôn hạ càng

            Game_Sleep(XConfig.SPIRIT_TIME_FRONT);
            Car_Backward();
            Game_Sleep(XConfig.TIME_FOR_CAR_BACKWARD);
            Car_Stop();
            Switch_State(STATE_FIND_BALL);
        }

	}
	
	public void Run()
	{
		switch(m_State)
		{
			case STATE_INIT:
				STATE_INIT_func();
			break;
			case STATE_FIND_BALL:
				STATE_FIND_BALL_func();
			break;
			case STATE_FOLLOW_BALL:
				STATE_FOLLOW_BALL_func();
			break;
			case STATE_CATCH_BALL:
				STATE_CATCH_BALL_func();
			break;
			case STATE_FIND_GOAL:
				STATE_FIND_GOAL_func();
			break;
			case STATE_CENTER_BALL:
				STATE_CENTER_BALL_func();
				break;
			case STATE_CLEAN_PATH:
			STATE_CLEAN_PATH_func();
			break;
			case STATE_GO_GOAL:
				STATE_GO_GOAL_func();
			break;
			case STATE_RELEASE_BALL:
				STATE_RELEASE_BALL_func();
			break;
		}
	}
	public void sendCommnand(String command)
    {
		if(!m_Bluetooth.getPrevSentMsg().equals(command))
		{
			m_Bluetooth.send(command);
		}
    }
	//Action method of Car
	public void Car_Stop()
	{
		sendCommnand(MESSEAGE_STOP);
	}
	public void Car_Rotate_Left()
	{
		sendCommnand(MESSEAGE_ROTATELEFT);
	}
	public void Car_Rotate_Right()
	{
		sendCommnand(MESSEAGE_ROTATERIGHT);
	}
	public void Car_TurnLeft()
	{
		sendCommnand(MESSEAGE_TURNLEFT);
	}
	public void Car_TurnRight()
	{
		sendCommnand(MESSEAGE_TURNRIGHT);
	}
	public void Car_Forward()
	{
		sendCommnand(MESSEAGE_FORWARDFAST);
	}
	public void Car_Backward()
	{
		sendCommnand(MESSEAGE_BACKWARD);
	}
	public void Motor_Blow_In()
	{
		sendCommnand(MESSEAGE_MOTOR_BLOW_IN);
	}
	public void Motor_Blow_Out()
	{
		sendCommnand(MESSEAGE_MOTOR_BLOW_OUT);
	}
	public void Motor_Stop()
	{
		sendCommnand(MESSEAGE_MOTOR_STOP);
	}
	public void Servo1_Down()
	{
		sendCommnand(MESSEAGE_SERVO1_DOWN);
	}
	public void Servo1_Down_Release_Ball()
	{
		sendCommnand(MESSEAGE_SERVO1_DOWN_RELEASE_BALL);
	}
	public void Servo1_Up()
	{
		sendCommnand(MESSEAGE_SERVO1_UP);
	}

	public static void setAndroidStarted(boolean val)
	{
		ANDROID_STARTED = val;
	}

	public static void setAndroidInitialized(boolean val)
	{
		ANDROID_INITIALIZED = val;
	}

	public void askHoldingBallStatus()
	{
		sendCommnand(MESSAGE_ASK_BALL_STATUS);
	}

	public void setHoldingBallStatus(boolean val)
	{
		m_isHoldingBall = val;
	}

    public float getX() {
		if(XConfig.USE_ROTATION_VECTOR)
        	return m_VectorDetect.getX();
		else
			return 0;
    }

	public float getY() {
		if(XConfig.USE_ROTATION_VECTOR)
			return m_VectorDetect.getY();
		else
			return 0;
	}

	public float getZ() {
		if(XConfig.USE_ROTATION_VECTOR)
			return m_VectorDetect.getZ();
		else
			return 0;
	}
	public  int[] getOrientations(){
		return orientations;
	}

	public void onDestroy(){
		if (m_Bluetooth != null)
			m_Bluetooth.Disconnect();
		if (m_VectorDetect != null)
			m_VectorDetect.unRegisterListener();
	}
	public void onPause(){
		if (m_VectorDetect != null)
			m_VectorDetect.unRegisterListener();
	}
	public void onResume(){
		if (m_VectorDetect != null)
			m_VectorDetect.registerListener();
	}
	public int getState(){
        return m_State;
    }

	public int getColorMessage(){
		return m_ColorMessage;
	}
	
    public String getGyroscopeInfo () {
		if (m_Gyroscope != null)
			return "Pos: " + m_Gyroscope.getCurrentDirection() + " | Degree: " + m_Gyroscope.getCurrentDegree();
		return "Gyroscope";
	}

}
