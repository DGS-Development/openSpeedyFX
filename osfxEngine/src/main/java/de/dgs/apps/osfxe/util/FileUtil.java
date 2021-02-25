package de.dgs.apps.osfxe.util;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

/*
Copyright 2021 DGS-Development (https://github.com/DGS-Development)

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */

/*
 * Helper class to interact with files.
 */
public class FileUtil {
    /**
     * Returns all files inside a certain directory.
     * @param directory The directory to search for files.
     * @return All found files inside the directory.
     */
    public static List<File> listFiles(File directory) {
        return listFiles(directory, null);
    }

    /**
     * Returns all files inside a certain directory.
     * @param directory The directory to search for files.
     * @param fileEnding The file ending of the files to find.
     * @return All found files with the provided file ending.
     */
    public static List<File> listFiles(File directory, String fileEnding) {
        List<File> files = new LinkedList<>();

        listFiles(directory, fileEnding, files);

        return files;
    }

    private static void listFiles(File directory, String fileEnding, List<File> files) {
        if(directory.isFile()) {
            if(fileEnding == null || directory.getName().toLowerCase().endsWith(fileEnding))
                files.add(directory);
        }
        else if(directory.isDirectory()) {
            File[] listedFiles = directory.listFiles();

            if(listedFiles == null)
                return;

            for(File tmpFile : listedFiles) {
                if(tmpFile.isDirectory()) {
                    listFiles(tmpFile, fileEnding, files);
                }
                else if(tmpFile.isFile()) {
                    if(fileEnding == null || tmpFile.getName().toLowerCase().endsWith(fileEnding))
                        files.add(tmpFile);
                }
            }
        }
    }
}
