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

    public static HashMap<String, Integer> specialList = new HashMap<>();

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
        singleUseList.add(Waffle.ID);
        singleUseList.add(OldCoin.ID);
        singleUseList.add(PotionBelt.ID);
        singleUseList.add(Strawberry.ID);
        singleUseList.add(Pear.ID);
        singleUseList.add(Mango.ID);

        noValueList.add(MarkOfTheBloom.ID);
        noValueList.add(NlothsMask.ID);
        noValueList.add(SpiritPoop.ID);
        noValueList.add(GremlinMask.ID);

        perChargeList.put(NeowsLament.ID, 3);
        perChargeList.put(Matryoshka.ID, 2);
        perChargeList.put(Omamori.ID, 2);
        perChargeList.put(WingBoots.ID, 3);
        perChargeList.put(Circlet.ID, 1);

        specialList.put("loadout:AllInOneBag",Integer.MAX_VALUE);
    }


}
