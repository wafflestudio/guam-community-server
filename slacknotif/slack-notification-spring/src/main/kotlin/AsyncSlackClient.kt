package io.wafflestudio.spring.slack

import java.util.concurrent.Future
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadFactory
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

class AsyncSlackClient(
    options: SlackClientOptions,
) : SlackClient {

    private val runner = QueuedThreadExecutor(maxQueueSize = options.maxQueueSize)

    override fun isEnabled() {
        // enable -> send
        TODO("Not yet implemented")
    }

    override fun captureEvent(e: SlackEvent) {
        TODO("Not yet implemented")
    }

    class QueuedThreadExecutor : ThreadPoolExecutor {
        constructor(maxQueueSize: Int) : super(
            1,
            maxQueueSize,
            0L,
            TimeUnit.MILLISECONDS,
            LinkedBlockingQueue(),
            DaemonThreadFactory()
        )

        override fun submit(task: Runnable): Future<*> {
            return super.submit(task)
        }

        override fun afterExecute(r: Runnable?, t: Throwable?) {
            super.afterExecute(r, t)
        }

        private class DaemonThreadFactory : ThreadFactory {
            private var cnt = 0

            override fun newThread(r: Runnable): Thread {

                val ret = Thread(r, "SlackAsyncConnection-" + cnt++)
                ret.isDaemon = true
                return ret
            }
        }
    }
}
