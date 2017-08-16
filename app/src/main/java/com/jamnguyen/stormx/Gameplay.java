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

	public static final String MESSAGE_HAVEBALL = "4";
	public static final String MESSAGE_NOBALL = "5";
    public static final int SWITCH_LEFT = 0x10;
	public static final int SWITCH_RIGHT = 0x01;
    public static final int NUM_BALL = 1;
	public static final int ANGLE_DIFF = 6;
	public static final int ANGLE_DIFF_SMALL = 4;
	private XDetector m_Detector;
    private XBluetooth m_Bluetooth;
	private XVectorDetection m_VectorDetect = null;
	private float m_DegreeInit = 0;
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
	public static final int COLOR_TOO_FAR = 6;//ball qua xa

	private int m_State = -1;
	private int m_lastState = -1;
	public static final int STATE_INIT = 0;
	public static final int STATE_FIND_BALL = 1;//xoay tim ball cho den khi nao thay ball se dung
	public static final int STATE_FOLLOW_BALL = 2;//
	public static final int STATE_CATCH_BALL = 3;//
	public static final int STATE_FIND_GOAL = 4;
	public static final int STATE_GO_GOAL = 5;
	public static final int STATE_RELEASE_BALL = 6;
	public static final int STATE_CLEAN_PATH = 7;
	public static final int STATE_CENTER_BALL = 8;
	public static final int STATE_WAITING = 9;
	public static final int STATE_INIT_CHEAT = 10;

	
	public long m_CurrentTime = 0;
	public boolean m_isHoldingBall = false;

	public static boolean ANDROID_STARTED = false;
	public static boolean ANDROID_INITIALIZED = false;
	public static int m_BallCount = 0;//đếm số banh hiện tại
	/// Gyroscope
	public static Gyroscope m_Gyroscope = null;
	
	private static long m_startTime = 0;

	private boolean m_isInitCheated = false;
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
		 }
            // //Check if catching ball successful
            // Car_Stop();
            // askHoldingBallStatus();
            // Game_Sleep(XConfig.TIME_FOR_ASK);
            // if(!m_isHoldingBall)
            // {
                // Switch_State(STATE_FOLLOW_BALL);
            // }
		// } else 
		if (state == STATE_WAITING)
		{
			m_startTime = System.currentTimeMillis();
		}
		m_lastState = m_State;
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
			m_DegreeInit = m_VectorDetect.getX();
		}
		
		if(ANDROID_INITIALIZED)
		{
			Car_Forward();
			Game_Sleep(XConfig.TIME_FOR_CAR_FORWARD_INIT);
			if(m_isInitCheated)
			{
				Switch_State(STATE_FIND_BALL);
			}
			else
			{
				m_startTime = System.currentTimeMillis();
				m_isInitCheated = true;
				Motor_Blow_In();
				Servo1_Down();
				Switch_State(STATE_INIT_CHEAT);
			}
		}
		//Switch_State(STATE_FOLLOW_BALL);
//		Switch_State(STATE_FIND_GOAL); // Đang test về khung lưới
		ANDROID_INITIALIZED = true;
	}
	public void STATE_INIT_CHEAT_func()
	{
		long curr = System.currentTimeMillis();
		if(curr - m_startTime > XConfig.TIME_FOR_SERVO1_DOWN_CHEAT)
		{
			Motor_Stop();
			Servo1_Up();
		}
		if ((m_SwitchMessage & SWITCH_LEFT) != 0 && (m_SwitchMessage & SWITCH_RIGHT) != 0)//Đụng cả 2 công tắc
		{
			Motor_Blow_Out();
			Servo1_Down();
			Car_Stop();
		}
		else if ((m_SwitchMessage & SWITCH_LEFT) != 0 || (m_SwitchMessage & SWITCH_RIGHT) != 0)//Đụng 1 trong 2 công tắc thì chạy lùi lại
		{
			Car_Forward();
			Servo1_Down();
		}
		else//ko đụng công tắc nào
		{
			int degree = getCarDegree();
			if(degree > XConfig.DEGREE_DELTA_SMALL)
				Car_TurnLeft();
			else if(degree < -XConfig.DEGREE_DELTA_SMALL)
				Car_TurnRight();
			else
				Car_Forward();
		}
		if(curr - m_startTime > XConfig.TIME_FOR_INIT_CHEAT)
		{
			Servo1_Up();
			Car_Backward();
			Game_Sleep(XConfig.TIME_FOR_CAR_BACKWARD);
			Switch_State(STATE_FIND_BALL);
		}
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
		if(m_ColorMessage == COLOR_MIDDLE)
		{
			// Car_Stop();
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
			// Car_Stop();
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
		Game_Sleep(XConfig.TIME_FOR_CLEAN_SHORT);
		Car_Stop();

		askHoldingBallStatus();
		// Game_Sleep(XConfig.TIME_FOR_ASK);

		// if(m_isHoldingBall)
			// Switch_State(STATE_FIND_GOAL);
		// else
        	// Switch_State(STATE_FOLLOW_BALL);
		
		Switch_State(STATE_WAITING);
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
			Servo1_Up();
			Game_Sleep(XConfig.TIME_FOR_CLEAN_SHORT);
			
			Motor_Stop();		

			Car_Stop();
			
			askHoldingBallStatus();
			
            Switch_State(STATE_WAITING);
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
        if(XConfig.USE_GYROSCOPE || XConfig.USE_ROTATION_VECTOR)
		{
			int degree = getCarDegree();
			if ((m_SwitchMessage & SWITCH_LEFT) != 0 || (m_SwitchMessage & SWITCH_RIGHT) != 0)//Đụng 1 trong 2 công tắc thì chạy lùi lại
			{
				Car_Backward();
				Game_Sleep(XConfig.TIME_FOR_CAR_BACKWARD);
			}
			else//ko đụng cái nào
			{
				if(degree > XConfig.DEGREE_DELTA)
					Car_Rotate_Left();
				else if(degree < - XConfig.DEGREE_DELTA)
					Car_Rotate_Right();
				else
					Switch_State(STATE_GO_GOAL);
			}
			return;
		}

		if(m_ColorMessage == COLOR_ZERO)
		{
			if((m_SwitchMessage & SWITCH_LEFT) != 0)
			{
				Car_Rotate_Right();
			}
			else if((m_SwitchMessage & SWITCH_RIGHT) != 0)
			{
				Car_Rotate_Left();
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
					Car_Rotate_Left();
					break;
				case COLOR_RIGHT:
                    Car_Rotate_Right();
					break;
				case COLOR_MIDDLE:
					Car_Stop();
					Switch_State(STATE_GO_GOAL);
					break;
			}
			// Car_Stop();
			// Switch_State(STATE_GO_GOAL);
		}
	}
	public int getCarDegree()
	{
		if(XConfig.USE_ROTATION_VECTOR)
		{
			//Dương bên phải, âm bên trái
			double degree = m_VectorDetect.getX() - m_DegreeInit;
			if(degree > 180.0)
				degree = (degree - 360.0);
			else if(degree < -180.0)
				degree = (360.0 + degree);
			return (int)degree;
		}
		//dùng con quay
		if(XConfig.USE_GYROSCOPE)
		{
		// int moment = (m_Gyroscope.getMoment() - m_MomentInit)%m_MomentOfPhone;
		// if(moment > m_MomentOfPhone/2)
			// moment = moment - m_MomentOfPhone;
		// else if(moment < -m_MomentOfPhone/2)
			// moment = moment + m_MomentOfPhone;
		// return (int)((double)moment*360.0/(double)m_MomentOfPhone);
		}
		return 0;
	}
	public void STATE_GO_GOAL_func()
	{
		if(XConfig.USE_GYROSCOPE || XConfig.USE_ROTATION_VECTOR)
		{
			int degree = getCarDegree();
			if(degree > XConfig.DEGREE_DELTA || degree < - XConfig.DEGREE_DELTA)//xe lệch quá nhiều
			{
				Switch_State(STATE_FIND_GOAL);
				return;
			}
			else if(degree >= - XConfig.DEGREE_DELTA_SMALL && degree <= XConfig.DEGREE_DELTA_SMALL )//xe đã đúng hướng
			{
				if (((m_SwitchMessage & (SWITCH_LEFT | SWITCH_RIGHT)) != 0))//Đụng cả 2 công tắc thì chuyển state nhả bóng
				{
					Car_Stop();
					Switch_State(STATE_RELEASE_BALL);
					return;
				}
				else if ((m_SwitchMessage & SWITCH_LEFT) != 0 || (m_SwitchMessage & SWITCH_RIGHT) != 0)//Đụng 1 trong 2 công tắc thì chạy chậm
					Car_Forward_Slow();
				else//ko đụng công tắc nào
				{
					Car_Forward();
				}
			}
			else //xe hơi lệch
			{
				if(degree > XConfig.DEGREE_DELTA_SMALL)//hơi lệch bên phải thì quẹo trái
					Car_TurnLeft();
				else //hơi lệch bên trái thì quẹo phải
					Car_TurnRight();
			}
			return;
		}
		//ko dùng vector
		if (((m_SwitchMessage & (SWITCH_LEFT | SWITCH_RIGHT)) != 0))
		{
			Switch_State(STATE_RELEASE_BALL);
		}

		

//		Car_Forward();

//        if(m_Detector.getBallRadius() < XConfig.GOAL_THRESHOLD_RADIUS)
//        {
//            Car_Backward();
//            Game_Sleep(XConfig.TIME_FOR_CLEAN_SHORT);
//        }

		switch (m_ColorMessage)
		{
			case COLOR_ZERO:// dang follow ball ma bi mat focus
				Car_Stop();
				Switch_State(STATE_FIND_GOAL);
				break;
			case COLOR_NEAR:
				Car_Forward();
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
	public void STATE_RELEASE_BALL_func() {
		Car_Stop();
        if (XConfig.isTEAM_STORMX) {
			Motor_Blow_Out();
			Servo1_Down();
			Game_Sleep(XConfig.TIME_FOR_BLOW_OUT);
			Motor_Stop();
			Servo1_Up();
			Game_Sleep(XConfig.TIME_FOR_SERVO1_UP);
			setHoldingBallStatus(false);
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
	
	public void STATE_WAITING_func() {
		long curr = System.currentTimeMillis();
		if(m_isHoldingBall)
		{
			switch(m_lastState)
			{
				case STATE_CLEAN_PATH:
				case STATE_CATCH_BALL:
					Switch_State(STATE_FIND_GOAL);
//					m_Detector.setDetectBall(false);
					break;
				default:
					break;
			}
			return;
		}
		if(curr - m_startTime >= XConfig.TIME_OUT)
		{
			Switch_State(STATE_FIND_BALL);			
//			m_Detector.setDetectBall(true);
		}
	}
	
	public void Run()
	{
		switch(m_State)
		{
			case STATE_INIT:
				STATE_INIT_func();
			break;
			case STATE_INIT_CHEAT:
				STATE_INIT_CHEAT_func();
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
			case STATE_WAITING:
				STATE_WAITING_func();
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
	public void Car_Forward_Slow()
	{
		sendCommnand(MESSEAGE_FORWARDSLOW);
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
		if (m_Gyroscope != null || m_VectorDetect != null)
			return "Degree: " + getCarDegree();
		return "Gyroscope";
	}

}
