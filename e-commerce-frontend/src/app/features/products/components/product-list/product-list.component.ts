import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ProductService } from '../../services/product.service';
import { ProductPageQueriedView, ProductQueriedView, ProductStatus } from '../../models/product.model';
// Standalone component for creating and editing products
import { ProductFormModalComponent } from '../product-form-modal/product-form-modal.component';
import { PaginatorModule } from 'primeng/paginator';
import { SelectModule } from 'primeng/select';
import { FormsModule } from '@angular/forms';
import { SettingService } from '../../services/setting.service';
import { YesNo } from '../../models/setting.model';

/**
 * 商品列表元件 (ProductListComponent)
 * 負責顯示商品的清單畫面，並提供新增、編輯與上下架狀態切換的入口。
 */
@Component({
  selector: 'app-product-list',
  standalone: true,
  imports: [CommonModule, ProductFormModalComponent, PaginatorModule, SelectModule, FormsModule],
  templateUrl: './product-list.component.html',
  styleUrls: ['./product-list.component.css']
})
export class ProductListComponent implements OnInit {
  
  /**
   * 當前商品分頁的視圖資料，包含商品列表與分頁資訊。
   */
  pageView: ProductPageQueriedView | null = null;
  
  /**
   * 指示目前是否正在向後端載入資料中。
   */
  loading = false;

  // Pagination state
  pageSize = 10;
  first = 0;

  // Modal State
  /**
   * 控制商品新增/編輯 Modal 視窗的顯示與隱藏。
   */
  showModal = false;
  
  /**
   * 當前選中準備編輯的商品。若為 null 則代表處於「新增商品」模式。
   */
  selectedProduct: ProductQueriedView | null = null;

  // Filters
  categories: any[] = [];
  selectedType: string | undefined;

  types: any[] = [];
  selectedSubType: string | undefined;
  selectedStatus: string | undefined;

  statusOptions = [
    { label: 'Active', value: 'ACTIVE' },
    { label: 'Inactive', value: 'INACTIVE' }
  ];

  constructor(
    private productService: ProductService,
    private settingService: SettingService
  ) { }

  /**
   * Angular 元件生命週期：初始化時載入第一頁的商品。
   */
  ngOnInit(): void {
    this.loadTypes();
    this.loadProducts();
  }

  loadTypes(): void {
    // 取得所有的 Type (dataType = SYSTEM, type = PRODUCT_TYPE)
    this.settingService.getSettings(undefined, 'SYSTEM', 'PRODUCT_TYPE', undefined, YesNo.Y).subscribe({
      next: (data) => {
        this.categories = data.map(item => ({ label: item.name, value: item.code }));
      },
      error: (err) => console.error('Failed to load product types', err)
    });
  }

  onCategoryChange(): void {
    this.selectedSubType = undefined;
    this.updateTypeOptions();
    this.loadProducts(0);
  }

  updateTypeOptions(): void {
    if (this.selectedType) {
      // 根據選中的 Type，查詢對應的 Sub Type (dataType = PRODUCT_TYPE, type = selectedType)
      this.settingService.getSettings(undefined, 'PRODUCT_TYPE', this.selectedType, undefined, YesNo.Y).subscribe({
        next: (data) => {
          this.types = data.map(item => ({ label: item.name, value: item.code }));
        },
        error: (err) => console.error('Failed to load product sub types', err)
      });
    } else {
      this.types = [];
    }
  }

  onFilterChange(): void {
    this.loadProducts(0);
  }

  /**
   * 向後端 API 請求載入指定頁數的商品列表。
   * 
   * @param page 要載入的頁碼 (從 0 開始，預設為 0)
   */
  loadProducts(page = 0): void {
    this.loading = true;
    this.productService.getProducts(page, this.pageSize, undefined, this.selectedType, this.selectedSubType, this.selectedStatus).subscribe({
      next: (data) => {
        this.pageView = data;
        this.first = page * this.pageSize;
        this.loading = false;
      },
      error: (err) => {
        console.error('Failed to load products', err);
        this.loading = false;
      }
    });
  }

  onPageChange(event: any) {
    this.loadProducts(event.page);
  }

  /**
   * 開啟「新增商品」的 Modal 視窗。
   */
  openCreateModal(): void {
    this.selectedProduct = null;
    this.showModal = true;
  }

  /**
   * 開啟「編輯商品」的 Modal 視窗，並傳入欲編輯的商品資料。
   * 
   * @param product 準備被編輯的商品檢視物件
   */
  openEditModal(product: ProductQueriedView): void {
    this.selectedProduct = product;
    this.showModal = true;
  }

  /**
   * 關閉商品 Modal 視窗。
   * 
   * @param refresh 若傳入 true，表示資料有異動，關閉視窗後會重新載入當前頁面的商品列表
   */
  closeModal(result: any): void {
    this.showModal = false;
    this.selectedProduct = null;
    
    if (result === true) {
      this.loadProducts(this.pageView?.currentPage || 0);
    } else if (result && result.action === 'CREATE') {
      const createData = result.data;
      
      // 1. Optimistic Add: 先在畫面上插入一筆假的商品資料
      const optimisticProduct: ProductQueriedView = {
        productId: 'temp-' + Date.now(),
        tenantId: '',
        name: createData.name,
        description: createData.description,
        type: createData.type,
        subType: createData.subType,
        price: createData.price,
        stock: 0, // 新建立的商品預設庫存為 0
        version: 0,
        imageUrls: createData.imageUrls,
        tags: createData.tags || [],
        status: ProductStatus.ACTIVE,
        available: true
      };
      
      if (!this.pageView) {
        this.pageView = { content: [], totalElements: 0, totalPages: 1, currentPage: 0 };
      }
      this.pageView.content = [optimisticProduct, ...this.pageView.content];
      
      // 2. 實際發送 API 請求
      this.productService.createProduct(createData).subscribe({
        next: () => {
          // 成功後重新載入列表，以取得後端產生的真實 productId
          this.loadProducts(this.pageView?.currentPage || 0);
        },
        error: (err) => {
          // 失敗時，復原畫面 (將剛剛加入的假商品移除)
          this.pageView!.content = this.pageView!.content.filter(p => p.productId !== optimisticProduct.productId);
          alert('Create failed: ' + (err.error?.message || err.message));
        }
      });
    } else if (result && result.action === 'UPDATE') {
      const updateData = result.data;
      const originalProduct: ProductQueriedView = result.original;
      
      // 1. Optimistic Update: 先在畫面上更新該筆資料
      if (this.pageView) {
        const index = this.pageView.content.findIndex(p => p.productId === originalProduct.productId);
        if (index !== -1) {
          this.pageView.content[index] = {
            ...this.pageView.content[index],
            name: updateData.name,
            description: updateData.description,
            type: updateData.type,
            subType: updateData.subType,
            price: updateData.price,
            imageUrls: updateData.imageUrls,
            tags: updateData.tags || [],
            version: updateData.version + 1 // 樂觀地預先增加版本號
          };
        }
      }
      
      // 2. 實際發送 API 請求
      this.productService.updateProduct(originalProduct.productId, updateData).subscribe({
        next: () => {
          // 樂觀更新已於上方完成，此處不再呼叫 loadProducts() 以免被尚未支援 tags 的後端覆蓋
        },
        error: (err) => {
          // 失敗時，復原畫面
          if (this.pageView) {
            const index = this.pageView.content.findIndex(p => p.productId === originalProduct.productId);
            if (index !== -1) {
              this.pageView.content[index] = originalProduct;
            }
          }
          alert('Update failed: ' + (err.error?.message || err.message));
          this.loadProducts(this.pageView?.currentPage || 0);
        }
      });
    }
  }

  /**
   * 切換商品的狀態 (上架 / 下架)。
   * 採用樂觀更新 (Optimistic Update) 策略：先更新前端畫面，若後端 API 報錯再復原。
   * 
   * @param product 要切換狀態的商品物件
   */
  toggleStatus(product: ProductQueriedView): void {
    const newStatus = product.status === ProductStatus.ACTIVE ? ProductStatus.INACTIVE : ProductStatus.ACTIVE;
    this.productService.changeStatus(product.productId, { version: product.version, status: newStatus }).subscribe({
      next: () => {
        // Optimistic update
        product.status = newStatus;
        product.version += 1;
      },
      error: (err) => {
        alert('Failed to update status: ' + (err.error?.message || err.message));
        this.loadProducts(this.pageView?.currentPage || 0); // revert
      }
    });
  }

  /**
   * 取得商品圖片載入失敗時的預設 (Fallback) SVG 圖片。
   * 
   * @returns Base64 編碼的預設 SVG 圖片字串
   */
  get fallbackImage(): string {
    return 'data:image/svg+xml;charset=UTF-8,%3Csvg%20width%3D%22200%22%20height%3D%22200%22%20xmlns%3D%22http%3A%2F%2Fwww.w3.org%2F2000%2Fsvg%22%20viewBox%3D%220%200%20200%20200%22%20preserveAspectRatio%3D%22none%22%3E%3Cdefs%3E%3Cstyle%20type%3D%22text%2Fcss%22%3E%23holder_1899%20text%20%7B%20fill%3A%239494a0%3Bfont-weight%3A500%3Bfont-family%3AInter%2C%20sans-serif%3Bfont-size%3A14pt%20%7D%20%3C%2Fstyle%3E%3C%2Fdefs%3E%3Cg%20id%3D%22holder_1899%22%3E%3Crect%20width%3D%22200%22%20height%3D%22200%22%20fill%3D%22%23191923%22%3E%3C%2Frect%3E%3Cg%3E%3Ctext%20x%3D%2260%22%20y%3D%22105%22%3ENo%20Image%3C%2Ftext%3E%3C%2Fg%3E%3C%2Fg%3E%3C%2Fsvg%3E';
  }

  /**
   * 處理圖片載入錯誤事件 (HTML img 標籤的 error 事件)。
   * 將出錯的圖片 URL 替換為 fallbackImage。
   * 
   * @param event 原生 DOM 事件
   */
  handleImageError(event: any) {
    event.target.src = this.fallbackImage;
  }
}
