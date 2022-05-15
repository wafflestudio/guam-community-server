package waffle.guam.community.data.jdbc.letter

import org.springframework.data.jpa.domain.Specification
import org.springframework.data.jpa.repository.JpaRepository

interface LetterRepository : JpaRepository<LetterEntity, Long> {
    fun findAll(spec: Specification<LetterEntity>): List<LetterEntity>
}
