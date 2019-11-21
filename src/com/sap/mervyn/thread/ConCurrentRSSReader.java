package com.sap.mervyn.thread;

import com.sap.mervyn.thread.util.Tools;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

public class ConCurrentRSSReader {

    public static void main(String[] args) throws IOException, ParserConfigurationException, SAXException {
        final String rssUrl = "http://lorem-rss.herokuapp.com/feed";

        InputStream in = loadRSS(rssUrl);
        Document document = parseXML(in);

        Element eleRss = (Element) document.getFirstChild();
        Element eleChannel = (Element) eleRss.getElementsByTagName("channel").item(0);

        Node ndTitle = eleChannel.getElementsByTagName("title").item(0);
        String title = ndTitle.getFirstChild().getNodeValue();
        System.out.println("title: " + title);

    }

    private static Document parseXML(InputStream in) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        return db.parse(in);
    }

    private static InputStream loadRSS(String rssUrl) throws IOException {
        final PipedInputStream pin = new PipedInputStream();
        final PipedOutputStream pout = new PipedOutputStream(pin);

        Thread workerThread = new Thread(() -> {
            try {
                download(rssUrl, pout);
            } catch (Exception e) {
                Tools.silentClose(pout, pin);
                e.printStackTrace();
            }
        }, "rss-loader");

        workerThread.start();

        return pin;
    }

    private static BufferedInputStream issueRequest(String rssUrl) throws Exception {
        URL requestURL = new URL(rssUrl);
        final HttpURLConnection conn = (HttpURLConnection) requestURL.openConnection();
        conn.setConnectTimeout(2000);
        conn.setReadTimeout(2000);
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Connection", "close");
        conn.setDoInput(true);
        conn.connect();

        int statusCode = conn.getResponseCode();
        if (HttpURLConnection.HTTP_OK != statusCode) {
            conn.disconnect();
            throw new Exception("Server exception, status code: " + statusCode);
        }

        BufferedInputStream in = new BufferedInputStream(conn.getInputStream()) {
            @Override
            public void close() throws IOException {
                try {
                    super.close();
                } finally {
                    conn.disconnect();
                }
            }
        };

        return in;
    }

    private static void download(String rssUrl, PipedOutputStream pout) throws Exception {
        ReadableByteChannel readChannel = null;
        WritableByteChannel writeChannel = null;

        try {
            BufferedInputStream in = issueRequest(rssUrl);
            readChannel = Channels.newChannel(in);
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            writeChannel = Channels.newChannel(pout);

            while (readChannel.read(buffer) > 0) {
                buffer.flip();
                writeChannel.write(buffer);
                buffer.clear();
            }

        } finally {
            Tools.silentClose(writeChannel, readChannel);
        }
    }

}
