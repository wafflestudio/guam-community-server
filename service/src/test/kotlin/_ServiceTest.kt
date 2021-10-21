package waffle.guam.community

import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.listeners.Listener
import io.kotest.core.spec.IsolationMode
import io.kotest.spring.SpringListener
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
class TestApplication

class ProjectConfig : AbstractProjectConfig() {
    override fun listeners(): List<Listener> = listOf(SpringListener)
    override val isolationMode = IsolationMode.InstancePerLeaf
}
