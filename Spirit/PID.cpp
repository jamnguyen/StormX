#include "PID.h"

PID::PID()
{
    m_sampleTime = 1000;
    m_direction = DIRECT;
    m_pOnE = true;
    m_pOnM = false;
}

void PID::Compute()
{
    if(!m_isAuto) return;
    unsigned long now = millis();
    int timeChange = (now - m_lastTime);
    if(timeChange >= m_sampleTime)
    {
        /*Compute all the working error variables*/
        double error = m_setPoint - m_input;
        double dInput = (m_input - m_lastInput);
        m_outputSum+= (m_ki * error);

        /*Add Proportional on Measurement, if P_ON_M is specified*/
        if(m_pOnM) m_outputSum-= m_pOnMKp * dInput;

        if(m_outputSum > m_outMax) m_outputSum= m_outMax;
        else if(m_outputSum < m_outMin) m_outputSum= m_outMin;

        /*Add Proportional on Error, if P_ON_E is specified*/
        if(m_pOnE) m_output = m_pOnEKp * error;
        else m_output = 0;

        /*Compute Rest of PID m_output*/
        m_output += m_outputSum - m_kd * dInput;

        if(m_output > m_outMax) m_output = m_outMax;
        else if(m_output < m_outMin) m_output = m_outMin;

        /*Remember some variables for next time*/
        m_lastInput = m_input;
        m_lastTime = now;
    }
}

void PID::SetTunings(double Kp, double Ki, double Kd, double pOn)
{
    if (Kp < 0 || Ki < 0|| Kd < 0 || pOn < 0 || pOn > 1) return;

    m_pOnE = pOn > 0; //some p on error is desired;
    m_pOnM = pOn < 1; //some p on measurement is desired;

    double SampleTimeInSec = ((double)m_sampleTime)/1000;
    m_kp = Kp;
    m_ki = Ki * SampleTimeInSec;
    m_kd = Kd / SampleTimeInSec;

    if(m_direction == REVERSE)
    {
        m_kp = (0 - m_kp);
        m_ki = (0 - m_ki);
        m_kd = (0 - m_kd);
    }

    m_pOnEKp = pOn * m_kp;
    m_pOnMKp = (1 - pOn) * m_kp;
}

void PID::SetSampleTime(int NewSampleTime)
{
    if (NewSampleTime > 0)
    {
        double ratio  = (double)NewSampleTime
                        / (double)m_sampleTime;
        m_ki *= ratio;
        m_kd /= ratio;
        m_sampleTime = (unsigned long)NewSampleTime;
    }
}

void PID::SetOutputLimits(double Min, double Max)
{
    if(Min > Max) return;
    m_outMin = Min;
    m_outMax = Max;

    if(m_output > m_outMax) m_output = m_outMax;
    else if(m_output < m_outMin) m_output = m_outMin;

    if(m_outputSum > m_outMax) m_outputSum= m_outMax;
    else if(m_outputSum < m_outMin) m_outputSum= m_outMin;
}

void PID::SetMode(int Mode)
{
    bool newAuto = (Mode == AUTOMATIC);
    if(newAuto == !m_isAuto)
    {
        Initialize();
    }
    m_isAuto = newAuto;
}

void PID::Initialize()
{
    m_lastInput = m_input;
    m_outputSum = m_output;
    if(m_outputSum > m_outMax) m_outputSum= m_outMax;
    else if(m_outputSum < m_outMin) m_outputSum= m_outMin;
}

void PID::SetDirection(int Direction)
{
    m_direction = Direction;
}
