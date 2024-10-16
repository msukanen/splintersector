package net.msukanen.splintersector

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.ScreenUtils
import com.badlogic.gdx.utils.viewport.FitViewport
import ktx.app.KtxApplicationAdapter
import ktx.graphics.*
import kotlin.random.Random

/**
 * Just maining my business here.
 */
class Main : KtxApplicationAdapter {
    private val batch by lazy { SpriteBatch() }
    private val image by lazy { Texture("libgdx.png") }
    private val backgroundTexture by lazy { Texture("background.png") }
    private val bucket by lazy {
        Sprite(Texture("bucket.png")).apply {
            setSize(1f, 1f)
        }
    }
    private val dropTexture by lazy { Texture("drop.png") }
    private val viewport by lazy { FitViewport(8f, 5f) }
    private val touchPos by lazy { Vector2() }
    private val dropMap = mutableMapOf<Int, Sprite>()
    private var lastDropId = -1
    private val delta: Float
        get() = Gdx.graphics.deltaTime
    private var dropTimer = 0f

    /**
     * Let's handle input, logic and actual rendering here.
     */
    override fun render() {
        input()
        logic()
        draw()
    }

    /**
     * Resize the ViewPort.
     */
    override fun resize(width: Int, height: Int) {
        viewport.update(width, height, true)
    }

    /**
     * Input handing - the horror show.
     */
    private fun input() {
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) ||
            Gdx.input.isKeyPressed(Input.Keys.D)) {
            bucket.translateX(PLAYER_SPEED * delta)
        } else if (Gdx.input.isKeyPressed(Input.Keys.LEFT) ||
            Gdx.input.isKeyPressed(Input.Keys.A)) {
            bucket.translateX(-PLAYER_SPEED * delta)
        }

        if (Gdx.input.isTouched) {
            touchPos.set(Gdx.input.x.toFloat(), Gdx.input.y.toFloat())
            viewport.unproject(touchPos)
            bucket.setCenterX(touchPos.x)
        }
    }

    /**
     * Some Vulcan activity... a.k.a. logic.
     */
    private fun logic() {
        // Clamp X into range of [0..worldWidth].
        bucket.x = bucket.x.coerceIn(0f, viewport.worldWidth - bucket.width)
        with (delta) {
            dropMap.forEach { e -> e.value.translateY(-DROPLET_SPEED * this) }
            dropTimer += this
        }

        // Introduce some variability to droplet spawning time.
        if (dropTimer > (1f..2f).random()) {
            dropTimer = 0f
            createDroplet()
        }
    }

    /**
     * Stick figures! Also, other things.
     */
    private fun draw() {
        ScreenUtils.clear(Color.BLACK)
        viewport.apply()
        batch.projectionMatrix = viewport.camera.combined
        batch.use {
            batch.draw(backgroundTexture, 0f, 0f, viewport.worldWidth, viewport.worldHeight)
            bucket.draw(batch)
            dropMap.forEach { e -> e.value.draw(batch) }
        }
    }

    /**
     * Dropsies are made here, yes.
     */
    private fun createDroplet() {
        val drop = Sprite(dropTexture)
        drop.setSize(DROPLET_SIZE.first, DROPLET_SIZE.second)
        drop.x = (0f..viewport.worldWidth - drop.width).random()
        drop.y = viewport.worldHeight
        lastDropId += 1
        dropMap[lastDropId] = drop
    }

    companion object {
        val DROPLET_SIZE = Pair(1f, 1f)
        const val DROPLET_SPEED = 2f
        const val PLAYER_SPEED = 4f
    }
}
