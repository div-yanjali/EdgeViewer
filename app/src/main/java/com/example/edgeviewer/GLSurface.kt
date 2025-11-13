package com.example.edgeviewer

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class GLSurface(context: Context) : GLSurfaceView(context) {

    private val renderer: FrameRenderer

    init {
        setEGLContextClientVersion(2)
        renderer = FrameRenderer()
        setRenderer(renderer)
        renderMode = RENDERMODE_WHEN_DIRTY
    }

    fun updateFrame(rgba: ByteArray, width: Int, height: Int) {
        renderer.updateFrame(rgba, width, height)
        requestRender()
    }

    private class FrameRenderer : Renderer {

        private val vertexShaderCode = """
            attribute vec4 aPosition;
            attribute vec2 aTexCoord;
            varying vec2 vTexCoord;
            void main() {
                gl_Position = aPosition;
                vTexCoord = aTexCoord;
            }
        """

        private val fragmentShaderCode = """
            precision mediump float;
            varying vec2 vTexCoord;
            uniform sampler2D uTexture;
            void main() {
                gl_FragColor = texture2D(uTexture, vTexCoord);
            }
        """

        private val quadCoords = floatArrayOf(
            -1f, 1f, 0f,
            -1f, -1f, 0f,
            1f, 1f, 0f,
            1f, -1f, 0f
        )

        private val texCoords = floatArrayOf(
            0f, 0f,
            0f, 1f,
            1f, 0f,
            1f, 1f
        )

        private val vertexBuffer: FloatBuffer =
            ByteBuffer.allocateDirect(quadCoords.size * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer().apply {
                    put(quadCoords)
                    position(0)
                }

        private val texBuffer: FloatBuffer =
            ByteBuffer.allocateDirect(texCoords.size * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer().apply {
                    put(texCoords)
                    position(0)
                }

        private var program = 0
        private var textureId = IntArray(1)

        @Volatile private var frameBuffer: ByteBuffer? = null
        @Volatile private var frameWidth = 0
        @Volatile private var frameHeight = 0

        fun updateFrame(rgba: ByteArray, width: Int, height: Int) {
            synchronized(this) {
                frameWidth = width
                frameHeight = height
                frameBuffer = ByteBuffer.allocateDirect(rgba.size)
                    .order(ByteOrder.nativeOrder())
                    .apply {
                        put(rgba)
                        position(0)
                    }
            }
        }

        override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
            GLES20.glClearColor(0f, 0f, 0f, 1f)

            program = createProgram(vertexShaderCode, fragmentShaderCode)

            GLES20.glGenTextures(1, textureId, 0)
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId[0])

            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR)
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR)
        }

        override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
            GLES20.glViewport(0, 0, width, height)
        }

        override fun onDrawFrame(gl: GL10?) {
            synchronized(this) {
                val buffer = frameBuffer ?: return

                GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)

                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId[0])
                GLES20.glTexImage2D(
                    GLES20.GL_TEXTURE_2D,
                    0,
                    GLES20.GL_RGBA,
                    frameWidth,
                    frameHeight,
                    0,
                    GLES20.GL_RGBA,
                    GLES20.GL_UNSIGNED_BYTE,
                    buffer
                )

                GLES20.glUseProgram(program)

                val posHandle = GLES20.glGetAttribLocation(program, "aPosition")
                val texHandle = GLES20.glGetAttribLocation(program, "aTexCoord")
                val texUniform = GLES20.glGetUniformLocation(program, "uTexture")

                GLES20.glEnableVertexAttribArray(posHandle)
                GLES20.glVertexAttribPointer(
                    posHandle, 3, GLES20.GL_FLOAT,
                    false, 0, vertexBuffer
                )

                GLES20.glEnableVertexAttribArray(texHandle)
                GLES20.glVertexAttribPointer(
                    texHandle, 2, GLES20.GL_FLOAT,
                    false, 0, texBuffer
                )

                GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
                GLES20.glUniform1i(texUniform, 0)

                GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)

                GLES20.glDisableVertexAttribArray(posHandle)
                GLES20.glDisableVertexAttribArray(texHandle)
            }
        }

        private fun loadShader(type: Int, code: String): Int {
            val shader = GLES20.glCreateShader(type)
            GLES20.glShaderSource(shader, code)
            GLES20.glCompileShader(shader)
            return shader
        }

        private fun createProgram(vertexCode: String, fragmentCode: String): Int {
            val vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexCode)
            val fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentCode)

            return GLES20.glCreateProgram().apply {
                GLES20.glAttachShader(this, vertexShader)
                GLES20.glAttachShader(this, fragmentShader)
                GLES20.glLinkProgram(this)
            }
        }
    }
}
