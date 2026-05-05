package api.monitoring.prototipagem.model.repository;


import java.time.LocalDateTime;
import java.util.List;

import api.monitoring.prototipagem.model.ReadingData;

public interface ReadingDataRepository {

    void save(ReadingData data);

    List<ReadingData> findByDateRange(LocalDateTime start, LocalDateTime end);

}
