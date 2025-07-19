package pacman.model.entity.dynamic.ghost;

import pacman.model.entity.dynamic.physics.Direction;
import pacman.model.entity.dynamic.physics.Vector2D;
import static pacman.model.entity.dynamic.ghost.GhostMode.SCATTER;
import static pacman.model.entity.dynamic.ghost.GhostMode.CHASE;


public class ClydeDecorator extends GhostDecorator {

    public ClydeDecorator(Ghost ghost) {
        super(ghost);
    }

    @Override
    public Vector2D getTargetLocation(){
        Vector2D playerPosition = decoratedGhost.getPlayerPosition();
        Vector2D modifiedPlayerLocation;
        Vector2D ghostLocation = decoratedGhost.getPosition();

        if (Vector2D.calculateEuclideanDistance(playerPosition, ghostLocation) > 8.0*16) {
            modifiedPlayerLocation = playerPosition;
        } else {
            modifiedPlayerLocation = decoratedGhost.getTargetCorner();
        }

        return switch (decoratedGhost.getGhostMode()) {
            case CHASE -> modifiedPlayerLocation;
            case SCATTER -> decoratedGhost.getTargetCorner();
            case FRIGHTENED -> null;
        };
    }
}
