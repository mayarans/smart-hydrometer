package api.monitoring.prototipagem.infra.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import api.monitoring.prototipagem.application.GetReadingsByDateRangeUseCase;
import api.monitoring.prototipagem.application.GetSystemStatsByDateRangeUseCase;
import api.monitoring.prototipagem.infra.dto.response.ReadingDataResponse;
import api.monitoring.prototipagem.infra.dto.response.ReadingStatsResponse;

@RestController
@RequestMapping("/readings")
public class ReadingController {

    private final GetReadingsByDateRangeUseCase getReadingsByDateRangeUseCase;
    private final GetSystemStatsByDateRangeUseCase getSystemStatsByDateRangeUseCase;

    public ReadingController(GetReadingsByDateRangeUseCase getReadingsByDateRangeUseCase,
                             GetSystemStatsByDateRangeUseCase getSystemStatsByDateRangeUseCase) {
        this.getReadingsByDateRangeUseCase = getReadingsByDateRangeUseCase;
        this.getSystemStatsByDateRangeUseCase = getSystemStatsByDateRangeUseCase;
    }

    @GetMapping
    public ResponseEntity<List<ReadingDataResponse>> getReadings(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime windowStart,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime windowEnd) {
        var readings = getReadingsByDateRangeUseCase.execute(windowStart, windowEnd);
        return ResponseEntity.ok(readings);
    }

    @GetMapping("/stats")
    public ResponseEntity<ReadingStatsResponse> getStats(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime windowStart,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime windowEnd) {
        var stats = getSystemStatsByDateRangeUseCase.execute(windowStart, windowEnd);
        if (stats == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(stats);
    }
}
