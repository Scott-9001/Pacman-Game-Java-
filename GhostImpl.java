package pacman.model.entity.dynamic.ghost;

import javafx.scene.image.Image;
import pacman.model.entity.Renderable;
import pacman.model.entity.dynamic.ghost.ghostStrategy.FrightenedBehavior;
import pacman.model.entity.dynamic.ghost.ghostStrategy.GhostBehavior;
import pacman.model.entity.dynamic.ghost.ghostStrategy.NormalBehavior;
import pacman.model.entity.dynamic.physics.*;
import pacman.model.level.Level;

import java.util.*;

/**
 * Concrete implementation of Ghost entity in Pac-Man Game
 */
public class GhostImpl implements Ghost {

    private static final int minimumDirectionCount = 8;
    private final Layer layer = Layer.FOREGROUND;
    public Image image;
    private final BoundingBox boundingBox;
    private final Vector2D startingPosition;
    private final Vector2D targetCorner;
    private KinematicState kinematicState;
    private GhostMode ghostMode;
    private Vector2D targetLocation;
    private Vector2D playerPosition;
    private Direction playerDirection;
    private Direction currentDirection;
    private Set<Direction> possibleDirections;
    private Map<GhostMode, Double> speeds;
    private int currentDirectionCount = 0;
    private Ghost decoratedGhost;
    public static final Image FRIGHTENED_IMAGE = new Image("maze/ghosts/frightened.png");
    public final Image normalImage;
    private GhostBehavior behavior;

    public GhostImpl(Image image, BoundingBox boundingBox, KinematicState kinematicState, GhostMode ghostMode, Vector2D targetCorner) {
        this.image = image;
        this.normalImage = image;
        this.boundingBox = boundingBox;
        this.kinematicState = kinematicState;
        this.startingPosition = kinematicState.getPosition();
        this.ghostMode = ghostMode;
        this.possibleDirections = new HashSet<>();
        this.targetCorner = targetCorner;
        this.targetLocation = getTargetLocation();
        this.currentDirection = null;
        setGhostBehavior();
    }

    private void setGhostBehavior() {
        if (this.ghostMode == GhostMode.FRIGHTENED) {
            this.behavior = new FrightenedBehavior();
        } else {
            this.behavior = new NormalBehavior();
        }
    }

    @Override
    public GhostMode getGhostMode() {
        return this.ghostMode;
    }

    @Override
    public Vector2D getPlayerPosition() {
        return this.playerPosition;
    }

    @Override
    public Vector2D getTargetCorner() {
        return this.targetCorner;
    }

    @Override
    public Direction getPlayerDirection() {
        return this.playerDirection;
    }

    @Override
    public void setSpeeds(Map<GhostMode, Double> speeds) {
        this.speeds = speeds;
    }

    @Override
    public Image getImage() {
        return image;
    }

    @Override
    public void update() {
        this.updateDirection();
        this.kinematicState.update();
        this.boundingBox.setTopLeft(this.kinematicState.getPosition());
    }


    private void updateDirection() {
        // Ghosts update their target location when they reach an intersection
        behavior.updateDirection(this);
    }

    public Set<Direction> getPossibleDirections() {
        return this.possibleDirections;
    }

    public Direction getCurrentDirection() {
        return this.currentDirection;
    }

    public void setCurrentDirectionCount(int count) {
        this.currentDirectionCount = count;
    }

    public void setCurrentDirection(Direction direction) {
        this.currentDirection = direction;
    }

    public KinematicState getKinematicState() {
        return this.kinematicState;
    }

    public void setTargetLocation(Vector2D targetLocation) {
        this.targetLocation = targetLocation;
    }

    public Vector2D getTargetLocation() {
        if (decoratedGhost != null) {
            return decoratedGhost.getTargetLocation();
        } else {
            return switch (this.ghostMode) {
                case CHASE -> this.playerPosition;
                case SCATTER -> this.targetCorner;
                case FRIGHTENED -> null;
            };
        }
    }

    @Override
    public void updateSpeed(){
        this.kinematicState.setSpeed(speeds.get(ghostMode));
    }

    @Override
    public void enterFrightenedMode(){
        this.ghostMode = GhostMode.FRIGHTENED;
        setGhostBehavior();
        behavior.setNewImage(this);
        this.kinematicState.setSpeed(speeds.get(ghostMode));
    }

    @Override
    public void exitFrightenedMode(){
        this.ghostMode = GhostMode.SCATTER;
        setGhostBehavior();
        behavior.setNewImage(this);
        this.kinematicState.setSpeed(speeds.get(ghostMode));
    }

    public Direction selectDirection(Set<Direction> possibleDirections) {
        if (possibleDirections.isEmpty()) {
            return currentDirection;
        }

        // ghosts have to continue in a direction for a minimum time before changing direction
        if (currentDirection != null && currentDirectionCount < minimumDirectionCount) {
            currentDirectionCount++;
            return currentDirection;
        }

        Map<Direction, Double> distances = new HashMap<>();

        for (Direction direction : possibleDirections) {
            // ghosts never choose to reverse travel
            if (currentDirection == null || direction != currentDirection.opposite()) {
                distances.put(direction, Vector2D.calculateEuclideanDistance(this.kinematicState.getPotentialPosition(direction), this.targetLocation));
            }
        }

        // only go the opposite way if trapped
        if (distances.isEmpty()) {
            return currentDirection.opposite();
        }

        // select the direction that will reach the target location fastest
        return Collections.min(distances.entrySet(), Map.Entry.comparingByValue()).getKey();
    }

    public Direction selectRandomDirection(Set<Direction> possibleDirections){
        if (possibleDirections.isEmpty()) {
            return currentDirection;
        }
        if (currentDirection != null && currentDirectionCount < minimumDirectionCount) {
            currentDirectionCount++;
            return currentDirection;
        }
        List<Direction> validDirections = new ArrayList<>();

        for (Direction direction : possibleDirections) {
            if (currentDirection == null || direction != currentDirection.opposite()) {
                validDirections.add(direction);
            }
        }
        if (validDirections.isEmpty()) {
            return currentDirection.opposite();
        }
        Random random = new Random();
        return validDirections.get(random.nextInt(validDirections.size()));
    }

    @Override
    public void setGhostMode(GhostMode ghostMode) {
        this.ghostMode = ghostMode;
        setGhostBehavior();
        this.kinematicState.setSpeed(speeds.get(ghostMode));
        // ensure direction is switched
        this.currentDirectionCount = minimumDirectionCount;
    }


    @Override
    public boolean collidesWith(Renderable renderable) {
        return boundingBox.collidesWith(kinematicState.getSpeed(), kinematicState.getDirection(), renderable.getBoundingBox());
    }

    @Override
    public void collideWith(Level level, Renderable renderable) {
        behavior.collideWith(level,renderable,this);
    }

    @Override
    public void update(Vector2D playerPosition, Direction playerDirection) {
        this.playerPosition = playerPosition;
        this.playerDirection = playerDirection;
    }

    @Override
    public Vector2D getPositionBeforeLastUpdate() {
        return this.kinematicState.getPreviousPosition();
    }

    @Override
    public double getHeight() {
        return this.boundingBox.getHeight();
    }

    @Override
    public double getWidth() {
        return this.boundingBox.getWidth();
    }

    @Override
    public Vector2D getPosition() {
        return this.kinematicState.getPosition();
    }

    @Override
    public void setPosition(Vector2D position) {
        this.kinematicState.setPosition(position);
    }

    @Override
    public Layer getLayer() {
        return this.layer;
    }

    @Override
    public BoundingBox getBoundingBox() {
        return this.boundingBox;
    }

    @Override
    public void reset() {
        // return ghost to starting position
        this.kinematicState = new KinematicStateImpl.KinematicStateBuilder()
                .setPosition(startingPosition)
                .build();
        this.boundingBox.setTopLeft(startingPosition);
        this.ghostMode = GhostMode.SCATTER;
        setGhostBehavior();
        this.currentDirectionCount = minimumDirectionCount;
        this.image = this.normalImage;
    }

    @Override
    public void setPossibleDirections(Set<Direction> possibleDirections) {
        this.possibleDirections = possibleDirections;
    }

    @Override
    public Direction getDirection() {
        return this.kinematicState.getDirection();
    }

    @Override
    public Vector2D getCenter() {
        return new Vector2D(boundingBox.getMiddleX(), boundingBox.getMiddleY());
    }

    public void setDecorator(Ghost decorator) {
        this.decoratedGhost = decorator;
    }
}
