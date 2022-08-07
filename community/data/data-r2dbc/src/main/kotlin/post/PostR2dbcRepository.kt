package waffle.guam.community.data.r2dbc.post

import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface PostR2dbcRepository : CoroutineCrudRepository<PostEntity, Long>
