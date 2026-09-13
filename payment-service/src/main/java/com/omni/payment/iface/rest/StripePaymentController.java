package com.omni.payment.iface.rest;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.omni.payment.application.service.PaymentCommandService;
import com.stripe.Stripe;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import com.stripe.param.checkout.SessionCreateParams;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

/**
 * 處理 Stripe 金流整合的 Controller。
 * 此類別提供建立 Stripe Checkout Session 的 API 端點，
 * 並負責接收 Stripe 的非同步 Webhook 事件來更新付款狀態。
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/stripe")
public class StripePaymentController {

	private final PaymentCommandService paymentCommandService;
	
	@Value("${stripe.api.secretKey}")
	private String stripeSecretKey;

	@Value("${stripe.webhook.secret}")
	private String endpointSecret;

	// 開發時使用 localhost 測試前端路由，上線後需替換為實際的前端網域
	private static final String FRONTEND_BASE_URL = "http://localhost:4200";

	/**
	 * 透過建構子注入 PaymentCommandService。
	 *
	 * @param paymentCommandService 處理付款相關業務邏輯的服務
	 */
	public StripePaymentController(PaymentCommandService paymentCommandService) {
		this.paymentCommandService = paymentCommandService;
	}

	/**
	 * 初始化 Stripe API，設定從 application properties 中讀取的 Secret Key。
	 * 此方法會在 Bean 建構完成後自動被呼叫。
	 */
	@PostConstruct
	public void init() {
		Stripe.apiKey = stripeSecretKey;
	}

	/**
	 * 建立 Stripe Checkout Session 時的請求參數封裝 (Payload)。
	 *
	 * @param paymentId 系統內部的付款 ID
	 * @param orderId   關聯的訂單 ID
	 * @param amount    需付款的總金額
	 */
	public record CreateSessionRequest(String paymentId, String orderId, BigDecimal amount) {}

	/**
	 * 建立 Stripe Checkout Session 後的回應資料封裝 (Payload)。
	 *
	 * @param sessionId 建立成功後的 Stripe 專屬 Session ID
	 * @param url       前端需導向至該 URL 進行付款
	 */
	public record CreateSessionResponse(String sessionId, String url) {}

	/**
	 * 建立全新的 Stripe Checkout Session 的端點。
	 * 將傳入的金額轉換為最小貨幣單位 (例如：TWD/USD 轉為 cents)，
	 * 並配置成功與取消的返回網址，同時附帶訂單的 Metadata。
	 *
	 * @param request 建立 Checkout Session 所需的詳細資訊
	 * @return 包含 Session ID 與跳轉 URL 的 ResponseEntity，若發生錯誤則回傳錯誤狀態
	 */
	@PostMapping("/create-checkout-session")
	public ResponseEntity<?> createCheckoutSession(@RequestBody CreateSessionRequest request) {
		try {
			// 將金額轉為分/cents (Stripe 要求必須使用最小貨幣單位)
			long amountInCents = request.amount().multiply(new BigDecimal(100)).longValue();

			SessionCreateParams params = SessionCreateParams.builder()
					.setMode(SessionCreateParams.Mode.PAYMENT)
					.setSuccessUrl(FRONTEND_BASE_URL + "/payment/success?session_id={CHECKOUT_SESSION_ID}")
					.setCancelUrl(FRONTEND_BASE_URL + "/payment/cancel")
					.addLineItem(SessionCreateParams.LineItem.builder()
							.setQuantity(1L)
							.setPriceData(SessionCreateParams.LineItem.PriceData.builder()
									.setCurrency("twd") // 可依需求改為 usd 等其他幣別
									.setUnitAmount(amountInCents)
									.setProductData(SessionCreateParams.LineItem.PriceData.ProductData.builder()
											.setName("Order #" + request.orderId())
											.build())
									.build())
							.build())
					.putMetadata("paymentId", request.paymentId())
					.putMetadata("orderId", request.orderId())
					.putMetadata("amount", request.amount().toString())
					.build();

			Session session = Session.create(params);
			return ResponseEntity.ok(new CreateSessionResponse(session.getId(), session.getUrl()));
		} catch (Exception e) {
			log.error("建立 Stripe Checkout Session 失敗", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(Map.of("error", e.getMessage()));
		}
	}

	/**
	 * 接收 Stripe Webhook 事件的端點。
	 * Stripe 會在背景非同步呼叫此端點 (例如：當付款成功時)。
	 * 基於安全考量，此端點會驗證請求中的數位簽章。
	 *
	 * @param payload   Stripe 發送的原始 JSON 字串
	 * @param sigHeader 用來進行安全驗證的 Stripe-Signature 標頭
	 * @return 回傳 CompletableFuture 包裝的 ResponseEntity 代表執行成功或失敗
	 */
	@PostMapping("/webhook")
	public CompletableFuture<ResponseEntity<String>> handleWebhook(@RequestBody String payload,
			@RequestHeader("Stripe-Signature") String sigHeader) {
		
		Event event;
		try {
			// 如果在本地測試且沒有設定 Webhook Secret，可略過簽章驗證，
			// 但在上線(Production)環境中，絕對要進行驗證！
			if ("YOUR_STRIPE_WEBHOOK_SECRET".equals(endpointSecret)) {
				log.warn("尚未設定 Webhook Secret，跳過簽章驗證。");
				event = com.stripe.net.ApiResource.GSON.fromJson(payload, Event.class);
			} else {
				event = Webhook.constructEvent(payload, sigHeader, endpointSecret);
			}
		} catch (SignatureVerificationException e) {
			log.warn("無效的數位簽章。", e);
			return CompletableFuture.completedFuture(ResponseEntity.status(HttpStatus.BAD_REQUEST).build());
		} catch (Exception e) {
			log.error("解析 Webhook 失敗。", e);
			return CompletableFuture.completedFuture(ResponseEntity.status(HttpStatus.BAD_REQUEST).build());
		}

		if ("checkout.session.completed".equals(event.getType())) {
			Session session = (Session) event.getDataObjectDeserializer().getObject().orElse(null);
			if (session != null) {
				String paymentId = session.getMetadata().get("paymentId");
				String orderId = session.getMetadata().get("orderId");
				String amountStr = session.getMetadata().get("amount");
				BigDecimal amount = new BigDecimal(amountStr);

				log.info("Checkout Session 付款完成！PaymentId: {}, OrderId: {}", paymentId, orderId);

				return paymentCommandService.processPayment(paymentId, orderId, amount)
						.thenApply(v -> ResponseEntity.ok("Success"))
						.exceptionally(ex -> {
							log.error("執行 ProcessPaymentCommand 失敗", ex);
							// 即使業務邏輯發生錯誤，依然回傳 200，避免 Stripe 認為投遞失敗而無限次重試
							return ResponseEntity.ok("Business Logic Error"); 
						});
			}
		}

		return CompletableFuture.completedFuture(ResponseEntity.ok("Event received"));
	}
}
