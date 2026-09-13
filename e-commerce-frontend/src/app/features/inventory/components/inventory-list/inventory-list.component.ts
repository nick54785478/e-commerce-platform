import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ProductService } from '../../../products/services/product.service';
import { ProductPageQueriedView, ProductQueriedView } from '../../../products/models/product.model';
import { StockAdjustmentModalComponent } from '../stock-adjustment-modal/stock-adjustment-modal.component';

import { PaginatorModule } from 'primeng/paginator';
import { TableModule } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { InputTextModule } from 'primeng/inputtext';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-inventory-list',
  standalone: true,
  imports: [CommonModule, FormsModule, PaginatorModule, TableModule, ButtonModule, InputTextModule, StockAdjustmentModalComponent],
  templateUrl: './inventory-list.component.html',
  styleUrls: ['./inventory-list.component.css']
})
export class InventoryListComponent implements OnInit {
  pageView: ProductPageQueriedView | null = null;
  loading = false;

  pageSize = 10;
  first = 0;

  showModal = false;
  selectedProduct: ProductQueriedView | null = null;
  searchName: string = '';

  constructor(private productService: ProductService) { }

  ngOnInit(): void {
    this.loadProducts();
  }

  loadProducts(page = 0): void {
    this.loading = true;
    this.productService.getProducts(page, this.pageSize, this.searchName).subscribe({
      next: (data) => {
        this.pageView = data;
        this.first = page * this.pageSize;
        this.loading = false;
      },
      error: (err) => {
        console.error('Failed to load products for inventory', err);
        this.loading = false;
      }
    });
  }

  onPageChange(event: any) {
    this.loadProducts(event.page);
  }

  onSearch(): void {
    this.loadProducts(0);
  }

  openAdjustmentModal(product: ProductQueriedView): void {
    this.selectedProduct = product;
    this.showModal = true;
  }

  closeModal(result: any): void {
    this.showModal = false;
    
    if (result && result.action) {
      const quantity = result.quantity;
      const action = result.action; // 'ADD' or 'REDUCE'
      const productToUpdate = this.selectedProduct!;
      
      // Optimistic update
      const originalStock = productToUpdate.stock;
      if (action === 'ADD') {
        productToUpdate.stock += quantity;
      } else if (action === 'REDUCE') {
        productToUpdate.stock -= quantity;
      }

      const apiCall = action === 'ADD' 
          ? this.productService.addStock(productToUpdate.productId, quantity)
          : this.productService.reduceStock(productToUpdate.productId, quantity);

      apiCall.subscribe({
        next: () => {
          // Success, optimistic update is already applied
        },
        error: (err) => {
          // Revert optimistic update
          productToUpdate.stock = originalStock;
          alert('Failed to adjust stock: ' + (err.error?.message || err.message));
        }
      });
    }
    
    this.selectedProduct = null;
  }
}
