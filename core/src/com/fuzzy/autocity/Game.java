package com.fuzzy.autocity;

import com.fuzzy.autocity.exceptions.TileOutOfBoundsException;
import com.fuzzy.autocity.factories.WorldFactory;
import com.fuzzy.autocity.simulation.Simulation;

public class Game extends Thread implements Invokable {
    private World world;
    private boolean isRunning = true;
    private long lastloop = System.nanoTime();
    private double delta = 0;
    private Simulation simulation;

    public Game() {
        this.startGame();
    }

    public void restartGame() {
        this.simulation = null;
        this.world = null;
        this.startGame();
    }

    public void startGame() {
        System.out.println("Generating world...");
        WorldFactory builder = new WorldFactory();
        this.world = builder.generate(155, 90);
        this.simulation = new Simulation(this);
    }

    private void newMap(int x, int y) {
        System.out.println("Generating world...");
        WorldFactory builder = new WorldFactory();
        this.world = builder.generate(x, y);
        this.simulation = new Simulation(this);
    }

    public World getWorld() {
        return this.world;
    }

    public void run() {
        this.main();
    }

    /**
     * Main game loop
     */
    private void main() {
        while (isRunning) {

            long now = System.nanoTime();
            long updateLength = now - lastloop;
            delta += ((double) updateLength / 1000000000);
            lastloop = now;
//TODO: Do we really want the game logic to only update once a second? That seems awfully slow.
            if (delta >= 1) {
                this.onTick();
                delta = 0;
            } else {
                Thread.yield();
                try {
                    Thread.sleep(1);
                } catch (Exception e) {
                }
            }

            this.onClose();
        }
    }

    /**
     * Things to do on game exit
     */
    private void onClose() {

    }

    /**
     * Things to do on every game tick
     */
    private void onTick() {
        this.simulation.onTick();
    }

    @Override
    public void Execute(String command) {
        String[] tmp = command.split(delimiter);
        switch (tmp[1]) {
            case "newmap":
                try {
                    newMap(Integer.valueOf(tmp[2]), Integer.valueOf(tmp[3]));
                } catch (NumberFormatException nfe) {
                    System.out.println("Invalid command.");
                    System.out.println(" @" + Devmode.class.getName());
                }
        }
    }
}