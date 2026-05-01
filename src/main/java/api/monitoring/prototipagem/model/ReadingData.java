package api.monitoring.prototipagem.model;

import api.monitoring.prototipagem.model.exceptions.InvalidTimeException;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

public record ReadingData(
        String id,
        Microcontroller microcontroller,
        Flow flow,
        LevelSensor levelSensor,
        SolenoidValve valve,
        LocalDateTime timestamp
) {

    public static Builder aReadingData(){
        return new Builder();
    }

    public static class Builder{
        private Long id;
        private Double flowReading;
        private Boolean valveState;
        private Boolean waterDetected;
        private long timestamp;

        public Builder withMicrocontrollerId(Long id){
            this.id = id;
            return this;
        }

        public Builder withFlowReading(Double value){
            this.flowReading = value;
            return this;
        }

        public Builder withValveState(Boolean state){
            this.valveState = state;
            return this;
        }

        public Builder withWaterDetected(Boolean waterDetected){
            this.waterDetected = waterDetected;
            return this;
        }

        public Builder withTimestamp(long timestamp){
            this.timestamp = timestamp;
            return this;
        }

        public ReadingData build(){
            return new ReadingData(
                    UUID.randomUUID().toString(),
                    new Microcontroller(this.id),
                    Flow.of(this.flowReading),
                    new LevelSensor(this.waterDetected),
                    new SolenoidValve(SolenoidValve.States.from(this.valveState)),
                    getLocalDateTime(this.timestamp)
            );
        }

        private LocalDateTime getLocalDateTime(long timestamp){
            int millisecondsInOneSecond = 1000;
            var localDateTime = Instant.ofEpochMilli(timestamp * millisecondsInOneSecond).
                    atZone(ZoneId.systemDefault())
                    .toLocalDateTime();
            validateLocalDateTime(localDateTime);
            return localDateTime;
        }

        private void validateLocalDateTime(LocalDateTime time) {
            if(time != null && time.isAfter(LocalDateTime.now())){
                throw new InvalidTimeException("Future time not allowed, only historical data. Context: " + time);
            }
        }



    }

}


