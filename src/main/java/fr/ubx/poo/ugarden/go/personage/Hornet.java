package fr.ubx.poo.ugarden.go.personage;

import fr.ubx.poo.ugarden.game.Direction;
import fr.ubx.poo.ugarden.game.Game;
import fr.ubx.poo.ugarden.game.Map;
import fr.ubx.poo.ugarden.game.Position;
import fr.ubx.poo.ugarden.go.GameObject;
import fr.ubx.poo.ugarden.go.Movable;
import fr.ubx.poo.ugarden.go.Takeable;
import fr.ubx.poo.ugarden.go.decor.*;

import java.util.Random;

public class Hornet extends GameObject implements Movable, Takeable {
    private long lastMoveTime;
    private final long hornetMoveFrequency;
    public Direction direction = Direction.UP;

    public Hornet(Game game, Position position) {
        super(game, position);
        this.lastMoveTime = System.currentTimeMillis();
        this.hornetMoveFrequency = game.configuration().hornetMoveFrequency() * 1000L;
    }

    @Override
    public boolean canMove(Direction direction) {
        Position nextPos = direction.nextPosition(getPosition());
        Map currentMap = game.world().getGrid();
        if (currentMap.inside(nextPos)) {
            Decor nextDecor = currentMap.get(nextPos);
            GameObject nextEntity = currentMap.getEntity(nextPos);
            return !(nextDecor instanceof Tree) &&
                    !(nextEntity instanceof Hornet) &&
                    !(nextEntity instanceof Hedgehog) &&
                    !(nextEntity instanceof DoorNextOpened) &&
                    !(nextEntity instanceof DoorNextClosed) &&
                    !(nextEntity instanceof DoorPrevOpened);
        }
        return false;
    }

    @Override
    public void doMove(Direction direction) {
        Position nextPos = direction.nextPosition(getPosition());
        setPosition(nextPos);
        this.direction = direction;
        this.lastMoveTime = System.currentTimeMillis();
    }

    public void update(long now) {
        long elapsedTime = (now - lastMoveTime) / 1000000;
        if (elapsedTime >= hornetMoveFrequency) {
            Direction randomDirection = getRandomDirection();
            if (canMove(randomDirection)) {
                doMove(randomDirection);
            }
            lastMoveTime = now;
        }
    }

    private Direction getRandomDirection() {
        Direction[] directions = Direction.values();
        Random random = new Random();
        Direction direction;
        do {
            direction = directions[random.nextInt(directions.length)];
        } while (!canMove(direction));
        return direction;
    }

    @Override
    public void takenBy(Gardener gardener) {
        gardener.hurt();
    }

    public Direction getDirection() {
        return direction;
    }
}
