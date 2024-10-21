class Query(query: String?) {
    private var _path: String
    private val _items = mutableMapOf<String, String?>()

    init {
        val q = query?.split("?", limit = 2)
        if (q != null) {
            _path = q[0]
            if (q.size > 1) {
                q[1].split("&").forEach { it ->
                    val item = it.split("=", limit = 2)
                    if (item.size > 1) {
                        _items[item[0]] = item[1]
                    } else {
                        _items[item[0]] = null
                    }
                }
            }
        } else {
            _path = query ?: ""
        }
    }

    var path
        get() = _path
        set(value) {
            //TODO: sanitizing the path part.
            _path = value
        }
    val items get() = _items
    override fun toString(): String {
        val map = items.map { i -> "${i.key}=${i.value}" }.joinToString("&")
        return "$path?$map"
    }
}
