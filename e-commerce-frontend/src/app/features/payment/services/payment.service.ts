import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface ProcessPaymentPayload {
  orderId: string;
  amount: number;
}

export interface PaymentProcessedResource {
  code: string;
  message: string;
  paymentId: string;
}

@Injectable({
  providedIn: 'root'
})
export class PaymentService {
  private apiUrl = '/api/payments';

  constructor(private http: HttpClient) {}

  processPayment(paymentId: string, payload: ProcessPaymentPayload): Observable<PaymentProcessedResource> {
    // TODO: 未來預計在這裡介接第三方金流 API (如 Stripe, LinePay)
    // 目前預設都是 Success，直接打後端的 Process API 觸發後續 Saga 流程
    return this.http.put<PaymentProcessedResource>(`${this.apiUrl}/${paymentId}/process`, payload);
  }
}
