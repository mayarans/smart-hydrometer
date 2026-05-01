package api.monitoring.prototipagem.infra.dto;

import java.time.Instant;
import java.time.ZoneId;

public record MqttMessage(
    Long deviceId,
    Double flow,
    boolean hasWater,
    boolean isValveOpen,
    long timestampInSeconds
) {

    @Override
    public String toString() {
        var hasWaterFormatted = hasWater ? "yes" : "no";
        var isValveOpenFormatted = isValveOpen ? "yes" : "no";
        var timestampFormatted = Instant.ofEpochMilli(timestampInSeconds * 1000)
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
        return """
                Device: %d,
                Flow value: %.2f
                Is water present?: %s
                Is valve open?: %s
                Received in: %s
                """.formatted(deviceId, flow, hasWaterFormatted,isValveOpenFormatted, timestampFormatted);
    }
}
