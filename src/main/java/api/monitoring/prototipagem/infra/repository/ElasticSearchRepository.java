package api.monitoring.prototipagem.infra.repository;


import api.monitoring.prototipagem.model.ReadingData;
import api.monitoring.prototipagem.model.repository.ReadingDataRepository;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.stereotype.Repository;

import static api.monitoring.prototipagem.infra.dto.ReadingDocument.aReadingDocument;

@Repository
public class ElasticSearchRepository implements ReadingDataRepository {

    private final ElasticsearchOperations elasticsearchOperations;

    public ElasticSearchRepository(ElasticsearchOperations elasticsearchOperations) {
        this.elasticsearchOperations = elasticsearchOperations;
    }

    @Override
    public void save(ReadingData data) {
        var document = aReadingDocument()
                .withId(data.id())
                .withMicrocontrollerId(data.microcontroller().id())
                .withFlowValue(data.flow().value())
                .withWaterDetected(data.levelSensor().waterDetected())
                .withTimestamp(data.timestamp())
                .withValveState(data.valve().state().name())
                .build();
        elasticsearchOperations.save(document);
    }
}
