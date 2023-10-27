package com.kpi.modeling.model;

import com.kpi.modeling.Model;

public class Create extends BaseItem {

    private final Distribution distribution;

    public Create(Model model, String name, Distribution distribution) {
        super(model, name);
        this.distribution = distribution;
    }

    public void populateEvent() {
        final double start = model.getTcurr() + distribution.getValue();
        final Event newEvent = new Event(start);
        newEvent.setHeldBy(this);
        model.getActiveEvents().add(newEvent);
    }

    @Override
    public void handleFinish(Event event) {
        double rand = Math.random();
        for (BaseItemProb bip : next) {
            if (rand <= bip.probability()) {
                model.incrementNumCreate();
                quantity++;
                bip.item().handleAccept(event);
                this.populateEvent();
                return;
            } else {
                rand -= bip.probability();
            }
        }
        declareFailure(event);
    }

    @Override
    public void printInfo() {
        System.out.printf("%s: created - %d\n", name, quantity);
    }

    @Override
    public void printStatistics() {
    }
}
