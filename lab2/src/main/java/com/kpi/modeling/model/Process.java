package com.kpi.modeling.model;

import com.kpi.modeling.Model;

import java.util.ArrayDeque;
import java.util.Queue;

public class Process extends BaseItem implements Queueable {

    /**
     * Shows queue limit. If less or equal than 0 then queue is infinite.
     */
    protected final int maxQueueSize;

    protected Queue<Event> eventQueue;

    protected Distribution distribution;

    protected double meanQueue, meanTime;

    protected Event current;

    protected int failures;

    public Process(Model model, String name, Distribution distribution,
                   int maxQueueSize, Mode mode) {
        super(model, name, mode);
        this.distribution = distribution;
        this.maxQueueSize = maxQueueSize;
        this.eventQueue = new ArrayDeque<>(maxQueueSize <= 0 ? 10 : maxQueueSize);
    }

    public Process(Model model, String name, Distribution distribution,
                   int maxQueueSize, Mode mode, boolean redistributable) {
        this(model, name, distribution, maxQueueSize, mode);
        this.redistributable = redistributable;
    }

    @Override
    public void handleFinish(Event event) {
        final BaseItem nextItem = this.findNext(event);
        if (nextItem == null) {
            declareFailure(event);
            failures++;
            return;
        }
        if (!eventQueue.isEmpty()) {
            current = eventQueue.poll();
            final double processTime = distribution.getValue();
            meanTime+= processTime;
            final double processTimeEnd = model.getTcurr() + processTime;
            current.setHeldBy(this);
            current.setProcessTime(processTimeEnd);
        } else {
            current = null;
        }
        nextItem.handleAccept(event);
        quantity++;
    }

    @Override
    public void handleAccept(Event event) {
        event.setHeldBy(this);
        event.setStartTime(Double.MAX_VALUE);
        event.setProcessTime(Double.MAX_VALUE);
        if (current == null) {
            final double processTime = this.getProcessTime(event);
            meanTime += processTime;
            final double processTimeEnd = model.getTcurr() + processTime;
            event.setProcessTime(processTimeEnd);
            current = event;
        } else {
            if (maxQueueSize <= 0 || eventQueue.size() < maxQueueSize) {
                eventQueue.add(event);
            } else {
                declareFailure(event);
                failures++;
            }
        }
    }

    protected double getProcessTime(Event event) {
        return distribution.getValue();
    }

    public void printInfo() {
        System.out.printf("%s: state - %s; queue - %d; average load - %.4f\n",
                name,
                this.isFree() ? "free" : "busy",
                eventQueue.size(),
                quantity / model.getTcurr());
    }

    @Override
    public void printStatistics() {
        System.out.printf(
                """
                        %s:
                        Processed = %d
                        Mean process time = %.3f
                        Mean length of queue = %.3f
                        Failure probability = %.3f
                        Average load = %.4f
                        %n""",
                name,
                quantity,
                meanTime / (quantity + eventQueue.size()),
                meanQueue / model.getTcurr(),
                failures / (double) (failures + quantity),
                quantity / model.getTcurr());
    }

    @Override
    public void updateMeanQueue(double delta) {
        meanQueue += eventQueue.size() * delta;
    }

    @Override
    public Queue<Event> getEventQueue() {
        return eventQueue;
    }

    public void setEventQueue(Queue<Event> eventQueue) {
        this.eventQueue = eventQueue;
    }

    public Distribution getDistribution() {
        return distribution;
    }

    public void setDistribution(Distribution distribution) {
        this.distribution = distribution;
    }

    public boolean isFree() {
        return current == null;
    }

    public Event getCurrent() {
        return current;
    }

    public double getMeanTime() {
        return meanTime;
    }

    public int getFailures() {
        return failures;
    }
}
