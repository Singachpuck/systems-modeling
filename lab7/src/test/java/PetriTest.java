import PetriObj.*;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PetriTest {

    @Test
    void basic() throws ExceptionInvalidTimeDelay {
        final List<PetriP> places = new ArrayList<>();
        final List<PetriT> transitions = new ArrayList<>();
        final List<ArcIn> arcsIn = new ArrayList<>();
        final List<ArcOut> arcsOut = new ArrayList<>();

        final double[] processTime = {6.0, 7.0, 5.0};

        PetriT prev = null;
        for (int i = 0; i < 3; i++) {
            // Places
            final PetriP finish = new PetriP("Finish" + i);
            final PetriP toRobot = new PetriP("ToRobot" + i);
            final PetriP toProcess = new PetriP("ToProcess" + i);
            final PetriP toExit = new PetriP("ToExit" + i);
            final PetriP toBreak = new PetriP("ToBreak" + i);
            final PetriP ready = new PetriP("Ready" + i);
            ready.setMark(1);
            if (i == 0) {
                final PetriP start = new PetriP("Start" + i);
                start.setMark(1);
                places.add(start);
                final PetriT generateT = new PetriT("Generate" + i);
                generateT.setDistribution("exp", 40.0);
                transitions.add(generateT);
                arcsIn.add(new ArcIn(start, generateT));
                arcsOut.add(new ArcOut(generateT, start, 1));
                arcsOut.add(new ArcOut(generateT, toRobot, 1));
            } else {
                arcsOut.add(new ArcOut(prev, toRobot, 1));
            }

            places.addAll(List.of(
                    toRobot,
                    toProcess,
                    toExit,
                    toBreak,
                    ready,
                    finish
            ));

            // Transitions
            final PetriT enterT = new PetriT("Enter" + i);
            enterT.setDistribution("unif", 8.0);
            enterT.setParamDeviation(1.0);
            transitions.add(enterT);
            final PetriT processT = new PetriT("Process" + i, processTime[i]);
            transitions.add(processT);
            final PetriT exitT = new PetriT("Exit" + i);
            exitT.setDistribution("unif", 8.0);
            exitT.setParamDeviation(1.0);
            transitions.add(exitT);
            final PetriT breakT = new PetriT("Break" + i, processTime[i]);
            transitions.add(breakT);

            // Arcs
            arcsIn.add(new ArcIn(toRobot, enterT));
            arcsOut.add(new ArcOut(enterT, toProcess, 1));
            arcsIn.add(new ArcIn(toProcess, processT));
            arcsOut.add(new ArcOut(processT, toExit, 1));
            arcsIn.add(new ArcIn(toExit, exitT));
            arcsOut.add(new ArcOut(exitT, toBreak, 1));
            arcsIn.add(new ArcIn(toBreak, breakT));
            arcsOut.add(new ArcOut(breakT, ready, 1));
            arcsIn.add(new ArcIn(ready, enterT));

            arcsOut.add(new ArcOut(exitT, finish, 1));

            if (i != 2) {
                final PetriP nextReady = new PetriP("NextReady" + i);
                nextReady.setMark(3);
                places.add(nextReady);
                final PetriT nextT = new PetriT("Next" + i);
                if (i == 0) {
                    nextT.setDistribution("norm", 60.0);
                    nextT.setParamDeviation(10.0);
                } else {
                    nextT.setDistribution("exp", 100.0);
                }
                transitions.add(nextT);

                arcsIn.add(new ArcIn(finish, nextT));
                arcsOut.add(new ArcOut(nextT, nextReady, 1));
                arcsIn.add(new ArcIn(nextReady, nextT));
                prev = nextT;
            }
        }


        // Run
        final PetriNet net = new PetriNet("Task 2",
                (ArrayList<PetriP>) places,
                (ArrayList<PetriT>) transitions,
                (ArrayList<ArcIn>) arcsIn,
                (ArrayList<ArcOut>) arcsOut);
        PetriP.initNext();
        PetriT.initNext();
        ArcIn.initNext();
        ArcOut.initNext();
        final PetriSim sim = new PetriSim(net);
        final PetriObjModel model = new PetriObjModel(new ArrayList<>(Collections.singletonList(sim)));

        model.go(1000);

        System.out.println(places.get(places.size() - 1).getMark());
    }
}
