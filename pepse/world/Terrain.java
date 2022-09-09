package pepse.world;

import danogl.collisions.GameObjectCollection;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;
import pepse.util.ColorSupplier;
import pepse.util.PerlinNoise;

import java.awt.*;

/**
 * Responsible for the creation and management of terrain.
 */
public class Terrain {
    private static final Color BASE_GROUND_COLOR = new Color(212, 123, 74);
    private static final int TERRAIN_DEPTH = 25;
    public static final String TOP_GROUND = "topGround";
    public static final String BOTTOM_GROUND = "bottomGround";

    private GameObjectCollection gameObjects;
    private int groundLayer;
    private Vector2 windowDimensions;
    private float groundHeightAtX0;
    private PerlinNoise perlinNoise;

    /**
     * constructor
     *
     * @param gameObjects      The collection of all participating game objects.
     * @param groundLayer      The number of the layer to which the created ground objects should be added.
     * @param windowDimensions The dimensions of the windows.
     * @param seed             A seed for a random number generator.
     */
    public Terrain(danogl.collisions.GameObjectCollection gameObjects,
                   int groundLayer,
                   Vector2 windowDimensions, int seed) {
        this.gameObjects = gameObjects;
        this.groundLayer = groundLayer;
        this.windowDimensions = windowDimensions;
        this.groundHeightAtX0 = windowDimensions.y()*(2/3f);
        this.perlinNoise = new PerlinNoise(seed);
    }

    /**
     * This method creates terrain in a given range of x-values.
     *
     * @param minX The lower bound of the given range (will be rounded to a multiple of Block.SIZE).
     * @param maxX The upper bound of the given range (will be rounded to a multiple of Block.SIZE).
     */
    public void createInRange(int minX, int maxX) {
        int minRange = (int) (minX - (minX % Block.SIZE));
        int maxRange = (int) (maxX - (maxX % Block.SIZE));

        for (int i = minRange; i <= maxRange; i = (int) (i + Block.SIZE)) {
            int blocksHeight = (int) (((int) (groundHeightAt(i) / Block.SIZE)) * Block.SIZE);
            for (int j = blocksHeight; j < blocksHeight + (TERRAIN_DEPTH * Block.SIZE); j += Block.SIZE) {
                RectangleRenderable rectangleRenderable =
                        new RectangleRenderable(ColorSupplier.approximateColor(BASE_GROUND_COLOR));
                Block block = new Block(
                        new Vector2(i, j),
                        rectangleRenderable);

                //separate between the layers of the top ground and the other ground
                if ((j == blocksHeight) || (j == (blocksHeight + Block.SIZE))) {
                    gameObjects.addGameObject(block, this.groundLayer);
                    block.setTag(TOP_GROUND);
                } else {
                    gameObjects.addGameObject(block, this.groundLayer + 5);
                    block.setTag(BOTTOM_GROUND);

                }
            }
        }
    }

    /**
     * This method return the ground height at a given location.
     *
     * @param x A number.
     * @return The ground height at the given location.
     */
    public float groundHeightAt(float x) {
        float groundHeight = (float) (Block.SIZE * perlinNoise.noise(x / Block.SIZE) * 21);
        if (groundHeight < 0 ) {
            return groundHeightAtX0;
        } else if (groundHeight + groundHeightAtX0 > windowDimensions.y()) {
            return windowDimensions.y() - 90;
        }
        return groundHeight + groundHeightAtX0;
    }

}
