/*
 * Copyright (c) 2020. Laurent Réveillère
 */

package fr.ubx.poo.ugarden.go.decor;

import fr.ubx.poo.ugarden.game.Position;
import fr.ubx.poo.ugarden.go.personage.Gardener;

public class DoorNextClosed extends Decor {
    public DoorNextClosed(Position position) {
        super(position);
    }

    @Override
    public boolean walkableBy(Gardener gardener) {
        return gardener.canWalkOn(this);
    }
}
