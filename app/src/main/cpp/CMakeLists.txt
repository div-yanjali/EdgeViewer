cmake_minimum_required(VERSION 3.4.1)

project(edgeviewer)

add_library(native-lib SHARED native-lib.cpp)

# UPDATE THIS PATH to match where you extracted OpenCV !!
set(OpenCV_DIR ${CMAKE_SOURCE_DIR}/../../../../opencv-4.10.0-android-sdk/sdk/native/jni)

include_directories(${OpenCV_DIR}/include)

add_library(libopencv_java4 SHARED IMPORTED)
set_target_properties(libopencv_java4 PROPERTIES IMPORTED_LOCATION
        ${CMAKE_SOURCE_DIR}/../jniLibs/${ANDROID_ABI}/libopencv_java4.so)

target_link_libraries(
        native-lib
        libopencv_java4
        log
)
