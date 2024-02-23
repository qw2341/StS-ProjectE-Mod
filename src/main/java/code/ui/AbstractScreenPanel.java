package code.ui;

import code.ProjectEMod;
import code.util.ListItem;
import code.util.TextSearchBox;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.MathHelper;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.screens.mainMenu.ScrollBar;
import com.megacrit.cardcrawl.screens.mainMenu.ScrollBarListener;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.stream.Collectors;

public class AbstractScreenPanel<T> implements ScrollBarListener {
    protected ScrollBar scrollBar;

    public static final float START_X = 400.0F * Settings.scale;
    public static final float START_Y = Settings.HEIGHT - 300.0F * Settings.scale;

    public static final float SEGMENT_WIDTH = Settings.WIDTH / 2f;
    protected final float spaceY;
    public final float spaceX;
    protected float scrollY = START_Y;
    protected float targetY = this.scrollY;

    protected float scrollLowerBound = Settings.HEIGHT - 200.0F * Settings.scale;
    protected float scrollUpperBound = scrollLowerBound + Settings.DEFAULT_SCROLL_LIMIT;

    private float arrowScale1 = 1.0F, arrowScale2 = 1.0F, arrowScale3 = 1.0F, arrowTimer = 0.0F;

    protected boolean grabbedScreen = false;
    protected float grabStartY = 0.0F;
    protected int row = 0;
    protected int col = 0;

    protected int itemsPerLine;

    public ArrayList<ListItem<T>> playerItems;
    public ArrayList<ListItem<T>> transmutableItems;

    public ListItem<T> hoveredItem = null;
    protected ListItem<T> clickStartedItem = null;

    private final TextSearchBox searchBox;

    private final ItemGetter<T> getPlayerItems;
    private final ItemGetter<T> getTransmutableItems;

    private float itemWidth;
    private float itemHeight;

    public AbstractScreenPanel(ItemGetter<T> getPlayerItems, ItemGetter<T> getTransmutableItems, float itemWidth, float itemHeight, float spacing) {
        this.scrollBar = new ScrollBar(this);
        this.searchBox = new TextSearchBox(this, 0f, Settings.HEIGHT / 4f, false);

        this.getPlayerItems = getPlayerItems;
        this.getTransmutableItems = getTransmutableItems;

        this.playerItems = getPlayerItems.getItems();
        this.transmutableItems = getTransmutableItems.getItems();

        this.itemWidth = itemWidth;
        this.itemHeight = itemHeight;
        this.spaceX = itemWidth + spacing;
        this.spaceY = itemHeight + spacing;
        this.itemsPerLine = Math.round(SEGMENT_WIDTH / (getItemWidth() + this.spaceX));
    }
    protected void callOnOpen() {
        onChangeEMC();
    }

    public void onChangeEMC() {
        this.playerItems = getPlayerItems.getItems();
        this.transmutableItems = getTransmutableItems.getItems();
        ProjectEMod.logger.info("Stored items: " + this.transmutableItems.toString());
        updateFilters();
    }

    public void updateItemClickLogic() {
        if(hoveredItem != null && clickStartedItem == hoveredItem) {
            if(InputHelper.justReleasedClickLeft) {
                if(this.playerItems.contains(hoveredItem)) {
                    //remove from player
                    hoveredItem.toEMC();
                } else if (this.transmutableItems.contains(hoveredItem)) {
                    //add to player
                    hoveredItem.toPlayer();
                }
                onChangeEMC();
                clickStartedItem = null;
            }

        }
    }

    public void update() {
        this.searchBox.update();
        updateItemClickLogic();

        hoveredItem = null;

        boolean isScrollingScrollBar = scrollBar.update();
        if (!isScrollingScrollBar) {
            updateScrolling();
        }
        updateList(playerItems);
        updateList(transmutableItems);
        InputHelper.justClickedLeft = false;
        InputHelper.justClickedRight = false;
    }

    private boolean testTextFilter(ListItem<?> item) {
        if (item.id != null && StringUtils.containsIgnoreCase(item.id,searchBox.filterText)) return true;
        if (item.name != null && StringUtils.containsIgnoreCase(item.name,searchBox.filterText)) return true;
        if (item.desc != null && StringUtils.containsIgnoreCase(item.desc,searchBox.filterText)) return true;
        return false;
    }


    protected boolean testFilters(ListItem<?> item) {
        boolean textCheck = searchBox.filterText.equals("") || testTextFilter(item);
        return textCheck;
    }



    public void updateFilters() {
        resetFilters();
        this.playerItems.forEach(i -> i.shouldRender = testFilters(i));
        this.transmutableItems.forEach(i -> i.shouldRender = testFilters(i) && TransmutationTable.PLAYER_EMC >= i.emc);
        calculateScrollBounds();
    }

    private void resetFilters() {
        this.playerItems.forEach(i -> i.shouldRender = true);
        this.transmutableItems.forEach(i -> i.shouldRender = true);
    }

    private void updateList(ArrayList<ListItem<T>> items) {
        for (ListItem<T> item : items)
        {
            if(!item.shouldRender) continue;

            item.update();

            if (item.hb.hovered)
            {
                hoveredItem = item;
            }
            if(item.hb.hovered && InputHelper.justClickedLeft) {
                item.hb.clicked = true;
                clickStartedItem = item;
                //ProjectEMod.logger.info("Clicked " + clickStartedItem.id);
                InputHelper.justClickedLeft = false;
            }
        }
    }

    public void render(SpriteBatch sb) {
        row = -1;
        col = 0;


        renderList(sb, playerItems, START_X);
        renderList(sb, transmutableItems, START_X + SEGMENT_WIDTH);

        renderArrows(sb);

        scrollBar.render(sb);
        searchBox.render(sb);
    }

    private void renderArrows(SpriteBatch sb) {
        float x = Settings.WIDTH / 2.0F - 73.0F * Settings.scale - 32.0F;
        sb.setColor(Color.WHITE);
        sb.draw(ImageMaster.UPGRADE_ARROW, x, Settings.HEIGHT / 2.0F - 32.0F, 32.0F, 32.0F, 64.0F, 64.0F, this.arrowScale1 * Settings.scale, this.arrowScale1 * Settings.scale, 0.0F, 0, 0, 64, 64, false, false);
        x += 64.0F * Settings.scale;
        sb.setColor(Color.WHITE);
        sb.draw(ImageMaster.UPGRADE_ARROW, x, Settings.HEIGHT / 2.0F - 32.0F, 32.0F, 32.0F, 64.0F, 64.0F, this.arrowScale2 * Settings.scale, this.arrowScale2 * Settings.scale, 0.0F, 0, 0, 64, 64, false, false);
        x += 64.0F * Settings.scale;
        sb.draw(ImageMaster.UPGRADE_ARROW, x, Settings.HEIGHT / 2.0F - 32.0F, 32.0F, 32.0F, 64.0F, 64.0F, this.arrowScale3 * Settings.scale, this.arrowScale3 * Settings.scale, 0.0F, 0, 0, 64, 64, false, false);

        this.arrowTimer += Gdx.graphics.getDeltaTime() * 2.0F;
        this.arrowScale1 = 0.8F + (MathUtils.cos(this.arrowTimer) + 1.0F) / 8.0F;
        this.arrowScale2 = 0.8F + (MathUtils.cos(this.arrowTimer - 0.8F) + 1.0F) / 8.0F;
        this.arrowScale3 = 0.8F + (MathUtils.cos(this.arrowTimer - 1.6F) + 1.0F) / 8.0F;
    }

    private void renderList(SpriteBatch sb, ArrayList<ListItem<T>> items, float startX) {
        row = 0;
        col = 0;
        float curX;
        float curY;

        for (Iterator<ListItem<T>> it = items.iterator(); it.hasNext();) {
            ListItem<?> item = it.next();

            if(!item.shouldRender) continue;

            if (col == itemsPerLine) {
                col = 0;
                row += 1;
            }
            curX = (startX + spaceX * col);
            curY = (scrollY - spaceY * row);

            item.setPos(curX, curY);

            item.render(sb);
            col += 1;
        }
    }

    public void open() {
        callOnOpen();

        targetY = START_Y;

        calculateScrollBounds();
    }

    public void close() {
        adjustPotionPositions();
    }

    private void adjustPotionPositions() {
        for(int i = 0; i < AbstractDungeon.player.potions.size(); ++i) {
            ((AbstractPotion)AbstractDungeon.player.potions.get(i)).adjustPosition(i);
        }

    }

    protected void updateScrolling()
    {
        int y = InputHelper.mY;
        if (!grabbedScreen)
        {
            if (InputHelper.scrolledDown) {
                targetY += Settings.SCROLL_SPEED;
            } else if (InputHelper.scrolledUp) {
                targetY -= Settings.SCROLL_SPEED;
            }
            if (InputHelper.justClickedLeft)
            {
                grabbedScreen = true;
                grabStartY = (y - targetY);
            }
        }
        else if (InputHelper.isMouseDown)
        {
            targetY = (y - grabStartY);
        }
        else
        {
            grabbedScreen = false;
        }
        scrollY = MathHelper.scrollSnapLerpSpeed(scrollY, targetY);
        resetScrolling();
        updateBarPosition();
    }


    @Override
    public void scrolledUsingBar(float newPercent)
    {
        float newPosition = MathHelper.valueFromPercentBetween(scrollLowerBound, scrollUpperBound, newPercent);
        scrollY = newPosition;
        targetY = newPosition;
        updateBarPosition();
    }

    private void updateBarPosition()
    {
        float percent = MathHelper.percentFromValueBetween(scrollLowerBound, scrollUpperBound, scrollY);
        scrollBar.parentScrolledToPercent(percent);
    }

    protected void calculateScrollBounds()
    {
        int size = playerItems.size();

        int scrollTmp = 0;
        if (size > this.itemsPerLine) {
            scrollTmp = size / this.itemsPerLine;
            scrollTmp += this.itemsPerLine;
            if (size % this.itemsPerLine != 0) {
                ++scrollTmp;
            }
            scrollUpperBound = scrollLowerBound + Settings.DEFAULT_SCROLL_LIMIT + (scrollTmp) * getItemHeight() * Settings.scale;
        } else {
            scrollUpperBound = scrollLowerBound + Settings.DEFAULT_SCROLL_LIMIT;
        }
    }

    private void resetScrolling()
    {
        if (targetY < scrollLowerBound) {
            targetY = MathHelper.scrollSnapLerpSpeed(targetY, scrollLowerBound);
        } else if (targetY > scrollUpperBound) {
            targetY = MathHelper.scrollSnapLerpSpeed(targetY, scrollUpperBound);
        }
    }

    private float getItemHeight() {
        return itemHeight;
    }

    private float getItemWidth() {
        return itemWidth;
    }

    public interface ItemGetter<K> {
        ArrayList<ListItem<K>> getItems();
    }
}
