package com.kpi.modeling.advanced.hospital;

import com.kpi.modeling.Model;
import com.kpi.modeling.model.Destroy;
import com.kpi.modeling.model.Event;

public class HospitalDestroy extends Destroy {

    public HospitalDestroy(Model model, String name) {
        super(model, name);
    }

    @Override
    public void handleAccept(Event event) {
        event.setHeldBy(null);
        if (model.getActiveEvents().remove(event)) {
            model.incrementNumProcess();
            quantity++;
            if (event instanceof HospitalPatient patient) {
                this.printPatient(patient);
            }
        }
    }

    private void printPatient(HospitalPatient patient) {
        System.out.println("Patient left. " + patient);
        System.out.println();
    }
}
