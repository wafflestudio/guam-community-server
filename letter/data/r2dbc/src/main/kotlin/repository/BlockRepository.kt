package waffle.guam.letter.data.r2dbc.repository

import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import waffle.guam.letter.data.r2dbc.data.BlockEntity

interface BlockRepository : CoroutineCrudRepository<BlockEntity, Long>
