package pacman.model.level;

import org.json.simple.JSONObject;
import pacman.ConfigurationParseException;
import pacman.model.engine.observer.GameState;
import pacman.model.entity.Renderable;
import pacman.model.entity.dynamic.DynamicEntity;
import pacman.model.entity.dynamic.ghost.Ghost;
import pacman.model.entity.dynamic.ghost.BlinkyDecorator;
import pacman.model.entity.dynamic.ghost.InkyDecorator;
import pacman.model.entity.dynamic.ghost.GhostMode;
import pacman.model.entity.dynamic.physics.PhysicsEngine;
import pacman.model.entity.dynamic.player.Controllable;
import pacman.model.entity.dynamic.player.Pacman;
import pacman.model.entity.staticentity.StaticEntity;
import pacman.model.entity.staticentity.collectable.Collectable;
import pacman.model.level.observer.LevelStateObserver;
import pacman.model.maze.Maze;
import pacman.model.engine.GhostCounter;
import pacman.model.entity.dynamic.player.pacmanStates.State;
import pacman.model.entity.dynamic.player.pacmanStates.NormalState;
import pacman.model.entity.dynamic.player.pacmanStates.BraveState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Concrete implement of Pac-Man level
 */
public class LevelImpl implements Level {

    private static final int START_LEVEL_TIME = 100;
    private final Maze maze;
    private final List<LevelStateObserver> observers;
    private List<Renderable> renderables;
    private Controllable player;
    private List<Ghost> ghosts;
    private int tickCount;
    private Map<GhostMode, Integer> modeLengths;
    private int numLives;
    private int points;
    private GameState gameState;
    private List<Renderable> collectables;
    private GhostMode currentGhostMode;
    private LevelConfigurationReader levelConfigurationReader;
    private boolean inFrightenedPeriod = false;

    public LevelImpl(JSONObject levelConfiguration, Maze maze) {
        this.renderables = new ArrayList<>();
        this.maze = maze;
        this.tickCount = 0;
        this.observers = new ArrayList<>();
        this.modeLengths = new HashMap<>();
        this.gameState = GameState.READY;
        this.currentGhostMode = GhostMode.SCATTER;
        this.points = 0;

        initLevel(new LevelConfigurationReader(levelConfiguration));
    }

    private void initLevel(LevelConfigurationReader levelConfigurationReader) {
        this.levelConfigurationReader = levelConfigurationReader;
        BlinkyDecorator blinky = null;
        InkyDecorator inky = null;
        // Fetch all renderables for the level
        this.renderables = maze.getRenderables();

        // Set up player
        if (!(maze.getControllable() instanceof Controllable)) {
            throw new ConfigurationParseException("Player entity is not controllable");
        }
        this.player = (Controllable) maze.getControllable();
        this.player.setSpeed(levelConfigurationReader.getPlayerSpeed());
        setNumLives(maze.getNumLives());

        // Set up ghosts
        this.ghosts = maze.getGhosts().stream()
                .map(element -> (Ghost) element)
                .collect(Collectors.toList());
        Map<GhostMode, Double> ghostSpeeds = levelConfigurationReader.getGhostSpeeds();

        for (Ghost ghost : this.ghosts) {
            player.registerObserver(ghost);
            ghost.setSpeeds(ghostSpeeds);
            ghost.setGhostMode(this.currentGhostMode);
            if (ghost instanceof BlinkyDecorator) {
                blinky = (BlinkyDecorator) ghost;
            } else if (ghost instanceof InkyDecorator) {
                inky = (InkyDecorator) ghost;
            }
        }

        if (blinky != null && inky != null) {
            blinky.registerBlinkyObserver(inky);
        }

        this.modeLengths = levelConfigurationReader.getGhostModeLengths();
        // Set up collectables
        this.collectables = new ArrayList<>(maze.getPellets());
        this.collectables.addAll(maze.getPowerpellets());
    }

    @Override
    public List<Renderable> getRenderables() {
        return this.renderables;
    }

    private List<DynamicEntity> getDynamicEntities() {
        return renderables.stream().filter(e -> e instanceof DynamicEntity).map(e -> (DynamicEntity) e).collect(
                Collectors.toList());
    }

    private List<StaticEntity> getStaticEntities() {
        return renderables.stream().filter(e -> e instanceof StaticEntity).map(e -> (StaticEntity) e).collect(
                Collectors.toList());
    }

    @Override
    public void tick() {
        int frightenedCount = 0;
        for (Ghost ghost : this.ghosts) {
            if (ghost.getGhostMode() == GhostMode.FRIGHTENED) {
                frightenedCount++;
            }

            if (frightenedCount == 0) {
                this.player.setStateToNormal();
            } else {
                this.player.setStateToBrave();
            }
        }
        if (this.gameState != GameState.IN_PROGRESS) {

            if (tickCount >= START_LEVEL_TIME) {
                setGameState(GameState.IN_PROGRESS);
                tickCount = 0;
            }

        } else {

            if (tickCount == modeLengths.get(currentGhostMode)) {

                if (this.currentGhostMode != GhostMode.FRIGHTENED) {
                    // update ghost mode
                    this.currentGhostMode = GhostMode.getNextGhostMode(currentGhostMode);
                    for (Ghost ghost : this.ghosts) {
                        ghost.setGhostMode(this.currentGhostMode);
                    }
                } else {
                    this.currentGhostMode = GhostMode.SCATTER;
                    for (Ghost ghost : this.ghosts) {
                        ghost.exitFrightenedMode();
                    }
                }
                tickCount = 0;
            }

            if (tickCount % Pacman.PACMAN_IMAGE_SWAP_TICK_COUNT == 0) {
                this.player.switchImage();
            }

            // Update the dynamic entities
            List<DynamicEntity> dynamicEntities = getDynamicEntities();

            for (DynamicEntity dynamicEntity : dynamicEntities) {
                maze.updatePossibleDirections(dynamicEntity);
                dynamicEntity.update();
            }

            for (int i = 0; i < dynamicEntities.size(); ++i) {
                DynamicEntity dynamicEntityA = dynamicEntities.get(i);

                // handle collisions between dynamic entities
                for (int j = i + 1; j < dynamicEntities.size(); ++j) {
                    DynamicEntity dynamicEntityB = dynamicEntities.get(j);

                    if (dynamicEntityA.collidesWith(dynamicEntityB) ||
                            dynamicEntityB.collidesWith(dynamicEntityA)) {
                        dynamicEntityA.collideWith(this, dynamicEntityB);
                        dynamicEntityB.collideWith(this, dynamicEntityA);
                    }
                }

                // handle collisions between dynamic entities and static entities
                for (StaticEntity staticEntity : getStaticEntities()) {
                    if (dynamicEntityA.collidesWith(staticEntity)) {
                        dynamicEntityA.collideWith(this, staticEntity);
                        PhysicsEngine.resolveCollision(dynamicEntityA, staticEntity);
                    }
                }
            }
        }

        tickCount++;
    }

    @Override
    public void setGhostModeToFrightened() {
        for (Ghost ghost : this.ghosts){
            ghost.enterFrightenedMode();
        }
        tickCount = 0;
        currentGhostMode = GhostMode.FRIGHTENED;
    }

    @Override
    public boolean isPlayer(Renderable renderable) {
        return renderable == this.player;
    }

    @Override
    public boolean isGhost(Renderable renderable) {
        for (Ghost ghost : this.ghosts) {
            if (ghost==renderable){
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isCollectable(Renderable renderable) {
        return maze.getPellets().contains(renderable) && ((Collectable) renderable).isCollectable() || maze.getPowerpellets().contains(renderable) && ((Collectable) renderable).isCollectable();
    }

    @Override
    public void collect(Collectable collectable) {
        this.points += collectable.getPoints();
        notifyObserversWithScoreChange(collectable.getPoints());
        this.collectables.remove(collectable);
    }

    @Override
    public void handleLoseLife() {
        if (gameState == GameState.IN_PROGRESS) {
            for (DynamicEntity dynamicEntity : getDynamicEntities()) {
                dynamicEntity.reset();
                Map<GhostMode, Double> ghostSpeeds = levelConfigurationReader.getGhostSpeeds();
                for (Ghost ghost : this.ghosts) {
                    ghost.setSpeeds(ghostSpeeds);
                    ghost.updateSpeed();
                }
            }
            setNumLives(numLives - 1);
            setGameState(GameState.READY);
            tickCount = 0;
        }
    }

    @Override
    public void handleGhostLoseLife(Renderable ghost) {
        ghost.reset();
        if (ghost instanceof Ghost) {
            Ghost castedGhost = (Ghost) ghost;
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    Map<GhostMode, Double> ghostSpeeds = levelConfigurationReader.getGhostSpeeds();
                    castedGhost.setSpeeds(ghostSpeeds);
                    castedGhost.updateSpeed();
                }
            }, 1000);
        }
    }

    @Override
    public void pacmanAteGhost() {
        GhostCounter.getInstance().incrementCount();
        rewardScore();
    }

    @Override
    public void enterNewFrightenedPeriod() {
        GhostCounter.getInstance().resetCount();
    }

    private void rewardScore(){  // for eating ghost
        int num = GhostCounter.getInstance().getGhostCount();
        if (num == 1){
            notifyObserversWithScoreChange(200);
        } else if (num == 2){
            notifyObserversWithScoreChange(400);
        } else if (num == 3){
            notifyObserversWithScoreChange(800);
        } else if (num == 4){
            notifyObserversWithScoreChange(1600);
        }
    }

    @Override
    public void moveLeft() {
        player.left();
    }

    @Override
    public void moveRight() {
        player.right();
    }

    @Override
    public void moveUp() {
        player.up();
    }

    @Override
    public void moveDown() {
        player.down();
    }

    @Override
    public boolean isLevelFinished() {
        return collectables.isEmpty();
    }

    @Override
    public void registerObserver(LevelStateObserver observer) {
        this.observers.add(observer);
        observer.updateNumLives(this.numLives);
        observer.updateGameState(this.gameState);
    }

    @Override
    public void removeObserver(LevelStateObserver observer) {
        this.observers.remove(observer);
    }

    @Override
    public void notifyObserversWithNumLives() {
        for (LevelStateObserver observer : observers) {
            observer.updateNumLives(this.numLives);
        }
    }

    private void setGameState(GameState gameState) {
        this.gameState = gameState;
        notifyObserversWithGameState();
    }

    @Override
    public void notifyObserversWithGameState() {
        for (LevelStateObserver observer : observers) {
            observer.updateGameState(gameState);
        }
    }

    /**
     * Notifies observer of change in player's score
     */
    public void notifyObserversWithScoreChange(int scoreChange) {
        for (LevelStateObserver observer : observers) {
            observer.updateScore(scoreChange);
        }
    }

    @Override
    public int getPoints() {
        return this.points;
    }

    @Override
    public int getNumLives() {
        return this.numLives;
    }

    private void setNumLives(int numLives) {
        this.numLives = numLives;
        notifyObserversWithNumLives();
    }

    @Override
    public void handleGameEnd() {
        this.renderables.removeAll(getDynamicEntities());
    }
}
