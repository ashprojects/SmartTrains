package Utilities;


import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.IOException;

import commons.Config;

public class StationReporter {
    public static void report(final String code, final String name) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Connection connection = Jsoup.connect(Config.reportUrl)
                            .data("reqType", "report")
                            .data("item", "station");
                    if (code != null) {
                        connection = connection.data("code", code);
                    }
                    if (name != null) {
                        connection = connection.data("name", name);
                    }
                    connection.post();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }
}
