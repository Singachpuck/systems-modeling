package com.kpi.modeling.model;

import com.kpi.modeling.Model;

import java.util.ArrayDeque;
import java.util.Queue;

public class Process extends BaseItem implements Queueable {

    private final Distribution distribution;

    private final int maxQueueSize;

    private final Queue<Event> eventQueue;

    private double meanQueue;

    private Event current;

    private int failures;

    public Process(Model model, String name, Distribution distribution, int maxQueueSize) {
        super(model, name);
        this.distribution = distribution;
        this.maxQueueSize = maxQueueSize;
        this.eventQueue = new ArrayDeque<>(maxQueueSize);
    }

    @Override
    public void handleFinish(Event event) {
        double rand = Math.random();
        for (BaseItemProb bip : next) {
            if (rand <= bip.probability()) {
                if (!eventQueue.isEmpty()) {
                    current = eventQueue.poll();
                    final double processTime = model.getTcurr() + distribution.getValue();
                    current.setProcessTime(processTime);
                } else {
                    current = null;
                }
                bip.item().handleAccept(event);
                quantity++;
                return;
            } else {
                rand -= bip.probability();
            }
        }
        declareFailure(event);
        failures++;
    }

    @Override
    public void handleAccept(Event event) {
        event.setHeldBy(this);
        event.setStartTime(Double.MAX_VALUE);
        event.setProcessTime(Double.MAX_VALUE);
        if (current == null) {
            final double processTime = model.getTcurr() + distribution.getValue();
            event.setProcessTime(processTime);
            current = event;
        } else {
            if (eventQueue.size() < maxQueueSize) {
                eventQueue.add(event);
            } else {
                declareFailure(event);
                failures++;
            }
        }
    }

    public void printInfo() {
        System.out.printf("%s: state - %s; queue - %d; average load - %.4f\n",
                name,
                current == null ? "free" : "busy",
                eventQueue.size(),
                quantity / model.getTcurr());
    }

    @Override
    public void printStatistics() {
        System.out.printf(
                """
                        %s:
                        Mean length of queue = %.3f
                        Failure probability = %.3f
                        Average load = %.4f
                        %n""",
                name,
                meanQueue / model.getTcurr(),
                failures / (double) (failures + quantity),
                quantity / model.getTcurr());
    }

    @Override
    public void updateMeanQueue(double delta) {
        meanQueue += eventQueue.size() * delta;
    }
}
