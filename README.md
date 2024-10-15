# mini-pixels

mini-pixels 是 [Pixels](https://github.com/pixelsdb/pixels) 的简化版，旨在为OLAP数据库内核的教学提供实验框架。mini-pixels 保留了 Pixels 存储和查询的核心功能，基本代码来自于[Pixels C++实现](https://github.com/pixelsdb/pixels/tree/master/cpp)。

## 代码结构

mini-pixels 主要包含以下几个核心模块：

- **[pixels-common](https://github.com/pixelsdb/mini-pixels/tree/master/pixels-common)**: 包含项目中通用的工具库和基础组件，提供了整个项目中不同模块间的共享功能和结构定义。
- **[pixels-core](https://github.com/pixelsdb/mini-pixels/tree/master/pixels-core)**: 实现了项目的核心功能，包括数据的存储和查询逻辑。

## 课程与实验

mini-pixels 是中国人民大学 **实用数据库开发** 课程的实验框架，课程于 **2024 年秋季学期** 开设。课程和实验围绕数据库和大数据系统中常用的**列式存储技术**展开，旨在通过实践帮助学生掌握数据库的实际开发与运用。

以下是课程的实验设计时间表。实验将围绕列式存储技术进行，内容涵盖数据存储、查询优化等主题。

| 实验编号 | 实验主题                          | 预计开始时间 | 预计结束时间 | 说明                                               |
| -------- | --------------------------------- | ------------ | ------------ | -------------------------------------------------- |
| 实验1    | 部署mini-pixels环境并进行TPCH测试 | 2024-10-15   | 2024-10-29   | 熟悉如何部署`mini-pixels`开发环境                  |
| 实验2    | 数据存储基础                      | 待定         | 待定         | 介绍列式存储的基本概念，并实现简单的列式存储结构。 |
| 实验3    | 查询优化                          | 待定         | 待定         | 深入研究如何通过优化查询逻辑提高性能。             |

实验的具体细节将根据课程进度进行调整，未确定的实验时间将在课程中通知。

这些实验将帮助学生在实践中深刻理解数据库系统的工作原理，并为未来的数据库开发工作奠定基础。如果在实验过程中遇到问题，可以在 Discussions 中讨论，或者提交 Issue 或 Pull request。

此外，课程中的学生也可以通过提交 **Issue** 和 **Pull Request** 来贡献代码和提出改进建议，他们的贡献将进一步推动 mini-pixels 的持续完善和发展。

## 致谢

[DuckDB](https://github.com/duckdb/duckdb): 高效的嵌入式查询引擎，mini-pixels 中将 Pixels 作为开放文件格式接入DuckDB以执行查询。