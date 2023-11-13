package com.kpi.modeling;

import com.kpi.modeling.advanced.hospital.MultichannelProcess;
import com.kpi.modeling.model.Process;
import com.kpi.modeling.model.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class PerformanceModelTest {

    static Stream<Arguments> range() {
        final int nStart = 100,
                nEnd = 1000,
                nStep = 100;
        final List<Arguments> arguments = new ArrayList<>();
        for (int i = nStart; i < nEnd; i+= nStep) {
            arguments.add(Arguments.of(i));
        }
        return arguments.stream();
    }

    @ParameterizedTest
    @MethodSource("range")
    void chainPerformance(int n) {
        final EventLimitedModel model = new EventLimitedModel();
        final List<BaseItem> schema = new ArrayList<>();

        final Distribution createDist = new Distribution(Distribution.DistEnum.EXP, 5.0);
        final Create c = new Create(model, "Create", createDist, BaseItem.Mode.PROB);
        schema.add(c);

        BaseItem parent = c;
        for (int i = 0; i < n; i++) {
            final MultichannelProcess next = new MultichannelProcess(model, "Process " + i, -1, BaseItem.Mode.PROB);
            final Distribution childDist = new Distribution(Distribution.DistEnum.NORMAL, 10.0, 5.0);
            final Process child1 = new Process(model, "Process " + i + " child 1", childDist, -1, BaseItem.Mode.PROB);
            final Process child2 = new Process(model, "Process " + i + " child 2", childDist, -1, BaseItem.Mode.PROB);
            next.addChannel(child1);
            next.addChannel(child2);
            schema.add(next);

            parent.addNext(next);
            parent = next;
        }

        final Destroy d = new Destroy(model, "Destroy");
        schema.add(d);
        parent.addNext(d);

        model.setSchema(schema);

        long before = System.nanoTime();
        model.simulateWithLimitedEvents(n);
        long after = System.nanoTime();
        System.out.println("Processed: " + model.numProcess);
        System.out.println("Elapsed time: " + (after - before));
    }
}
