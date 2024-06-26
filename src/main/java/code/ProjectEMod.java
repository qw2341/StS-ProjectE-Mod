package code;

import basemod.AutoAdd;
import basemod.BaseMod;
import basemod.devcommands.ConsoleCommand;
import basemod.helpers.RelicType;
import basemod.interfaces.*;
import code.commands.EMCCommand;
import code.ui.ExchangeScreen;
import code.ui.ModSettings;
import code.ui.TransmutationTable;
import code.util.ExceptionSaver;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.localization.RelicStrings;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.relics.*;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import code.relics.AbstractEasyRelic;
import code.util.ProAudio;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@SuppressWarnings({"unused", "WeakerAccess"})
@SpireInitializer
public class ProjectEMod implements
        EditRelicsSubscriber,
        EditStringsSubscriber,
        AddAudioSubscriber, PostInitializeSubscriber, PostRenderSubscriber, PreUpdateSubscriber, PostDungeonInitializeSubscriber {

    public static Logger logger = LogManager.getLogger(ProjectEMod.class.getName());
    public static HashSet<Integer> relicsToRemove = new HashSet<>();
    public static LinkedList<AbstractRelic> relicsToAdd = new LinkedList<>();

    public static HashSet<String> screenRelics = new HashSet<>();

    static {
        screenRelics.add(BottledFlame.ID);
        screenRelics.add(BottledLightning.ID);
        screenRelics.add(BottledTornado.ID);
        screenRelics.add(TinyHouse.ID);
        screenRelics.add(EmptyCage.ID);
        screenRelics.add(Orrery.ID);
        screenRelics.add(Cauldron.ID);
        screenRelics.add(DollysMirror.ID);
    }


    public static ExceptionSaver exceptionSaver;



    public static final String modID = "projecte";

    public static String makeID(String idText) {
        return modID + ":" + idText;
    }



    public static Settings.GameLanguage[] SupportedLanguages = {
            Settings.GameLanguage.ENG,
            Settings.GameLanguage.ZHS
    };

    private String getLangString() {
        for (Settings.GameLanguage lang : SupportedLanguages) {
            if (lang.equals(Settings.language)) {
                return Settings.language.name().toLowerCase();
            }
        }
        return "eng";
    }

    public ProjectEMod() {
        BaseMod.subscribe(this);

        ModSettings.loadSettings();
        try {
            exceptionSaver = new ExceptionSaver();
        } catch (IOException e) {
            logger.info("Error loading exception lists");
        }
    }

    public static String makePath(String resourcePath) {
        return modID + "Resources/" + resourcePath;
    }

    public static String makeImagePath(String resourcePath) {
        return modID + "Resources/images/" + resourcePath;
    }

    public static String makeRelicPath(String resourcePath) {
        return modID + "Resources/images/relics/" + resourcePath;
    }

    public static String makePowerPath(String resourcePath) {
        return modID + "Resources/images/powers/" + resourcePath;
    }

    public static String makeCharacterPath(String resourcePath)
    {
        return modID + "Resources/images/char/" + resourcePath;
    }

    public static String makeCardPath(String resourcePath) {
        return modID + "Resources/images/cards/" + resourcePath;
    }

    public static void initialize() {
        ProjectEMod thismod = new ProjectEMod();
    }


    @Override
    public void receiveEditRelics() {
        new AutoAdd(modID)
                .packageFilter(AbstractEasyRelic.class)
                .any(AbstractEasyRelic.class, (info, relic) -> {
                    if (relic.color == null) {
                        BaseMod.addRelic(relic, RelicType.SHARED);
                    } else {
                        BaseMod.addRelicToCustomPool(relic, relic.color);
                    }
                    if (!info.seen) {
                        UnlockTracker.markRelicAsSeen(relic.relicId);
                    }
                });
    }


    @Override
    public void receiveEditStrings() {
        BaseMod.loadCustomStringsFile(RelicStrings.class, modID + "Resources/localization/" + getLangString() + "/Relicstrings.json");
        BaseMod.loadCustomStringsFile(UIStrings.class, modID + "Resources/localization/" + getLangString() + "/UIstrings.json");
    }

    @Override
    public void receiveAddAudio() {
        for (ProAudio a : ProAudio.values())
            BaseMod.addAudio(makeID(a.name()), makePath("audio/" + a.name().toLowerCase() + ".ogg"));
    }

    @Override
    public void receivePostInitialize() {
        TransmutationTable tt = new TransmutationTable();
        BaseMod.addTopPanelItem(tt);
        BaseMod.addSaveField(makeID("emcsave"), tt);

        ConsoleCommand.addCommand("emc", EMCCommand.class);

        ModSettings.initModPanel();

        BaseMod.addCustomScreen(TransmutationTable.exchangeScreen);
//        try {
//            ExceptionSaver.printOutAllEMCs();
//        } catch (IOException e) {
//            logger.info("Failed to print out all emcs!");
//        }
    }

    @Override
    public void receivePostRender(SpriteBatch sb) {

    }

    @Override
    public void receivePreUpdate() {
        if(AbstractDungeon.isScreenUp && AbstractDungeon.screen == ExchangeScreen.Enum.EXCHANGE_SCREEN) {
            if (InputHelper.pressedEscape) {
                AbstractDungeon.closeCurrentScreen();
                InputHelper.pressedEscape = false;
            }
        }
    }

    public static void modifyPlayerRelics() {
        if(relicsToRemove.size()>0) {

            ArrayList<AbstractRelic> playerRelics = AbstractDungeon.player.relics;
            relicsToRemove.forEach(i->playerRelics.get(i).onUnequip());
            AbstractDungeon.player.relics = IntStream.range(0, playerRelics.size())
                    .filter(i -> !relicsToRemove.contains(i))
                    .mapToObj(playerRelics::get)
                    .collect(Collectors.toCollection(ArrayList::new));
            AbstractDungeon.player.reorganizeRelics();

            AbstractRelic.relicPage = 0;
            AbstractDungeon.topPanel.adjustRelicHbs();

            relicsToRemove.clear();
            TransmutationTable.exchangeScreen.currentPanel.onChangeEMC();
        }
        if(relicsToAdd.size()>0){
            for (AbstractRelic r : relicsToAdd) {
                AbstractDungeon.getCurrRoom().spawnRelicAndObtain((float)Settings.WIDTH / 2.0F, (float)Settings.HEIGHT / 2.0F, r);
            }
            relicsToAdd.clear();
            TransmutationTable.exchangeScreen.currentPanel.onChangeEMC();
        }
    }

    @Override
    public void receivePostDungeonInitialize() {
        TransmutationTable.resetLists();
    }
}
