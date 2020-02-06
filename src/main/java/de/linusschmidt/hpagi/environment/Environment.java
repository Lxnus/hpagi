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

public class Environment extends JPanel {

    private Entity npc;
    private Agent agent;
    private Printer printer;
    private FileUtil fileUtil;
    private Dimension dimension;

    private Entity targetNPC;

    private List<Wall> walls;

    public Environment(Agent agent) {
        this.agent = agent;

        this.agent.setEnvironment(this);

        this.printer = new Printer();
        this.fileUtil = new FileUtil();
        this.dimension = new Dimension(500, 500);

        this.walls = new ArrayList<>();

        this.buildNPC();
        this.buildTargetNPC();
        this.load();
        this.draw();
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

    private void draw() {
        JFrame frame = new JFrame();
        frame.add(this);
        frame.setSize(this.getPreferredSize());
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setVisible(true);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        double[] state = agent.getBinaryState();
        int x = state[0] == 1 ? 1 : state[1] == 1 ? -1 : 0;
        int y = state[2] == 1 ? 1 : state[3] == 1 ? -1 : 0;
        if(this.npc.getX() > 0 && this.npc.getX() < this.dimension.getWidth() && this.npc.getY() > 0 && this.npc.getY() < this.dimension.getHeight()) {
            this.npc.update(x * 10, y * 10);
        }

        g.setColor(Color.BLACK);
        for(Wall wall : this.walls) {
            if(wall.isVisible()) {
                g.fillRect(wall.getX(), wall.getY(), 10, 10);
            }
        }
        g.setColor(Color.GREEN);
        g.fillRect(this.npc.getX(), this.npc.getY(), 10, 10);

        g.setColor(Color.RED);
        g.fillRect(this.targetNPC.getX(), this.targetNPC.getY(), 10, 10);

        try {
            Thread.sleep(10);
        } catch (Exception ignored) {}
        this.repaint();
    }

    @Override
    public Dimension getPreferredSize() {
        return this.dimension;
    }

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

    public double getReward() {
        return 1.0D / MathUtilities.distance(this.npc.getX(), this.targetNPC.getX(), this.npc.getY(), this.targetNPC.getY());
    }

    /*
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
