package pacman.model.entity.dynamic.ghost;

import pacman.model.entity.dynamic.ghost.blinkyObserver.BlinkyPositionSubject;
import pacman.model.entity.dynamic.physics.Vector2D;
import pacman.model.entity.dynamic.ghost.blinkyObserver.BlinkyPositionObserver;

import java.util.HashSet;
import java.util.Set;


public class BlinkyDecorator extends GhostDecorator implements BlinkyPositionSubject {
    private final Set<BlinkyPositionObserver> observers;

    public BlinkyDecorator(Ghost ghost) {
        super(ghost);
        this.observers = new HashSet<>();
    }

    public void registerBlinkyObserver(BlinkyPositionObserver observer){
        this.observers.add(observer);
        observer.updateBlinkyPos(decoratedGhost.getPosition(), decoratedGhost.getDirection());
    }

    public void removeBlinkyObserver(BlinkyPositionObserver observer){
        this.observers.remove(observer);
    }

    public void notifyBlinkyObservers(){
        for (BlinkyPositionObserver blinkyPositionObserver : observers) {
            blinkyPositionObserver.updateBlinkyPos(decoratedGhost.getPosition(), decoratedGhost.getDirection());
        }
    }

    @Override
    public void update() {
        super.update();
        notifyBlinkyObservers();
    }

    @Override
    public Vector2D getTargetLocation(){
        return switch (decoratedGhost.getGhostMode()) {
            case CHASE -> decoratedGhost.getPlayerPosition();
            case SCATTER -> decoratedGhost.getTargetCorner();
            case FRIGHTENED -> null;
        };
    }
}