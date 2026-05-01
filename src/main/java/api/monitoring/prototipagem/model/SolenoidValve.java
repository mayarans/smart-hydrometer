package api.monitoring.prototipagem.model;

public record SolenoidValve(States state) {

    public enum States {
        OPEN(Boolean.TRUE),
        CLOSED(Boolean.FALSE);

        private final Boolean state;

        States(Boolean state) {
            this.state = state;
        }

        private Boolean state(){
            return this.state;
        }

        public static States from(Boolean value){
            for(States state : States.values()){
                if(state.state().equals(value)){
                    return state;
                }
            }
            throw new IllegalArgumentException("Invalid value passed as solenoid valve state: " + value);

        }
    }
}
