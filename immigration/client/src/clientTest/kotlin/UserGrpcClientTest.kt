package waffle.guam.immigration.client.test

import io.grpc.ManagedChannelBuilder
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import waffle.guam.immigration.api.user.GetUserRequest
import waffle.guam.immigration.app.ImmigrationApplication
import waffle.guam.immigration.client.user.UserGrpcClient

@SpringBootTest(
    classes = [ImmigrationApplication::class],
    webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
    properties = ["server.port=7777"]
)
class ItemGrpcClientTest @Autowired constructor(
    @Value("\${server.port}")
    private val port: Int
) {
    @Test
    fun a(): Unit = runBlocking {
        val client = UserGrpcClient(ManagedChannelBuilder.forAddress("localhost", port).usePlaintext().build())
        println(client.getUser(GetUserRequest("test")))
    }
}
