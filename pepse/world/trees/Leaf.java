package pepse.world.trees;

import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.components.ScheduledTask;
import danogl.components.Transition;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

import java.util.Random;

/**
 * This class represents the leaf object
 */

public class Leaf extends GameObject {

    public static final String LEAF = "leaf";
    private static final float FADEOUT_TIME = 20;
    private static final int BOUND_FOR_LIFE_TIME = 100;
    private static final String TOP_GROUND = "topGround";
    private static final int LEAF_SPEED_Y = 90;
    private static final float MAX_LEAF_SPEED_X = 20f;
    private static final float START_ANGLE = 6f;
    private static final Vector2 MIN_SIZE_LEAF = new Vector2(26, 26);
    private static final Vector2 MAX_SIZE_LEAF = new Vector2(30, 30);
    private static final int MAX_DEATH_TIME = 10;
    private static final String BOTTOM_GROUND = "bottomGround";
    private final Vector2 center;
    private final Random rand;
    private Transition<Float> leafFall;
    private Transition<Float> moveAngle;
    private Transition<Vector2> sizeChange;


    /**
     * Construct a new GameObject instance.
     *
     * @param topLeftCorner Position of the object, in window coordinates (pixels).
     *                      Note that (0,0) is the top-left corner of the window.
     * @param dimensions    Width and height in window coordinates.
     * @param renderable    The renderable representing the object. Can be null, in which case
     */
    public Leaf(Vector2 topLeftCorner, Vector2 dimensions, Renderable renderable, Random rand) {
        super(topLeftCorner, dimensions, renderable);
        this.setTag(LEAF);
        this.rand = rand;
        this.physics().setMass(0);
        this.center = this.getCenter();
        float waitTime = rand.nextFloat();
        new ScheduledTask(this, waitTime, true, this::doTransitions);
        leafBirth();
    }

    /**
     * This method responsible for the returning of the leaf to the right location after it  falls and
     * disappears
     */
    private void reBorn() {
        this.renderer().fadeIn(0);
        this.setCenter(this.center);
        leafBirth();
    }

    /**
     * This method calls the leafDeath function after the lifetime of the leaf is over
     */
    private void leafBirth() {
        new ScheduledTask(this,
                randTime(BOUND_FOR_LIFE_TIME), false, this::leafDeath);
    }

    /**
     * This method responsible for the falling and the fading out of the leaf
     */
    private void leafDeath() {
        this.transform().setVelocityY(LEAF_SPEED_Y);
        this.renderer().fadeOut(FADEOUT_TIME);
        new ScheduledTask(this, FADEOUT_TIME, false, this::deathCycle);
        this.leafFall = new Transition<Float>(this,
                this.transform()::setVelocityX,
                MAX_LEAF_SPEED_X,
                -MAX_LEAF_SPEED_X,
                Transition.CUBIC_INTERPOLATOR_FLOAT,
                1,
                Transition.TransitionType.TRANSITION_BACK_AND_FORTH,
                null);
    }

    /**
     * This method chooses random number that can represents the lifetime or the death cycle
     * @param bound number is chooses in the range of this bound
     * @return the chosen random tome
     */
    private int randTime(int bound) {
        int randTime = rand.nextInt(bound);
        while (randTime < 3) {
            randTime = rand.nextInt(bound);
        }
        return randTime;
    }

    /**
     * This method calls the reBorn function after  random MAX_DEATH_TIME
     */
    private void deathCycle() {
        new ScheduledTask(this,
                randTime(MAX_DEATH_TIME), false, this::reBorn);
    }

    /**
     * This method does 2 transitions that responsible for the movement and the expansion\contraction of the
     * leaf
     */
    private void doTransitions() {
        this.moveAngle = new Transition<Float>(this,
                this.renderer()::setRenderableAngle,
                -START_ANGLE,
                START_ANGLE,
                Transition.CUBIC_INTERPOLATOR_FLOAT,
                1,
                Transition.TransitionType.TRANSITION_BACK_AND_FORTH,
                null);

        this.sizeChange=new Transition<Vector2>(this,
                this::setDimensions,
                MIN_SIZE_LEAF,
                MAX_SIZE_LEAF,
                Transition.LINEAR_INTERPOLATOR_VECTOR,
                3,
                Transition.TransitionType.TRANSITION_BACK_AND_FORTH,
                null);
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
        if (other.getTag().equals(TOP_GROUND) || other.getTag().equals(BOTTOM_GROUND) ) {
            this.removeComponent(moveAngle);
            this.removeComponent(sizeChange);
            this.removeComponent(leafFall);
            new ScheduledTask(this,0.01f,
                    false,()->transform().setVelocity(Vector2.ZERO));

        }
    }

}
