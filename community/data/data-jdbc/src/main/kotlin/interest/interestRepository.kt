package waffle.guam.community.data.jdbc.interest

import org.springframework.data.jpa.repository.JpaRepository

interface interestRepository : JpaRepository<InterestEntity, InterestId>
