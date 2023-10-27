package com.kpi.modeling.model;

import com.kpi.modeling.Model;

import java.util.HashSet;
import java.util.Set;

public abstract class BaseItem implements OnAccept, OnFinish {

    protected final Model model;

    protected final String name;

    protected final Set<BaseItemProb> next = new HashSet<>();

    protected int quantity;

    public BaseItem(Model model, String name) {
        this.model = model;
        this.name = name;
    }

    public abstract void printInfo();

    public abstract void printStatistics();

    public void addNext(BaseItem baseItem, double probability) {
        next.add(new BaseItemProb(baseItem, probability));
    }

    public void declareFailure(Event event) {
        if (model.getActiveEvents().remove(event)) {
            model.incrementFailure();
        }
    }

    @Override
    public void handleAccept(Event event) {
        declareFailure(event);
    }

    @Override
    public void handleFinish(Event event) {
        declareFailure(event);
    }

    protected record BaseItemProb(BaseItem item, double probability) {
    }
}
