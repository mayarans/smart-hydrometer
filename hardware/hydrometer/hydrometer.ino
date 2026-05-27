#include <WiFi.h>
#include <ArduinoJson.h>
#include <PubSubClient.h>

const char* ssid     = "wifi";
const char* password = "teste123";
const char* id = "705";
const char* ntp_server = "pool.ntp.org";
const long gmt_offset_sec = -3 * 3600;
const int day_light_offset_sec = 0;
const char* mqtt_server = "10.88.23.50";
const uint8_t pin_flow_sensor = 4;
const uint8_t pin_level_sensor = 5;
const uint8_t pin_valve_actuator = 6;

int pulses_cont = 0;
double flow;
uint8_t has_water, is_valve_open;
hw_timer_t *send_data_timer = NULL;

WiFiClient espClient;
PubSubClient client(espClient);


void IRAM_ATTR  increasePulseCont(){
  pulses_cont++;
}

void IRAM_ATTR changeValveState() {
  has_water = is_valve_open = digitalRead(pin_level_sensor);
  digitalWrite(pin_valve_actuator, is_valve_open);
}

void IRAM_ATTR onSendDataTimeout() {
  readData();
  
  if (!client.connected()) {
    return;
  }

  time_t now = time(NULL);

  StaticJsonDocument<200> doc;
  doc["deviceId"] = id;
  doc["flow"] = flow;
  doc["hasWater"] = has_water;
  doc["isValveOpen"] = is_valve_open;
  doc["timestampInSeconds"] = now;

  char buffer[256];
  serializeJson(doc, buffer);

  boolean ok = client.publish("topic.flow", buffer);
}

void setupMQTT() {
  client.setServer(mqtt_server, 1883);

  while (!client.connected()) {
    if (client.connect("ESP32Client")) {
      Serial.println(" conectado]");
    } else {
      Serial.print(" erro=");
      Serial.print(client.state());
      Serial.println("]");
      delay(2000);
    }
  }
}

void setupTime() {
  configTime(gmt_offset_sec, day_light_offset_sec, ntp_server);

  struct tm time_info;
  uint retries = 0;
  while (!getLocalTime(&time_info) && retries < 10) {
    delay(500);
    retries++;
  }
}

void connectWifi() {
  WiFi.begin(ssid, password);

  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
  }
}

void setupPins(){
  pinMode(pin_flow_sensor, INPUT_PULLDOWN);
  attachInterrupt(digitalPinToInterrupt(pin_flow_sensor), increasePulseCont, RISING);

  pinMode(pin_level_sensor, INPUT_PULLDOWN);
  attachInterrupt(digitalPinToInterrupt(pin_level_sensor), changeValveState, CHANGE);

  pinMode(pin_valve_actuator, OUTPUT);
  digitalWrite(pin_valve_actuator, LOW);
}

void setupTimer(){
  send_data_timer = timerBegin(1000000); 
  timerAttachInterrupt(send_data_timer, &onSendDataTimeout);
  timerAlarm(send_data_timer, 1000000, true, 0); 
}

void readData(){
  double pulse_frequency = 1/pulses_cont;
  flow = pulse_frequency/7.5;
  pulses_cont = 0;
}

void setup() {
  delay(2000); 
  Serial.begin(115200);
  delay(2000);
  setupPins();
  setupTimer();
  connectWifi();
  setupTime();
  setupMQTT();
}

void loop() {
  if (WiFi.status() != WL_CONNECTED) {
    connectWifi();
  }

  if (!client.connected()) {
    setupMQTT();
  }

  client.loop();
  delay(500);
}