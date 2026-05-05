#include <WiFi.h>
#include <ArduinoJson.h>
#include <PubSubClient.h>

const char* ssid     = "wifi";
const char* password = "teste123";
const char* id = "705";
double flow = 100;
int has_water, is_valve_open;

const char* ntp_server = "pool.ntp.org";
const long gmt_offset_sec = -3 * 3600;
const int day_light_offset_sec = 0;
const char* mqtt_server = "10.88.23.50";

WiFiClient espClient;
PubSubClient client(espClient);

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

void readData(){
  has_water = is_valve_open = 1;
  flow += 1;
}

void sendData() {
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

void setup() {
  delay(2000); 
  Serial.begin(115200);
  delay(2000);
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

  readData();
  sendData();

  delay(500);
}