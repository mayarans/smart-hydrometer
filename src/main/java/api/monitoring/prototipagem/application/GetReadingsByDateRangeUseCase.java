package api.monitoring.prototipagem.application;

import api.monitoring.prototipagem.infra.dto.response.ReadingDataResponse;
import api.monitoring.prototipagem.model.repository.ReadingDataRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class GetReadingsByDateRangeUseCase {

    private final ReadingDataRepository repository;

    public GetReadingsByDateRangeUseCase(ReadingDataRepository repository) {
        this.repository = repository;
    }

    public List<ReadingDataResponse> execute(LocalDateTime start, LocalDateTime end) {
        var readings = repository.findByDateRange(start, end);
        return ReadingDataResponse.from(readings);
    }
}
