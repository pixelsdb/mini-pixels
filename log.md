在pixels_DB/pixels/mini-pixels/pixels-core/include/exception/PixelsFileVersionInvalidException.h
和pixels_DB/pixels/mini-pixels/pixels-common/include/physical/Request.h
加入#include <cstdint>
不然会编译报错


在ColumnVector.cpp中的resize和ensuresize似乎自相矛盾了，一个是要size大于this->length，一个是小于？

这个ensuresize似乎没啥问题？先不管看看
