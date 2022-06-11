package com.github.rccookie.engine2d.ui;

import java.util.stream.Collectors;

import com.github.rccookie.engine2d.IO;
import com.github.rccookie.engine2d.UIObject;
import com.github.rccookie.engine2d.image.Color;
import com.github.rccookie.engine2d.image.Font;
import com.github.rccookie.engine2d.image.Image;
import com.github.rccookie.engine2d.image.RenderResult;
import com.github.rccookie.engine2d.util.Num;
import com.github.rccookie.geometry.performance.int2;
import com.github.rccookie.markdown.Blockquote;
import com.github.rccookie.markdown.Bold;
import com.github.rccookie.markdown.CodeBlock;
import com.github.rccookie.markdown.Document;
import com.github.rccookie.markdown.DocumentItem;
import com.github.rccookie.markdown.Element;
import com.github.rccookie.markdown.Heading;
import com.github.rccookie.markdown.InlineCode;
import com.github.rccookie.markdown.Italic;
import com.github.rccookie.markdown.Link;
import com.github.rccookie.markdown.ListItem;
import com.github.rccookie.markdown.Markdown;
import com.github.rccookie.markdown.OrderedList;
import com.github.rccookie.markdown.OrderedSublist;
import com.github.rccookie.markdown.Rule;
import com.github.rccookie.markdown.SoftBreak;
import com.github.rccookie.markdown.Space;
import com.github.rccookie.markdown.Strikethrough;
import com.github.rccookie.markdown.Text;
import com.github.rccookie.markdown.TextBlock;
import com.github.rccookie.markdown.TextItem;
import com.github.rccookie.markdown.UnorderedList;
import com.github.rccookie.markdown.UnorderedSublist;
import com.github.rccookie.markdown.Word;
import com.github.rccookie.util.Arguments;
import com.github.rccookie.util.Console;
import com.github.rccookie.util.Stopwatch;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MarkdownPanel extends UIObject {

    private static final Color QUOTE_COLOR = Color.WHITE.setAlpha(0.1f);
    private static final Color CODE_BLOCK_COLOR = new Color(0.1f);
    private static final Color RULE_COLOR = new Color(0.5f);
    private static final Color INLINE_CODE_COLOR = new Color(0xd7ba7d);

    private static final int FONT_SIZE = 20;
    private static final int[] HEADING_SIZE = { 40, 30, 25, 20, 16, 12 };
    private static final int INLINE_CODE_FONT_SIZE = 20;
    private static final int CODE_BLOCK_FONT_SIZE = 20;

    private static final Font FONT = new Font("Segoe UI", FONT_SIZE);
    private static final Font[] HEADING_FONTS = new Font[6];
    static {
        for(int i=0; i<HEADING_SIZE.length; i++)
            HEADING_FONTS[i] = FONT.setSize(HEADING_SIZE[i]).setBold(true);
    }
    private static final Font CODE_BLOCK_FONT = new Font("Consolas", CODE_BLOCK_FONT_SIZE);
    private static final Font INLINE_CODE_FONT = CODE_BLOCK_FONT.setSize(INLINE_CODE_FONT_SIZE);

    @NotNull
    private Document document;

    public MarkdownPanel(@Nullable UIObject parent, @NotNull String file) {
        this(parent, Markdown.parse(IO.readFile(file)));
    }

    public MarkdownPanel(@Nullable UIObject parent, @NotNull Document document) {
        super(parent);
        this.document = Arguments.checkNull(document, "document");
        onParentSizeChange.add(this::modified);
        setFocusable(false);
    }

    @NotNull
    public Document getDocument() {
        return document;
    }

    public void setDocument(@NotNull Document document) {
        if(this.document == document) return;
        this.document = Arguments.checkNull(document, "document");
        modified();
    }

    @Override
    protected @Nullable Image generateImage() {
        Stopwatch watch = new Stopwatch().start();
        Image image = new Image(clampSize(getParent().getSize()), getTheme().first);
        renderDocument(document, image, int2.zero());
        Console.mapDebug("Markdown rendering time", watch.stop().getPassedMillis());
        return image;
    }


    int lineHeight;

    private void renderDocument(Element<DocumentItem<?>> document, Image image, int2 offset) {
        int indent = offset.x;
        for(DocumentItem<?> item : document) {
            if(offset.y >= image.size.y) break;
            resetLineHeight();
            renderDocumentItem(item, image, offset);
            offset.x = indent;
            offset.y += lineHeight + 10;
        }
    }

    private void renderDocumentItem(DocumentItem<?> item, Image image, int2 offset) {
        if(item instanceof Blockquote) renderBlockquote((Blockquote) item, image, offset);
        else if(item instanceof CodeBlock) renderCodeBlock((CodeBlock) item, image, offset);
        else if(item instanceof Heading) renderHeading((Heading) item, image, offset);
        else if(item instanceof OrderedSublist) renderOrderedSublist((OrderedSublist) item, image, offset);
        else if(item instanceof OrderedList) renderOrderedList((OrderedList) item, image, offset);
        else if(item instanceof Rule) renderRule((Rule) item, image, offset);
        else if(item instanceof TextBlock) renderTextBlock((TextBlock) item, FONT, image, offset.x, offset);
        else if(item instanceof UnorderedSublist) renderUnorderedSublist((UnorderedSublist) item, image, offset);
        else if(item instanceof UnorderedList) renderUnorderedList((UnorderedList) item, image, offset);
        else throw new IllegalStateException("Unknown document item: " + item);
    }

    private void renderBlockquote(Blockquote quote, Image image, int2 offset) {
        Image content = new Image(image.size.subed(offset).added(-5-15, 0));
        int2 contentSize = int2.zero();
        renderDocument(quote, content, contentSize);

        image.fillRect(offset.added(5, 0),
                new int2(image.size.x - offset.x - 5-5, contentSize.y),
                QUOTE_COLOR);
        image.fillRect(offset.added(5, 0),
                new int2(3, contentSize.y), getTheme().accent);
        image.drawImage(content, offset.added(15, 0));

        offset.y += contentSize.y;
        resetLineHeight();
    }

    private void renderCodeBlock(CodeBlock codeBlock, Image image, int2 offset) {
        Image content = CODE_BLOCK_FONT.render(codeBlock.code().toString(),
                getTheme().textFirst, image.size.x - offset.x);

        image.fillRect(offset,
                new int2(image.size.x - offset.x, content.size.y + 20),
                CODE_BLOCK_COLOR);
        image.drawImage(content, offset.added(15, 10));

        offset.y += content.size.y + 20;
        resetLineHeight();
    }

    private void renderHeading(Heading heading, Image image, int2 offset) {
        renderTextBlock(heading, HEADING_FONTS[heading.getSize()-1], image, offset.x, offset);
    }

    private void renderOrderedList(OrderedList list, Image image, int2 offset) {
        int gapSize = FONT.renderChar(' ', Color.CLEAR).size.x;
        int prefixAreaSize = FONT.render(list.childCount() + ".", Color.CLEAR).size.x + gapSize;
        int indent = offset.x;
        offset.x += prefixAreaSize;
        int i = 1;
        for(ListItem<?> item : list) {
            if(offset.y >= image.size.y) break;
            resetLineHeight();

            Image prefix = FONT.render(i++ + ".", getTheme().textFirst);
            image.drawImage(prefix, offset.added(-(gapSize + prefix.size.x), 0));
            updateLineHeight(FONT.size);

            renderListItem(item, image, offset);
            offset.x = indent + prefixAreaSize;
            offset.y += lineHeight + 10;
        }
        offset.x = indent;
        resetLineHeight();
    }

    private void renderOrderedSublist(OrderedSublist list, Image image, int2 offset) {
        int indent = offset.x;
        renderTextBlock(list.text(), FONT, image, indent, offset);
        offset.x = indent;
        offset.y += resetLineHeight();
        OrderedList listPart = new OrderedList();
        for(int i=0; i<list.childCount(); i++)
            listPart.add(list.children().get(i));
        renderOrderedList(listPart, image, offset);
    }

    private void renderUnorderedList(UnorderedList list, Image image, int2 offset) {
        int dotAreaSize = FONT.size;
        int indent = offset.x;
        offset.x += dotAreaSize;
        for(ListItem<?> item : list) {
            if(offset.y >= image.size.y) break;
            resetLineHeight();

            image.fillCircleCr(offset.toF().added(-dotAreaSize/2f, FONT.size/2f), FONT.size / 6f, getTheme().textFirst);
            updateLineHeight(FONT.size);

            renderListItem(item, image, offset);
            offset.x = indent + dotAreaSize;
            offset.y += lineHeight + 10;
        }
        offset.x = indent;
        resetLineHeight();
    }

    private void renderUnorderedSublist(UnorderedSublist list, Image image, int2 offset) {
        int indent = offset.x;
        renderTextBlock(list.text(), FONT, image, indent, offset);
        offset.x = indent;
        offset.y += resetLineHeight();
        UnorderedList listPart = new UnorderedList();
        for(int i=0; i<list.childCount(); i++)
            listPart.add(list.children().get(i));
        renderUnorderedList(listPart, image, offset);
    }

    private void renderListItem(ListItem<?> item, Image image, int2 offset) {
        if(item instanceof Blockquote) renderBlockquote((Blockquote) item, image, offset);
        else if(item instanceof CodeBlock) renderCodeBlock((CodeBlock) item, image, offset);
        else if(item instanceof TextBlock) renderTextBlock((TextBlock) item, FONT, image, offset.x, offset);
        else if(item instanceof UnorderedSublist) renderUnorderedSublist((UnorderedSublist) item, image, offset);
        else if(item instanceof OrderedSublist) renderOrderedSublist((OrderedSublist) item, image, offset);
        else throw new IllegalStateException("Unknown list item: " + item);
    }

    private void renderRule(Rule rule, Image image, int2 offset) {
        image.fillRect(offset, new int2(image.size.x - offset.x, 2), RULE_COLOR);
        offset.y += 2;
        resetLineHeight();
    }

    private void renderTextBlock(Element<TextItem<?>> block, Font font, Image image, int indent, int2 offset) {
        for(TextItem<?> item : block) {
            if(offset.y >= image.size.y) break;
            renderTextItem(item, font, image, indent, offset);
        }
    }

    private void renderTextItem(TextItem<?> item, Font font, Image image, int indent, int2 offset) {
        if(item instanceof Bold) renderBold((Bold) item, font, image, indent, offset);
        else if(item instanceof Italic) renderItalic((Italic) item, font, image, indent, offset);
        else if(item instanceof Strikethrough) renderStrikethrough((Strikethrough) item, font, image, indent, offset);
        else if(item instanceof com.github.rccookie.markdown.Image) renderImage((com.github.rccookie.markdown.Image) item, font, image, indent, offset);
        else if(item instanceof Link) renderLink((Link) item, font, image, indent, offset);
        else if(item instanceof InlineCode) renderInlineCode((InlineCode) item, font, image, indent, offset);
        else if(item instanceof Space) renderSpace((Space) item, font, image, indent, offset);
        else if(item instanceof SoftBreak) renderSoftBreak((SoftBreak) item, font, image, indent, offset);
        else if(item instanceof Text) renderText((Text) item, font, image, indent, offset);
        else throw new IllegalStateException("Unknown text item: " + item);
    }

    private void renderBold(Bold bold, Font font, Image image, int indent, int2 offset) {
        renderTextBlock(bold, font.setBold(true), image, indent, offset);
    }

    private void renderItalic(Italic italic, Font font, Image image, int indent, int2 offset) {
        renderTextBlock(italic, font.setItalic(true), image, indent, offset);
    }

    private void renderStrikethrough(Strikethrough strikethrough, Font font, Image image, int indent, int2 offset) {
        renderTextBlock(strikethrough, font.setStrikethrough(true), image, indent, offset);
    }

    private void renderImage(com.github.rccookie.markdown.Image img, Font font, Image image, int indent, int2 offset) {
        Image loaded = Image.load(img.url().toString());
        if(offset.x > indent && image.size.x - offset.x < loaded.size.x) {
            offset.x = indent;
            offset.y += font.size;
        }
        if(loaded.size.x > image.size.x - indent)
            loaded = loaded.scaledToWidth(image.size.x - indent);
        image.drawImage(loaded, offset);
        offset.x += loaded.size.x;
        updateLineHeight(loaded.size.y);
    }

    private void renderLink(Link link, Font font, Image image, int indent, int2 offset) {
        Word alt = link.alt().toString().isEmpty() ? link.url() : link.alt();
        renderString(alt.toString(), font, getTheme().textAccent, image, indent, offset);
    }

    private void renderInlineCode(InlineCode code, Font font, Image image, int indent, int2 offset) {
        renderString(code.code().toString(), INLINE_CODE_FONT, INLINE_CODE_COLOR, image, indent, offset);
    }

    private void renderSpace(Space space, Font font, Image image, int indent, int2 offset) {
        offset.x += font.renderChar(' ', Color.CLEAR).size.x;
        updateLineHeight(font.size);
    }

    private void renderSoftBreak(SoftBreak softBreak, Font font, Image image, int indent, int2 offset) {
        offset.x = indent;
        offset.y += font.size;
        resetLineHeight();
    }

    private void renderText(Element<Word> text, Font font, Image image, int indent, int2 offset) {
        renderString(text.stream().map(Word::toString).collect(Collectors.joining(" ")),
                font, getTheme().textFirst, image, indent, offset);
    }

    private void renderString(String string, Font font, Color color, Image image, int indent, int2 offset) {
        RenderResult result = font.tryRenderWordsOrChars(string.replace('\n', ' '), color, image.size.x - offset.x);
        image.drawImage(result.image, offset.added(0, font == INLINE_CODE_FONT ? 3 : 0));
        offset.x += result.image.size.x;
        updateLineHeight(font.size);

        while(!result.remaining.isBlank()) {
            offset.x = indent;
            offset.y += lineHeight;
            resetLineHeight();
            if(offset.y >= image.size.y) return;

            result = font.tryRenderWordsOrChars(result.remaining.stripLeading(), color, image.size.x - offset.x);
            image.drawImage(result.image, offset.added(0, font == INLINE_CODE_FONT ? 3 : 0));
            offset.x += result.image.size.x;
            updateLineHeight(font.size);
        }
    }


    private int resetLineHeight() {
        int height = lineHeight;
        lineHeight = 0;
        return height;
    }

    private void updateLineHeight(int min) {
        lineHeight = Num.max(lineHeight, min);
    }
}
