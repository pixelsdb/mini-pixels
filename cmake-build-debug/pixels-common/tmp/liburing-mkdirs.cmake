# Distributed under the OSI-approved BSD 3-Clause License.  See accompanying
# file Copyright.txt or https://cmake.org/licensing for details.

cmake_minimum_required(VERSION 3.5)

file(MAKE_DIRECTORY
  "/home/whz/mini-pixels/cmake-build-debug/pixels-common/liburing"
  "/home/whz/mini-pixels/cmake-build-debug/pixels-common/src/liburing-build"
  "/home/whz/mini-pixels/cmake-build-debug/pixels-common"
  "/home/whz/mini-pixels/cmake-build-debug/pixels-common/tmp"
  "/home/whz/mini-pixels/cmake-build-debug/pixels-common/src/liburing-stamp"
  "/home/whz/mini-pixels/cmake-build-debug/pixels-common/src"
  "/home/whz/mini-pixels/cmake-build-debug/pixels-common/src/liburing-stamp"
)

set(configSubDirs )
foreach(subDir IN LISTS configSubDirs)
    file(MAKE_DIRECTORY "/home/whz/mini-pixels/cmake-build-debug/pixels-common/src/liburing-stamp/${subDir}")
endforeach()
if(cfgdir)
  file(MAKE_DIRECTORY "/home/whz/mini-pixels/cmake-build-debug/pixels-common/src/liburing-stamp${cfgdir}") # cfgdir has leading slash
endif()
