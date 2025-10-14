/*
 * Copyright 2018-2019 PixelsDB.
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
package io.pixelsdb.pixels.cli;

import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;

import java.util.Scanner;

/**
 * @author tao
 * @author hank
 * @create in 2018-10-30 11:07
 **/

/**
 * pixels loader command line tool
 * <p>
 * LOAD -f pixels -o s3://text-105/source -s pixels -t test_105 -n 275000 -r \t -c 16 -l s3://pixels-105/v-0-order
 * [-l] is optional, its default value is the orderPath of the last writable layout of the table.
 *
 * <br>This should be run under root user to execute cache cleaning commands
 * <p>
 * QUERY -w /home/pixels/opt/pixels/1187_dedup_query.txt -l /home/pixels/opt/pixels/pixels_duration_1187_v_1_compact_cache_2020.01.10-2.csv -d /home/pixels/opt/presto-server/sbin/drop-caches.sh
 * </p>
 * <p>
 * QUERY -w /home/pixels/opt/pixels/105_dedup_query.txt -l /home/pixels/opt/pixels/pixels_duration_local.csv
 * </p>
 * <p>
 * QUERY -w /home/pixels/opt/pixels/105_dedup_query.txt -l /home/pixels/opt/pixels/pixels_duration_local.csv -r true -q 3
 * </p>
 * <p>
 * COPY -p .pxl -s hdfs://node01:9000/pixels/pixels/test_105/v_1_order -d hdfs://node01:9000/pixels/pixels/test_105/v_1_order -n 3 -c 3
 * </p>
 * <p>
 * COMPACT -s pixels -t test_105 -n yes -c 8
 * </p>
 * <p>
 * STAT -s tpch -t region
 * </p>
 */
public class Main
{
    public static void main(String args[])
    {
        Scanner scanner = new Scanner(System.in);
        String inputStr;

        while (true)
        {
            System.out.print("pixels> ");
            if (scanner.hasNextLine())
            {
                inputStr = scanner.nextLine().trim();
            }
            else
            {
                // Issue #631: in case of input from a file, exit at EOF.
                System.out.println("Bye.");
                break;
            }

            if (inputStr.isEmpty() || inputStr.equals(";"))
            {
                continue;
            }

            if (inputStr.endsWith(";"))
            {
                inputStr = inputStr.substring(0, inputStr.length() - 1);
            }

            if (inputStr.equalsIgnoreCase("exit") || inputStr.equalsIgnoreCase("quit") ||
                    inputStr.equalsIgnoreCase("-q"))
            {
                System.out.println("Bye.");
                break;
            }

            if (inputStr.equalsIgnoreCase("help") || inputStr.equalsIgnoreCase("-h"))
            {
                System.out.println("Supported commands:\n" +
                        "LOAD\n" +
                        "COMPACT\n" +
                        "IMPORT\n" +
                        "STAT\n" +
                        "QUERY\n" +
                        "COPY\n" +
                        "FILE_META");
                System.out.println("{command} -h to show the usage of a command.\nexit / quit / -q to exit.\n");
                continue;
            }

            String command = inputStr.trim().split("\\s+")[0].toUpperCase();

            if (command.equals("LOAD"))
            {
                ArgumentParser argumentParser = ArgumentParsers.newArgumentParser("Pixels ETL LOAD")
                        .defaultHelp(true);

                argumentParser.addArgument("-o", "--origin").required(true)
                        .help("specify the path of original data files");
                argumentParser.addArgument("-s", "--schema").required(true)
                        .help("specify the name of database");
                argumentParser.addArgument("-t", "--table").required(true)
                        .help("specify the name of table");
                argumentParser.addArgument("-n", "--row_num").required(true)
                        .help("specify the max number of rows to write in a file");
                argumentParser.addArgument("-r", "--row_regex").required(true)
                        .help("specify the split regex of each row in a file");
                argumentParser.addArgument("-c", "--consumer_thread_num").setDefault("4").required(true)
                        .help("specify the number of consumer threads used for data generation");
                argumentParser.addArgument("-e", "--encoding_level").setDefault("2")
                        .help("specify the encoding level for data loading");
                argumentParser.addArgument("-p", "--nulls_padding").setDefault(false)
                        .help("specify whether nulls padding is enabled");

                Namespace ns;
                try
                {
                    ns = argumentParser.parseArgs(inputStr.substring(command.length()).trim().split("\\s+"));
                } catch (ArgumentParserException e)
                {
                    argumentParser.handleError(e);
                    continue;
                }

                try
                {
                    // do something
                } catch (Exception e)
                {
                    e.printStackTrace();
                }
            }

            if (!command.equals("QUERY"))
            {
                System.out.println("Command '" + command + "' not found");
            }
        }
        // Use exit to terminate other threads and invoke the shutdown hooks.
        scanner.close();
        System.exit(0);
    }
}