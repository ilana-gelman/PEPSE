package pepse.world.daynight;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.components.CoordinateSpace;
import danogl.components.Transition;
import danogl.gui.rendering.OvalRenderable;
import danogl.util.Vector2;

import java.awt.*;

/**
 * Represents the sun - moves across the sky in an elliptical path.
 */
public class Sun {

    private static final String SUN = "sun";
    private static final int SUN_SIZE = 100;

    /**
     * This function creates a yellow circle that moves in the sky in an elliptical path
     * (in camera coordinates).
     *
     * @param gameObjects      The collection of all participating game objects.
     * @param layer            The number of the layer to which the created sun should be added.
     * @param windowDimensions The dimensions of the windows.
     * @param cycleLength      The amount of seconds it should take the created game object to complete a
     *                         full cycle.
     * @return A new game object representing the sun.
     */
    public static GameObject create(GameObjectCollection gameObjects, int layer,
                                    Vector2 windowDimensions, float cycleLength) {
        OvalRenderable ovalRenderable = new OvalRenderable(Color.YELLOW);
        GameObject sun = new GameObject(Vector2.ZERO, new Vector2(SUN_SIZE, SUN_SIZE), ovalRenderable);
        sun.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        sun.setTag(SUN);
        gameObjects.addGameObject(sun, layer);
        float windowCenterX = windowDimensions.x() / 2;
        float windowCenterY = windowDimensions.y() / 2;
        Vector2 centerVec = new Vector2(windowCenterX, windowCenterY);
        new Transition<Float>(sun,
                theta -> sun.setCenter(centerVec.add(new Vector2((float) (Math.sin(theta)) * windowCenterX,
                        (float) Math.cos(theta) * windowDimensions.y() * (3 / 5f)))),
                0f,
                (float) Math.PI * 2,
                Transition.LINEAR_INTERPOLATOR_FLOAT,
                cycleLength,
                Transition.TransitionType.TRANSITION_LOOP,
                null);
        return sun;
//

    }


}
