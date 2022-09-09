package pepse.world;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.collisions.Layer;
import danogl.components.CoordinateSpace;
import danogl.gui.rendering.Renderable;
import danogl.gui.rendering.TextRenderable;
import danogl.util.Counter;
import danogl.util.Vector2;

import java.awt.*;


/**
 * Display a graphic object on the game window showing a numeric count of energy left.
 */
public class NumericEnergyCounter extends GameObject {

    private final Counter energyCounter;
    private final GameObjectCollection gameObjectCollection;
    private static final TextRenderable renderable = new TextRenderable("");
    private static final String ENERGY_NUM_MSG = "Num Of Energy: %d";

    /**
     * Construct a new GameObject instance.
     *
     * @param topLeftCorner Position of the object, in window coordinates (pixels).
     *                      Note that (0,0) is the top-left corner of the window.
     * @param dimensions    Width and height in window coordinates.
     */
    public NumericEnergyCounter(Counter energyCounter, Vector2 topLeftCorner, Vector2 dimensions,
                                GameObjectCollection gameObjectCollection) {
        super(topLeftCorner, dimensions, renderable);
        this.energyCounter = energyCounter;
        this.gameObjectCollection = gameObjectCollection;
        renderable.setColor(Color.WHITE);
        renderable.setString(String.format(ENERGY_NUM_MSG, this.energyCounter.value()));
        this.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
    }

    /**
     * Called once per frame. Any logic is put here.
     * @param deltaTime The time, in seconds, that passed since the last invocation of this method .
     */
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        renderable.setString(String.format(ENERGY_NUM_MSG, this.energyCounter.value()));
    }

}
