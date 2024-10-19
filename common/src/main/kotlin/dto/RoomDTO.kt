package dto

import kotlinx.serialization.Serializable

@Serializable
data class RoomDTO(
    val id: Int,
    val name: String,
    val connections: List<Int>,
    val imageId: Int,
)
