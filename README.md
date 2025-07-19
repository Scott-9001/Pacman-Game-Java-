Download Pacman Game.zip

Use gradle run to run this program.

Different ghost types are achieved by creating new factories. I extended the existing factory pattern by creating GhostFactory, BlinkyFactory, ClydeFactory, InkyFactory and PinkyFactory classes.

The implementation of new ghost functions for each ghost type is done through using the decorator pattern. The decorator wraps the GhostImpl objects to provide different functions for different ghosts. The decorator pattern involves GhostDecorator, BlinkyDecorator, ClydeDecorator, InkyDecorator and PinkyDecorator.

I created an observer pattern so that Inky can obtain Blinky’s location. This design pattern involves BlinkyPositionObserver, BlinkyPositionSubject, InkyDecorator and BlinkyDecorator.

Strategy pattern is used for ghosts to control their behaviours in frightened mode and non-frightened mode. This pattern involves GhostImpl, GhostBehavior, FrightenedBehavior and NormalBehaviour class.

Singleton pattern is used to hold the number of ghosts pacman has eaten in the current frightened period. This pattern involves LevelImpl class and GhostCounter class.

State pattern is used for different states of pacman. There are two states for pacman: when ghost are not in frightened mode and when ghosts are in frightened mode. This pattern involves Pacman, State, NormalState and BraveState class.

This program runs smoothly. All modifications are made to implement the required features.
