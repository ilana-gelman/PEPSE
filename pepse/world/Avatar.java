package pepse.world;

import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.collisions.GameObjectCollection;
import danogl.gui.ImageReader;
import danogl.gui.UserInputListener;
import danogl.gui.rendering.*;
import danogl.util.Vector2;
import java.awt.event.KeyEvent;

/**
 * An avatar can move around the world.
 */
public class Avatar extends GameObject {

    private static final float VELOCITY_X = 300;
    private static final float VELOCITY_Y = -300;
    private static final float GRAVITY = 350;
    private static final String[] WALK_PATHS = {"pepse/assets/walk1.png", "pepse/assets/walk2.png"};
    private static final String[] JUMP_PATHS = { "pepse/assets/jump2.png"};
    private static final String[] FLY_PATHS = {"pepse/assets/fly.png"};
    private static final String STATIC_PNG = "pepse/assets/static.png";
    private static final double TIME_BETWEEN_CLIPS = 0.1;
    private static final int INITIAL_ENERGY = 100;
    private static final int BEAR_SIZE = 70;
    private static final double ENERGY_FACTOR = 0.5;
    private static final String TOP_GROUND = "topGround";
    private static final String TRUNK = "trunk";
    private static final String TOP_GROUND1 = "topGround";
    private static final int VELOCITY_Y_LIMIT = 350;
    private final Renderable renderer;
    private final AnimationRenderable animationWalk;
    private final ImageRenderable staticBear;
    private final AnimationRenderable animationJump;
    private final AnimationRenderable animationFly;
    private float energy;
    private UserInputListener inputListener;
    private boolean isFly = false;
    private boolean isIncrease = false;

    /**
     * Construct a new GameObject instance.
     *
     * @param topLeftCorner Position of the object, in window coordinates (pixels).
     *                      Note that (0,0) is the top-left corner of the window.
     * @param dimensions    Width and height in window coordinates.
     * @param renderable    The renderable representing the object. Can be null, in which case
     */
    public Avatar(Vector2 topLeftCorner, Vector2 dimensions, Renderable renderable,
                  UserInputListener inputListener, ImageReader imageReader) {
        super(topLeftCorner, dimensions, renderable);
        this.inputListener = inputListener;
        this.energy = INITIAL_ENERGY;
        this.renderer = renderable;
        this.staticBear = imageReader.readImage(STATIC_PNG, true);
        this.animationWalk =
                new AnimationRenderable(WALK_PATHS, imageReader, true, TIME_BETWEEN_CLIPS);
        this.animationJump =
                new AnimationRenderable(JUMP_PATHS, imageReader, true, TIME_BETWEEN_CLIPS);
        this.animationFly =
                new AnimationRenderable(FLY_PATHS, imageReader, true, TIME_BETWEEN_CLIPS);
    }


    /**
     * This function creates an avatar that can travel the world and is followed by the camera. The can stand,
     * walk, jump and fly, and never reaches the end of the world.
     *
     * @param gameObjects   The collection of all participating game objects.
     * @param layer         The number of the layer to which the created avatar should be added.
     * @param topLeftCorner The location of the top-left corner of the created avatar.
     * @param inputListener Used for reading input from the user.
     * @param imageReader   Used for reading images from disk or from within a jar
     * @return A newly created representing the avatar.
     */
    public static Avatar create(GameObjectCollection gameObjects, int layer,
                                Vector2 topLeftCorner, UserInputListener inputListener,
                                ImageReader imageReader) {
        Renderable staticBear = imageReader.readImage(STATIC_PNG,
                true);
        Avatar avatar = new Avatar(topLeftCorner,
                new Vector2(BEAR_SIZE, BEAR_SIZE), staticBear, inputListener, imageReader);
        gameObjects.addGameObject(avatar, layer);
        avatar.physics().preventIntersectionsFromDirection(Vector2.ZERO);
        avatar.transform().setAccelerationY(GRAVITY);
        return avatar;
    }

    /**
     * Called on the first frame of a collision.
     * @param other The GameObject with which a collision occurred.
     * @param collision Information regarding this collision.
     *                 A reasonable elastic behavior can be achieved with:
     *                 setVelocity(getVelocity().flipped(collision.getNormal()));
     */

    @Override
    public void onCollisionEnter(GameObject other, Collision collision) {
        super.onCollisionEnter(other, collision);
        if (other.getTag().equals(TOP_GROUND) | other.getTag().equals(TRUNK)) {
            this.transform().setVelocityY(0);
        }
        if (isFly) {
            if (other.getTag().equals(TOP_GROUND1)) {
                isFly = false;
            }
        }
    }

    /**
     * Called once per frame. Any logic is put here.
     * @param deltaTime The time, in seconds, that passed since the last invocation of this method .
     */
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        boolean isRest = true;
        float xVel = 0;
        this.renderer().setRenderable(staticBear);

        //left
        if (inputListener.isKeyPressed(KeyEvent.VK_LEFT)) {
            isRest = false;
            xVel -= VELOCITY_X;
            this.renderer().setRenderable(animationWalk);
            this.renderer().setIsFlippedHorizontally(true);
        }
        //right
        if (inputListener.isKeyPressed(KeyEvent.VK_RIGHT)) {
            isRest = false;
            xVel += VELOCITY_X;
            this.renderer().setRenderable(animationWalk);
            this.renderer().setIsFlippedHorizontally(false);
        }

        transform().setVelocityX(xVel);

        if (inputListener.isKeyPressed(KeyEvent.VK_SPACE) &&
                inputListener.isKeyPressed(KeyEvent.VK_SHIFT) && this.energy > 0) {
            isRest = handleFlying();
        }
        if (inputListener.isKeyPressed(KeyEvent.VK_SPACE) && getVelocity().y() == 0) {
            isRest = handleJumping();
        }

        handleEnergyIncrement(isRest);

        handleAvatarsVelocity();

    }

    /**
     * This method responsible for the avatar jump
     * @return if the avatar is in resting position
     */
    private boolean handleJumping() {
        boolean isRest;
        isRest = false;
        transform().setVelocityY(VELOCITY_Y);
        this.renderer().setRenderable(animationJump);
        return isRest;
    }

    /**
     * This method responsible for the avatar flying
     * @return if the avatar is in resting position
     */

    private boolean handleFlying() {
        boolean isRest;
        isRest = false;
        isIncrease = false;
        this.energy -= (float) ENERGY_FACTOR;
        transform().setVelocityY(VELOCITY_Y);
        this.renderer().setRenderable(animationFly);
        isFly = true;
        return isRest;
    }

    /**
     * This method increases the energy by 0.5, if avatar is resting and not flying and the left energy is
     * less than 100
     * @param isRest if the avatar in resting position
     */
    private void handleEnergyIncrement(boolean isRest) {
        if (isRest && this.energy < INITIAL_ENERGY && (!isFly)) {
            isIncrease = true;
            this.energy += (float) ENERGY_FACTOR;
        }
    }


    /**
     * This method makes sure that the avatar velocity does not exceed the velocity bound
     */

    private void handleAvatarsVelocity() {
        if (getVelocity().y() < VELOCITY_Y) {
            transform().setVelocityY(VELOCITY_Y);
        }

        if (getVelocity().y() > -VELOCITY_Y) {
            transform().setVelocityY(VELOCITY_Y_LIMIT);
        }

        if (getVelocity().x() > VELOCITY_X) {
            transform().setVelocityX(VELOCITY_X);
        }
    }

    /**
     * This method returns the current energy
     * @return the current energy
     */
    public float getEnergy() {
        return energy;
    }


    /**
     * This method returns if the energy is an int(for example 10.0 considers as int)
     * @return  true if the energy is an int, false otherwise
     */
    public boolean isInt() {
        return energy == (int)energy;
    }


    /**
     * This method returns if the energy should be increased
     * @return true if the energy should be increased, false otherwise
     */
    public boolean isIncrease() {
        return isIncrease;
    }
}


