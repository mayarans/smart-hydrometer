package api.monitoring.prototipagem.application;

import api.monitoring.prototipagem.infra.dto.MqttMessage;
import api.monitoring.prototipagem.model.repository.ReadingDataRepository;
import org.springframework.stereotype.Service;

import static api.monitoring.prototipagem.model.ReadingData.aReadingData;

@Service
public class SaveSensorReadingUseCase {

    private final ReadingDataRepository repository;

    public SaveSensorReadingUseCase(ReadingDataRepository repository) {
        this.repository = repository;
    }

    public void execute(MqttMessage message){
        var data = aReadingData()
                .withMicrocontrollerId(message.deviceId())
                .withFlowReading(message.flow())
                .withValveState(message.isValveOpen())
                .withWaterDetected(message.hasWater())
                .withTimestamp(message.timestampInSeconds())
                .build();
        repository.save(data);
    }
}
