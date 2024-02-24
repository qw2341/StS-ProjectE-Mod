package code.patches;

import code.ProjectEMod;
import code.ui.ExchangeScreen;
import code.ui.TransmutationTable;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.screens.select.GridCardSelectScreen;

public class ScreensPatch {
    @SpirePatch(clz = GridCardSelectScreen.class, method = "callOnOpen")
    public static class GridScreenOpenBool {
        @SpirePrefixPatch
        public static void Prefix() {
            if(ExchangeScreen.show) TransmutationTable.exchangeScreen.close();
            ProjectEMod.isScreenUp = true;

        }
    }

    @SpirePatch(clz = AbstractDungeon.class, method = "closeCurrentScreen")
    public static class GridScreenCloseBool {
        @SpirePrefixPatch
        public static void Prefix() {
            ProjectEMod.isScreenUp = false;
        }
    }
}
