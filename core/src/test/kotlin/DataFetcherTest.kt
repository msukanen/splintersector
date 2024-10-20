import com.badlogic.gdx.Gdx
import com.badlogic.gdx.backends.headless.HeadlessApplication
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import ktx.app.KtxApplicationAdapter
import net.msukanen.splintersector.DataFetcher
import net.msukanen.splintersector.server.ServerLauncher
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class DataFetcherTest {
    private val serverControl = ServerLauncher
    private lateinit var dataFetcher: DataFetcher
    private lateinit var headlessApp: HeadlessApplication

    @BeforeTest
    fun start() {
        serverControl.start()
        Thread.sleep(1500)
        val cfg = HeadlessApplicationConfiguration()
        headlessApp = HeadlessApplication(object : KtxApplicationAdapter {}, cfg)
        dataFetcher = DataFetcher(Gdx.app, Gdx.net)
    }

    @AfterTest
    fun stop() {
        headlessApp.exit()
        serverControl.stop()
    }

    @Test
    fun `fetch API usage`() {
        runBlocking {
            val deferred = async(Dispatchers.IO) {
                suspendCancellableCoroutine<String> { cont ->
                    dataFetcher.fetchApiUsage(object : DataFetcher.DataCallback {
                        override fun onSuccess(data: String) {
                            cont.resumeWith(Result.success(data))
                        }

                        override fun onFailure(t: Throwable) {
                            cont.resumeWith(Result.failure(t))
                        }
                    })
                }
            }

            val data = deferred.await()
            println("Yay for:\n$data")

            val json = Json { prettyPrint = true }
            val apiUsage = json.decodeFromString<ApiUsage>(data)
            assertEquals(3, apiUsage.call.size)
            assertEquals("fetch info about a specific room", apiUsage.call[0].room)
        }
    }
}

@Serializable
data class ApiUsage(val call: List<Call>)
@Serializable
data class Call(val room: String? = null, val item: String? = null, val monster: String? = null)
