### 國泰世華Java Engineer線上作業

- 使用的springboot版本是2.7.1
  此決定的依據是: 網路上survey時看到 2.x → 3.x 會有不少坑
  ➜ 保險起見 (也為了節省找bug的時間)，故使用springboot 2 的最後一版 2.7.18

- 各套件的maven版本是參考springboot官網的資料
  https://docs.spring.io/spring-boot/docs/2.7.x/reference/html/dependency-versions.html

- API呼叫之URL:
   - GET,    http://localhost:8080/coindesk/api/v1.0.0/coin/list
   - POST,   http://localhost:8080/coindesk/api/v1.0.0/coin/add
   - PUT,    http://localhost:8080/coindesk/api/v1.0.0/coin/update
   - DELETE, http://localhost:8080/coindesk/api/v1.0.0/coin/delete

- h2 console URL:
   http://localhost:8080/coindesk/h2-console/


[註]:
- API目前可接收參數形式: application/json
- 欲新增幣別資料，可參考以下網站
  https://www.ifreesite.com/currency.htm
- JDK8升級到JDK11:
  如果有問題, 請試著執行maven update