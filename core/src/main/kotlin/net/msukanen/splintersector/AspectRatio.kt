package net.msukanen.splintersector

enum class AspectRatio {
    AR_4_3,
    AR_16_9,
    AR_16_10;

    companion object {
        fun getDimensions(width: Int, aspectRatio: AspectRatio) = Pair(width, when (aspectRatio) {
                AR_4_3, AR_16_9 -> width/4*3
                AR_16_10 -> width/16*10
        })
    }
}
