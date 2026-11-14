# FX Horizon – Java applications Lecture homework

Browser-based desktop application with **Java 25 + Spring Boot 4.1.2**.

| Menu | URL | Implemented in |
|---|---|---|
| Mainpage | `/` | `MainController`, `templates/index.html` |
| SOAP (MNB rates + Chart.js) | `/soap` | `soap/SoapController`, `soap/MnbService`, `templates/soap.html`, generated client in `soapclient/` |
| Forex-Account | `/forex/account` | `forex/ForexController#account`, `forex_account.html` |
| Forex-ActPrice | `/forex/actprice` | `ForexController#actPrice`, `forex_actprice.html` |
| Forex-HistPrice | `/forex/histprice` | `ForexController#histPrice`, `forex_histprice.html` |
| Forex-Open | `/forex/open` | `ForexController#open`, `forex_open.html` |
| Forex-Pos | `/forex/positions` | `ForexController#positions`, `forex_positions.html` |
| Forex-Close | `/forex/close` | `ForexController#close`, `forex_close.html` |
| Test (bonus) | `/calc` | `calc/*`, tests in `src/test/java` |
| React (bonus) | `/react/` | React + Axios app in `frontend/src` (built into `static/react`), REST API `towns/TownApiController`, JPA entities `towns/Town`, `towns/County`, H2 file DB in `C:\data` |

Theme: **Bootswatch Flatly** (free, responsive, MIT) – https://bootswatch.com/flatly/

## Run
```
java -jar homework.jar
```
then open http://localhost:8080/

Oanda credentials are in `src/main/resources/application.properties`
(or pass `--oanda.account-id=... --oanda.token=...` on the command line).

## React menu (bonus)
CRUD for the `towns` table of **towns_db** (county chosen from the `counties` table).

* Backend: `/api/counties`, `/api/towns` (GET, POST, PUT, DELETE) – `towns/TownApiController.java`
* Database: H2, saved to `C:\data\townsdb.mv.db`; filled from `src/main/resources/data/*.csv` on the first start
* Frontend: Vite + React + Axios in `frontend/`

Build the React app (needs Node.js 20.19+):
```
cd frontend
npm install
npm run build      # writes src/main/resources/static/react
```
Then `mvn clean package` packs it into `homework.jar`.
For development: run the Spring app, then `npm run dev` and open http://localhost:5173/react/
