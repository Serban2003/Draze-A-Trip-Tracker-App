package com.example.triptracker;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;

import org.acra.ACRA;
import org.acra.config.CoreConfiguration;
import org.acra.config.CoreConfigurationBuilder;
import org.acra.config.MailSenderConfigurationBuilder;
import org.acra.config.ToastConfigurationBuilder;
import org.acra.data.StringFormat;
import org.acra.sender.EmailIntentSender;

public class App extends Application {
    public static final String CHANNEL_ID = "LocationTrackingChannel";

    @Override
    public void onCreate() {
        createNotificationChannel();
        super.onCreate();
    }

    //Creates the one channel on which the service notification will be displayed
    private void createNotificationChannel() {
        NotificationChannel serviceChannel = new NotificationChannel(
                CHANNEL_ID,
                "Location Tracking Channel",
                NotificationManager.IMPORTANCE_DEFAULT
        );

        NotificationManager manager = getSystemService(NotificationManager.class);
        manager.createNotificationChannel(serviceChannel);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);

        ACRA.init(this, new CoreConfigurationBuilder()
                //core configuration:
                .withBuildConfigClass(BuildConfig.class)
                .withReportFormat(StringFormat.JSON)
                .withPluginConfigurations(
                        //each plugin you chose above can be configured with its builder like this:
                        new ToastConfigurationBuilder()
                                .withText(getString(R.string.acra_toast_text))
                                .build(),

        new MailSenderConfigurationBuilder()
                //required
                .withMailTo("iustinianserban@gmail.com")
                //defaults to true
                .withReportAsFile(true)
                //defaults to ACRA-report.stacktrace
                .withReportFileName("Crash.txt")
                //defaults to "<applicationId> Crash Report"
                .withSubject(getString(R.string.mail_subject))
                //defaults to empty
                .withBody(getString(R.string.mail_body))
                .build()
                )
        );
    }
}
