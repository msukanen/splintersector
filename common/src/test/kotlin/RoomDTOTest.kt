import dto.RoomDTO
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertTrue

class RoomDTOTest {
    @Test
    fun roomDTO() {
        val obj = Json.decodeFromString<RoomDTO>("""{
            "name": "A test room",
            "id": 1,
            "imageId": 1,
            "connections": [2,3]
        }""".trimMargin())
        assertTrue(obj.name == "A test room")
    }
}
