package de.linusschmidt.hpagi.utilities;

import de.linusschmidt.hpagi.main.Main;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * @author Linus Schmidt
 * All rights reserved!
 */
public class Printer {

    public Printer() {}

    private String getTime() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
        return simpleDateFormat.format(calendar.getTime());
    }

    public void printConsole(String message) {
        System.out.println(String.format("[%s][%s]: %s", this.getTime(), Main.getFramework_Name(), message));
    }

    public void printConsoleSL(String message) {
        System.out.print(String.format("[%s][%s]: %s", this.getTime(), Main.getFramework_Name(), message));
    }

    public void printConsoleError(String message) {
        System.err.println(String.format("[%s][%s]: %s", this.getTime(), Main.getFramework_Name(), message));
    }
}
