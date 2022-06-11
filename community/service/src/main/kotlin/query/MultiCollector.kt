package waffle.guam.community.service.query

interface MultiCollector<D, ID> : Collector<D, ID> {
    fun multiGet(ids: Collection<ID>): Map<ID, D>
}
