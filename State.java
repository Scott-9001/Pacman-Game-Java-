package pacman.model.entity.dynamic.player.pacmanStates;

import pacman.model.entity.Renderable;
import pacman.model.entity.dynamic.player.Pacman;
import pacman.model.level.Level;

public interface State {
    void collideWith(Level level, Renderable renderable, Pacman pacman);
}