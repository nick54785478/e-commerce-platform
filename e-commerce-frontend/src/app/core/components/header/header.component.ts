import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { InputTextModule } from 'primeng/inputtext';
import { ButtonModule } from 'primeng/button';
import { MenubarModule } from 'primeng/menubar';
import { IconFieldModule } from 'primeng/iconfield';
import { InputIconModule } from 'primeng/inputicon';
import { SidebarModule } from 'primeng/sidebar';
import { BadgeModule } from 'primeng/badge';
import { ApiService, RecommendationResponse } from '../../../features/discovery/services/api.service';
import { ProductService } from '../../../features/products/services/product.service';
import { ProductQueriedView } from '../../../features/products/models/product.model';
import { catchError, map, switchMap } from 'rxjs/operators';
import { forkJoin, of, Observable } from 'rxjs';
import { CartService } from '../../../features/cart/services/cart.service';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [CommonModule, RouterModule, InputTextModule, ButtonModule, MenubarModule, IconFieldModule, InputIconModule, SidebarModule, BadgeModule],
  templateUrl: './header.component.html',
  styleUrl: './header.component.css'
})
export class HeaderComponent {
  showRecommendations = false;
  loadingRecommendations = false;
  recommendedProducts: ProductQueriedView[] = [];
  cartTotalCount$: Observable<number>;

  constructor(
    private apiService: ApiService,
    private productService: ProductService,
    private cartService: CartService
  ) {
    this.cartTotalCount$ = this.cartService.cartTotalCount$;
  }

  openRecommendations() {
    this.showRecommendations = true;
    if (this.recommendedProducts.length === 0) {
      this.loadRecommendations();
    }
  }

  loadRecommendations() {
    this.loadingRecommendations = true;
    const testUserId = 'nickgh@example.com';
    this.apiService.getRecommendations(testUserId).pipe(
      switchMap((response: RecommendationResponse) => {
        if (!response.items || response.items.length === 0) {
          return of([]);
        }
        const itemIds = response.items.slice(0, 5).map((item: any) => item.itemId);
        const productRequests = itemIds.map((id: string) => 
          this.productService.getProductById(id).pipe(
            catchError(err => {
              console.warn(`Failed to fetch product ${id}`, err);
              return of(null);
            })
          )
        );
        return forkJoin(productRequests);
      }),
      map((products: (ProductQueriedView | null)[]) => products.filter((p: ProductQueriedView | null): p is ProductQueriedView => p !== null))
    ).subscribe({
      next: (validProducts: ProductQueriedView[]) => {
        this.recommendedProducts = validProducts;
        this.loadingRecommendations = false;
      },
      error: (err: any) => {
        console.error('Failed to load recommendations', err);
        this.loadingRecommendations = false;
      }
    });
  }

  get fallbackImage(): string {
    return 'data:image/svg+xml;charset=UTF-8,%3Csvg%20width%3D%22200%22%20height%3D%22200%22%20xmlns%3D%22http%3A%2F%2Fwww.w3.org%2F2000%2Fsvg%22%20viewBox%3D%220%200%20200%20200%22%20preserveAspectRatio%3D%22none%22%3E%3Cdefs%3E%3Cstyle%20type%3D%22text%2Fcss%22%3E%23holder_1899%20text%20%7B%20fill%3A%239494a0%3Bfont-weight%3A500%3Bfont-family%3AInter%2C%20sans-serif%3Bfont-size%3A14pt%20%7D%20%3C%2Fstyle%3E%3C%2Fdefs%3E%3Cg%20id%3D%22holder_1899%22%3E%3Crect%20width%3D%22200%22%20height%3D%22200%22%20fill%3D%22%23191923%22%3E%3C%2Frect%3E%3Cg%3E%3Ctext%20x%3D%2260%22%20y%3D%22105%22%3ENo%20Image%3C%2Ftext%3E%3C%2Fg%3E%3C%2Fg%3E%3C%2Fsvg%3E';
  }
}
