import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { CreateOrderResource, OrderCreatedResource, PaymentsQueriedResource } from '../models/order.model';

@Injectable({
  providedIn: 'root'
})
export class OrderService {
  private apiUrl = '/api/orders';

  constructor(private http: HttpClient) {}

  createOrder(data: CreateOrderResource): Observable<OrderCreatedResource> {
    return this.http.post<OrderCreatedResource>(this.apiUrl, data);
  }

  getPayments(orderId: string): Observable<PaymentsQueriedResource> {
    return this.http.get<PaymentsQueriedResource>(`${this.apiUrl}/${orderId}/payments`);
  }
}
