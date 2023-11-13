package com.kpi.modeling;

public class EventLimitedModel extends Model {

    public void simulateWithLimitedEvents(int limit) {
        this.init();
        while (numProcess <= limit) {
            this.onNext();
            this.printInfo();
        }
        this.printStatistic();
    }
}
