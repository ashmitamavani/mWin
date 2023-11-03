#include <jni.h>
#include <string>

extern "C" JNIEXPORT jstring JNICALL
Java_com_mwin_reward_value_Constants_getUrlValue(
        JNIEnv *env,
        jobject /* this */) {
    std::string URL = "https://mwin.co.in/Api/api100/";
    return env->NewStringUTF(URL.c_str());
}
extern "C" JNIEXPORT jstring JNICALL
Java_com_mwin_reward_value_Constants_getMivValue(
        JNIEnv *env,
        jobject /* this */) {
    std::string MIV = "uk1yk46umuhm65yu";
    return env->NewStringUTF(MIV.c_str());
}
extern "C" JNIEXPORT jstring JNICALL
Java_com_mwin_reward_value_Constants_getMkeyValue(
        JNIEnv *env,
        jobject /* this */) {
    std::string MKEY = "fg654jn98t18rtgn";
    return env->NewStringUTF(MKEY.c_str());
}
extern "C"
JNIEXPORT jstring JNICALL
Java_com_mwin_reward_value_Constants_getToken(JNIEnv *env,
                                              jclass clazz) {
    // TODO: implement getToken()
    std::string token = "4b36d6a5-e3e9-658t-8584-d8af62d21c92";
    return env->NewStringUTF(token.c_str());
}