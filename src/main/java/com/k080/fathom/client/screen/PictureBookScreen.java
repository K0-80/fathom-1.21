package com.k080.fathom.client.screen;

import com.k080.fathom.Fathom;
import com.k080.fathom.component.ModComponents;
import com.k080.fathom.item.book.BookPage;
import com.k080.fathom.item.book.BookPages;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.OrderedText;
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

    private static final int ANIMATION_TOTAL_FRAMES = 22;
    private static final int ANIMATION_FRAMETIME = 2;

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

        long time = this.client.world.getTime();
        int frameIndex = (int) ((time / ANIMATION_FRAMETIME) % ANIMATION_TOTAL_FRAMES);
        int vOffset = frameIndex * BOOK_HEIGHT;
        context.drawTexture(BOOK_TEXTURE, x, y, 0, vOffset, BOOK_WIDTH, BOOK_HEIGHT, BOOK_WIDTH, BOOK_HEIGHT * ANIMATION_TOTAL_FRAMES);

        if (!unlockedPages.isEmpty() && currentPage < unlockedPages.size()) {
            BookPage page = unlockedPages.get(currentPage);

            // Render image if present
            page.getTexture().ifPresent(textureId -> {
                context.drawTexture(textureId, x, y, 0, 0, BOOK_WIDTH, BOOK_HEIGHT, BOOK_WIDTH, BOOK_HEIGHT);
            });

            // Render text if present
            page.getContent().ifPresent(textContent -> {
                int textX = x + 20; // 20px left padding
                int textY = y + 30; // 30px top padding
                int maxWidth = BOOK_WIDTH - 40; // 20px padding on each side
                int color = 0x000000; // Black text

                List<OrderedText> wrappedLines = textRenderer.wrapLines(textContent, maxWidth);
                for (int i = 0; i < wrappedLines.size(); i++) {
                    OrderedText line = wrappedLines.get(i);
                    int currentY = textY + (i * textRenderer.fontHeight);

                    // Stop drawing if text would go into the bottom padding area
                    if (currentY > y + BOOK_HEIGHT - 30 - textRenderer.fontHeight) {
                        break;
                    }
                    context.drawText(textRenderer, line, textX, currentY, color, false);
                }
            });
        }
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}