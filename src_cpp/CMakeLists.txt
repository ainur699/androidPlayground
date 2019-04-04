cmake_minimum_required(VERSION 3.4.1)
project(jnitest LANGUAGES C CXX)

set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -DANDROID_NDK -D__STDC_CONSTANT_MACROS")
set(CMAKE_CXX_STANDARD 14)

include_directories(${PROJECT_SOURCE_DIR}/include ${PROJECT_SOURCE_DIR}/LibBase/include)
add_library( lib_opencv SHARED IMPORTED )
set_target_properties(lib_opencv PROPERTIES IMPORTED_LOCATION ${PROJECT_SOURCE_DIR}/libs/${ANDROID_ABI}/libopencv_java3.so)

set(
        GIFLIB_SRC
        giflib/dgif_lib.c
        giflib/egif_lib.c
        giflib/gif_err.c
        giflib/gif_font.c
        giflib/gif_hash.c
        giflib/gifalloc.c
        giflib/openbsd-reallocarray.c
        giflib/quantize.c
)

add_library(
        ${PROJECT_NAME}
        SHARED
        jnitest.cpp
        iogif.cpp
        ${GIFLIB_SRC}
)

add_library( libstd SHARED IMPORTED )
set_target_properties( libstd PROPERTIES IMPORTED_LOCATION ${CMAKE_ANDROID_NDK}/sources/cxx-stl/llvm-libc++/libs/${ANDROID_ABI}/libc++_shared.so)

target_link_libraries(${PROJECT_NAME}
        -ljnigraphics
        lib_opencv
        libstd
        )
