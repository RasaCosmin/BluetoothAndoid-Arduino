#include <SoftwareSerial.h>
SoftwareSerial BTserial(0,1);

char command;
String str;
boolean ledon = false;
#define led 5

void setup() {
  Serial.begin(9600);
  pinMode(led, OUTPUT);
  BTserial.begin(38400);
}

void loop() {
  Serial.println(str);
  if(BTserial.available()>0){
    str = "";
  }
  while(BTserial.available()>0){
    command = ((byte)BTserial.read());
    if(command == ':'){
      break;
    }else{
      str+=command;
    }
    delay(1);
    Serial.println(str);
  }
  if(str=="TO"){
    ledOn();
    ledon = true;
    Serial.println(str);
  }

  if(str=="TF"){
    ledOff();
    ledon=false;
    Serial.println(str);
  }
  if((str.toInt()>=0)&&(str.toInt()<=255)){
    if(ledOn == true){
      analogWrite(led, str.toInt());
      Serial.println(str);
      delay(10);
    }
  }
}

void ledOn(){
   analogWrite(led, 255);
   delay(10);
}

void ledOff(){
   analogWrite(led, 0);
   delay(10);
}

