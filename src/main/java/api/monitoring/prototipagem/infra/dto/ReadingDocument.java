package api.monitoring.prototipagem.infra.dto;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import api.monitoring.prototipagem.model.ReadingData;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Document(indexName = "sensor_readings")
public class ReadingDocument {

    @Id
    private String id;
    private MicrocontrollerPart microcontroller;
    private FlowPart flow;
    private LevelSensorPart levelSensor;
    private ValvePart valve;
    @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second)
    private LocalDateTime timestamp;

    private ReadingDocument(String id,
                            MicrocontrollerPart microcontroller,
                            FlowPart flow,
                            LevelSensorPart levelSensor,
                            ValvePart valve,
                            LocalDateTime timestamp) {
        this.id = id;
        this.microcontroller = microcontroller;
        this.flow = flow;
        this.levelSensor = levelSensor;
        this.valve = valve;
        this.timestamp = timestamp;
    }

    public static Builder aReadingDocument(){
        return new Builder();
    }


    public static class Builder {
        private String id;
        private Long microId;
        private Double flowValue;
        private String valveState;
        private Boolean waterDetected;
        private LocalDateTime timestamp;

        public Builder withId(String id){
            this.id = id;
            return this;
        }

        public Builder withMicrocontrollerId(Long id){
            this.microId = id;
            return this;
        }

        public Builder withFlowValue(Double value){
            this.flowValue = value;
            return this;
        }

        public Builder withValveState(String state){
            this.valveState = state;
            return this;
        }

        public Builder withWaterDetected(Boolean waterDetected){
            this.waterDetected = waterDetected;
            return this;
        }

        public Builder withTimestamp(LocalDateTime timestamp){
            this.timestamp = timestamp;
            return this;
        }

        public ReadingDocument build(){
            return new ReadingDocument(
                    this.id,
                    new MicrocontrollerPart(this.microId),
                    new FlowPart(this.flowValue),
                    new LevelSensorPart(this.waterDetected),
                    new ValvePart(this.valveState),
                    this.timestamp
            );
        }
    }


    private record MicrocontrollerPart(Long id){}
    private record FlowPart (Double value){}
    private record LevelSensorPart(Boolean waterDetected) {}
    private record ValvePart(String state){}

    public ReadingData toReadingData() {
        return ReadingData.aReadingData()
                .withMicrocontrollerId(this.microcontroller.id)
                .withFlowReading(this.flow.value)
                .withValveState(this.valve.state.equalsIgnoreCase("OPEN"))
                .withWaterDetected(this.levelSensor.waterDetected)
                .withTimestamp(this.timestamp.atZone(ZoneId.systemDefault()).toEpochSecond())
                .build();
    }
}