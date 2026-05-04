package api.monitoring.prototipagem.infra.dto.response;

import api.monitoring.prototipagem.model.ReadingData;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public record ReadingDataResponse(
        String id,
        Long microcontrollerId,
        Double flow,
        Boolean waterDetected,
        String valveState,
        LocalDateTime timestamp
) {
    public static ReadingDataResponse from(ReadingData data) {
        return new ReadingDataResponse(
                data.id(),
                data.microcontroller().id(),
                data.flow().value(),
                data.levelSensor().waterDetected(),
                data.valve().state().name(),
                data.timestamp()
        );
    }

    public static List<ReadingDataResponse> from(List<ReadingData> data) {
        return data.stream().map(ReadingDataResponse::from).collect(Collectors.toList());
    }
}
