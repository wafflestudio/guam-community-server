package waffle.guam.community.data.jdbc.category

import org.springframework.data.jpa.repository.JpaRepository

interface CategoryRepository : JpaRepository<CategoryEntity, Long>
