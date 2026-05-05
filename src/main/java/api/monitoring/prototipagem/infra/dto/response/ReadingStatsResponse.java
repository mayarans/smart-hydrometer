package api.monitoring.prototipagem.infra.dto.response;

import api.monitoring.prototipagem.model.ReadingStatistics;

public record ReadingStatsResponse(
        Double averageFlow,
        Double stdDeviationFlow,
        Double minFlow,
        Double maxFlow,
        Long valveOpenTimeInSeconds,
        Long waterDetectedTimeInSeconds,
        Long airDetectedTimeInSeconds,
        Integer stateChanges
) {
    public static ReadingStatsResponse from(ReadingStatistics stats) {
        return new ReadingStatsResponse(
                stats.avgFlow(),
                stats.stdDeviationFlow(),
                stats.minFlow(),
                stats.maxFlow(),
                stats.valveOpenTimeInSeconds(),
                stats.waterDetectedTimeInSeconds(),
                stats.airDetectedTimeInSeconds(),
                stats.stateChanges()
        );
    }
}
