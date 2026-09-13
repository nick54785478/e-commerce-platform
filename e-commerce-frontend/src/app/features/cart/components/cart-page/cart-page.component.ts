import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { ButtonModule } from 'primeng/button';
import { InputNumberModule } from 'primeng/inputnumber';
import { FormsModule } from '@angular/forms';
import { CartService } from '../../services/cart.service';
import { CartItem } from '../../models/cart.model';
import { Observable } from 'rxjs';
import { take } from 'rxjs/operators';
import { OrderService } from '../../../../core/services/order.service';
import { CreateOrderResource } from '../../../../core/models/order.model';
import { Router } from '@angular/router';
import { MessageService } from 'primeng/api';
import { ToastModule } from 'primeng/toast';

@Component({
  selector: 'app-cart-page',
  standalone: true,
  imports: [CommonModule, RouterModule, ButtonModule, InputNumberModule, FormsModule, ToastModule],
  providers: [MessageService],
  templateUrl: './cart-page.component.html',
  styleUrls: ['./cart-page.component.css']
})
export class CartPageComponent implements OnInit {

  cartItems$: Observable<CartItem[]>;
  cartTotalCount$: Observable<number>;
  cartTotalPrice$: Observable<number>;

  constructor(
    private cartService: CartService,
    private orderService: OrderService,
    private router: Router,
    private messageService: MessageService
  ) {
    this.cartItems$ = this.cartService.cartItems$;
    this.cartTotalCount$ = this.cartService.cartTotalCount$;
    this.cartTotalPrice$ = this.cartService.cartTotalPrice$;
  }

  ngOnInit(): void {
  }

  updateQuantity(productId: string, qty: number): void {
    if (qty != null) {
      this.cartService.updateQuantity(productId, qty);
    }
  }

  removeItem(productId: string): void {
    this.cartService.removeFromCart(productId);
  }

  get fallbackImage(): string {
    return 'data:image/svg+xml;charset=UTF-8,%3Csvg%20width%3D%22200%22%20height%3D%22200%22%20xmlns%3D%22http%3A%2F%2Fwww.w3.org%2F2000%2Fsvg%22%20viewBox%3D%220%200%20200%20200%22%20preserveAspectRatio%3D%22none%22%3E%3Cdefs%3E%3Cstyle%20type%3D%22text%2Fcss%22%3E%23holder_1899%20text%20%7B%20fill%3A%239494a0%3Bfont-weight%3A500%3Bfont-family%3AInter%2C%20sans-serif%3Bfont-size%3A14pt%20%7D%20%3C%2Fstyle%3E%3C%2Fdefs%3E%3Cg%20id%3D%22holder_1899%22%3E%3Crect%20width%3D%22200%22%20height%3D%22200%22%20fill%3D%22%23191923%22%3E%3C%2Frect%3E%3Cg%3E%3Ctext%20x%3D%2260%22%20y%3D%22105%22%3ENo%20Image%3C%2Ftext%3E%3C%2Fg%3E%3C%2Fg%3E%3C%2Fsvg%3E';
  }

  handleImageError(event: any) {
    event.target.src = this.fallbackImage;
  }

  checkout(): void {
    this.cartItems$.pipe(take(1)).subscribe(items => {
      if (!items || items.length === 0) {
        this.messageService.add({ severity: 'warn', summary: 'Warning', detail: 'Your cart is empty!' });
        return;
      }

      const payload: CreateOrderResource = {
        items: items.map(item => ({
          productId: item.productId,
          quantity: item.quantity
        }))
      };

      this.orderService.createOrder(payload).subscribe({
        next: (res) => {
          this.cartService.clearCart();
          this.messageService.add({ severity: 'success', summary: 'Success', detail: 'Order created successfully!' });
          setTimeout(() => {
            this.router.navigate(['/payment', res.orderId]);
          }, 1500);
        },
        error: (err: any) => {
          this.messageService.add({ severity: 'error', summary: 'Error', detail: 'Failed to create order.' });
          console.error(err);
        }
      });
    });
  }
}
