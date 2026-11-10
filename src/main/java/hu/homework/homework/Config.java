package hu.homework.homework;

import java.util.List;
import com.oanda.v20.account.AccountID;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Config {
    private Config() {}

    public static final String URL = "https://api-fxpractice.oanda.com";
    public static final List<String> INSTRUMENTS =
            List.of("EUR_USD", "USD_CHF", "GBP_USD", "USD_JPY", "AUD_USD", "EUR_GBP");
    public static final AccountID ACCOUNTID;
    public static final String TOKEN;

    static {
        Properties p = new Properties();
        try (FileInputStream in = new FileInputStream("password.txt")) {
            p.load(in);
        } catch (IOException e) {
            throw new IllegalStateException("password.txt not found next to homework.jar", e);
        }
        ACCOUNTID = new AccountID(p.getProperty("accountId").trim());
        TOKEN = p.getProperty("token").trim();
    }
}