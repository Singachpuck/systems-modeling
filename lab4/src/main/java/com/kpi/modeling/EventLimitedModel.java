package com.kpi.modeling;

public class EventLimitedModel extends Model {

    @Override
    protected void printInfo() {
    }

    @Override
    protected void printStatistic() {
    }

    public void simulateWithLimitedEvents(int limit) {
        this.init();
        while (numProcess <= limit) {
            this.onNext();
            this.printInfo();
        }
        this.printStatistic();
    }
}
