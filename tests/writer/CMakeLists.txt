# Use FetchContent to download and integrate GoogleTest
include(FetchContent)
FetchContent_Declare(
        googletest
        URL https://github.com/google/googletest/archive/03597a01ee50ed33e9dfd640b249b4be3799d395.zip
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
    target_compile_options(IntegerWriterTest PRIVATE -Werror -fsanitize=undefined -fsanitize=address)
    target_compile_options(PixelsWriterTest PRIVATE -Werror -fsanitize=undefined -fsanitize=address)

    target_link_options(IntegerWriterTest BEFORE PUBLIC -fsanitize=undefined PUBLIC -fsanitize=address)
    target_link_options(PixelsWriterTest BEFORE PUBLIC -fsanitize=undefined PUBLIC -fsanitize=address)
endif()

# Link Google Test and other necessary libraries to the test executables
target_link_libraries(
        IntegerWriterTest
        gtest_main
        pixels-common
        pixels-core
        duckdb
        GTest::gtest_main
)

target_link_libraries(
        PixelsWriterTest
        gtest_main
        pixels-common
        pixels-core
        duckdb
        GTest::gtest_main
)

set(GTEST_DIR "${PROJECT_SOURCE_DIR}/third-party/googletest")
include_directories(${GTEST_DIR}/googletest/include)
include_directories(${PROJECT_SOURCE_DIR}/pixels-core/include)
include_directories(${PROJECT_SOURCE_DIR}/pixels-common/include)
include_directories(${CMAKE_CURRENT_BINARY_DIR}/../../pixels-common/liburing/src/include)

# Enable GoogleTest in the project
include(GoogleTest)