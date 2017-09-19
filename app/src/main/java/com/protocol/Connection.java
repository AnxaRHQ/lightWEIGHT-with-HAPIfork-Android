package com.protocol;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.UUID;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.http.Header;
import org.apache.http.HeaderIterator;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.ProtocolVersion;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;

import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;

import com.controllers.ApplicationEx;
import com.obj.PhotoDownloadObj;
import com.protocol.WebServices.SERVICES;
import com.protocol.xml.handlers.DefaultResponseHandler;
import com.protocol.xml.handlers.ForkSyncResponsehandler;
import com.protocol.xml.handlers.LoginResponseHandler;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;


public class Connection implements Runnable {

    private ArrayList<NameValuePair> cookies;
    private ArrayList<NameValuePair> params;
    private ArrayList<NameValuePair> headers;


    PhotoDownloadObj photoObj;

    public static final int GET = 0;
    public static final int POST = 1;
    public static final int BITMAP = 2;

    public static final int REQUEST_START = 0;
    public static final int REQUEST_ERROR = 1;
    public static final int REQUEST_SUCCESS = 2;

    private static final int TIME_OUT_SOCKET = 15000;
    private static final int TIME_OUT_CONNECTION = 15000;

    public final static int DEFAULT_EXCEPTION_CONNECTION = 404;

    private Handler handler;

    private int method;
    private String url;
    private String data;
    private boolean multipost = false;

    private DefaultResponseHandler xmlHandler;

    private HttpClient httpClient;
    WebServices webservice;

    public final static String OFFLINE_STR = "<mobileuser xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"><api_response><message>Failed</message><message_detail>Koneksi internet anda tidak stabil/terputus. HAPIfork tidak dapat tersambung saat ini</message_detail><error_count>1</error_count></api_response><user><user_id>0</user_id><height>0</height><start_weight>0</start_weight><current_weight>0</current_weight><target_weight>0</target_weight><has_hapitrack>false</has_hapitrack><has_hapifork>false</has_hapifork><has_hapiwatch>false</has_hapiwatch><user_login><approved>false</approved><lockedout>false</lockedout></user_login><user_profile><num_friends>0</num_friends><num_activities>0</num_activities><num_photos>0</num_photos><height xsi:nil=\"true\" /><start_weight xsi:nil=\"true\" /><current_weight xsi:nil=\"true\" /><target_weight xsi:nil=\"true\" /><offset_utc xsi:nil=\"true\" /><isdaylightsaving xsi:nil=\"true\" /><deactivate_date xsi:nil=\"true\" /></user_profile><hapi_fork /></user></mobileuser>";


    String twohypens = "--";
    String lineend = "\r\n";
    String boundary = "12345";
    Bitmap bmp;
    Context textContext;

    public Connection() {
        cookies = new ArrayList<NameValuePair>();
        params = new ArrayList<NameValuePair>();
        headers = new ArrayList<NameValuePair>();
        webservice = new WebServices();
        handler = new Handler();
    }

    public Connection(Handler _handler) {
        cookies = new ArrayList<NameValuePair>();
        params = new ArrayList<NameValuePair>();
        headers = new ArrayList<NameValuePair>();
        handler = _handler;
        webservice = new WebServices();
    }

    public void addParam(String name, String value) {
        params.add(new BasicNameValuePair(name, value));
    }

    public void addHeader(String name, String value) {
        headers.add(new BasicNameValuePair(name, value));
    }

    public void addCookie(String name, String value) {
        cookies.add(new BasicNameValuePair(name, value));
    }

    public void create(int method, String url, String data, DefaultResponseHandler xmlHandler) {
        this.method = method;
        this.url = url;
        this.data = data;
        this.xmlHandler = xmlHandler;
        ConnectionManager.getInstance().push(this);
    }

    public void create(int method, String url, String data) {
        this.method = method;
        this.url = url;
        this.data = data;
        ConnectionManager.getInstance().push(this);
    }

    public void get(String url) {
        create(GET, url, null);
    }

    public void post(String url, String data) {
        create(POST, url, data);
    }

    public void post(String url) {
        create(POST, url, null);
    }


    public void post(String url, DefaultResponseHandler xmlHandler) {
        create(POST, url, null, xmlHandler);
    }

    public void post(String url, String data, DefaultResponseHandler xmlHandler) {

        create(POST, url, data, xmlHandler);
    }

    public void get(String url, DefaultResponseHandler xmlHandler) {
        create(GET, url, null, xmlHandler);
    }

    public void bitmap(String url) {
        create(BITMAP, url, null);

    }


    @Override
    public void run() {

        int serverResponseCode = 0;
        String serverResponseMessage = "";
        String resultStr = "";


        if (url == null || "".equals(url)) {
            ConnectionManager.getInstance().didComplete(this);
            handler.sendMessage(Message.obtain(handler, REQUEST_ERROR, "The url is invalid"));
            return;
        }
        try {
            handler.sendMessage(Message.obtain(handler, REQUEST_START));

			/* Basic http params */
            HttpParams httpParams = new BasicHttpParams();

            ConnManagerParams.setTimeout(httpParams, TIME_OUT_SOCKET);

            HttpConnectionParams.setConnectionTimeout(httpParams, TIME_OUT_CONNECTION);
            HttpConnectionParams.setSoTimeout(httpParams, TIME_OUT_SOCKET);

            HttpProtocolParams.setContentCharset(httpParams, "utf-8");
            HttpConnectionParams.setTcpNoDelay(httpParams, true);
            HttpConnectionParams.setSocketBufferSize(httpParams, 8192);
            HttpProtocolParams.setVersion(httpParams, HttpVersion.HTTP_1_1);

			/* Register schemes, HTTP and HTTPS */
            SchemeRegistry registry = new SchemeRegistry();
            registry.register(new Scheme("http", new PlainSocketFactory(), 80));

			/* Make a thread safe connection manager for the client */
            ThreadSafeClientConnManager manager = new ThreadSafeClientConnManager(httpParams, registry);
            httpClient = new DefaultHttpClient(manager, httpParams);

            // Create local HTTP context
            HttpContext localContext = new BasicHttpContext();

            HttpResponse response = null;

            switch (method) {
                case GET:
                    String query = formQueryString();

                    HttpGet httpGet = new HttpGet(url + query);

                    Log.i("INFO", "URL:" + url + query);

				/*if you need to set cookie parameters this has to be repeated using cookie name value pair*/
                    for (NameValuePair h : headers) {
                        Log.i("INFO", "Header:" + h.getName() + "::" + h.getValue());
                        httpGet.setHeader(h.getName(), h.getValue());
                    }
                    response = httpClient.execute(httpGet, localContext);

                    break;

                case POST:
                    String quer1y = formQueryString();
                    url = url + "&" + quer1y;

                    Log.i("INFO", "URL:" + url);

                    HttpPost httpPost = new HttpPost(url);
                    for (NameValuePair h : headers) {
                        Log.i("INFO", "Header:" + h.getName() + "::" + h.getValue());
                        httpPost.setHeader(h.getName(), h.getValue());
                    }
                    if (!params.isEmpty()) {
                        Log.i("INFO", "Params:" + params);
                        httpPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
                    }

                    if (data != null) {
                        Log.i("INFO", "Data:" + data);
                        httpPost.setEntity(new StringEntity(data, HTTP.UTF_8));
                    }

                    httpPost.getAllHeaders();
                    httpPost.getMethod();
                    httpPost.getParams();

                    response = httpClient.execute(httpPost);
                    break;
            }

			/* If there is an xml handler, parse the response else return the string back */
            if (xmlHandler != null) {
                if (multipost) {
                    processXMLResponse(resultStr, serverResponseCode, serverResponseMessage);
                } else {
                    processXMLResponse(response);
                }
            } else if (method != BITMAP) {
                processEntity(response);
            }

        } catch (Exception e) {
            Log.i("Exception", "@run2: " + e.getMessage() + xmlHandler + " " + url);
            handler.sendMessage(Message.obtain(handler, REQUEST_ERROR, "There is a service Error @ " + url));

           try{
               resultStr = OFFLINE_STR;
               serverResponseCode = 404;
               serverResponseMessage = "Koneksi internet anda tidak stabil/terputus. HAPIfork tidak dapat tersambung saat ini";
               processXMLResponse(resultStr, serverResponseCode, serverResponseMessage);
           }catch (Exception ex){
               ex.printStackTrace();
           }

            e.printStackTrace();
        } finally {
            try {
                httpClient.getConnectionManager().closeExpiredConnections();
                httpClient.getConnectionManager().shutdown();
                ConnectionManager.getInstance().didComplete(this);
            } catch (Exception ex) {
                Log.i("Exception", "@run1: " + ex.getMessage() + xmlHandler);
                httpClient = null;
            }

        }

    }

    /*for GET method*/
    private String formQueryString() {
        try {
            String combinedParams = "";
            if (!params.isEmpty()) {
                // combinedParams += "?";
                for (NameValuePair p : params) {
                    //  String paramString = p.getName() + "=" + URLEncoder.encode(p.getValue(), "UTF-8");
                    String paramString = p.getName() + "=" + p.getValue();

                    if (combinedParams.length() > 1) {
                        combinedParams += "&" + paramString;
                    } else {
                        combinedParams += paramString;
                    }
                }
            }
            return combinedParams;
        } catch (Exception e) {
            Log.i("Exception", "@formQueryString: " + e.getMessage());
            httpClient = null;
            return "";
        }
    }

    private void processXMLResponse(String result, int statusCode, String statusMessage) throws Exception {
        System.out.println("RESULT +" + result);
        switch (statusCode) {
            case HttpStatus.SC_OK:
            /* Parse the response XML Message */
                xmlHandler.setResultCode(String.valueOf(HttpStatus.SC_OK));

                xmlHandler.setResultCode(String.valueOf(statusCode));
                xmlHandler.setResultMessage(statusMessage);

                xmlHandler.setResponseObj(result);


                SAXParserFactory factory = SAXParserFactory.newInstance();
                SAXParser parser = factory.newSAXParser();
                ByteArrayInputStream bin = new ByteArrayInputStream(result.getBytes());
                parser.parse(bin, xmlHandler);
                break;
            default:
                xmlHandler.setResultCode(String.valueOf(statusCode));
                xmlHandler.setResultMessage(statusMessage);
                xmlHandler.setResponseObj(result);
                factory = SAXParserFactory.newInstance();
                parser = factory.newSAXParser();
                bin = new ByteArrayInputStream(result.getBytes());
                parser.parse(bin, xmlHandler);
                break;
        }

    }

    private void processEntity(HttpResponse response) throws Exception {
        int statusCode = response.getStatusLine().getStatusCode();

        String statusMessage = response.getStatusLine().getReasonPhrase();
        Log.i("Exception: processEntity@Connection ", "statusCode::" + statusCode + "::statusMessage::" + statusMessage);
        HttpEntity entity = response.getEntity();

        BufferedReader br = new BufferedReader(new InputStreamReader(entity.getContent()));
        String line, result = "";
        while ((line = br.readLine()) != null)
            result += line;
        if (handler != null) {
            Message message = Message.obtain(handler, REQUEST_SUCCESS, result);
            handler.sendMessage(message);
        }

        if (xmlHandler != null) {
            xmlHandler.setResponseObj(result);

        }
    }

    private void processXMLResponse(HttpResponse response) throws Exception {

        try {
            int statusCode = response.getStatusLine().getStatusCode();

            String statusMessage = response.getStatusLine().getReasonPhrase();

            HttpEntity entity = response.getEntity();

            BufferedReader br = new BufferedReader(new InputStreamReader(entity.getContent(), HTTP.UTF_8));


            String line, result = "";

            while ((line = br.readLine()) != null)
                result += line;
            System.out.println("RESULT +" + result);


            switch (statusCode) {
                case HttpStatus.SC_OK:
            /* Parse the response XML Message */
                    xmlHandler.setResultCode(String.valueOf(HttpStatus.SC_OK));
                    SAXParserFactory factory = SAXParserFactory.newInstance();
                    SAXParser parser = factory.newSAXParser();
                    ByteArrayInputStream bin = new ByteArrayInputStream(result.getBytes());
                    parser.parse(bin, xmlHandler);
                    break;
                default:
                    Log.i("Exception", "HTTPStatus" + statusCode);
                    xmlHandler.setResultCode(String.valueOf(statusCode));
                    xmlHandler.setResultMessage(statusMessage);
                    xmlHandler.setResponseObj(result);
                    factory = SAXParserFactory.newInstance();
                    parser = factory.newSAXParser();
                    bin = new ByteArrayInputStream(result.getBytes());
                    parser.parse(bin, xmlHandler);

                    break;
            }

        } catch (NullPointerException e) {
            Log.i("Exception", "HTTPStatus exception");
        }
    }


    /* http://api.hapilabs.com/beta/product/common/v1/post?cmd=send_connection&device_id=2412113Q5&cr_si=de8f97bacb8072a464ee4dd8da551409f60623f6*/

    public void send_connection(String device_id, Handler responseHandler, String data) {

        String url = webservice.getURL(SERVICES.SEND_CONNECT);
        xmlHandler = new ForkSyncResponsehandler(responseHandler);
        addParam("device_id", device_id);
        String commands = (webservice.getCommand(SERVICES.SEND_CONNECT) + device_id);
        addParam("cr_si", createSignature(commands, ApplicationEx.getInstance().sharedKey_deviceInfo));

        addHeader("Content-Type", "application/xml");
        addHeader("charset", "utf-8");
        addHeader("Accept", "application/xml");
        create(Connection.POST, url, data);
    }

    //http://api.hapilabs.com/product/hapifork/v2/get?cmd=get_device_id_status&device_id=24121LE1C&cr_si=14e142fef8ddb5046f5188aa4f3b7543b4685bad
    public void get_device_id_status(String device_id, Handler responseHandler, String data) {

        String url = webservice.getURL(SERVICES.GET_DEVICE_ID_STATUS);
        xmlHandler = new ForkSyncResponsehandler(responseHandler);
        addParam("device_id", device_id);
        String commands = (webservice.getCommand(SERVICES.GET_DEVICE_ID_STATUS) + device_id);
        addParam("cr_si", createSignature(commands, ApplicationEx.getInstance().sharedKey_sendData));
        addHeader("Content-Type", "application/xml");
        addHeader("charset", "utf-8");
        addHeader("Accept", "application/xml");

        System.out.println("get_device_id_status URL: " + url);
        create(Connection.GET, url, data);
    }

    //http://api.hapilabs.com/product/hapifork/v2/get?cmd=get_device_id_status&device_id=24121LE1C&cr_si=14e142fef8ddb5046f5188aa4f3b7543b4685bad
    public void get_device_id_profile(String device_id, Handler responseHandler, String data) {

        String url = webservice.getURL(SERVICES.GET_DEVICE_PROFILE);
        xmlHandler = new ForkSyncResponsehandler(responseHandler);
        addParam("device_id", device_id);
        String commands = (webservice.getCommand(SERVICES.GET_DEVICE_PROFILE) + device_id);
        addParam("cr_si", createSignature(commands, ApplicationEx.getInstance().sharedKey_sendData));
        addHeader("Content-Type", "application/xml");
        addHeader("charset", "utf-8");
        addHeader("Accept", "application/xml");

        System.out.println("GET_DEVICE_PROFILE URL: " + url);
        create(Connection.GET, url, data);
    }

    /*transactionURL = http://api.hapilabs.com/beta/product/hapifork/v1/post?cmd=send_data&device_id=2412113Q5&cr_si=ab829f50f176c771a97f3a7f487a40b2743769bc
     */
    public void sendData(String device_id, Handler responseHandler, String data) {
        String url = webservice.getURL(SERVICES.SEND_DATA);
        xmlHandler = new ForkSyncResponsehandler(responseHandler);
        addParam("device_id", device_id);

        String commands = (webservice.getCommand(SERVICES.SEND_DATA) + device_id);
        addParam("cr_si", createSignature(commands, ApplicationEx.getInstance().sharedKey_sendData));

        addHeader("Content-Type", "application/xml");
        addHeader("charset", "utf-8");
        addHeader("Accept", "application/xml");
        create(Connection.POST, url, data);
    }

    public void send_account_link(String device_id, Handler responseHandler, String data) {
        String url = webservice.getURL(SERVICES.SEND_ACCOUNT_LINK);
        xmlHandler = new ForkSyncResponsehandler(responseHandler);
        addParam("device_id", device_id);

        String commands = (webservice.getCommand(SERVICES.SEND_ACCOUNT_LINK) + device_id);
        addParam("cr_si", createSignature(commands, ApplicationEx.getInstance().sharedKey_deviceInfo));

        addHeader("Content-Type", "application/xml");
        addHeader("charset", "utf-8");
        addHeader("Accept", "application/xml");
        create(Connection.POST, url, data);
        System.out.println("SEND_ACCOUNT_LINK URL: " + url);

    }

    public void send_account_unlink(String device_id, Handler responseHandler, String data) {
        String url = webservice.getURL(SERVICES.SEND_ACCOUNT_UNLINK);
        xmlHandler = new ForkSyncResponsehandler(responseHandler);
        addParam("device_id", device_id);

        String commands = (webservice.getCommand(SERVICES.SEND_ACCOUNT_UNLINK) + device_id);
        addParam("cr_si", createSignature(commands, ApplicationEx.getInstance().sharedKey_deviceInfo));

        addHeader("Content-Type", "application/xml");
        addHeader("charset", "utf-8");
        addHeader("Accept", "application/xml");
        create(Connection.POST, url, data);
        System.out.println("SEND_ACCOUNT_UNLINK URL: " + url);

    }

    public void send_connection_new(String device_id, Handler responseHandler, String data) {
        String url = webservice.getURL(SERVICES.SEND_CONNECTION);
        xmlHandler = new ForkSyncResponsehandler(responseHandler);
        addParam("device_id", device_id);

        String commands = (webservice.getCommand(SERVICES.SEND_CONNECTION) + device_id);
        addParam("cr_si", createSignature(commands, ApplicationEx.getInstance().sharedKey_deviceInfo));

        addHeader("Content-Type", "application/xml");
        addHeader("charset", "utf-8");
        addHeader("Accept", "application/xml");
        create(Connection.POST, url, data);
        System.out.println("SEND_CONNECTION URL: " + url);

    }

    public void send_disconnection_new(String device_id, Handler responseHandler, String data) {
        String url = webservice.getURL(SERVICES.SEND_DISCONNECTION);
        xmlHandler = new ForkSyncResponsehandler(responseHandler);
        addParam("device_id", device_id);

        String commands = (webservice.getCommand(SERVICES.SEND_DISCONNECTION) + device_id);
        addParam("cr_si", createSignature(commands, ApplicationEx.getInstance().sharedKey_deviceInfo));

        addHeader("Content-Type", "application/xml");
        addHeader("charset", "utf-8");
        addHeader("Accept", "application/xml");
        create(Connection.POST, url, data);
        System.out.println("SEND_DISCONNECTION URL: " + url);

    }

    private String createSignature(String input, String sharedKey) {
        String signature = "";

        String hashInput = input + sharedKey;

        System.out.println("createSignature hashInput: " + hashInput);

        try {
            signature = ApplicationEx.SHA1(hashInput);
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            //TODO: call error display UI here
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            //TODO: call error display UI here
        }
        return signature;


    }//where data = xml string format post data

    public void loginServices(String username, String password, String data, Handler responseHandler) {

        String url = webservice.getURL(SERVICES.LOGIN);
        xmlHandler = new LoginResponseHandler(responseHandler);
        addParam("signature", createSignature((webservice.getCommand(SERVICES.LOGIN) + username), ApplicationEx.getInstance().sharedKey));

        if (username.contains("+")) {
            String currentString = username;
            String[] separated = currentString.split("@");

            try {
                separated[0] = URLEncoder.encode(separated[0], "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            username = separated[0].concat("@" + separated[1]);
        }
        addParam("username", username);

        addHeader("Content-Type", "application/xml");
        addHeader("charset", "utf-8");
        addHeader("Accept", "application/xml");
        create(Connection.POST, url, data);
    }

    public void getBitmap(String url, String clientid, String photoid) {
        photoObj = new PhotoDownloadObj();
        photoObj.clientid = clientid;
        photoObj.photoid = photoid;
        bitmap(url);

    }

    File saveImage(Bitmap myBitmap, Context context) {

        File myDir = new File(Environment.getExternalStorageDirectory(), context.getPackageName());
        if (!myDir.exists()) {
            myDir.mkdir();
        }

        UUID uniqueKey = UUID.randomUUID();

        String fname = uniqueKey.toString() + "UploadedImage.png";
        File file = new File(myDir, fname);
        if (file.exists()) file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            myBitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }

}
