package com.example.edgeviewer

import android.media.Image

object ImageUtil {
    fun yuv420ToNV21(image: Image): ByteArray {
        val width = image.width
        val height = image.height
        val ySize = width * height
        val uvSize = width * height / 2
        val nv21 = ByteArray(ySize + uvSize)

        val yBuffer = image.planes[0].buffer
        val uBuffer = image.planes[1].buffer
        val vBuffer = image.planes[2].buffer

        yBuffer.get(nv21, 0, ySize)

        val rowStrideU = image.planes[1].rowStride
        val rowStrideV = image.planes[2].rowStride

        var pos = ySize
        val uvWidth = width / 2
        val uvHeight = height / 2

        for (i in 0 until uvHeight) {
            val vRow = ByteArray(rowStrideV)
            val uRow = ByteArray(rowStrideU)
            vBuffer.get(vRow)
            uBuffer.get(uRow)
            for (j in 0 until uvWidth) {
                nv21[pos++] = vRow[j]
                nv21[pos++] = uRow[j]
            }
        }

        return nv21
    }
}
