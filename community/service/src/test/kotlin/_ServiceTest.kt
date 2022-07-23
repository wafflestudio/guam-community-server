package waffle.guam.community

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Import
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Service
import waffle.guam.community.service.client.NotificationRequest
import waffle.guam.community.service.client.NotificationService
import waffle.guam.community.service.command.image.ImageListUploaded
import waffle.guam.community.service.command.image.ImagePath
import waffle.guam.community.service.command.image.UploadImageList
import waffle.guam.community.service.command.image.UploadImageListHandler

@Import(HibernateConfig::class)
@SpringBootApplication
class TestApplication {

    @Primary
    @Service
    class MockImageHandler : UploadImageListHandler {
        override fun handle(command: UploadImageList): ImageListUploaded {
            return command.imagePaths.mapIndexed { i, _ -> "TEST/$i" }
                .map { ImagePath(it, it) }
                .let { ImageListUploaded(it) }
        }
    }

    @Primary
    @Service
    class MockNotificationService : NotificationService {
        override fun notify(request: NotificationRequest) {
            return
        }
    }
}
