package ru.timurchan.fedata;



import com.google.gson.Gson;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

public class ConnectionV2<ClassType> {
    private final static String TAG = "Connection";

    // ConnectionV2 Exceptions
    static final public int CE_Json = 999;
    static final public int CE_Success = 1000;
    static final public int CE_IOException = 1001;
    static final public int CE_UnknownHostException = 1002;
    static final public int CE_Exception = 1003;

    private int requestId;
    private ConnectionListener mListener;
    private ClassType mData;
    private int mConnectionError = CE_Success;
    private boolean mUseApiRequest = true;

    public interface ConnectionListener {
        <T> void onDataLoaded(final T result, final int request_id, final Type typeOfT, final RequestResult answer);
    }

    //------- Synchronous Type --------------------------------------------------------------------------------------------------------
    public ClassType getData() {
        return mData;
    }

    private RequestResult getCEResult() {
        System.out.println("onDataLoaded: connection error = " + mConnectionError);
        String str = "";
        switch (mConnectionError) {
            case CE_UnknownHostException:
                str = "Проверьте интернет соединение.";
                break;
            case CE_IOException:
                str = "Проверьте интернет соединение.";
                break;
            case CE_Exception:
                str = "Проверьте интернет соединение.";
                break;
        }
        RequestResult answer = new RequestResult(false, mConnectionError, str);
        return answer;
    }

    public RequestResult getApiDataGet(final String myurl, Type typeOfT ) {
        ClassType res = getApiDataGetInternal(myurl, typeOfT);

        if(mConnectionError != CE_Success) {
            return getCEResult();
        }

        if(res == null) {
            System.out.println( "onDataLoaded: result is null for url = " + myurl + " and Type = " + typeOfT.toString());
            RequestResult answer = new RequestResult(false, RequestResult.ET_NULL);
            return answer;
        }

        ApiRequest res0 = new ApiRequest();
        if(mUseApiRequest) {
            res0 = (ApiRequest) res; // это должно сработать всегда - потому что всегда есть от сервера resCode и resMessage
            if (res == null) {
                System.out.println( "onDataLoaded: result is not subclass of ApiRequest for url = " + myurl + " and Type = " + typeOfT.toString());
                RequestResult answer = new RequestResult(false, RequestResult.ET_NULL);
                return answer;
            }
            if (res0.resCode != ApiRequest.API_CODE_OK) {
                System.out.println( "onDataLoaded: error for request = " + requestId + " and Type = " + typeOfT.toString() + ": res_code = " + res0.resCode + " and res_message = " + res0.resMessage);
                RequestResult answer = new RequestResult(false, RequestResult.ET_NOT_SUCCESS, res0.resMessage);
                mData = res;


                return answer;
            }
        }

        mData = res;
        System.out.println( "onDataLoaded: request = " + requestId + " and Type = " + typeOfT.toString() + " is OK");
        RequestResult answer = new RequestResult(true, RequestResult.ET_SUCCESS, mUseApiRequest ? res0.resMessage : "");
        return answer;
    }


    //------- Asynchronous Type -------------------------------------------------------------------------------------------------------

    static public void getApiDataAsync(final String myurl,
                                     final int request_id,
                                     final ConnectionListener listener,
                                     Type typeOfT) {
        ConnectionV2 conn = new ConnectionV2();
        conn.getApiDataAsyncInternal(myurl, request_id, listener, typeOfT);
    }

    // используется для ассинхронного post-завпроса со строковыми параметрами (например при регистрации)
    static public <T> void getApiDataAsync(final String myurl,
                                             Map<String, String> params,
                                             final int request_id,
                                             final ConnectionListener listener,
                                             Type typeOfT) {
        ConnectionV2 conn = new ConnectionV2();
        conn.getApiDataAsyncInternal(myurl, params, request_id, listener, typeOfT);
    }

    // используется для отправки файлов на сервер
    static public <T> void getApiDataAsync(final String myurl,
                                             final String filename,
                                             final int request_id,
                                             final ConnectionListener listener,
                                             Type typeOfT) {
        ConnectionV2 conn = new ConnectionV2();
        conn.getApiDataAsyncInternal(myurl, filename, request_id, listener, typeOfT);
    }

    //-------------------------------------------------------------------------------------------------------------------

    private <T> void onDataLoaded(final T result, final Type typeOfT) {
        if (mListener != null) {
            if(mConnectionError != CE_Success) {
                mListener.onDataLoaded(new ApiRequest(), requestId, typeOfT, getCEResult());
                return;
            }

            if(result == null) {
                System.out.println( "onDataLoaded: result is null for request = " + requestId + " and Type = " + typeOfT.toString());
                RequestResult answer = new RequestResult(false, RequestResult.ET_NULL);
                mListener.onDataLoaded(new ApiRequest(), requestId, typeOfT, answer);
                return;
            }

            ApiRequest res0 = new ApiRequest();
            if(mUseApiRequest) {
                res0 = (ApiRequest) result; // это должно сработать всегда - потому что всегда есть от сервера resCode и resMessage
                if (res0 == null) {
                    System.out.println( "onDataLoaded: result is null for request = " + requestId + " and Type = " + typeOfT.toString());
                    RequestResult answer = new RequestResult(false, RequestResult.ET_NULL);
                    mListener.onDataLoaded(new ApiRequest(), requestId, typeOfT, answer);
                    return;
                }
                ClassType res = (ClassType) res0; // это тоже должно сработать - если программист правильно передал нужный тип и класс в коннектор
                if (res == null) {
                    System.out.println( "onDataLoaded: result is null for request = " + requestId + " and Type = " + typeOfT.toString() + " after type customization");
                    RequestResult answer = new RequestResult(false, RequestResult.ET_NULL);
                    mListener.onDataLoaded(new ApiRequest(), requestId, typeOfT, answer);
                    return;
                }
                if (res0.resCode != ApiRequest.API_CODE_OK) {
                    System.out.println( "onDataLoaded: error for request = " + requestId + " and Type = " + typeOfT.toString() + ": res_code = " + res0.resCode + " and res_message = " + res0.resMessage);
                    RequestResult answer = new RequestResult(false, RequestResult.ET_NOT_SUCCESS, res0.resMessage);
                    mListener.onDataLoaded(res, requestId, typeOfT, answer);
                    return;
                }
            }

            System.out.println( "onDataLoaded: request = " + requestId + " and Type = " + typeOfT.toString() + " is OK");
            RequestResult answer = new RequestResult(true, RequestResult.ET_SUCCESS, mUseApiRequest ? res0.resMessage : "");
            mListener.onDataLoaded(result, requestId, typeOfT, answer);
        }
    }

    public void dontUseApiRequest() {
        mUseApiRequest = false;
    }

    private String readStream(InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader r = new BufferedReader(new InputStreamReader(is),1000);
        for (String line = r.readLine(); line != null; line = r.readLine()){
            sb.append(line);
            sb.append(System.getProperty("line.separator"));
        }
        is.close();
        return sb.toString();
    }

    private <T> T getApiDataGetInternal(final String myurl, Type typeOfT ) {
        T result = null;
        HttpURLConnection connection = null;
//        System.out.println( "Perform API request: " + myurl);
        try {
            URL url = new URL(myurl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setInstanceFollowRedirects(true);

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new IOException("Unexpected HTTP response: "
                        + connection.getResponseCode() + " " + connection.getResponseMessage());
            }

            InputStream in = null;
            in = new BufferedInputStream(connection.getInputStream());
            //Reader reader = new InputStreamReader(in);
            String json_string = readStream(in);
//            System.out.println( "API request: " + myurl + " : " + json_string);
            System.out.println( "API request: " + myurl);

            if(typeOfT == String.class) {
                //String result = readStream(in);
                return (T) json_string;
            }
            else {
//                Gson gson = new GsonBuilder()
//                        .registerTypeAdapter(Boolean.class, booleanAsIntAdapter)
//                        .registerTypeAdapter(boolean.class, booleanAsIntAdapter)
//                        .create();
                Gson gson = new Gson();

                try {
                    //result = gson.fromJson(reader, typeOfT);
                    result = gson.fromJson(json_string, typeOfT);
                } catch (Exception e) {
                    e.printStackTrace();
                    mConnectionError = CE_Json;
                }
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
            mConnectionError = CE_UnknownHostException;
        } catch (IOException e) {
            e.printStackTrace();
            mConnectionError = CE_IOException;
        } catch (Exception e) {
            e.printStackTrace();
            mConnectionError = CE_Exception;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return result;
    }

    // используется для ассинхронного post-завпроса со строковыми параметрами
    private <T> T getApiDataPostInternal(final String targetURL, Type typeOfT,
                                final Map<String, String> params) {
        T result = null;
        System.out.println("Perform API POST request: " + targetURL);
//        System.out.println( params.toString());

        String urlParameters = "";
        int counter = 0;
        for (Map.Entry<String, String> entry: params.entrySet()) {
            if(counter > 0)
                urlParameters += "&";
            urlParameters += entry.getKey() + "=" + FeUrlProvider.encode(entry.getValue());
            //System.out.println( "getApiDataPost params = " + urlParameters);
            counter++;
        }

        URL url;
        HttpURLConnection connection = null;
        try {
            //Create connection
            url = new URL(targetURL);
            connection = (HttpURLConnection)url.openConnection();
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestProperty("Content-Length", "" + Integer.toString(urlParameters.getBytes().length));
            connection.setRequestProperty("Content-Language", "en-US");

            //Send request
            DataOutputStream wr = new DataOutputStream (connection.getOutputStream ());
            wr.writeBytes (urlParameters);
            wr.flush ();
            wr.close ();

            //Get Response
            InputStream is = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            String line;
            StringBuffer response = new StringBuffer();
            while((line = rd.readLine()) != null) {
                response.append(line);
                response.append('\r');
            }
            rd.close();

            String json = response.toString();
//            System.out.println( "json_string for " + targetURL + " : " + json);
//            Gson gson = new GsonBuilder()
//                    .registerTypeAdapter(Boolean.class, booleanAsIntAdapter)
//                    .registerTypeAdapter(boolean.class, booleanAsIntAdapter)
//                    .create();
            Gson gson = new Gson();

            try {
                result = gson.fromJson(json, typeOfT);
            } catch(Exception e) {
                e.printStackTrace();
                mConnectionError = CE_Json;
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
            mConnectionError = CE_UnknownHostException;
        } catch (IOException e) {
            e.printStackTrace();
            mConnectionError = CE_IOException;
        } catch (Exception e) {
            e.printStackTrace();
            mConnectionError = CE_Exception;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }

        return result;
    }

    private <T> T getApiDataPostMultipartInternal(final String targetURL, Type typeOfT, final String fileName) {

        DataOutputStream dos = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        File sourceFile = new File(fileName);

        T result = null;
        System.out.println("Perform API multipart request: " + targetURL);

        URL url;
        HttpURLConnection connection = null;
        try {
            //Create connection
            FileInputStream fileInputStream = new FileInputStream(sourceFile);
            url = new URL(targetURL);
            connection = (HttpURLConnection)url.openConnection();
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("ENCTYPE", "multipart/form-data");
            connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
            connection.setRequestProperty("uploaded_file", fileName);

            //Send request
            dos = new DataOutputStream(connection.getOutputStream());

            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Type: image/jpeg" + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"photo\";filename=\"" + fileName + "\"" + lineEnd);
            dos.writeBytes(lineEnd);

            // create a buffer of  maximum size
            bytesAvailable = fileInputStream.available();

            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            buffer = new byte[bufferSize];

            // read file and write it into form...
            bytesRead = fileInputStream.read(buffer, 0, bufferSize);

            while (bytesRead > 0) {

                dos.write(buffer, 0, bufferSize);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

            }

            // send multipart form data necesssary after file data...
            dos.writeBytes(lineEnd);
            dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

            //Get Response
            InputStream is = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            String line;
            StringBuffer response = new StringBuffer();
            while((line = rd.readLine()) != null) {
                response.append(line);
                response.append('\r');
            }
            rd.close();

            String json = response.toString();
            Gson gson = new Gson();
//            Gson gson = new GsonBuilder()
//                    .registerTypeAdapter(Boolean.class, booleanAsIntAdapter)
//                    .registerTypeAdapter(boolean.class, booleanAsIntAdapter)
//                    .create();

            try {
                result = gson.fromJson(json, typeOfT);
            } catch(Exception e) {
                e.printStackTrace();
                mConnectionError = CE_Json;
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
            mConnectionError = CE_UnknownHostException;
        } catch (IOException e) {
            e.printStackTrace();
            mConnectionError = CE_IOException;
        } catch (Exception e) {
            e.printStackTrace();
            mConnectionError = CE_Exception;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }

        return result;
    }

    protected class ApiDataLoader<T> extends Thread {
        String mUrl;
        Type mType;
        Map<String, String> mParams = new HashMap<>();
        String mFilename;

        public ApiDataLoader(final String url, final Type typeOfT) {
            mUrl = url;
            mType = typeOfT;
        }

        public ApiDataLoader(final String url, final Type typeOfT, Map<String, String> params) {
            mUrl = url;
            mType = typeOfT;
            mParams = params;
        }

        public ApiDataLoader(final String url, final Type typeOfT, String filename) {
            mUrl = url;
            mType = typeOfT;
            mFilename = filename;
        }

        public void run(){
            T result = null;
            if(mFilename != null) {
                result = getApiDataPostMultipartInternal(mUrl, mType, mFilename);
            } else if(mParams == null || mParams.isEmpty()) {
                result = getApiDataGetInternal(mUrl, mType);
            } else {
                result = getApiDataPostInternal(mUrl, mType, mParams);
            }
            onDataLoaded(result, mType);
        }
    }


    private <T> void getApiDataAsyncInternal(final String myurl,
                                    final int request_id,
                                    final ConnectionListener listener,
                                    Type typeOfT) {
        if(listener != null) {
            mListener = listener;
            requestId = request_id;
            ApiDataLoader<T> loader = new ApiDataLoader<>(myurl, typeOfT);
            loader.start();
        } else {
            System.out.println( "internal error: listener is null");
        }
    }

    // используется для ассинхронного post-завпроса со строковыми параметрами (например при регистрации)
    private <T> void getApiDataAsyncInternal(final String myurl,
                                    Map<String, String> params,
                                    final int request_id,
                                    final ConnectionListener listener,
                                    Type typeOfT) {
        if(listener != null) {
            mListener = listener;
            requestId = request_id;
            ApiDataLoader<T> loader = new ApiDataLoader<>(myurl, typeOfT, params);
            loader.start();
        } else {
            System.out.println( "internal error: listener is null");
        }
    }

    // используется для отправки файлов на сервер
    private <T> void getApiDataAsyncInternal(final String myurl,
                                    final String filename,
                                    final int request_id,
                                    final ConnectionListener listener,
                                    Type typeOfT) {
        if(listener != null) {
            mListener = listener;
            requestId = request_id;
            ApiDataLoader<T> loader = new ApiDataLoader<>(myurl, typeOfT, filename);
            loader.start();
        } else {
            System.out.println( "internal error: listener is null");
        }
    }

//    private static final TypeAdapter<Boolean> booleanAsIntAdapter = new TypeAdapter<Boolean>() {
//        @Override public void write(JsonWriter out, Boolean value) throws IOException {
//            if (value == null) {
//                out.nullValue();
//            } else {
//                out.value(value);
//            }
//        }
//        @Override public Boolean read(JsonReader in) throws IOException {
//            JsonToken peek = in.peek();
//            switch (peek) {
//                case BOOLEAN:
//                    return in.nextBoolean();
//                case NULL:
//                    in.nextNull();
//                    return null;
//                case NUMBER:
//                    return in.nextInt() != 0;
//                case STRING:
//                    return Boolean.parseBoolean(in.nextString());
//                default:
//                    throw new IllegalStateException("Expected BOOLEAN or NUMBER but was " + peek);
//            }
//        }
//    };
}
