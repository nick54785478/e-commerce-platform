$files = @(
    ".\order-service\src\main\java\com\example\demo\application\saga\OrderManagementSaga.java",
    ".\order-service\src\main\java\com\example\demo\application\service\OrderQueryService.java",
    ".\order-service\src\main\java\com\example\demo\iface\rest\OrderController.java"
)

foreach ($file in $files) {
    if (Test-Path $file) {
        $content = Get-Content $file
        $content = $content -replace 'import com.example.demo.application.command.CreatePaymentCommand;', 'import com.omni.payment.api.command.CreatePaymentCommand;'
        $content = $content -replace 'import com.example.demo.application.command.CancelPaymentCommand;', 'import com.omni.payment.api.command.CancelPaymentCommand;'
        $content = $content -replace 'import com.example.demo.application.command.RefundPaymentCommand;', 'import com.omni.payment.api.command.RefundPaymentCommand;'
        $content = $content -replace 'import com.example.demo.application.domain.payment.event.PaymentProcessedEvent;', 'import com.omni.payment.api.event.PaymentProcessedEvent;'
        $content = $content -replace 'import com.example.demo.application.domain.payment.aggregate.Payment;', ''
        $content = $content -replace 'import com.omni.order.api.dto.PaymentQueriedView;', 'import com.omni.payment.api.dto.PaymentQueriedView;'
        $content = $content -replace 'import com.omni.order.api.query.GetOrderPaymentsQuery;', 'import com.omni.payment.api.query.GetOrderPaymentsQuery;'
        $content | Set-Content $file
    }
}
