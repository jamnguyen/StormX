#include "Controller.h"

Controller::Controller(uint8_t servo) {
  _servo = new Servo();
  _servo->attach(servo);
  _currentPos = _targetPos = _speed;
  _stepInterval = 500;
  _lastStepTime = micros();
  _enablePin = 0xff;
  _stepper = nullptr;
  _state = Controller::STOP;
  setServoInterval();
}

Controller::Controller(uint8_t step, uint8_t dir, uint8_t enable) {
	
	_enablePin = enable;
	pinAsOutput(_enablePin);
  digitalLow(_enablePin);
  
	_stepper = new AccelStepper(AccelStepper::FULL2WIRE, step, dir);
  _stepper->setMaxSpeed(2000);
  setMotorAcceleration();
  _speed = 0;
  _servo = nullptr;
  _state = Controller::STOP;
}

void Controller::run() {
  if(_stepper != nullptr) {
    switch(_state) {
      case Controller::GOTO:
        _stepper->runSpeedToPosition();
        break;
      case Controller::STOP:
      case Controller::FORWARD:
      case Controller::BACKWARD:
      case Controller::ROTATE:      
      default:      
        _stepper->runSpeed();
        break;
    }
  }
  /*
  if(_servo != nullptr) 
  {
    if(_stepInterval <= 0 || _currentPos == _targetPos) return;
    unsigned long time = micros();   
    _currentPos = _targetPos;
    _servo->write(_currentPos);
    if (time - _lastStepTime >= _stepInterval)
    {
      if (_currentPos < _targetPos) {
        _currentPos += 1;
      } else {
        _currentPos -= 1;
      }
      _servo->write(_currentPos);
      _lastStepTime = time;
    }
  }  
  */
}

long Controller::getMotorSpeed() {
  return _speed;
}

void Controller::setMotorSpeed(long speed) {
  _speed = speed;
  if(_speed == 0) {
    setState(Controller::STOP);
  } else if(_speed > 0) {
    setState(Controller::FORWARD);
  } else {
    setState(Controller::BACKWARD);
  }
  _stepper->setSpeed(_speed);
}

uint8_t Controller::getState() {
  return _state;
}

void Controller::setState(uint8_t state) {
  _state = state;
}

void Controller::setMotorAcceleration(int acceleration) {
  _stepper->setAcceleration(acceleration);
}

void Controller::setServoInterval(unsigned long interval) {
  _stepInterval = interval;
}

void Controller::setTargetPosition(long pos)
{
  if(_stepper!=nullptr)
  {
    _stepper->move(pos);
    setState(Controller::GOTO);
  }
  else
  {
    _targetPos = pos;
  }
}

Controller::~Controller() {
  if(_servo != nullptr) {
    delete _servo;
  }
  if(_stepper != nullptr) {
    delete _stepper;
  }
}

