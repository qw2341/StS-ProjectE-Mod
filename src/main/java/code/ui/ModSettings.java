package code.ui;

import basemod.BaseMod;
import basemod.ModLabeledToggleButton;
import basemod.ModMinMaxSlider;
import basemod.ModPanel;
import code.ProjectEMod;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.evacipated.cardcrawl.modthespire.lib.SpireConfig;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.localization.UIStrings;

import java.io.IOException;
import java.util.Properties;

public class ModSettings {
    public static final String BADGE_IMAGE = ProjectEMod.makeImagePath("Badge.png");

    public static Properties theDefaultDefaultSettings = new Properties();
    public static SpireConfig config = null;

    private final static String CURSE_REMOVE_MULT_KEY = "curseRemoveMult";
    public static float CURSE_REMOVE_MULT = -1.0f;

    private final static String CURSE_OBTAIN_DISC_RATE_KEY = "curseObtainDiscount";
    public static float CURSE_OBTAIN_DISCOUNT_RATE = 0.25f;

    private final static String ENABLE_EXPLOIT_PREVENTION_KEY = "enableExploitPrevention";
    public static boolean ENABLE_EXPLOIT_PREVENTION = true;

    private final static String TO_EMC_MULT_KEY = "toEMCMult";
    public static float TO_EMC_MULT = 1.0f;
    private final static String TO_ITEM_MULT_KEY = "toItemMult";
    public static float TO_ITEM_MULT = 1.0f;

    private final static String TREAT_BASIC_AS_CURSE_KEY = "treatBasicAsCurse";
    public static boolean TREAT_BASIC_AS_CURSE = false;

    public static void loadSettings() {
        theDefaultDefaultSettings.setProperty(CURSE_REMOVE_MULT_KEY, String.valueOf(-1.0f));
        theDefaultDefaultSettings.setProperty(CURSE_OBTAIN_DISC_RATE_KEY, String.valueOf(0.25f));
        theDefaultDefaultSettings.setProperty(ENABLE_EXPLOIT_PREVENTION_KEY, "TRUE");
        theDefaultDefaultSettings.setProperty(TO_EMC_MULT_KEY, String.valueOf(1.0f));
        theDefaultDefaultSettings.setProperty(TO_ITEM_MULT_KEY, String.valueOf(1.0f));
        theDefaultDefaultSettings.setProperty(TREAT_BASIC_AS_CURSE_KEY, "FALSE");

        try{
            config = new SpireConfig("projectEMod", "projecteConfig", theDefaultDefaultSettings);
            config.load();

            CURSE_REMOVE_MULT = config.getFloat(CURSE_REMOVE_MULT_KEY);
            CURSE_OBTAIN_DISCOUNT_RATE = config.getFloat(CURSE_OBTAIN_DISC_RATE_KEY);
            ENABLE_EXPLOIT_PREVENTION = config.getBool(ENABLE_EXPLOIT_PREVENTION_KEY);
            TO_EMC_MULT = config.getFloat(TO_EMC_MULT_KEY);
            TO_ITEM_MULT = config.getFloat(TO_ITEM_MULT_KEY);
            TREAT_BASIC_AS_CURSE = config.getBool(TREAT_BASIC_AS_CURSE_KEY);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void initModPanel() {

        // Load the Mod Badge
        Texture badgeTexture = new Texture(Gdx.files.internal(BADGE_IMAGE));

        ModPanel settingsPanel = new ModPanel();

        float startingXPos = 350.0f;
        float settingXPos = startingXPos;
        float xSpacing = 250.0f;
        float settingYPos = 750.0f;
        float lineSpacing = 50.0f;

        UIStrings UIStrings = CardCrawlGame.languagePack.getUIString(ProjectEMod.makeID("OptionsMenu"));
        String[] SettingText = UIStrings.TEXT;


        ModLabeledToggleButton exploitPreventionButton = new ModLabeledToggleButton(SettingText[0],SettingText[1],
                settingXPos, settingYPos, Settings.CREAM_COLOR, FontHelper.charDescFont, // Position (trial and error it), color, font
                ENABLE_EXPLOIT_PREVENTION, // Boolean it uses
                settingsPanel, (label) -> {},
                (button) -> { // The actual button:
                    ENABLE_EXPLOIT_PREVENTION = button.enabled; // The boolean true/false will be whether the button is enabled or not
                    try {
                        // And based on that boolean, set the settings and save them
                        config.setBool(ENABLE_EXPLOIT_PREVENTION_KEY, ENABLE_EXPLOIT_PREVENTION);
                        config.save();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });

        settingsPanel.addUIElement(exploitPreventionButton); // Add the button to the settings panel. Button is a go.

        settingXPos = startingXPos + 200.0f * Settings.scale;
        settingYPos -= 1.5 * lineSpacing;

        ModMinMaxSlider curseRmMult = new ModMinMaxSlider(SettingText[2],settingXPos, settingYPos,
                -1, 1, CURSE_REMOVE_MULT, "%.0f", settingsPanel, slider -> {
            //on change
            CURSE_REMOVE_MULT = Math.round(slider.getValue());
            try {
                // And based on that boolean, set the settings and save them
                config.setFloat(CURSE_REMOVE_MULT_KEY, CURSE_REMOVE_MULT);
                config.save();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        settingsPanel.addUIElement(curseRmMult);
        settingXPos = startingXPos + 200.0f * Settings.scale;
        settingYPos -= 1.5 * lineSpacing;

        ModMinMaxSlider curseObDisc = new ModMinMaxSlider(SettingText[3],settingXPos, settingYPos,
                -1, 1, CURSE_OBTAIN_DISCOUNT_RATE, "%.2f", settingsPanel, slider -> {
            //on change
            CURSE_OBTAIN_DISCOUNT_RATE = slider.getValue();
            try {
                // And based on that boolean, set the settings and save them
                config.setFloat(CURSE_OBTAIN_DISC_RATE_KEY, CURSE_OBTAIN_DISCOUNT_RATE);
                config.save();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        settingsPanel.addUIElement(curseObDisc);
        settingXPos = startingXPos + 200.0f * Settings.scale;
        settingYPos -= 1.5 * lineSpacing;

        ModMinMaxSlider emcMt = new ModMinMaxSlider(SettingText[4],settingXPos, settingYPos,
                0, 2, TO_EMC_MULT, "%.2f", settingsPanel, slider -> {
            //on change
            TO_EMC_MULT = slider.getValue();
            try {
                // And based on that boolean, set the settings and save them
                config.setFloat(TO_EMC_MULT_KEY, TO_EMC_MULT);
                config.save();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        settingsPanel.addUIElement(emcMt);
        settingXPos = startingXPos + 200.0f * Settings.scale;
        settingYPos -= 1.5 * lineSpacing;
        ModMinMaxSlider itemMt = new ModMinMaxSlider(SettingText[5],settingXPos, settingYPos,
                0, 2, TO_ITEM_MULT, "%.2f", settingsPanel, slider -> {
            //on change
            TO_ITEM_MULT = slider.getValue();
            try {
                // And based on that boolean, set the settings and save them
                config.setFloat(TO_ITEM_MULT_KEY, TO_ITEM_MULT);
                config.save();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        settingsPanel.addUIElement(itemMt);
        settingXPos = startingXPos;
        settingYPos -= lineSpacing;

        ModLabeledToggleButton basicCardButton = new ModLabeledToggleButton(SettingText[6],SettingText[7],
                settingXPos, settingYPos, Settings.CREAM_COLOR, FontHelper.charDescFont, // Position (trial and error it), color, font
                TREAT_BASIC_AS_CURSE, // Boolean it uses
                settingsPanel, (label) -> {},
                (button) -> { // The actual button:
                    TREAT_BASIC_AS_CURSE = button.enabled; // The boolean true/false will be whether the button is enabled or not
                    try {
                        // And based on that boolean, set the settings and save them
                        config.setBool(TREAT_BASIC_AS_CURSE_KEY, TREAT_BASIC_AS_CURSE);
                        config.save();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });

        settingsPanel.addUIElement(basicCardButton); // Add the button to the settings panel. Button is a go.

        settingYPos -= lineSpacing;

        BaseMod.registerModBadge(badgeTexture, "ProjectE", "Jasonwqq", "A minecraft projecte experience", settingsPanel);
    }
}
