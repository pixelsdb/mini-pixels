# 一些实验相关的tips
因为写实验的过程中遇到了许多坑，所以写一个tips记录一下。
## 依赖
update之后（之前的版本需不需要不太记得了），需要有boost库，不然编不好。
## build
git clone 之后，需要执行`make pull`，make pull会拉取所有的子模块。

随后需要执行`make -j8` (这里8是指使用的核心数，如果直接`-j`在编译duckdb的时候很可能会直接爆掉，所以可以开小一点，实在不行可以不加`-j`，单核编译，会很慢，但不容易爆掉。)

`make`默认编译得到的是release版本，为了方便debug，建议使用`make -j8 debug`，这样会生成debug版本的可执行文件。

## run

编译完成后，会在`build/release`或者`build/debug`目录下生成文件。

进入目录下，有一个`duckdb`可以直接运行，它默认链接了pixel的扩展，可以直接读取mini-pixels的数据。（如果写对了的话）具体参照pdf中的select语句即可。

生成*.pxl文件的方法是进入到`build/realease/extension/pixel/pixels-cli`目录下，执行`./pixels-cli`，按照pdf给出的语法执行load语句。

## 任务

任务是要实现成功laod date,timestamp,decimal类型的数据。

具体我们需要做的就是找到对应的比如datecolumnvector,datecolumnwriter等.cpp或.h等未完成的文件，参照已经给出的integer的对应的函数和实现，以及pixel主仓库java版本中的实现补完代码就行。

传入的时候默认都是先调用add(string)方法，对于timestamp和date，cpp没有java那样自带的date类型，所以需要自己完成解析。（decimal也是）

## tips
1. 为了方便debug，最好编译debug版本，这样gdb更好用。
2. 目前为止还有一个date类型的writer的指针问题还没合入master，需要把有一个函数的long*改成int*。（现在的版本已改）底层的存储，date是int类型，timestamp和decimal是long类型。
3. debug版本的ub会直接报错，会导致integer类型的负数无法正确写入，但应该没关系。
4. pdf中说的-n参数指定最大行数的表述大概有一些问题，因为目前的cli并不会写多个文件，当行数超过-n时并不会默认开一个新的，而是会生成一个无法读取的文件，并且cli不会报错，小心点。
5. 在修改代码后，通常可以直接到/mini-pixels目录下执行`make -j debug`。并且能正确更新可执行文件。不到万不得已最好不要直接`make clean`，因为会把所有的东西都删掉，重新编译会很慢，因为有duckdb。
6. 目前版本的decimal，在precision低于10的时候reader的显示会有问题，别急，可以设置成比10大的。
7. 因为没有官方的测试文件，手动生成的时候请不要手抖，如果在末尾增加了几个空行，会导致`segmentation fault`，或者是`runtime error`，小心点。
8. 任务三的表述有点奇怪，我个人觉得那不是说你要写什么，而是在你完成前两步，成功make并且pixel-cli load不报错之后，用之前提到的编译出来的duckdb执行测试，看看自己有没有写对。
9. 建议虚函数全部加上override，这样编译器会帮你检查是否override正确。
10. 目前的timestamp的precision没用，duckdb读取的时候默认是秒*1e6对应的long。