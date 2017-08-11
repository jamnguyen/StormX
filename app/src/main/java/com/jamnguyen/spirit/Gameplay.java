package com.jamnguyen.spirit;
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
	public static final String MESSEAGE_Container_Up        =   "O"; //Nâng thùng lên
	public static final String MESSEAGE_Container_Down      =  "N"; //Hạ thùng xuống
	public static final String MESSEAGE_MOTOR_MIDDLE_STOP      =  "P"; //thùng stop

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
	public static final int STATE_FOLLOW_BALL_IN_CORNER = 9;
	public static final int STATE_FIND_BALL_LEFT = 10;
	public static final int STATE_FIND_BALL_RIGHT = 11;
	public static final int STATE_TEST = 12;//muốn test gì thì test vào đây
	
	public long m_CurrentTime = 0;
	public long m_InitTime = 0;
	// public static final int CONTAINER_STATE_STOP = 0;
	// public static final int CONTAINER_STATE_UP = 1;
	// public static final int CONTAINER_STATE_DOWN = 2;
	// public int m_ContainerState = CONTAINER_STATE_STOP; //0:st

	public static boolean ANDROID_STARTED = false;
	public static boolean ANDROID_INITIALIZED = false;
	public static int m_BallCount = 0;//đếm số banh hiện tại//Số lần lấy ball
	/// Gyroscope
	private static Gyroscope m_Gyroscope = null;
	private static int m_MomentInit = 0;
	private static int  m_MomentOfPhone      = XConfig.GYROSCOPE_MOMENT_DEFAULT;//phone của Tiến

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
			orientations = new int[3];
		}
		if(XConfig.USE_GYROSCOPE)
		{
			m_Gyroscope = new Gyroscope(context);
		}
		//m_VectorInit = null;
		m_InitTime = System.currentTimeMillis();
		m_CurrentTime = System.currentTimeMillis();
		m_State = STATE_INIT;
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
		m_ColorMessage = 0;
		m_BallCount = 0;
		m_Detector.init();
        //Set GOAL vector
        Log.d("dung.levan","STATE_INIT_func");
		ANDROID_INITIALIZED = true;
		if(XConfig.USE_ROTATION_VECTOR)
		{
			orientations[0] = (int) m_VectorDetect.getX();
			orientations[1] = (int)m_VectorDetect.getY();
			orientations[2] = (int)m_VectorDetect.getZ();
			Log.d("dung.levan","orientations " + orientations[0] + " - " + orientations[1] + " - " + orientations[2]);
		}
		if(XConfig.USE_GYROSCOPE)
		{
			m_MomentInit = m_Gyroscope.getMoment();
		}
		switch(m_Detector.getTouchDirection())
		{
			case XDetector.TOUCH_CENTER:
				Switch_State(STATE_FIND_BALL);
			break;
			case XDetector.TOUCH_LEFT:
				Switch_State(STATE_FIND_BALL_LEFT);
			break;
			case XDetector.TOUCH_RIGHT:
				Switch_State(STATE_FIND_BALL_RIGHT);
			break;
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
		if(m_ColorMessage == COLOR_MIDDLE || m_ColorMessage == COLOR_NEAR)
		{
			Car_Stop();//Nên Stop
			Switch_State(STATE_FOLLOW_BALL);
			return;
		}
		//nhìn thấy bóng và đụng cả hai công tắc
		if(m_ColorMessage != COLOR_ZERO && (m_SwitchMessage & (SWITCH_LEFT|SWITCH_RIGHT)) != 0)
		{
			Car_Stop();//Nên Stop
			Switch_State(STATE_FOLLOW_BALL_IN_CORNER);
			return;
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
		//nhìn thấy bóng và đụng cả hai công tắc
		if(m_ColorMessage != COLOR_ZERO && (m_SwitchMessage & (SWITCH_LEFT|SWITCH_RIGHT)) != 0)
		{
			Car_Stop();//Nên Stop
			Switch_State(STATE_FOLLOW_BALL_IN_CORNER);
			return;
		}
		switch(m_ColorMessage)
		{
			case COLOR_ZERO:// dang follow ball ma bi mat focus 
			Car_Stop();
			Switch_State(STATE_FIND_BALL);
			break;
			case COLOR_NEAR:
			Car_Forward();
			Game_Sleep(XConfig.TIME_FOR_CATCH_BALL);
			m_BallCount++;
			if(m_BallCount >= XConfig.SPIRIT_NUM_CATCH_BALL)
			{
				Switch_State(STATE_FIND_GOAL);
			}
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
	public void STATE_FOLLOW_BALL_IN_CORNER_func()
	{
		Car_Rotate_Right();
		Game_Sleep(XConfig.TIME_FOR_ROTATE_IN_CORNER);
		Car_Forward();
		Game_Sleep(XConfig.TIME_FOR_ROTATE_IN_CORNER);
		m_BallCount++;
		if(m_BallCount >= XConfig.SPIRIT_NUM_CATCH_BALL)
		{
			Switch_State(STATE_FIND_GOAL);
			return;
		}
		Switch_State(STATE_FIND_BALL);
	}
	public int getCarDegree()
	{
		int moment = (m_Gyroscope.getMoment() - m_MomentInit)%m_MomentOfPhone;
		if(moment > m_MomentOfPhone/2)
			moment = moment - m_MomentOfPhone;
		else if(moment < -m_MomentOfPhone/2)
			moment = moment + m_MomentOfPhone;
		return (int)((double)moment*360.0/(double)m_MomentOfPhone);
	}
	public void STATE_FIND_GOAL_func()
	{
		if(XConfig.USE_GYROSCOPE)
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
		//trường hợp ko dùng XConfig.USE_GYROSCOPE
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
				case COLOR_MIDDLE:
					Car_Stop();
					Switch_State(STATE_GO_GOAL);
					break;
				case COLOR_LEFT:
					Car_Rotate_Left();
					break;
				case COLOR_RIGHT:
					Car_Rotate_Right();
					break;
			}
		}
	}
	public void STATE_GO_GOAL_func()
	{
		if(XConfig.USE_GYROSCOPE)
		{
			int degree = getCarDegree();
			if(degree >= XConfig.DEGREE_DELTA || degree <= - XConfig.DEGREE_DELTA)//xe lệch quá nhiều
			{
				Switch_State(STATE_FIND_GOAL);
				return;
			}
			else if(degree > - XConfig.DEGREE_DELTA_SMALL && degree < XConfig.DEGREE_DELTA_SMALL )//xe đã đúng hướng
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
				if(degree >= XConfig.DEGREE_DELTA_SMALL)//hơi lệch bên phải thì quẹo trái
					Car_TurnLeft();
				else //hơi lệch bên trái thì quẹo phải
					Car_TurnRight();
			}
			return;
		}
		//trường hợp ko dùng XConfig.USE_GYROSCOPE
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

		if (((m_SwitchMessage & (SWITCH_LEFT | SWITCH_RIGHT)) != 0))//đụng cả hai công tắc
		{
			Car_Stop();
			Switch_State(STATE_RELEASE_BALL);
			return;
		}


		switch (m_ColorMessage) {
			case COLOR_ZERO:// dang follow ball ma bi mat focus
				Car_Stop();
				Switch_State(STATE_FIND_GOAL);
				break;
			case COLOR_NEAR:
				Car_Stop();
				Switch_State(STATE_RELEASE_BALL);
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
	public void Container_Up()
	{
		sendCommand(MESSEAGE_Container_Up);
	}
	public void Container_Down()
	{
		sendCommand(MESSEAGE_Container_Down);
	} 
	public void Container_Stop()
	{
		sendCommand(MESSEAGE_MOTOR_MIDDLE_STOP);
	} 
	public void STATE_FIND_BALL_LEFT_func()
	{
		Car_Rotate_Left();
		Game_Sleep(100);
		Car_Forward();
		Game_Sleep(100);
		Car_Rotate_Right();
		Game_Sleep(100);
		// 
		while(true)//ko đụng cả 2 công tắc hoặc chỉ đụng 1 cái
		{
			if((m_SwitchMessage & (SWITCH_LEFT | SWITCH_RIGHT)) != 0)//Đụng cả hai công tắc
			{
				Car_Stop();
				Switch_State(STATE_RELEASE_BALL);
				return;
			}
			if((m_SwitchMessage & SWITCH_LEFT) != 0)//Đụng left
				Car_TurnRight();
			else if((m_SwitchMessage & SWITCH_RIGHT) != 0)//Đụng Right
				Car_TurnLeft();
			else
				Car_Forward();
		}
	}
	public void STATE_FIND_BALL_RIGHT_func()
	{
		Car_Rotate_Right();
		Game_Sleep(100);
		Car_Forward();
		Game_Sleep(100);
		Car_Rotate_Left();
		Game_Sleep(100);
		// 
		while(true)
		{
			if((m_SwitchMessage & (SWITCH_LEFT | SWITCH_RIGHT)) != 0)//Đụng cả hai công tắc
			{
				Car_Stop();
				Switch_State(STATE_RELEASE_BALL);
				return;
			}
			if((m_SwitchMessage & SWITCH_LEFT) != 0)//Đụng left
				Car_TurnRight();
			else if((m_SwitchMessage & SWITCH_RIGHT) != 0)//Đụng Right
				Car_TurnLeft();
			else
				Car_Forward();
		}
	}
	public void STATE_TEST_func()
	{
		
	}
			
	public void STATE_RELEASE_BALL_func() {
		m_BallCount = 0;
		// Car_Backward();
		// Game_Sleep(XConfig.TIME_FOR_BACKWARD_WHEN_GOAL);
		Container_Up();
		// Car_Forward();
		// Game_Sleep(XConfig.TIME_FOR_FORWARD_WHEN_GOAL);
		Game_Sleep(XConfig.TIME_FOR_RELEASE_BALL);
		Container_Down();
		Switch_State(STATE_FIND_BALL);
	}
	
	public void Run()
	{
		if((XConfig.SPIRIT_NUM_CATCH_BALL > 1)&&(XConfig.TIME_ALL_GAME - (System.currentTimeMillis() - m_InitTime) < XConfig.TIME_REMAIN_FOR_BACK_GOAL))//trường hợp muốn lấy banh nhiều lần
		{
			Switch_State(STATE_FIND_GOAL);
			STATE_FIND_GOAL_func();
			return;
		}
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
			case STATE_FOLLOW_BALL_IN_CORNER:
				STATE_FOLLOW_BALL_IN_CORNER_func();
			break;
			case STATE_CATCH_BALL:
				//STATE_CATCH_BALL_func();
			break;
			case STATE_FIND_GOAL:
				STATE_FIND_GOAL_func();
			break;
			case STATE_CENTER_BALL:
				//STATE_CENTER_BALL_func();
				break;
			case STATE_CLEAN_PATH:
			//STATE_CLEAN_PATH_func();
			break;
			case STATE_GO_GOAL:
				STATE_GO_GOAL_func();
			break;
			case STATE_RELEASE_BALL:
				STATE_RELEASE_BALL_func();
			break;
			case STATE_FIND_BALL_LEFT:
				STATE_FIND_BALL_LEFT_func();
			break;
			case STATE_FIND_BALL_RIGHT:
				STATE_FIND_BALL_RIGHT_func();
			break;
			case STATE_TEST:
				STATE_TEST_func();
			break;
		}
	}
	public void sendCommand(String command)
    {
		if(!m_Bluetooth.getPrevSentMsg().equals(command))
		{
			m_Bluetooth.send(command);
		}
    }
	//Action method of Car
	public void Car_Stop()
	{
		sendCommand(MESSEAGE_STOP);
	}
	public void Car_Rotate_Left()
	{
		sendCommand(MESSEAGE_ROTATELEFT);
	}
	public void Car_Rotate_Right()
	{
		sendCommand(MESSEAGE_ROTATERIGHT);
	}
	public void Car_TurnLeft()
	{
		sendCommand(MESSEAGE_TURNLEFT);
	}
	public void Car_TurnRight()
	{
		sendCommand(MESSEAGE_TURNRIGHT);
	}
	public void Car_Forward()
	{
		sendCommand(MESSEAGE_FORWARDFAST);
	}
	public void Car_Forward_Slow()
	{
		sendCommand(MESSEAGE_FORWARDSLOW);
	}
	public void Car_Backward()
	{
		sendCommand(MESSEAGE_BACKWARD);
	}
	public void Motor_Blow_In()
	{
		sendCommand(MESSEAGE_MOTOR_BLOW_IN);
	}
	public void Motor_Blow_Out()
	{
		sendCommand(MESSEAGE_MOTOR_BLOW_OUT);
	}
	public void Motor_Stop()
	{
		sendCommand(MESSEAGE_MOTOR_STOP);
	}
	public void Servo1_Down()
	{
		sendCommand(MESSEAGE_SERVO1_DOWN);
	}
	public void Servo1_Down_Release_Ball()
	{
		sendCommand(MESSEAGE_SERVO1_DOWN_RELEASE_BALL);
	}
	public void Servo1_Up()
	{
		sendCommand(MESSEAGE_SERVO1_UP);
	}

	public static void setAndroidStarted(boolean val)
	{
		ANDROID_STARTED = val;
	}

	public static void setAndroidInitialized(boolean val)
	{
		ANDROID_INITIALIZED = val;
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
			return "m_MomentInit: " + m_MomentInit + " currentMoment: " + (int)m_Gyroscope.getMoment() + " Degree: " + getCarDegree();
		return "Gyroscope";
	}

}
