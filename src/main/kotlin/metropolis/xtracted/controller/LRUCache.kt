package metropolis.xtracted.controller

class LRUCache<key, value>(private val maxSize: Int) : LinkedHashMap<key, value>(maxSize, 0.75f, true) {

    override fun removeEldestEntry(eldest: MutableMap.MutableEntry<key, value>?): Boolean {
        return size > maxSize
    }
}