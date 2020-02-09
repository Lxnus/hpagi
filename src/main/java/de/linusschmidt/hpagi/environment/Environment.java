package de.linusschmidt.hpagi.environment;

import de.linusschmidt.hpagi.agent.Agent;
import de.linusschmidt.hpagi.utilities.FileUtil;
import de.linusschmidt.hpagi.utilities.MathUtilities;
import de.linusschmidt.hpagi.utilities.Printer;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Environment implements IEnvironment {

    private Entity npc;
    private Agent agent;
    private Dimension dimension;

    private Entity targetNPC;

    public Environment(Agent agent) {
        this.agent = agent;

        this.agent.setEnvironment(this);

        this.dimension = new Dimension(500, 500);

        this.buildNPC();
        this.buildTargetNPC();
    }

    private void buildNPC() {
        int x = (int) Math.round(Math.random() * this.dimension.getWidth());
        int y = (int) Math.round(Math.random() * this.dimension.getHeight());
        this.npc = new Entity(x, y);
    }

    private void buildTargetNPC() {
        int x = (int) Math.round(Math.random() * this.dimension.getWidth());
        int y = (int) Math.round(Math.random() * this.dimension.getHeight());
        this.targetNPC = new Entity(x, y);
    }

    @Override
    public double[] possibleActions() {
        return new double[0];
    }

    @Override
    public void apply(double s) {}

    @Override
    public double getReward() {
        return 1.0D / MathUtilities.distance(this.npc.getX(), this.targetNPC.getX(), this.npc.getY(), this.targetNPC.getY());
    }

    @Override
    public boolean isFinish() {
        return false;
    }

    @Override
    public void reset() {}

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
