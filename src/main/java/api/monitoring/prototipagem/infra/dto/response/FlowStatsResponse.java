package api.monitoring.prototipagem.infra.dto.response;

import api.monitoring.prototipagem.model.Flow;

public record FlowStatsResponse(
        Double averageFlow,
        Double stdDeviationFlow,
        Double minFlow,
        Double maxFlow
) {
    public static FlowStatsResponse from(Flow.Stats stats) {
        return new FlowStatsResponse(
                stats.avg(),
                stats.std(),
                stats.min(),
                stats.max()
        );
    }
}
