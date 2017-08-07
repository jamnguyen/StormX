#ifndef __MOTOR_H__
#define __MOTOR_H__

#if defined(ARDUINO) && ARDUINO >= 100
#include "Arduino.h"
#else
#include "WProgram.h"
#include <pins_arduino.h>
#endif

class Motor {
public:
  Motor(int in1, int in2);
	Motor(int pwm, int in1, int in2);
	void stop();
  void setSpeed(int speed);
	void forward(int speed);
	void backward(int speed);
  void run();
private:
	unsigned int m_pwm;
	unsigned int m_in1;
	unsigned int m_in2;
	int m_speed;  
};
#endif //__MOTOR_H__
