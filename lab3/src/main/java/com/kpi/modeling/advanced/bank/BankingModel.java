package com.kpi.modeling.advanced.bank;

import com.kpi.modeling.Model;
import com.kpi.modeling.model.Process;
import com.kpi.modeling.model.*;

import java.util.Collection;
import java.util.List;

public class BankingModel extends Model {

    @Override
    protected void init() {
        final Distribution processDist = new Distribution(Distribution.DistEnum.NORMAL, 1.0, 0.3);
        final Collection<BaseItem> schema = getSchema();
        for (BaseItem item : schema) {
            if (item instanceof Process p) {
                Event e1 = new Event(Double.MAX_VALUE);
                e1.setHeldBy(p);
                final Distribution old = p.getDistribution();
                p.setDistribution(processDist);
                p.handleAccept(e1);
                p.setDistribution(old);

                Event e2 = new Event(Double.MAX_VALUE);
                e2.setHeldBy(p);
                Event e3 = new Event(Double.MAX_VALUE);
                e3.setHeldBy(p);
                p.getEventQueue().add(e2);
                p.getEventQueue().add(e3);
                this.getActiveEvents().addAll(List.of(e1, e2, e3));
            } else if (item instanceof Create c) {
                Event newEvent = c.populateEvent();
                newEvent.setStartTime(newEvent.getStartTime() + 0.1);
            }
        }
    }
}
