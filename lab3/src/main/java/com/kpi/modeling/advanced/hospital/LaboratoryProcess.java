package com.kpi.modeling.advanced.hospital;

import com.kpi.modeling.Model;
import com.kpi.modeling.model.Distribution;
import com.kpi.modeling.model.Event;
import com.kpi.modeling.model.Process;

public class LaboratoryProcess extends Process {

    public LaboratoryProcess(Model model, String name, Distribution distribution) {
        super(model, name, distribution, -1, Mode.PROB);
    }

    @Override
    public void handleFinish(Event event) {
        if (event instanceof HospitalPatient patient && patient.getType() == HospitalPatient.PatientType.TWO) {
            patient.setType(HospitalPatient.PatientType.ONE);
        }
        super.handleFinish(event);
    }
}
