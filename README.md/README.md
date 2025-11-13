
📌 README.md — Real-Time Edge Detection Viewer (Android + OpenCV + OpenGL + Web)
🚀 Overview

This project is a Real-Time Edge Detection Viewer that integrates:

Android (Kotlin/Java)

OpenCV (C++ via JNI / NDK)

OpenGL ES 2.0 Renderer

TypeScript + HTML Web Viewer

The application captures live camera frames, sends them to native C++ for OpenCV edge-detection, and renders the processed output using OpenGL ES in real-time (10–15 FPS target).
A small web viewer is also included to display a sample processed frame.

This project demonstrates your ability to work across Android, Native C++/NDK, OpenGL, and Web-TS, while maintaining a clean repository and commit history.

🧩 Features Implemented
✅ Android Application
📸 1. Camera Feed (Camera2 API / TextureView)

Real-time camera preview using TextureView.

Frame extraction using ImageReader.

Proper background handler threads.

🔁 2. JNI + OpenCV C++ Processing

Frame transferred from Kotlin → C++ through JNI.

OpenCV used to perform:

Grayscale conversion

Canny Edge Detection

Processed frame returned as RGBA byte buffer.

🎨 3. OpenGL ES 2.0 Renderer

Custom OpenGL renderer (GLSurface.kt).

Renders the processed RGBA buffer as a GL texture.

Smooth rendering pipeline.

💡 Optional Add-ons (If implemented)

Toggle button: raw feed ↔ edge-detected view.

FPS counter.

Shader-based filters (invert, grayscale, etc.).

🌐 Web Viewer (TypeScript)

A minimal TypeScript + HTML viewer that:

Loads a sample processed frame (PNG/Base64).

Displays FPS text / overlay.

Demonstrates TypeScript DOM usage & project structure.

Buildable with tsc.

⚙️ Project Structure
EdgeViewer/
│
├── app/
│   ├── src/main/java/com/example/edgeviewer/
│   │     ├── MainActivity.kt
│   │     ├── CameraHandler.kt
│   │     ├── ImageUtil.kt
│   │     ├── GLSurface.kt
│   │     └── AndroidManifest.xml
│   │
│   ├── src/main/cpp/
│   │     ├── native-lib.cpp
│   │     └── CMakeLists.txt
│   │
│   ├── src/main/jniLibs/
│         └── armeabi-v7a/libopencv_java4.so
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
├── settings.gradle
├── build.gradle
└── README.md

🛠 Setup Instructions
📌 1. Install Required Tools
Android Side

Android Studio

Android SDK 31+

NDK (23.1.7779620 or compatible)

CMake 3.18.1+

OpenCV Android SDK (4.x)

Web Side

Node.js

TypeScript (npm install -g typescript)

📌 2. Add OpenCV Native Libraries

Copy from OpenCV Android SDK:

OpenCV-android-sdk/sdk/native/libs/armeabi-v7a/libopencv_java4.so


Paste into:

app/src/main/jniLibs/armeabi-v7a/

📌 3. Configure CMake (native build)

app/src/main/cpp/CMakeLists.txt:

cmake_minimum_required(VERSION 3.18.1)

project("edgeviewer")

add_library(native-lib SHARED native-lib.cpp)

find_library(log-lib log)

add_library(opencv_java4 SHARED IMPORTED)
set_target_properties(opencv_java4 PROPERTIES IMPORTED_LOCATION
        ${CMAKE_SOURCE_DIR}/../jniLibs/armeabi-v7a/libopencv_java4.so)

target_link_libraries(
        native-lib
        opencv_java4
        ${log-lib})

📌 4. Build the Web Viewer

Inside /web:

npm install
tsc


Then open:

web/index.html


to view the page.

🧠 How It Works (Architecture Explanation)
🎥 1. Frame Capture (Android)

CameraHandler.kt initializes Camera2 API.

Frames from the camera sensor arrive via ImageReader.

Frame converted to suitable format → passed to JNI.

🔗 2. JNI Bridge (Kotlin ↔ C++)

Kotlin calls processFrame(byte[] inputFrame, int width, int height) in native code.

JNI converts data into OpenCV cv::Mat.

🧮 3. OpenCV Processing (C++)

Frame converted to grayscale:

cvtColor(src, gray, CV_RGBA2GRAY);


Edge detection:

Canny(gray, edges, 80, 150);


Converted back to RGBA and returned to Android.

🎨 4. OpenGL Renderer

Android receives processed buffer as ByteBuffer.

OpenGL ES uploads it to GL texture.

Draws a fullscreen quad displaying the processed frame.

🌐 5. Web Viewer

Displays static processed frame (PNG / Base64).

Uses TypeScript DOM updates for simple stats.

📷 Screenshots

(Add after running the app)

Android App Raw Feed

Android Edge-Detected Output

Web Viewer Screenshot

📦 How to Run the Android App

Connect your Android device (USB debugging enabled)

In Android Studio → Run

App opens showing real-time edge detection output

📦 How to Run the Web Viewer
cd web
tsc


Open:

web/index.html

📘 Notes

Repository contains clean commit history with step-wise development.

Native OpenCV build used for best performance.

OpenGL ES ensures fast display of processed frames.

🏁 Conclusion

A complete, multi-platform RnD-style implementation integrating:

Android Camera

OpenCV Native (C++)

OpenGL ES Rendering

TypeScript Web UI

This project highlights the ability to work across Android, Native C++, OpenGL, and Web technologies in one pipeline.
