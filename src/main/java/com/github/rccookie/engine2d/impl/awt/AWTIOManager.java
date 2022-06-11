package com.github.rccookie.engine2d.impl.awt;

import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import com.github.rccookie.engine2d.impl.IOManager;
import com.github.rccookie.engine2d.util.RuntimeIOException;
import com.github.rccookie.util.Console;
import com.github.rccookie.util.Future;
import com.github.rccookie.util.PrecomputedFutureImpl;
import com.github.rccookie.util.UncheckedException;

import org.jetbrains.annotations.NotNull;

public class AWTIOManager implements IOManager {

    @Override
    public String read(String file) throws RuntimeIOException {
        try {
            //noinspection ConstantConditions
            return new String(AWTIOManager.class.getClassLoader().getResourceAsStream("resources/" + file).readAllBytes());
        } catch(Exception e) {
            throw new RuntimeIOException(e);
        }
    }

    @Override
    public String @NotNull [] listFiles() {
        return Arrays.stream(System.getProperty("java.class.path", ".")
                .split(System.getProperty("path.separator")))
                .flatMap(AWTIOManager::getResources)
                .map(p -> p.replace('\\', '/'))
                .filter(p -> p.startsWith("resources/"))
                .toArray(String[]::new);
    }

    @Override
    public Future<String> getClipboard() {
        try {
            return new PrecomputedFutureImpl<>((String) Toolkit.getDefaultToolkit()
                    .getSystemClipboard().getContents(this)
                    .getTransferData(DataFlavor.stringFlavor));
        } catch (Exception e) {
            Console.error(e);
            return new PrecomputedFutureImpl<>("");
        }
    }

    @Override
    public void setClipboard(@NotNull String content) {
        try {
            StringSelection str = new StringSelection(content);
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(str, str);
        } catch(Exception e) {
            throw new UncheckedException(e);
        }
    }



    private static Stream<String> getResources(String path) {
        File file = new File(path);
        return file.isDirectory() ?
                getResourcesFromDirectory(file, file.toPath()) :
                getResourcesFromJar(file);
    }

    private static Stream<String> getResourcesFromDirectory(File directory, Path root) {
        //noinspection ConstantConditions
        return Arrays.stream(directory.listFiles())
                .flatMap(f -> f.isDirectory() ? getResourcesFromDirectory(f, root) : Stream.of(root.relativize(f.toPath()).toString()));
    }

    private static Stream<String> getResourcesFromJar(File file) {
        try(ZipFile jar = new ZipFile(file)) {
            // Read all files before closing
            List<ZipEntry> files = new ArrayList<>();
            jar.stream().forEach(files::add);
            return files.stream().map(ZipEntry::getName);
        } catch(final Exception e){
            throw new RuntimeIOException(e);
        }
    }
}
