package com.kpi.modeling;

import com.kpi.modeling.advanced.bank.BankingModel;
import com.kpi.modeling.advanced.hospital.*;
import com.kpi.modeling.model.Process;
import com.kpi.modeling.model.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import java.util.List;

public class QueueingModelTest {

    @ParameterizedTest
    @CsvFileSource(resources = "/bankProblem.csv")
    void bankModel(Distribution.DistEnum dist1, double l1, Distribution.DistEnum dist2, double l2, int queue) {
        final Model model = new BankingModel();

        final Distribution createDist = new Distribution(dist1, l1);
        final Distribution processDist = new Distribution(dist2, l2);

        Create c = new Create(model, "Create", createDist, BaseItem.Mode.PRIORITY, true);
        Process p1 = new Process(model, "Cashier1", processDist, queue, BaseItem.Mode.PROB);
        Process p2 = new Process(model, "Cashier2", processDist, queue, BaseItem.Mode.PROB);
        Destroy d = new Destroy(model, "Destroy");

        c.addNext(p1, 10);
        c.addNext(p2, 0);
        p1.addNext(d);
        p2.addNext(d);

        final List<BaseItem> schema = List.of(
                c, p1, p2, d
        );

        model.setSchema(schema);

        model.simulate(1000);
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/hospitalProblem.csv")
    void hospitalModel(Distribution.DistEnum d1, double l1,
                       Distribution.DistEnum d2, double p21, double p22,
                       Distribution.DistEnum d3, double p31, double p32,
                       Distribution.DistEnum d4, double p41, double p42,
                       Distribution.DistEnum d5, double p51, double p52) {
        final Model model = new Model();

        final Distribution createDist = new Distribution(d1, l1);
        final Create c = new HospitalCreate(model, "Patient generator", createDist, BaseItem.Mode.PROB);

        // Doctor cabinet
        final MultichannelProcess doctorCabinet = new MultichannelProcess(model, "Doctor cabinet",
                -1, BaseItem.Mode.PREDICATE);
        final Process doctor1 = new DoctorProcess(model, "Doctor 1");
        final Process doctor2 = new DoctorProcess(model, "Doctor 2");
        doctorCabinet.addChannel(doctor1);
        doctorCabinet.addChannel(doctor2);

        final Distribution cabinetLabDist = new Distribution(d3, p31, p32);
        final Process fromCabinetToLab = new Process(model, "From cabinet to lab", cabinetLabDist, -1, BaseItem.Mode.PROB);

        // Laboratory
        final Distribution registrationDist = new Distribution(d4, p41, p42);
        final Process registration = new Process(model, "Registration", registrationDist, -1, BaseItem.Mode.PROB);

        final Distribution analysisDist = new Distribution(d5, p51, p52);
        final MultichannelProcess laboratory = new MultichannelProcess(model, "Laboratory", -1, BaseItem.Mode.PREDICATE, true);
        final Process labWorker1 = new LaboratoryProcess(model, "Lab worker 1", analysisDist);
        final Process labWorker2 = new LaboratoryProcess(model, "Lab worker 2", analysisDist);
        laboratory.addChannel(labWorker1);
        laboratory.addChannel(labWorker2);

        final Process fromLabToCabinet = new Process(model, "From lab to cabinet", cabinetLabDist, -1, BaseItem.Mode.PROB);

        // Chamber
        final Distribution chamberDist = new Distribution(d2, p21, p22);
        final MultichannelProcess nurses = new MultichannelProcess(model, "Nurses", -1, BaseItem.Mode.PROB);
        final Process nurse1 = new Process(model, "Nurse 1", chamberDist, -1, BaseItem.Mode.PROB);
        final Process nurse2 = new Process(model, "Nurse 2", chamberDist, -1, BaseItem.Mode.PROB);
        final Process nurse3 = new Process(model, "Nurse 3", chamberDist, -1, BaseItem.Mode.PROB);
        nurses.addChannel(nurse1);
        nurses.addChannel(nurse2);
        nurses.addChannel(nurse3);

        final Destroy chamberFinish = new HospitalDestroy(model, "Chamber finish");
        final Destroy labFinish = new HospitalDestroy(model, "Lab finish");

        c.addNext(doctorCabinet);

        doctorCabinet.addNext(nurses, e -> e instanceof HospitalPatient patient
                && patient.getType() == HospitalPatient.PatientType.ONE);
        nurses.addNext(chamberFinish);

        doctorCabinet.addNext(fromCabinetToLab, e -> e instanceof HospitalPatient patient
                && (patient.getType() == HospitalPatient.PatientType.TWO || patient.getType() == HospitalPatient.PatientType.THREE));

        fromCabinetToLab.addNext(registration);
        registration.addNext(laboratory);

        laboratory.addNext(fromLabToCabinet, e -> e instanceof HospitalPatient patient
                && patient.getType() == HospitalPatient.PatientType.ONE);
        laboratory.addNext(labFinish, e -> e instanceof HospitalPatient patient
                && patient.getType() == HospitalPatient.PatientType.THREE);

        fromLabToCabinet.addNext(doctorCabinet);


        final List<BaseItem> schema = List.of(
                c, doctorCabinet, doctor1, doctor2, fromCabinetToLab, registration,
                laboratory, labWorker1, labWorker2, fromLabToCabinet, nurses, nurse1, nurse2, nurse3,
                chamberFinish, labFinish
        );

        model.setSchema(schema);
        model.simulate(1000);
    }
}
