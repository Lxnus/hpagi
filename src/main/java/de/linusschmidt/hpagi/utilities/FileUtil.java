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
                this.printer.printConsole("Main-Folder[" + folder.getName() + "] created!");
            } else {
                this.printer.printConsoleError("Main-Folder[" + folder.getName() + "] cannot created or already exist!");
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
                this.printer.printConsole("File[" + file.getName() + "] created!");
            } else {
                this.printer.printConsoleError("File[" + file.getName() + "] cannot created or already exist!");
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
                this.printer.printConsole("Folder[" + folder.getName() + "] created!");
            } else {
                this.printer.printConsoleError("Folder[" + folder.getName() + "] cannot created or already exist!");
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
                this.printer.printConsole("File[" + file.getName() + "] created!");
            } else {
                this.printer.printConsoleError("File[" + file.getName() + "] cannot created or already exist!");
            }
        } catch (Exception ignored) {}
        return file;
    }

    private String getMainFolderName() {
        return this.mainFolderName;
    }
}
