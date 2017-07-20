#include <Adafruit_GPS.h>
#include <MsTimer2.h>
#include <SoftwareSerial.h>


int RXD_PIN = 2; 
int TXD_PIN = 3;  

SoftwareSerial bt(RXD_PIN, TXD_PIN); // 객체 이름(bt) 생성과 동시 핀번호 설정
SoftwareSerial gpsSerial(11, 4);
Adafruit_GPS GPS(&gpsSerial);
uint32_t timer = millis();

//RC카
const int RM_A = 6;
const int RM_B = 7;
const int LM_A = 8;
const int LM_B = 9;

const int RM_E = 5;
const int LM_E = 10;

int offset = 8;
int pwm = 200;

#define MAX 255 //최대속도
#define MIN 0

#define MAXIMUM 20 //초음파 멕시멈
#define MINIMUM 15

//초음파
#define ECHO 12
#define TRIG 13

//ㅡㅡㅡㅡㅡㅡ뒤초음파 
#define ECHO2 A0
#define TRIG2 A1

int dur;  //시차
float dis; //거리
int Bdur;  //시차
float Bdis; //거리

//GPS



void Suitcase_Go();
void Suitcase_Back();
void Suitcase_Stop();
void Suitcase_Left();
void Suitcase_Right();
void SpeedUp();
void SpeedDn();

void fast();
void slow();
void standard();

void setup(){           
  Serial.begin(9600); //우노보드의 통신속도
  Serial.flush();
  Serial.println("Handsfree Suitcase");
  bt.begin(9600); // 블루투스의 통신속도
  GPS.begin(9600);
  GPS.sendCommand(PMTK_SET_NMEA_OUTPUT_RMCGGA);
  GPS.sendCommand(PMTK_SET_NMEA_UPDATE_1HZ);
  delay(5000);
  
  pinMode( RM_A, OUTPUT); pinMode( RM_B, OUTPUT);
  pinMode( LM_A, OUTPUT); pinMode( LM_B, OUTPUT);

  digitalWrite(RM_E, HIGH); // 오른쪽 모터의 Enable 핀 활성화
  digitalWrite(LM_E, HIGH); // 왼쪽 모터의 Enable 핀 활성화
  
  //앞초음파
  pinMode(TRIG, OUTPUT);
  pinMode(ECHO, INPUT);

//뒤 초음파 ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ
    pinMode(TRIG2, OUTPUT);
    pinMode(ECHO2, INPUT);

} // 아두이노 셋업문 


void loop(){

 //앞초음파 
  digitalWrite(TRIG,HIGH);
  delayMicroseconds(10);
  digitalWrite(TRIG,LOW);
  dur=pulseIn(ECHO,HIGH);
  dis=(float)dur*0.017;
  
   if((int)dis<15 ){ 
     Serial.println((int)dis);
    Suitcase_Stop();
    Serial.println("be careful");
    delay(500);
   }
  
  
// 뒤초음파 ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ
  digitalWrite(TRIG2,HIGH);
  delayMicroseconds(10);
  digitalWrite(TRIG2,LOW);
  Bdur=pulseIn(ECHO2,HIGH);
  Bdis=(float)Bdur*0.017;

  if((int)dis>=15){
        if((int)Bdis>MAXIMUM){
        Serial.print(Bdis);
        Serial.println("CM   Too far");
          slow();
       }
      if((int)Bdis<MINIMUM){
         Serial.print(Bdis);
        Serial.println("CM   Too close");
        fast();
      }
      if(15<=(int)Bdis  && (int)Bdis<=30){
         Serial.print(Bdis);
        Serial.println("CM   ok");
        standard();
       }
      delay(500);
  }
  

 if(bt.available()){ //블루투스if문
    char data=(char)bt.read();
    //delay(50);

    switch(data){ //블루투스 스위치문
        case 'g':
         Serial.println("GO");
         Suitcase_Go();
         break;
         case 'b':
         Serial.println("BACK");
         Suitcase_Back();
         break;
         case 'c':
         Serial.println("SpeedUp");
         SpeedUp();
         break;
         case 'a':
         Serial.println("SpeedDown");
         SpeedDn();
         break;
         case 's':
         Serial.println("STOP");
         Suitcase_Stop();
         break;
         case 'l':
         Serial.println("LEFT");
         Suitcase_Left();
         break;
         case 'r':
         Serial.println("RIGHT");
         Suitcase_Right();
         break;
         default:
         break;
       }
  }

  char c = GPS.read();
  if(GPS.newNMEAreceived()){
    if(!GPS.parse(GPS.lastNMEA()))
    return;
  }

  if(timer>millis()) timer = millis();
  if(millis() - timer>5000){
    timer = millis();
    if(GPS.fix){
      Serial.print(GPS.latitudeDegrees, 4);
      Serial.print(", ");
      Serial.println(GPS.longitudeDegrees, 4);
      Serial.println(" ");
      Serial.println(" ");
      float lat=GPS.latitudeDegrees;
      float lon= GPS.longitudeDegrees;
      String slat = String(lat);
      String slon = String(lon);
  
      Serial.print("# ");
      Serial.print(slat);
      Serial.print(", ");
      Serial.println(slon);
      Serial.println(" ");
  
      BTSerial.print(slat);
      BTSerial.print(",");
      BTSerial.print(slon);
      BTSerial.write('\n');
  
    }
    else{
      Serial.println("NO sat fix :-(");
      }
    }
}//루프

void Suitcase_Go()
{
 digitalWrite( RM_A, HIGH ); digitalWrite( RM_B, LOW );
 digitalWrite( LM_A, HIGH ); digitalWrite( LM_B, LOW );
 analogWrite( RM_E, pwm ); analogWrite( LM_E, pwm );
}

void Suitcase_Back()
{
 digitalWrite( RM_A, LOW ); digitalWrite( RM_B, HIGH );
 digitalWrite( LM_A, LOW ); digitalWrite( LM_B, HIGH );
 analogWrite( RM_E, pwm ); analogWrite( LM_E, pwm );
}

void Suitcase_Left()
{
 digitalWrite( RM_A, LOW ); digitalWrite( RM_B, HIGH );
 digitalWrite( LM_A, HIGH ); digitalWrite( LM_B, LOW );
 analogWrite( RM_E, pwm ); analogWrite( LM_E, pwm );

 delay(500);

 digitalWrite( RM_A, HIGH ); digitalWrite( RM_B, LOW );
 digitalWrite( LM_A, HIGH ); digitalWrite( LM_B, LOW );
 analogWrite( RM_E, pwm ); analogWrite( LM_E, pwm );

}

void Suitcase_Right()
{
 digitalWrite( RM_A, HIGH ); digitalWrite( RM_B, LOW );
 digitalWrite( LM_A, LOW ); digitalWrite( LM_B, HIGH );
 analogWrite( RM_E, pwm ); analogWrite( LM_E, pwm );

 delay(500);

 digitalWrite( RM_A, HIGH ); digitalWrite( RM_B, LOW );
 digitalWrite( LM_A, HIGH ); digitalWrite( LM_B, LOW );
 analogWrite( RM_E, pwm ); analogWrite( LM_E, pwm );
}

void Suitcase_Stop()
{
 digitalWrite( RM_A, HIGH ); digitalWrite( RM_B, HIGH );
 digitalWrite( LM_A, HIGH ); digitalWrite( LM_B, HIGH );
}

void SpeedUp()
{
 if( pwm <= (MAX - offset) ) pwm = pwm + offset;
 else pwm = MAX;
 analogWrite( RM_E, pwm ); analogWrite( LM_E, pwm );
}

void SpeedDn()
{
 if( pwm >= (MIN + offset) ) pwm = pwm - offset;
 else pwm = MIN;
 analogWrite( RM_E, pwm ); analogWrite( LM_E, pwm );
}

void fast()
{
  int pwm=200;
  for(pwm=180;pwm<255;pwm+=10)
  {  
      analogWrite( RM_E, pwm ); analogWrite( LM_E, pwm );
  }
}
 
void slow()
{
  int pwm=200;
    for(pwm=200;pwm>90;pwm-=10)
   {  
    analogWrite( RM_E, pwm ); analogWrite( LM_E, pwm );
    }
}

void standard(){
  analogWrite( RM_E, pwm ); analogWrite( LM_E, pwm );
}
