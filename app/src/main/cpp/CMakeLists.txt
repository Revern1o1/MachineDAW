cmake_minimum_required(VERSION 3.22.1)
project(machinedaw_native)

find_package(oboe REQUIRED CONFIG)

add_library(machinedaw SHARED
    jni_bridge.cpp
    AudioEngine.cpp
    MachineRegistry.cpp
    SineTestMachine.cpp
    SwarmMachine.cpp
)

target_include_directories(machinedaw PRIVATE
    ${CMAKE_CURRENT_SOURCE_DIR}
)

target_link_libraries(machinedaw
    oboe::oboe
    log
    android
)

if(CMAKE_BUILD_TYPE STREQUAL "Release")
    target_compile_options(machinedaw PRIVATE -O3 -ffast-math)
endif()
