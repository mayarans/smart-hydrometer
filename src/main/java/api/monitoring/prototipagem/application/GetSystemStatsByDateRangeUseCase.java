package api.monitoring.prototipagem.application;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import api.monitoring.prototipagem.infra.dto.response.ReadingStatsResponse;
import api.monitoring.prototipagem.model.ReadingData;
import api.monitoring.prototipagem.model.ReadingStatistics;
import api.monitoring.prototipagem.model.SolenoidValve;
import api.monitoring.prototipagem.model.repository.ReadingDataRepository;

@Service
public class GetSystemStatsByDateRangeUseCase {

    private final ReadingDataRepository repository;

    public GetSystemStatsByDateRangeUseCase(ReadingDataRepository repository) {
        this.repository = repository;
    }

    public ReadingStatsResponse execute(LocalDateTime start, LocalDateTime end) {
        List<ReadingData> readings = repository.findByDateRange(start, end);
        
        if (readings.isEmpty()) {
            return null;
        }

        // Flow stats calculation
        List<Double> flowValues = readings.stream().map(r -> r.flow().value()).collect(Collectors.toList());
        double sum = flowValues.stream().mapToDouble(Double::doubleValue).sum();
        double avg = sum / flowValues.size();
        double min = flowValues.stream().mapToDouble(Double::doubleValue).min().orElse(0.0);
        double max = flowValues.stream().mapToDouble(Double::doubleValue).max().orElse(0.0);
        double variance = flowValues.stream().mapToDouble(v -> Math.pow(v - avg, 2)).sum() / flowValues.size();
        double std = Math.sqrt(variance);

        // Time-based and state change calculations
        long valveOpenTime = 0;
        long waterDetectedTime = 0;
        int stateChanges = 0;

        if (readings.size() > 1) {
            for (int i = 0; i < readings.size() - 1; i++) {
                ReadingData current = readings.get(i);
                ReadingData next = readings.get(i + 1);

                long durationInSeconds = next.timestamp().toEpochSecond(ZoneOffset.UTC) - current.timestamp().toEpochSecond(ZoneOffset.UTC);

                if (current.valve().state() == SolenoidValve.States.OPEN) {
                    valveOpenTime += durationInSeconds;
                }
                if (current.levelSensor().waterDetected()) {
                    waterDetectedTime += durationInSeconds;
                }
                if (current.valve().state() != next.valve().state()) {
                    stateChanges++;
                }
            }
            
            long totalTime = readings.get(readings.size() - 1).timestamp().toEpochSecond(ZoneOffset.UTC) - readings.get(0).timestamp().toEpochSecond(ZoneOffset.UTC);
            long airDetectedTime = totalTime > waterDetectedTime ? totalTime - waterDetectedTime : 0;

            ReadingStatistics fullStats = new ReadingStatistics(avg, std, min, max, valveOpenTime, waterDetectedTime, airDetectedTime, stateChanges);
            return ReadingStatsResponse.from(fullStats);

        } else {
            // Handle case with only one reading
            ReadingStatistics singleReadingStats = new ReadingStatistics(avg, 0.0, min, max, 0L, 0L, 0L, 0);
            return ReadingStatsResponse.from(singleReadingStats);
        }
    }
}
