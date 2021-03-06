package de.linusschmidt.hpagi.environment;

import de.linusschmidt.hpagi.utilities.Printer;

import java.awt.*;

/**
 * @author Linus Schmidt
 * All rights reserved!
 */
public class Environment implements IEnvironment {

    private int startX;
    private int startY;

    private double[] lastRecord;

    private Entity npc;
    private Entity targetNPC;
    private Dimension dimension;
    private Printer printer = new Printer();

    public Environment() {
        this.dimension = new Dimension(5, 5);

        this.buildNPC();
        this.buildTargetNPC();
        printer.printConsole(String.format("NPC: [%s][%s], TargetNPC: [%s][%s]", this.npc.getX(), this.npc.getY(), this.targetNPC.getX(), this.targetNPC.getY()));
    }

    private void buildNPC() {
        this.startX = (int) Math.round(Math.random() * this.dimension.getWidth());
        this.startY = (int) Math.round(Math.random() * this.dimension.getHeight());
        this.npc = new Entity(this.startX, this.startY);
    }

    private void buildTargetNPC() {
        int x = (int) Math.round(Math.random() * this.dimension.getWidth());
        int y = (int) Math.round(Math.random() * this.dimension.getHeight());
        this.targetNPC = new Entity(x, y);
    }

    @Override
    public double[] possibleActions() {
        return new double[] { 0, 1, 2, 3 };
    }

    @Override
    public void apply(double s) {
        double lastX = this.npc.getX();
        double lastY = this.npc.getY();
        if(s == 0) {
            this.npc.update(1, 0);
        } else if(s == 1) {
            this.npc.update(0, 1);
        } else if(s == 2) {
            this.npc.update(-1, 0);
        } else if(s == 3) {
            this.npc.update(0, -1);
        }
        this.lastRecord = new double[] {
                s,
                lastX,
                lastY,
                this.npc.getX(),
                this.npc.getY()
        };
        printer.printConsole(String.format("NPC: [%s][%s], TargetNPC: [%s][%s]", this.npc.getX(), this.npc.getY(), this.targetNPC.getX(), this.targetNPC.getY()));
    }

    @Override
    public double getReward() {
        //double reward = 1 / MathUtilities.distance(this.npc.getX(), this.targetNPC.getX(), this.npc.getY(), this.targetNPC.getY());
        if(this.npc.getX() == this.targetNPC.getX() && this.npc.getY() == this.targetNPC.getY()) {
            return 1.0D;
        } else if(this.npc.getX() < 0 || this.npc.getX() > this.dimension.getWidth() || this.npc.getY() < 0 || this.npc.getY() > this.dimension.getHeight()) {
            return 0.0D;
        }
        return 0.0D;
    }

    @Override
    public boolean isFinish() {
        if(this.npc.getX() < 0 || this.npc.getX() > this.dimension.getWidth() || this.npc.getY() < 0 || this.npc.getY() > this.dimension.getHeight()) {
            return true;
        } else return this.npc.getX() == this.targetNPC.getX() && this.npc.getY() == this.targetNPC.getY();
    }

    @Override
    public void reset() {
        this.npc.setX(this.startX);
        this.npc.setY(this.startY);
    }

    public double[] getLastRecord() {
        return this.lastRecord;
    }

    /*
    private void load() {
        this.printer.printConsole("Loading environment walls...");
        BufferedReader bufferedReader;
        try {
            bufferedReader = new BufferedReader(new FileReader(this.fileUtil.createFileInFolder("environment/wall", "wall.states")));
            String line;
            while((line = bufferedReader.readLine()) != null) {
                Wall wall = Wall.loadWall(line);
                this.walls.add(wall);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.printer.printConsole("done. Load!");
    }

    private void save() {
        this.printer.printConsole("Saving environment walls...");
        BufferedWriter bufferedWriter;
        try {
            bufferedWriter = new BufferedWriter(new FileWriter(this.fileUtil.createFileInFolder("environment/wall", "wall.state")));
            for(Wall wall : this.walls) {
                bufferedWriter.write(wall.toString());
                bufferedWriter.newLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.printer.printConsole("done. Save!");
    }
    */
}
