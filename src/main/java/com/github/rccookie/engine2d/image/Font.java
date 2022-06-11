package com.github.rccookie.engine2d.image;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.github.rccookie.engine2d.Application;
import com.github.rccookie.engine2d.util.Num;
import com.github.rccookie.geometry.performance.int2;
import com.github.rccookie.util.Arguments;

import org.jetbrains.annotations.NotNull;

public class Font {

    private static final Map<String, Boolean> SUPPORTED_FONTS = new HashMap<>();

    public static final int PLAIN = 0;
    public static final int BOLD = 1;
    public static final int ITALIC = 1 << 1;
    public static final int STRIKETHROUGH = 1 << 2;
    public static final int UNDERLINED = 1 << 3;
    public static final int LEFT_ALIGN = 0;
    public static final int CENTER_ALIGN = 1 << 4;
    public static final int RIGHT_ALIGN = 1 << 5;
    public static final int BLOCK_ALIGN = 1 << 6;

    private static final int DEFAULT_SIZE = 20;
    private static final String DEFAULT_NAME = Application.getImplementation().getImageFactory().getDefaultFont();
    private static final String SERIF_NAME = Application.getImplementation().getImageFactory().getDefaultSerifFont();
    private static final String MONOSPACE_NAME = Application.getImplementation().getImageFactory().getDefaultMonospaceFont();

    public static final Font DEFAULT = defaultF(DEFAULT_SIZE);
    public static final Font SERIF = serif(DEFAULT_SIZE);
    public static final Font MONOSPACE = monospace(DEFAULT_SIZE);


    @NotNull
    public final String name;
    public final int size;
    public final boolean bold;
    public final boolean italic;
    public final boolean strikethrough;
    public final boolean underlined;
    @NotNull
    public final FontAlignment alignment;
    public final boolean blockAlignment;

    final int flags;
    private final FontData data;

    public Font(@NotNull String name, int size) {
        this(name, size, PLAIN);
    }

    public Font(@NotNull String name, int size, int flags) {
        this.name = Arguments.checkNull(name, "name");
        this.size = size;
        this.bold = (flags & BOLD) != 0;
        this.italic = (flags & ITALIC) != 0;
        this.strikethrough = (flags & STRIKETHROUGH) != 0;
        this.underlined = (flags & UNDERLINED) != 0;
        this.blockAlignment = (flags & BLOCK_ALIGN) != 0;

        int align = flags & (CENTER_ALIGN | RIGHT_ALIGN);
        if(align == 0) this.alignment = FontAlignment.LEFT;
        else if(align == CENTER_ALIGN) this.alignment = FontAlignment.CENTER;
        else if(align == RIGHT_ALIGN) this.alignment = FontAlignment.RIGHT;
        else throw new IllegalArgumentException("Cannot use CENTER_ALIGN and RIGHT_ALIGN at the same time");

        this.flags = flags;
        this.data = FontData.getInstance(this);
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        Font font = (Font) o;
        return size == font.size && flags == font.flags && name.equals(font.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, size, flags);
    }

    @Override
    public String toString() {
        return "Font{" +
                "name='" + name + '\'' +
                ", size=" + size +
                ", bold=" + bold +
                ", italic=" + italic +
                ", strikethrough=" + strikethrough +
                ", underlined=" + underlined +
                ", alignment=" + alignment +
                ", blockAlignment=" + blockAlignment +
                '}';
    }

    public Font setName(String name) {
        if(this.name.equals(name)) return this;
        return new Font(name, size, flags);
    }

    public Font setSize(int size) {
        if(this.size == size) return this;
        return new Font(name, size, flags);
    }

    public Font setFlags(int flags) {
        if(this.flags == flags) return this;
        return new Font(name, size, flags);
    }

    public Font setBold(boolean bold) {
        if(this.bold == bold) return this;
        return bold ?
                new Font(name, size, flags | BOLD) :
                new Font(name, size, flags & ~BOLD);
    }

    public Font setItalic(boolean italic) {
        if(this.italic == italic) return this;
        return italic ?
                new Font(name, size, flags | ITALIC) :
                new Font(name, size, flags & ~ITALIC);
    }

    public Font setStrikethrough(boolean strikethrough) {
        if(this.strikethrough == strikethrough) return this;
        return strikethrough ?
                new Font(name, size, flags | STRIKETHROUGH) :
                new Font(name, size, flags & ~STRIKETHROUGH);
    }

    public Font setUnderlined(boolean underlined) {
        if(this.underlined == underlined) return this;
        return underlined ?
                new Font(name, size, flags | UNDERLINED) :
                new Font(name, size, flags & ~UNDERLINED);
    }

    public Font setAlignment(FontAlignment alignment) {
        if(this.alignment == Arguments.checkNull(alignment, "alignment")) return this;
        if(alignment == FontAlignment.LEFT)
            return new Font(name, size, flags & ~CENTER_ALIGN & ~RIGHT_ALIGN);
        if(alignment == FontAlignment.CENTER)
            return new Font(name, size, flags & ~RIGHT_ALIGN | CENTER_ALIGN);
        return new Font(name, size, flags & ~CENTER_ALIGN | RIGHT_ALIGN);
    }

    public Font setBlockAlignment(boolean blockAlignment) {
        if(this.blockAlignment == blockAlignment) return this;
        return blockAlignment ?
                new Font(name, size, flags | BLOCK_ALIGN) :
                new Font(name, size, flags & ~BLOCK_ALIGN);
    }

    public boolean isSupported() {
        return SUPPORTED_FONTS.computeIfAbsent(name.toLowerCase(), Application.getImplementation().getImageFactory()::isFontSupported);
    }

    public Image render(String text, Color color) {
        String[] strLines = text.split("\r?\n");
        Line[] lines = new Line[strLines.length];

        int width = 1;
        for(int i=0; i<lines.length; i++) {
            lines[i] = new Line(strLines[i], color);
            width = Num.max(width, lines[i].width);
        }

        Image image = new Image(Num.max(1, width), size * lines.length);
        for(int i=0; i<lines.length; i++)
            lines[i].render(image, i * size);
        return image;
    }

    public Image render(String text, Color color, int maxWidth) {

        List<Line> lines = new ArrayList<>();
        int width = 1;

        for(String str : text.split("\r?\n")) {
            if(str.isEmpty())
                lines.add(new Line("", color));
            else do {
                Line line = new Line(str, color, maxWidth, false);
                lines.add(line);
                width = Num.max(width, line.width);
                str = str.substring(line.str.length()).replaceFirst("^\\s*", "");
            } while(!str.isEmpty());
        }

        Image image = new Image(Num.max(1, width), size * lines.size());
        for(int i=0; i<lines.size(); i++)
            lines.get(i).render(image, i * size);
        return image;
    }

    public RenderResult tryRenderWordsOrChars(String line, Color color, int maxWidth) {

        if(line.contains("\n"))
            throw new IllegalArgumentException("No newlines allowed");

        char[] characters = line.toCharArray();
        Image[] chars = new Image[characters.length];
        String rendered = line, remaining = "";

        int width = 0;
        for(int i=0; i<characters.length; i++) {
            chars[i] = data.getChar(characters[i], color);
            width += chars[i].size.x;

            if(width > maxWidth) {

                for(; i>=0 && characters[i] != ' '; i--) width -= chars[i].size.x;
                rendered = line.substring(0, i+1);
                remaining = line.substring(i+1);
                for(; i>=0 && characters[i] == ' '; i--) width -= chars[i].size.x;

                if(i <= 0) {
                    i = 1;
                    width = chars[0].size.x;
                    while(i < characters.length) {
                        chars[i] = data.getChar(characters[i], color);
                        if(width + chars[i].size.x > maxWidth) break;
                        width += chars[i].size.x;
                        i++;
                    }
                    rendered = line.substring(0, i+1);
                    remaining = line.substring(i+1);
//                    for(; width < maxWidth; width += chars[i].size.x) i++;
                }

                chars = Arrays.copyOfRange(chars, 0, i+1);
                break;
            }
        }

        return new RenderResult(rendered, remaining, renderChars(width, chars));
    }

    public RenderResult tryRenderWords(String line, Color color, int maxWidth) {

        if(line.contains("\n"))
            throw new IllegalArgumentException("No newlines allowed");

        char[] characters = line.toCharArray();
        Image[] chars = new Image[characters.length];
        String rendered = line, remaining = "";

        int width = 0;
        for(int i=0; i<characters.length; i++) {
            chars[i] = data.getChar(characters[i], color);
            width += chars[i].size.x;

            if(width > maxWidth) {

                for(; i>=0 && characters[i] != ' '; i--) width -= chars[i].size.x;
                rendered = line.substring(0, i+1);
                remaining = line.substring(i+1);
                for(; i>=0 && characters[i] == ' '; i--) width -= chars[i].size.x;
                chars = Arrays.copyOfRange(chars, 0, i+1);
                break;
            }
        }

        return new RenderResult(rendered, remaining, renderChars(Num.max(1, width), chars));
    }

    public RenderResult tryRenderChars(String line, Color color, int maxWidth) {

        if(line.contains("\n"))
            throw new IllegalArgumentException("No newlines allowed");

        char[] characters = line.toCharArray();
        Image[] chars = new Image[characters.length];
        String rendered = line, remaining = "";

        int width = 0;
        for(int i=0; i<characters.length; i++) {

            chars[i] = data.getChar(characters[i], color);

            if(width + chars[i].size.x > maxWidth) {
                rendered = line.substring(0, i);
                remaining = line.substring(i);
                chars = Arrays.copyOfRange(chars, 0, i+1);
                break;
            }

            width += chars[i].size.x;
        }

        return new RenderResult(rendered, remaining, renderChars(width, chars));
    }

    public Image renderChar(char character, Color color) {
        return data.getChar(character, color).clone();
    }

    public int2 charSize(char character) {
        return data.getChar(character, Color.CLEAR).size;
    }

    private Image renderChars(int width, Image[] chars) {
        Image image = new Image(Num.max(1, width), size);
        int x = 0;
        for(Image character : chars) {
            image.drawImage(character, new int2(x, 0));
            x += character.size.x;
        }
        return image;
    }

    private class Line {
        public String str;
        private Image[] chars;
        private int width = 0;
        private boolean softWrap = false;

        public Line(String str, Color color) {
            this(str, color, Integer.MAX_VALUE, true);
        }

        public Line(String str, Color color, int maxWidth, boolean charByChar) {

            if(str.contains("\n"))
                throw new IllegalArgumentException("Newlines not permitted");

            this.str = str;
            chars = new Image[str.length()];
            for(int i = 0; i<chars.length; i++) {
                chars[i] = data.getChar(this.str.charAt(i), color);
                width += chars[i].size.x;

                if(width > maxWidth && i != 0) {
                    width -= chars[i].size.x;
                    i--;
                    if(!charByChar) {
                        for(; i>0 && str.charAt(i) != ' '; i--)
                            width -= chars[i].size.x;
                        for(; i>0 && str.charAt(i) == ' '; i--)
                            width -= chars[i].size.x;
                    }

                    chars = Arrays.copyOfRange(chars, 0, ++i);
                    this.str = str.substring(0, i);
                    softWrap = true;
                    break;
                }
            }
        }

        public Image render() {
            Image image = new Image(Num.max(width, 1), size);
            render(image, 0);
            return image;
        }

        public void render(Image onto, int heightOffset) {

            String str = this.str.replaceAll("^\\s+|\\s+$", "");
            if(str.isEmpty()) return;

            if(blockAlignment && softWrap) {
                int spaceCount = 0;
                for(int i=0; i<str.length(); i++)
                    if(str.charAt(i) == ' ') spaceCount++;

                float gap = spaceCount == 0 ? 0 :
                        (onto.size.x - width) / (float) spaceCount;

                float x = 0;
                for(int i = 0; i< str.length(); i++) {
                    onto.drawImage(chars[i], new int2(Num.round(x), heightOffset));
                    x += chars[i].size.x;
                    if(str.charAt(i) == ' ') x += gap;
                }
                return;
            }

            int x = 0;
            if(alignment == FontAlignment.CENTER)
                x = (onto.size.x - width) / 2;
            else if(alignment == FontAlignment.RIGHT)
                x = onto.size.x - width;

            for(Image character : chars) {
                onto.drawImage(character, new int2(x, heightOffset));
                x += character.size.x;
            }
        }
    }

    public static Font defaultF(int size) {
        return defaultF(size, PLAIN);
    }

    public static Font defaultF(int size, int flags) {
        return new Font(Font.DEFAULT_NAME, size, flags);
    }

    public static Font serif(int size) {
        return serif(size, PLAIN);
    }

    public static Font serif(int size, int flags) {
        return new Font(Font.SERIF_NAME, size, flags);
    }

    public static Font monospace(int size) {
        return monospace(size, PLAIN);
    }

    public static Font monospace(int size, int flags) {
        return new Font(Font.MONOSPACE_NAME, size, flags);
    }
}
