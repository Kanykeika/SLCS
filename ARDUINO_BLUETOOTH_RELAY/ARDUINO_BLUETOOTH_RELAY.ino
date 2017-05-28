
int relay[4]={4,5,6,7};  // arduino pin numbers for relay module
int tempBT[4]={1,2,3,4};
boolean tempBtn[4]={HIGH,LOW,HIGH,HIGH};// temporary state of switch buttons
boolean OUT[4]= {HIGH,HIGH,HIGH,HIGH}; // turn on or off leds
boolean IN[4]= {HIGH,HIGH,HIGH,HIGH}; // turn on or off leds
int BT[4]= {1,2,3,4};
boolean Btn[4]= {HIGH,HIGH,HIGH,HIGH}; // state of switch buttons
int button[4]={8,9,10,11}; // arduino pin numbers for switch buttons
int temp; // value came from android
char value_to_send0[4] = {'5','6','7','8'}; // android can not read integers, so declare chars
char value_to_send1[4] = {'1','2','3','4'};
#include <SoftwareSerial.h>
SoftwareSerial BTserial(2, 3); // RX | TX
// Connect the HC-05 TX to Arduino pin 2 RX. 
// Connect the HC-05 RX to Arduino pin 3 TX through a voltage divider.


void sendAndroidValues(int index)
 {
  if(BT[index] < 5){
    BTserial.write(value_to_send1[index]);
  }else{
    BTserial.write(value_to_send0[index]);
  }
  delay(200);        //added a delay to eliminate missed transmissions
}


void setup() 
{
  Serial.begin(9600);

  // HC-05 default serial speed for communication mode is 9600
  BTserial.begin(9600);  

  for(int i = 0; i < 4; i++) 
  {
    pinMode(relay[i], OUTPUT); // Configure Arduino's 4,5,6,7 pin to output mode to write data to relay module
    pinMode(button[i],INPUT); // Configure Arduino's 8,9,10,11 pin to input mode to read data from switch btns
    digitalWrite(relay[i], OUT[i]); // by default turn off all the leds in relay module
  }       
}

void loop()
{
    // read data from Bluetooth
    if (BTserial.available())
    {  
        temp = BTserial.read();
        Serial.write(temp);
        Serial.println("");
        if(temp == 1) tempBT[0] = 1;
        if(temp == 2) tempBT[1] = 2;
        if(temp == 3) tempBT[2] = 3;
        if(temp == 4) tempBT[3] = 4;
        if(temp == 5) tempBT[0] = 5;
        if(temp == 6) tempBT[1] = 6;
        if(temp == 7) tempBT[2] = 7;
        if(temp == 8) tempBT[3] = 8;
        // sending all Bluetooth values to android
        if(temp == 9) {
          Serial.println("get signal that application started");
          for(int i=0; i<4; i++){
            sendAndroidValues(i);
            delay(100);
          }
        }
     }

    // read data from buttons
    for(int i = 0; i < 4; i++)
    {
      tempBtn[i] = digitalRead(button[i]); 
    }

    // read data from relay
    for(int i = 0; i < 4; i++)
    {
      IN[i] = bitRead(PORTD,relay[i]); 
    }

    // check temp and bt values
    for(int i = 0; i < 4; i++){
     if(tempBT[i] != BT[i]) { // if temporary value read from bluetooth is not equal to previous 
        BT[i] = tempBT[i]; // update previous value
        sendAndroidValues(i);
        delay(100);
        if(BT[i] >= 5) {
          OUT[i] = LOW; // turn on led
        } 
        else OUT[i] = HIGH; // turn off led
        }
      }

     //check temp and button values if changed change also bluetooth values
     for(int i=0;i<4;i++){
      if(tempBtn[i] != Btn[i]) { // if button position changed
        Btn[i]=tempBtn[i]; // update state of button
        if(IN[i] == 1){ // if relay led is off
          OUT[i] = LOW; // turn on
          tempBT[i]=BT[i]=i+5;
        }else{ // if relay led is on
          OUT[i] = HIGH; // turn off
          tempBT[i]=BT[i]=i+1;
        }
        sendAndroidValues(i);  
      }
    }
    
     // relay control
     for(int i=0;i<4;i++) 
     {
       digitalWrite(relay[i],OUT[i]);
     } 
}
