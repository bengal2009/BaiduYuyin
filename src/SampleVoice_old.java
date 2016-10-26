
import org.json.JSONObject;

import javax.xml.bind.DatatypeConverter;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;

public class SampleVoice_old {

    private static final String serverURL = "http://vop.baidu.com/server_api";
    private static String token = "";
    private static final String testFileName = "test.wav";
    //put your own params here
    private static final String apiKey ="bd6kSzmqtlUaG1SEjbqR4R28";
    private static final String secretKey ="5eaad29500bcbd35c84bf6bfac5e9190";
    private static final String cuid ="6131442";

    public static void main(String[] args) throws Exception {
        String command="arecord -q -D plughw:1,0 -c1  -r 8000 --duration=3 -f S16_LE  test.wav";
        System.out.println("Start Recording.....");

        String output = executeCommand(command);
        System.out.println(output);
        System.out.println("Start Recongnize");
        getToken();
        method1();
//        method3();
//        method2();
    }
    private static String executeCommand(String command) {

        StringBuffer output = new StringBuffer();

        Process p;
        try {
            p = Runtime.getRuntime().exec(command);
            p.waitFor();
            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(p.getInputStream()));

            String line = "";
            while ((line = reader.readLine())!= null) {
                output.append(line + "\n");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return output.toString();

    }

    private static void getToken() throws Exception {
        String getTokenURL = "https://openapi.baidu.com/oauth/2.0/token?grant_type=client_credentials" +
                "&client_id=" + apiKey + "&client_secret=" + secretKey;
        HttpURLConnection conn = (HttpURLConnection) new URL(getTokenURL).openConnection();
        token = new JSONObject(printResponse(conn)).getString("access_token");
    }

    private static void method1() throws Exception {
        File pcmFile = new File("test.wav");
        HttpURLConnection conn = (HttpURLConnection) new URL(serverURL).openConnection();
        System.out.println("Method 1");
        // construct params
        JSONObject params = new JSONObject();
        params.put("format", "wav");
        params.put("rate", 8000);
        params.put("channel", "1");
        params.put("token", token);
        params.put("cuid", cuid);
        params.put("len", pcmFile.length());
        params.put("speech", DatatypeConverter.printBase64Binary(loadFile(pcmFile)));

        // add request header
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");

        conn.setDoInput(true);
        conn.setDoOutput(true);

        // send request
        DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
        wr.writeBytes(params.toString());
        wr.flush();
        wr.close();

       printResponse(conn);
    }
    private static void method3() throws Exception {
        File pcmFile = new File("test.pcm");
        HttpURLConnection conn = (HttpURLConnection) new URL(serverURL).openConnection();
        System.out.println("Method 3");
        // construct params
        JSONObject params = new JSONObject();
        params.put("format", "wav");
        params.put("rate", 8000);
        params.put("channel", "1");
        params.put("token", token);
        params.put("cuid", cuid);
        params.put("len", pcmFile.length());
        params.put("speech", DatatypeConverter.printBase64Binary(loadFile(pcmFile)));

        // add request header
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");

        conn.setDoInput(true);
        conn.setDoOutput(true);

        // send request
        DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
        wr.writeBytes(params.toString());
        wr.flush();
        wr.close();

        printResponse(conn);
    }

    private static void method2() throws Exception {
        File pcmFile = new File(testFileName);
        HttpURLConnection conn = (HttpURLConnection) new URL(serverURL
                + "?cuid=" + cuid + "&token=" + token).openConnection();

        // add request header
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "audio/wav; rate=8000");

        conn.setDoInput(true);
        conn.setDoOutput(true);

        // send request
        DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
        wr.write(loadFile(pcmFile));
        wr.flush();
        wr.close();

        printResponse(conn);
    }

    private static String printResponse(HttpURLConnection conn) throws Exception {
        if (conn.getResponseCode() != 200) {
            // request error
            return "";
        }
        InputStream is = conn.getInputStream();
        BufferedReader rd = new BufferedReader(new InputStreamReader(is));
        String line;
        StringBuffer response = new StringBuffer();
        while ((line = rd.readLine()) != null) {
            response.append(line);
            response.append('\r');
        }
        rd.close();
        System.out.println(new JSONObject(response.toString()).toString(4));
        return response.toString();
    }

    private static byte[] loadFile(File file) throws IOException {
        InputStream is = new FileInputStream(file);

        long length = file.length();
        byte[] bytes = new byte[(int) length];

        int offset = 0;
        int numRead = 0;
        while (offset < bytes.length
                && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
            offset += numRead;
        }

        if (offset < bytes.length) {
            is.close();
            throw new IOException("Could not completely read file " + file.getName());
        }

        is.close();
        return bytes;
    }
    // GBK编码转为UTF-8
    private static String getUtf8String(String s) throws UnsupportedEncodingException
    {
        StringBuffer sb = new StringBuffer();
        sb.append(s);
        String xmlString = "";
        String xmlUtf8 = "";
        xmlString = new String(sb.toString().getBytes("GBK"));
        xmlUtf8 = URLEncoder.encode(xmlString , "GBK");

        return URLDecoder.decode(xmlUtf8, "UTF-8");
    }

}