package fr.ubx.poo.ugarden.go.personage;

import fr.ubx.poo.ugarden.game.Direction;
import fr.ubx.poo.ugarden.game.Game;
import fr.ubx.poo.ugarden.game.Map;
import fr.ubx.poo.ugarden.game.Position;
import fr.ubx.poo.ugarden.go.GameObject;
import fr.ubx.poo.ugarden.go.Movable;
import fr.ubx.poo.ugarden.go.TakeVisitor;
import fr.ubx.poo.ugarden.go.WalkVisitor;
import fr.ubx.poo.ugarden.go.bonus.Apple;
import fr.ubx.poo.ugarden.go.bonus.Bonus;
import fr.ubx.poo.ugarden.go.bonus.Key;
import fr.ubx.poo.ugarden.go.bonus.PoisonedApple;
import fr.ubx.poo.ugarden.go.decor.*;
import javafx.application.Platform;
import javafx.scene.text.Text;
import java.util.Timer;
import java.util.TimerTask;

public class Gardener extends GameObject implements Movable, TakeVisitor, WalkVisitor {

    private final int maxEnergy;
    private int energy;

    private int diseaseLevel = 1;

    private Direction direction;
    private boolean moveRequested = false;
    private long lastMoveTime;
    private long lastRecoverTime;

    public Gardener(Game game, Position position) {
        super(game, position);
        this.direction = Direction.DOWN;
        this.maxEnergy = game.configuration().gardenerEnergy();
        this.energy = maxEnergy;
        this.lastMoveTime = System.currentTimeMillis();
        this.lastRecoverTime = lastMoveTime;
    }

    public void take(Apple apple) {
        this.diseaseLevel = 1;
        int energyBoost = game.configuration().energyBoost();
        this.energy += energyBoost;
        if (this.energy > this.maxEnergy) {
            this.energy = this.maxEnergy;
        }
        apple.remove();
    }

    public void take(PoisonedApple poisonedApple) {
        this.diseaseLevel += 1;
        int diseaseDuration = game.configuration().diseaseDuration();
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (getDiseaseLevel() <= 1) {
                    diseaseLevel = 1;
                } else {
                    diseaseLevel -= 1;
                }
                timer.cancel();
            }
        }, diseaseDuration * 1000L); // Convert seconds to milliseconds
        poisonedApple.remove();
    }

    @Override
    public void take(Key key) {
        key.remove();
    }

    public int getEnergy() {
        return this.energy;
    }

    public int getDiseaseLevel() {
        return this.diseaseLevel;
    }

    public void requestMove(Direction direction) {
        if (direction != this.direction) {
            this.direction = direction;
            setModified(true);
        }
        moveRequested = true;
    }

    @Override
    public final boolean canMove(Direction direction) {
        Position nextPos = direction.nextPosition(getPosition());
        Map currentMap = game.world().getGrid();
        if (currentMap.inside(nextPos)) {
            Decor nextDecor = currentMap.get(nextPos);
            return !(nextDecor instanceof Flowers) && !(nextDecor instanceof Tree);
        }
        return false;
    }

    @Override
    public void doMove(Direction direction) {
        Position nextPos = direction.nextPosition(getPosition());
        Decor next = game.world().getGrid().get(nextPos);
        this.energy -= next.energyConsumptionWalk() * getDiseaseLevel();
        setPosition(nextPos);
        if (next != null) {
            next.takenBy(this);
        }
        this.lastMoveTime = System.currentTimeMillis();
    }

    public void update(long now) {
        if (moveRequested) {
            if (canMove(direction)) {
                doMove(direction);
            }
        }
        moveRequested = false;
        long elapsedTime = System.currentTimeMillis() - lastMoveTime;
        if (elapsedTime >= game.configuration().energyRecoverDuration() * 1000L) {
            recoverEnergy();
        }
    }

    private void recoverEnergy() {
        long elapsedTime = System.currentTimeMillis() - lastRecoverTime;
        if (elapsedTime >= game.configuration().energyRecoverDuration() * 1000L) {
            energy++;
            if (energy > maxEnergy) {
                energy = maxEnergy;
            }
            lastRecoverTime = System.currentTimeMillis();
        }
    }

    public void hurt(int damage) {
        energy -= damage;
    }

    public void hurt() {
        hurt(1);
    }

    public Direction getDirection() {
        return direction;
    }
}
