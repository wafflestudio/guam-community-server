package waffle.guam.community.data.jdbc.tag

import org.springframework.data.jpa.repository.JpaRepository

interface TagRepository : JpaRepository<TagEntity, Long>
