//
// Created by liyu on 12/23/23.
//

#include "vector/TimestampColumnVector.h"
#include <stdexcept>
#include <iostream>

TimestampColumnVector::TimestampColumnVector(int precision, bool encoding): ColumnVector(VectorizedRowBatch::DEFAULT_SIZE, encoding) {
    TimestampColumnVector(VectorizedRowBatch::DEFAULT_SIZE, precision, encoding);
}

TimestampColumnVector::TimestampColumnVector(uint64_t len, int precision, bool encoding): ColumnVector(len, encoding) {
    this->precision = precision;
    if(encoding) {
        posix_memalign(reinterpret_cast<void **>(&this->times), 64,
                       len * sizeof(long));
    } else {
        this->times = nullptr;
    }
}


void TimestampColumnVector::close() {
    if(!closed) {
        ColumnVector::close();
        if(encoding && this->times != nullptr) {
            free(this->times);
        }
        this->times = nullptr;
    }
}

void TimestampColumnVector::print(int rowCount) {
    throw InvalidArgumentException("not support print longcolumnvector.");
//    for(int i = 0; i < rowCount; i++) {
//        std::cout<<longVector[i]<<std::endl;
//		std::cout<<intVector[i]<<std::endl;
//    }
}

TimestampColumnVector::~TimestampColumnVector() {
    if(!closed) {
        TimestampColumnVector::close();
    }
}

void * TimestampColumnVector::current() {
    if(this->times == nullptr) {
        return nullptr;
    } else {
        return this->times + readIndex;
    }
}

/**
     * Set a row from a value, which is the days from 1970-1-1 UTC.
     * We assume the entry has already been isRepeated adjusted.
     *
     * @param elementNum
     * @param days
 */
void TimestampColumnVector::set(int elementNum, long ts) {
    if(elementNum >= writeIndex) {
        writeIndex = elementNum + 1;
    }
    times[elementNum] = ts;
    // TODO: isNull
}

void TimestampColumnVector::add(const std::string &value) {
    int64_t timestamp = std::stoll(value);  // 将字符串转换为时间戳
    add(timestamp);  // 调用 add(int64_t)
}

void TimestampColumnVector::add(int64_t value) {
    ensureSize(writeIndex * 2, true);  // 扩展容量
    timestampVector.push_back(value);  // 存储时间戳
    isNull[writeIndex++] = false;  // 标记为非 null
}

void TimestampColumnVector::add(int value) {
    add(static_cast<int64_t>(value));  // 将 int 转换为 int64_t 存储
}

void TimestampColumnVector::addNull() {
    ensureSize(writeIndex * 2, true);  // 扩展容量
    timestampVector.push_back(0);  // 使用 0 表示 null
    isNull[writeIndex++] = true;  // 标记为 null
}

void TimestampColumnVector::ensureSize(uint64_t size, bool preserveData) {
    if (size > timestampVector.size()) {
        timestampVector.reserve(size * 2);  // 扩展容量
        isNull = (uint8_t*) realloc(isNull, sizeof(uint8_t) * size);  // 重新分配 isNull 数组
    }
}
