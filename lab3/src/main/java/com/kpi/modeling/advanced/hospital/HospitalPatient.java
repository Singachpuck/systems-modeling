package com.kpi.modeling.advanced.hospital;

import com.kpi.modeling.model.Event;

public class HospitalPatient extends Event {

    private PatientType type;

    private final double startTimeFixed;

    public HospitalPatient(double startTime, PatientType type) {
        super(startTime);
        this.type = type;
        this.startTimeFixed = startTime;
    }

    public PatientType getType() {
        return type;
    }

    public void setType(PatientType type) {
        this.type = type;
    }

    public enum PatientType {
        ONE(15), TWO(40), THREE(30);

        private final double processTime;

        PatientType(double processTime) {
            this.processTime = processTime;
        }

        public double getProcessTime() {
            return processTime;
        }
    }

    @Override
    public String toString() {
        return String.format("Patient of type %s has been in hospital for %.3f", type.name(),
                this.getProcessTime() - startTimeFixed);
    }
}
