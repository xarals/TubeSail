package com.xaral.tubesail.download;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

class DownloadThread extends Thread {
    private final String url;
    private final long start;
    private final long end;
    private final byte[] partData;

    public DownloadThread(String url, long start, long end) {
        this.url = url;
        this.start = start;
        this.end = end;
        this.partData = new byte[(int)(end - start + 1)];
    }

    public byte[] getPartData() {
        return partData;
    }

    @Override
    public void run() {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestProperty("Range", "bytes=" + start + "-" + end);
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/126.0.0.0 Safari/537.36");
            connection.connect();

            try (InputStream inputStream = connection.getInputStream()) {

                int bytesRead;
                int offset = 0;
                byte[] buffer = new byte[1024];

                while ((bytesRead = inputStream.read(buffer, 0, buffer.length)) != -1) {
                    System.arraycopy(buffer, 0, partData, offset, bytesRead);
                    offset += bytesRead;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

