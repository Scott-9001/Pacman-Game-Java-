package pacman.model.entity.dynamic.ghost.ghostStrategy;

import pacman.model.entity.Renderable;
import pacman.model.entity.dynamic.ghost.GhostImpl;
import pacman.model.level.Level;

/**
 * Strategy interface for ghost behaviors based on the current mode (FRIGHTENED or NORMAL).
 */
public interface GhostBehavior {
    void updateDirection(GhostImpl ghost);
    void setNewImage(GhostImpl ghost);
    void collideWith(Level level, Renderable renderable, GhostImpl ghost);
}
