package airmont.core;

import airmont.core.download.FileDownloadCallback;
import airmont.core.download.FileDownloader;

import java.net.URL;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class SimultaneousDownloads {

    private final ExecutorService executorService;
    private final LinkedList<DownloadTask> tasks;

    public SimultaneousDownloads(int threadCount) {
        executorService = Executors.newFixedThreadPool(threadCount);
        tasks = new LinkedList<>();
    }

    public Future<Path> download(URL url, Path destination, FileDownloadCallback callback) {
        RemoveFromTasksWhenDoneCallback removeFromTasksWhenDoneCallback = new RemoveFromTasksWhenDoneCallback(callback, tasks);
        DownloadTask task = new DownloadTask(url, destination, removeFromTasksWhenDoneCallback);
        removeFromTasksWhenDoneCallback.setTask(task);
        tasks.add(task);
        return executorService.submit(task);
    }

    public void stopDownload(URL url) {
        tasks.stream()
                .filter(task -> url.equals(task.getUrl()))
                .findFirst()
                .ifPresent(DownloadTask::stop);
    }

    public void stopAllDownloads() {
        tasks.forEach(DownloadTask::stop);
    }

    public void stop() {
        stopAllDownloads();
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(1000, TimeUnit.MILLISECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
        }
    }

    private static class DownloadTask implements Callable<Path> {

        private final FileDownloader fileDownloader;
        private final URL url;
        private final Path destination;
        private final FileDownloadCallback callback;

        private DownloadTask(URL url, Path destination, FileDownloadCallback callback) {
            fileDownloader = new FileDownloader();
            this.url = url;
            this.destination = destination;
            this.callback = callback;
        }

        @Override
        public Path call() throws Exception {
            return fileDownloader.download(url, destination, callback);
        }

        public URL getUrl() {
            return url;
        }

        public void stop() {
            fileDownloader.stop();
        }
    }

    private static class RemoveFromTasksWhenDoneCallback implements FileDownloadCallback {

        private final FileDownloadCallback delegate;
        private final LinkedList<DownloadTask> tasks;
        private DownloadTask task;

        private RemoveFromTasksWhenDoneCallback(FileDownloadCallback delegate, LinkedList<DownloadTask> tasks) {
            this.delegate = delegate;
            this.tasks = tasks;
        }

        @Override
        public void before(URL url, Path destinationFile) {
            delegate.before(url, destinationFile);
        }

        @Override
        public void start(Map<String, List<String>> headerFields) {
            delegate.start(headerFields);
        }

        @Override
        public void resume(long fileSizeInBytes, Map<String, List<String>> headerFields) {
            delegate.resume(fileSizeInBytes, headerFields);
        }

        @Override
        public void read(int bytesRead) {
            delegate.read(bytesRead);
        }

        @Override
        public void exception(Exception e) {
            delegate.exception(e);
        }

        @Override
        public void finish(boolean stopped) {
            tasks.remove(task);
            delegate.finish(stopped);
        }

        public void setTask(DownloadTask task) {
            this.task = task;
        }
    }

}
