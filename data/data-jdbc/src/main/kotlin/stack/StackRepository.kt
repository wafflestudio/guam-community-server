package waffle.guam.community.data.jdbc.stack

import org.springframework.data.jpa.repository.JpaRepository

interface StackRepository : JpaRepository<StackEntity, StackId>
