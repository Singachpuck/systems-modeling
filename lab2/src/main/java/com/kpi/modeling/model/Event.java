package com.kpi.modeling.model;

public class Event {

    private BaseItem heldBy;

    private double startTime;

    private double processTime = Double.MAX_VALUE;

    public Event(double startTime) {
        this.startTime = startTime;
    }

    public double getStartTime() {
        return startTime;
    }

    public void setStartTime(double startTime) {
        this.startTime = startTime;
    }

    public double getProcessTime() {
        return processTime;
    }

    public void setProcessTime(double processTime) {
        this.processTime = processTime;
    }

    public BaseItem getHeldBy() {
        return heldBy;
    }

    public void setHeldBy(BaseItem heldBy) {
        this.heldBy = heldBy;
    }
}
