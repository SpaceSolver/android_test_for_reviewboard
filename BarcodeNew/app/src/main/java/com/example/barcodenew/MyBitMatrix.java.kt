package com.example.barcodenew

class MyBitMatrix(width: Int, height: Int) {
    private val rowSize: Int
    private val bits: IntArray
    operator fun get(x: Int, y: Int): Boolean {
        val offset = y * rowSize + x / 32
        return bits[offset] ushr (x and 0x1f) and 1 != 0
    }

    fun setRegion(left: Int, top: Int, width: Int, height: Int) {
        val right = left + width
        val bottom = top + height
        for (y in top until bottom) {
            val offset = y * rowSize
            for (x in left until right) {
                bits[offset + x / 32] = bits[offset + x / 32] or (1 shl (x and 0x1f))
            }
        }
    }

    init {
        rowSize = (width + 31) / 32
        bits = IntArray(rowSize * height)
    }
}