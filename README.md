# mini-pixels

mini-pixels 是 [pixels](https://github.com/pixelsdb/pixels) 的简化版，旨在为OLAP数据库内核的教学提供实验框架。mini-pixels 保留了 Pixels 存储和查询的核心功能，基本代码来自于[pixels C++实现](https://github.com/pixelsdb/pixels/tree/master/cpp)。

## 代码结构

mini-pixels 主要包含以下主要目录或sub-module：

- **experiments**: 实验文档。
- **include**: mini-pixels的duckdb extension所需的头文件。
- **pixels-common**: 通用的工具库和基础组件，提供了整个项目中不同模块间的共享功能和结构定义。
- **pixels-core**: 实现了项目的核心功能，包括数据的存储和查询逻辑。
- **pixels-duckdb**: 修改后的duckdb-1.0，也是pixelsdb项目下维护的duckdb fork。
- **pixels-proto**: protobuf的定义文件，目前主要是pixels文件格式的metadata定义。
- **tests**: 功能测试和单元测试。
- **third-party**: 第三方依赖，如protobuf。

## 课程与实验

mini-pixels 目前用于中国人民大学 **实用数据库开发** 课程的实验框架。
课程于 **2024 年秋季学期** 开设。
课程和实验围绕分析型数据库和大数据系统中常用的**列式存储技术**展开，旨在通过实践帮助学生建立列存储引擎和数据库内核开发的基础。

以下是课程的实验设计时间表。

| 实验编号 | 实验主题                     | 预计开始时间 | 预计结束时间 | 说明                                     |
| -------- |--------------------------| ------------ | ------------ |----------------------------------------|
| 实验1    | 部署mini-pixels环境并进行TPCH测试 | 2024-10-15   | 2024-10-29   | 熟悉如何部署`mini-pixels`开发环境                |
| 实验2    | 实现ColumnWriter                 | 2024-12-15       | 2025-1-20         | 熟悉列式存储的设计和实现，完善mini-pixels中的ColumnWriter并且正确读取写入的文件 |

如果在实验过程中遇到问题可以在 Discussions 中讨论或提交 Issue。

同学也可以通过提交 **Issue** 和 **Pull Request** 来贡献代码和提出改进建议，帮助完善 mini-pixels。

## 致谢

[duckdb](https://github.com/duckdb/duckdb): 高效的嵌入式查询引擎，mini-pixels 中将 pixels 作为开放文件格式接入duckdb以执行查询。
