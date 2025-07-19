package pacman.model.entity.dynamic.ghost;

import pacman.model.entity.dynamic.physics.Direction;
import pacman.model.entity.dynamic.physics.Vector2D;
import static pacman.model.entity.dynamic.ghost.GhostMode.SCATTER;
import static pacman.model.entity.dynamic.ghost.GhostMode.CHASE;


public class PinkyDecorator extends GhostDecorator {

    public PinkyDecorator(Ghost ghost) {
        super(ghost);
    }

    @Override
    public Vector2D getTargetLocation(){
        Vector2D playerPosition = decoratedGhost.getPlayerPosition();
        Direction playerDirection = decoratedGhost.getPlayerDirection();
        Vector2D modifiedPlayerLocation;
        switch (playerDirection) {
            case RIGHT:
                modifiedPlayerLocation = new Vector2D(playerPosition.getX() + 4.0*16, playerPosition.getY());
                break;
            case LEFT:
                modifiedPlayerLocation = new Vector2D(playerPosition.getX() - 4.0*16, playerPosition.getY());
                break;
            case UP:
                modifiedPlayerLocation = new Vector2D(playerPosition.getX(), playerPosition.getY() - 4.0*16);
                break;
            case DOWN:
                modifiedPlayerLocation = new Vector2D(playerPosition.getX(), playerPosition.getY() + 4.0*16);
                break;
            default:
                throw new IllegalStateException("Unexpected value");

        }

//        System.out.println("PinkyDecorator: originalPlayerLocation = " + playerPosition);
//        System.out.println("PinkyDecorator: PlayerDirection = " + playerDirection);
//        System.out.println("PinkyDecorator: modifiedPlayerLocation = " + modifiedPlayerLocation);

        return switch (decoratedGhost.getGhostMode()) {
            case CHASE -> modifiedPlayerLocation;
            case SCATTER -> decoratedGhost.getTargetCorner();
            case FRIGHTENED -> null;
        };
    }
}


