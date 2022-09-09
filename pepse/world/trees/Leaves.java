package pepse.world.trees;


import danogl.collisions.GameObjectCollection;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;
import pepse.util.ColorSupplier;
import pepse.world.Block;

import java.awt.*;
import java.util.Random;


/**
 * This class represents the leaves that in the top of the tree (contains leaf objects)
 */
public class Leaves {

    private static final Color BASE_LEAF_COLOR = new Color(50, 200, 30);


    /**
     * This function creates the leaves that will be located in the top of the tree
     * @param gameObjects   The collection of all participating game objects.
     * @param layer  The number of the layer to which the created leaves should be added.
     * @param leavesInRow number of leaves in row and col
     * @param startLocation the location of the first leaf
     * @param rand random object with constant seed


     */
    public static void create(GameObjectCollection gameObjects, int layer, int leavesInRow,
                              Vector2 startLocation, Random rand) {

        for (int i = 0; i < leavesInRow; i++) {
            for (int j = 0; j < leavesInRow; j++) {

                //choose color for the leaf
                RectangleRenderable rectangleRenderable =
                        new RectangleRenderable(ColorSupplier.approximateColor(BASE_LEAF_COLOR));

                //create leaf
                Vector2 topLeftCorner = new Vector2(startLocation.x() + (Block.SIZE * j),
                        startLocation.y() - (i * Block.SIZE) - Block.SIZE);
                Leaf leaf = new Leaf(topLeftCorner, new Vector2(Block.SIZE, Block.SIZE),
                        rectangleRenderable, rand);
                gameObjects.addGameObject(leaf, layer);
            }

        }


    }
}
