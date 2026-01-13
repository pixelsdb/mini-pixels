# 实验2报告模板

- 姓名

- 学号



## 一、vectorcolumn类型解读

简单描述一下对代码逻辑的理解 需要关注vectorcolumn类型的存储格式`VectorColumnVector`，读取`VectorColumnReader`和写入`VectorColumnWriter`



## 二、vectorcolumn测试

需要测试`TestVectorColumnWriter`类并进行扩展测试

### 2.1 测试数据集

下载测试数据集`nytimes-16-angular.hdf5`

```
wget http://ann-benchmarks.com/nytimes-16-angular.hdf5
```

下载好数据集后，需要配置数据的路径和生成pxl文件的路径

```java
        String hdf5FilePath = "/home/whz/test/mini-pixels/java/nytimes-16-angular.hdf5";
        String pixelsOutputPath = "/home/whz/test/mini-pixels/java/vectorOut/nytimes-vectors.pxl";

```



### 2.2 测试结果

提供了一个已经完成的简单测试类:`TestVectorColumnWriter`类包含三个测试

- `testSimpleWrite`  简单测试，不调用`PixelsWriteImpl`
- `testReadWriteNYTimesDataset`写入nytimes-16的train数据并且进行读取
- `testReadWriteNYTimesTestSet`写入nytimes-16的test数据并且进行读取

需要提供成功测试的截图

需要自行尝试修改测试类，包括:

- 修改`PixelsWriterImpl`的写入配置参数，尝试不同的写入配置下是否可以成功写入和读取，比较不同参数下，写入速度和生成的文件大小差异

- 添加更多的测试数据集，可以从[该网站](http://ann-benchmarks.com/)下载
- 更改测试类

需要提供自己的测试思路以及测试结果的截图



### 2.3 vectorcolumn bug修复

在测试过程中遇到的各种bug推荐按照以下的格式记录

```
名称: 起一个简明扼要的名称 比如pixels-readerImpl: readLong buffer out of range.
描述: bug发生的位置，bug发生的表现，bug发生的原因，本机的测试环境，最重要的是确保这个bug可以复现。
解决方法: 简单描述自己的解决方案，修复后进行了哪些测试
```



## 三、(Bouns) 使用LOAD导入数据

上述的测试方法构建了`PixelsWriterImpl`类写入数据，手动指定构建的schema

```java
            String schemaString="struct<id:bigint,vector:vector(16)>";
            TypeDescription schema = TypeDescription.fromString(schemaString);
```

这种方式虽然把数据转为pixels的内部格式，但是数据并没有被真正导入数据库中，如果想进行完整的数据导入流程，需要先启动整个数据库和metadata服务，随后通过`pixels-cli`导入数据，可以参考[pixels-docs](https://github.com/pixelsdb/pixels/blob/master/docs/INSTALL.md)和[pixels-tpch](https://github.com/pixelsdb/pixels/blob/master/docs/TPC-H.md)

