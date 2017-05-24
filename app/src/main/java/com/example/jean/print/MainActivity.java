package com.example.jean.print;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintJob;
import android.print.PrintManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.maxxton.printer.PrintDocument;
import com.maxxton.printer.PrintException;
import com.maxxton.printer.PrintFormatException;
import com.maxxton.printer.PrintProtocol;
import com.maxxton.printer.Printer;

import java.io.IOException;
import java.net.UnknownHostException;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "LogMainActi";
    @BindView(R.id.webview)
    WebView mWebView;

    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

//        doWebViewPrint();

        try{

            new RetrieveFeedTask().execute();
        }catch (Exception e){
            write(e.getMessage());
        }
    }
    
    private void write(String msg){
        runOnUiThread(() -> {
            Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
        });

        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    class RetrieveFeedTask extends AsyncTask<String, Void, Void> {

        private Exception exception;

        protected Void doInBackground(String... urls) {
            try {
                shit();
            } catch (Exception e) {
                this.exception = e;


                return null;
            }

            return null;
        }

        protected void onPostExecute(Void feed) {
            // TODO: check this.exception
            // TODO: do something with the feed

            if (this.exception != null){

                runOnUiThread(() -> {
                    Toast.makeText(MainActivity.this, exception.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        }
    }


    private void shit() {
        try {

            Printer printer = new Printer("192.168.204.16");
            PrintDocument document = new PrintDocument("document astana Zhangali");
            document.insert("Hello kazpost");

            printer.print(document, PrintProtocol.RAW);

/*
            Socket sock = new Socket("192.168.204.16", 9100);
            PrintWriter oStream = new PrintWriter(sock.getOutputStream());
            oStream.println("HI,test from Android Device");
            Log.d("He", "here");
            oStream.println("\n\n\n\f");
            oStream.close();
            sock.close();
*/
        } catch (Exception e) {


            write(e.getMessage());
            e.printStackTrace();
        }
    }


    private void doWebViewPrint() {
        // Create a WebView object specifically for printing
        webView = new WebView(this);
        webView.setWebViewClient(new WebViewClient() {

            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                Log.i(TAG, "page finished loading " + url);
                createWebPrintJob(view);
                webView = null;
            }
        });

        // Generate an HTML document on the fly:
        String htmlDocument = "<html><body><h1>Test Content</h1><p>Testing, " +
                "testing, testing...</p></body></html>";
        webView.loadDataWithBaseURL(null, htmlDocument, "text/HTML", "UTF-8", null);

        // Keep a reference to WebView object until you pass the PrintDocumentAdapter
        // to the PrintManager
        webView = webView;
    }

    private void createWebPrintJob(WebView webView) {

        // Get a PrintManager instance
        PrintManager printManager = (PrintManager) this
                .getSystemService(Context.PRINT_SERVICE);

        // Get a print adapter instance
        PrintDocumentAdapter printAdapter = webView.createPrintDocumentAdapter();

        // Create a print job with name and adapter instance
        String jobName = getString(R.string.app_name) + " Document";
        PrintJob printJob = printManager.print(jobName, printAdapter,
                new PrintAttributes.Builder().build());

        printManager.print("String", printAdapter, null);
        // Save the job object for later status checking
//        mPrintJobs.add(printJob);
    }
}
