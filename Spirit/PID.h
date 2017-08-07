// PID.h
// Mai Tien
#ifndef PID_h
#define PID_h

#include <AccelStepper.h>

class PID {

  private:
    unsigned long m_lastTime;
    double m_input, m_output, m_setPoint;
    double m_outputSum, m_lastInput;
    double m_kp, m_ki, m_kd;
    int m_sampleTime;
    double m_outMin, m_outMax;
    bool m_isAuto;
    int m_direction;
    bool m_pOnE, m_pOnM;
    double m_pOnEKp, m_pOnMKp;

  public:
    PID();
    void Compute();
    void SetTunings(double Kp, double Ki, double Kd, double pOn);
    void SetSampleTime(int NewSampleTime = 1000);
    void SetOutputLimits(double Min, double Max);
    void SetMode(int Mode);
    void Initialize();
    void SetDirection(int Direction);

    enum {
        MANUAL = 0,
        AUTOMATIC = 1,
    } MODE;

    enum {
        DIRECT = 0,
        REVERSE = 1,
    } DIRECTION;

    enum {
        P_ON_M = 0,
        P_ON_E = 1,
    } P_ON;
};

#endif //PID_h
