// Controller.h
// Mai Tien
#ifndef Controller_h
#define Controller_h

#include <stdlib.h>
#include <AccelStepper.h>
#include <Servo.h>
#include "Macro.h"

class Controller
{
public:
    typedef enum
    {
  		STOP		= 0,
  		FORWARD		= 1,
  		BACKWARD	= 2,
  		ROTATE		= 3,
      GOTO      = 4,
    } State;
	
	  //Control Servo
    Controller(uint8_t servo);
	
	  //Control Motor
    Controller(uint8_t step, uint8_t dir, uint8_t enable = 8);

    void run();

    long getMotorSpeed();
    void setMotorSpeed(long speed);
    void setMotorAcceleration(int acceleration = 1000);
    
    void setTargetPosition(long pos);
    
    uint8_t getState();
    void setState(uint8_t stated);

    void setServoInterval(unsigned long interval = 500);
    
    ~Controller();
    
private:
    AccelStepper*   _stepper;
	  uint8_t         _enablePin;
    long            _speed;
    
    uint8_t         _state;
    long            _currentPos;
    long            _targetPos;
    
    Servo*          _servo;
    unsigned long   _stepInterval;
    unsigned long   _lastStepTime;
};

#endif //Controller_h
