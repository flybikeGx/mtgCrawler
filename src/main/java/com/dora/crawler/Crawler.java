package com.dora.crawler;

import okhttp3.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import javax.net.ssl.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Crawler {
    String baseUrl;

    OkHttpClient client;

    public static String CHS_NAME = "alt=\"Simplified Chinese\"[\\s\\S]*?\">([\\u4e00-\\u9fa5]*)</a><br>";
    public static String TYPE = "";

    public Crawler() {
        client = new OkHttpClient.Builder()
        .sslSocketFactory(createSSLSocketFactory())
        .hostnameVerifier(new TrustAllHostnameVerifier())
        .build();
    }

    private static class TrustAllCerts implements X509TrustManager {
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {}

        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {}

        public X509Certificate[] getAcceptedIssuers() {return new X509Certificate[0];}
    }

    private static class TrustAllHostnameVerifier implements HostnameVerifier {
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    }


    private static SSLSocketFactory createSSLSocketFactory() {
        SSLSocketFactory ssfFactory = null;

        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null,  new TrustManager[] { new TrustAllCerts() }, new SecureRandom());

            ssfFactory = sc.getSocketFactory();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ssfFactory;
    }

    public String toCHS(String cardName) throws IOException {
        String engName = cardName;
        engName.replace("\\s+", "+");
        engName.replace("'s", "%27s");
        String url = String.format("http://magiccards.info/query?q=%s&v=card&s=cname", engName);

        Request request = new Request.Builder()
                .url(url).build();

        Response response = client.newCall(request).execute();

        String html = response.body().string();
        String pattern = "(alt=\"Simplified Chinese\"[\\s\\S]*?\">)([\\u4e00-\\u9fa5]*)(</a><br>)";
        // 创建 Pattern 对象
        Pattern r = Pattern.compile(pattern);

        // 现在创建 matcher 对象
        Matcher m = r.matcher(html);
        if (m.find( )) {
            engName = m.group(2);
        }
        return engName;
    }


    public String getInfo(String cardName, String pattern) throws IOException {
        String engName = cardName;
        engName = engName.replace(" ", "+").replace("’", "+");
        String url = String.format("http://magiccards.info/query?q=%s&v=card&s=cname", engName);
//        System.out.println(url);
        Request request = new Request.Builder()
                .url(url).build();

        Response response = client.newCall(request).execute();

        String html = response.body().string();
        // 创建 Pattern 对象
        Pattern r = Pattern.compile(pattern);

        // 现在创建 matcher 对象
        Matcher m = r.matcher(html);
        if (m.find( )) {
            engName = m.group(1);
        }
        return engName;
    }

    public void downloadPic(String cardName) throws IOException {
        String engName = cardName;
        engName = engName.replace(" ", "+").replace("’", "+");
        String url = String.format("http://magiccards.info/query?q=%s&v=card&s=cname", engName);
//        System.out.println(url);
        Request request = new Request.Builder()
                .url(url).build();

        Response response = client.newCall(request).execute();

        String html = response.body().string();

        Document document = Jsoup.parse(html);

        List<Element> elements = document.getElementsByAttributeValue("height", "445");

        String path = elements.get(0).attr("src");

        request = new Request.Builder().url("https://magiccards.info" + path).build();


        final File file = new File("~/mtgPicCache/" + cardName + ".full.jpg");
        file.getParentFile().mkdirs();

        client.newCall(request).enqueue(new Callback() {
            public void onFailure(Call call, IOException e) {

            }

            public void onResponse(Call call, Response response) throws IOException {
                InputStream is = null;
                byte[] buf = new byte[2048];
                int len = 0;
                FileOutputStream fos = null;
                try {
                    is = response.body().byteStream();
                    fos = new FileOutputStream(file);
                    while ((len = is.read(buf)) != -1) {
                        fos.write(buf, 0, len);
                    }
                    fos.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (is != null) {
                            is.close();
                        }
                        if (fos != null) {
                            fos.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

    }
}
