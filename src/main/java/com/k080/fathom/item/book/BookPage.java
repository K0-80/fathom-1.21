package com.k080.fathom.item.book;

import net.minecraft.util.Identifier;

import java.util.Comparator;

public record BookPage(Identifier id, Identifier texture, int pageNumber) {
    public static final Comparator<BookPage> COMPARATOR = Comparator.comparingInt(BookPage::pageNumber);
}