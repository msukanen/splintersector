package net.msukanen.splintersector

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.ScreenUtils
import com.badlogic.gdx.utils.viewport.FitViewport
import ktx.app.KtxApplicationAdapter
import ktx.graphics.*

/**
 * Just maining my business here.
 */
class Main : KtxApplicationAdapter {
    private val batch by lazy { SpriteBatch() }
    //private val image by lazy { Texture("libgdx.png") }
    private val backgroundTexture by lazy { Texture("background.png") }
    private val bucket by lazy {
        Sprite(Texture("wizardess-elf.png")).apply {
            setSize((327f/448f)*INITIAL_PLAYER_SCALE, 1f*INITIAL_PLAYER_SCALE)
        }
    }
    private val bucketRect = lazy { Rectangle() }
    private val dropTexture by lazy { Texture("drop.png") }
    private val viewport by lazy { FitViewport(8f, 5f) }
    private val touchPos by lazy { Vector2() }
    private val dropMap = mutableMapOf<Int, Pair<SpriteAI, Rectangle>>()
    private var lastDropId = -1
    private val delta: Float
        get() = Gdx.graphics.deltaTime
    private var dropTimer = 0f

    override fun create() {
        super.create()
        //music.play()
    }
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
        if (!bucketRect.isInitialized()) {
            bucketRect.value.width = bucket.width
            bucketRect.value.height = bucket.height
        }
        bucketRect.value.x = bucket.x
        bucketRect.value.y = bucket.y
        with (delta) {
            var eraseList = mutableListOf<Pair<SpriteAI, Rectangle>>()
            dropMap.forEach { e->
                e.value.first.translateY(-(e.value.first.speed + e.value.first.speedDelta) * this)
                e.value.second.y = e.value.first.y
                // Fast, faster, ludicrous speed...
                e.value.first.speedDelta += DROPLET_SPEED_DELTA_CAP_INCREASE
                // Add a fall-through droplet into to-be-erased list.
                val caught = e.value.second.overlaps(bucketRect.value)
                if (e.value.first.y < -e.value.first.height || caught) {
                    if (caught) {
                        dropSound.play()
                    }
                    eraseList.add(e.value)
                }
            }
            // Dry the unneeded buggers...
            eraseList.forEach { e-> dropMap.remove(e.first.id) }

            dropTimer += this
        }

        // Introduce some variability to droplet spawning time.
        if (dropTimer > (0.5f..2.5f).random()) {
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
            dropMap.forEach { e -> e.value.first.draw(batch) }
        }
    }

    /**
     * Dropsies are made here, yes.
     */
    private fun createDroplet() {
        lastDropId += 1
        val drop = SpriteAI(dropTexture, lastDropId)
        drop.setSize(DROPLET_SIZE.first, DROPLET_SIZE.second)
        drop.x = (0f..viewport.worldWidth - drop.width).random()
        drop.y = viewport.worldHeight
        drop.speedDelta = DROPLET_SPEED_DELTA_CAP_INCREASE
        drop.speed = lastDropId.toFloat() * DROPLET_SPEED_DELTA_CAP_INCREASE // Let's increase initial speed gradually.
        val dropRect = Rectangle(drop.x, drop.y, drop.width, drop.height)
        dropMap[lastDropId] = Pair(drop, dropRect)
    }

    companion object {
        val DROPLET_SIZE = Pair(1f, 1f)
        const val DROPLET_SPEED_DELTA_CAP_INCREASE = 0.025f
        const val PLAYER_SPEED = 4f
        private val dropSound by lazy { Gdx.audio.newSound(Gdx.files.internal("drop.mp3")) }
        private val music by lazy {
            val mus = Gdx.audio.newMusic(Gdx.files.internal("seerie.mp3"))
            mus.volume = 1f
            mus.isLooping = true
            mus
        }
        const val INITIAL_PLAYER_SCALE = 2f
    }
}
