package de.areto.datachef.comparators;

import de.areto.datachef.model.datavault.DVObject;

import java.util.Comparator;

public class DVObjectLoadComparator implements Comparator<DVObject> {

    @Override
    public int compare(DVObject o1, DVObject o2) {
        int sort1 = getOrderNumber(o1);
        int sort2 = getOrderNumber(o2);

        if (sort1 == sort2) {
            return o1.getName().compareTo(o2.getName());
        } else {
            return Integer.compare(sort1, sort2);
        }
    }

    // Order: Hub -> Link -> Satellite

    private static int getOrderNumber(DVObject o) {
        if (o.getType().equals(DVObject.Type.HUB)) {
            return 1;
        } else if (o.getType().equals(DVObject.Type.SAT)) {
            return 3;
        }
        return 2; //o.getType().equals(ObjectType.LNK)
    }

}
