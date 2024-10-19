package net.msukanen.splintersector

class Dungeon(val id: String, mainRoomId: String) {
    public val mainEntrance by lazy { Room(mainRoomId) }

}
