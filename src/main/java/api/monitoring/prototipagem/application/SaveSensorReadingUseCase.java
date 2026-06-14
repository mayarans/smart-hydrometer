package api.monitoring.prototipagem.application;

import api.monitoring.prototipagem.infra.dto.MqttMessage;
import api.monitoring.prototipagem.model.repository.ReadingDataRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import static api.monitoring.prototipagem.model.ReadingData.aReadingData;

@Service
public class SaveSensorReadingUseCase {

    private final ReadingDataRepository repository;
    private final RestTemplate restTemplate;

    public SaveSensorReadingUseCase(ReadingDataRepository repository) {
        this.repository = repository;
        this.restTemplate = new RestTemplate(); 
    }

    public void execute(MqttMessage message) {
    try {
        String fastapiUrl = System.getenv("FASTAPI_URL");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> dadosParaIA = new HashMap<>();
        dadosParaIA.put("vazao", message.flow());
        dadosParaIA.put("valvula_aberta", message.isValveOpen() ? 1 : 0);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(dadosParaIA, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(fastapiUrl, request, String.class);
        System.out.println(response.getBody());

    } catch (Exception e) {
        System.out.println("FastAPI indisponível: " + e.getMessage());
    }

    try {
        var data = aReadingData()
                .withMicrocontrollerId(message.deviceId())
                .withFlowReading(message.flow())
                .withValveState(message.isValveOpen())
                .withWaterDetected(message.hasWater())
                .withTimestamp(message.timestampInSeconds())
                .build();
        repository.save(data);
    } catch (Exception e) {
        System.out.println("Erro ao salvar no banco: " + e.getMessage());
    }
  }
}