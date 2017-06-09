package Downloader;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.content.FileProvider;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadAndInstallAPKFile extends AsyncTask<String, Integer, Void> {

    ProgressDialog progressDialog;
    int status = 0;

    private Context context;

    public void setContext(Context context, ProgressDialog progress) {
        this.context = context;
        this.progressDialog = progress;
    }

    public void onPreExecute() {
        progressDialog.show();
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        progressDialog.setProgress(values[0]);
    }

    @Override
    protected Void doInBackground(String... arg0) {
        try {
            URL url = new URL(arg0[0]);
            HttpURLConnection c = (HttpURLConnection) url.openConnection();
            c.setRequestMethod("GET");
            c.setDoOutput(true);
            c.connect();
            int lenghtOfFile = c.getContentLength();
            File sdcard = context.getFilesDir();
            System.out.println(sdcard.getAbsolutePath());
            File myDir = new File(sdcard, "/" + context.getApplicationContext().getPackageName());
            myDir.mkdirs();
            File outputFile = new File(myDir, "update.apk");
            if (outputFile.exists()) {
                outputFile.delete();
            }
            outputFile.createNewFile();
            FileOutputStream fos = new FileOutputStream(outputFile);

            InputStream is = c.getInputStream();

            byte[] buffer = new byte[1024];
            int len1 = 0, total = 0;
            while ((len1 = is.read(buffer)) != -1) {
                fos.write(buffer, 0, len1);
                total += len1;
                publishProgress((int) ((total * 100) / lenghtOfFile));
            }
            fos.flush();
            fos.close();
            is.close();

            Intent intent = new Intent(Intent.ACTION_VIEW);
            Uri photoURI = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", outputFile);
            intent.setDataAndType(photoURI, "application/vnd.android.package-archive");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // without this flag android returned a intent error!
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            context.startActivity(intent);


        } catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace();
            status = 1;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void onPostExecute(Void unused) {
        progressDialog.dismiss();

    }
}