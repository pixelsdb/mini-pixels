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
 
#include "DecimalColumnWriter.h"
#include "../include/vector/ColumnVector.h"
#include "encoding/RunLenIntEncoder.h"
#include "PixelsWriterOption.h"
#include "utils/EncodingUtils.h"

std::shared_ptr<PixelsWriterOption> writerOption = getWriterOption();

DecimalColumnWriter::DecimalColumnWriter(std::shared_ptr<TypeDescription> type, std::shared_ptr<PixelsWriterOption> writerOption)
    : ColumnWriter(type, writerOption) {
    // You can initialize additional members here if needed, like encoding strategies
}

int DecimalColumnWriter::write(std::shared_ptr<ColumnVector> vector, int length) {
    // Retrieve the decimal column vector
    auto columnVector = std::static_pointer_cast<DecimalColumnVector>(vector);
    if (!columnVector) {
        throw std::invalid_argument("Invalid vector type");
    }

    // Example: Assuming you want to encode decimals with run-length encoding (RLE)
    if (writerOption->useRunLengthEncoding) {
        // Perform run-length encoding for decimals
        RunLenIntEncoder encoder;
        encoder.encode(columnVector->values, length);
    } else {
        // If RLE isn't used, write values as they are
        for (int i = 0; i < length; ++i) {
            // Here you would write each decimal value (this is just a placeholder for actual logic)
            // For example, columnVector->values[i] can be written to a file or buffer
        }
    }

    return length;  // Return the number of values written
}

bool DecimalColumnWriter::decideNullsPadding(std::shared_ptr<PixelsWriterOption> writerOption) {
    // Decide if null padding is needed based on the writer options
    return writerOption->nullPaddingEnabled;
}
