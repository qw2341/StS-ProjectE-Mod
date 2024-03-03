package code.util;

import basemod.BaseMod;
import code.ProjectEMod;
import com.badlogic.gdx.Gdx;
import com.evacipated.cardcrawl.modthespire.lib.SpireConfig;
import com.google.gson.reflect.TypeToken;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.helpers.RelicLibrary;
import com.megacrit.cardcrawl.localization.TutorialStrings;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import loadout.LoadoutMod;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import static basemod.abstracts.CustomSavable.saveFileGson;

public class ExceptionSaver {
    private final File cardFile;
    private final String cardFilePath;
    private final File relicFile;
    private final String relicFilePath;
    private final File potionFile;
    private final String potionFilePath;

    static Type saveType;

    public static HashMap<String, Integer> cardExceptions = new HashMap<>();
    public static HashMap<String, Integer> relicExceptions = new HashMap<>();
    public static HashMap<String, Integer> potionExceptions = new HashMap<>();

    public ExceptionSaver() throws IOException {
        this.cardFilePath = SpireConfig.makeFilePath("projectEMod","ExceptionCardList","json");
        this.cardFile = new File(this.cardFilePath);
        this.cardFile.createNewFile();

        this.relicFilePath = SpireConfig.makeFilePath("projectEMod","ExceptionRelicList","json");
        this.relicFile = new File(this.relicFilePath);
        this.relicFile.createNewFile();

        this.potionFilePath = SpireConfig.makeFilePath("projectEMod","ExceptionPotionList","json");
        this.potionFile = new File(this.potionFilePath);
        this.potionFile.createNewFile();

        this.saveType = new TypeToken<HashMap<String, Integer>>() { }.getType();
        this.load();

    }

    private void read(String filePath, HashMap<String, Integer> map) throws IOException {
        Reader reader = Files.newBufferedReader(Paths.get(filePath));
        map.clear();
        HashMap<String, Integer> result = saveFileGson.fromJson(reader, saveType);
        if(result != null) map.putAll(result);
        reader.close();
    }

    public void load() throws IOException {
        read(this.cardFilePath, cardExceptions);
        read(this.relicFilePath, relicExceptions);
        read(this.potionFilePath, potionExceptions);
    }

    private static void save(String filePath, HashMap<String, Integer> map) throws IOException {
        FileWriter fileWriter = new FileWriter(filePath);
        saveFileGson.toJson(map, saveType, fileWriter);
        fileWriter.flush();
        fileWriter.close();
    }

    public void save() throws IOException {
        save(this.cardFilePath, cardExceptions);
        save(this.relicFilePath, relicExceptions);
        save(this.potionFilePath, potionExceptions);
    }

    public void saveCards() throws IOException {
        save(this.cardFilePath, cardExceptions);
    }

    public void saveRelics() throws IOException {
        save(this.relicFilePath, relicExceptions);
    }

    public void savePotions() throws IOException {
        save(this.potionFilePath, potionExceptions);
    }

    public static HashMap<String, Integer> readList(String fileName) {
        String jsonPath = ProjectEMod.makePath("EMCvalues" + File.separator + fileName);
        return BaseMod.gson.fromJson(loadJson(jsonPath), saveType);
    }

    private static String loadJson(String jsonPath) {
        return Gdx.files.internal(jsonPath).readString(String.valueOf(StandardCharsets.UTF_8));
    }

    public static void printOutAllEMCs() throws IOException {
        String cPath = SpireConfig.makeFilePath("projectEMod","AllCardsList","json");
        File cards = new File(cPath);
        cards.createNewFile();
        LinkedHashMap<String, Integer> cEMCs = new LinkedHashMap<>();

        ArrayList<AbstractCard> cards1 = CardLibrary.getAllCards();
        Comparator<AbstractCard> BY_COLOR = Comparator.comparing(c -> c.color);
        Comparator<AbstractCard> BY_Rarity = Comparator.comparing(c -> c.rarity);
        Comparator<AbstractCard> BY_NAME = Comparator.comparing(c-> c.name);

        cards1.sort(BY_COLOR.thenComparing(BY_Rarity.thenComparing(BY_NAME)));

        cards1.forEach(c -> cEMCs.put(c.cardID, -1));

        save(cPath, cEMCs);

        String rPath = SpireConfig.makeFilePath("projectEMod","AllRelicsList","json");
        File relics = new File(rPath);
        relics.createNewFile();
        LinkedHashMap<String, Integer> rEMCs = new LinkedHashMap<>();

        ArrayList<AbstractRelic> relics1 = LoadoutMod.relicsToDisplay;
        Comparator<AbstractRelic> BY_Rarityr = Comparator.comparing(c -> c.tier);
        Comparator<AbstractRelic> BY_NAMEr = Comparator.comparing(c-> c.name);

        relics1.sort(BY_Rarityr.thenComparing(BY_NAMEr));

        relics1.forEach(c -> rEMCs.put(c.relicId, -1));

        save(rPath, rEMCs);

        String pPath = SpireConfig.makeFilePath("projectEMod","AllPotionsList","json");
        File potions = new File(pPath);
        potions.createNewFile();
        LinkedHashMap<String, Integer> pEMCs = new LinkedHashMap<>();

        ArrayList<AbstractPotion> potions1 = LoadoutMod.potionsToDisplay;
        Comparator<AbstractPotion> BY_Rarityp = Comparator.comparing(c -> c.rarity);
        Comparator<AbstractPotion> BY_NAMEp = Comparator.comparing(c-> c.name);

        potions1.sort(BY_Rarityp.thenComparing(BY_NAMEp));

        potions1.forEach(c -> pEMCs.put(c.ID, -1));

        save(pPath, pEMCs);

        ProjectEMod.logger.info("Printed out all EMCs!");
    }
}
