package trinity.play2learn.backend.configs.fileUpload.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;

@Configuration
public class GoogleDriveConfig {

    @Value("${google.drive.credentials.path}")
    private String credentialsPath;

    @Value("${google.drive.tokens.path}")
    private String tokensDirectoryPath;

    @Bean
    public Drive driveService() throws Exception {

        InputStream in = new FileInputStream(credentialsPath);

        GoogleClientSecrets clientSecrets =
                GoogleClientSecrets.load(
                        JacksonFactory.getDefaultInstance(),
                        new InputStreamReader(in)
                );

        GoogleAuthorizationCodeFlow flow =
                new GoogleAuthorizationCodeFlow.Builder(
                        GoogleNetHttpTransport.newTrustedTransport(),
                        JacksonFactory.getDefaultInstance(),
                        clientSecrets,
                        List.of(DriveScopes.DRIVE)
                )
                .setDataStoreFactory(
                        new FileDataStoreFactory(new File(tokensDirectoryPath))
                )
                .setAccessType("offline")
                .build();

        LocalServerReceiver receiver =
                new LocalServerReceiver.Builder().setPort(8888).build();

        Credential credential =
                new AuthorizationCodeInstalledApp(flow, receiver)
                        .authorize("user");

        return new Drive.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                JacksonFactory.getDefaultInstance(),
                credential
        )
        .setApplicationName("Play2Learn")
        .build();
    }
}
