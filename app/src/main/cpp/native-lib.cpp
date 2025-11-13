#include <jni.h>
#include <opencv2/imgproc.hpp>
#include <vector>

using namespace cv;

extern "C" {

JNIEXPORT jbyteArray JNICALL
Java_com_example_edgeviewer_MainActivity_nativeProcessFrame(
        JNIEnv *env,
        jobject,
        jbyteArray nv21Arr,
        jint width,
        jint height) {

    jbyte* nv21 = env->GetByteArrayElements(nv21Arr, NULL);

    // YUV NV21 → BGR
    Mat yuv(height + height/2, width, CV_8UC1, (uchar*)nv21);
    Mat bgr;
    cvtColor(yuv, bgr, COLOR_YUV2BGR_NV21);

    // BGR → Gray → Canny
    Mat gray, edges;
    cvtColor(bgr, gray, COLOR_BGR2GRAY);
    Canny(gray, edges, 80, 150);

    // Convert edges → RGBA (OpenGL)
    Mat rgba;
    cvtColor(edges, rgba, COLOR_GRAY2RGBA);

    // Convert to Java byte array
    int size = rgba.total() * rgba.elemSize();
    jbyteArray out = env->NewByteArray(size);
    env->SetByteArrayRegion(out, 0, size, (jbyte*)rgba.data);

    env->ReleaseByteArrayElements(nv21Arr, nv21, JNI_ABORT);

    return out;
}

}
