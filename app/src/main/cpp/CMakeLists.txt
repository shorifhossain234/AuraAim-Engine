cmake_minimum_required(VERSION 3.22.1)

project("auraim_native")

# Add native C++ physics & computer vision library
add_library(
        auraim_native
        SHARED
        auraim_native.cpp
)

find_library(
        log-lib
        log
)

target_link_libraries(
        auraim_native
        ${log-lib}
)
