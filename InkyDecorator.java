package pacman.model.entity.dynamic.ghost;

import pacman.model.entity.dynamic.ghost.blinkyObserver.BlinkyPositionObserver;
import pacman.model.entity.dynamic.physics.Direction;
import pacman.model.entity.dynamic.physics.Vector2D;


public class InkyDecorator extends GhostDecorator implements BlinkyPositionObserver {
    private Vector2D blinkysPosition;
    private Direction blinkysDirection;

    public InkyDecorator(Ghost ghost) {
        super(ghost);
    }

    public void updateBlinkyPos(Vector2D position, Direction direction) {
        this.blinkysPosition = position;
        this.blinkysDirection = direction;
    }

    @Override
    public Vector2D getTargetLocation(){

        Vector2D pacmanLocation = decoratedGhost.getPlayerPosition();
        Direction pacmanDirection = decoratedGhost.getPlayerDirection();
        Vector2D modifiedPlayerLocation;
        double newX = 0;
        double newY = 0;
        double blinkyX;
        double blinkyY;
        double doubledX;
        double doubledY;
        double finalAdjustedX;
        double finalAdjustedY;

        switch (pacmanDirection) {
            case RIGHT:
                newX = pacmanLocation.getX() + 2.0*16;
                break;
            case LEFT:
                newX = pacmanLocation.getX() - 2.0*16;
                newY = pacmanLocation.getY();
                break;
            case UP:
                newX = pacmanLocation.getX();
                newY = pacmanLocation.getY() + 2.0*16;
                break;
            case DOWN:
                newX = pacmanLocation.getX();
                newY = pacmanLocation.getY() - 2.0*16;
                break;
            default:
                throw new IllegalStateException("Unexpected value");
        }

        blinkyX = blinkysPosition.getX();
        blinkyY = blinkysPosition.getY();
        if (newX>=blinkyX){
            doubledX = 2*(newX-blinkyX);
        } else {
            doubledX = (-2)*(blinkyX-newX);
        }
        if (newY>=blinkyY){
            doubledY = 2*(newY-blinkyY);
        } else {
            doubledY = (-2)*(blinkyY-newY);
        }
        finalAdjustedX = doubledX+blinkyX;
        finalAdjustedY = doubledY+blinkyY;
        modifiedPlayerLocation = new Vector2D(finalAdjustedX,finalAdjustedY);

        return switch (decoratedGhost.getGhostMode()) {
            case CHASE -> modifiedPlayerLocation;
            case SCATTER -> decoratedGhost.getTargetCorner();
            case FRIGHTENED -> null;
        };
    }
}