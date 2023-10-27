package com.kpi.modeling.model;

import com.kpi.modeling.Model;

public class Destroy extends BaseItem {

    public Destroy(Model model, String name) {
        super(model, name);
    }

    @Override
    public void handleAccept(Event event) {
        if (model.getActiveEvents().remove(event)) {
            model.incrementNumProcess();
            quantity++;
        }
    }

    @Override
    public void printInfo() {
        System.out.printf("%s: destroyed - %d\n", name, quantity);
    }

    @Override
    public void printStatistics() {}
}
