package tech.rkanelabs.marblelab.data

data class Tile(
    val type: TileType = TileType.Floor,
    val walls: WallMask = WallMask.None
)

enum class TileType {
    Empty,
    Floor,
    Marble,
    Goal,
    Hole,
}

@JvmInline
value class WallMask(val bits: Int) {
    companion object {
        val None = WallMask(0)
        val Up = WallMask(1 shl 0)
        val Right = WallMask(1 shl 1)
        val Down = WallMask(1 shl 2)
        val Left = WallMask(1 shl 3)
        val All = Up.with(Right).with(Down).with(Left)
    }

    fun has(mask: WallMask) = (bits and mask.bits) != 0
    fun with(mask: WallMask) = WallMask(bits or mask.bits)
    fun without(mask: WallMask) = WallMask(bits and mask.bits)
}