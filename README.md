# FXWorks Trading Solutions – Java Spring Boot homework

Browser-based Spring Boot application (Java 25) for the *Java Applications – Lecture – Homework* assignment at John von Neumann University (GAMF). It combines a **SOAP client** for the Hungarian National Bank (MNB) and a **Forex client** for the Oanda v20 API (practice account).

**Authors:** Fahim Muntashir Ratul (H1566N) and Md Aminul Islam (PQFBDZ)

## Features

| Menu | Route | Description |
|---|---|---|
| Home | `/` | Company front page (Editorial theme by HTML5 UP) |
| SOAP | `/soap` | MNB exchange rates: choose currency and start/end date, see a table and a Chart.js line chart |
| Forex – Account | `/forex/account` | Oanda account information |
| Forex – ActPrice | `/forex/actprice` | Actual bid/ask price of a selected instrument |
| Forex – HistPrice | `/forex/histprice` | Last 10 historical prices for a selected instrument and granularity |
| Forex – Open | `/forex/open` | Open a market position (positive = Long, negative = Short) |
| Forex – Pos | `/forex/pos` | Table of open positions |
| Forex – Close | `/forex/close` | Close a trade by Trade ID at the market price |

## Requirements

- **Java 25**
- Internet connection (MNB web service, Oanda API, Chart.js CDN)
- An Oanda **practice** account (Account ID and API token)

## Run

1. Download `homework.jar` from the root of this repository.
2. In the same folder, create `password.txt` (it is not stored in Git):

   ```
   accountId=YOUR-OANDA-ACCOUNT-ID
   token=YOUR-OANDA-API-TOKEN
   ```

3. Start the application:

   ```
   java -jar homework.jar
   ```

4. Open <http://localhost:8080>.

The SOAP page works without `password.txt`; the Forex pages need it. The Forex market is closed at weekends, so orders cannot be filled then.

## Build from source

```
./mvnw clean package        # macOS / Linux
mvnw.cmd clean package      # Windows
```

The result is `target/homework.jar`. The first build downloads the MNB WSDL (`http://www.mnb.hu/arfolyamok.asmx?wsdl`) and generates the SOAP client into `src/main/java/soapclient`.

## Project structure

```
pom.xml                              Maven build (jaxws plugin, jar name "homework")
src/main/java/hu/homework/homework/
    HomeworkApplication.java         Spring Boot entry point
    HomeController.java              "/"
    Config.java                      Oanda URL, instrument list, reads password.txt
    SoapController.java              "/soap"
    ForexController.java             account, actprice, open, close
    ForexDataController.java         histprice, pos
src/main/java/soapclient/            generated MNB SOAP client
src/main/java/com/oanda/v20/         Oanda v20 Java client
src/main/resources/templates/        Thymeleaf pages and layout fragments
src/main/resources/static/           Editorial theme (CSS, JS, images)
```

## Technologies

Java 25, Spring Boot (Maven), Thymeleaf, JAX-WS (`jaxws-rt` / `jaxws-maven-plugin`), Oanda v20-java, Gson, Apache HttpClient, Chart.js, Editorial by HTML5 UP.

## Team and work split

| | Ratul | Aminul |
|---|---|---|
| Setup and layout | Repository, project, jar, theme, layout, front page, footer | Windows setup and testing |
| Features | Account, ActPrice, Open, Close, `Config` | SOAP (MNB), HistPrice, Pos |
| Branches | `ratul-layout`, `ratul-forex`, `ratul-jar` | `aminul-soap`, `aminul-forex` |

Work was done on task branches and merged into `main` with pull requests.

## Credits

Theme: [Editorial](https://html5up.net/editorial) by [HTML5 UP](https://html5up.net) (CCA 3.0).
