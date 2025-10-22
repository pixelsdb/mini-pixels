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
package io.pixelsdb.pixels.storage.http;

import io.pixelsdb.pixels.common.physical.*;
import org.junit.Test;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class TestHttpStream
{
    private volatile Exception readerException = null;
    private volatile Exception writerException = null;
    private final int sendLimit = 8*1024*1024;
    private final int sendNum = 600;

    @Test
    public void testPhysicalReaderAndWriter() throws IOException
    {
        Storage httpStream = StorageFactory.Instance().getStorage(Storage.Scheme.httpstream);
        Thread readerThread = new Thread(() -> {
            try
            {
                try (PhysicalReader fsReader = PhysicalReaderUtil.newPhysicalReader(
                        httpStream, "httpstream://localhost:29920"))
                {
                    int num1 = fsReader.readInt(ByteOrder.BIG_ENDIAN);
                    assert(num1 == 13);
                    num1 = fsReader.readInt(ByteOrder.BIG_ENDIAN);
                    assert(num1 == 169);

                    long num2 = fsReader.readLong(ByteOrder.BIG_ENDIAN);
                    assert(num2 == 28561L);
                    num2 = fsReader.readLong(ByteOrder.BIG_ENDIAN);
                    assert(num2 == 815730721L);

                    boolean failed = false;
                    byte[] buffer = new byte[sendLimit];
                    for (int i = 0; i < sendNum; i++)
                    {
                        fsReader.readFully(buffer);
                        /*
                        for (int j = 0; j < sendLimit; j++)
                        {
                            byte tmp = buffer[j];
                            if (tmp != (byte) ('a' + j % 10))
                            {
                                System.out.println("failed sendNum " + i + " sendLen " + sendLimit + " tmp: " + tmp);
                                failed = true;
                            }
                        }*/
                    }
                    if (failed)
                    {
                        throw new IOException("failed");
                    }
                }
            }
            catch (IOException e)
            {
                readerException = e;
                throw new RuntimeException(e);
            }
        });
        Thread writerThread = new Thread(() -> {
            try
            {
                try (PhysicalWriter fsWriter = PhysicalWriterUtil.newPhysicalWriter(
                        httpStream, "httpstream://localhost:29920"))
                {
                    ByteBuffer buffer = ByteBuffer.allocate(24);
                    buffer.putInt(13);
                    buffer.putInt(169);
                    buffer.putLong(28561L);
                    buffer.putLong(815730721L);
                    fsWriter.append(buffer);
                    fsWriter.flush();
                    buffer = ByteBuffer.allocate(sendLimit);
                    for (int j = 0; j < sendLimit; j++)
                    {
                        buffer.put((byte) ('a' + j % 10));
                    }
                    for (int i = 0; i < sendNum; i++)
                    {
                        fsWriter.append(buffer.array(), 0, sendLimit);
                        fsWriter.flush();
                        Thread.sleep(1);
                    }
                }
                catch (InterruptedException e)
                {
                    throw new RuntimeException(e);
                }
            }
            catch (IOException e)
            {
                writerException = e;
                throw new RuntimeException(e);
            }
        });
        readerThread.start();
        writerThread.start();
        try
        {
            readerThread.join();
            writerThread.join();
            if (this.readerException != null || this.writerException != null)
            {
                throw new IOException();
            }
        }
        catch (InterruptedException e)
        {
            throw new RuntimeException(e);
        }
    }
}
