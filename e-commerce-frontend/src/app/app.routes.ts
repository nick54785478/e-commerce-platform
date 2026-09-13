import { Routes } from '@angular/router';
import { ShopLayoutComponent } from './core/components/shop-layout/shop-layout.component';
import { AdminLayoutComponent } from './core/components/admin-layout/admin-layout.component';

export const routes: Routes = [
  {
    path: '',
    component: ShopLayoutComponent,
    children: [
      {
        path: '',
        loadComponent: () => import('./features/discovery/components/home/home.component').then(m => m.HomeComponent)
      },
      {
        path: 'cart',
        loadComponent: () => import('./features/cart/components/cart-page/cart-page.component').then(m => m.CartPageComponent)
      },
      {
        path: 'payment/success',
        loadComponent: () => import('./features/payment/components/payment-success/payment-success.component').then(m => m.PaymentSuccessComponent)
      },
      {
        path: 'payment/cancel',
        loadComponent: () => import('./features/payment/components/payment-cancel/payment-cancel.component').then(m => m.PaymentCancelComponent)
      },
      {
        path: 'payment/:orderId',
        loadComponent: () => import('./features/payment/components/payment-page/payment-page.component').then(m => m.PaymentPageComponent)
      }
    ]
  },
  {
    path: '',
    component: AdminLayoutComponent,
    children: [
      {
        path: 'products',
        loadComponent: () => import('./features/products/components/product-list/product-list.component').then(m => m.ProductListComponent)
      },
      {
        path: 'products/settings',
        loadComponent: () => import('./features/products/components/setting-list/setting-list.component').then(m => m.SettingListComponent)
      },
      {
        path: 'inventory',
        loadComponent: () => import('./features/inventory/components/inventory-list/inventory-list.component').then(m => m.InventoryListComponent)
      }
    ]
  },
  { path: '**', redirectTo: '' }
];
