package de.linusschmidt.hpagi.environment;

import de.linusschmidt.hpagi.utilities.MathUtilities;

import java.awt.*;

public class Environment implements IEnvironment {

    private Entity npc;
    private Entity targetNPC;
    private Dimension dimension;

    public Environment() {
        this.dimension = new Dimension(5, 5);

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
        return new double[] { 0, 1 };
    }

    @Override
    public void apply(double s) {
        if(s == 0) {
            this.npc.update(1, 0);
        } else if(s == 1) {
            this.npc.update(0, 1);
        }
    }

    @Override
    public double getReward() {
        return 1.0D / MathUtilities.distance(this.npc.getX(), this.targetNPC.getX(), this.npc.getY(), this.targetNPC.getY());
    }

    @Override
    public boolean isFinish() {
        if(this.npc.getX() < 0 || this.npc.getX() > this.dimension.getWidth() || this.npc.getY() < 0 || this.npc.getY() > this.dimension.getHeight()) {
            return true;
        } else return this.getReward() == 1;
    }

    @Override
    public void reset() {
        int x = (int) Math.round(Math.random() * this.dimension.getWidth());
        int y = (int) Math.round(Math.random() * this.dimension.getHeight());
        this.npc.setX(x);
        this.npc.setY(y);
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
