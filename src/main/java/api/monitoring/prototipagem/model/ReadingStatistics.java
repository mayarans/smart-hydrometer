package api.monitoring.prototipagem.model;

public record ReadingStatistics(
        Double avgFlow,
        Double stdDeviationFlow,
        Double minFlow,
        Double maxFlow,
        Long valveOpenTimeInSeconds,
        Long waterDetectedTimeInSeconds,
        Long airDetectedTimeInSeconds,
        Integer stateChanges
) {
}
