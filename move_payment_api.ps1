$orderSrc = ".\order-service\src\main\java\com\example\demo"
$paymentApiSrc = ".\shared-apis\payment-api\src\main\java\com\omni\payment\api"
$orderApiSrc = ".\shared-apis\order-api\src\main\java\com\omni\order\api"

New-Item -ItemType Directory -Force -Path "$paymentApiSrc\command"
New-Item -ItemType Directory -Force -Path "$paymentApiSrc\event"
New-Item -ItemType Directory -Force -Path "$paymentApiSrc\query"
New-Item -ItemType Directory -Force -Path "$paymentApiSrc\dto"

Move-Item -Path "$orderSrc\application\command\*Payment*.java" -Destination "$paymentApiSrc\command"
Move-Item -Path "$orderSrc\application\domain\payment\event\*.java" -Destination "$paymentApiSrc\event"
Move-Item -Path "$orderApiSrc\dto\PaymentQueriedView.java" -Destination "$paymentApiSrc\dto"
Move-Item -Path "$orderApiSrc\query\*Payment*.java" -Destination "$paymentApiSrc\query"

Get-ChildItem -Path "$paymentApiSrc\command" -Filter *.java | ForEach-Object {
    (Get-Content $_.FullName) -replace 'package com.example.demo.application.command;', 'package com.omni.payment.api.command;' | Set-Content $_.FullName
}

Get-ChildItem -Path "$paymentApiSrc\event" -Filter *.java | ForEach-Object {
    (Get-Content $_.FullName) -replace 'package com.example.demo.application.domain.payment.event;', 'package com.omni.payment.api.event;' | Set-Content $_.FullName
}

Get-ChildItem -Path "$paymentApiSrc\dto" -Filter *.java | ForEach-Object {
    (Get-Content $_.FullName) -replace 'package com.omni.order.api.dto;', 'package com.omni.payment.api.dto;' | Set-Content $_.FullName
}

Get-ChildItem -Path "$paymentApiSrc\query" -Filter *.java | ForEach-Object {
    (Get-Content $_.FullName) -replace 'package com.omni.order.api.query;', 'package com.omni.payment.api.query;' | Set-Content $_.FullName
}
