package code.patches;

import code.ProjectEMod;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.megacrit.cardcrawl.core.OverlayMenu;

public class OverlayMenuUpdatePatch {
    @SpirePatch(clz = OverlayMenu.class, method = "update")
    public static class UpdateFix {
        @SpirePrefixPatch
        public static void Prefix(OverlayMenu __instance) {
            ProjectEMod.modifyPlayerRelics();
        }
    }
}