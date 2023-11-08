package com.kpi.modeling.advanced.hospital;

import com.kpi.modeling.Model;
import com.kpi.modeling.model.BaseItem;
import com.kpi.modeling.model.Event;
import com.kpi.modeling.model.Process;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MultichannelProcess extends Process {

    private final List<Process> channels = new ArrayList<>();

    private final Hub exitPoint;

    private final boolean withMeanArrival;

    private double prevArrival;

    private double meanArrival;

    public MultichannelProcess(Model model, String name, int maxQueueSize, Mode mode, boolean withMeanArrival) {
        super(model, name, null, maxQueueSize, mode);

        this.exitPoint = new Hub(model, String.format("Hub (%s)", name), mode);
        this.next = this.exitPoint.getNext();
        this.nextPredicate = this.exitPoint.getNextPredicate();
        this.withMeanArrival = withMeanArrival;
    }

    public MultichannelProcess(Model model, String name, int maxQueueSize, Mode mode) {
        this(model, name, maxQueueSize, mode, false);
    }

    public void addChannel(Process channel) {
        channel.getParents().clear();
        channel.getNext().clear();
        channel.addNext(exitPoint);
        channel.setEventQueue(eventQueue);
        channels.add(channel);
    }

    @Override
    public void handleAccept(Event event) {
        event.setHeldBy(this);
        event.setStartTime(Double.MAX_VALUE);
        event.setProcessTime(Double.MAX_VALUE);
        boolean taken = false;
        final List<Process> shuffled = new ArrayList<>(channels);
        Collections.shuffle(shuffled);
        for (Process process : shuffled) {
            if (process.getCurrent() == null) {
                process.handleAccept(event);
                taken = true;
                break;
            }
        }

        if (!taken) {
            if (maxQueueSize <= 0 || eventQueue.size() < maxQueueSize) {
                eventQueue.add(event);
            } else {
                declareFailure(event);
                failures++;
            }
        }

        if (withMeanArrival) {
            meanArrival += model.getTcurr() - prevArrival;
            prevArrival = model.getTcurr();
        }
    }

    @Override
    public void handleFinish(Event event) {
        throw new UnsupportedOperationException("handleFinish() is not supported for Multichannel Process.");
    }

    public void printInfo() {}

    @Override
    public void printStatistics() {
        final int quantity = channels.stream()
                .mapToInt(BaseItem::getQuantity)
                .sum();

        final double mean = channels.stream()
                .mapToDouble(Process::getMeanTime)
                .sum();

        final double meanSplit = quantity + eventQueue.size();

        final int failures = channels.stream()
                .mapToInt(Process::getFailures)
                .sum();

        System.out.printf(
                """
                        %s:
                        Processed = %d
                        Mean process time = %.3f
                        Mean length of queue = %.3f
                        Failure probability = %.3f
                        Average load = %.4f
                        Average arrival time = %.3f
                        %n""",
                name,
                quantity,
                mean / meanSplit,
                meanQueue / model.getTcurr(),
                failures / (double) (failures + quantity),
                quantity / model.getTcurr(),
                meanArrival / meanSplit);
    }
}
