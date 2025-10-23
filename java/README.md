# Mini-Pixels Java

mini-pixels的Java代码库是 [pixels Java](https://github.com/pixelsdb/pixels) 的简化版。

## 代码结构

Mini-Pixels Java代码主要包含以下主要module：

- **mini-cli**: Mini-Pixels的命令行工具。
- **mini-common**: 通用的工具库和基础组件，提供了整个项目中不同模块间的共享功能和结构定义。
- **mini-core**: 实现了项目的核心功能，包括数据的存储和查询逻辑。
- **mini-storage-http**: Mini-Pixels的http storage实现，用于shuffle等场景中基于http的P2P数据传输。
- **mini-storage-localfs**: Mini-Pixels的localfs storage实现，用于读写本地文件系统中的数据。

## Prerequisites

We develop and run Mini-Pixels (and also [Pixels](https://github.com/pixelsdb/pixels)) on Ubuntu 22.04. MacOS and other recent version of Linux distributions should also work.
For MS Windows, you can try if you want, and let us know if you get it work:)
This document only provides the instructions on Ubuntu.

Besides the OS, JDK 11 (or above) and Maven 3.8 (or above) are required to build Pixels. Earlier Maven versions may work but are not tested.

### Install JDK

In ubuntu 22.04, JDK 21 or below can be installed using apt:
```bash
sudo apt install openjdk-21-jdk openjdk-21-jre
```
Replace 21 with other valid JDK version if needed.

Check the java version:
```bash
java -version
```
If other version of JDK is in use, switch to the required JDK:
```bash
update-java-alternatives --list
sudo update-java-alternatives --set /path/to/the/required/jdk
```

### Install Maven
On some operating systems, the Maven installed by apt or yum might be incompatible with recent JDKs such as 17+. 
In this case, manually install a later Maven compatible with your JDK following [Maven Installation](https://maven.apache.org/install.html).

Check if Maven is using the required JDK:
```bash
mvn --version
```
The printed Java version should be consistent to the Java version printed by java --version.
Otherwise, check if the JAVA_HOME environment variable is pointing to `/path/to/the/required/jdk`.

## Build Mini-Pixels

After installing the prerequisites, enter any `SRC_BASE` directory, clone the Mini-Pixels codebase and build its Java part as follows:
```bash
git clone https://github.com/pixelsdb/mini-pixels.git
cd mini-pixels/java
# ensure PIXELS_HOME environment variable is set to the installation directory of Mini-Pixels (not SRC_BASE).
export PIXELS_HOME=[pixels-install-dir]
mvn clean install
```

After that, the library jars of Mini-Pixels has been installed to the local Maven repository.
Please also find the executable jar(s) of Mini-Pixels：
`mini-cli-*-full.jar` in `mini-cli/target`, this is the jar of Mini-Pixels command line tool.

## Develop Mini-Pixels in IntelliJ

You can open `SRC_BASE/mini-pixels/java` as a maven project in IntelliJ.
When the project is fully indexed and the dependencies are successfully downloaded,
you can build Pixels using the maven plugin (as an alternative of the `mvn` command), run unit tests, and debug the code.

To use the maven plugin, run/debug the unit tests, or run/debug the main classes of Pixels in Intellij, set the `PIXELS_HOME` environment
variable for `Maven`, `Junit`, or `Application` in `Run` -> `Edit Configurations` -> `Edit Configuration Templetes`.
Ensure that the `PIXELS_HOME` directory exists, copy `mini-common/src/main/resources/pixels.properties` into `PIXELS_HOME/etc`,
and create the directory `PIXELS_HOME/logs` where the log files will be written to.

> Note: `SRC_BASE` and `PIXELS_HOME` should be separate directories. They contain the source code and the installation of Mini-Pixels, respectively.

Due to Java's strong module encapsulation introduced in JDK 9 and further tightened in later versions (e.g., **JDK 21**), 
the JVM restricts deep reflective access to internal classes, particularly those related to NIO and direct memory management (in packages like `sun.nio.ch`). 
This restriction can cause runtime errors or prevent critical native components, such as `DirectIoLib`, from functioning correctly during I/O operations. 
To resolve this and ensure compatibility with modern JDKs, the following JVM arguments must be added to your runtime or test execution configuration:

```
--add-opens=java.base/sun.nio.ch=ALL-UNNAMED
--add-opens=java.base/java.nio=ALL-UNNAMED
```

## Lab 1

The first lab is to optimize `mini-storage-http`. This module can be used to transfer data from one process to the other, and it will be used by the
shuffle framework in PixelsDB (the cloud lakehouse engine based on Pixels).

The efficiency of `mini-storage-http` significantly impacts the performance of data transfer and data shuffle.
The current implementation of `mini-storage-http` needs to be optimized in performance, resource efficiency, and comment+code qualify.
For example, it attempts to use Netty (a popular async-network library used in many big-data systems such as Spark) to implement asynchronous data transfer.
However, it actually transfers data serially.

Please help improve the performance of this component (e.g., by enabling real async transfer, reducing memory copy, etc.) and reduce the CPU and memory consumptions.

You can test the performance of this component by running the unit test in `mini-storage-http/src/test/java/io/pixelsdb/pixels/storage/http/TestHttpStream`.
Ensure you have 8GB free memory on your laptop to run this test.

We will add a command in `mini-cli` for the performance evaluation shortly after.