package waffle.guam.community

import org.slf4j.Logger
import org.slf4j.LoggerFactory

class Utils {
    // add any common POJO util logics here
}

interface Log {
    val log: Logger
        get() = LoggerFactory.getLogger(this.javaClass)
}
