package api.monitoring.prototipagem.model;


import api.monitoring.prototipagem.model.exceptions.PhysicalInconsistencyException;

import java.util.Objects;

public class Flow {

    private final Double value;

    private Flow(Double value) {
        this.value = value;
    }

    public static Flow of(Double value){
        Objects.requireNonNull(value,"Flow value can't be null");
       assertFlowReadingIsNotLowerThanZero(value);
        return new Flow(value);
    }
    public Double value(){
        return this.value;
    }

    private static void assertFlowReadingIsNotLowerThanZero(Double value){
        if(value < 0) {
            throw new PhysicalInconsistencyException("Flow can't be lower than 0");
        }
    }
}