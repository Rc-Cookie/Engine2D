package com.github.rccookie.engine2d.util.awt;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UncheckedIOException;

import com.github.rccookie.engine2d.Application;
import com.github.rccookie.engine2d.ILoader;
import com.github.rccookie.engine2d.impl.ApplicationLoader;
import com.github.rccookie.engine2d.impl.awt.AWTImplementation;
import com.github.rccookie.util.Args;
import com.github.rccookie.util.ArgsParser;
import com.github.rccookie.util.Console;

import org.jetbrains.annotations.NotNull;

/**
 * AWT implementation of {@link ApplicationLoader}. Example usage:
 * <pre>
 *     public static void main(String[] args) {
 *         new AWTApplicationLoader(new MyLoader(), new AWTStartupPrefs());
 *     }
 * </pre>
 */
public class AWTApplicationLoader implements ApplicationLoader {

    /**
     * Creates a new AWTApplication loader and launches the application.
     * If async starting is disabled in the prefs this method will block
     * until the application is closed.
     *
     * @param loader The loader used to load the application
     * @param prefs Startup preferences
     */
    public AWTApplicationLoader(@NotNull ILoader loader, @NotNull AWTStartupPrefs prefs) {

        prefs = prefs.clone();
        evalArgs(prefs);

        loader.initialize();

        Application.setup(new AWTImplementation(prefs), prefs.parallel);

        loader.load();

        if(prefs.async)
            Application.startAsync();
        else Application.start();
    }

    private static void evalArgs(AWTStartupPrefs prefs) {
        ArgsParser parser = new ArgsParser();
        parser.addDefaults();
        parser.addOption(null, "logfile", null, "Sets the output file for the application. If not specified, this will be AppData/Local/<Application name>/out.log")
                .action(() -> setLogFile(System.getProperty("user.home") + "/AppData/Local/" + prefs.name + "/out.log"))
                .action(AWTApplicationLoader::setLogFile);
        parser.addOption(null, "logHeader", true, "Logs the specified header on startup. Useful to distinguish between sessions in a log file");
        Args options = parser.parse(prefs.args);

        // Give chance to suppress message with args
        Console.mapDebug("Program args", String.join(" ", prefs.args));
        if(options.is("logHeader"))
            Console.logTime(options.get("logHeader"));
    }

    private static void setLogFile(String file) {
        try {
            PrintStream oldOut = System.out;
            new File(file).getParentFile().mkdirs();
            System.setOut(new PrintStream(new FileOutputStream(file, true)));
            System.setErr(System.out);
            Console.Config.colored = false;
            Console.Config.out.setOut(System.out);
            oldOut.println("Logs redirected to " + file);
        } catch(IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
