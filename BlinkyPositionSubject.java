package pacman.model.entity.dynamic.ghost.blinkyObserver;

/***
 * Subject that is being observed by BlinkyPositionObserver
 */
public interface BlinkyPositionSubject {

    /**
     * Adds an observer to list of observers for subject
     *
     * @param observer observer for PlayerPositionSubject
     */
    void registerBlinkyObserver(BlinkyPositionObserver observer);

    /**
     * Removes an observer from list of observers for subject
     *
     * @param observer observer for PlayerPositionObserver
     */
    void removeBlinkyObserver(BlinkyPositionObserver observer);

    /**
     * Notifies observer of change in Blinky's position
     */
    void notifyBlinkyObservers();
}
