import PetriObj.*;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

public class Task2Test {

    @Test
    void model() throws ExceptionInvalidTimeDelay {
        final ArrayList<PetriSim> list = new ArrayList<>();
        list.add(new PetriSim(getGenerate()));
        list.add(new PetriSim(getMovePart(6)));
        list.add(new PetriSim(getProcessPart(1)));
        list.add(new PetriSim(getMovePart(7)));
        list.add(new PetriSim(getProcessPart(2)));
        list.add(new PetriSim(getMovePart(5)));
        list.get(0).getNet().getListP()[1] = list.get(1).getNet().getListP()[4]; // Generate => Move
        list.get(1).getNet().getListP()[5] = list.get(2).getNet().getListP()[1]; // Move => Process
        list.get(2).getNet().getListP()[2] = list.get(3).getNet().getListP()[4]; // Process => Move
        list.get(3).getNet().getListP()[5] = list.get(4).getNet().getListP()[1]; // Move => Process
        list.get(4).getNet().getListP()[2] = list.get(5).getNet().getListP()[4]; // Process => Move

        final PetriObjModel model = new PetriObjModel(list);
        model.setIsProtokol(false);
        model.go(1000);

        model.getListObj().get(5).printMark();
    }

    public PetriNet getGenerate() throws ExceptionInvalidTimeDelay {
        final ArrayList<PetriP> places = new ArrayList<>();
        final ArrayList<PetriT> transitions = new ArrayList<>();
        final ArrayList<ArcIn> arcsIn = new ArrayList<>();
        final ArrayList<ArcOut> arcsOut = new ArrayList<>();

        // Places
        places.add(new PetriP("newP",1));
        places.add(new PetriP("detailsP"));

        // Transitions
        transitions.add(new PetriT("generateT",40.0));

        // Arcs
        arcsIn.add(new ArcIn(places.get(0),transitions.get(0),1));
        arcsOut.add(new ArcOut(transitions.get(0),places.get(0),1));
        arcsOut.add(new ArcOut(transitions.get(0),places.get(1),1));

        final PetriNet net = new PetriNet("Generate",places,transitions,arcsIn,arcsOut);
        PetriP.initNext();
        PetriT.initNext();
        ArcIn.initNext();
        ArcOut.initNext();
        return net;
    }

    public PetriNet getProcessPart(int type) throws ExceptionInvalidTimeDelay {
        final ArrayList<PetriP> places = new ArrayList<>();
        final ArrayList<PetriT> transitions = new ArrayList<>();
        final ArrayList<ArcIn> arcsIn = new ArrayList<>();
        final ArrayList<ArcOut> arcsOut = new ArrayList<>();

        // Places
        places.add(new PetriP("channelsP",3));
        places.add(new PetriP("detailsP"));
        places.add(new PetriP("processedP"));

        // Transitions
        if (type == 1) {
            transitions.add(new PetriT("processT",60.0));
            transitions.get(0).setDistribution("norm", transitions.get(0).getTimeServ());
            transitions.get(0).setParamDeviation(10.0);
        } else if (type == 2) {
            transitions.add(new PetriT("processT",100.0));
            transitions.get(0).setDistribution("exp", transitions.get(0).getTimeServ());
        } else {
            throw new IllegalArgumentException("Illegal type: " + type);
        }

        // Arcs
        arcsIn.add(new ArcIn(places.get(0),transitions.get(0),1));
        arcsIn.add(new ArcIn(places.get(1),transitions.get(0),1));
        arcsOut.add(new ArcOut(transitions.get(0),places.get(0),1));
        arcsOut.add(new ArcOut(transitions.get(0),places.get(2),1));


        final PetriNet net = new PetriNet("Process",places,transitions,arcsIn,arcsOut);
        PetriP.initNext();
        PetriT.initNext();
        ArcIn.initNext();
        ArcOut.initNext();
        return net;
    }

    public PetriNet getMovePart(double timeProcess) throws ExceptionInvalidTimeDelay {
        final ArrayList<PetriP> places = new ArrayList<>();
        final ArrayList<PetriT> transitions = new ArrayList<>();
        final ArrayList<ArcIn> arcsIn = new ArrayList<>();
        final ArrayList<ArcOut> arcsOut = new ArrayList<>();

        // Places
        places.add(new PetriP("robotReadyP",1));
        places.add(new PetriP("deliveredP"));
        places.add(new PetriP("pickedP"));
        places.add(new PetriP("movedP"));
        places.add(new PetriP("detailsP"));
        places.add(new PetriP("detailsDeliveredP"));

        // Transitions
        transitions.add(new PetriT("enterT",8.0));
        transitions.get(0).setDistribution("norm", transitions.get(0).getTimeServ());
        transitions.get(0).setParamDeviation(1.0);
        transitions.add(new PetriT("returnT",timeProcess));
        transitions.add(new PetriT("exitT",8.0));
        transitions.get(2).setDistribution("norm", transitions.get(2).getTimeServ());
        transitions.get(2).setParamDeviation(1.0);
        transitions.add(new PetriT("moveT",timeProcess));

        // Arcs
        arcsIn.add(new ArcIn(places.get(0),transitions.get(0),1));
        arcsIn.add(new ArcIn(places.get(1),transitions.get(1),1));
        arcsIn.add(new ArcIn(places.get(2),transitions.get(3),1));
        arcsIn.add(new ArcIn(places.get(3),transitions.get(2),1));
        arcsIn.add(new ArcIn(places.get(4),transitions.get(0),1));
        arcsOut.add(new ArcOut(transitions.get(1),places.get(0),1));
        arcsOut.add(new ArcOut(transitions.get(0),places.get(2),1));
        arcsOut.add(new ArcOut(transitions.get(2),places.get(1),1));
        arcsOut.add(new ArcOut(transitions.get(3),places.get(3),1));
        arcsOut.add(new ArcOut(transitions.get(2),places.get(5),1));


        final PetriNet net = new PetriNet("Move",places,transitions,arcsIn,arcsOut);
        PetriP.initNext();
        PetriT.initNext();
        ArcIn.initNext();
        ArcOut.initNext();
        return net;
    }
}
