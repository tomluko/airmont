package airmont.core.server;

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.lang.annotation.Annotation;
import java.net.URI;
import java.nio.file.Paths;
import java.util.Objects;

public class DownloadFileExtension implements BeforeAllCallback, AfterAllCallback {

    private static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(DownloadFileExtension.class.getSimpleName());
    private static final String SERVER = "SERVER";

    @Override
    public void beforeAll(ExtensionContext extensionContext) throws Exception {
        DownloadFile downloadFile = getDownloadFileAnnotation(extensionContext);
        if (downloadFile != null) {
            URI uri = Objects.requireNonNull(DownloadFileExtension.class.getClassLoader().getResource(downloadFile.file())).toURI();
            Server server = new Server(downloadFile.port());
            server.add(new FileUploadEndpoint(Paths.get(uri)));
            server.start();
            storeServer(extensionContext, server);
        }
    }

    @Override
    public void afterAll(ExtensionContext extensionContext) {
        DownloadFile downloadFile = getDownloadFileAnnotation(extensionContext);
        if (downloadFile != null) {
            Server server = getServer(extensionContext);
            if (server != null) {
                server.stop();
            }
        }
    }

    private void storeServer(ExtensionContext extensionContext, Server server) {
        extensionContext.getStore(NAMESPACE).put(SERVER, server);
    }

    private Server getServer(ExtensionContext extensionContext) {
        return (Server) extensionContext.getStore(NAMESPACE).get(SERVER);
    }

    private DownloadFile getDownloadFileAnnotation(ExtensionContext extensionContext) {
        return getAnnotation(extensionContext.getRequiredTestClass(), DownloadFile.class);
    }

    private static <T extends Annotation> T getAnnotation(Class<?> aClass, @SuppressWarnings("SameParameterValue") Class<T> annotationClass) {
        T annotation;
        while (aClass != null) {
            annotation = aClass.getAnnotation(annotationClass);
            if (annotation != null) {
                return annotation;
            }
            aClass = aClass.getSuperclass();
        }
        return null;
    }
}
