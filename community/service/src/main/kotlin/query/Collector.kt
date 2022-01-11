package waffle.guam.community.service.query

interface Collector<D, ID> {
    fun get(id: ID): D
}
