package com.kpi.modeling.detailManufacturing;

import PetriObj.*;

import java.util.ArrayList;

public class DetailTransportModel {

    public static void main(String[] args) throws ExceptionInvalidTimeDelay {
        final DetailTransportModel model = new DetailTransportModel();
        model.runModel(1_000_000);
    }

    public void runModel(double simTime) throws ExceptionInvalidTimeDelay {
        final ArrayList<PetriSim> list = new ArrayList<>();
        list.add(new PetriSim(this.getGenerateDetails(3.0)));
        list.add(new PetriSim(this.getLoadTransitionUnloadPart(8)));
        list.add(new PetriSim(this.getEmptyTransitionPart()));
        list.add(new PetriSim(this.getGenerateDetails(5.0)));
        list.add(new PetriSim(this.getLoadTransitionUnloadPart(0)));
        list.add(new PetriSim(this.getEmptyTransitionPart()));
        list.add(new PetriSim(this.getFromCtoATransitionPart()));

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
        model.go(simTime);

        this.printResult(model);
    }

    public PetriNet getGenerateDetails(double deviation) throws ExceptionInvalidTimeDelay {
        final ArrayList<PetriP> places = new ArrayList<>();
        final ArrayList<PetriT> transitions = new ArrayList<>();
        final ArrayList<ArcIn> arcsIn = new ArrayList<>();
        final ArrayList<ArcOut> arcsOut = new ArrayList<>();

        // Places
        places.add(new PetriP("GenerateReady",1));
        places.add(new PetriP("Details"));

        // Transitions
        transitions.add(new PetriT("Generate"));
        transitions.get(0).setDistribution("norm", 20.0);
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

    public PetriNet getLoadTransitionUnloadPart(int busStart) throws ExceptionInvalidTimeDelay {
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
        transitions.add(new PetriT("Load", 20.0));
        transitions.add(new PetriT("Transition", 30.0));
        transitions.add(new PetriT("Unload", 20.0));

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

    public PetriNet getEmptyTransitionPart() throws ExceptionInvalidTimeDelay {
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
        transitions.add(new PetriT("Transition", 30.0));

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

    public PetriNet getFromCtoATransitionPart() throws ExceptionInvalidTimeDelay {
        final ArrayList<PetriP> places = new ArrayList<>();
        final ArrayList<PetriT> transitions = new ArrayList<>();
        final ArrayList<ArcIn> arcsIn = new ArrayList<>();
        final ArrayList<ArcOut> arcsOut = new ArrayList<>();

        // Places
        places.add(new PetriP("Buses start"));
        places.add(new PetriP("Buses end"));


        // Transitions
        transitions.add(new PetriT("Transition", 20.0));

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

        System.out.println("Delivered runs from A: " + deliveredASuccess);
        System.out.println("Empty runs from A: " + emptyRunsA);
        System.out.printf("Empty runs ratio from A to B: %.6f%n", (double) emptyRunsA / (deliveredASuccess + emptyRunsA));

        System.out.println("Delivered runs from B: " + deliveredBSuccess);
        System.out.println("Empty runs from B: " + emptyRunsB);
        System.out.printf("Empty runs ratio from B to C: %.6f%n", (double) emptyRunsB / (deliveredBSuccess + emptyRunsB));
    }
}
