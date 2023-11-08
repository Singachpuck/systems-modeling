package com.kpi.modeling.model;

import com.kpi.modeling.Model;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Predicate;

public abstract class BaseItem implements OnAccept, OnFinish {

    protected final Model model;

    protected final String name;

    protected final Mode mode;

    protected final Set<BaseItem> parents = new HashSet<>();

    protected Set<ItemParam> next;

    protected Set<ItemPredicate> nextPredicate;

    protected int quantity;

    protected boolean redistributable;

    public BaseItem(Model model, String name, Mode mode) {
        this.model = model;
        this.name = name;
        this.mode = mode;
        this.redistributable = false;

        if (mode == Mode.PRIORITY) {
            next = new TreeSet<>(Comparator.comparingDouble(ItemParam::param).reversed());
        } else if (mode == Mode.PROB){
            next = new HashSet<>();
        } else if (mode == Mode.PREDICATE) {
            nextPredicate = new HashSet<>();
        }
    }

    public abstract void printInfo();

    public abstract void printStatistics();

    public void addNext(BaseItem baseItem, double param) {
        if (mode == Mode.PREDICATE) {
            throw new UnsupportedOperationException("Not supported for mode PREDICATE.");
        }

        baseItem.parents.add(this);
        next.add(new ItemParam(baseItem, param));
    }

    public void addNext(BaseItem baseItem) {
        baseItem.parents.add(this);
        if (mode == Mode.PREDICATE) {
            nextPredicate.add(new ItemPredicate(baseItem, e -> true));
        } else {
            next.add(new ItemParam(baseItem, 1.0));
        }
    }

    public void addNext(BaseItem baseItem, Predicate<Event> predicate) {
        if (mode != Mode.PREDICATE) {
            throw new UnsupportedOperationException("Not supported for mode " + mode.name());
        }

        nextPredicate.add(new ItemPredicate(baseItem, predicate));
    }

    protected BaseItem findNext(Event event) {
        if (mode == Mode.PROB) {
            double rand = Math.random();
            for (ItemParam bip : next) {
                if (rand <= bip.param()) {
                    return bip.item();
                } else {
                    rand -= bip.param();
                }
            }
            return null;
        } else if (mode == Mode.PRIORITY) {
            int minQueue = Integer.MAX_VALUE;
            BaseItem minItem = null;
            for (ItemParam bip : next) {
                if (bip.item() instanceof Process p) {
                    if (p.isFree()) {
                        return bip.item();
                    }
                    if (minQueue > p.getEventQueue().size()) {
                        minQueue = p.getEventQueue().size();
                        minItem = p;
                    }
                } else {
                    return bip.item();
                }
            }
            if (minItem == null) {
                throw new RuntimeException("Unexpected state.");
            }
            return minItem;
        } else if (mode == Mode.PREDICATE) {
            for (ItemPredicate ip : nextPredicate) {
                if (ip.predicate.test(event)) {
                    return ip.item();
                }
            }
            return null;
        }

        throw new RuntimeException("Unknown mode " + mode.name());
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

    public int getQuantity() {
        return quantity;
    }

    public Set<BaseItem> getParents() {
        return parents;
    }

    public Set<ItemParam> getNext() {
        return next;
    }

    public Set<ItemPredicate> getNextPredicate() {
        return nextPredicate;
    }

    public boolean isRedistributable() {
        return redistributable;
    }

    public record ItemParam(BaseItem item, double param) {
    }

    public record ItemPredicate(BaseItem item, Predicate<Event> predicate) {
    }

    public enum Mode {
        PROB, PRIORITY, PREDICATE
    }
}
