package code.util;

import com.megacrit.cardcrawl.relics.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;

public class ExceptionRelicList {
    //for single use relics that does not get used up
    public static HashSet<String> singleUseList = new HashSet<>();
    //for things like bloom mark
    public static HashSet<String> noValueList = new HashSet<>();

    public static HashMap<String, Integer> perChargeList = new HashMap<>();

    static {
        singleUseList.add(CallingBell.ID);
        singleUseList.add(TinyHouse.ID);
        singleUseList.add(EmptyCage.ID);
        singleUseList.add(Orrery.ID);
        singleUseList.add(Cauldron.ID);
        singleUseList.add(DollysMirror.ID);
        singleUseList.add(PandorasBox.ID);
        singleUseList.add(WarPaint.ID);
        singleUseList.add(Whetstone.ID);
        singleUseList.add(Astrolabe.ID);

        noValueList.add(MarkOfTheBloom.ID);
        noValueList.add(NlothsMask.ID);
        noValueList.add(SpiritPoop.ID);

        perChargeList.put(NeowsLament.ID, 3);
    }


}
