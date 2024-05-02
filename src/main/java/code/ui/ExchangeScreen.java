package code.ui;


import basemod.ReflectionHacks;
import basemod.abstracts.CustomScreen;
import code.ProjectEMod;
import code.util.ListItem;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.Loader;
import com.evacipated.cardcrawl.modthespire.lib.SpireEnum;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.RelicLibrary;
import com.megacrit.cardcrawl.helpers.controller.CInputActionSet;
import com.megacrit.cardcrawl.helpers.input.InputAction;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.localization.TutorialStrings;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.potions.PotionSlot;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.TinyChest;
import com.megacrit.cardcrawl.ui.buttons.GridSelectConfirmButton;
import loadout.LoadoutMod;
import loadout.relics.AllInOneBag;
import loadout.screens.AbstractSelectScreen;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class ExchangeScreen extends CustomScreen implements HeaderButtonPlusListener{

    public static boolean show = false;
    private static final UIStrings gUiStrings = CardCrawlGame.languagePack.getUIString("GridCardSelectScreen");
    public static final String[] gTEXT = gUiStrings.TEXT;
    protected GridSelectConfirmButton confirmButton = new GridSelectConfirmButton(gTEXT[0]);

    public AbstractScreenPanel<?> currentPanel;

    public ArrayList<ListItem<AbstractCard>> trasmutableCards;
    public ArrayList<ListItem<AbstractCard>> trasmutableRelics;
    public ArrayList<ListItem<AbstractCard>> trasmutablePotions;

    public AbstractScreenPanel<AbstractCard> cardPanel;
    public AbstractScreenPanel<AbstractRelic> relicPanel;
    public AbstractScreenPanel<AbstractPotion> potionPanel;

    private final HeaderButtonPlus cardButton;
    private final HeaderButtonPlus relicButton;
    private final HeaderButtonPlus potionButton;

    private final HeaderButtonPlus[] buttons;

    private static UIStrings gameUIStrings = CardCrawlGame.languagePack.getUIString("RunHistoryScreen");
    public static String[] GAME_TEXT = gameUIStrings.TEXT;
    private static TutorialStrings tipStrings = CardCrawlGame.languagePack.getTutorialString("Potion Tip");
    public static String[] TIP_TEXT = tipStrings.LABEL;

    private float startX = 75.0f * Settings.scale;
    private float startY = Settings.HEIGHT - 300.0F * Settings.scale;
    private float spaceY = 50.0f * Settings.scale;

    private final InputAction tabKey;
    public TextPopup textPopup;

    public ExchangeScreen() {
        trasmutableCards = new ArrayList<>();
        trasmutableRelics = new ArrayList<>();
        trasmutablePotions = new ArrayList<>();
        float curX = startX;
        float curY = startY;
        cardButton = new HeaderButtonPlus(GAME_TEXT[9], curX, curY, this, true, ImageMaster.DECK_ICON);
        curY -= spaceY;
        relicButton = new HeaderButtonPlus(GAME_TEXT[10], curX, curY, this, true, RelicLibrary.getRelic(TinyChest.ID).img);
        curY -= spaceY;
        potionButton = new HeaderButtonPlus(TIP_TEXT[0], curX, curY, this, true, ImageMaster.POTION_BOTTLE_CONTAINER);
        curY -= spaceY;

        this.buttons = new HeaderButtonPlus[] {this.cardButton, this.relicButton, this.potionButton};

        this.tabKey = new InputAction(Input.Keys.TAB);
    }

    public static class Enum
    {
        @SpireEnum
        public static AbstractDungeon.CurrentScreen EXCHANGE_SCREEN;
    }

    @Override
    public AbstractDungeon.CurrentScreen curScreen()
    {
        return Enum.EXCHANGE_SCREEN;
    }

    @Override
    public void reopen() {

        if(cardPanel == null)
            cardPanel = new AbstractScreenPanel<AbstractCard>(
                    ()->TransmutationTable.getList(AbstractDungeon.player.masterDeck.group, true),
                    ()->TransmutationTable.getList(TransmutationTable.savedCards, false),
                    AbstractCard.RAW_W * Settings.scale, AbstractCard.RAW_H * Settings.scale, -50f);
        if(relicPanel == null)
            relicPanel = new AbstractScreenPanel<AbstractRelic>(
                    ()->TransmutationTable.getList(AbstractDungeon.player.relics, true),
                    ()->TransmutationTable.getList(TransmutationTable.savedRelics, false),
                    AbstractRelic.RAW_W * Settings.scale, AbstractRelic.RAW_W * Settings.scale, 0f);
        if(potionPanel == null)
            potionPanel = new AbstractScreenPanel<AbstractPotion>(
                    ()->TransmutationTable.getList((ArrayList<AbstractPotion>) AbstractDungeon.player.potions.stream().filter(p -> !p.ID.equals(PotionSlot.POTION_ID)).collect(Collectors.toCollection(ArrayList::new)), true),
                    ()->TransmutationTable.getList(TransmutationTable.savedPotions, false),
                    64f * Settings.scale, 64f * Settings.scale, 25f);

        currentPanel = cardPanel;


        AbstractDungeon.screen = curScreen();
        AbstractDungeon.isScreenUp = true;


        AbstractDungeon.overlayMenu.showBlackScreen(0.5f);

        if(Loader.isModLoaded("loadout")) AbstractSelectScreen.hideLoadoutRelics();

        show = true;

        confirmButton.isDisabled = false;
        confirmButton.show();

        currentPanel.open();
    }

    public boolean isTabbing() {
        return this.tabKey.isPressed();
    }

    public void open() {



        if (AbstractDungeon.screen != AbstractDungeon.CurrentScreen.NONE)
            AbstractDungeon.previousScreen = AbstractDungeon.screen;
        // Call reopen in this example because the basics of
        // setting the current screen are the same across both
        reopen();

    }

    public void close()
    {
        currentPanel.close();

        confirmButton.isDisabled = true;
        confirmButton.hide();

        show = false;

        genericScreenOverlayReset();
        if(Loader.isModLoaded("loadout")) {
            AllInOneBag.INSTANCE.showButton();
        }
    }

    public void render(SpriteBatch sb) {
        if(!show) return;

        if(textPopup != null && textPopup.shown) {
            textPopup.render(sb);
            return;
        }

        currentPanel.render(sb);

        for (HeaderButtonPlus b : this.buttons) b.render(sb);
        confirmButton.render(sb);
    }

    public void hide() {
        confirmButton.isDisabled = true;
        confirmButton.hide();
        show = false;
    }

    public void update() {

        if(textPopup != null && textPopup.shown) {
            textPopup.update();
            return;
        }




        confirmButton.update();

        if (confirmButton.hb.clicked) {
            CInputActionSet.select.unpress();
            confirmButton.hb.clicked = false;
            AbstractDungeon.closeCurrentScreen();
        }
        for (HeaderButtonPlus b : this.buttons) b.update();
        if (this.confirmButton.hb.hovered) return;
        currentPanel.update();

    }


    @Override
    public void didChangeOrder(HeaderButtonPlus button, boolean isAscending) {
        if(button == this.cardButton) {
            if(currentPanel != cardPanel) {
                currentPanel.close();
                currentPanel = cardPanel;
                currentPanel.open();
            }
        } else if(button == this.relicButton) {
            if(currentPanel != relicPanel) {
                currentPanel.close();
                currentPanel = relicPanel;
                currentPanel.open();
            }
        } else if(button == this.potionButton) {
            if(currentPanel != potionPanel) {
                currentPanel.close();
                currentPanel = potionPanel;
                currentPanel.open();
            }
        }
    }

    @Override
    public boolean allowOpenMap() {
        return true;
    }

    @Override
    public void openingSettings() {
        if(Loader.isModLoaded("loadout")) {
            AllInOneBag.INSTANCE.showButton();
        }
    }

    @Override
    public void openingMap() {
        AbstractDungeon.previousScreen = curScreen();
    }
}


