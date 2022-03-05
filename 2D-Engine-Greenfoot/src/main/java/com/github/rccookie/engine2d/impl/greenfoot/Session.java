package com.github.rccookie.engine2d.impl.greenfoot;

import java.awt.image.BufferedImage;
import java.lang.reflect.Field;

import com.github.rccookie.util.Console;

import greenfoot.platforms.standalone.GreenfootUtilDelegateStandAlone;
import greenfoot.util.GreenfootUtil;

public enum Session {
    ONLINE,
    APPLICATION,
    STANDALONE;

    private static Session current = null;

    public static Session current() {
        return current;
    }

    static void calcCurrent(GreenfootStartupPrefs prefs) {
        if(prefs.sessionOverride != null) {
            current = prefs.sessionOverride;
            Console.Config.coloredOutput = current == STANDALONE;
            Console.split("Session: {} (overridden)", current);
            return;
        }
        try {
            new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
            try {
                Field delegateField = GreenfootUtil.class.getDeclaredField("delegate");
                delegateField.setAccessible(true);
                current = delegateField.get(null) instanceof GreenfootUtilDelegateStandAlone ? Session.STANDALONE : Session.APPLICATION;
            } catch(Exception e) {
                current = Session.APPLICATION;
            }
        } catch(Throwable t) {
            current = Session.ONLINE;
        }
        Console.Config.coloredOutput = current == STANDALONE;
        Console.split("Session: {}", current);
    }
}
