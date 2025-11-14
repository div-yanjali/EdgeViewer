# 📌 Real-Time Edge Detection Viewer  
### Android • OpenCV (C++) • OpenGL ES • TypeScript Web Viewer

---

## 🚀 Overview
This project implements a **Real-Time Edge Detection Viewer** integrating:

- **Android (Kotlin/Java)**
- **OpenCV (C++ via JNI / NDK)**
- **OpenGL ES 2.0 Renderer**
- **TypeScript + HTML Web Viewer**

The Android app:
- Captures camera frames in real time  
- Sends frames to C++ (JNI)  
- Processes them using OpenCV (Canny Edge Detection)  
- Renders them through OpenGL ES using a texture pipeline  

A minimal **web viewer** displays a sample processed frame.

This project demonstrates cross-platform RnD skills across **Android, Native C++, OpenGL, NDK, and Web TypeScript** — with proper modular structure and Git versioning.

---

## 🧩 Features Implemented

### ✅ **Android App**
#### 📸 1. Camera Feed (Camera2 API / TextureView)
- Real-time capture using `TextureView` + `ImageReader`
- Background thread for frame handling

#### 🔁 2. OpenCV C++ (via JNI)
- JNI bridge for sending frames to native code
- OpenCV logic implemented in C++:
  - Grayscale conversion  
  - Canny Edge Detection  
- Processed frame returned as RGBA buffer

#### 🎨 3. OpenGL ES Rendering
- Custom renderer (`GLSurface.kt`)
- Uploads processed RGBA buffer to a GL texture
- Smooth rendering pipeline

#### ⭐ Optional Add-ons
- Raw ↔ Edge Mode toggle  
- FPS counter  
- Fragment shader visual effects  

---

## 🌐 Web Viewer (TypeScript)
Located in `/web`:

- Displays a **processed frame** (PNG/Base64)
- Lightweight TypeScript DOM rendering
- Built with `tsc`

---

## ⚙️ Project Structure

```plaintext
EdgeViewer/
│
├── app/
│   └── src/main/
│        ├── java/com/example/edgeviewer/
│        │       ├── MainActivity.kt
│        │       ├── CameraHandler.kt
│        │       ├── GLSurface.kt
│        │       ├── ImageUtil.kt
│        │       └── AndroidManifest.xml
│        │
│        ├── cpp/
│        │       ├── native-lib.cpp
│        │       └── CMakeLists.txt
│        │
│        └── jniLibs/
│                └── armeabi-v7a/libopencv_java4.so
│
├── gradle/
│   └── wrapper/
│         ├── gradle-wrapper.jar
│         └── gradle-wrapper.properties
│
├── web/
│   ├── index.html
│   ├── package.json
│   ├── tsconfig.json
│   └── src/main.ts
│
├── build.gradle
├── settings.gradle
├── local.properties
└── README.md
