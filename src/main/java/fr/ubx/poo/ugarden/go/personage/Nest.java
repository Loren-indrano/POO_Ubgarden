package fr.ubx.poo.ugarden.go.personage;

import fr.ubx.poo.ugarden.game.Direction;
import fr.ubx.poo.ugarden.game.Game;
import fr.ubx.poo.ugarden.game.Position;
import fr.ubx.poo.ugarden.go.GameObject;
import java.util.Timer;

public class Nest extends GameObject {
    private final Game game;
    private final Position position;
    private long lastSpawnTime;

    public Nest(Game game, Position position) {
        super(game, position);
        this.game = game;
        this.position = position;
        this.lastSpawnTime = System.currentTimeMillis();
    }

    public void update(long now) {
        long elapsedTime = System.currentTimeMillis() - lastSpawnTime;
        if (elapsedTime >= 10000) {
            game.world().getGrid().setEntity(new Hornet(game, position));
            lastSpawnTime = System.currentTimeMillis();
        }
    }
}
