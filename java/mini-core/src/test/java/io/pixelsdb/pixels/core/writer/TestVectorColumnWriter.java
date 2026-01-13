/*
 * Copyright 2017-2019 PixelsDB.
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
package io.pixelsdb.pixels.core.writer;
import io.jhdf.HdfFile;
import io.jhdf.api.Dataset;
import io.pixelsdb.pixels.common.physical.Storage;
import io.pixelsdb.pixels.common.physical.StorageFactory;
import io.pixelsdb.pixels.core.*;
import io.pixelsdb.pixels.core.encoding.EncodingLevel;
import io.pixelsdb.pixels.core.exception.PixelsWriterException;
import io.pixelsdb.pixels.core.reader.PixelsReaderOption;
import io.pixelsdb.pixels.core.reader.PixelsRecordReader;
import io.pixelsdb.pixels.core.vector.LongColumnVector;
import io.pixelsdb.pixels.core.vector.VectorColumnVector;
import io.pixelsdb.pixels.core.vector.VectorizedRowBatch;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * TestVectorColumnWriter
 * Tests reading HDF5 vector dataset and writing/reading with Pixels format
 *
 * @author guodong
 * @author hank
 * @author whz
 */
public class TestVectorColumnWriter {

    // Simple test with uniform vectors
    @Test
    public void testSimpleWrite() throws IOException {
        int length = 5;
        int dimension = 2;
        VectorColumnVector vectorColumnVector = new VectorColumnVector(length, dimension);
        vectorColumnVector.reset();
        vectorColumnVector.init();
        for (int i = 0; i < length; ++i) {
            vectorColumnVector.add(getUniformVec(dimension, 1.1));
        }
        PixelsWriterOption pixelsWriterOption = new PixelsWriterOption()
                .pixelStride(10000).encodingLevel(EncodingLevel.EL2).byteOrder(ByteOrder.BIG_ENDIAN);
        VectorColumnWriter vectorColumnWriter = new VectorColumnWriter(
                TypeDescription.createVector(dimension), pixelsWriterOption);
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < length; ++i) {
            vectorColumnWriter.write(vectorColumnVector, vectorColumnVector.getLength());
        }
        System.out.println("Simple write completed in: " + (System.currentTimeMillis() - startTime) + "ms");
    }

    /**
     * Test reading from nytimes-16-angular.hdf5 and writing to Pixels format
     */
    @Test
    public void testReadWriteNYTimesDataset() {
        String hdf5FilePath = "/home/whz/test/mini-pixels/java/nytimes-16-angular.hdf5";
        String pixelsOutputPath = "/home/whz/test/mini-pixels/java/vectorOut/nytimes-vectors.pxl";

        // delete if exist
        File outVectorFile = new File(pixelsOutputPath);
        if (outVectorFile.exists()) {
            if (!outVectorFile.delete()) {
                throw new RuntimeException(
                        "Failed to delete existing file: " + outVectorFile.getAbsolutePath()
                );
            }
        }

        // Check if HDF5 file exists
        File hdf5File = new File(hdf5FilePath);
        if (!hdf5File.exists()) {
            System.err.println("HDF5 file not found: " + hdf5FilePath);
            System.err.println("Please ensure the file exists in the current directory.");
            return;
        }

        try {
            System.out.println("Reading HDF5 file: " + hdf5FilePath);

            // Read HDF5 file
            HdfFile hdfFile = new HdfFile(hdf5File);

            // Get train dataset (contains the vectors)
            Dataset trainDataset = hdfFile.getDatasetByPath("/train");
            if (trainDataset == null) {
                System.err.println("Could not find /train dataset in HDF5 file");
                hdfFile.close();
                return;
            }

            // Read train data - it's stored as float array
            Object trainData = trainDataset.getData();
            float[][] trainVectors = null;

            if (trainData instanceof float[][]) {
                trainVectors = (float[][]) trainData;
            } else {
                System.err.println("Unexpected data type for train dataset: " + trainData.getClass());
                hdfFile.close();
                return;
            }

            int numVectors = trainVectors.length;
            int dimension = trainVectors[0].length;

            System.out.println("Dataset info:");
            System.out.println("  Number of vectors: " + numVectors);
            System.out.println("  Vector dimension: " + dimension);
            System.out.println("  First vector: " + Arrays.toString(Arrays.copyOf(trainVectors[0], Math.min(5, dimension))));

            // Write to Pixels format
            writeVectorsToPixels(trainVectors, pixelsOutputPath, dimension);

            // Read back and verify
            readAndVerifyPixelsVectors(pixelsOutputPath, trainVectors, dimension);

            hdfFile.close();
            System.out.println("\nTest completed successfully!");
        } catch (IOException e) {
            System.err.println("Error reading HDF5 file or writing Pixels file:");
            e.printStackTrace();
        }
    }

    /**
     * Write vectors to Pixels format with id column
     */
    private void writeVectorsToPixels(float[][] vectors, String outputPath, int dimension)
            throws IOException {
        System.out.println("\nWriting vectors to Pixels format: " + outputPath);
        long startTime = System.currentTimeMillis();

        try {
            Storage storage = StorageFactory.Instance().getStorage("file");

            // Schema: struct<id:bigint,vector:vector(dimension)>
            String schemaString = "struct<id:bigint,vector:vector(16)>";
            TypeDescription schema = TypeDescription.fromString(schemaString);

            VectorizedRowBatch rowBatch = schema.createRowBatch();
            LongColumnVector idVector = (LongColumnVector) rowBatch.cols[0];
            VectorColumnVector vectorVector = (VectorColumnVector) rowBatch.cols[1];

            PixelsWriter pixelsWriter = PixelsWriterImpl.newBuilder()
                    .setSchema(schema)
                    .setPixelStride(10000)
                    .setRowGroupSize(8192)
                    .setStorage(storage)
                    .setPath(outputPath)
                    .setBlockSize(64 * 1024 * 1024)  // 64MB
                    .setReplication((short) 1)
                    .setBlockPadding(true)
                    .setEncodingLevel(EncodingLevel.EL2)
                    .setCompressionBlockSize(1024)
                    .build();

            int rowsWritten = 0;
            for (int i = 0; i < vectors.length; i++) {
                int row = rowBatch.size++;

                // Set ID
                idVector.vector[row] = i;
                idVector.isNull[row] = false;

                // Convert float[] to double[]
                double[] doubleVector = new double[dimension];
                for (int j = 0; j < dimension; j++) {
                    doubleVector[j] = vectors[i][j];
                }

                // Set vector
                vectorVector.vector[row] = doubleVector;
                vectorVector.isNull[row] = false;

                if (rowBatch.size == rowBatch.getMaxSize()) {
                    pixelsWriter.addRowBatch(rowBatch);
                    rowsWritten += rowBatch.size;
                    rowBatch.reset();
                }
            }

            if (rowBatch.size != 0) {
                pixelsWriter.addRowBatch(rowBatch);
                rowsWritten += rowBatch.size;
                rowBatch.reset();
            }

            pixelsWriter.close();

            long duration = System.currentTimeMillis() - startTime;
            System.out.println("  Written " + rowsWritten + " vectors in " + duration + "ms");
            System.out.println("  Write throughput: " + (rowsWritten * 1000.0 / duration) + " vectors/sec");
        } catch (PixelsWriterException e) {
            throw new IOException("Failed to write Pixels file", e);
        }
    }

    /**
     * Read back the Pixels file and verify the data
     */
    private void readAndVerifyPixelsVectors(String pixelsPath, float[][] originalVectors, int dimension)
            throws IOException {
        System.out.println("\nReading and verifying Pixels file: " + pixelsPath);
        long startTime = System.currentTimeMillis();

        Storage storage = StorageFactory.Instance().getStorage("file");

        PixelsReaderOption option = new PixelsReaderOption();
        String[] cols = {"id", "vector"};
        option.skipCorruptRecords(true);
        option.tolerantSchemaEvolution(true);
        option.includeCols(cols);

        PixelsReader pixelsReader = PixelsReaderImpl.newBuilder()
                .setStorage(storage)
                .setPath(pixelsPath)
                .setEnableCache(false)
                .setCacheOrder(new ArrayList<>())
                .setPixelsFooterCache(new PixelsFooterCache())
                .build();

        PixelsRecordReader recordReader = pixelsReader.read(option);

        int rowsRead = 0;
        int mismatchCount = 0;
        int samplesDisplayed = 0;
        final int MAX_SAMPLES = 5;

        VectorizedRowBatch rowBatch;
        while ((rowBatch = recordReader.readBatch()) != null && rowBatch.size > 0) {
            LongColumnVector idVector = (LongColumnVector) rowBatch.cols[0];
            VectorColumnVector vectorVector = (VectorColumnVector) rowBatch.cols[1];

            for (int i = 0; i < rowBatch.size; i++) {
                long id = idVector.vector[i];
                double[] readVector = vectorVector.vector[i];

                // Verify vector dimension
                if (readVector.length != dimension) {
                    System.err.println("ERROR: Vector dimension mismatch at id=" + id +
                            ": expected=" + dimension + ", actual=" + readVector.length);
                    mismatchCount++;
                    continue;
                }

                // Verify vector content (allow small floating point error)
                boolean match = true;
                for (int j = 0; j < dimension; j++) {
                    if (Math.abs(readVector[j] - originalVectors[(int) id][j]) > 1e-6) {
                        match = false;
                        break;
                    }
                }

                if (!match) {
                    mismatchCount++;
                    if (samplesDisplayed < MAX_SAMPLES) {
                        System.err.println("ERROR: Vector content mismatch at id=" + id);
                        System.err.println("  Original: " + Arrays.toString(Arrays.copyOf(originalVectors[(int) id], Math.min(5, dimension))));
                        System.err.println("  Read:     " + Arrays.toString(Arrays.copyOf(readVector, Math.min(5, dimension))));
                        samplesDisplayed++;
                    }
                } else if (samplesDisplayed < MAX_SAMPLES) {
                    // Display first few matching records as samples
                    System.out.println("Sample vector id=" + id + ": " +
                            Arrays.toString(Arrays.copyOf(readVector, Math.min(5, dimension))) + "...");
                    samplesDisplayed++;
                }

                rowsRead++;
            }
        }

        pixelsReader.close();

        long duration = System.currentTimeMillis() - startTime;
        System.out.println("  Read " + rowsRead + " vectors in " + duration + "ms");
        System.out.println("  Read throughput: " + (rowsRead * 1000.0 / duration) + " vectors/sec");

        if (mismatchCount == 0) {
            System.out.println("  ✓ All vectors verified successfully!");
        } else {
            System.err.println("  ✗ Found " + mismatchCount + " mismatches out of " + rowsRead + " vectors");
        }

        if (rowsRead != originalVectors.length) {
            System.err.println("  ✗ Row count mismatch: expected=" + originalVectors.length +
                    ", actual=" + rowsRead);
        }
    }

    /**
     * Test reading test set from nytimes dataset
     */
    @Test
    public void testReadWriteNYTimesTestSet() {
        String hdf5FilePath = "/home/whz/test/mini-pixels/java/nytimes-16-angular.hdf5";
        String pixelsOutputPath = "/home/whz/test/mini-pixels/java/vectorOut/nytimes-vectors.pxl";
        // delete if exist
        File outVectorFile = new File(pixelsOutputPath);
        if (outVectorFile.exists()) {
            if (!outVectorFile.delete()) {
                throw new RuntimeException(
                        "Failed to delete existing file: " + outVectorFile.getAbsolutePath()
                );
            }
        }
        File hdf5File = new File(hdf5FilePath);
        if (!hdf5File.exists()) {
            System.err.println("HDF5 file not found: " + hdf5FilePath);
            return;
        }

        try {
            System.out.println("Reading test set from HDF5 file: " + hdf5FilePath);

            HdfFile hdfFile = new HdfFile(hdf5File);

            // Get test dataset
            Dataset testDataset = hdfFile.getDatasetByPath("/test");
            if (testDataset == null) {
                System.err.println("Could not find /test dataset in HDF5 file");
                hdfFile.close();
                return;
            }

            Object testData = testDataset.getData();
            float[][] testVectors = null;

            if (testData instanceof float[][]) {
                testVectors = (float[][]) testData;
            } else {
                System.err.println("Unexpected data type for test dataset: " + testData.getClass());
                hdfFile.close();
                return;
            }

            int numVectors = testVectors.length;
            int dimension = testVectors[0].length;

            System.out.println("Test dataset info:");
            System.out.println("  Number of vectors: " + numVectors);
            System.out.println("  Vector dimension: " + dimension);

            // Write to Pixels format
            writeVectorsToPixels(testVectors, pixelsOutputPath, dimension);

            // Read back and verify
            readAndVerifyPixelsVectors(pixelsOutputPath, testVectors, dimension);

            hdfFile.close();
            System.out.println("\nTest set processing completed successfully!");
        } catch (IOException e) {
            System.err.println("Error processing test dataset:");
            e.printStackTrace();
        }
    }

    private double[] getUniformVec(int dimension, double val) {
        double[] vec = new double[dimension];
        for (int i = 0; i < dimension; i++) {
            vec[i] = val;
        }
        return vec;
    }
}
