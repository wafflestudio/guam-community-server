package waffle.guam.community.controller.project

import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import waffle.guam.community.UserContext
import waffle.guam.community.controller.project.req.GetProjectList
import waffle.guam.community.controller.project.req.PostProject
import waffle.guam.community.controller.project.req.PutProject

@RestController
@RequestMapping("api/v1/projects")
class ProjectController {
    @GetMapping
    fun getList(
        userContext: UserContext,
        request: GetProjectList,
    ) {

    }

    @GetMapping
    fun getDetail(
        @RequestParam projectId: Long,
    ) {

    }

    @PostMapping
    fun post(
        @RequestBody request: PostProject,
    ) {

    }

    @PutMapping
    fun update(
        @RequestBody request: PutProject
    ) {

    }

    @DeleteMapping
    fun delete(
        @RequestParam projectId: Long,
    ) {

    }
}
