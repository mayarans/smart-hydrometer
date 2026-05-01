package api.monitoring.prototipagem.infra.context;

import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.core.MessageProducer;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.event.MqttConnectionFailedEvent;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.messaging.MessageChannel;

@Configuration
public class MqttContext {

    @Value("${spring.mqtt.url:tcp://localhost:1883}")
    private String mqttUrl;


    @Bean
    public MqttPahoClientFactory mqttPahoClientFactory(){
        var factory = new DefaultMqttPahoClientFactory();
        var options = new MqttConnectOptions();
        options.setServerURIs(new String[]{mqttUrl});
        options.setCleanSession(true);
        options.setMqttVersion(MqttConnectOptions.MQTT_VERSION_3_1_1);
        options.setConnectionTimeout(10);
        options.setKeepAliveInterval(60);
        factory.setConnectionOptions(options);
        return factory;
    }

    @Bean
    public MessageChannel messageChannel(){
        return new DirectChannel();
    }

    @Bean
    public MessageProducer messageProducer(){
        var adapter =  new MqttPahoMessageDrivenChannelAdapter("backend-client", mqttPahoClientFactory(),"topic.flow");
        adapter.setQos(1);
        adapter.setOutputChannel(messageChannel());

        return adapter;
    }

    @Bean
    public ApplicationListener<MqttConnectionFailedEvent> connectionFailedEventApplicationListener() {
         return event -> System.out.println("Connection error: " + event.getCause().getMessage());
    }


}
