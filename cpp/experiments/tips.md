# 实验相关的tips
Author: [XBsleepy](https://github.com/XBsleepy)

## 依赖
pixels-cli 依赖 boost 库

wsl 或某些特定版本的系统对 liburing 的支持不太好，如果编译完成之后用 duckdb 进行 select 时出现 `DirectRandomAccessFile:initialize io uring fails.` 可以换一个发行版或者 git clone 一个 liburing，重装一下。

## 编译
git clone 之后，需要执行 `make pull` 拉取所有的子模块，否则可能会出现 `CMake Error`。

随后需要执行 `make -j$(nproc)` 限制并行数（nproc 指当前进程可用的 CPU 数量），未指定时可能会无限使用 CPU 资源进而导致系统崩溃。

`make` 默认编译得到的是 release 版本，建议使用 `make -j$(nproc) debug` 生成 debug 版本的可执行文件，方便找到问题。

## 运行

`make release` (或 `make debug`) 会在 `build/release` (或 `build/debug`) 目录下生成文件。

可执行文件 `duckdb` 默认链接了 pixel 扩展，可以读取 *.pxl 的数据。

生成 *.pxl 文件的方法是运行可执行文件 `./pixels-cli`，按照pdf给出的语法执行load语句。

> 在 linux 下可使用 `find . -name pixels-cli` 在当前目录下递归查找可执行文件 `pixels-cli`
> 
> 默认位置是 `build/realease/extension/pixel/pixels-cli` 或 `build/debug/extension/pixel/pixels-cli`

## 任务

实验要求实现通过 `pixels-cli` 读取列数据类型为 date, timestamp, decimal 的 *.tbl 文件并生成 *.pxl 文件，可通过 `duckdb` 读取 *.pxl 文件验证其正确性。

具体我们需要做的就是，找到对应的比如 datecolumnvector, datecolumnwriter 等 .cpp 或 .h 等未完成的文件，参照已经给出的 integer 类型对应的函数和实现，以及 pixel 主仓库中 java 版本中的实现补完代码。

在补完代码之后，运行编译得到的 `pixels-cli`，执行 load 语句去生成 *.pxl 文件。

随后可以在 duckdb 中执行 select 语句，如果能正确显示数据，就完成了任务。


## 总结
1. 为了方便 debug，最好编译 debug 版本，方便 gdb 调试（也可借助 CLion 等工具）
2. 底层的存储，date 是 int 类型，timestamp 和 decimal 是 long 类型
3. pixels-cli 并不会写多个文件，当行数超过 -n (load 时指定的最大行数) 时并不会默认开一个新的，而是会生成一个无法读取的文件，并且cli不会报错
4. writer 默认都是先调用 add(string) 方法，对于 timestamp 和 date，cpp 没有 java 那样自带的 date 类型，所以需要自己完成 string 到 date 类型的解析。（decimal也是，decimal 支持 18 位的精度，如果直接转成 float 或者 double 再变成int，是很有可能有精度损失的）
5. 在修改代码后，通常可以直接到 mini-pixels 目录下执行 `make -j$(nproc) debug`。并且能正确更新可执行文件。不到万不得已最好不要直接`make clean`，因为会把所有的东西都删掉，重新编译会很慢
6. 目前版本的 decimal 数据类型在 precision 低于 10 的时候，reader 的显示会有问题，可以设置成比10大的
7. 如果想要自己生成测试文件请不要在文末添加空行，会导致`segmentation fault`，或者是`runtime error`
8. 任务三实际是在说使用 duckdb 读取 *.pxl 文件来验证通过 pixles-cli 生成的 *.pxl 是否正确，可以视作测试环节
9. 建议虚函数全部加上 override，这样编译器会帮你检查是否 override 正确
10. 目前的 timestamp 的 precision 没用，duckdb读取的时候默认是 秒 * 1e6 对应的 long
