//
// Created by yuly on 06.04.23.
//

#include "vector/DateColumnVector.h"
#include <stdexcept>
#include <iostream>

DateColumnVector::DateColumnVector(uint64_t len, bool encoding): ColumnVector(len, encoding) {
	if(encoding) {
        posix_memalign(reinterpret_cast<void **>(&dates), 32,
                       len * sizeof(int32_t));
	} else {
		this->dates = nullptr;
	}
	memoryUsage += (long) sizeof(int) * len;
}

void DateColumnVector::close() {
	if(!closed) {
		if(encoding && dates != nullptr) {
			free(dates);
		}
		dates = nullptr;
		ColumnVector::close();
	}
}

void DateColumnVector::print(int rowCount) {
	for(int i = 0; i < rowCount; i++) {
		std::cout<<dates[i]<<std::endl;
	}
}

DateColumnVector::~DateColumnVector() {
	if(!closed) {
		DateColumnVector::close();
	}
}

/**
     * Set a row from a value, which is the days from 1970-1-1 UTC.
     * We assume the entry has already been isRepeated adjusted.
     *
     * @param elementNum
     * @param days
 */
void DateColumnVector::set(int elementNum, int days) {
	if(elementNum >= writeIndex) {
		writeIndex = elementNum + 1;
	}
	dates[elementNum] = days;
	// TODO: isNull
}

void * DateColumnVector::current() {
    if(dates == nullptr) {
        return nullptr;
    } else {
        return dates + readIndex;
    }
}


void DateColumnVector::add(const std::string &value) {
    try {
        // 将日期字符串转为 int64_t 类型的天数
        int64_t date = std::stoll(value);  // 假设日期已经是天数
        add(date);  // 调用 add(int64_t) 方法
    } catch (const std::invalid_argument &e) {
        throw InvalidArgumentException("Invalid date format: " + value);
    } catch (const std::out_of_range &e) {
        throw InvalidArgumentException("Date value out of range: " + value);
    }
}

void DateColumnVector::add(int64_t value) {
    if (writeIndex >= length) {
        ensureSize(writeIndex * 2, true);  // 扩展为原来两倍
    }

    // 添加日期值
    dates[writeIndex] = static_cast<int>(value);  // 将 int64_t 转换为 int 存储
    isNull[writeIndex] = false;  // 标记为非 null
    writeIndex++;
}

void DateColumnVector::addNull() {
    ensureSize(writeIndex * 2, true);  // 扩展容量为原来两倍
    dates[writeIndex] = 0;  // 用 0 表示 null 日期
    isNull[writeIndex] = true;  // 标记为 null
    writeIndex++;
}

void DateColumnVector::ensureSize(uint64_t size, bool preserveData) {
    if (size > length) {
        int *newDates = new int[size];
        uint8_t *newIsNull = new uint8_t[size];

        if (preserveData) {
            std::copy(dates, dates + length, newDates);
            std::copy(isNull, isNull + length, newIsNull);
        }

        delete[] dates;
        delete[] isNull;

        dates = newDates;
        isNull = newIsNull;

        length = size;
    }
}

