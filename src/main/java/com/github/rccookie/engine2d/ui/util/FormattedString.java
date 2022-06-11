package com.github.rccookie.engine2d.ui.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.stream.Collectors;

import com.github.rccookie.util.Arguments;
import com.github.rccookie.util.Cloneable;
import com.github.rccookie.util.Console;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FormattedString implements List<FormattedString.Segment>, Cloneable<FormattedString> {

    private final List<Segment> data = new ArrayList<>();

    public FormattedString(Segment... segments) {
        addAll(List.of(segments));
    }

    @Override
    public String toString() {
        return data.stream().map(Segment::toString).collect(Collectors.joining());
    }

    @Override
    public int size() {
        return data.size();
    }

    @Override
    public boolean isEmpty() {
        return data.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return data.contains(o);
    }

    @NotNull
    @Override
    public Iterator<Segment> iterator() {
        return data.iterator();
    }

    @NotNull
    @Override
    public Object @NotNull [] toArray() {
        return data.toArray();
    }

    @NotNull
    @Override
    public <T> T @NotNull [] toArray(@NotNull T @NotNull [] a) {
        //noinspection SuspiciousToArrayCall
        return data.toArray(a);
    }

    @Override
    public boolean add(Segment segment) {
        return data.add(Arguments.checkNull(segment, "segment"));
    }

    @Override
    public boolean remove(Object o) {
        return data.remove(o);
    }

    @Override
    public boolean containsAll(@NotNull Collection<?> c) {
        return data.containsAll(c);
    }

    @Override
    public boolean addAll(@NotNull Collection<? extends Segment> c) {
        boolean out = false;
        for(Segment s : c) out |= add(s);
        return out;
    }

    @Override
    public boolean addAll(int index, @NotNull Collection<? extends Segment> c) {
        for(Segment s : c) add(index++, s);
        return !c.isEmpty();
    }

    @Override
    public boolean removeAll(@NotNull Collection<?> c) {
        return data.removeAll(c);
    }

    @Override
    public boolean retainAll(@NotNull Collection<?> c) {
        return data.retainAll(c);
    }

    @Override
    public void clear() {
        data.clear();
    }

    @Override
    public Segment get(int index) {
        return data.get(index);
    }

    @Override
    public Segment set(int index, Segment element) {
        return data.set(index, Arguments.checkNull(element, "element"));
    }

    @Override
    public void add(int index, Segment element) {
        data.add(index, Arguments.checkNull(element, "element"));
    }

    @Override
    public Segment remove(int index) {
        return data.remove(index);
    }

    @Override
    public int indexOf(Object o) {
        return data.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return data.lastIndexOf(o);
    }

    @NotNull
    @Override
    public ListIterator<Segment> listIterator() {
        return data.listIterator();
    }

    @NotNull
    @Override
    public ListIterator<Segment> listIterator(int index) {
        return data.listIterator(index);
    }

    @NotNull
    @Override
    public List<Segment> subList(int fromIndex, int toIndex) {
        return data.subList(fromIndex, toIndex);
    }

    @Override
    public @NotNull FormattedString clone() {
        FormattedString f = new FormattedString();
        for(Segment s : this) f.add(s.clone());
        return f;
    }


    public static class Segment implements Iterable<Format>, Cloneable<Segment> {

        private String string;
        private final Set<Format> formats = new HashSet<>();

        public Segment(String string, Format... formats) {
            this.string = Arguments.checkNull(string, "string");
            this.formats.addAll(Set.of(formats));
        }

        private Segment(String string, boolean bold, boolean italic,
                        boolean strikethrough, boolean underlined, boolean monospace) {
            this.string = string;

            if(bold)          add(Format.BOLD);
            if(italic)        add(Format.ITALIC);
            if(strikethrough) add(Format.STRIKETHROUGH);
            if(underlined)    add(Format.UNDERLINED);
            if(monospace)     add(Format.MONOSPACE);
        }

        @Override
        public String toString() {
            String str = string;
            for(Format f : formats)
                if(f instanceof Format.Link)
                    str = f.toString(str);
            for(Format f : formats)
                if(!(f instanceof Format.Link))
                    str = f.toString(str);
            return str;
        }

        public String getString() {
            return string;
        }

        public void setString(String string) {
            this.string = Arguments.checkNull(string, "string");
        }

        public void add(Format format) {
            formats.add(Arguments.checkNull(format, "format"));
        }

        public boolean remove(Format format) {
            return formats.remove(format);
        }

        @NotNull
        @Override
        public Iterator<Format> iterator() {
            return formats.iterator();
        }

        @Override
        public @NotNull Segment clone() {
            return new Segment(string, formats.toArray(new Format[0]));
        }
    }

    public interface Format {
        Format BOLD          = s -> '*' + s + '*';
        Format ITALIC        = s -> '^' + s + '^';
        Format STRIKETHROUGH = s -> '~' + s + '~';
        Format UNDERLINED    = s -> '_' + s + '_';
        Format MONOSPACE     = s -> '`' + s + '`';

        class Link implements Format {
            public final String url;

            public Link(String url) {
                this.url = Arguments.checkNull(url, "url");
            }

            @Override
            public String toString(String str) {
                return '@' + url + (str.length() == 0 ? "" : '[' + str + ']');
            }
        }

        String toString(String str);
    }

    public static FormattedString parse(String str) {

        boolean bold = false, // = *abc*
                italic = false, // = ^abc^
                strikethrough = false, // = ~abc~
                underlined = false, // = _abc_
                monospace = false; // = `abc`

        FormattedString f = new FormattedString();
        StringBuilder segment = new StringBuilder();

        for(int i=0; i<str.length(); i++) {
            char c = str.charAt(i);
            if(c == '\\') {
                if(i != str.length()-1)
                    c = str.charAt(++i);
                segment.append(c);
            }
            else if(c == '*') {
                appendSegment(f, segment, bold, italic, strikethrough, underlined, monospace);
                bold = !bold;
            }
            else if(c == '^') {
                appendSegment(f, segment, bold, italic, strikethrough, underlined, monospace);
                italic = !italic;
            }
            else if(c == '~') {
                appendSegment(f, segment, bold, italic, strikethrough, underlined, monospace);
                strikethrough = !strikethrough;
            }
            else if(c == '_') {
                appendSegment(f, segment, bold, italic, strikethrough, underlined, monospace);
                underlined = !underlined;
            }
            else if(c == '`') {
                appendSegment(f, segment, bold, italic, strikethrough, underlined, monospace);
                monospace = !monospace;
            }
            else if(c == '@') { // link, = @http[abc]
                appendSegment(f, segment, bold, italic, strikethrough, underlined, monospace);

                StringBuilder url = new StringBuilder();
                for(i++; i<str.length(); i++) {
                    c = str.charAt(i);
                    if(c == '\\') {
                        if(i != str.length()-1)
                            c = str.charAt(++i);
                        url.append(c);
                    }
                    else if(c == ' ' || c == '[') break;
                    else url.append(c);
                }

                if(i < str.length()-1 && str.charAt(i) == '[') {
                    for(i++; i<str.length(); i++) {
                        c = str.charAt(i);
                        if(c == '\\') {
                            if(i != str.length()-1)
                                c = str.charAt(++i);
                            segment.append(c);
                        }
                        else if(c == ']') break;
                        else segment.append(c);
                    }
                }
                if(segment.length() == 0)
                    segment.append(url);

                Segment added = appendSegment(f, segment, bold, italic, strikethrough, underlined, monospace);
                if(added != null) added.add(new Format.Link(url.toString()));
            }
            else segment.append(c);
        }

        appendSegment(f, segment, bold, italic, strikethrough, underlined, monospace);

        return f;
    }

    @Nullable
    private static Segment appendSegment(FormattedString f, StringBuilder segment, boolean bold, boolean italic,
                                      boolean strikethrough, boolean underlined, boolean monospace) {
        if(segment.length() == 0) return null;
        Segment s = new Segment(segment.toString(), bold, italic, strikethrough, underlined, monospace);
        f.add(s);
        segment.delete(0, segment.length());
        return s;
    }

    public static FormattedString plain(String str) {
        return new FormattedString(new Segment(str));
    }

    public static void main(String[] args) {
        Console.log(parse("Some *bold, ^italic, ~strikethrough, _underlined, `monospace\\_~ and^ @myUrl[]* text.`"));
    }
}
