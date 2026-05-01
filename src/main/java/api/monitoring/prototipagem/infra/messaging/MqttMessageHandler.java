package api.monitoring.prototipagem.infra.messaging;

import api.monitoring.prototipagem.application.SaveSensorReadingUseCase;
import api.monitoring.prototipagem.infra.dto.MqttMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.stereotype.Component;

@Component
public class MqttMessageHandler {

    private static final ObjectMapper  MAPPER = new ObjectMapper();
    private static final Logger LOGGER = LoggerFactory.getLogger(MqttMessageHandler.class);
    private final SaveSensorReadingUseCase saveDataUseCase;

    public MqttMessageHandler(SaveSensorReadingUseCase saveDataUseCase) {
        this.saveDataUseCase = saveDataUseCase;
    }

    @ServiceActivator(inputChannel = "messageChannel")
    public void handle(String message){
        try {
            var parsedMessage = MAPPER.readValue(message.trim(), MqttMessage.class);
            LOGGER.info("Received message: {}", parsedMessage);
            saveDataUseCase.execute(parsedMessage);

        } catch (JsonProcessingException e){
            LOGGER.error("Error trying to parse json: {}", e.getMessage());
        }
    }




}
