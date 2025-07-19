package pacman.model.entity.dynamic.ghost.ghostStrategy;

import pacman.model.entity.Renderable;
import pacman.model.entity.dynamic.ghost.GhostImpl;
import pacman.model.entity.dynamic.physics.Direction;
import pacman.model.level.Level;
import pacman.model.maze.Maze;

/**
 * Behavior when the ghost is in NORMAL mode (not FRIGHTENED).
 */
public class NormalBehavior implements GhostBehavior {

    @Override
    public void updateDirection(GhostImpl ghost) {
        if (Maze.isAtIntersection(ghost.getPossibleDirections())) {
            ghost.setTargetLocation(ghost.getTargetLocation());
        }

        Direction newDirection = ghost.selectDirection(ghost.getPossibleDirections());

        if (ghost.getCurrentDirection() != newDirection) {
            ghost.setCurrentDirectionCount(0);
        }
        ghost.setCurrentDirection(newDirection);
        switch (ghost.getCurrentDirection()) {
            case LEFT -> ghost.getKinematicState().left();
            case RIGHT -> ghost.getKinematicState().right();
            case UP -> ghost.getKinematicState().up();
            case DOWN -> ghost.getKinematicState().down();
        }
    }

    @Override
    public void setNewImage(GhostImpl ghost) {
        ghost.image = ghost.normalImage;
    }

    @Override
    public void collideWith(Level level, Renderable renderable, GhostImpl ghost) {
        if (level.isPlayer(renderable)) {
            level.handleLoseLife();
        }
    }
}
