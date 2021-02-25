package de.dgs.apps.osfxe.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

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
 * Helper class to extract a ZIP directory to a certain target directory.
 */
public class ZipUtil {
    /**
     * Extract a ZIP input stream to a certain target directory.
     * @param inputStream The {@link InputStream} containing the ZIP data.
     * @param targetDirectory The target directory to extract to.
     * @throws IOException {@link IOException} if an error occurs.
     */
    public static void zipInputStreamToDirectory(InputStream inputStream, File targetDirectory) throws IOException {
        byte[] buffer = new byte[2048];
        ZipInputStream zipInputStream = new ZipInputStream(inputStream);

        ZipEntry zipEntry = zipInputStream.getNextEntry();

        while (zipEntry != null) {
            File newFile = zipEntryToFile(zipEntry, targetDirectory);

            if (zipEntry.isDirectory()) {
                if (!newFile.isDirectory() && !newFile.mkdirs())
                    throw new IOException("Unable to create zip directory: " + newFile);
            }
            else {
                File parent = newFile.getParentFile();

                //Workaround for archives created by Windows.
                //See https://www.baeldung.com/java-compress-and-uncompress.
                if (!parent.isDirectory() && !parent.mkdirs())
                    throw new IOException("Unable to create zip directory: " + parent);

                FileOutputStream fileOutputStream = new FileOutputStream(newFile);

                int readBytes;

                while ((readBytes = zipInputStream.read(buffer)) > 0)
                    fileOutputStream.write(buffer, 0, readBytes);

                fileOutputStream.close();
            }

            zipEntry = zipInputStream.getNextEntry();
        }

        zipInputStream.closeEntry();
        zipInputStream.close();
    }

    private static File zipEntryToFile(ZipEntry zipEntry, File destinationDirectory) throws IOException {
        File destinationFile = new File(destinationDirectory, zipEntry.getName());

        String destinationDirectoryPath = destinationDirectory.getCanonicalPath();
        String destinationFilePath = destinationFile.getCanonicalPath();

        if (!destinationFilePath.startsWith(destinationDirectoryPath + File.separator))
            throw new IOException("Zip entry is outside of the target directory: " + zipEntry.getName());

        return destinationFile;
    }
}
