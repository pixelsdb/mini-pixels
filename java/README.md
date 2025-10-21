# Mini-Pixels Java

mini-pixels的Java代码库是 [pixels Java](https://github.com/pixelsdb/pixels) 的简化版。

## 代码结构

Mini-Pixels Java代码主要包含以下主要module：

- **mini-cli**: Mini-Pixels的命令行工具。
- **mini-common**: 通用的工具库和基础组件，提供了整个项目中不同模块间的共享功能和结构定义。
- **mini-core**: 实现了项目的核心功能，包括数据的存储和查询逻辑。
- **mini-storage-http**: Mini-Pixels的http storage实现，用于shuffle等场景中基于http的P2P数据传输。
- **mini-storage-localfs**: Mini-Pixels的localfs storage实现，用于读写本地文件系统中的数据。

## Build Mini-Pixels

JDK 11 (or above) and Maven 3.8 (or above) are required to build Pixels.
Earlier Maven versions may work but are not tested.
After installing these prerequisites, enter any `SRC_BASE` directory, clone the Mini-Pixels codebase and build its Java part as follows:
```bash
git clone https://github.com/pixelsdb/mini-pixels.git
cd mini-pixels/java
# ensure PIXELS_HOME environment variable is set to the installation directory of pixels (not SRC_BASE).
export PIXELS_HOME=[pixels-install-dir]
mvn clean install
```

After that, the library jars of Mini-Pixels has been installed to the local Maven repository.
Please also find the executable jar(s) of Mini-Pixels：
`mini-cli-*-full.jar` in `mini-cli/target`, this is the jar of Mini-Pixels command line tool.

## Develop Mini-Pixels in IntelliJ

You can open `SRC_BASE/pixels` as a maven project in IntelliJ.
When the project is fully indexed and the dependencies are successfully downloaded,
you can build Pixels using the maven plugin (as an alternative of the `mvn` command), run unit tests, and debug the code.

To use the maven plugin, run/debug the unit tests, or run/debug the main classes of Pixels in Intellij, set the `PIXELS_HOME` environment
variable for `Maven`, `Junit`, or `Application` in `Run` -> `Edit Configurations` -> `Edit Configuration Templetes`.
Ensure that the `PIXELS_HOME` directory exists and follow the instructions in [Install Pixels](docs/INSTALL.md#install-pixels) to put
the `pixels.properties` into `PIXELS_HOME/etc` and create the `logs` directory where the log files will be
written into.