package code.util;

import basemod.devcommands.deck.DeckManipulator;
import code.ProjectEMod;
import code.ui.TransmutationTable;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.actions.unique.AddCardToDeckAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.potions.PotionSlot;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;

public class ListItem<T> {

    public float width, height;

    public T item;
    public Hitbox hb;

    public boolean shouldRender = true;

    public String id;
    public String name;
    public String desc;

    public int emc;

    public boolean isOwnedByPlayer = false;

    public ListItem(T item) {
        if (item instanceof AbstractCard) {
            AbstractCard i = ((AbstractCard) item);
            this.id = i.cardID;
            this.name = i.name;
            this.desc = "";
            this.width = i.hb.width;
            this.height = i.hb.height;
            this.hb = i.hb;
            this.emc = TransmutationTable.getCardEMC(i);
            this.item = (T) i;
        } else if (item instanceof AbstractRelic) {
            AbstractRelic i = ((AbstractRelic) item).makeCopy();
            this.id = i.relicId;
            this.name = i.name;
            this.desc = i.description;
            this.width = i.hb.width;
            this.height = i.hb.height;
            this.hb = i.hb;
            this.emc = TransmutationTable.getRelicEMC(i);
            i.isSeen = true;
            this.item = (T) i;
            ((AbstractRelic)this.item).counter = ((AbstractRelic) item).counter;
        } else if (item instanceof AbstractPotion) {
            AbstractPotion i = ((AbstractPotion) item);
            this.id = i.ID;
            this.name = i.name;
            this.desc = i.description;
            this.width = i.hb.width;
            this.height = i.hb.height;
            this.hb = i.hb;
            this.emc = TransmutationTable.getPotionEMC(i);
            this.item = (T) i;
        }


    }

    public void render(SpriteBatch sb) {
        // Conditional rendering based on item type
        if (item instanceof AbstractCard) {
            if(this.hb.hovered) {
                ((AbstractCard) item).renderInLibrary(sb);
                ((AbstractCard) item).renderCardTip(sb);
            } else {
                ((AbstractCard) item).renderInLibrary(sb);
            }
        } else if (item instanceof AbstractRelic) {
            this.hb.render(sb);
            ((AbstractRelic) item).render(sb, false, Color.BLACK);
        } else if (item instanceof AbstractPotion) {
            ((AbstractPotion) item).labRender(sb);
        }
        FontHelper.cardEnergyFont_L.getData().setScale(0.5f);
        FontHelper.renderFontCentered(sb,FontHelper.cardEnergyFont_L,"EMC " + getEmc(), this.hb.cX, this.hb.cY - this.height / 2f, Settings.CREAM_COLOR);
    }

    public void update() {
        if (item instanceof AbstractCard) {
            ((AbstractCard) item).updateHoverLogic();
            ((AbstractCard) item).update();
        } else if (item instanceof AbstractRelic) {
            this.hb.update();
            ((AbstractRelic) item).update();
        } else if (item instanceof AbstractPotion) {
            ((AbstractPotion) item).update();
            this.hb.update();
        }
    }

    public void setPos(float x, float y) {
        if (item instanceof AbstractCard) {
            ((AbstractCard) item).target_x = x;
            ((AbstractCard) item).target_y = y;
        } else if (item instanceof AbstractRelic) {
            ((AbstractRelic) item).targetX = x;
            ((AbstractRelic) item).targetY = y;
            ((AbstractRelic) item).currentX = x;
            ((AbstractRelic) item).currentY = y;
            ((AbstractRelic) item).hb.move(x,y);
            this.hb.move(x,y);
        } else if (item instanceof AbstractPotion) {
            ((AbstractPotion) item).move(x,y);
            ((AbstractPotion) item).hb.move(x,y);
        }
    }

    public int getEmc() {
        if (isOwnedByPlayer) {
            if (item instanceof AbstractCard) {
                return this.emc;
            } else if (item instanceof AbstractRelic) {
                String relicId = ((AbstractRelic) item).relicId;
                if(ExceptionRelicList.perChargeList.containsKey(relicId)){
                    return (int) Math.round(((float) this.emc / (float) ExceptionRelicList.perChargeList.get(relicId)) * (float) ((AbstractRelic) item).counter);
                }
                return this.emc *
                        (((AbstractRelic) item).usedUp ||
                                ExceptionRelicList.singleUseList.contains(relicId) ||
                                ExceptionRelicList.noValueList.contains(relicId)
                                ? 0 : 1);
            } else if (item instanceof AbstractPotion) {
                return this.emc;
            }
        } else {
            if (item instanceof AbstractCard) {
                if (((AbstractCard) item).type == AbstractCard.CardType.CURSE || ((AbstractCard) item).type == AbstractCard.CardType.STATUS) return (int) (this.emc * ProjectEMod.CURSE_OBTAIN_DISCOUNT_RATE);
            }
        }

        return this.emc;
    }

    public void toEMC() {
        //remember


        //remove it from player
        if (item instanceof AbstractCard) {
            AbstractCard card = (AbstractCard) item;
            if(card.type == AbstractCard.CardType.CURSE || card.type == AbstractCard.CardType.STATUS) {
                if(TransmutationTable.PLAYER_EMC < this.emc) return;
            }

            AbstractDungeon.player.masterDeck.removeCard(card);
            card.onRemoveFromMasterDeck();
            if(!TransmutationTable.savedCardIDs.contains(card.cardID)) {
                TransmutationTable.savedCardIDs.add(card.cardID);
                TransmutationTable.savedCards.add(card.makeCopy());
            }

        } else if (item instanceof AbstractRelic) {
//            ProjectEMod.relicsToRemove.add(TransmutationTable..indexOf(item));
            if(!TransmutationTable.savedRelicIDs.contains(((AbstractRelic) item).relicId)) {
                TransmutationTable.savedRelicIDs.add(((AbstractRelic) item).relicId);
                TransmutationTable.savedRelics.add(((AbstractRelic) item).makeCopy());
            }
        } else if (item instanceof AbstractPotion) {
            AbstractDungeon.player.removePotion((AbstractPotion) item);
            if(!TransmutationTable.savedPotionIDs.contains(((AbstractPotion) item).ID)) {
                TransmutationTable.savedPotionIDs.add(((AbstractPotion) item).ID);
                TransmutationTable.savedPotions.add(((AbstractPotion) item).makeCopy());
            }
        }

        //add emc
        TransmutationTable.PLAYER_EMC += getEmc();
    }

    public void toPlayer() {
        if(TransmutationTable.PLAYER_EMC >= this.emc) {
            //give to player
            if (item instanceof AbstractCard) {
                AbstractCard card = ((AbstractCard) item).makeCopy();
                AbstractDungeon.player.masterDeck.addToTop(card);
                AbstractDungeon.player.relics.forEach(r -> r.onObtainCard(card));
                AbstractDungeon.player.relics.forEach(AbstractRelic::onMasterDeckChange);
            } else if (item instanceof AbstractRelic) {
                ProjectEMod.relicsToAdd.add(((AbstractRelic) item).makeCopy());
            } else if (item instanceof AbstractPotion) {
                if(AbstractDungeon.player.potionSlots <= AbstractDungeon.player.potions.stream()
                        .filter(potion -> !(potion instanceof PotionSlot))
                        .count()) return;
                AbstractDungeon.player.obtainPotion(((AbstractPotion) item).makeCopy());
            }
            //deduct emc
            TransmutationTable.PLAYER_EMC -= getEmc();
        }
    }

    @Override
    public String toString() {
        return id;
    }
}
