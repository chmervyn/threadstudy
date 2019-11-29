package com.sap.mervyn.thread.ch9;

import com.sap.mervyn.thread.util.Tools;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.InputStream;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

/**
 * 支持异步解析器的XML解析器
 */
public class XMLDocumentParser {





    // 封装对XML解析结果进行处理的回调放方法
    public abstract static class ResultHandler {
        abstract void onSuccess(Document document);

        void onError(Throwable e) { e.printStackTrace(); }
    }

    public static class ParsingTask {
        private final InputStream in;
        private volatile Executor executor;
        private volatile ResultHandler resultHandler;

        public ParsingTask(InputStream in, Executor executor, ResultHandler resultHandler) {
            this.in = in;
            this.executor = executor;
            this.resultHandler = resultHandler;
        }

        public ParsingTask(InputStream in) {
            this(in, null, null);
        }

        public Future<Document> execute() throws Exception {
            FutureTask<Document> ft;

            final Callable<Document> task = () -> doParse(in);

            final Executor theExecutor = executor;

            // 解析模式：异步/同步
            final boolean isAsyncParsing = theExecutor != null;
            final ResultHandler rh;

            if (isAsyncParsing && (rh = resultHandler) != null) {
                ft = new FutureTask<Document>(task) {
                    @Override
                    protected void done() {
                        // 回调ResultHandler的相关方法对XML解析结果进行处理

                    }
                };
            }
        }

        void callbackResultHandler(FutureTask<Document> ft, ResultHandler rh) {
            // 获取任务处理结果前判断任务是否被取消
        }

        private static Document doParse(InputStream in) throws Exception {
            Document document = null;
            try {
                DocumentBuilder db = DocumentBuilderFactory.newInstance()
                        .newDocumentBuilder();
                document = db.parse(in);
            } finally {
                Tools.silentClose(in);
            }
            return document;
        }
    }


}
