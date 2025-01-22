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
 
 
 #include "DateColumnWriter.h"
#include "ColumnVector.h"
#include "encoding/RunLenIntEncoder.h"
#include "PixelsWriterOption.h"

DateColumnWriter::DateColumnWriter(std::shared_ptr<TypeDescription> type, std::shared_ptr<PixelsWriterOption> writerOption)
    : ColumnWriter(type, writerOption), runlengthEncoding(writerOption->useRunLengthEncoding) {
    if (runlengthEncoding) {
        encoder = std::make_unique<RunLenIntEncoder>();
    }
}

int DateColumnWriter::write(std::shared_ptr<ColumnVector> vector, int length) {
    // 获取日期列向量
    auto columnVector = std::static_pointer_cast<DateColumnVector>(vector);
    if (!columnVector) {
        throw std::invalid_argument("Invalid vector type");
    }

    // 如果启用了 run-length 编码，则使用编码器
    if (runlengthEncoding) {
        encoder->encode(columnVector->values, length);
    } else {
        // 否则，直接将数据写入文件
        writeCurPartTime(columnVector, columnVector->values, length, 0);
    }

    return length;  // 返回写入的数据数量
}

void DateColumnWriter::close() {
    // 在此处完成任何必要的清理工作
    if (encoder) {
        encoder->flush();  // 刷新编码器
    }

    // 清理当前像素数据
    curPixelVector.clear();
}

void DateColumnWriter::newPixel() {
    // 开始一个新的像素块，初始化相关的数据
    curPixelVector.clear();
}

void DateColumnWriter::writeCurPartTime(std::shared_ptr<ColumnVector> columnVector, int* values, int curPartLength, int curPartOffset) {
    // 如果启用了 Run-Length 编码
    if (runlengthEncoding) {
        encoder->encode(values + curPartOffset, curPartLength);
    } else {
        // 否则，直接将当前的日期值添加到像素数据中
        for (int i = 0; i < curPartLength; ++i) {
            curPixelVector.push_back(values[i]);
        }
    }
}

bool DateColumnWriter::decideNullsPadding(std::shared_ptr<PixelsWriterOption> writerOption) {
    // 根据写入选项决定是否需要填充空值
    return writerOption->nullPaddingEnabled;
}

pixels::proto::ColumnEncoding DateColumnWriter::getColumnChunkEncoding() const {
    // 返回当前列的编码方式
    return runlengthEncoding ? pixels::proto::ColumnEncoding::RLE : pixels::proto::ColumnEncoding::PLAIN;
}
