import kotlin.test.Test
import kotlin.test.assertEquals

class QueryTest {
    @Test
    fun `see that query gets split accordingly`() {
        val query = Query("/splnsect/api/room?id=2&x=3&y=3=4")
        val path = query.path
        assertEquals("/splnsect/api/room", path)
        assertEquals("2", query.items["id"])
        assertEquals("3", query.items["x"])
        assertEquals("3=4", query.items["y"])
    }
}
