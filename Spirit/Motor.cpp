#include "Motor.h"

Motor::Motor(int in1, int in2){
  Motor(-1, in1, in2);
}

Motor::Motor(int pwm, int in1, int in2){
	m_pwm = pwm;
	m_in1 = in1;
	m_in2 = in2;
  
  if(m_pwm != -1) {
	  pinMode (m_pwm, OUTPUT);
  }
	pinMode (m_in1, OUTPUT);
	pinMode (m_in2, OUTPUT);

	stop();
}

void Motor::stop(){
	setSpeed(0);
}

void Motor::setSpeed(int speed){
  m_speed = constrain(m_speed, -255, 255);
}

void Motor::run(){
  if(m_speed == 0) {
    digitalWrite(m_in1, LOW);
    digitalWrite(m_in2, LOW);
  } else if (m_speed > 0){
    digitalWrite(m_in1, LOW);
    digitalWrite(m_in2, HIGH);
  } else {
    digitalWrite(m_in1, HIGH);
    digitalWrite(m_in2, LOW);
  }
  if(m_pwm != -1) {
    analogWrite(m_pwm, constrain(abs(m_speed), 0, 255));  
  }
}

void Motor::forward(int speed){
	setSpeed(speed);
}

void Motor::backward(int speed){
	setSpeed(-speed);
}
