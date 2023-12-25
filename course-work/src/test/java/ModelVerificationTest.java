import PetriObj.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.ArrayList;

public class ModelVerificationTest {

    private double SIM_TIME = 1_000_000;

    @ParameterizedTest
    @ValueSource(ints = {1000, 5000, 10000, 20000, 50000, 100000, 200000, 500000, 1_000_000, 2_000_000})
    public void runNormal(double t) throws ExceptionInvalidTimeDelay {
        SIM_TIME = t;
        this.runModel(20,20,30,30,20,20,20,20,20,8);
    }

    @ParameterizedTest
    @CsvFileSource(resources = "regression.csv")
    public void runRegression(double p1, double p2, double p3) throws ExceptionInvalidTimeDelay {
        this.runModel(p1,20,30,p2,20,20,20,p3,20,8);
    }

    @ParameterizedTest
    @CsvFileSource(resources = "input.csv", delimiter = '\t')
    public void runModel(double loadA, double loadB, double trA, double trB, double trC, double unloadB, double unloadC, double genA, double genB, int busA) throws ExceptionInvalidTimeDelay {
        final ArrayList<PetriSim> list = new ArrayList<>();
        list.add(new PetriSim(this.getGenerateDetails(genA, 3.0)));
        list.add(new PetriSim(this.getLoadTransitionUnloadPart(loadA, trA, unloadB, busA)));
        list.add(new PetriSim(this.getEmptyTransitionPart(trA)));
        list.add(new PetriSim(this.getGenerateDetails(genB, 5.0)));
        list.add(new PetriSim(this.getLoadTransitionUnloadPart(loadB, trB, unloadC, 0)));
        list.add(new PetriSim(this.getEmptyTransitionPart(trB)));
        list.add(new PetriSim(this.getFromCtoATransitionPart(trC)));

        list.get(1).getNet().getListP()[0] = list.get(0).getNet().getListP()[1]; // Generate => Factory A
        list.get(2).getNet().getListP()[0] = list.get(1).getNet().getListP()[1]; // Bus start join
        list.get(2).getNet().getListP()[3] = list.get(1).getNet().getListP()[5]; // Bus end join
        list.get(1).setPriority(1);

        list.get(4).getNet().getListP()[1] = list.get(1).getNet().getListP()[5]; // Bus end A => Bus start B

        list.get(4).getNet().getListP()[0] = list.get(3).getNet().getListP()[1]; // Generate => Factory B
        list.get(5).getNet().getListP()[0] = list.get(4).getNet().getListP()[1]; // Bus start join
        list.get(5).getNet().getListP()[3] = list.get(4).getNet().getListP()[5]; // Bus end join
        list.get(4).setPriority(1);

        // FromCtoATransitionPart
        list.get(6).getNet().getListP()[0] = list.get(4).getNet().getListP()[5];
        list.get(6).getNet().getListP()[1] = list.get(1).getNet().getListP()[1];

        final PetriObjModel model = new PetriObjModel(list);
        model.setIsProtokol(false);
        model.go(SIM_TIME);

        this.printResult(model);
    }

    public PetriNet getGenerateDetails(double generateTime, double deviation) throws ExceptionInvalidTimeDelay {
        final ArrayList<PetriP> places = new ArrayList<>();
        final ArrayList<PetriT> transitions = new ArrayList<>();
        final ArrayList<ArcIn> arcsIn = new ArrayList<>();
        final ArrayList<ArcOut> arcsOut = new ArrayList<>();

        // Places
        places.add(new PetriP("GenerateReady",1));
        places.add(new PetriP("Details"));

        // Transitions
        transitions.add(new PetriT("Generate"));
        transitions.get(0).setDistribution("norm", generateTime);
        transitions.get(0).setParamDeviation(deviation);

        // Arcs
        arcsIn.add(new ArcIn(places.get(0),transitions.get(0),1));
        arcsOut.add(new ArcOut(transitions.get(0),places.get(0),1));
        arcsOut.add(new ArcOut(transitions.get(0),places.get(1),1000));

        final PetriNet net = new PetriNet("GenerateNet",places,transitions,arcsIn,arcsOut);
        PetriP.initNext();
        PetriT.initNext();
        ArcIn.initNext();
        ArcOut.initNext();
        return net;
    }

    public PetriNet getLoadTransitionUnloadPart(double loadTime, double trTime, double unloadTime, int busStart) throws ExceptionInvalidTimeDelay {
        final ArrayList<PetriP> places = new ArrayList<>();
        final ArrayList<PetriT> transitions = new ArrayList<>();
        final ArrayList<ArcIn> arcsIn = new ArrayList<>();
        final ArrayList<ArcOut> arcsOut = new ArrayList<>();

        // Places
        places.add(new PetriP("Details"));
        places.add(new PetriP("Buses start", busStart));
        places.add(new PetriP("To Transition"));
        places.add(new PetriP("To Unload"));
        places.add(new PetriP("Details delivered"));
        places.add(new PetriP("Buses end"));


        // Transitions
        transitions.add(new PetriT("Load", loadTime));
        transitions.add(new PetriT("Transition", trTime));
        transitions.add(new PetriT("Unload", unloadTime));

        // Arcs
        arcsIn.add(new ArcIn(places.get(0),transitions.get(0),1000));
        arcsIn.add(new ArcIn(places.get(1),transitions.get(0),1));
        arcsOut.add(new ArcOut(transitions.get(0),places.get(2),1));
        arcsIn.add(new ArcIn(places.get(2),transitions.get(1),1));
        arcsOut.add(new ArcOut(transitions.get(1),places.get(3),1));
        arcsIn.add(new ArcIn(places.get(3),transitions.get(2),1));
        arcsOut.add(new ArcOut(transitions.get(2),places.get(4),1000));
        arcsOut.add(new ArcOut(transitions.get(2),places.get(5),1));

        final PetriNet net = new PetriNet("LoadTransitionUnloadPart", places,transitions,arcsIn,arcsOut);
        PetriP.initNext();
        PetriT.initNext();
        ArcIn.initNext();
        ArcOut.initNext();
        return net;
    }

    public PetriNet getEmptyTransitionPart(double trTime) throws ExceptionInvalidTimeDelay {
        final ArrayList<PetriP> places = new ArrayList<>();
        final ArrayList<PetriT> transitions = new ArrayList<>();
        final ArrayList<ArcIn> arcsIn = new ArrayList<>();
        final ArrayList<ArcOut> arcsOut = new ArrayList<>();

        // Places
        places.add(new PetriP("Buses start"));
        places.add(new PetriP("To Transition"));
        places.add(new PetriP("Declined"));
        places.add(new PetriP("Buses end"));


        // Transitions
        transitions.add(new PetriT("No Details"));
        transitions.add(new PetriT("Transition", trTime));

        // Arcs
        arcsIn.add(new ArcIn(places.get(0),transitions.get(0),1));
        arcsOut.add(new ArcOut(transitions.get(0),places.get(1),1));
        arcsOut.add(new ArcOut(transitions.get(0),places.get(2),1));
        arcsIn.add(new ArcIn(places.get(1),transitions.get(1),1));
        arcsOut.add(new ArcOut(transitions.get(1),places.get(3),1));

        final PetriNet net = new PetriNet("EmptyTransitionPart", places,transitions,arcsIn,arcsOut);
        PetriP.initNext();
        PetriT.initNext();
        ArcIn.initNext();
        ArcOut.initNext();
        return net;
    }

    public PetriNet getFromCtoATransitionPart(double trTime) throws ExceptionInvalidTimeDelay {
        final ArrayList<PetriP> places = new ArrayList<>();
        final ArrayList<PetriT> transitions = new ArrayList<>();
        final ArrayList<ArcIn> arcsIn = new ArrayList<>();
        final ArrayList<ArcOut> arcsOut = new ArrayList<>();

        // Places
        places.add(new PetriP("Buses start"));
        places.add(new PetriP("Buses end"));


        // Transitions
        transitions.add(new PetriT("Transition", trTime));

        // Arcs
        arcsIn.add(new ArcIn(places.get(0),transitions.get(0),1));
        arcsOut.add(new ArcOut(transitions.get(0),places.get(1),1));

        final PetriNet net = new PetriNet("FromCtoATransitionPart", places,transitions,arcsIn,arcsOut);
        PetriP.initNext();
        PetriT.initNext();
        ArcIn.initNext();
        ArcOut.initNext();
        return net;
    }

    public void printResult(PetriObjModel model) {
        for (PetriSim sim : model.getListObj()) {
            sim.printMark();
        }

        long deliveredASuccess = model.getListObj().get(1).getNet().getListP()[4].getMark() / 1000;
        long emptyRunsA = model.getListObj().get(2).getNet().getListP()[2].getMark();

        long deliveredBSuccess = model.getListObj().get(4).getNet().getListP()[4].getMark() / 1000;
        long emptyRunsB = model.getListObj().get(5).getNet().getListP()[2].getMark();

//        System.out.println("Delivered runs from A: " + deliveredASuccess);
//        System.out.println("Empty runs from A: " + emptyRunsA);
//        System.out.printf("Empty runs ratio from A to B: %.6f%n", (double) emptyRunsA / (deliveredASuccess + emptyRunsA));
        System.out.printf("%d\t%d\t%d\t%d", deliveredASuccess * 1000, deliveredBSuccess * 1000, emptyRunsA, emptyRunsB);
//        System.out.println("Delivered runs from B: " + deliveredBSuccess);
//        System.out.println("Empty runs from B: " + emptyRunsB);
//        System.out.printf("Empty runs ratio from B to C: %.6f%n", (double) emptyRunsB / (deliveredBSuccess + emptyRunsB));
    }
}
