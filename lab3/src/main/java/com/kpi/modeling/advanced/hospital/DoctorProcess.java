package com.kpi.modeling.advanced.hospital;

import com.kpi.modeling.Model;
import com.kpi.modeling.model.Distribution;
import com.kpi.modeling.model.Event;
import com.kpi.modeling.model.Process;

public class DoctorProcess extends Process {

    public DoctorProcess(Model model, String name) {
        super(model, name, new Distribution(Distribution.DistEnum.FIXED, 10.0), 0, Mode.PROB);
    }

    @Override
    protected double getProcessTime(Event event) {
        if (event instanceof HospitalPatient patient) {
            return patient.getType().getProcessTime();
        }

        System.out.println("Using regular Event type with default distribution: " + distribution.getDist().name() + " with value 10.");
        return super.getProcessTime(event);
    }
}
