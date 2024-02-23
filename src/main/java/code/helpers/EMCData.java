package code.helpers;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.relics.AbstractRelic;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;

public class EMCData implements Serializable {
    public HashSet<String> savedCardIDs;
    public HashSet<String> savedRelicIDs;
    public HashSet<String> savedPotionIDs;

    public int PLAYER_EMC = 0;

    public EMCData(HashSet<String> savedCardIDs, HashSet<String> savedRelicIDs, HashSet<String> savedPotionIDs, int PLAYER_EMC) {
        this.savedCardIDs = savedCardIDs;
        this.savedRelicIDs = savedRelicIDs;
        this.savedPotionIDs = savedPotionIDs;
        this.PLAYER_EMC = PLAYER_EMC;
    }
}
