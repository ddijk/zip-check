package nl.dijkrosoft.appupdate;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public final class FileUnzipper {

    public static final int CHUNK_SIZE = 1024;

    private FileUnzipper() {
    }

    public static void unzip(File inputFile, File unzipDir) throws IOException {
        try (ZipFile zipFile = new ZipFile(inputFile)) {
            Enumeration<? extends ZipEntry> entries = zipFile.entries();

            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                String destPath = unzipDir + File.separator + entry.getName();

                if (entry.isDirectory()) {
                    File file = new File(destPath);
                    file.mkdirs();
                } else {
                    unzipFileEntry(zipFile, entry, destPath);
                }
            }
        }
    }

    private static void unzipFileEntry(ZipFile zipFile, ZipEntry entry, String destPath) throws IOException {
        System.out.println(entry.getName());
        final long entrySize = entry.getSize();
        System.out.println("size: " + entrySize);
        try (InputStream inputStream = zipFile.getInputStream(entry);
             FileOutputStream outputStream = new FileOutputStream(destPath)) {
            byte[] buffer = new byte[CHUNK_SIZE];

            int chunk;
            while ((chunk = inputStream.read(buffer)) > -1) {
                outputStream.write(buffer, 0, chunk);
            }
        }

        Path p = new File(destPath).toPath();
        BasicFileAttributes attrs = Files.readAttributes(p, BasicFileAttributes.class);
        final long size = attrs.size();
        System.out.println("attr size is " + size);
        System.out.println("-----------------");

        if (size != entrySize) {
            throw new RuntimeException("Sizes not equals for entry: " + entry.getName());
        }
    }

    public static void main(String[] args) throws IOException {


//        copyFileAndChangeSizeOfOneEntry();

        extract();
        System.out.println("Done");
    }

    private static void copyFileAndChangeSizeOfOneEntry() throws IOException {
        File zipFile = new File("/Users/isc75529/Documents/_STORIES/2021_03/16815_check_appupdate_extraction/au.zip");

        copyZipfile(zipFile, "file2.zip");
    }

    private static void extract() throws IOException {
        File zipFile = new File("/Users/isc75529/mies.zip");
        FileUnzipper.unzip(zipFile, new File("/tmp/appupdate/out"));
    }

    static void copyZipfile(File INPUT_FILENAME, String OUTPUT_FILE) throws IOException {
        ZipFile original = new ZipFile(INPUT_FILENAME);
        ZipOutputStream outputStream = new ZipOutputStream(new FileOutputStream(OUTPUT_FILE));
        Enumeration entries = original.entries();
        byte[] buffer = new byte[512];
        while (entries.hasMoreElements()) {
            ZipEntry entry = (ZipEntry) entries.nextElement();

            ZipEntry newEntry = new ZipEntry(entry.getName());
            System.out.println(String.format("File '%s' met size '%d', compressedSize '%d'", entry.getName(), entry.getSize(), entry.getCompressedSize()));
            if ("favicon.ico".equalsIgnoreCase(newEntry.getName())) {
                System.out.println(String.format("CHANGING SIZE from '%d' to '5500'", newEntry.getSize()));
                newEntry.setSize(5500);
                System.out.println("new size: "+ newEntry.getSize());
            }
            outputStream.putNextEntry(newEntry);
            if ("favicon.ico".equalsIgnoreCase(newEntry.getName())) {
                System.out.println("new size: "+ newEntry.getSize());
            }
            System.out.println("new size: "+ newEntry.getSize());
            InputStream in = original.getInputStream(entry);
            while (0 < in.available()) {
                int read = in.read(buffer);
                outputStream.write(buffer, 0, read);
                if ("favicon.ico".equalsIgnoreCase(newEntry.getName())) {
                    outputStream.write(buffer,0, read);
                }
            }
            in.close();
            outputStream.closeEntry();
        }
        outputStream.close();
    }
}
