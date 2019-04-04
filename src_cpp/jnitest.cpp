#include "iogif.hpp"
#include <android/bitmap.h>
#include <opencv2/core.hpp>
#include <opencv2/imgproc.hpp>


extern "C" JNIEXPORT jstring JNICALL Java_com_vicman_jnitest_fragments_InfoFragment_stringFromJNI(JNIEnv* env,  jobject /* this */)
{
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}


void BitmapToMat(JNIEnv* env, jobject, jobject inBitmap ,cv::Mat &dst)
{
    AndroidBitmapInfo info;
    void* inputPixels = nullptr;

    try {
        CV_Assert(AndroidBitmap_getInfo(env, inBitmap, &info) >= 0);
        CV_Assert(info.format == ANDROID_BITMAP_FORMAT_RGBA_8888);
        CV_Assert(AndroidBitmap_lockPixels(env, inBitmap, &inputPixels) >= 0);
        CV_Assert(inputPixels);

        cv::Mat bitmap(info.height, info.width, CV_8UC4, inputPixels);
        bitmap.copyTo(dst);

        AndroidBitmap_unlockPixels(env, inBitmap);
    }
    catch(const cv::Exception& e) {
        AndroidBitmap_unlockPixels(env, inBitmap);
        jclass je = env->FindClass("org/opencv/core/CvException");
        if(!je) je = env->FindClass("java/lang/Exception");
        env->ThrowNew(je, e.what());
        return;
    }
    catch (...) {
        AndroidBitmap_unlockPixels(env, inBitmap);
        jclass je = env->FindClass("java/lang/Exception");
        env->ThrowNew(je, "Unknown exception in JNI code {nBitmapToMat}");
        return;
    }
}


extern "C" JNIEXPORT void JNICALL Java_com_vicman_jnitest_fragments_ImageFragment_addTextToImage(JNIEnv* env, jobject This/* this */, jobject inBitmap, jlong outMatAddr)
{
    cv::Mat *dst = (cv::Mat*) outMatAddr;

    cv::Mat img;
    BitmapToMat(env, This, inBitmap, img);

    cv::copyMakeBorder(img, img, 0, 188, 0, 0, cv::BORDER_CONSTANT, cv::Scalar::all(0));
    cv::putText(img, "Hello from C++", cv::Point(50, 600), cv::HersheyFonts::FONT_HERSHEY_COMPLEX, 1.5, cv::Scalar(0,0,0,255), 2, cv::LINE_AA);

    img.copyTo(*dst);
}

extern "C" JNIEXPORT jbyteArray JNICALL Java_com_vicman_jnitest_fragments_GifFragment_addTextToGif(JNIEnv* env, jobject This/* this */, jbyteArray sourceGif)
{
    jsize gif_data_size = env->GetArrayLength(sourceGif);
    uchar* gif_data_ptr = (uchar*)env->GetByteArrayElements(sourceGif, NULL);
    std::vector<uchar> raw_gif(gif_data_ptr, gif_data_ptr + gif_data_size);

    std::vector<cv::Mat> imgs = gif_read(raw_gif);

    for(size_t i = 0; i < imgs.size(); i++) {
        cv::copyMakeBorder(imgs[i], imgs[i], 0, 188, 0, 0, cv::BORDER_CONSTANT, cv::Scalar::all(255));
        cv::putText(imgs[i], "Hello from C++", cv::Point(10 * i, 600), cv::HersheyFonts::FONT_HERSHEY_COMPLEX, 1.5, cv::Scalar(0,0,0), 2, cv::LINE_AA);
    }

    std::vector<uchar> dst_raw_gif;
    gif_write(dst_raw_gif, imgs);

    jbyteArray ret = (jbyteArray) env->NewByteArray(dst_raw_gif.size());
    env->SetByteArrayRegion (ret, 0, dst_raw_gif.size(), (const jbyte*)dst_raw_gif.data());

    return ret;
}
