package pepse;

import danogl.GameManager;
import danogl.GameObject;
import danogl.collisions.Layer;
import danogl.gui.ImageReader;
import danogl.gui.SoundReader;
import danogl.gui.UserInputListener;
import danogl.gui.WindowController;
import danogl.gui.rendering.Camera;
import danogl.util.Counter;
import danogl.util.Vector2;
import pepse.world.Avatar;
import pepse.world.NumericEnergyCounter;
import pepse.world.Sky;
import pepse.world.Terrain;
import pepse.world.daynight.Night;
import pepse.world.daynight.Sun;
import pepse.world.daynight.SunHalo;
import pepse.world.trees.Leaf;
import pepse.world.trees.Tree;
import pepse.world.trees.Trunk;


import java.awt.*;
import java.util.Random;

/**
 * The main class of the simulator.
 */
public class PepseGameManager extends GameManager {

    private static final int SUN_HALO_LAYER = Layer.BACKGROUND + 10;
    private static final int SUN_LAYER = Layer.BACKGROUND + 1;
    private static final int NIGHT_LAYER = Layer.FOREGROUND;
    private static final int NIGHT_DAY_CYCLE = 30;
    private static final int LEAF_LAYER = Layer.STATIC_OBJECTS + 10;
    private static final int TERRAIN_TOP_LAYER = Layer.STATIC_OBJECTS;
    private static final int AVATAR_LAYER = Layer.DEFAULT;
    private static final int TRUNK_LAYER = Layer.STATIC_OBJECTS + 8;
    private static final int SEED_BOUND = 300;
    private static final int INITIAL_ENERGY = 100;
    private static final int ENERGY_LOCATION = 15;
    private static final int ENERGY_COUNTER_SIZE = 50;
    private Avatar avatar;
    private int leftBound;
    private int rightBound;
    private Vector2 windowsDimensions;
    private Terrain terrain;
    private Tree tree;
    private Vector2 initialAvatarLocation;
    private Counter energyCounter;


    /**
     * Runs the entire simulation.
     *
     * @param args This argument should not be used.
     */
    public static void main(String[] args) {
        new PepseGameManager().run();
    }


    /**
     * The method will be called once when a GameGUIComponent is created, and again after every invocation
     * of windowController.resetGame().
     *
     * @param imageReader      Contains a single method: readImage, which reads an image from disk.
     *                         See its documentation for help.
     * @param soundReader      Contains a single method: readSound, which reads a wav file from disk.
     *                         See its documentation for help.
     * @param inputListener    Contains a single method: isKeyPressed, which returns whether a given key is
     *                         currently pressed by the user or not. See its documentation.
     * @param windowController Contains an array of helpful, self explanatory methods concerning the window.
     */
    @Override
    public void initializeGame(ImageReader imageReader,
                               SoundReader soundReader,
                               UserInputListener inputListener,
                               WindowController windowController) {
        super.initializeGame(imageReader, soundReader, inputListener, windowController);

        this.windowsDimensions = windowController.getWindowDimensions();
        this.initialAvatarLocation = new Vector2(windowController.getWindowDimensions().mult(0.5f));
        this.rightBound = (int) (initialAvatarLocation.x() + windowController.getWindowDimensions().x());
        this.leftBound = (int) (initialAvatarLocation.x() - windowController.getWindowDimensions().x());

        //create sky
        Sky.create(gameObjects(), windowController.getWindowDimensions(),
                Layer.BACKGROUND);

        //create terrain
        int seed = new Random().nextInt(SEED_BOUND);
        this.terrain = new Terrain(gameObjects(), Layer.STATIC_OBJECTS,
                windowController.getWindowDimensions(), seed);
        terrain.createInRange(leftBound, rightBound);

        //create day-night
        Night.create(gameObjects(), NIGHT_LAYER, windowController.getWindowDimensions(), NIGHT_DAY_CYCLE);
        GameObject sun = Sun.create(gameObjects(),
                SUN_LAYER, windowController.getWindowDimensions(), NIGHT_DAY_CYCLE);
        SunHalo.create(gameObjects(), SUN_HALO_LAYER, sun,
                new Color(255, 255, 0, 20));

        //create trees
        this.tree = new Tree(seed, gameObjects(), windowController.getWindowDimensions(),
                terrain::groundHeightAt);
        tree.createInRange(leftBound, rightBound);

        //create bear
        this.avatar = Avatar.create(gameObjects(), Layer.DEFAULT,
                initialAvatarLocation,
                inputListener,
                imageReader);
        Camera camera = new Camera(avatar,
                windowController.getWindowDimensions().mult(0.5f).subtract(initialAvatarLocation),
                windowController.getWindowDimensions(), windowController.getWindowDimensions());
        setCamera(camera);

        //collisions
        handleCollision();

        //creating energy counter
        creatingNumericEnergyCounter();


    }

    /**
     * This method deals with collisions between certain objects by their layer
     * the leaves collide with the terrain,the avatar collides with the terrain and the trunk
     */

    private void handleCollision() {
        gameObjects().layers().shouldLayersCollide(LEAF_LAYER, TERRAIN_TOP_LAYER, true);
        gameObjects().layers().shouldLayersCollide(AVATAR_LAYER, TERRAIN_TOP_LAYER, true);
        gameObjects().layers().shouldLayersCollide(AVATAR_LAYER, TRUNK_LAYER, true);
    }

    /**
     * creating Numeric energy Counter which represents in Numeric way
     * haw many energy left to the avatar in the game.
     */
    private void creatingNumericEnergyCounter() {
        this.energyCounter = new Counter(INITIAL_ENERGY);
        GameObject numericEnergyCounter = new NumericEnergyCounter(energyCounter,
                new Vector2(ENERGY_LOCATION, ENERGY_LOCATION),
                new Vector2(ENERGY_COUNTER_SIZE, ENERGY_COUNTER_SIZE),
                gameObjects());
        gameObjects().addGameObject(numericEnergyCounter, Layer.FOREGROUND);
    }


    /**
     * Called once per frame. Any logic is put here.
     * @param deltaTime The time, in seconds, that passed since the last invocation of this method .
     */
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

        handleNumericEnergyCounter();

        handleInfiniteWorld();
    }

    /**
     * This method produces another world (more terrain and trees) when the avatar reaches a certain range
     * from the windowDimensions
     */
    private void handleInfiniteWorld() {

        //when the avatar is close to the end of the world from the right side.
        if (Math.abs(this.avatar.getCenter().x()
                - rightBound) < 0.75 * this.windowsDimensions.x()) {

            this.terrain.createInRange(rightBound, (int) (rightBound + windowsDimensions.x() * 0.5));
            this.tree.createInRange(rightBound, (int) (rightBound + windowsDimensions.x() * 0.5));
            this.rightBound += this.windowsDimensions.x() * 0.5;
            removeInRange(leftBound, (int) (leftBound + windowsDimensions.x() * 0.5));
            this.leftBound = (int) (leftBound + windowsDimensions.x() * 0.5);
        }

        //when the avatar is close to the rnd of the world from the left side.
        if (Math.abs(this.avatar.getCenter().x() - leftBound) < 0.75 * windowsDimensions.x()) {

            this.terrain.createInRange((int) (leftBound - windowsDimensions.x() * 0.5), leftBound);
            this.tree.createInRange((int) (leftBound - windowsDimensions.x() * 0.5), leftBound);
            removeInRange((int) (rightBound - (windowsDimensions.x() * 0.5)), rightBound);
            this.rightBound -= this.windowsDimensions.x() * 0.5;
            this.leftBound = (int) (leftBound - (windowsDimensions.x() * 0.5));


        }
    }

    /**
     * This method is responsible for updating the correct energy
     * at any given time according to the state of the Avatar ,by increasing and decreasing the energy
     * respectively
     */
    private void handleNumericEnergyCounter() {
        if (this.energyCounter.value() != avatar.getEnergy() &&
                avatar.isInt()) {
            if (avatar.isIncrease()) {
                this.energyCounter.increment();
            }
            else {
                this.energyCounter.decrement();
            }

        }
    }


    /**
     * This method removes the terrain and trees that appears between the range minRange and maxRange
     * @param minRange the starts range to remove objects
     * @param maxRange the final range to remove objects
     */
    private void removeInRange(int minRange, int maxRange) {
        for (GameObject gameObj : gameObjects()) {
            if (gameObj.getCenter().x() < maxRange &&
                    gameObj.getCenter().x() > minRange) {
                if (gameObj.getTag().equals(Terrain.TOP_GROUND))
                    gameObjects().removeGameObject(gameObj, TERRAIN_TOP_LAYER);
                else if (gameObj.getTag().equals(Terrain.BOTTOM_GROUND)) {
                    gameObjects().removeGameObject(gameObj, TERRAIN_TOP_LAYER + 5);
                } else if (gameObj.getTag().equals(Trunk.TRUNK)) {
                    gameObjects().removeGameObject(gameObj, TRUNK_LAYER);
                } else if (gameObj.getTag().equals(Leaf.LEAF)) {
                    gameObjects().removeGameObject(gameObj, LEAF_LAYER);
                }
            }
        }
    }
}
