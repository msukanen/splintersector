package dto

import kotlinx.serialization.Serializable

@kotlinx.serialization.Serializable
data class RoomDTO(
    val id: Int,
    val name: String,
    val connections: List<Int>,
    val imageId: Int,
)
