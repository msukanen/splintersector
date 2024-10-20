import net.msukanen.splintersector.server.SERVER_PORT
import org.junit.jupiter.api.extension.ExtendWith
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URI
import kotlin.test.Test
import kotlin.test.assertTrue

@ExtendWith(ServerLauncherUmbrella::class)
class ServerLauncherTest {
    @Test
    fun fetchMainAPI() {
        val url = URI.create("http://localhost:$SERVER_PORT/splnsect/api").toURL()
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "GET"
        assertTrue(connection.responseCode == HttpURLConnection.HTTP_OK,
                   "Expected HTTP_OK (200) but got ${connection.responseCode} instead")
        val json = BufferedReader(InputStreamReader(connection.inputStream)).readText()
        println(json)
    }
}
