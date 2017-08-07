#define MOTOR_LEFT_INA_PIN          5  // Uno Digital Pin 5
#define MOTOR_LEFT_IN1_PIN          6  // Uno Digital Pin 6
#define MOTOR_LEFT_IN2_PIN          7  // Uno Digital Pin 7
#define MOTOR_LEFT_LIMIT_PIN        11  // Uno Digital Pin 11

#define MOTOR_RIGHT_INA_PIN         8  // Uno Digital Pin 8
#define MOTOR_RIGHT_IN1_PIN         9  // Uno Digital Pin 8
#define MOTOR_RIGHT_IN2_PIN         10  // Uno Digital Pin 10
#define MOTOR_RIGHT_LIMIT_PIN       12  // Uno Digital Pin 12

#define MOTOR_MID_PIN_EN            3
#define MOTOR_MID_PIN_A1            2
#define MOTOR_MID_PIN_A2            4

#define LED_DEBUG                   13

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

#define MESSEAGE_LIMIT_NONE         '0'
#define MESSEAGE_LIMIT_LEFT         '2'
#define MESSEAGE_LIMIT_RIGHT        '1'
#define MESSEAGE_LIMIT_BOTH         '3'

#define MOTOR_SPEED_MIN             100
#define MOTOR_SPEED_ROTATE_MAX      200
#define MOTOR_SPEED_ROTATE_MIN      100
#define MOTOR_SPEED_ROTATE          150
#define MOTOR_SPEED_MED             200
#define MOTOR_SPEED_MAX             255

#include "Motor.h"

Motor _left(MOTOR_LEFT_INA_PIN, MOTOR_LEFT_IN1_PIN, MOTOR_LEFT_IN2_PIN);
Motor _middle(MOTOR_MID_PIN_EN, MOTOR_MID_PIN_A1, MOTOR_MID_PIN_A2);
Motor _right(MOTOR_RIGHT_INA_PIN, MOTOR_RIGHT_IN1_PIN, MOTOR_RIGHT_IN2_PIN);
char _last_limit_command;
unsigned long   _lastTime;

#define DEBUG

void setup()
{
  Serial.begin(BAUD_RATE);
  
  //LIMIT LEFT
  pinMode(MOTOR_LEFT_LIMIT_PIN, INPUT_PULLUP);

  //LIMIT RIGHT
  pinMode(MOTOR_RIGHT_LIMIT_PIN, INPUT_PULLUP);

  pinMode(LED_DEBUG, OUTPUT);
  digitalWrite(LED_DEBUG, LOW);
}

void loop()
{
   while (true) {
    unsigned long curr = micros();    
      if(curr - _lastTime >= (100l * 1000l)) {    
      //
      int limit_left = digitalRead(MOTOR_LEFT_LIMIT_PIN);
      int limit_right = digitalRead(MOTOR_RIGHT_LIMIT_PIN);
      if (limit_left == LOW && limit_right == LOW) {
        writeCommand(MESSEAGE_LIMIT_BOTH);
        digitalWrite(LED_DEBUG, HIGH);
        Log("MESSEAGE_LIMIT_BOTH");
      } else if (limit_left == LOW) {
        writeCommand(MESSEAGE_LIMIT_LEFT);
        digitalWrite(LED_DEBUG, HIGH);
        Log("MESSEAGE_LIMIT_LEFT");
      } else if (limit_right == LOW) {
        writeCommand(MESSEAGE_LIMIT_RIGHT);
        digitalWrite(LED_DEBUG, HIGH);
        Log("MESSEAGE_LIMIT_RIGHT");
      } else {
        writeCommand(MESSEAGE_LIMIT_NONE);
        digitalWrite(LED_DEBUG, LOW);
      }
      //
      _lastTime = curr;
    }
    char cmd = readCommand();
    switch (cmd) {
      case MESSEAGE_FORWARDFAST:
        Log("MESSEAGE_FORWARDFAST");
        _left.setSpeed(MOTOR_SPEED_MAX);
        _right.setSpeed(MOTOR_SPEED_MAX);
        break;
      case MESSEAGE_FORWARDSLOW:
        Log("MESSEAGE_FORWARDSLOW");
        _left.setSpeed(MOTOR_SPEED_MIN);
        _right.setSpeed(MOTOR_SPEED_MIN);
        break;
      case MESSEAGE_BACKWARD:
        Log("MESSEAGE_BACKWARD");
        _left.setSpeed(-MOTOR_SPEED_MIN);
        _right.setSpeed(-MOTOR_SPEED_MIN);
        break;
      case MESSEAGE_ROTATELEFT:
        Log("MESSEAGE_TURNLEFT");
        _left.setSpeed(-MOTOR_SPEED_ROTATE);
        _right.setSpeed(MOTOR_SPEED_ROTATE);
        break;
      case MESSEAGE_TURNLEFT :
        Log("MESSEAGE_RUNLEFT");
        _left.setSpeed(MOTOR_SPEED_ROTATE_MIN);
        _right.setSpeed(MOTOR_SPEED_ROTATE_MAX);
        break;
      case MESSEAGE_ROTATERIGHT :
        Log("MESSEAGE_TURNRIGHT");
        _left.setSpeed(MOTOR_SPEED_ROTATE);
        _right.setSpeed(-MOTOR_SPEED_ROTATE);
        break;
      case MESSEAGE_TURNRIGHT:
        Log("MESSEAGE_RUNRIGHT");
        _left.setSpeed(MOTOR_SPEED_ROTATE_MAX);
        _right.setSpeed(MOTOR_SPEED_ROTATE_MIN);
        break;
      case MESSEAGE_STOP:
        Log("MESSEAGE_STOP");
        _left.setSpeed(0);
        _right.setSpeed(0);
        break;
      case MESSEAGE_ROTATELEFT180:
        Log("MESSEAGE_ROTATELEFT180");
        //_right.setTargetPosition(180);
        break;
      case MESSEAGE_ROTATERIGHT180:
        Log("MESSEAGE_ROTATERIGHT180");
        //_left.setTargetPosition(180);
        break;
      case MESSEAGE_MOTOR_BLOW_IN:
        Log("MESSEAGE_MOTOR_BLOW_IN");
        //Hut();
        break;
      case MESSEAGE_MOTOR_BLOW_OUT:
        Log("MESSEAGE_MOTOR_BLOW_OUT");
        //Thoi();
        break;
      case MESSEAGE_MOTOR_STOP:
        Log("MESSEAGE_MOTOR_STOP");
        //Dung();
        break;
      case MESSEAGE_SERVO1_UP:
        Log("SERVO_UP_ANGLE");
        //_servo_1.setTargetPosition(SERVO_UP_ANGLE);
        //_servo_1.write(SERVO_UP_ANGLE);
        break;
      case MESSEAGE_SERVO1_DOWN:
        Log("SERVO_DOWN_ANGLE");
        //_servo_1.setTargetPosition(SERVO_DOWN_ANGLE);
        //_servo_1.write(SERVO_DOWN_ANGLE);
        break;
      case MESSEAGE_SERVO1_DOWN_RELEASE_BALL:
        Log("SERVO_DOWN_RELEASEBALL_ANGLE");
        //_servo_1.write(SERVO_DOWN_RELEASEBALL_ANGLE);
      case MESSEAGE_SERVO2_OPEN:
        //_servo_2.setTargetPosition(SERVO_OPEN_ANGLE);
        //_servo_2.write(SERVO_OPEN_ANGLE);
        break;
      case MESSEAGE_SERVO2_CLOSE:
        //_servo_2.setTargetPosition(SERVO_CLOSE_ANGLE);
        //_servo_2.write(SERVO_CLOSE_ANGLE);
        break;
      default:
        break;
    }
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
