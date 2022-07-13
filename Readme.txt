• 此專案為「國泰世華Java Engineer線上作業」

• API呼叫之URL:
   GET,  http://localhost:8080/coindesk/api/v1.0.0/coin/list
   POST, http://localhost:8080/coindesk/api/v1.0.0/coin/add
   PUT,  http://localhost:8080/coindesk/api/v1.0.0/coin/update

• h2 console URL:
   http://localhost:8080/coindesk/h2-console/


[註]:
• 目前尚未能接收json參數
   欲指定api參數, 請用:
   form data 或 application/x-www-form-urlencoded

• 測試：計畫使用junit hierarchicalcontextrunner
• 目前測試尚有錯誤: initialization error: "No contexts found!"
   待查問題
