package com.k080.fathom.item.book;

import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.Comparator;
import java.util.Optional;

public class BookPage {
    public static final Comparator<BookPage> COMPARATOR = Comparator.comparingInt(BookPage::pageNumber);

    private final Identifier id;
    private final int pageNumber;
    private final Optional<Identifier> texture;
    private final Optional<Text> content;

    private BookPage(Identifier id, int pageNumber, Identifier texture) {
        this.id = id;
        this.pageNumber = pageNumber;
        this.texture = Optional.of(texture);
        this.content = Optional.empty();
    }

    private BookPage(Identifier id, int pageNumber, Text content) {
        this.id = id;
        this.pageNumber = pageNumber;
        this.texture = Optional.empty();
        this.content = Optional.of(content);
    }

    public static BookPage createImagePage(Identifier id, int pageNumber, Identifier texture) {
        return new BookPage(id, pageNumber, texture);
    }

    public static BookPage createTextPage(Identifier id, int pageNumber, Text content) {
        return new BookPage(id, pageNumber, content);
    }

    public Identifier id() {
        return id;
    }

    public int pageNumber() {
        return pageNumber;
    }

    public Optional<Identifier> getTexture() {
        return texture;
    }

    public Optional<Text> getContent() {
        return content;
    }
}