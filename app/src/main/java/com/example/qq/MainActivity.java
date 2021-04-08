package com.example.qq;

import android.annotation.SuppressLint;
import android.content.Context;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.GeolocationPermissions;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;


public class MainActivity extends AppCompatActivity {

    // JavaScriptInterface
    // â»JavaScriptãããã®ã¯ã©ã¹ã®ã¡ã½ãããå¼ã³åºã
    private class JavaScriptInterface {

        //private Context c;
        //public JavaScriptInterface(Context c){
        //    this.c = c;
        //}
        @JavascriptInterface
        public void MyMethod(String str) {
            Toast toast = Toast.makeText(getApplicationContext(), str, Toast.LENGTH_LONG);
            toast.show();
            Log.i("MyTAG","XX" + str);
        }
    }

    // ãªã½ã¼ã¹ãããã¡ã¤ã«ãçæãã(/data/data/ããã±ã¼ã¸å/files/)
    public boolean setRawResources(Context context , int resourcesID, String fileName){
        boolean result = false;

        // ãªã½ã¼ã¹ã®èª­ã¿è¾¼ã¿
        InputStream is =  context.getResources().openRawResource(resourcesID);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte [] buffer = new byte[1024];
        try{
            // 1024ãã¤ãæ¯ããã¡ã¤ã«ãèª­ã¿è¾¼ã
            while(true) {
                int len = is.read(buffer);
                if(len < 0)  break;
                baos.write(buffer, 0, len);
            }
        }catch (Exception e){
            e.printStackTrace();
            return result;
        }

        // ãã¡ã¤ã«ã®çæ
        File file = new File(context.getFilesDir() + "/" + fileName);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            fos.write(baos.toByteArray());
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(fos != null){
                try{
                    fos.close();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
        return result;
    }
    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final WebView webView = findViewById(R.id.webView);

        // ãªã½ã¼ã¹ãããã¡ã¤ã«ãçæãã(/data/data/ããã±ã¼ã¸å/files/ã«ä½æ)
        setRawResources(this, R.raw.index, "index.html");
        setRawResources(this, R.raw.jquery, "jquery.js");

        // ã­ã£ãã·ã¥ã¯ãªã¢
        // â»éçºæã®ã¿æå¹ã«ãã
        webView.clearCache(true);

        // JavaScriptãæå¹ã«ãã
        webView.getSettings().setJavaScriptEnabled(true);

        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(false);
        // HTML5 API flags
        webView.getSettings().setGeolocationEnabled(true);
        webView.getSettings().setAppCacheEnabled(true);
        webView.getSettings().setDatabaseEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        // WebChromeClientãè¨­å®ãã
        // â»ã³ã¬ãè¨­å®ããªãã¨JSã®alertã¯è¡¨ç¤ºãããªã
        //webView.setWebChromeClient(new WebChromeClient());
        webView.setWebChromeClient(new WebChromeClient() {
            public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
                callback.invoke(origin, true, false);
            }
        });

        ////webView.getSettings().setGeolocationDatabasePath( context.getFilesDir().getPath() );

        // JavaScriptInterfaceã®åæè¨­å®
        webView.addJavascriptInterface(new JavaScriptInterface(), "android");

        // ãã¡ã¤ã«ãèª­ã¿è¾¼ã
        webView.loadUrl("file:///" + MainActivity.this.getFilesDir() + "/index.html");

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WebView webView = findViewById(R.id.webView);

                // JavaããJavaScriptãå®è¡ãã
                // â»ãjavascript:ãã®å¾ã«JavaScriptãè¨è¿°ãã
                webView.loadUrl("javascript:" +
                        "MsgBox('showme');");

            }
        });

    }

}
