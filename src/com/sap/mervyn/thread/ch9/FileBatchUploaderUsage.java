package com.sap.mervyn.thread.ch9;

import com.sap.mervyn.thread.util.Debug;
import com.sap.mervyn.thread.util.Tools;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

public class FileBatchUploaderUsage {

    public static void main(String[] args) throws Exception {
        FileBatchUploader uploader = new FileBatchUploader("localhost", "datacenter", "abc123",
                "/home/datacenter/tmp/") {

            @Override
            public void init() throws Exception {
                Debug.info("init...");
                super.init();
            }

            @Override
            public void upload(File file) throws Exception {
                super.upload(file);
            }

            @Override
            public void close()  {
                Debug.info("close...");
                super.close();
            }

        };

        uploader.init();
        Set<File> files = new HashSet<File>();
        files.add(new File("/home/viscent/tmp/incomingX/message1.dat"));
        files.add(new File("/home/viscent/tmp/incomingX/message2.dat"));
        files.add(new File("/home/viscent/tmp/incomingX/message3.dat"));
        files.add(new File("/home/viscent/tmp/incomingX/message4.dat"));
        files.add(new File("/home/viscent/tmp/incomingX/message5.dat"));
        uploader.uploadFiles(files);

        Tools.delayedAction("",
                () -> {
                    Tools.silentClose(uploader);
                }, 120);
    }

}
