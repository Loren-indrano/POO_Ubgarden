package fr.ubx.poo.ugarden.game;


import fr.ubx.poo.ugarden.go.decor.Decor;
import fr.ubx.poo.ugarden.go.GameObject;

import java.util.Collection;

public interface Map {
    int width();

    int height();

    Decor get(Position position);
    GameObject getEntity(Position position);

    void remove(Position position);

    void removeEntity(Position position, GameObject object);

    Collection<Decor> values();
    Collection<GameObject> entities();

    boolean inside(Position nextPos);

    void set(Position position, Decor decor);

    void setEntity(GameObject gameObject);

    void checkDoor();
    int keyCount();
}
