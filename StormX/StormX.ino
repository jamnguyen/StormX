#define MOTOR_LEFT_STEP_PIN         2  // Uno Digital Pin 2
#define MOTOR_LEFT_DIRECTION_PIN    5  // Uno Digital Pin 5
#define LIMIT_LEFT_PIN              11  // Uno Digital Pin 11

#define MOTOR_RIGHT_STEP_PIN        4  // Uno Digital Pin 3
#define MOTOR_RIGHT_DIRECTION_PIN   7  // Uno Digital Pin 7
#define LIMIT_RIGHT_PIN             9  // Uno Digital Pin 9

#define HAVE_BALL_PIN               12

#define MOTOR_MID_PIN_A1            3
#define MOTOR_MID_PIN_A2            6

#define SERVO_1_PIN                 10  // Uno Digital Pin 10

#define BAUD_RATE                   9600

#define MESSEAGE_FORWARDFAST        'F'
#define MESSEAGE_FORWARDSLOW        'A'
#define MESSEAGE_BACKWARD           'B'


#define MESSEAGE_ROTATELEFT         'L'
#define MESSEAGE_TURNLEFT           'K'
#define MESSEAGE_ROTATERIGHT        'R'
#define MESSEAGE_TURNRIGHT          'T'
#define MESSEAGE_STOP               'S'
#define MESSEAGE_ROTATELEFT180      'X'
#define MESSEAGE_ROTATERIGHT180     'Y'

#define MESSEAGE_MOTOR_BLOW_IN      'C'
#define MESSEAGE_MOTOR_BLOW_OUT     'D'
#define MESSEAGE_MOTOR_STOP         'E'

#define MESSEAGE_SERVO1_UP          'G'
#define MESSEAGE_SERVO1_DOWN        'H'
#define MESSEAGE_SERVO1_DOWN_RELEASE_BALL  'M'  
#define MESSEAGE_SERVO2_OPEN        'I'
#define MESSEAGE_SERVO2_CLOSE       'J'
#define MESSEAGE_IS_HAVE_BALL       'Z'

#define MESSEAGE_LIMIT_NONE         '0'
#define MESSEAGE_LIMIT_LEFT         '2'
#define MESSEAGE_LIMIT_RIGHT        '1'
#define MESSEAGE_LIMIT_BOTH         '3'
#define MESSEAGE_HAVE_BALL          '4'
#define MESSEAGE_NOT_HAVE_BALL      '5'

#define MOTOR_SPEED_BLOW            (255) //0 - 255

#define MOTOR_SPEED_MIN             1000
#define MOTOR_SPEED_ROTATE_MAX      1100
#define MOTOR_SPEED_ROTATE_MIN      1000
#define MOTOR_SPEED_ROTATE          370
#define MOTOR_SPEED_MED             1100
#define MOTOR_SPEED_MAX             1200

#define SERVO_UP_ANGLE              150
#define SERVO_DOWN_ANGLE            85
#define SERVO_DOWN_RELEASEBALL_ANGLE 115
#define SERVO_OPEN_ANGLE            0
#define SERVO_CLOSE_ANGLE           180

//#define DEBUG


#include "Controller.h"

Controller _motor_left(MOTOR_LEFT_STEP_PIN, MOTOR_LEFT_DIRECTION_PIN);
Controller _motor_right(MOTOR_RIGHT_STEP_PIN, MOTOR_RIGHT_DIRECTION_PIN);
//Controller _servo_1(SERVO_1_PIN);
//Controller _servo_2(SERVO_2_PIN);
Servo _servo_1;
//Servo _servo_2;

char _last_limit_command;
unsigned long   _lastTime;

void setup() {
  Serial.begin(BAUD_RATE);

  //LIMIT LEFT
  pinAsInputPullUp(LIMIT_LEFT_PIN);

  //LIMIT RIGHT
  pinAsInputPullUp(LIMIT_RIGHT_PIN);
  
  //HAVE_BALL_PIN
  pinAsInputPullUp(HAVE_BALL_PIN);

  //
  pinAsOutput(13);
  digitalLow(13);

  //MOTOR_MID  
  pinAsOutput(MOTOR_MID_PIN_A1);
  pinAsOutput(MOTOR_MID_PIN_A2);
  Dung();

  //servo
  _servo_1.attach(SERVO_1_PIN);
  _servo_1.write(SERVO_UP_ANGLE);
  //_servo_2.attach(SERVO_2_PIN);
  //_servo_2.write(SERVO_CLOSE_ANGLE);  
  _lastTime = micros();
}

void loop() {
  while (true) {
    unsigned long curr = micros();    
      if(curr - _lastTime >= (100l * 1000l)) {    
      //
      int limit_left = digitalState(LIMIT_LEFT_PIN);
      int limit_right = digitalState(LIMIT_RIGHT_PIN);
      if (limit_left == LOW && limit_right == LOW) {
        writeCommand(MESSEAGE_LIMIT_BOTH);
        digitalHigh(13);
        Log("MESSEAGE_LIMIT_BOTH");
      } else if (limit_left == LOW) {
        writeCommand(MESSEAGE_LIMIT_LEFT);
        digitalHigh(13);
        Log("MESSEAGE_LIMIT_LEFT");
      } else if (limit_right == LOW) {
        writeCommand(MESSEAGE_LIMIT_RIGHT);
        digitalHigh(13);
        Log("MESSEAGE_LIMIT_RIGHT");
      } else {
        writeCommand(MESSEAGE_LIMIT_NONE);
        digitalLow(13);
      }
      //
      _lastTime = curr;
    }
    char cmd = readCommand();
    switch (cmd) {
      case MESSEAGE_FORWARDFAST:
        Log("MESSEAGE_FORWARDFAST");
        _motor_left.setMotorSpeed(MOTOR_SPEED_MAX);
        _motor_right.setMotorSpeed(MOTOR_SPEED_MAX);
        break;
      case MESSEAGE_FORWARDSLOW:
        Log("MESSEAGE_FORWARDSLOW");
        _motor_left.setMotorSpeed(MOTOR_SPEED_MIN);
        _motor_right.setMotorSpeed(MOTOR_SPEED_MIN);
        break;
      case MESSEAGE_BACKWARD:
        Log("MESSEAGE_BACKWARD");
        _motor_left.setMotorSpeed(-MOTOR_SPEED_MIN);
        _motor_right.setMotorSpeed(-MOTOR_SPEED_MIN);
        break;
      case MESSEAGE_ROTATELEFT:
        Log("MESSEAGE_ROTATELEFT");
        _motor_left.setMotorSpeed(-MOTOR_SPEED_ROTATE);
        _motor_right.setMotorSpeed(MOTOR_SPEED_ROTATE);
        break;
      case MESSEAGE_TURNLEFT :
        Log("MESSEAGE_TURNLEFT");
        _motor_left.setMotorSpeed(MOTOR_SPEED_ROTATE_MIN);
        _motor_right.setMotorSpeed(MOTOR_SPEED_ROTATE_MAX);
        break;
      case MESSEAGE_ROTATERIGHT :
        Log("MESSEAGE_ROTATERIGHT");
        _motor_left.setMotorSpeed(MOTOR_SPEED_ROTATE);
        _motor_right.setMotorSpeed(-MOTOR_SPEED_ROTATE);
        break;
      case MESSEAGE_TURNRIGHT:
        Log("MESSEAGE_TURNRIGHT");
        _motor_left.setMotorSpeed(MOTOR_SPEED_ROTATE_MAX);
        _motor_right.setMotorSpeed(MOTOR_SPEED_ROTATE_MIN);
        break;
      case MESSEAGE_STOP:
        Log("MESSEAGE_STOP");
        _motor_left.setMotorSpeed(0);
        _motor_right.setMotorSpeed(0);
        break;
      case MESSEAGE_ROTATELEFT180:
        _motor_right.setTargetPosition(180);
        break;
      case MESSEAGE_ROTATERIGHT180:
        _motor_left.setTargetPosition(180);
        break;
      case MESSEAGE_MOTOR_BLOW_IN:
        Hut();
        break;
      case MESSEAGE_MOTOR_BLOW_OUT:
        Thoi();
        break;
      case MESSEAGE_MOTOR_STOP:
        Dung();
        break;
      case MESSEAGE_SERVO1_UP:
        Log("SERVO_UP_ANGLE");
        //_servo_1.setTargetPosition(SERVO_UP_ANGLE);
        _servo_1.write(SERVO_UP_ANGLE);
        break;
      case MESSEAGE_SERVO1_DOWN:
        Log("SERVO_DOWN_ANGLE");
        //_servo_1.setTargetPosition(SERVO_DOWN_ANGLE);
        _servo_1.write(SERVO_DOWN_ANGLE);
        break;
      case MESSEAGE_SERVO1_DOWN_RELEASE_BALL:
        Log("SERVO_DOWN_RELEASEBALL_ANGLE");
        _servo_1.write(SERVO_DOWN_RELEASEBALL_ANGLE);
      case MESSEAGE_SERVO2_OPEN:
        //_servo_2.setTargetPosition(SERVO_OPEN_ANGLE);
        //_servo_2.write(SERVO_OPEN_ANGLE);
        break;
      case MESSEAGE_SERVO2_CLOSE:
        //_servo_2.setTargetPosition(SERVO_CLOSE_ANGLE);
        //_servo_2.write(SERVO_CLOSE_ANGLE);
        break;
      case MESSEAGE_IS_HAVE_BALL:
      {
        int have_ball = digitalState(HAVE_BALL_PIN);
        if(have_ball == LOW) {
          Log("MESSEAGE_HAVE_BALL");
          Serial.println(MESSEAGE_HAVE_BALL);
        } else {
          Log("MESSEAGE_NOT_HAVE_BALL");
          Serial.println(MESSEAGE_NOT_HAVE_BALL);
        }
      }
        break;
      default:
        break;
    }
    _motor_left.run();
    _motor_right.run();
    //_servo_1.run();
    //_servo_2.run();
  }
}

char readCommand() {
  char cmd = Serial.available() ? Serial.read() : 0;
  return ('a' <= cmd && cmd <= 'z') ? cmd - 32 : cmd;
}

void writeCommand(char cmd) {
  if (_last_limit_command != cmd) {
    _last_limit_command = cmd;
    Serial.println(_last_limit_command);
  }
}

void Log(String str) {
#ifdef DEBUG
  Serial.println("Log : " + str);
#endif //DEBUG
}

void Hut()
{
  analogWrite(MOTOR_MID_PIN_A1, 255);
  digitalLow(MOTOR_MID_PIN_A2);
}

void Thoi()
{
  digitalLow(MOTOR_MID_PIN_A1);
  analogWrite(MOTOR_MID_PIN_A2, 255);  
}

void Dung()
{
  digitalLow(MOTOR_MID_PIN_A1);
  digitalLow(MOTOR_MID_PIN_A2);
}


