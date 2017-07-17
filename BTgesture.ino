#include <SoftwareSerial.h>
#include <Wire.h>
#include "paj7620.h"

#define GES_REACTION_TIME    500       // You can adjust the reaction time according to the actual circumstance.
#define GES_ENTRY_TIME      800       // When yo u want to recognize the Forward/Backward gestures, your gestures' reaction time must less than GES_ENTRY_TIME(0.8s). 
#define GES_QUIT_TIME     1000

int RXD_PIN = 10;     //블루투스 tx
int TXD_PIN = 11;   //블루투스 rx
SoftwareSerial bt(RXD_PIN, TXD_PIN); // 객체 이름(bt) 생성과 동시 핀번호 설정

void setup(){
  uint8_t error = 0;
  Serial.begin(19200);  //우노보드의 통신속도
  bt.begin(19200);     // 블루투스의 통신속도
  
  Serial.println("Handsfree Suitcase Gesture BlueTooth");
  error = paj7620Init();      // initialize Paj7620 registers
  if (error) 
  {
    Serial.print("INIT ERROR,CODE:");
    Serial.println(error);
  }
  else
  {
    Serial.println("INIT OK");
  }
  Serial.println("Please input your gestures:\n");
}

void loop(){
  uint8_t data = 0, data1 = 0, error;
  error = paj7620ReadReg(0x43, 1, &data);
  byte geSig;
  
  if (!error) 
  {
    switch (data)                   // When different gestures be detected, the variable 'data' will be set to different values by paj7620ReadReg(0x43, 1, &data).
    {
      case GES_RIGHT_FLAG:
        delay(GES_ENTRY_TIME);
        paj7620ReadReg(0x43, 1, &data);
        if(data == GES_FORWARD_FLAG) 
        {
          Serial.println("Stop");
          geSig='s';
          bt.write(geSig);
          delay(GES_QUIT_TIME);
        }
        else if(data == GES_BACKWARD_FLAG) 
        {
          Serial.println("Backward");
          delay(GES_QUIT_TIME);
        }
        else
        {
          Serial.println("Right");
          geSig='r';
          bt.write(geSig);
        }          
        break;
        
      case GES_LEFT_FLAG: 
        delay(GES_ENTRY_TIME);
        paj7620ReadReg(0x43, 1, &data);
        if(data == GES_FORWARD_FLAG) 
        {
          Serial.println("Stop");
          geSig='s';
          bt.write(geSig);
          delay(GES_QUIT_TIME);
        }
        else if(data == GES_BACKWARD_FLAG) 
        {
          Serial.println("Backward");
          delay(GES_QUIT_TIME);
        }
        else
        {
          Serial.println("Left");
          geSig='l';
          bt.write(geSig);
        }          
        break;
        
      case GES_UP_FLAG:
        delay(GES_ENTRY_TIME);
        paj7620ReadReg(0x43, 1, &data);
        if(data == GES_FORWARD_FLAG) 
        {
          Serial.println("Stop");
          geSig='s';
          bt.write(geSig);
          delay(GES_QUIT_TIME);
        }
        else if(data == GES_BACKWARD_FLAG) 
        {
          Serial.println("Backward");
          delay(GES_QUIT_TIME);
        }
        else
        {
          Serial.println("Go");
          geSig='g';
          bt.write(geSig);
        }          
        break;
        
      case GES_DOWN_FLAG:
        delay(GES_ENTRY_TIME);
        paj7620ReadReg(0x43, 1, &data);
        if(data == GES_FORWARD_FLAG) 
        {
          Serial.println("Stop");
          geSig='s';
          bt.write(geSig);
          delay(GES_QUIT_TIME);
        }
        else if(data == GES_BACKWARD_FLAG) 
       {
          Serial.println("Backward");
          delay(GES_QUIT_TIME);
        }
        else
        {
          Serial.println("Back");
          geSig='b';
          bt.write(geSig);
        }          
        break;
        
      case GES_FORWARD_FLAG:
        Serial.println("Stop");
        geSig='s';
        bt.write(geSig);
        delay(GES_QUIT_TIME);
        break;

      case GES_CLOCKWISE_FLAG:
        Serial.println("SpeedUp");
        geSig='c';
        bt.write(geSig);
        break;
        
      case GES_COUNT_CLOCKWISE_FLAG:
        Serial.println("SpeedDown");
        geSig='a';
        bt.write(geSig);
        break;
         
      default:
        break;
    }
  }
  delay(100);
}
