package waffle.guam.community.data.jdbc.letter

import org.springframework.data.jpa.repository.JpaRepository

interface LetterRepository : JpaRepository<LetterEntity, Long>
