package com.k080.fathom.client.screen;

import com.k080.fathom.Fathom;
import com.k080.fathom.component.ModComponents;
import com.k080.fathom.item.book.BookPage;
import com.k080.fathom.item.book.BookPages;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class PictureBookScreen extends Screen {
    private static final Identifier BOOK_TEXTURE = Identifier.of(Fathom.MOD_ID, "textures/gui/book/book.png");
    private static final int BOOK_WIDTH = 256;
    private static final int BOOK_HEIGHT = 200;

    private final ItemStack bookStack;
    private List<BookPage> unlockedPages;
    private int currentPage = 0;

    public PictureBookScreen(ItemStack bookStack) {
        super(Text.translatable("item.fathom.picture_book"));
        this.bookStack = bookStack;
    }

    @Override
    protected void init() {
        super.init();
        loadPages();
    }

    private void loadPages() {
        Set<Identifier> unlockedPageIds = this.bookStack.getOrDefault(ModComponents.UNLOCKED_PAGES, Collections.emptySet());
        this.unlockedPages = new ArrayList<>();
        for (Identifier id : unlockedPageIds) {
            BookPages.getPage(id).ifPresent(this.unlockedPages::add);
        }
        this.unlockedPages.sort(BookPage.COMPARATOR);
    }

    private boolean turnPage(int amount) {
        int newPage = this.currentPage + amount;
        if (newPage >= 0 && newPage < this.unlockedPages.size()) {
            this.currentPage = newPage;
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) { // Left click
            int x = (this.width - BOOK_WIDTH) / 2;
            int y = (this.height - BOOK_HEIGHT) / 2;
            int bookMidpointX = x + BOOK_WIDTH / 2;

            // Check if click is within the book's area
            if (mouseX >= x && mouseX < x + BOOK_WIDTH && mouseY >= y && mouseY < y + BOOK_HEIGHT) {
                if (mouseX < bookMidpointX) {
                    if (this.turnPage(-1)) {
                        this.client.player.playSound(SoundEvents.ITEM_BOOK_PAGE_TURN, 1.0F, 1.0F);
                    }
                } else {
                    if (this.turnPage(1)) {
                        this.client.player.playSound(SoundEvents.ITEM_BOOK_PAGE_TURN, 1.0F, 1.0F);
                    }
                }
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        int x = (this.width - BOOK_WIDTH) / 2;
        int y = (this.height - BOOK_HEIGHT) / 2;

        context.drawTexture(BOOK_TEXTURE, x, y, 0, 0, BOOK_WIDTH, BOOK_HEIGHT, BOOK_WIDTH, BOOK_HEIGHT);

        if (!unlockedPages.isEmpty() && currentPage < unlockedPages.size()) {
            BookPage page = unlockedPages.get(currentPage);
            context.drawTexture(page.texture(), x, y, 0, 0, BOOK_WIDTH, BOOK_HEIGHT, BOOK_WIDTH, BOOK_HEIGHT);
        }
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}