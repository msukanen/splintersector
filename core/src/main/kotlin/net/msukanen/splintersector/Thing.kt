package net.msukanen.splintersector

import com.badlogic.gdx.graphics.Texture

class Thing(val id: String) {
    public val ongroundImage by lazy { Texture(id) }
}
