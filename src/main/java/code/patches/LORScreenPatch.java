package code.patches;

import code.ProjectEMod;
import code.ui.ExchangeScreen;
import code.ui.TransmutationTable;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import lor.Utils;

import java.util.ArrayList;

@SpirePatch(clz = Utils.class, method = "openCardRewardsScreen", paramtypez = {ArrayList.class, boolean.class, int.class, String.class}, requiredModId = "Library of Ruina", optional = true)
public class LORScreenPatch {
    public static void Prefix(ArrayList<AbstractCard> cards, boolean allowSkip, int appearnum, String text) {
        if(ExchangeScreen.show) TransmutationTable.exchangeScreen.close();
        ProjectEMod.isScreenUp = true;
    }

}
