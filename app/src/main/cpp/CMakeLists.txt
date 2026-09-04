cmake_minimum_required(VERSION 3.22.1)
project(machinedaw)

add_library(machinedaw SHARED
    jni_bridge.cpp
    AudioEngine.cpp
    MachineRegistry.cpp
    SineTestMachine.cpp
    SwarmMachine.cpp
)

find_package(oboe REQUIRED CONFIG)

target_link_libraries(machinedaw
    oboe::oboe
    android
    log
)

target_include_directories(machinedaw PRIVATE ${CMAKE_CURRENT_SOURCE_DIR})
