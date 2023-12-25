import PetriObj.ExceptionInvalidTimeDelay;
import PetriObj.PetriObjModel;
import PetriObj.PetriSim;
import com.kpi.modeling.detailManufacturing.DetailTransportModel;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

public class GenerateTest {

    @Test
    void t() throws ExceptionInvalidTimeDelay {
        final DetailTransportModel m = new DetailTransportModel();

        final PetriSim sim = new PetriSim(m.getGenerateDetails(3.0));

        final ArrayList<PetriSim> items = new ArrayList<>();
        items.add(sim);

        final PetriObjModel model = new PetriObjModel(items);
        model.setIsProtokol(false);
        model.go(10000);

        model.getListObj().get(0).printMark();
    }
}
