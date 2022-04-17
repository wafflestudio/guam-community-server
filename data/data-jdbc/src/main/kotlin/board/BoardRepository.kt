package waffle.guam.community.data.jdbc.board

import org.springframework.data.jpa.repository.JpaRepository

interface BoardRepository : JpaRepository<BoardEntity, Long>
