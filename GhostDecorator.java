package pacman.model.entity.dynamic.ghost;

import javafx.scene.image.Image;
import pacman.model.entity.Renderable;
import pacman.model.entity.dynamic.physics.*;
import pacman.model.level.Level;
import pacman.model.maze.Maze;

import java.util.*;

public abstract class GhostDecorator implements Ghost {
    protected Ghost decoratedGhost;

    public GhostDecorator(Ghost decoratedGhost) {
        this.decoratedGhost = decoratedGhost;
    }

    @Override
    public void enterFrightenedMode(){
        decoratedGhost.enterFrightenedMode();
    }

    @Override
    public void exitFrightenedMode(){
        decoratedGhost.exitFrightenedMode();
    }

    @Override
    public GhostMode getGhostMode() {
        return decoratedGhost.getGhostMode();
    }

    @Override
    public Vector2D getPlayerPosition() {
        return decoratedGhost.getPlayerPosition();
    }

    @Override
    public Vector2D getTargetCorner() {
        return decoratedGhost.getTargetCorner();
    }

    @Override
    public Direction getPlayerDirection() {
        return decoratedGhost.getPlayerDirection();
    }

    @Override
    public void update() {
        decoratedGhost.update();
    }

    @Override
    public void update(Vector2D playerPosition, Direction playerdirection) {
        decoratedGhost.update(playerPosition, playerdirection);
    }

    @Override
    public void setSpeeds(Map<GhostMode, Double> speeds) {
        decoratedGhost.setSpeeds(speeds);
    }

    @Override
    public Image getImage() {
        return decoratedGhost.getImage();
    }

    @Override
    public double getWidth() {
        return decoratedGhost.getWidth();
    }

    @Override
    public double getHeight() {
        return decoratedGhost.getHeight();
    }

    @Override
    public Vector2D getPosition() {
        return decoratedGhost.getPosition();
    }

    @Override
    public Renderable.Layer getLayer() {
        return decoratedGhost.getLayer();
    }

    @Override
    public BoundingBox getBoundingBox() {
        return decoratedGhost.getBoundingBox();
    }

    @Override
    public void reset() {
        decoratedGhost.reset();
    }

    @Override
    public void setGhostMode(GhostMode ghostMode) {
        decoratedGhost.setGhostMode(ghostMode);
    }

    @Override
    public boolean collidesWith(Renderable renderable) {
        return decoratedGhost.collidesWith(renderable);
    }

    @Override
    public void collideWith(Level level, Renderable renderable) {
        decoratedGhost.collideWith(level, renderable);
    }

    @Override
    public Vector2D getPositionBeforeLastUpdate() {
        return decoratedGhost.getPositionBeforeLastUpdate();
    }

    @Override
    public void setPosition(Vector2D position) {
        decoratedGhost.setPosition(position);
    }

    @Override
    public void setPossibleDirections(Set<Direction> possibleDirections) {
       decoratedGhost.setPossibleDirections(possibleDirections);
    }

    @Override
    public Direction getDirection() {
        return decoratedGhost.getDirection();
    }

    @Override
    public Vector2D getCenter() {
        return decoratedGhost.getCenter();
    }

    @Override
    public void updateSpeed(){
        decoratedGhost.updateSpeed();
    }

}

