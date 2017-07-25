package com.jamnguyen.stormx;
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
	public static final String MESSEAGE_SERVO2_OPEN              = "I";
	public static final String MESSEAGE_SERVO12_CLOSE            = "J";

    public static final int SWITCH_LEFT = 0X10;
    public static final int SWITCH_RIGHT = 0X01;
    public static final int NUM_BALL = 1;
	private XDetector m_Detector;
    private XBluetooth m_Bluetooth;
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
	
	public long m_CurrentTime = 0;
	public static final int TIME_FOR_BLOW_OUT = 100;
	public static final int TIME_FOR_BLOW_IN = 500;
	public static final int TIME_FOR_SERVO1_UP = 200;
	public static final int TIME_FOR_CAR_BACKWARD = 200;
	
	public Gameplay(){
		m_Bluetooth = null;
	}
	public Gameplay(XBluetooth BT, XDetector DT)
    {
        m_Bluetooth = BT;
        m_Detector = DT;
		m_CurrentTime = 0;
		m_ColorMessage = 0;
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
	
	public void STATE_INIT_func()
	{
		m_CurrentTime = 0;
		m_ColorMessage = 0;
		Switch_State(STATE_FIND_BALL);
	}
	public void Game_Sleep(long time)
	{
		try {
			Thread.sleep(time);
		}catch (Exception ex)
		{}
		// m_CurrentTime = System.currentTimeMillis();
		// while(System.currentTimeMillis() - m_CurrentTime < time)
		// {}
	}
	public void STATE_FIND_BALL_func()
	{
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
			Car_Stop();
			Switch_State(STATE_FOLLOW_BALL);
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
			Switch_State(STATE_CATCH_BALL);
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
	public void STATE_CATCH_BALL_func()
	{
		Motor_Blow_In();
		Servo1_Down();
		Game_Sleep(TIME_FOR_BLOW_IN);
		Servo1_Up();
		Game_Sleep(TIME_FOR_SERVO1_UP);
		Motor_Stop();
		Car_Backward();
		Game_Sleep(TIME_FOR_CAR_BACKWARD);
		Switch_State(STATE_FIND_GOAL);
	}
	public void STATE_FIND_GOAL_func()
	{
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
			Car_Stop();
			Switch_State(STATE_GO_GOAL);
		}
	}
	public void STATE_GO_GOAL_func()
	{
		if(m_ColorMessage == COLOR_ZERO)// Vi li do nao do mat focus goal
		{
			Car_Stop();
			Switch_State(STATE_FIND_GOAL);
			return;
		}
		if((m_SwitchMessage & SWITCH_LEFT) != 0 || (m_SwitchMessage & SWITCH_RIGHT) != 0)
		{
			Car_Stop();
			Switch_State(STATE_RELEASE_BALL);
			return;
		}
		Car_Forward();
	}
	public void STATE_RELEASE_BALL_func()
	{
		Motor_Blow_Out();
		Game_Sleep(TIME_FOR_BLOW_OUT);
		Motor_Stop();
		Car_Backward();
		Game_Sleep(TIME_FOR_CAR_BACKWARD);
		Car_Stop();
		Switch_State(STATE_FIND_BALL);
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
			case STATE_GO_GOAL:
				STATE_GO_GOAL_func();
			break;
			case STATE_RELEASE_BALL:
				STATE_RELEASE_BALL_func();
			break;
		}
	}
	
	//Action method of Car
	public void Car_Stop()
	{
		m_Bluetooth.send(MESSEAGE_STOP);
	}
	public void Car_Rotate_Left()
	{
		m_Bluetooth.send(MESSEAGE_ROTATELEFT);
	}
	public void Car_Rotate_Right()
	{
		m_Bluetooth.send(MESSEAGE_ROTATERIGHT);
	}
	public void Car_TurnLeft()
	{
		m_Bluetooth.send(MESSEAGE_TURNLEFT);
	}
	public void Car_TurnRight()
	{
		m_Bluetooth.send(MESSEAGE_TURNRIGHT);
	}
	public void Car_Forward()
	{
		m_Bluetooth.send(MESSEAGE_FORWARDFAST);
	}
	public void Car_Backward()
	{
		m_Bluetooth.send(MESSEAGE_BACKWARD);
	}
	public void Motor_Blow_In()
	{
		m_Bluetooth.send(MESSEAGE_MOTOR_BLOW_IN);
	}
	public void Motor_Blow_Out()
	{
		m_Bluetooth.send(MESSEAGE_MOTOR_BLOW_OUT);
	}
	public void Motor_Stop()
	{
		m_Bluetooth.send(MESSEAGE_MOTOR_STOP);
	}
	public void Servo1_Down()
	{
		m_Bluetooth.send(MESSEAGE_SERVO1_DOWN);
	}
	public void Servo1_Up()
	{
		m_Bluetooth.send(MESSEAGE_SERVO1_UP);
	}
}
