package code.patches;

import code.ui.ExchangeScreen;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.ui.panels.PotionPopUp;
import com.megacrit.cardcrawl.ui.panels.TopPanel;

@SpirePatch2(clz = TopPanel.class, method = "updatePotions")
public class PotionPopUpPatch {
    @SpirePrefixPatch
    public static SpireReturn<Void> Prefix(TopPanel __instance) {
        return ExchangeScreen.show ? SpireReturn.Return() : SpireReturn.Continue();
    }
}
