package com.kpi.modeling.model;

import java.util.Queue;

public interface Queueable {

    Queue<Event> getEventQueue();

    void updateMeanQueue(double delta);
}
