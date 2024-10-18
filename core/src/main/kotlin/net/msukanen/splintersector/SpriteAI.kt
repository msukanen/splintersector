package net.msukanen.splintersector

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite

class SpriteAI(
    texture: Texture,
    val id: Int,
) : Sprite(texture) {
    var speed: Float = 0f
    var speedDelta: Float = 1f
}
