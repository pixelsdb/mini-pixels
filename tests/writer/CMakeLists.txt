# Use FetchContent to download and integrate GoogleTest
include(FetchContent)
FetchContent_Declare(
        googletest
        URL https://github.com/google/googletest/archive/b514bdc898e2951020cbdca1304b75f5950d1f59.zip
)

set(gtest_force_shared_crt ON CACHE BOOL "" FORCE)  # Force Google Test to use shared CRT
FetchContent_MakeAvailable(googletest)  # Make Google Test available

# Enable testing for the project
enable_testing()

# Create executable targets for the tests
add_executable(IntegerWriterTest IntegerWriterTest.cpp)
add_executable(PixelsWriterTest PixelsWriterTest.cpp)

# Set compiler options for Debug build
if (CMAKE_BUILD_TYPE MATCHES "Debug")
    target_compile_options(IntegerWriterTest PRIVATE -fsanitize=undefined -fsanitize=address)
    target_compile_options(PixelsWriterTest PRIVATE -fsanitize=undefined -fsanitize=address)

    target_link_options(IntegerWriterTest BEFORE PUBLIC -fsanitize=undefined PUBLIC -fsanitize=address)
    target_link_options(PixelsWriterTest BEFORE PUBLIC -fsanitize=undefined PUBLIC -fsanitize=address)
endif()

# Link Google Test and other necessary libraries to the test executables
target_link_libraries(IntegerWriterTest
        GTest::gtest_main
        pixels-common
        pixels-core
        duckdb
)

target_link_libraries(PixelsWriterTest
        GTest::gtest_main
        pixels-common
        pixels-core
        duckdb
)

set(GTEST_DIR "${PROJECT_SOURCE_DIR}/third-party/googletest")
include_directories(${GTEST_DIR}/googletest/include)
include_directories(${PROJECT_SOURCE_DIR}/pixels-core/include)
include_directories(${PROJECT_SOURCE_DIR}/pixels-common/include)
include_directories(${CMAKE_CURRENT_BINARY_DIR}/../../pixels-common/liburing/src/include)

# Enable GoogleTest in the project
include(GoogleTest)