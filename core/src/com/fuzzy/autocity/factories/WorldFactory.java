package com.fuzzy.autocity.factories;

import com.fuzzy.autocity.Settlement;
import com.fuzzy.autocity.Tile;
import com.fuzzy.autocity.World;
import com.fuzzy.autocity.exceptions.PlacementAttemptsExceededException;
import com.fuzzy.autocity.exceptions.TileOutOfBoundsException;
import com.fuzzy.autocity.generators.fractals.DiamondSquareFractal;
import com.fuzzy.autocity.terrain.Grass;
import com.fuzzy.autocity.terrain.Sand;
import com.fuzzy.autocity.terrain.Water;
import com.fuzzy.autocity.world.resources.PineTree;

import java.util.Random;

//TODO create anonymous functions to reduce copypasted nested for loops
public class WorldFactory {
    private int sizeX;
    private int sizeY;
    private World world;

    private double foliageRequiredFractalValue = 0.4;

    public double getFoliageRequiredFractalValue() {
        return foliageRequiredFractalValue;
    }

    public void setFoliageRequiredFractalValue(double foliageRequiredFractalValue) {
        this.foliageRequiredFractalValue = foliageRequiredFractalValue;
    }

    public World generate(int sizeX, int sizeY) {
        this.world = new World(sizeX, sizeY);
        this.sizeX = sizeX;
        this.sizeY = sizeY;

        this.generateHeight();
        this.generateTerrain();
        this.generateFoliage();
        this.generateSettlements();

        return this.world;
    }

    private void generateSettlements() {
        SettlementFactory settlementFactory = new SettlementFactory();

        for (int i = 0; i < 5; i++) {
            try {
                Settlement settlement = settlementFactory.generate(world);
                world.addSettlement(settlement);
            } catch (PlacementAttemptsExceededException e) {
                //
            }
        }
    }

    private void generateHeight() {
        DiamondSquareFractal diamondSquareFractal = new DiamondSquareFractal();
        diamondSquareFractal.setRoughness(0.03);
        diamondSquareFractal.setSize(Math.max(sizeX, sizeY));

        Double[][] map = diamondSquareFractal.generate();

        for (int x = 0; x < world.getWidth(); x++) {
            for (int y = 0; y < world.getHeight(); y++) {
                try {
                    world.getTile(x, y).setHeight((int) (map[x][y] * 255));
                } catch (TileOutOfBoundsException e) {
                    // Nah
                }
            }
        }
    }

    private void generateTerrain() {
        for (int x = 0; x < world.getWidth(); x++) {
            for (int y = 0; y < world.getHeight(); y++) {
                try {
                    Tile tile = world.getTile(x, y);
                    int height = tile.getHeight();

                    if (height >= 64) {
                        tile.setTerrain(new Grass());
                    } else if (height >= 16) {
                        tile.setTerrain(new Sand());
                    } else {
                        tile.setTerrain(new Water());
                    }
                } catch (TileOutOfBoundsException e) {
                    //derp
                }
            }
        }

    }

    private void generateFoliage() {
        DiamondSquareFractal diamondSquareFractal = new DiamondSquareFractal();
        diamondSquareFractal.setRoughness(0.03);
        diamondSquareFractal.setSize(Math.max(this.sizeX, this.sizeY));

        Random random = new Random();

        Double[][] map = diamondSquareFractal.generate();

        for (int x = 0; x < world.getWidth(); x++) {
            for (int y = 0; y < world.getHeight(); y++) {
                try {
                    Tile tile = world.getTile(x, y);

                    if ((map[x][y] > this.foliageRequiredFractalValue) && (random.nextDouble() <= tile.getTerrain().getRandomEntitySpawnRate())) {
                        tile.setOccupyingObject(tile.getTerrain().getRandomTerrainObject());
                    }
                } catch (TileOutOfBoundsException e) {
                    // Nah
                }
            }
        }
    }
}