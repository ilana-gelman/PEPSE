package pepse.world.trees;


import danogl.collisions.GameObjectCollection;
import danogl.collisions.Layer;
import danogl.util.Vector2;
import pepse.world.Block;
import pepse.world.Terrain;

import java.util.HashMap;
import java.util.Objects;
import java.util.Random;
import java.util.function.Function;
import java.util.function.UnaryOperator;


/**
 * Responsible for the creation and management of trees.
 */
public class Tree {

    private static final int TRUNK_LAYER = Layer.STATIC_OBJECTS + 8;
    private static final int LEAF_LAYER = Layer.STATIC_OBJECTS + 10;
    private static final int CREATE_TREE_BOUND = 101;
    private static final int MAX_TRUNK_HEIGHT = 10;
    private final int seed;
    private GameObjectCollection gameObjects;
    private Vector2 windowDimensions;
    private Function<Float, Float> groundHeightFunc;

    /**
     * constructor
     * @param seed   A seed for a random number generator.
     * @param gameObjects The collection of all participating game objects.
     * @param windowDimensions The dimensions of the windows.
     * @param groundHeightFunc callback that returns the ground height at x location
     */

    public Tree(int seed, GameObjectCollection gameObjects, Vector2 windowDimensions,
                Function<Float, Float> groundHeightFunc) {

        this.gameObjects = gameObjects;
        this.windowDimensions = windowDimensions;
        this.groundHeightFunc = groundHeightFunc;
        this.seed = seed;

    }

    /**
     * This method creates trees in a given range of x-values.
     *
     * @param minX The lower bound of the given range (will be rounded to a multiple of Block.SIZE).
     * @param maxX The upper bound of the given range (will be rounded to a multiple of Block.SIZE).
     */
    public void createInRange(int minX, int maxX) {
        int minRange = (int) (minX - (minX % Block.SIZE));
        int maxRange = (int) (maxX - (maxX % Block.SIZE));
        int numOfCols = (int) (((maxRange - minRange) / Block.SIZE) + 1);
        for (int i = 0; i < numOfCols; i++) {
            float xLocation = minRange + (i * Block.SIZE);
            Random rand = new Random(Objects.hash(xLocation, seed));
            int numRand = rand.nextInt(CREATE_TREE_BOUND);
            if (numRand < MAX_TRUNK_HEIGHT) {

                //we want to create tree in the current location
                //create trunk
                float groundHeight = this.groundHeightFunc.apply(xLocation);
                groundHeight = (int) (((int) (groundHeight / Block.SIZE)) * Block.SIZE);

                int maxOfPossibleBlocks =
                        (int) ((groundHeight - 10) / Block.SIZE);
                int numOfBlocksInTrunk = Trunk.create(gameObjects, TRUNK_LAYER,
                        maxOfPossibleBlocks, groundHeight, xLocation, rand);

                //create leaves
                int leavesInRow = numOfBlocksInTrunk / 2 + 1;
                Leaves.create(gameObjects, LEAF_LAYER, leavesInRow,
                        new Vector2((float) (xLocation + (Block.SIZE * 0.5f) -
                                (0.5 * Block.SIZE * leavesInRow)),
                                groundHeight - (leavesInRow * Block.SIZE)), rand);

            }
        }
    }
}
