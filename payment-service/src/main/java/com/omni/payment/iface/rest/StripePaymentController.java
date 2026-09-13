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

@Slf4j
@RestController
@RequestMapping("/api/v1/stripe")
public class StripePaymentController {

	private final PaymentCommandService paymentCommandService;
	
	@Value("${stripe.api.secretKey}")
	private String stripeSecretKey;

	@Value("${stripe.webhook.secret}")
	private String endpointSecret;

	// Use localhost for testing frontend routing, or update with your actual frontend domain
	private static final String FRONTEND_BASE_URL = "http://localhost:4200";

	public StripePaymentController(PaymentCommandService paymentCommandService) {
		this.paymentCommandService = paymentCommandService;
	}

	@PostConstruct
	public void init() {
		Stripe.apiKey = stripeSecretKey;
	}

	public record CreateSessionRequest(String paymentId, String orderId, BigDecimal amount) {}
	public record CreateSessionResponse(String sessionId, String url) {}

	@PostMapping("/create-checkout-session")
	public ResponseEntity<?> createCheckoutSession(@RequestBody CreateSessionRequest request) {
		try {
			// Convert amount to cents (Stripe requires smallest currency unit)
			long amountInCents = request.amount().multiply(new BigDecimal(100)).longValue();

			SessionCreateParams params = SessionCreateParams.builder()
					.setMode(SessionCreateParams.Mode.PAYMENT)
					.setSuccessUrl(FRONTEND_BASE_URL + "/payment/success?session_id={CHECKOUT_SESSION_ID}")
					.setCancelUrl(FRONTEND_BASE_URL + "/payment/cancel")
					.addLineItem(SessionCreateParams.LineItem.builder()
							.setQuantity(1L)
							.setPriceData(SessionCreateParams.LineItem.PriceData.builder()
									.setCurrency("twd") // or usd
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
			log.error("Failed to create Stripe Checkout Session", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(Map.of("error", e.getMessage()));
		}
	}

	@PostMapping("/webhook")
	public CompletableFuture<ResponseEntity<String>> handleWebhook(@RequestBody String payload,
			@RequestHeader("Stripe-Signature") String sigHeader) {
		
		Event event;
		try {
			// If testing without a webhook secret, you can bypass signature verification
			// but for production, always verify!
			if ("YOUR_STRIPE_WEBHOOK_SECRET".equals(endpointSecret)) {
				log.warn("Webhook secret not configured, skipping signature verification.");
				event = com.stripe.net.ApiResource.GSON.fromJson(payload, Event.class);
			} else {
				event = Webhook.constructEvent(payload, sigHeader, endpointSecret);
			}
		} catch (SignatureVerificationException e) {
			log.warn("Invalid signature.", e);
			return CompletableFuture.completedFuture(ResponseEntity.status(HttpStatus.BAD_REQUEST).build());
		} catch (Exception e) {
			log.error("Failed to parse webhook.", e);
			return CompletableFuture.completedFuture(ResponseEntity.status(HttpStatus.BAD_REQUEST).build());
		}

		if ("checkout.session.completed".equals(event.getType())) {
			Session session = (Session) event.getDataObjectDeserializer().getObject().orElse(null);
			if (session != null) {
				String paymentId = session.getMetadata().get("paymentId");
				String orderId = session.getMetadata().get("orderId");
				String amountStr = session.getMetadata().get("amount");
				BigDecimal amount = new BigDecimal(amountStr);

				log.info("Checkout Session Completed! PaymentId: {}, OrderId: {}", paymentId, orderId);

				return paymentCommandService.processPayment(paymentId, orderId, amount)
						.thenApply(v -> ResponseEntity.ok("Success"))
						.exceptionally(ex -> {
							log.error("ProcessPaymentCommand failed", ex);
							// Still return 200 so Stripe doesn't retry infinitely if it's a permanent business error
							return ResponseEntity.ok("Business Logic Error"); 
						});
			}
		}

		return CompletableFuture.completedFuture(ResponseEntity.ok("Event received"));
	}
}
