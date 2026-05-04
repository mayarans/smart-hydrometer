package api.monitoring.prototipagem.infra.repository;


import api.monitoring.prototipagem.infra.dto.ReadingDocument;
import api.monitoring.prototipagem.model.Flow;
import api.monitoring.prototipagem.model.ReadingData;
import api.monitoring.prototipagem.model.repository.ReadingDataRepository;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    @Override
    public List<ReadingData> findByDateRange(LocalDateTime start, LocalDateTime end) {
        Criteria criteria = new Criteria("timestamp").greaterThanEqual(start).lessThanEqual(end);
        Query query = new CriteriaQuery(criteria);
        SearchHits<ReadingDocument> searchHits = elasticsearchOperations.search(query, ReadingDocument.class);
        return searchHits.getSearchHits().stream()
                .map(SearchHit::getContent)
                .map(ReadingDocument::toReadingData)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Flow.Stats> getStatsByDateRange(LocalDateTime start, LocalDateTime end) {
        // initial simplified approach
        // TODO: optimize by using aggregations
        List<Double> flowValues = findByDateRange(start, end).stream()
                .map(reading -> reading.flow().value())
                .collect(Collectors.toList());

        if (flowValues.isEmpty()) {
            return Optional.empty();
        }

        double sum = flowValues.stream().mapToDouble(Double::doubleValue).sum();
        double avg = sum / flowValues.size();
        double min = flowValues.stream().mapToDouble(Double::doubleValue).min().getAsDouble();
        double max = flowValues.stream().mapToDouble(Double::doubleValue).max().getAsDouble();

        double variance = flowValues.stream()
                .mapToDouble(v -> Math.pow(v - avg, 2))
                .sum() / flowValues.size();
        double std = Math.sqrt(variance);

        return Optional.of(new Flow.Stats(avg, std, min, max));
    }
}
