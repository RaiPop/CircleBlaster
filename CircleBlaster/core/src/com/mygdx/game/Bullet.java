package com.mygdx.game;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;

public class Bullet extends ModelInstance {
    public Vector3 direction;

    public Bullet (Model mod, float x, float y, float z) {
        super(mod, x, y, z);
    }
    public Bullet (Model mod, Vector3 vec) {
        super(mod, vec);
    }
}
