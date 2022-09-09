package pepse.world.trees;


import danogl.collisions.GameObjectCollection;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;
import pepse.util.ColorSupplier;
import pepse.world.Block;

import java.awt.*;
import java.util.Random;

/**
 * This class represents the trunk of the tree
 */

public class Trunk {
    private static final Color BASE_TRUNK_COLOR = new Color(100, 50, 20);
    public static final String TRUNK = "trunk";
    private static final int MIN_HEIGHT_TRUNK = 2;
    private static final int MAX_HEIGHT_TRUNK = 10;

    /**
     * This function creates the trunk of the tree
     * @param gameObjects The collection of all participating game objects.
     * @param layer The number of the layer to which the created trunk should be added.
     * @param groundRange the range of the terrain
     * @param groundHeightAt the height of the ground in specific x
     * @param xLocation where to locate the trunk
     * @param rand rand random object with constant seed
     * @return num of blocks the trunk build of
     */

    public static int create(GameObjectCollection gameObjects, int layer,
                             int groundRange, float groundHeightAt,
                             float xLocation, Random rand) {

        int numOfBlocks = rand.nextInt(groundRange);
        while (numOfBlocks < MIN_HEIGHT_TRUNK || numOfBlocks > MAX_HEIGHT_TRUNK) {
            numOfBlocks = rand.nextInt(groundRange);
        }

        //create blocks for the trunk
        for (int i = 1; i < numOfBlocks + 1; i++) {
            Vector2 blockLocation = new Vector2(xLocation,
                    groundHeightAt - (Block.SIZE * i));
            RectangleRenderable rectangleRenderable =
                    new RectangleRenderable(ColorSupplier.approximateColor(BASE_TRUNK_COLOR));
            Block block = new Block(blockLocation, rectangleRenderable);
            gameObjects.addGameObject(block, layer);
            block.setTag(TRUNK);

        }
        return numOfBlocks;

    }
}
