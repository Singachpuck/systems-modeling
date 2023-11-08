package com.kpi.modeling.advanced.hospital;

import com.kpi.modeling.Model;
import com.kpi.modeling.model.Create;
import com.kpi.modeling.model.Distribution;
import com.kpi.modeling.model.Event;

public class HospitalCreate extends Create {

    public HospitalCreate(Model model, String name, Distribution distribution, Mode mode) {
        super(model, name, distribution, mode);
    }

    @Override
    public Event populateEvent() {
        final double start = model.getTcurr() + this.getDistribution().getValue();
        final Event newEvent = this.generatePatient(start);
        newEvent.setHeldBy(this);
        model.getActiveEvents().add(newEvent);
        return newEvent;
    }

    private HospitalPatient generatePatient(double start) {
        double rand = Math.random();
        if (rand <= 0.5) {
            return new HospitalPatient(start, HospitalPatient.PatientType.ONE);
        } else if (rand <= 0.6) {
            return new HospitalPatient(start, HospitalPatient.PatientType.TWO);
        } else {
            return new HospitalPatient(start, HospitalPatient.PatientType.THREE);
        }
    }
}
