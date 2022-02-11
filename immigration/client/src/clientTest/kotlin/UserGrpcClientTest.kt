package waffle.guam.immigration.client.test

import io.grpc.ManagedChannelBuilder
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import waffle.guam.immigration.api.user.GetUserRequest
import waffle.guam.immigration.api.user.UpdateUserDeviceRequest
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
    private val client = UserGrpcClient(ManagedChannelBuilder.forAddress("localhost", port).usePlaintext().build())

    @Test
    fun getUserRequest(): Unit = runBlocking {
        println(client.getUser(GetUserRequest("test")))
    }

    @Test
    fun updateUserDeviceRequest(): Unit = runBlocking {
        val deviceId = "someString"
        client.updateUserDevice(UpdateUserDeviceRequest("test", deviceId))
    }
}
