#include <jni.h>
#include <string>
#include <cmath>

// Structure for 2D Vector Physics
struct Vector2D {
    double x;
    double y;
};

// Vector Reflection Law: r = d - 2*(d . n)*n
extern "C" JNIEXPORT jdoubleArray JNICALL
Java_com_auraim_engine_service_OverlayService_calculateReflection(
        JNIEnv* env,
        jobject /* this */,
        jdouble dirX, jdouble dirY,
        jdouble normalX, jdouble normalY) {

    double dotProduct = dirX * normalX + dirY * normalY;
    double reflectedX = dirX - 2.0 * dotProduct * normalX;
    double reflectedY = dirY - 2.0 * dotProduct * normalY;

    jdoubleArray result = env->NewDoubleArray(2);
    jdouble fill[2] = {reflectedX, reflectedY};
    env->SetDoubleArrayRegion(result, 0, 2, fill);

    return result;
}
