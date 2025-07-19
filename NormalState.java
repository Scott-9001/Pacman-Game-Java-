package pacman.model.entity.dynamic.player.pacmanStates;

import pacman.model.entity.Renderable;
import pacman.model.entity.dynamic.player.Pacman;
import pacman.model.entity.staticentity.collectable.Collectable;
import pacman.model.level.Level;

public class NormalState implements State {
    public void collideWith(Level level, Renderable renderable, Pacman pacman) {
        if (level.isCollectable(renderable)) {
            Collectable collectable = (Collectable) renderable;
            level.collect(collectable);
            if (collectable.isPowerPellet()){
                level.setGhostModeToFrightened();
                level.enterNewFrightenedPeriod();
            }
            collectable.collect();
        }
    }
}
