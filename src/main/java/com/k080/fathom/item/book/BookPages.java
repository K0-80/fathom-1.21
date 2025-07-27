package com.k080.fathom.item.book;

import com.k080.fathom.Fathom;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public class BookPages {
    public static final Map<Identifier, BookPage> PAGES = new LinkedHashMap<>();

    // Define all your pages here. The page number determines the order in the book.
    public static final BookPage PAGE_1 = registerImage("page_1", 1);
    public static final BookPage PAGE_2 = registerImage("page_2", 2);
    public static final BookPage PAGE_3 = registerImage("page_3", 3);
    public static final BookPage PAGE_4 = registerImage("page_4", 4);
    public static final BookPage PAGE_5 = registerImage("page_5", 5);

    private static BookPage registerImage(String id, int pageNumber) {
        Identifier pageId = Identifier.of(Fathom.MOD_ID, id);
        // Page textures are located in assets/fathom/textures/gui/book/
        Identifier textureId = Identifier.of(Fathom.MOD_ID, "textures/gui/book/" + id + ".png");
        BookPage page = BookPage.createImagePage(pageId, pageNumber, textureId);
        PAGES.put(pageId, page);
        return page;
    }

    //for text pages, currently bit bugged lol
    private static BookPage registerText(String id, int pageNumber, String contentKey) {
        Identifier pageId = Identifier.of(Fathom.MOD_ID, id);
        Text content = Text.translatable(contentKey);
        BookPage page = BookPage.createTextPage(pageId, pageNumber, content);
        PAGES.put(pageId, page);
        return page;
    }

    public static Optional<BookPage> getPage(Identifier id) {
        return Optional.ofNullable(PAGES.get(id));
    }
}