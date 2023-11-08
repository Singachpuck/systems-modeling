package com.kpi.modeling;

import com.kpi.modeling.model.Process;
import com.kpi.modeling.model.*;

import java.util.*;

public class Model {

    private double tcurr;

    private int numCreate, numProcess, failure, trades;

    private Collection<BaseItem> schema;

    private final List<Event> events = new ArrayList<>();

    protected void init() {
        for (BaseItem item : schema) {
            if (item instanceof Create c) {
                c.populateEvent();
            }
        }
    }

    public void simulate(double timeModeling) {
        this.init();
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
            final BaseItem create = minCreatedEvent.getHeldBy();
            create.handleFinish(minCreatedEvent);

            // Redistribution
            this.redistributeTo(minCreatedEvent.getHeldBy());
        } else {
            tprev = tcurr;
            tcurr = minProcessEvent.getProcessTime();

            final Process owner = (Process) minProcessEvent.getHeldBy();
            owner.handleFinish(minProcessEvent);

            // Redistribution
            this.redistributeFrom(owner);
            this.redistributeTo(minCreatedEvent.getHeldBy());
        }

        for (BaseItem item : schema) {
            if (item instanceof Queueable q) {
                q.updateMeanQueue(tcurr - tprev);
            }
        }
    }

    private void redistributeFrom(BaseItem owner) {
        if (!(owner instanceof Queueable)) {
            return;
        }

        final Optional<Queueable> tradeWith = owner.getParents()
                .stream()
                .filter(BaseItem::isRedistributable)
                .flatMap(parent -> parent.getNext().stream())
                .map(BaseItem.ItemParam::item)
                .filter(sibling -> sibling instanceof Queueable q
                        && (q.getEventQueue().size() - ((Queueable) owner).getEventQueue().size() >= 2))
                .map(sibling -> (Queueable) sibling)
                .findAny();

        if (tradeWith.isPresent() && tradeWith.get().getEventQueue() instanceof Deque<Event> deque) {
            final Event toTrade = deque.pollFirst();
            owner.handleAccept(toTrade);
            trades++;
        }
    }

    private void redistributeTo(BaseItem newOwner) {
        if (!(newOwner instanceof Queueable)) {
            return;
        }

        final Optional<Queueable> tradeWith = newOwner.getParents()
                .stream()
                .filter(BaseItem::isRedistributable)
                .flatMap(parent -> parent.getNext().stream())
                .map(BaseItem.ItemParam::item)
                .filter(child -> child instanceof Queueable q
                        && (((Queueable) newOwner).getEventQueue().size() - q.getEventQueue().size() >= 2))
                .map(sibling -> (Queueable) sibling)
                .findAny();

        if (tradeWith.isPresent() && ((Queueable) newOwner).getEventQueue() instanceof Deque<Event> deque) {
            final Event toTrade = deque.pollFirst();
            ((OnAccept) tradeWith.get()).handleAccept(toTrade);
            trades++;
        }
    }

    private void printStatistic() {
        System.out.println("\n-------------RESULTS-------------");
        System.out.println("numCreate= " + numCreate + "; numProcess = " + numProcess + "; failure = " + failure +
                "; total failure probability = " + ((double) failure / (numProcess + failure)) +
                "; trades = " + trades);
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

    public Collection<BaseItem> getSchema() {
        return schema;
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
