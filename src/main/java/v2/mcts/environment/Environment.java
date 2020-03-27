package v2.mcts.environment;

import de.linusschmidt.hpagi.utilities.MathUtilities;
import de.linusschmidt.hpagi.utilities.Printer;

import java.awt.*;
import java.util.Arrays;
import java.util.List;

/**
 * @author Linus Schmidt
 */
public final class Environment implements IEnvironment<Double> {

    private int startX;
    private int startY;

    private Entity npc;
    private Entity targetNPC;
    private Dimension dimension;

    public Environment() {
        this.dimension = new Dimension(50, 50);

        this.buildNPC();
        this.buildTargetNPC();

        Printer printer = new Printer();
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
    public List<Double> getStates() {
        return Arrays.asList(0.0D, 1.0D, 2.0D, 3.0D);
    }

    @Override
    public void apply(Double state) {
        if(state == 0) {
            this.npc.update(1, 0);
        } else if(state == 1) {
            this.npc.update(0, 1);
        } else if(state == 2) {
            this.npc.update(-1, 0);
        } else if(state == 3) {
            this.npc.update(0, -1);
        }
    }

    @Override
    public double getReward() {
        double reward = 1.0 / (1.0 + MathUtilities.distance(this.npc.getX(), this.npc.getY(), this.targetNPC.getX(), this.targetNPC.getY()));
        if(this.npc.getX() == this.targetNPC.getX() && this.npc.getY() == this.targetNPC.getY()) {
            return 1.0D;
        } else if(this.npc.getX() < 0 || this.npc.getX() > this.dimension.getWidth() || this.npc.getY() < 0 || this.npc.getY() > this.dimension.getHeight()) {
            return 0.0;
        }
        return reward;
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
