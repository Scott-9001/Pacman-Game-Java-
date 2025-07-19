package pacman.model.entity.dynamic.ghost.ghostStrategy;

import pacman.model.entity.Renderable;
import pacman.model.entity.dynamic.ghost.GhostImpl;
import pacman.model.entity.dynamic.physics.Direction;
import pacman.model.level.Level;

/**
 * Behavior when the ghost is in FRIGHTENED mode.
 */
public class FrightenedBehavior implements GhostBehavior {

    @Override
    public void updateDirection(GhostImpl ghost) {
        Direction newDirection = ghost.selectRandomDirection(ghost.getPossibleDirections());
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
        ghost.image = ghost.FRIGHTENED_IMAGE;
    }

    @Override
    public void collideWith(Level level, Renderable renderable, GhostImpl ghost) {
        if (level.isPlayer(renderable)) {
            level.handleGhostLoseLife(ghost);
        }
    }
}
