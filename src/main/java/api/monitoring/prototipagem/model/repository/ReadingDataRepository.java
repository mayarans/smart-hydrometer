package api.monitoring.prototipagem.model.repository;


import api.monitoring.prototipagem.model.ReadingData;
import api.monitoring.prototipagem.model.Flow;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ReadingDataRepository {

    void save(ReadingData data);

    List<ReadingData> findByDateRange(LocalDateTime start, LocalDateTime end);

    Optional<Flow.Stats> getStatsByDateRange(LocalDateTime start, LocalDateTime end);

}
