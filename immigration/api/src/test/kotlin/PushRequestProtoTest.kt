package waffle.guam.immigration.api

import org.junit.jupiter.api.Test
import waffle.guam.immigration.api.user.SendPushRequest
import waffle.guam.immigration.api.user.toProto

class PushRequestProtoTest {
    @Test
    fun sendPushRequest() {
        val request = SendPushRequest(listOf(1, 2, 3), "", "", "")

        assert(request == SendPushRequest(request.toProto()))

        val imageNullRequest = request.copy(imagePath = null)

        assert(imageNullRequest == SendPushRequest(imageNullRequest.toProto()))
    }
}
