#include <stdio.h>
#include <unistd.h>
#include <fcntl.h>
#include <jni.h>

int fd = 0;

JNIEXPORT jint JNICALL
Java_com_example_project_game_1main_openDriver(JNIEnv *env, jclass clazz, jstring path) {
    // TODO: implement openDriver()
    jboolean iscopy;
    const char *path_utf = (*env)->GetStringUTFChars(env, path, &iscopy);
    fd = open(path_utf, O_WRONLY);
    (*env)->ReleaseStringUTFChars(env, path, path_utf);

    if(fd<0) return -1;
    else return 1;
}

JNIEXPORT void JNICALL
Java_com_example_project_game_1main_closeDriver(JNIEnv *env, jclass clazz) {
// TODO: implement closeDriver()
if(fd>0) close(fd);
}

JNIEXPORT void JNICALL
Java_com_example_project_game_1main_writeDriver(JNIEnv *env, jclass clazz, jbyteArray data, jint length) {
// TODO: implement writeDriver()
jbyte* chars = (*env)->GetByteArrayElements(env, data, 0);
if(fd>0) write(fd, (unsigned char*)chars, length);
(*env)->ReleaseByteArrayElements(env, data, chars, 0);
}

int fd_seg = 0;

JNIEXPORT jint JNICALL
Java_com_example_project_game_1main_openDriverseg
        (JNIEnv *env, jclass clazz, jstring path) {
    jboolean iscopy;
    const char *path_utf = (*env)->GetStringUTFChars(env, path, &iscopy);
    fd_seg = open(path_utf, O_WRONLY);
    (*env)->ReleaseStringUTFChars(env, path, path_utf);

    if(fd_seg < 0) return -1;
    else return 1;
}

JNIEXPORT void JNICALL
Java_com_example_project_game_1main_closeDriverseg
        (JNIEnv *env, jclass clazz)
{
    if(fd_seg>0) close(fd_seg);
}

JNIEXPORT void JNICALL
Java_com_example_project_game_1main_writeDriverseg
        (JNIEnv* env, jclass clazz, jbyteArray arr, jint count)
{
    jbyte* chars = (*env)->GetByteArrayElements(env, arr, 0);
    if(fd_seg>0) write(fd_seg, (unsigned char*)chars, count);
    (*env)->ReleaseByteArrayElements(env, arr, chars, 0);
}

// MainActivity, GPIO button
int fd_gpio;
JNIEXPORT jint JNICALL Java_com_example_project_JNIDriver_openDriver
        (JNIEnv *env, jclass clazz, jstring path) {
    jboolean iscopy;
    const char *path_utf = (*env)->GetStringUTFChars(env, path, &iscopy);
    fd_gpio = open(path_utf, O_RDONLY);
    (*env)->ReleaseStringUTFChars(env, path, path_utf);

    if(fd_gpio < 0)
        return -1;
    else
        return 1;
}

JNIEXPORT void JNICALL Java_com_example_project_JNIDriver_closeDriver
        (JNIEnv * env, jclass clazz) {
    if(fd_gpio > 0) close(fd_gpio);
}

JNIEXPORT jchar JNICALL Java_com_example_project_JNIDriver_readDriver
        (JNIEnv *env, jobject thiz) {
    char ch = 0;
    if(fd_gpio > 0){
        read(fd_gpio, &ch, 1);
    }
    return ch;
}

JNIEXPORT jint JNICALL Java_com_example_project_JNIDriver_getInterrupt
        (JNIEnv *env, jobject thiz) {
    int ret = 0;
    char value[100];
    char* ch1 = "Up";
    char* ch2 = "Down";
    char* ch3 = "Left";
    char* ch4 = "Right";
    char* ch5 = "Center";
    ret = read(fd_gpio, &value, 100);

    if(ret < 0)
        return -1;
    else{
        if(strcmp(ch1, value) == 0)
            return 1;
        else if(strcmp(ch2, value) == 0)
            return 2;
        else if(strcmp(ch3, value) == 0)
            return 3;
        else if(strcmp(ch4, value) == 0)
            return 4;
        else if(strcmp(ch5, value) == 0)
            return 5;
    }
    return 0;
}