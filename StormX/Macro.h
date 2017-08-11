#ifndef Macro_h
#define Macro_h

#define portOfPin(P)        (((P)>=0&&(P)<8)?&PORTD:(((P)>7&&(P)<14)?&PORTB:&PORTC))
#define ddrOfPin(P)         (((P)>=0&&(P)<8)?&DDRD:(((P)>7&&(P)<14)?&DDRB:&DDRC))
#define pinOfPin(P)         (((P)>=0&&(P)<8)?&PIND:(((P)>7&&(P)<14)?&PINB:&PINC))
#define pinIndex(P)         ((uint8_t)(P>13?P-14:P&7))
#define pinMask(P)          ((uint8_t)(1<<pinIndex(P)))

#define pinAsInput(P)       (pinMode(P, INPUT))//(*(ddrOfPin(P))&=~pinMask(P))
#define pinAsInputPullUp(P) (pinMode(P, INPUT_PULLUP))//(*(ddrOfPin(P))&=~pinMask(P))
#define pinAsOutput(P)      (pinMode(P, OUTPUT))//(*(ddrOfPin(P))|=pinMask(P))
#define digitalLow(P)       (digitalWrite(P, LOW))//(*(portOfPin(P))&=~pinMask(P))
#define digitalHigh(P)      (digitalWrite(P, HIGH))//(*(portOfPin(P))|=pinMask(P))
#define isHigh(P)           (digitalRead(P) == HIGH)//((*(pinOfPin(P))& pinMask(P))>0)
#define isLow(P)            (digitalRead(P) == LOW)//((*(pinOfPin(P))& pinMask(P))==0)
#define digitalState(P)     (digitalRead(P))//(pinMode(P, OUTPUT))//((uint8_t)isHigh(P))

#endif //Macro_h
