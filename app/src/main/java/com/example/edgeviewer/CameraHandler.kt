package com.example.edgeviewer

import android.content.Context
import android.graphics.ImageFormat
import android.hardware.camera2.*
import android.media.ImageReader
import android.os.Handler
import android.os.HandlerThread
import android.util.Size
import android.util.Log

class CameraHandler(
    private val context: Context,
    private val callback: FrameCallback
) {

    interface FrameCallback {
        fun onFrame(nv21: ByteArray, width: Int, height: Int)
    }

    private lateinit var imageReader: ImageReader
    private var cameraDevice: CameraDevice? = null
    private var session: CameraCaptureSession? = null

    private val bgThread = HandlerThread("CamThread").apply { start() }
    private val handler = Handler(bgThread.looper)

    fun startCamera() {
        val manager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        val cameraId = manager.cameraIdList[0]
        val size = Size(640, 480)

        imageReader = ImageReader.newInstance(size.width, size.height, ImageFormat.YUV_420_888, 2)
        imageReader.setOnImageAvailableListener({ reader ->
            val img = reader.acquireLatestImage() ?: return@setOnImageAvailableListener
            val nv21 = ImageUtil.yuv420ToNV21(img)
            callback.onFrame(nv21, size.width, size.height)
            img.close()
        }, handler)

        manager.openCamera(cameraId, object : CameraDevice.StateCallback() {
            override fun onOpened(cam: CameraDevice) {
                cameraDevice = cam
                val req = cam.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
                req.addTarget(imageReader.surface)
                cam.createCaptureSession(listOf(imageReader.surface),
                    object : CameraCaptureSession.StateCallback() {
                        override fun onConfigured(sess: CameraCaptureSession) {
                            session = sess
                            sess.setRepeatingRequest(req.build(), null, handler)
                        }
                        override fun onConfigureFailed(sess: CameraCaptureSession) {}
                    }, handler)
            }

            override fun onDisconnected(cam: CameraDevice) { cam.close() }
            override fun onError(cam: CameraDevice, error: Int) { cam.close() }

        }, handler)
    }

    fun stopCamera() {
        session?.close()
        cameraDevice?.close()
        imageReader.close()
        bgThread.quitSafely()
    }
}
