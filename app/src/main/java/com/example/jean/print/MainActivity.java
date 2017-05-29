package com.example.jean.print;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintJob;
import android.print.PrintManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.maxxton.printer.PrintDocument;
import com.maxxton.printer.PrintProtocol;
import com.maxxton.printer.Printer;

import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.custom.printer.api.android.CustomAndroidAPI;
import it.custom.printer.api.android.CustomException;
import it.custom.printer.api.android.CustomPrinter;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "LogMainActi";
    @BindView(R.id.webview)
    WebView mWebView;
    InetAddress iAddress;

    private WebView webView;
    private CustomPrinter prnDevice = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        ipPrintService = new ipPrintFile(this, mHandler);
        mConversationArrayAdapter = new ArrayAdapter<String>(this, R.id.remote_device);

        connectToDevice("10.62.10.153");

        new RetrieveFeedTask().execute();
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

            try {
                String[] ethPrin =
                        CustomAndroidAPI.EnumEthernetDevices(5000, getApplicationContext());

//                Log.d("Jean", ethPrin.length + "");
                prnDevice = new CustomAndroidAPI()
//                        .getPrinterDriverETH("10.62.10.153");
                        .getPrinterDriverETH(iAddress.getHostAddress());
                prnDevice.feed(1);
                prnDevice.printText("Test test");
                prnDevice.printText("Сактандыру\t\t\t\t\t Авиа");
                prnDevice.printText("Сдать\t\t\t\t\t");
                prnDevice.printText("Куда Участок МЖД и ПК г.Астана [220081]");
                prnDevice.printText("Куда Участок сортировки письменной [220097]");


                prnDevice.printText("\t\tВес 10.750 кг");
                prnDevice.printText("Пломба № 1987621");

            } catch (CustomException e) {
                e.printStackTrace();
            }

        }
    }


    private void shit() {
        try {


            Printer printer = new Printer("10.62.10.153");
            PrintDocument document = new PrintDocument("document astana Zhangali");
            document.insert("Сактандыру\t\t\t\t\t Авиа");
            document.insert("Сдать\t\t\t\t\t");
            document.insert("Куда Участок МЖД и ПК г.Астана [220081]");
            document.insert("Куда Участок сортировки письменной [220097]");

//            prnDevice.printText("sdfdf");

            document.insert("\t\tВес 10.750 кг");
            document.insert("Пломба № 1987621");


            printer.print(document, PrintProtocol.RAW);


            String[] ethPrin =
                    CustomAndroidAPI.EnumEthernetDevices(5000, getApplicationContext());

//            Log.d("Jean", ethPrin.length + "");

            prnDevice = new CustomAndroidAPI()
                    .getPrinterDriverETH(iAddress.getHostAddress());
            prnDevice.feed(1);
            prnDevice.printText("Test test");
            prnDevice.printText("Сактандыру\t\t\t\t\t Авиа");
            prnDevice.printText("Сдать\t\t\t\t\t");
            prnDevice.printText("Куда Участок МЖД и ПК г.Астана [220081]");
            prnDevice.printText("Куда Участок сортировки письменной [220097]");

            prnDevice.printText("\t\tВес 10.750 кг");
            prnDevice.printText("Пломба № 1987621");


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void connectToDevice(String remote) {
        //String remote = mRemoteDevice.getText().toString();
        if (remote.length() == 0)
            return;
        //disconnect first
/*
        if (ipPrintService.getState() == ipPrintFile.STATE_CONNECTED) {
            ipPrintService.stop();
            setConnectState(ipPrintFile.STATE_DISCONNECTED);
            //return;
        }
*/

        try {
            iAddress = InetAddress.getByName(remote);
        } catch (UnknownHostException e) {
            Toast.makeText(this, "Invalid IP host", Toast.LENGTH_SHORT)
                    .show();
            remote = null;
        }

        if (remote != null) {
            Log.d("Main", "connecting to " + remote);
            ipPrintService.connect(remote);
        } else {
            Log.d("Main", "unknown remote device!");
        }
    }

    ipPrintFile ipPrintService = null;

    void setConnectState(Integer iState) {
        switch (iState) {
            case ipPrintFile.STATE_CONNECTED:
//                updateConnectButton(true);
                break;
            case ipPrintFile.STATE_DISCONNECTED:
//                updateConnectButton(false);
                break;
            case ipPrintFile.STATE_CONNECTING:
                Log.d("Main", "connecting...");
                break;
            case ipPrintFile.STATE_LISTEN:
                Log.d("Main", "listening...");
                break;
            case ipPrintFile.STATE_IDLE:
                Log.d("Main", "state none");
                break;
            default:
                Log.d("Main", "unknown state var " + iState.toString());
        }
    }

    private static final boolean D = true;
    private String mConnectedDeviceName = null;
    private ArrayAdapter<String> mConversationArrayAdapter;

    // The Handler that gets information back from the ipPrintService
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            mConnectedDeviceName = msg.getData().getString(msgTypes.DEVICE_NAME);

            switch (msg.what) {
                case msgTypes.MESSAGE_STATE_CHANGE:
                    Bundle bundle = msg.getData();
                    int status = bundle.getInt("state");
                    if (D)
                        Log.i(TAG, "handleMessage: MESSAGE_STATE_CHANGE: " + msg.arg1);  //arg1 was not used! by ipprintFile
                    setConnectState(msg.arg1);
                    switch (msg.arg1) {
                        case ipPrintFile.STATE_CONNECTED:
                            Log.d("Main", "connected to: " + mConnectedDeviceName);
                            mConversationArrayAdapter.clear();
                            Log.i(TAG, "handleMessage: STATE_CONNECTED: " + mConnectedDeviceName);
                            break;
                        case ipPrintFile.STATE_CONNECTING:
                            Log.d("Main", "connecting...");
                            Log.i(TAG, "handleMessage: STATE_CONNECTING: " + mConnectedDeviceName);
                            break;
                        case ipPrintFile.STATE_LISTEN:
                            Log.d("Main", "connection ready");
                            Log.i(TAG, "handleMessage: STATE_LISTEN");
                            break;
                        case ipPrintFile.STATE_IDLE:
                            Log.d("Main", "STATE_NONE");
                            Log.i(TAG, "handleMessage: STATE_NONE: not connected");
                            break;
                        case ipPrintFile.STATE_DISCONNECTED:
                            Log.d("Main", "disconnected");
                            Log.i(TAG, "handleMessage: STATE_DISCONNECTED");
                            break;
                    }
                    break;
                case msgTypes.MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;
                    // construct a string from the buffer
                    String writeMessage = new String(writeBuf);
                    mConversationArrayAdapter.add("Me:  " + writeMessage);
                    break;
                case msgTypes.MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    mConversationArrayAdapter.add(mConnectedDeviceName + ":  " + readMessage);
                    Log.d("Main", "recv>>>" + readMessage);
                    break;
                case msgTypes.MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    mConnectedDeviceName = msg.getData().getString(msgTypes.DEVICE_NAME);
                    //Toast.makeText(getApplicationContext(), "Connected to " + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
//                    Toast.makeText(MainActivity.this, "", Toast.LENGTH_SHORT).show();(mConnectedDeviceName, "Connected");
                    Log.i(TAG, "handleMessage: CONNECTED TO: " + msg.getData().getString(msgTypes.DEVICE_NAME));
                    //printESCP();
//                    updateConnectButton(false);

                    break;
                case msgTypes.MESSAGE_TOAST:
//                    Toast toast = Toast.makeText(getApplicationContext(), msg.getData().getString(msgTypes.TOAST), Toast.LENGTH_SHORT);//.show();
//                    toast.setGravity(Gravity.CENTER,0,0);
//                    toast.show();
//                    Toast.makeText(MainActivity.this, "", Toast.LENGTH_SHORT).show();(msg.getData().getString(msgTypes.TOAST));
                    Log.i(TAG, "handleMessage: TOAST: " + msg.getData().getString(msgTypes.TOAST));
                    Log.d("Main", msg.getData().getString(msgTypes.TOAST));
                    break;
                case msgTypes.MESSAGE_INFO:
                    Log.d("Main", msg.getData().getString(msgTypes.INFO));
                    //mLog.append(msg.getData().getString(msgTypes.INFO));
                    //mLog.refreshDrawableState();
                    String s = msg.getData().getString(msgTypes.INFO);
                    if (s.length() == 0)
                        s = String.format("int: %i" + msg.getData().getInt(msgTypes.INFO));
                    Log.i(TAG, "handleMessage: INFO: " + s);
                    break;
            }
        }
    };

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
