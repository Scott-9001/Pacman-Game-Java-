package pacman.model.factories;

import javafx.scene.image.Image;
import pacman.ConfigurationParseException;
import pacman.model.entity.Renderable;
import pacman.model.entity.dynamic.physics.BoundingBox;
import pacman.model.entity.dynamic.physics.BoundingBoxImpl;
import pacman.model.entity.dynamic.physics.Vector2D;
import pacman.model.entity.staticentity.collectable.Pellet;

/**
 * Concrete renderable factory for Pellet objects
 */
public class PowerPelletFactory implements RenderableFactory {
    private static final Image PELLET_IMAGE = new Image("maze/pellet.png");
    private static final int NUM_POINTS = 50;
    private final Renderable.Layer layer = Renderable.Layer.BACKGROUND;

    @Override
    public Renderable createRenderable(Vector2D position) {
        try {

            Vector2D newPosition = position.adjustOffset();
            BoundingBox boundingBox = new BoundingBoxImpl(
                    newPosition,
                    2*PELLET_IMAGE.getHeight(),
                    2*PELLET_IMAGE.getWidth()
            );

            Pellet powerPellet = new Pellet(
                    boundingBox,
                    layer,
                    PELLET_IMAGE,
                    NUM_POINTS
            );

            powerPellet.setIsPowerPellet(true);

            return powerPellet;

        } catch (Exception e) {
            throw new ConfigurationParseException(
                    String.format("Invalid pellet configuration | %s", e));
        }
    }
}
