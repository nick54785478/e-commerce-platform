import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, ActivatedRoute, Router } from '@angular/router';
import { ButtonModule } from 'primeng/button';
import { MessageService } from 'primeng/api';
import { ToastModule } from 'primeng/toast';
import { OrderService } from '../../../../core/services/order.service';
import { PaymentService } from '../../services/payment.service';
import { Subscription } from 'rxjs';
import { environment } from '../../../../../environments/environment';

@Component({
  selector: 'app-payment-page',
  standalone: true,
  imports: [CommonModule, RouterModule, ButtonModule, ToastModule],
  providers: [MessageService],
  templateUrl: './payment-page.component.html',
  styleUrls: ['./payment-page.component.css']
})
export class PaymentPageComponent implements OnInit, OnDestroy {
  orderId: string | null = null;
  paymentId: string | null = null;
  paymentAmount: number = 0;
  isProcessing = false;
  
  private querySub?: Subscription;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private messageService: MessageService,
    private orderService: OrderService,
    private paymentService: PaymentService
  ) {}

  ngOnInit(): void {
    this.orderId = this.route.snapshot.paramMap.get('orderId');
    if (this.orderId) {
      this.pollPayment();
    }
  }

  ngOnDestroy(): void {
    if (this.querySub) {
      this.querySub.unsubscribe();
    }
  }

  pollPayment(): void {
    // 簡單輪詢直到後端 Saga 建立付款
    let retries = 0;
    const intervalId = setInterval(() => {
      if (retries > 10) {
        clearInterval(intervalId);
        this.messageService.add({ severity: 'error', summary: 'Error', detail: 'Could not fetch payment info.' });
        return;
      }
      this.querySub = this.orderService.getPayments(this.orderId!).subscribe({
        next: (res) => {
          if (res.data && res.data.length > 0) {
            this.paymentId = res.data[0].paymentId;
            this.paymentAmount = res.data[0].amount;
            clearInterval(intervalId);
          }
        }
      });
      retries++;
    }, 1000);
  }

  processPayment(): void {
    if (!this.paymentId || !this.orderId) {
      this.messageService.add({ severity: 'warn', summary: 'Warning', detail: 'Payment is not ready yet. Please wait.' });
      return;
    }

    this.isProcessing = true;
    
    this.paymentService.createCheckoutSession({
      paymentId: this.paymentId,
      orderId: this.orderId,
      amount: this.paymentAmount
    }).subscribe({
      next: (res) => {
        if (res.url) {
          window.location.href = res.url;
        } else {
          this.isProcessing = false;
          this.messageService.add({ severity: 'error', summary: 'Error', detail: 'Invalid Stripe Session URL.' });
        }
      },
      error: (err) => {
        this.isProcessing = false;
        this.messageService.add({ severity: 'error', summary: 'Error', detail: 'Failed to initialize payment session!' });
        console.error(err);
      }
    });
  }
}
