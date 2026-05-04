package api.monitoring.prototipagem.application;

import api.monitoring.prototipagem.infra.dto.response.FlowStatsResponse;
import api.monitoring.prototipagem.model.repository.ReadingDataRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class GetFlowStatsByDateRangeUseCase {

    private final ReadingDataRepository repository;

    public GetFlowStatsByDateRangeUseCase(ReadingDataRepository repository) {
        this.repository = repository;
    }

    public FlowStatsResponse execute(LocalDateTime start, LocalDateTime end) {
        return repository.getStatsByDateRange(start, end)
                .map(FlowStatsResponse::from)
                .orElse(null); // Or throw an exception if no data is found
    }
}
