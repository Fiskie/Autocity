package autocity.core.generators.builders;

import autocity.core.Settlement;
import autocity.core.exceptions.TileOutOfBoundsException;
import autocity.core.world.paths.Road;

public class RoadBuilder {
    private Settlement settlement;

    public RoadBuilder(Settlement settlement) {
        this.settlement = settlement;
    }

    public void generateStartingRoads() {
        Road road = new Road();

        try {
            this.settlement.getOriginTile().setOccupyingObject(road);
        } catch (TileOutOfBoundsException e) {
            // Not expected
        }
    }
}