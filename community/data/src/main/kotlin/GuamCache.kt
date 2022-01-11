package waffle.guam.community.data

interface GuamCache<V : Any, K : Any> {
    fun get(key: K): V
    fun multiGet(keys: Collection<K>): Map<K, V>
    fun reload(key: K)
    fun invalidate(key: K)
}
