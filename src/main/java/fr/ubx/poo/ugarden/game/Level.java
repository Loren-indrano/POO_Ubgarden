package fr.ubx.poo.ugarden.game;

import fr.ubx.poo.ugarden.go.GameObject;
import fr.ubx.poo.ugarden.go.bonus.Key;
import fr.ubx.poo.ugarden.go.decor.*;
import fr.ubx.poo.ugarden.go.decor.ground.Carrots;
import fr.ubx.poo.ugarden.go.decor.ground.Grass;
import fr.ubx.poo.ugarden.go.decor.ground.Land;
import fr.ubx.poo.ugarden.go.bonus.PoisonedApple;
import fr.ubx.poo.ugarden.go.bonus.Apple;
import fr.ubx.poo.ugarden.go.personage.Hedgehog;
import fr.ubx.poo.ugarden.go.personage.Nest;
import fr.ubx.poo.ugarden.launcher.MapEntity;
import fr.ubx.poo.ugarden.launcher.MapLevel;

import java.util.Collection;
import java.util.HashMap;
import java.util.ArrayList;

public class Level implements Map {

    private final int level;
    private final int width;

    private final int height;

    private final java.util.Map<Position, Decor> decors = new HashMap<>();
    private final java.util.List<GameObject> gameObjects = new ArrayList<>();

    public Level(Game game, int level, MapLevel entities) {
        this.level = level;
        this.width = entities.width();
        this.height = entities.height();

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                Position position = new Position(level, i, j);
                MapEntity mapEntity = entities.get(i, j);
                switch (mapEntity) {
                    case Grass:
                        decors.put(position, new Grass(position));
                        break;
                    case Tree:
                        decors.put(position, new Tree(position));
                        break;
                    case PoisonedApple:
                        Decor poisonAppleGrass = new Grass(position);
                        poisonAppleGrass.setBonus(new PoisonedApple(position, poisonAppleGrass));
                        decors.put(position, poisonAppleGrass);
                        break;
                    case Land:
                        decors.put(position, new Land(position));
                        break;
                    case Flowers:
                        decors.put(position, new Flowers(position));
                        break;
                    case DoorNextClosed:
                        decors.put(position, new DoorNextClosed(position));
                        break;
                    case DoorNextOpened:
                        decors.put(position, new DoorNextOpened(position));
                        break;
                    case DoorPrevOpened:
                        decors.put(position, new DoorPrevOpened(position));
                        break;
                    case Apple:
                        Decor appleGrass = new Grass(position);
                        appleGrass.setBonus(new Apple(position, appleGrass));
                        decors.put(position, appleGrass);
                        break;
                    case Carrots:
                        decors.put(position, new Carrots(position));
                        break;
                    case Nest:
                        decors.put(position, new Grass(position));
                        GameObject nest = new Nest(game, position);
                        gameObjects.add(nest);
                        break;
                    case Hedgehog:
                        decors.put(position, new Grass(position));
                        GameObject hedgehog = new Hedgehog(game, position);
                        gameObjects.add(hedgehog);
                        break;
                    case Key: {
                        Decor grass = new Grass(position);
                        grass.setBonus(new Key(position, grass));
                        decors.put(position, grass);
                        break;
                    }
                    default:
                        throw new RuntimeException("EntityCode " + mapEntity.name() + " not processed");
                }
            }
        }
    }

    @Override
    public int width() {
        return this.width;
    }

    @Override
    public int height() {
        return this.height;
    }

    public Decor get(Position position) {
        return decors.get(position);
    }

    @Override
    public GameObject getEntity(Position position) {
        for (GameObject gameObject : gameObjects) {
            if (gameObject.getPosition().equals(position)) {
                return gameObject;
            }
        }
        return null;
    }

    @Override
    public void remove(Position position) {
        decors.remove(position);
    }

    @Override
    public void removeEntity(Position position, GameObject object) {
        for (GameObject gameObject : gameObjects) {
            if (gameObject.getPosition().equals(position) && gameObject.equals(object)) {
                gameObjects.remove(gameObject);
                object.remove();
                break;
            }
        }
    }

    public Collection<Decor> values() {
        return decors.values();
    }

    public Collection<GameObject> entities() {
        return gameObjects;
    }

    @Override
    public boolean inside(Position position) {
        return position.x()>=0 && position.x()<=width-1 && position.y()>=0 && position.y()<=height-1;
    }

    @Override
    public void set(Position position, Decor decor) {
        if (!inside(position)) {
            throw new IllegalArgumentException("Illegal Position");
        } else if (decor != null) {
            decors.put(position, decor);
        }
    }

    @Override
    public void setEntity(GameObject gameObject) {
        if (gameObject != null)
            gameObjects.add(gameObject);
    }

    public int keyCount() {
        int keyCount = 0;
        for (Decor decor : decors.values()) {
            if (decor.getBonus() instanceof Key) {
                keyCount++;
            }
        }
        return keyCount;
    }

    @Override
    public void checkDoor() {
        if (keyCount() == 0) {
            for (Decor decor : new ArrayList<>(decors.values())) {
                if (decor instanceof DoorNextClosed) {
                    setEntity(new DoorNextOpened(decor.getPosition()));
                    decor.remove();
                    break;
                }
            }
        }
    }
}
