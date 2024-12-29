# CLion 开发配置

1.拉取项目并且执行make pull



2.打开项目mini-pixels目录下面的pixels-dukcdb 因为clion默认project目录下的CMakeLists.txt为根cmake



![image-20241229121331338](/home/whz/mini-pixels/figure/image-20241229121331338.png)

3.配置cmake options

需要配置如下几项

```
-DDUCKDB_EXTENSION_NAMES="pixels"
-DDUCKDB_EXTENSION_PIXELS_PATH=/home/whz/mini-pixels
-DDUCKDB_EXTENSION_PIXELS_SHOULD_LINK="TRUE"
-DDUCKDB_EXTENSION_PIXELS_INCLUDE_PATH=/home/whz/mini-pixels/include
-DCMAKE_PREFIX_PATH=/home/whz/mini-pixels/third-party/protobuf/cmake/build
```

![image-20241229122515503](/home/whz/mini-pixels/figure/image-20241229122515503.png)





保存后 clion会自动build

![image-20241229121901319](/home/whz/mini-pixels/figure/image-20241229121901319.png)

此时打开的工作目录是mini-pixels,项目目录是pixels-duckdb

4.点击pixels-cli作为目标,设置环境变量进行运行或者调试

![image-20241229122830709](/home/whz/mini-pixels/figure/image-20241229122830709.png)



