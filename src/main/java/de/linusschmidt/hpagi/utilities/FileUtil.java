package de.linusschmidt.hpagi.utilities;

import de.linusschmidt.hpagi.main.Main;

import java.io.File;

/**
 * @author Linus Schmidt
 * All rights reserved!
 */
public class FileUtil {

    private String mainFolderName = Main.getFramework_Name() + File.separator;

    private Printer printer;

    public FileUtil() {
        this.printer = new Printer();
        this.createMainFolder();
    }

    public File createMainFolder() {
        File folder;
        folder = new File(mainFolderName);
        try {
            if (folder.mkdirs()) {
                folder.createNewFile();
                this.printer.printConsole(String.format("Main-Folder[%s] created!", folder.getName()));
            } else {
                this.printer.printConsoleError(String.format("Main-Folder[%s] cannot created or already exist!", folder.getName()));
            }
        } catch (Exception ignored) {}
        return folder;
    }

    public File createFileInFolder(String folderPath, String fileName) {
        this.createFolder(folderPath);
        File file;
        file = new File(getMainFolderName() + folderPath + File.separator + fileName);
        try {
            if (!file.exists()) {
                file.createNewFile();
                this.printer.printConsole(String.format("File[%s] created!", file.getName()));
            } else {
                this.printer.printConsoleError(String.format("File[%s] cannot created or already exist!", file.getName()));
            }
        } catch (Exception ignored) {}
        return  file;
    }

    public File createFolder(String folderName) {
        File folder;
        folder = new File(getMainFolderName() + folderName + File.separator);
        try {
            if (folder.mkdirs()) {
                folder.createNewFile();
                this.printer.printConsole(String.format("Folder[%s] created!", folder.getName()));
            } else {
                this.printer.printConsoleError(String.format("Folder[%s] cannot created or already exist!", folder.getName()));
            }
        } catch (Exception ignored) {}
        return  folder;
    }

    public File createFile(String fileName) {
        File file;
        file = new File(getMainFolderName() + File.separator + fileName);
        try {
            if(!file.exists()) {
                file.createNewFile();
                this.printer.printConsole(String.format("File[%s] created!", file.getName()));
            } else {
                this.printer.printConsoleError(String.format("File[%s] cannot created or already exist!", file.getName()));
            }
        } catch (Exception ignored) {}
        return file;
    }

    private String getMainFolderName() {
        return this.mainFolderName;
    }
}
