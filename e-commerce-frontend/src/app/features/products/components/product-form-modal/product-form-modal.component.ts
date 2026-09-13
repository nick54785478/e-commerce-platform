import { Component, Input, Output, EventEmitter, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ProductService } from '../../services/product.service';
import { ProductQueriedView, CreateProductResource, UpdateProductResource } from '../../models/product.model';

import { DialogModule } from 'primeng/dialog';
import { ButtonModule } from 'primeng/button';
import { InputTextModule } from 'primeng/inputtext';
import { InputNumberModule } from 'primeng/inputnumber';
import { AutoCompleteModule } from 'primeng/autocomplete';
import { SelectModule } from 'primeng/select';
import { SettingService } from '../../services/setting.service';

/**
 * 商品表單 Modal 元件 (ProductFormModalComponent)
 * 提供共用的商品新增與編輯表單，整合了圖片上傳功能與前端資料驗證機制。
 */
@Component({
  selector: 'app-product-form-modal',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule, DialogModule, ButtonModule, InputTextModule, InputNumberModule, AutoCompleteModule, SelectModule],
  templateUrl: './product-form-modal.component.html',
  styleUrls: ['./product-form-modal.component.css']
})
export class ProductFormModalComponent implements OnInit {
  
  /**
   * 控制對話框顯示狀態 (雙向綁定給 p-dialog 使用，讓右上角 x 按鈕能正常關閉)
   */
  isVisible = true;

  /**
   * 接收外部傳入的商品資料。
   * 若有值，表示此表單為「編輯模式」；若為 null，則表示「新增模式」。
   */
  @Input() product: ProductQueriedView | null = null;
  
  /**
   * 當表單關閉時對外發出的事件。
   * 會傳遞一個 boolean 值，或傳遞一個包含 action 與 data 的 payload 用於樂觀更新。
   */
  @Output() close = new EventEmitter<any>();

  /**
   * 響應式表單群組，用於管理輸入欄位與驗證狀態。
   */
  form!: FormGroup;

  categoryOptions: { label: string, value: string }[] = [];
  subTypeOptions: { label: string, value: string }[] = [];
  allSettings: any[] = [];
  
  /**
   * 指示當前是否正在送出表單至後端，避免重複點擊提交。
   */
  isSubmitting = false;
  
  /**
   * 儲存已上傳的圖片 URL 列表。
   */
  imageUrls: string[] = [];
  
  /**
   * 指示當前是否正在上傳圖片中，用於控制畫面上的 Loading 狀態。
   */
  isUploading = false;

  constructor(
    private fb: FormBuilder,
    private productService: ProductService,
    private settingService: SettingService
  ) {}

  /**
   * Angular 元件生命週期：初始化表單 (FormGroup)。
   * 依據是否為編輯模式，給予不同的預設值與驗證規則 (例如庫存數量在編輯時不可更改)。
   */
  ngOnInit(): void {
    const isEdit = !!this.product;
    this.imageUrls = this.product?.imageUrls ? [...this.product.imageUrls] : [];

    this.loadProductTypes();

    this.form = this.fb.group({
      name: [this.product?.name || '', [Validators.required, Validators.maxLength(100)]],
      description: [this.product?.description || '', [Validators.maxLength(500)]],
      type: [this.product?.type || '', Validators.required],
      subType: [this.product?.subType || '', Validators.required],
      price: [this.product?.price || '', [Validators.required, Validators.min(0.01)]],
      tags: [this.product?.tags ? [...this.product.tags] : []]
    });

    // 監聽 Data Type 變化來更新 Product Type 選項
    this.form.get('type')?.valueChanges.subscribe(type => {
      this.updateSubTypeOptions(type);
      if (!this.product || this.product.type !== type) {
        this.form.get('subType')?.setValue('');
      }
    });
  }

  /**
   * Load active Product Types from Setting API
   */
  private loadProductTypes(): void {
    this.settingService.getSettings('TTRAVEL', 'PRODUCT_TYPE', undefined, undefined, 'Y')
      .subscribe({
        next: (settings) => {
          this.allSettings = settings;
          const uniqueCategories = [...new Set(settings.map(item => item.type))];
          this.categoryOptions = uniqueCategories.map(c => ({ label: c as string, value: c as string }));
          
          if (this.product?.type) {
            this.updateSubTypeOptions(this.product.type);
          }
        },
        error: (err) => {
          console.error('Failed to load product types', err);
        }
      });
  }

  private updateSubTypeOptions(type: string): void {
    if (type) {
      const filtered = this.allSettings.filter(item => item.type === type);
      this.subTypeOptions = filtered.map(item => ({ label: item.name, value: item.code }));
    } else {
      this.subTypeOptions = [];
    }
  }

  /**
   * 判斷當前表單是否處於「編輯模式」。
   * 
   * @returns true: 編輯模式, false: 新增模式
   */
  get isEditMode(): boolean {
    return !!this.product;
  }

  /**
   * 處理使用者選擇檔案 (上傳圖片) 的事件。
   * 會呼叫後端 API 將圖片上傳至 MinIO，成功後將 URL 塞入 imageUrls 列表。
   * 
   * @param event 原生 input file 選擇事件
   */
  onFileSelected(event: any): void {
    const file: File = event.target.files[0];
    if (file) {
      this.isUploading = true;
      this.productService.uploadImage(file).subscribe({
        next: (url) => {
          this.imageUrls.push(url);
          this.isUploading = false;
        },
        error: (err) => {
          console.error('Image upload failed', err);
          alert('Image upload failed.');
          this.isUploading = false;
        }
      });
    }
  }

  /**
   * 移除已上傳的圖片。
   * 
   * @param index 要移除的圖片在 imageUrls 陣列中的索引
   */
  removeImage(index: number): void {
    this.imageUrls.splice(index, 1);
  }

  /**
   * 提交表單的事件處理。
   * 依據是否為編輯模式，組裝對應的 Resource Payload 並發送 API 請求。
   * 請求成功後會觸發 close 事件並帶上 true 要求刷新列表。
   */
  onSubmit(): void {
    if (this.form.invalid || this.isSubmitting) return;

    this.isSubmitting = true;
    const formValue = this.form.getRawValue(); // 使用 getRawValue 避免被 disabled 的欄位值遺失

    if (this.isEditMode && this.product) {
      const updateData: UpdateProductResource = {
        version: this.product.version,
        name: formValue.name,
        description: formValue.description,
        type: formValue.type,
        subType: formValue.subType,
        price: formValue.price,
        imageUrls: this.imageUrls,
        tags: formValue.tags
      };
      
      // 將更新請求的 Payload 傳遞給父元件進行「樂觀更新 (Optimistic Update)」
      this.close.emit({ action: 'UPDATE', data: updateData, original: this.product } as any);
    } else {
      const createData: CreateProductResource = {
        name: formValue.name,
        description: formValue.description,
        type: formValue.type,
        subType: formValue.subType,
        price: formValue.price,
        imageUrls: this.imageUrls,
        tags: formValue.tags || []
      };

      // 將新增請求的 Payload 傳遞給父元件進行「樂觀更新 (Optimistic Update)」
      this.close.emit({ action: 'CREATE', data: createData } as any);
    }
  }

  /**
   * 處理取消按鈕點擊事件，觸發 close 事件，且不要求刷新列表。
   */
  onCancel(): void {
    this.close.emit(false);
  }
}
