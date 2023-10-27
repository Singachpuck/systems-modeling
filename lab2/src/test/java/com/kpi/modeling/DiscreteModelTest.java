package com.kpi.modeling;

import com.kpi.modeling.model.*;
import com.kpi.modeling.model.Process;
import org.junit.jupiter.api.Test;

import java.util.List;

class DiscreteModelTest {

    private final Model model = new Model();

    @Test
    void simpleTest() {
        final Distribution createDist = new Distribution(Distribution.DistEnum.EXP, 5.0);
        final Distribution processDist = new Distribution(Distribution.DistEnum.EXP, 4.0);

        Create c = new Create(model, "Create", createDist);
        Process p1 = new Process(model, "Process1", processDist, 3);
        Destroy d = new Destroy(model, "Destroy");

        c.addNext(p1, 1.0);
        p1.addNext(d, 1.0);

        final List<BaseItem> schema = List.of(
                c, p1, d
        );

        model.setSchema(schema);

        model.simulate(1000);
    }

    @Test
    void chainTest() {
        final Distribution createDist = new Distribution(Distribution.DistEnum.EXP, 3.0);
        final Distribution processDist = new Distribution(Distribution.DistEnum.EXP, 4.0);

        Create c = new Create(model, "Create", createDist);
        Process p1 = new Process(model, "Process1", processDist, 5);
        Process p2 = new Process(model, "Process2", processDist, 5);
        Process p3 = new Process(model, "Process3", processDist, 5);
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

    @Test
    void complexTest() {
        final Distribution createDist = new Distribution(Distribution.DistEnum.EXP, 3.0);
        final Distribution processDist = new Distribution(Distribution.DistEnum.NORMAL, 4.0, 1.0);

        Create c = new Create(model, "Create", createDist);
        Process p1 = new Process(model, "Process1", processDist, 5);
        Process p2 = new Process(model, "Process2", processDist, 5);
        Process p3 = new Process(model, "Process3", processDist, 5);
        Process p4 = new Process(model, "Process4", processDist, 5);
        Process p5 = new Process(model, "Process5", processDist, 5);
        Destroy d = new Destroy(model, "Destroy");

        c.addNext(p1, 0.3);
        c.addNext(p2, 0.7);
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