package com.example.edgeviewer

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.File
import java.io.FileOutputStream

class MainActivity : AppCompatActivity() {

    companion object {
        init { System.loadLibrary("native-lib") }
    }

    external fun nativeProcessFrame(nv21: ByteArray, width: Int, height: Int): ByteArray

    private lateinit var cameraHandler: CameraHandler
    private lateinit var glSurface: GLSurface

    private var savedOnce = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 1)
        }

        glSurface = GLSurface(this)
        val layout = FrameLayout(this)
        layout.addView(glSurface)
        setContentView(layout)

        cameraHandler = CameraHandler(this, object : CameraHandler.FrameCallback {
            override fun onFrame(nv21: ByteArray, width: Int, height: Int) {
                Thread {
                    val rgba = nativeProcessFrame(nv21, width, height)
                    glSurface.updateFrame(rgba, width, height)

                    if (!savedOnce) {
                        saveSample(rgba, width, height)
                        savedOnce = true
                    }

                }.start()
            }
        })

        cameraHandler.startCamera()
    }

    private fun saveSample(rgba: ByteArray, w: Int, h: Int) {
        val bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)

        val ints = IntArray(w * h)
        var p = 0
        for (i in 0 until w * h) {
            val r = rgba[p++].toInt() and 0xFF
            val g = rgba[p++].toInt() and 0xFF
            val b = rgba[p++].toInt() and 0xFF
            val a = rgba[p++].toInt() and 0xFF
            ints[i] = (a shl 24) or (r shl 16) or (g shl 8) or b
        }
        bmp.setPixels(ints, 0, w, 0, 0, w, h)

        val file = File(getExternalFilesDir(null), "processed_sample.png")
        val fos = FileOutputStream(file)
        bmp.compress(Bitmap.CompressFormat.PNG, 100, fos)
        fos.close()

        Log.i("MainActivity", "Saved sample frame to: ${file.absolutePath}")
    }

    override fun onDestroy() {
        cameraHandler.stopCamera()
        super.onDestroy()
    }
}
