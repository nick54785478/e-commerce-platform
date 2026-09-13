import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ProductService } from '../../../products/services/product.service';
import { FavoriteService } from '../../../../core/services/favorite.service';
import { SystemMessageService } from '../../../../core/services/system-message.service';
import { ProductQueriedView } from '../../../products/models/product.model';
import { ApiService, RecommendationResponse } from '../../services/api.service';
import { forkJoin, of } from 'rxjs';
import { catchError, map, switchMap } from 'rxjs/operators';
import { CardModule } from 'primeng/card';
import { ButtonModule } from 'primeng/button';
import { TagModule } from 'primeng/tag';
import { DialogModule } from 'primeng/dialog';
import { PaginatorModule } from 'primeng/paginator';
import { SelectModule } from 'primeng/select';
import { FormsModule } from '@angular/forms';
import { SettingService } from '../../../products/services/setting.service';
import { CartService } from '../../../cart/services/cart.service';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, FormsModule, CardModule, ButtonModule, TagModule, DialogModule, PaginatorModule, SelectModule],
  templateUrl: './home.component.html',
  styleUrl: './home.component.css'
})
export class HomeComponent implements OnInit {
  products: ProductQueriedView[] = [];
  recommendedProducts: ProductQueriedView[] = [];
  selectedProduct: ProductQueriedView | null = null;
  displayViewDialog: boolean = false;
  favoriteProductIds: Set<string> = new Set<string>();

  // Pagination state
  totalRecords: number = 0;
  pageSize: number = 6;
  first: number = 0;

  // Filter state
  categories: any[] = [];
  selectedCategory: string = '';

  openViewDialog(product: ProductQueriedView) {
    this.selectedProduct = product;
    this.displayViewDialog = true;
    
    // Log VIEW behavior
    const testUserId = 'nickgh@example.com';
    this.apiService.logBehavior(testUserId, product.productId, 'VIEW').subscribe({
      error: (err) => console.error('Failed to log VIEW behavior', err)
    });
  }

  constructor(
    private productService: ProductService,
    private favoriteService: FavoriteService,
    private systemMessageService: SystemMessageService,
    private apiService: ApiService,
    private settingService: SettingService,
    private cartService: CartService
  ) { }

  ngOnInit(): void {
    // Fetch initial products
    this.loadProducts(0);
    
    // Fetch categories and group them by Type and SubType
    this.settingService.getSettings('TTRAVEL', undefined, undefined, undefined, 'Y').subscribe({
      next: (settings) => {
        const topLevel = settings.filter(s => s.type === 'PRODUCT_TYPE');
        this.categories = topLevel.map(parent => ({
          label: parent.name,
          value: parent.code,
          items: settings
            .filter(s => s.type === parent.code)
            .map(sub => ({ label: sub.name, value: sub.code }))
        }));
      },
      error: (err) => console.error('Failed to load categories', err)
    });

    // Fetch recommendations for test user
    this.loadRecommendations();

    // Fetch user favorites
    this.favoriteService.getFavorites().subscribe({
      next: (favorites) => {
        favorites.forEach(f => this.favoriteProductIds.add(f.productId));
      },
      error: (err) => console.error('Failed to load favorites', err)
    });
  }

  toggleFavorite(event: Event, product: ProductQueriedView) {
    event.stopPropagation();
    if (this.favoriteProductIds.has(product.productId)) {
      this.favoriteService.removeFavorite(product.productId).subscribe({
        next: () => {
          this.favoriteProductIds.delete(product.productId);
          this.systemMessageService.showSuccess('已移除', `已將 ${product.name} 從最愛中移除`);
          
          const testUserId = 'nickgh@example.com';
          this.apiService.logBehavior(testUserId, product.productId, 'UNFAVORITE').subscribe({
            error: (err) => console.error('Failed to log UNFAVORITE behavior', err)
          });
        },
        error: (err) => {
          this.systemMessageService.showError('發生錯誤', '無法移除最愛');
          console.error(err);
        }
      });
    } else {
      this.favoriteService.addFavorite(product.productId).subscribe({
        next: () => {
          this.favoriteProductIds.add(product.productId);
          this.systemMessageService.showSuccess('已加入', `已將 ${product.name} 加入最愛`);
          
          const testUserId = 'nickgh@example.com';
          this.apiService.logBehavior(testUserId, product.productId, 'FAVORITE').subscribe({
            error: (err) => console.error('Failed to log FAVORITE behavior', err)
          });
        },
        error: (err) => {
          this.systemMessageService.showError('發生錯誤', '無法加入最愛');
          console.error(err);
        }
      });
    }
  }

  addToCart(product: ProductQueriedView) {
    if (product.stock > 0) {
      this.cartService.addToCart({
        productId: product.productId,
        name: product.name,
        price: product.price,
        imageUrl: product.imageUrls && product.imageUrls.length > 0 ? product.imageUrls[0] : '',
        quantity: 1,
        stock: product.stock
      });
      this.systemMessageService.showSuccess('Cart Updated', `${product.name} added to cart.`);
      this.displayViewDialog = false; // Optional: close dialog after adding
    } else {
      this.systemMessageService.showError('Out of Stock', `${product.name} is currently out of stock.`);
    }
  }

  loadProducts(page: number) {
    this.productService.getProducts(page, this.pageSize, undefined, undefined, this.selectedCategory || undefined).subscribe({
      next: (data) => {
        this.products = data.content || [];
        this.totalRecords = data.totalElements || 0;
        this.first = page * this.pageSize;
      },
      error: (err) => {
        console.error('Failed to load products', err);
      }
    });
  }

  loadRecommendations() {
    const testUserId = 'nickgh@example.com'; // Using the requested test user ID
    this.apiService.getRecommendations(testUserId).pipe(
      switchMap((response: RecommendationResponse) => {
        if (!response.items || response.items.length === 0) {
          return of([]);
        }
        // Take top 6 recommended item IDs
        const itemIds = response.items.slice(0, 6).map(item => item.itemId);
        
        // Fetch product details for each ID in parallel
        const productRequests = itemIds.map(id => 
          this.productService.getProductById(id).pipe(
            catchError(err => {
              console.warn(`Failed to fetch product ${id}`, err);
              return of(null);
            })
          )
        );
        
        return forkJoin(productRequests);
      }),
      map((products) => products.filter((p): p is ProductQueriedView => p !== null))
    ).subscribe({
      next: (validProducts) => {
        this.recommendedProducts = validProducts;
      },
      error: (err) => {
        console.error('Failed to load recommendations', err);
      }
    });
  }

  onPageChange(event: any) {
    // event.page is the 0-based page number from PrimeNG Paginator
    this.loadProducts(event.page);
  }

  onCategoryChange() {
    this.first = 0;
    this.loadProducts(0);
  }

  // Fallback image generator
  getFallbackImage(): string {
    return 'data:image/svg+xml;charset=UTF-8,%3Csvg%20width%3D%22200%22%20height%3D%22200%22%20xmlns%3D%22http%3A%2F%2Fwww.w3.org%2F2000%2Fsvg%22%20viewBox%3D%220%200%20200%20200%22%20preserveAspectRatio%3D%22none%22%3E%3Cdefs%3E%3Cstyle%20type%3D%22text%2Fcss%22%3E%23holder_1899%20text%20%7B%20fill%3A%239494a0%3Bfont-weight%3A500%3Bfont-family%3AInter%2C%20sans-serif%3Bfont-size%3A14pt%20%7D%20%3C%2Fstyle%3E%3C%2Fdefs%3E%3Cg%20id%3D%22holder_1899%22%3E%3Crect%20width%3D%22200%22%20height%3D%22200%22%20fill%3D%22%23191923%22%3E%3C%2Frect%3E%3Cg%3E%3Ctext%20x%3D%2260%22%20y%3D%22105%22%3ENo%20Image%3C%2Ftext%3E%3C%2Fg%3E%3C%2Fg%3E%3C%2Fsvg%3E';
  }
}
