LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE_TAGS := optional
LOCAL_CERTIFICATE := platform

LOCAL_PACKAGE_NAME := YYDRobotPhotos
LOCAL_SRC_FILES := $(call all-java-files-under, src)
LOCAL_RESOURCE_DIR := $(LOCAL_PATH)/res
LOCAL_ASSET_DIR := $(LOCAL_PATH)/assets

LOCAL_JNI_SHARED_LIBRARIES +=libmsc




LOCAL_STATIC_JAVA_LIBRARIES := \
	android-support-v4 \
	photos_lib2 \
	photos_lib3 \
	photos_lib5 \
	photos_lib6 \
	photos_lib8

LOCAL_PROGUARD_ENABLED := disabled

include $(BUILD_PACKAGE)	


include $(CLEAR_VARS)

LOCAL_PREBUILT_STATIC_JAVA_LIBRARIES := \
	photos_lib2:libs/gson-2.3.1.jar \
	photos_lib3:libs/Msc_z.jar \
	photos_lib5:libs/universal-image-loader-1.9.5.jar \
	photos_lib6:libs/xUtils-2.6.14.jar \
	photos_lib8:libs/StickyGridHeaders.jar


include $(BUILD_MULTI_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE := libmsc
LOCAL_SRC_FILES_64 := libs/arm64-v8a/libmsc.so
LOCAL_MULTILIB := 64
LOCAL_MODULE_CLASS := SHARED_LIBRARIES
LOCAL_MODULE_SUFFIX := .so
include $(BUILD_PREBUILT)
