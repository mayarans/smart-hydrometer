package api.monitoring.prototipagem.infra.controller;

import api.monitoring.prototipagem.application.GetFlowStatsByDateRangeUseCase;
import api.monitoring.prototipagem.application.GetReadingsByDateRangeUseCase;
import api.monitoring.prototipagem.infra.dto.response.FlowStatsResponse;
import api.monitoring.prototipagem.infra.dto.response.ReadingDataResponse;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/readings")
public class ReadingController {

    private final GetReadingsByDateRangeUseCase getReadingsByDateRangeUseCase;
    private final GetFlowStatsByDateRangeUseCase getFlowStatsByDateRangeUseCase;

    public ReadingController(GetReadingsByDateRangeUseCase getReadingsByDateRangeUseCase,
                             GetFlowStatsByDateRangeUseCase getFlowStatsByDateRangeUseCase) {
        this.getReadingsByDateRangeUseCase = getReadingsByDateRangeUseCase;
        this.getFlowStatsByDateRangeUseCase = getFlowStatsByDateRangeUseCase;
    }

    @GetMapping
    public ResponseEntity<List<ReadingDataResponse>> getReadings(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime windowStart,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime windowEnd) {
        var readings = getReadingsByDateRangeUseCase.execute(windowStart, windowEnd);
        return ResponseEntity.ok(readings);
    }

    @GetMapping("/stats")
    public ResponseEntity<FlowStatsResponse> getStats(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime windowStart,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime windowEnd) {
        var stats = getFlowStatsByDateRangeUseCase.execute(windowStart, windowEnd);
        if (stats == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(stats);
    }
}
