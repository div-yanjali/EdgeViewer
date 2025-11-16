# 📌 Real-Time Edge Detection Viewer  
### Android • OpenCV (C++ / JNI) • OpenGL ES • TypeScript Web Viewer

---

## 🚀 Overview

This project implements a **Real-Time Edge Detection Viewer**, integrating:

- **Android (Kotlin/Java)**  
- **OpenCV (C++ via JNI / NDK)**  
- **OpenGL ES 2.0**  
- **TypeScript Web Viewer**

The Android app captures camera frames → sends them to C++ through JNI → processes them using **OpenCV Canny Edge Detection** → renders them using OpenGL ES.

A minimal Web Viewer demonstrates how processed frames can be exported and displayed in a browser.

This project shows end-to-end understanding of **Android native pipelines, JNI, OpenCV, OpenGL, and Web TS** with clean commits and modular structure.

---

# 🧩 Features Implemented

## ✅ Android (Native + Rendering)

### 📸 Camera Feed — TextureView + Camera2 API  
- Real-time preview  
- Frames received via ImageReader  
- Background thread for smooth frame delivery  

### 🔁 Native Processing — JNI + OpenCV C++  
- Frame passed from Kotlin → C++  
- OpenCV processing:  
  - Grayscale  
  - Canny edge detection  
- RGBA result returned to Android  

### 🎨 OpenGL ES 2.0 Renderer  
- Converts processed buffer → GL texture  
- Draws on fullscreen quad  
- Smooth 10–15 FPS rendering  

---

## 🌐 Web Viewer (TypeScript)
- Simple TypeScript + HTML viewer 
- Loads a sample processed image.  
- Displays optional stats (FPS, resolution)
- Demonstrates a clean TS setup using tsc

---

# 🖥️ Architecture Diagram

```plaintext
┌──────────────────────┐
│      Camera2 API      │
│  (TextureView Input)  │
└────────────┬─────────┘
             │ Frame (YUV/RGBA)
             ▼
┌──────────────────────┐
│   Kotlin Layer        │
│  (ImageReader / App)  │
└────────────┬─────────┘
             │ JNI Call
             ▼
┌──────────────────────┐
│   Native C++ (JNI)    │
│    OpenCV Pipeline    │
│  - Grayscale          │
│  - Canny Edge         │
└────────────┬─────────┘
             │ RGBA Output Buffer
             ▼
┌──────────────────────┐
│   OpenGL ES Renderer  │
│  (GLSurface → Texture)│
└────────────┬─────────┘
             │ Final Frame
             ▼
      📱 Android Display


              ┌─────────────────────┐
              │   Web Viewer (TS)   │
              │ Loads sample frame  │
              │   Displays Output   │
              └─────────────────────┘


```

# ⚙️ **Setup Instructions**

## 🚧 A. Android Setup

### **1. Required Tools**
Install the following:

- Android Studio  
- Android SDK 31+  
- **NDK 23.1.7779620**  
- **CMake 3.18.1+**  
- OpenCV Android SDK (version 4.x recommended)

---

### **2. Add OpenCV Native Libraries**

- Download the OpenCV Android SDK and copy:

      OpenCV-android-sdk/sdk/native/libs/armeabi-v7a/libopencv_java4.so
  Place it here:
  
      app/src/main/jniLibs/armeabi-v7a/

### **3. Configure CMake**

    app/src/main/cpp/CMakeLists.txt:
    
    cmake_minimum_required(VERSION 3.18.1)
    project("edgeviewer")
    
    add_library(native-lib SHARED native-lib.cpp)
    
    find_library(log-lib log)
    
    add_library(opencv_java4 SHARED IMPORTED)
    set_target_properties(opencv_java4 PROPERTIES
        IMPORTED_LOCATION ${CMAKE_SOURCE_DIR}/../jniLibs/armeabi-v7a/libopencv_java4.so)
    
    target_link_libraries(
            native-lib
            opencv_java4
            ${log-lib})
    
# 🌐 Web Viewer Setup (TypeScript)

Navigate to /web:

    Install packages:
    npm install

Build TypeScript:

    npx tsc

Open the viewer:

    web/index.html

# ⚙️ Project Structure

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

```
# 🏁 Final Notes

This project demonstrates:
- Android Camera2 + TextureView
- Native C++ + OpenCV edge processing
- OpenGL ES rendering pipeline
- Web viewer using TypeScript
- Clean, modular project structure
