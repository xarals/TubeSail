package com.xaral.tubesail.download;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class MultiThreadedDownload extends Thread {
    private int partCount = -1;
    private static final int PART_SIZE = 4024 * 1024;
    private final int numThreads;
    private final String fileUrl;
    private byte[][] fileParts;
    private boolean[] readyPart;
    private boolean saveFile = false;
    private byte[] fileData;

    public MultiThreadedDownload(String fileUrl, int numThreads) {
        this.numThreads = numThreads;
        this.fileUrl = fileUrl;
    }

    public MultiThreadedDownload(String fileUrl, int numThreads, boolean saveFile) {
        this.numThreads = numThreads;
        this.fileUrl = fileUrl;
        this.saveFile = saveFile;
    }

    public byte[] getFileData() {
        return fileData;
    }

    public int getPartCount() {
        return partCount;
    }

    public byte[] getPart(int index) {
        return fileParts[index];
    }

    public boolean readyPart(int index) {
        return readyPart[index];
    }

    @Override
    public void run() {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(fileUrl).openConnection();
            connection.setRequestMethod("HEAD");
            connection.connect();
            long contentLength = connection.getContentLengthLong();
            connection.disconnect();

            partCount = (int) (contentLength / PART_SIZE) + 1;
            fileParts = new byte[partCount][];
            readyPart = new boolean[partCount];
            List<Future<byte[]>> futures = new ArrayList<>();

            ExecutorService executor = Executors.newFixedThreadPool(numThreads);

            for (int i = 0; i < partCount; i++) {
                long start = (long) i * PART_SIZE;
                if (start >= contentLength)
                    break;

                long end = start + PART_SIZE - 1;
                if (end > contentLength)
                    end = contentLength - 1;

                DownloadThread task = new DownloadThread(fileUrl, start, end);

                Future<byte[]> future = executor.submit(new Callable<byte[]>() {
                    @Override
                    public byte[] call() throws Exception {
                        task.start();
                        task.join();
                        return task.getPartData();
                    }
                });

                futures.add(future);
            }

            if (saveFile) {
                fileData = new byte[(int) contentLength];
                int offset = 0;
                for (Future<byte[]> future : futures) {
                    byte[] part = future.get();
                    System.arraycopy(part, 0, fileData, offset, part.length);
                    offset += part.length;
                }
            } else {
                for (int i = 0; i < futures.size(); i++) {
                    byte[] part = futures.get(i).get();
                    fileParts[i] = part;
                    readyPart[i] = true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
