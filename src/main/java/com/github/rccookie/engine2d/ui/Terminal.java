package com.github.rccookie.engine2d.ui;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import com.github.rccookie.engine2d.Execute;
import com.github.rccookie.engine2d.UIObject;
import com.github.rccookie.engine2d.image.Font;
import com.github.rccookie.engine2d.image.Image;
import com.github.rccookie.engine2d.image.ThemeColor;
import com.github.rccookie.engine2d.ui.util.TypeWriter;
import com.github.rccookie.engine2d.util.ColorProperty;
import com.github.rccookie.geometry.performance.int2;
import com.github.rccookie.util.Console;
import com.github.rccookie.util.UncheckedException;

import org.jetbrains.annotations.Nullable;

public class Terminal extends UIObject {

    public final ColorProperty textColor = new ColorProperty(this, ThemeColor.TEXT_FIRST);
    public final ColorProperty backgroundColor = new ColorProperty(this, t -> t.first);

    private final Process process;
    private final StringBuilder output = new StringBuilder();
    private final TypeWriter writer = new TypeWriter();

    public Terminal(@Nullable UIObject parent, String cmd) {
        super(parent);
        onParentSizeChange.add(this::modified);

        try {
            process = Runtime.getRuntime().exec(cmd);
        } catch(IOException e) {
            throw new UncheckedException(e);
        }

        writer.onSubmit.add(s -> {
            s += '\n';
            writer.setString("");
            output.append(s);
            process.getOutputStream().write(s.getBytes());
            process.getOutputStream().flush();
        });
        input.keyPressed.addConsuming(writer::keyTyped);
        writer.onChange.add(this::modified);

        new Thread(() -> {
            try(Reader r = new InputStreamReader(this.process.getInputStream())) {
                int c;
                while((c = r.read()) != -1) {
                    char c_ = (char) c;
                    Execute.synced(() -> output(c_));
                }
            } catch(IOException e) {
                Console.error(e);
            }
        }).start();
        new Thread(() -> {
            try(Reader r = new InputStreamReader(this.process.getErrorStream())) {
                int c;
                while((c = r.read()) != -1) {
                    char c_ = (char) c;
                    Execute.synced(() -> output(c_));
                }
            } catch(IOException e) {
                Console.error(e);
            }
        }).start();
    }

    @Override
    protected @Nullable Image generateImage() {
        Image image = new Image(clampSize(getParent().getSize()), backgroundColor.get());
        // Draw text
        Image textImage = Font.MONOSPACE.render(output + "" + writer, textColor.get(), image.size.x);
        if(textImage.size.y <= image.size.y) image.drawImage(textImage, int2.zero);
        else image.drawImage(textImage, new int2(0, image.size.y - textImage.size.y));

        //        int h = 0;
//        List<String> wrappedLines = new ArrayList<>();
//        outer: for(String line : (output + "" + writer).split("\r?\n")) {
//            if(line.isEmpty())
//                wrappedLines.add("\n");
//            else {
//                do {
//                    RenderResult result = Font.MONOSPACE.tryRenderChars(line, textColor.get(), image.size.x);
//                    image.drawImage(result.image, new int2(0, h));
//                    wrappedLines.add(result.rendered);
//                    line = result.remaining;
//                    if((h += result.image.size.y) >= image.size.y) break outer;
//                } while(!line.isEmpty());
//                int last = wrappedLines.size()-1;
//                wrappedLines.set(last, wrappedLines.get(last) + "\n");
//            }
//        }
//        this.renderedLines = wrappedLines.toArray(new String[0]);
        return image;
    }

    private void output(char c) {
        output.append(c);
        modified();
    }
}
