package de.linusschmidt.hpagi.utilities;

import de.linusschmidt.hpagi.main.Main;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * @author Linus Schmidt
 *
 * "Printer" Klasse.
 * Verwendung:
 *      - Mit dieser Klasse lasse ich alle Sequenzen in einem einheitlichen Format ausgeben.
 *        In diesem Fall wird folgenes Format verwendet:
 *        ==> [<Time>][<Project>]: <Message>
 */
public class Printer {

    public Printer() {}

    public String getTime() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
        return simpleDateFormat.format(calendar.getTime());
    }

    public void printConsole(String message) {
        System.out.println("[" + this.getTime() + "][" + Main.getFramework_Name() + "]: " + message);
    }

    public void printConsoleError(String message) {
        System.out.println("[" + this.getTime() + "][" + Main.getFramework_Name() + "]: " + message);
    }
}
