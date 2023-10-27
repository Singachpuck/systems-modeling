package com.kpi.modeling;

import com.kpi.modeling.model.BaseItem;
import com.kpi.modeling.model.Create;
import com.kpi.modeling.model.Event;
import com.kpi.modeling.model.Queueable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Model {

    private double tcurr;

    private int numCreate, numProcess, failure;

    private Collection<BaseItem> schema;

    private final List<Event> events = new ArrayList<>();

    public void simulate(double timeModeling) {
        for (BaseItem item : schema) {
            if (item instanceof Create c) {
                c.populateEvent();
            }
        }
        while (tcurr < timeModeling) {
            this.onNext();
            this.printInfo();
        }
        this.printStatistic();
    }

    private void onNext() {
        Event minCreatedEvent = null;
        Event minProcessEvent = null;

        for (Event event : events) {
            if (minCreatedEvent == null || event.getStartTime() < minCreatedEvent.getStartTime()) {
                minCreatedEvent = event;
            }
            if (minProcessEvent == null || event.getProcessTime() < minProcessEvent.getProcessTime()) {
                minProcessEvent = event;
            }
        }

        final double tprev;
        if (minCreatedEvent.getStartTime() < minProcessEvent.getProcessTime()) {
            tprev = tcurr;
            tcurr = minCreatedEvent.getStartTime();
            minCreatedEvent
                    .getHeldBy()
                    .handleFinish(minCreatedEvent);
        } else {
            tprev = tcurr;
            tcurr = minProcessEvent.getProcessTime();
            minProcessEvent
                    .getHeldBy()
                    .handleFinish(minProcessEvent);
        }

        for (BaseItem item : schema) {
            if (item instanceof Queueable q) {
                q.updateMeanQueue(tcurr - tprev);
            }
        }
    }

    private void printStatistic() {
        System.out.println("\n-------------RESULTS-------------");
        System.out.println("numCreate= " + numCreate + "; numProcess = " + numProcess + "; failure = " + failure +
                "; total failure probability = " + ((double) failure / (numProcess + failure)));
        System.out.println();

        for (BaseItem item : schema) {
            item.printStatistics();
        }
    }

    private void printInfo() {
        System.out.println(" t= " + tcurr);
        for (BaseItem item : schema) {
            item.printInfo();
        }
        System.out.println();
        System.out.println();
        System.out.println();
    }

    public void setSchema(Collection<BaseItem> schema) {
        this.schema = schema;
    }

    public List<Event> getActiveEvents() {
        return this.events;
    }

    public double getTcurr() {
        return tcurr;
    }

    public void incrementFailure() {
        failure++;
    }

    public void incrementNumProcess() {
        numProcess++;
    }

    public void incrementNumCreate() {
        numCreate++;
    }
}
