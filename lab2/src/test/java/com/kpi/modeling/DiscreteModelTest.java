package com.kpi.modeling;

import com.kpi.modeling.model.Process;
import com.kpi.modeling.model.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import java.util.List;

class DiscreteModelTest {

    private final Model model = new Model();

    @ParameterizedTest
    @CsvFileSource(resources = "/simpleModel.csv")
    void simpleTest(Distribution.DistEnum dist1, double l1, Distribution.DistEnum dist2, double l2, int queue) {
        final Distribution createDist = new Distribution(dist1, l1);
        final Distribution processDist = new Distribution(dist2, l2);

        Create c = new Create(model, "Create", createDist);
        Process p1 = new Process(model, "Process1", processDist, queue);
        Destroy d = new Destroy(model, "Destroy");

        c.addNext(p1, 1.0);
        p1.addNext(d, 1.0);

        final List<BaseItem> schema = List.of(
                c, p1, d
        );

        model.setSchema(schema);

        model.simulate(1000);
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/chainModel.csv")
    void chainTest(Distribution.DistEnum dist1, double l1, Distribution.DistEnum dist2, double l2, int queue1, int queue2, int queue3) {
        final Distribution createDist = new Distribution(dist1, l1);
        final Distribution processDist = new Distribution(dist2, l2);

        Create c = new Create(model, "Create", createDist);
        Process p1 = new Process(model, "Process1", processDist, queue1);
        Process p2 = new Process(model, "Process2", processDist, queue2);
        Process p3 = new Process(model, "Process3", processDist, queue3);
        Destroy d = new Destroy(model, "Destroy");

        c.addNext(p1, 1.0);
        p1.addNext(p2, 1.0);
        p2.addNext(p3, 1.0);
        p3.addNext(d, 1.0);

        final List<BaseItem> schema = List.of(
                c, p1, p2, p3, d
        );

        model.setSchema(schema);

        model.simulate(1000);
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/complexModel.csv")
    void complexTest(Distribution.DistEnum dist1, double l1, Distribution.DistEnum dist2, double mean1,
                     double s1, int queue1, int queue2, int queue3, int queue4, int queue5, double prob1, double prob2) {
        final Distribution createDist = new Distribution(dist1, l1);
        final Distribution processDist = new Distribution(dist2, mean1, s1);

        Create c = new Create(model, "Create", createDist);
        Process p1 = new Process(model, "Process1", processDist, queue1);
        Process p2 = new Process(model, "Process2", processDist, queue2);
        Process p3 = new Process(model, "Process3", processDist, queue3);
        Process p4 = new Process(model, "Process4", processDist, queue4);
        Process p5 = new Process(model, "Process5", processDist, queue5);
        Destroy d = new Destroy(model, "Destroy");

        c.addNext(p1, prob1);
        c.addNext(p2, prob2);
        p1.addNext(p3, 1.0);
        p2.addNext(p4, 1.0);
        p3.addNext(p5, 1.0);
        p4.addNext(p5, 1.0);
        p5.addNext(d, 1.0);

        final List<BaseItem> schema = List.of(
                c, p1, p2, p3, p4, p5, d
        );

        model.setSchema(schema);

        model.simulate(1000);
    }
}