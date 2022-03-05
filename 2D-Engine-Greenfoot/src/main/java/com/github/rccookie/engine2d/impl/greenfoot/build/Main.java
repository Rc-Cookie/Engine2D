package com.github.rccookie.engine2d.impl.greenfoot.build;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.function.Predicate;

import com.github.rccookie.util.ArgsParser;
import com.github.rccookie.util.Console;

public final class Main {

    private static final String[] DEPENDENCIES = {
            "C:\\Users\\Leon\\Documents\\Code\\Java\\packages\\2D-Engine",
            "C:\\Users\\Leon\\Documents\\Code\\Java\\packages\\2D-Engine\\2D-Engine-Greenfoot",
            "C:\\Users\\Leon\\Documents\\Code\\Java\\packages\\event",
            "C:\\Users\\Leon\\Documents\\Code\\Java\\packages\\geometry",
            "C:\\Users\\Leon\\Documents\\Code\\Java\\packages\\json",
            "C:\\Users\\Leon\\Documents\\Code\\Java\\packages\\util"
    };

    private Main() {
        throw new UnsupportedOperationException();
    }

    public static void main(String[] args) throws IOException {
        ArgsParser parser = new ArgsParser();
        parser.addDefaults();
        parser.addOption('d', "dir", true, "Root directory of the project");
        parser.addOption(null, "dep", true, "Additional dependencies, seperated by ';'");
        parser.addOption(null, "noMaven", false, "Indicates the project has no maven layout");
        Map<String, String> options = parser.parse(args);

        File project = new File(options.getOrDefault("dir", ".")).getCanonicalFile();
        String name = project.getName();
        boolean maven = !options.containsKey("noMaven");

        Console.map("Building project", name);
        Console.map("Maven", !options.containsKey("noMaven"));

        File buildDir = new File(project, name);
        buildDir.mkdir();

        String[] deps;
        if(options.containsKey("dep") && !options.get("dep").isBlank()) {
            String[] extraDeps = options.get("dep").split(";");
            deps = new String[DEPENDENCIES.length + extraDeps.length];
            System.arraycopy(DEPENDENCIES, 0, deps, 0, DEPENDENCIES.length);
            System.arraycopy(extraDeps, 0, deps, DEPENDENCIES.length, extraDeps.length);
        }
        else deps = DEPENDENCIES;

        Console.mapDebug("Dependencies", deps);

        for(String dep : deps) {
            Console.info("Copying dependency '{}'...", dep.substring(Math.max(dep.lastIndexOf('\\'), dep.lastIndexOf('/')) + 1));
            copyDir(new File(dep, "target\\classes"), buildDir, f -> f.getName().endsWith(".java") || f.getName().endsWith(".class"));
        }

        Console.info("Copying project files...");
        copyDir(new File(project, maven ? "src\\main\\java" : "src"), buildDir, f -> f.getName().endsWith(".java") || f.getName().endsWith(".class"));
        copyDir(new File(project, maven ? "target\\classes" : "out\\production\\" + name), buildDir, f -> f.getName().endsWith(".java") || f.getName().endsWith(".class"));

        if(maven) {
            File resources = new File(project, "src\\main\\resources");
            if(resources.exists()) {
                Console.info("Copying resources...");
                copyDir(resources, buildDir, f -> !f.getName().equals("standalone.properties"));
            }
        }

        File gFile = new File(buildDir, "project.greenfoot");
        if(!gFile.exists()) {
            Console.info("Creating project.greenfoot file...");
            Files.writeString(gFile.toPath(), "height=611\n" +
                    "project.charset=windows-1252\n" +
                    "simulation.speed=50\n" +
                    "version=3.0.0\n" +
                    "width=853\n" +
                    "world.lastInstantiated=GreenfootLoader\n" +
                    "xPosition=662\n" +
                    "yPosition=292\n");
        }

        Console.info("Done.");
    }

    private static void copyDir(File src, File target, Predicate<File> filter) throws IOException {
        Path srcPath = src.toPath(), targetPath = target.toPath();
        Files.walk(srcPath).forEach(f -> {
            try {
                File file = f.toFile();
                if(file.isFile() && filter.test(file)) {
                    Path currentTarget = targetPath.resolve(srcPath.relativize(f));
                    new File(currentTarget.toFile().getParent()).mkdirs();
                    Files.copy(f, currentTarget, StandardCopyOption.REPLACE_EXISTING);
                }
            } catch(IOException e) {
                e.printStackTrace();
            }
        });
    }
}
