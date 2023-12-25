import PetriObj.*;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

public class Task3Test {

    @Test
    void model() throws ExceptionInvalidTimeDelay {
        final ArrayList<PetriSim> list = new ArrayList<>();
        list.add(new PetriSim(getGenerate()));
        list.add(new PetriSim(getGenerate()));
        list.add(new PetriSim(getBusPart(1, 20.0)));
        list.add(new PetriSim(getBusPart(1, 30.0)));
        list.add(new PetriSim(getBusPart(0, 20.0)));
        list.add(new PetriSim(getBusPart(0, 30.0)));
        list.add(new PetriSim(getFinalizePart()));
        list.get(2).setPriority(1);
        list.get(4).setPriority(1);

        list.get(2).getNet().getListP()[0] = list.get(0).getNet().getListP()[3]; // Generate => Bus A
        list.get(3).getNet().getListP()[0] = list.get(0).getNet().getListP()[3]; // Generate => Bus B
        list.get(4).getNet().getListP()[0] = list.get(1).getNet().getListP()[3]; // Generate 2 => Bus A
        list.get(5).getNet().getListP()[0] = list.get(1).getNet().getListP()[3]; // Generate 2 => Bus B
        list.get(1).getNet().getListP()[2] = list.get(0).getNet().getListP()[2];
        list.get(4).getNet().getListP()[1] = list.get(2).getNet().getListP()[4];
        list.get(4).getNet().getListP()[4] = list.get(2).getNet().getListP()[1];
        list.get(5).getNet().getListP()[1] = list.get(3).getNet().getListP()[4];
        list.get(5).getNet().getListP()[4] = list.get(3).getNet().getListP()[1];
        list.get(2).getNet().getListP()[5] = list.get(6).getNet().getListP()[0];
        list.get(3).getNet().getListP()[5] = list.get(6).getNet().getListP()[0];
        list.get(4).getNet().getListP()[5] = list.get(6).getNet().getListP()[0];
        list.get(5).getNet().getListP()[5] = list.get(6).getNet().getListP()[0];

        final PetriObjModel model = new PetriObjModel(list);
        model.setIsProtokol(false);
        model.go(1000);

        model.getListObj().get(6).printMark();
    }

    public PetriNet getGenerate() throws ExceptionInvalidTimeDelay {
        final ArrayList<PetriP> places = new ArrayList<>();
        final ArrayList<PetriT> transitions = new ArrayList<>();
        final ArrayList<ArcIn> arcsIn = new ArrayList<>();
        final ArrayList<ArcOut> arcsOut = new ArrayList<>();

        // Places
        places.add(new PetriP("generateReady",1));
        places.add(new PetriP("generated"));
        places.add(new PetriP("declined"));
        places.add(new PetriP("queue"));

        // Transitions
        transitions.add(new PetriT("generate",0.5));
        transitions.get(0).setDistribution("norm", transitions.get(0).getTimeServ());
        transitions.get(0).setParamDeviation(0.2);
        transitions.add(new PetriT("change"));
        transitions.get(1).setPriority(1);
        transitions.add(new PetriT("toQueue"));

        // Arcs
        arcsIn.add(new ArcIn(places.get(0),transitions.get(0),1));
        arcsIn.add(new ArcIn(places.get(1),transitions.get(1),1));
        arcsIn.add(new ArcIn(places.get(1),transitions.get(2),1));
        arcsIn.add(new ArcIn(places.get(3),transitions.get(1),30));
        arcsIn.get(3).setInf(true);
        arcsOut.add(new ArcOut(transitions.get(0),places.get(0),1));
        arcsOut.add(new ArcOut(transitions.get(0),places.get(1),1));
        arcsOut.add(new ArcOut(transitions.get(1),places.get(2),1));
        arcsOut.add(new ArcOut(transitions.get(2),places.get(3),1));

        final PetriNet net = new PetriNet("Generate",places,transitions,arcsIn,arcsOut);
        PetriP.initNext();
        PetriT.initNext();
        ArcIn.initNext();
        ArcOut.initNext();
        return net;
    }

    public PetriNet getBusPart(int busPresent, double moveTime) throws ExceptionInvalidTimeDelay {
        final ArrayList<PetriP> places = new ArrayList<>();
        final ArrayList<PetriT> transitions = new ArrayList<>();
        final ArrayList<ArcIn> arcsIn = new ArrayList<>();
        final ArrayList<ArcOut> arcsOut = new ArrayList<>();

        // Places
        places.add(new PetriP("queueP"));
        places.add(new PetriP("busReadyP",busPresent));
        places.add(new PetriP("placesP"));
        places.add(new PetriP("busesArrivedP"));
        places.add(new PetriP("availableP"));
        places.add(new PetriP("tripsP"));

        // Transitions
        transitions.add(new PetriT("enterT"));
        transitions.get(0).setPriority(1);
        transitions.add(new PetriT("moveT",moveTime));
        transitions.get(1).setDistribution("norm", transitions.get(1).getTimeServ());
        transitions.get(1).setParamDeviation(5.0);
        transitions.add(new PetriT("exitT",5.0));
        transitions.get(2).setDistribution("unif", transitions.get(2).getTimeServ());
        transitions.get(2).setParamDeviation(1.0);

        // Arcs
        arcsIn.add(new ArcIn(places.get(0),transitions.get(0),1));
        arcsIn.add(new ArcIn(places.get(1),transitions.get(0),1));
        arcsIn.get(1).setInf(true);
        arcsIn.add(new ArcIn(places.get(2),transitions.get(1),20));
        arcsIn.add(new ArcIn(places.get(3),transitions.get(2),1));
        arcsIn.add(new ArcIn(places.get(1),transitions.get(1),1));
        arcsOut.add(new ArcOut(transitions.get(0),places.get(2),1));
        arcsOut.add(new ArcOut(transitions.get(1),places.get(3),1));
        arcsOut.add(new ArcOut(transitions.get(2),places.get(4),1));
        arcsOut.add(new ArcOut(transitions.get(2),places.get(5),1));

        final PetriNet net = new PetriNet("Bus",places,transitions,arcsIn,arcsOut);
        PetriP.initNext();
        PetriT.initNext();
        ArcIn.initNext();
        ArcOut.initNext();
        return net;
    }

    public PetriNet getFinalizePart() throws ExceptionInvalidTimeDelay {
        final ArrayList<PetriP> places = new ArrayList<>();
        final ArrayList<PetriT> transitions = new ArrayList<>();
        final ArrayList<ArcIn> arcsIn = new ArrayList<>();
        final ArrayList<ArcOut> arcsOut = new ArrayList<>();

        // Places
        places.add(new PetriP("tripsP"));
        places.add(new PetriP("arrivedP"));

        // Transitions
        transitions.add(new PetriT("clientsCoefT"));

        // Arcs
        arcsIn.add(new ArcIn(places.get(0),transitions.get(0),1));
        arcsOut.add(new ArcOut(transitions.get(0),places.get(1),20));

        final PetriNet net = new PetriNet("Final",places,transitions,arcsIn,arcsOut);
        PetriP.initNext();
        PetriT.initNext();
        ArcIn.initNext();
        ArcOut.initNext();
        return net;
    }
}
