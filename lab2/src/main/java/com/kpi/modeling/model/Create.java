package com.kpi.modeling.model;

import com.kpi.modeling.Model;

public class Create extends BaseItem {

    private final Distribution distribution;

    public Create(Model model, String name, Distribution distribution, Mode mode) {
        super(model, name, mode);
        this.distribution = distribution;
    }

    public Create(Model model, String name, Distribution distribution, Mode mode, boolean redistributable) {
        this(model, name, distribution, mode);
        this.redistributable = redistributable;
    }

    public Event populateEvent() {
        final double start = model.getTcurr() + distribution.getValue();
        final Event newEvent = new Event(start);
        newEvent.setHeldBy(this);
        model.getActiveEvents().add(newEvent);
        return newEvent;
    }

    @Override
    public void handleFinish(Event event) {
        final BaseItem nextItem = this.findNext(event);
        if (nextItem == null) {
            declareFailure(event);
            return;
        }

        model.incrementNumCreate();
        quantity++;
        nextItem.handleAccept(event);
        this.populateEvent();
    }

    public Distribution getDistribution() {
        return distribution;
    }

    @Override
    public void printInfo() {
        System.out.printf("%s: created - %d\n", name, quantity);
    }

    @Override
    public void printStatistics() {
    }
}
