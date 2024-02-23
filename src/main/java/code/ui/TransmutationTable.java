package code.ui;

import basemod.TopPanelItem;
import basemod.abstracts.CustomSavable;
import basemod.patches.com.megacrit.cardcrawl.helpers.PotionLibrary.PotionHelperGetPotions;
import code.ProjectEMod;
import code.helpers.EMCData;
import code.util.ListItem;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.*;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.relics.AbstractRelic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.stream.Collectors;

public class TransmutationTable extends TopPanelItem implements CustomSavable<EMCData> {

    private static final Texture IMG = new Texture(ProjectEMod.makeImagePath("ui/TransmutationTable.png"));
    public static final String ID = ProjectEMod.makeID("transmutationtable");

    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(ProjectEMod.makeID("TransmutationTable"));
    public static final String[] TEXT = uiStrings.TEXT;

    private final ArrayList<PowerTip> tips;
    private static final ExchangeScreen exchangeScreen = new ExchangeScreen();

    public static final ArrayList<AbstractCard> savedCards;
    public static final HashSet<String> savedCardIDs;
    public static final ArrayList<AbstractRelic> savedRelics;
    public static final HashSet<String> savedRelicIDs;
    public static final ArrayList<AbstractPotion> savedPotions;
    public static final HashSet<String> savedPotionIDs;

    public static int PLAYER_EMC = 0;

    static {
        savedCards = new ArrayList<>();
        savedRelics = new ArrayList<>();
        savedPotions = new ArrayList<>();
        savedCardIDs = new HashSet<>();
        savedRelicIDs = new HashSet<>();
        savedPotionIDs = new HashSet<>();
    }

    public TransmutationTable() {
        super(IMG, ID);
        tips = new ArrayList<>();
        tips.add(new PowerTip(TEXT[0], TEXT[1]));
    }

    @Override
    protected void onClick() {
        if (!ExchangeScreen.show) exchangeScreen.open();
        else exchangeScreen.close();
    }

    @Override
    protected void onHover() {
        super.onHover();
        TipHelper.queuePowerTips(this.x, this.y - 64f,tips);
    }

    @Override
    public void update() {
        super.update();
        if(ExchangeScreen.show) exchangeScreen.update();
    }

    @Override
    public void render(SpriteBatch sb) {
        super.render(sb);
        this.renderEMC(sb);
        if(ExchangeScreen.show) exchangeScreen.render(sb);
    }

    public void renderEMC(SpriteBatch sb){
        FontHelper.renderFontLeft(sb, FontHelper.topPanelInfoFont, "EMC: " + PLAYER_EMC, this.x - 64.0f - FontHelper.getWidth(FontHelper.topPanelInfoFont,"EMC: " + PLAYER_EMC, Settings.scale), this.y + this.hb_h / 2f, Settings.CREAM_COLOR);
    }

    public static void renderScreen(SpriteBatch sb) {
//        if(ExchangeScreen.show) {
//            exchangeScreen.render(sb);
//        }
    }

    public static void updateScreen() {
//        if(ExchangeScreen.show) {
//            exchangeScreen.update();
//        }
    }

    public static int getCardEMC(AbstractCard card) {
        switch (card.rarity) {

            case BASIC:
                return 5;
            case SPECIAL:
                return 10;
            case COMMON:
                return 15;
            case UNCOMMON:
                return 30;
            case RARE:
                return 200;
            case CURSE:
                return 1;
            default:
                return 10;
        }
    }

    public static int getRelicEMC(AbstractRelic relic) {
        switch (relic.tier) {

            case DEPRECATED:
                return 1;
            case STARTER:
                return 100;
            case COMMON:
                return 150;
            case UNCOMMON:
                return 200;
            case RARE:
                return 300;
            case SPECIAL:
                return 250;
            case BOSS:
                return 500;
            case SHOP:
                return 250;
            default:
                return 100;
        }
    }

    public static int getPotionEMC(AbstractPotion potion) {
        switch (potion.rarity) {

            case PLACEHOLDER:
                return 10;
            case COMMON:
                return 20;
            case UNCOMMON:
                return 40;
            case RARE:
                return 60;
            default:
                return 15;
        }
    }

    public static <T> ArrayList<ListItem<T>> getList(ArrayList<T> items) {
        return items.stream().map(ListItem::new).collect(Collectors.toCollection(ArrayList::new));
    }

    public static <T> ArrayList<ListItem<T>> getList(HashSet<T> items) {
        return items.stream().map(ListItem::new).collect(Collectors.toCollection(ArrayList::new));
    }

    public static void resetLists() {
        savedCards.clear();
        savedRelics.clear();
        savedPotions.clear();
        savedCardIDs.clear();
        savedRelicIDs.clear();
        savedPotionIDs.clear();
    }

    @Override
    public EMCData onSave() {
        return new EMCData(savedCardIDs,savedRelicIDs,savedPotionIDs,PLAYER_EMC);
    }

    @Override
    public void onLoad(EMCData emcData) {
        resetLists();
        savedCardIDs.addAll(emcData.savedCardIDs);
        savedRelicIDs.addAll(emcData.savedRelicIDs);
        savedPotionIDs.addAll(emcData.savedPotionIDs);

        savedCardIDs.forEach(cID -> savedCards.add(CardLibrary.getCopy(cID)));
        savedRelicIDs.forEach(rID -> savedRelics.add(RelicLibrary.getRelic(rID).makeCopy()));
        savedPotionIDs.forEach(pID -> savedPotions.add(PotionHelper.getPotion(pID)));
    }
}
