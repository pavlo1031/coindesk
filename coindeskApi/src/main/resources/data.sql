-- 建立預設幣別
INSERT INTO `coin_type` (code, symbol, rate_float, description, description_zh)
VALUES ('USD', '&#36;', 20176.4049, 'United State Dollar', '美金'),
       ('GBP', '&pound;', 16859.2425, 'British Pound Sterling', '英鎊'),
       ('EUR', '&euro;', 19654.7641, 'Euro', '歐元'),
       ('CAD', '&#36;', 22.92, 'Canada dollar', '加拿大元'),
       ('MYR', 'RM', 6.2860, 'Malaysian ringgit', '馬來西亞令吉'),
       ('SGD', '&#36;', 24.08, 'Singapore dollar', '新家坡元');

-- 建立共通系統參數
INSERT INTO `global_common` (`name`, `value`)
VALUES ('currency_format', '#,##0.000'),
       ('disclaimer', 'This data was produced from the CoinDesk Bitcoin Price Index (USD). Non-USD currency data converted using hourly conversion rate from openexchangerates.org');
