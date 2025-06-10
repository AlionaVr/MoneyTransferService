package org.example.service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TransferLogger {
    private static final String LOG_FILE = "transfers.log";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static TransferLogger instance;
    private static final String SEPARATOR = "+-----------------------+-----------------+------------------+------------+-----------+-----------------+%n";
    private static final String HEADER_FORMAT = "| %-21s | %-15s | %-16s | %-10s | %-9s | %-15s |%n";
    private static final String DATA_FORMAT = "| %-21s | %-15s | %-16s | %-10.2f | %-9.2f | %-15s |%n";
    private boolean isHeaderWritten = false;

    private TransferLogger() {
    }


    private void writeHeader() throws IOException {
        try (FileWriter writer = new FileWriter(LOG_FILE, true)) {
            writer.write(String.format(SEPARATOR));
            writer.write(String.format(HEADER_FORMAT,
                    "Date",
                    "Card From",
                    "Card To",
                    "Amount",
                    "Fee",
                    "Status"
            ));
            writer.write(String.format(SEPARATOR));
            isHeaderWritten = true;
        }
    }

    public static TransferLogger getInstance() {
        if (instance == null) {
            synchronized (TransferLogger.class) {
                if (instance == null) {
                    instance = new TransferLogger();
                }
            }
        }
        return instance;
    }

    public void logTransfer(String fromCard, String toCard, Double amount, Double commission, String result) {
        LocalDateTime now = LocalDateTime.now();

        try (FileWriter writer = new FileWriter(LOG_FILE, true)) {
            if (!isHeaderWritten) {
                writeHeader();
            }

            writer.write(String.format(DATA_FORMAT,
                    now.format(formatter),
                    fromCard,
                    toCard,
                    amount,
                    commission,
                    result
            ));
            writer.write(String.format(SEPARATOR));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


