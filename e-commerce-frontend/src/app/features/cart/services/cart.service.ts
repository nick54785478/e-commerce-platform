import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { CartItem } from '../models/cart.model';

@Injectable({
  providedIn: 'root'
})
export class CartService {
  private readonly CART_KEY = 'omni_cart';
  private cartItemsSubject = new BehaviorSubject<CartItem[]>(this.loadCart());
  
  public cartItems$: Observable<CartItem[]> = this.cartItemsSubject.asObservable();
  
  public cartTotalCount$: Observable<number> = this.cartItems$.pipe(
    map(items => items.reduce((total, item) => total + item.quantity, 0))
  );

  public cartTotalPrice$: Observable<number> = this.cartItems$.pipe(
    map(items => items.reduce((total, item) => total + (item.price * item.quantity), 0))
  );

  constructor() {}

  private loadCart(): CartItem[] {
    const saved = localStorage.getItem(this.CART_KEY);
    if (saved) {
      try {
        return JSON.parse(saved);
      } catch (e) {
        console.error('Failed to parse cart from local storage', e);
      }
    }
    return [];
  }

  private saveCart(items: CartItem[]): void {
    localStorage.setItem(this.CART_KEY, JSON.stringify(items));
    this.cartItemsSubject.next(items);
  }

  addToCart(item: CartItem): void {
    const items = this.cartItemsSubject.value;
    const existing = items.find(i => i.productId === item.productId);

    if (existing) {
      if (existing.quantity + item.quantity <= existing.stock) {
         existing.quantity += item.quantity;
      } else {
         existing.quantity = existing.stock;
      }
      this.saveCart([...items]);
    } else {
      this.saveCart([...items, { ...item, quantity: Math.min(item.quantity, item.stock) }]);
    }
  }

  removeFromCart(productId: string): void {
    const items = this.cartItemsSubject.value.filter(i => i.productId !== productId);
    this.saveCart(items);
  }

  updateQuantity(productId: string, quantity: number): void {
    const items = this.cartItemsSubject.value;
    const index = items.findIndex(i => i.productId === productId);
    
    if (index !== -1) {
      if (quantity <= 0) {
        this.removeFromCart(productId);
      } else {
        items[index].quantity = Math.min(quantity, items[index].stock);
        this.saveCart([...items]);
      }
    }
  }

  clearCart(): void {
    this.saveCart([]);
  }
}
