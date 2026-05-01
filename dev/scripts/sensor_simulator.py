import time
import json
import random
import sys
import argparse
from paho.mqtt import client as mqtt_client

BROKER = 'localhost'
PORT = 1883
TOPIC = "topic.flow"

def connect_mqtt():
    def on_connect(client, userdata, flags, rc):
        if rc == 0:
            print("Conectado ao MQTT Broker!")
        else:
            print(f"Falha na conexão, código: {rc}")

    client = mqtt_client.Client()
    client.on_connect = on_connect
    client.connect(BROKER, PORT)
    return client

def run_simulator(interval, total):
    client = connect_mqtt()
    client.loop_start()

    print(f"Iniciando envio de {total} mensagens com intervalo de {interval}s...")

    for i in range(total):
        payload = {
            "deviceId": random.choice([101, 102, 103]),
            "flow": round(random.uniform(100.0, 2000.0), 2),
            "hasWater": random.choice([True, False]),
            "isValveOpen": True,
            "timestampInSeconds": int(time.time())
        }

        msg = json.dumps(payload)
        result = client.publish(TOPIC, msg)

        status = result[0]
        if status == 0:
            print(f"[Msg {i+1}/{total}] Enviada: {msg}")
        else:
            print(f"Falha ao enviar mensagem {i+1}")

        time.sleep(interval)

    client.loop_stop()
    print("\nSimulação finalizada.")

if __name__ == '__main__':
    parser = argparse.ArgumentParser(description='Simulador de Sensores MQTT')
    parser.add_argument('--intervalo', type=float, default=1.0, help='Intervalo entre mensagens (segundos)')
    parser.add_argument('--total', type=int, default=10, help='Total de mensagens a enviar')

    args = parser.parse_args()

    try:
        run_simulator(args.intervalo, args.total)
    except KeyboardInterrupt:
        print("\nSimulador interrompido pelo usuário.")
        sys.exit(0)