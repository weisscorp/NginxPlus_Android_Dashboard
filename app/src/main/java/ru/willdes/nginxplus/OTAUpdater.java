package ru.willdes.nginxplus;

import android.app.IntentService;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;

import java.io.File;

public class OTAUpdater extends IntentService {


    public OTAUpdater(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        String pathToApk = "update.apk";
        Uri uri = Uri.fromFile(new File(pathToApk));
        intent.setDataAndType(uri, "application/vnd.android.package-archive");
        startActivity(intent);
    }

    public checkVersion() {
        this();

    }
}
