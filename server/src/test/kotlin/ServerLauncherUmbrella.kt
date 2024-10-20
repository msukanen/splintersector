import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import net.msukanen.splintersector.server.ServerLauncher
import org.junit.jupiter.api.extension.AfterAllCallback
import org.junit.jupiter.api.extension.BeforeAllCallback
import org.junit.jupiter.api.extension.ExtensionContext
import java.lang.Thread.sleep

class ServerLauncherUmbrella : BeforeAllCallback, AfterAllCallback {
    private val server = ServerLauncher
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    override fun beforeAll(context: ExtensionContext?) {
        scope.launch {
            server.start()
        }
        sleep(1000)
    }

    override fun afterAll(context: ExtensionContext?) {
        server.stop()
    }
}
