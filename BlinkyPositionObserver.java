package pacman.model.entity.dynamic.ghost.blinkyObserver;

import pacman.model.entity.dynamic.physics.Vector2D;
import pacman.model.entity.dynamic.physics.Direction;

/***
 * Observer for BinkyPositionObserver
 */
public interface BlinkyPositionObserver {

    /**
     * Updates Inky with the new position of Blinky
     *
     * @param position Blinky's position
     */
    void updateBlinkyPos(Vector2D position, Direction direction);
}
