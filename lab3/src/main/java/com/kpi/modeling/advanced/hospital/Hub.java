package com.kpi.modeling.advanced.hospital;

import com.kpi.modeling.Model;
import com.kpi.modeling.model.Event;
import com.kpi.modeling.model.Process;

public class Hub extends Process {

    public Hub(Model model, String name, Mode mode) {
        super(model, name, null, 1, mode);
    }

    @Override
    public void handleAccept(Event event) {
        this.handleFinish(event);
    }

    @Override
    public void printInfo() {}

    @Override
    public void printStatistics() {}
}
