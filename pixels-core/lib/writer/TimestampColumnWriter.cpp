/*
 * Copyright 2024 PixelsDB.
 *
 * This file is part of Pixels.
 *
 * Pixels is free software: you can redistribute it and/or modify
 * it under the terms of the Affero GNU General Public License as
 * published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 *
 * Pixels is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * Affero GNU General Public License for more details.
 *
 * You should have received a copy of the Affero GNU General Public
 * License along with Pixels.  If not, see
 * <https://www.gnu.org/licenses/>.
 */
 
 #include "TimestampColumnWriter.h"
#include "ColumnVector.h"
#include "encoding/RunLenIntEncoder.h"
#include "PixelsWriterOption.h"

TimestampColumnWriter::TimestampColumnWriter(std::shared_ptr<TypeDescription> type, std::shared_ptr<PixelsWriterOption> writerOption)
    : ColumnWriter(type, writerOption), runlengthEncoding(writerOption->useRunLengthEncoding) {
    // 如果启用了run-length编码，则初始化编码器
    if (runlengthEncoding) {
        encoder = std::make_unique<RunLenIntEncoder>();
    }
}

int TimestampColumnWriter::write(std::shared_ptr<ColumnVector> vector, int length) {
    auto columnVector = std::static_pointer_cast<TimestampColumnVector>(vector);
    if (!columnVector) {
        throw std::invalid_argument("无效的列向量类型，期望 TimestampColumnVector。");
    }

    if (runlengthEncoding) {
        // 如果启用了run-length编码，对时间戳进行编码
        encoder->encode(columnVector->values, length);
    } else {
        // 如果没有启用编码，直接写入时间戳
        for (int i = 0; i < length; ++i) {
            // 在这里可以将时间戳写入文件或缓冲区
        }
    }

    return length;  // 返回写入的值的数量
}

void TimestampColumnWriter::close() {
    // 关闭时进行必要的清理工作
    if (encoder) {
        encoder->flush();  // 确保所有数据都被写出
    }
}

void TimestampColumnWriter::newPixel() {
    // 处理每个新像素时需要的操作，例如初始化像素数据
    curPixelVector.clear();  // 清空当前的像素值向量
}

void TimestampColumnWriter::writeCurPartTimestamp(std::shared_ptr<ColumnVector> columnVector, long* values, int curPartLength, int curPartOffset) {
    auto timestampVector = std::static_pointer_cast<TimestampColumnVector>(columnVector);
    if (!timestampVector) {
        throw std::invalid_argument("无效的列向量类型，期望 TimestampColumnVector。");
    }

    // 写入当前部分的时间戳数据
    for (int i = 0; i < curPartLength; ++i) {
        curPixelVector.push_back(values[curPartOffset + i]);
    }

    // 一旦所有部分数据都写入，执行写入操作
    if (curPixelVector.size() > 0) {
        // 示例：将 curPixelVector 写入文件或执行其他操作
        curPixelVector.clear();  // 写入后清空
    }
}

bool TimestampColumnWriter::decideNullsPadding(std::shared_ptr<PixelsWriterOption> writerOption) {
    // 根据writerOption中的配置决定是否启用空值填充
    return writerOption->nullPaddingEnabled;
}

pixels::proto::ColumnEncoding TimestampColumnWriter::getColumnChunkEncoding() const {
    // 返回当前列的编码类型
    return runlengthEncoding ? pixels::proto::ColumnEncoding::RUN_LENGTH_ENCODING : pixels::proto::ColumnEncoding::PLAIN;
}
