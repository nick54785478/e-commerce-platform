import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { 
  ProductPageQueriedView, 
  ProductQueriedView, 
  CreateProductResource, 
  UpdateProductResource, 
  ChangeProductStatusResource 
} from '../models/product.model';

/**
 * 產品服務 (Product Service)
 * 負責與後端 API 進行產品相關的通訊與資料存取。
 */
@Injectable({
  providedIn: 'root'
})
export class ProductService {
  /** 後端 API 基礎路徑 */
  private apiUrl = '/api/products';

  constructor(private http: HttpClient) {}

  /**
   * 取得產品分頁列表 (Get product pagination list)
   * 
   * @param page 當前頁碼 (從 0 開始)
   * @param size 每頁筆數 (預設為 10)
   * @param name 選填的產品名稱搜尋關鍵字
   * @param type 選填的產品種類搜尋關鍵字
   * @param subType 選填的產品次種類搜尋關鍵字
   * @param status 選填的產品狀態過濾 (ACTIVE, INACTIVE)
   * @returns 回傳包含產品列表及分頁資訊的 Observable
   */
  getProducts(page: number = 0, size: number = 10, name?: string, type?: string, subType?: string, status?: string): Observable<ProductPageQueriedView> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    
    if (name) {
      params = params.set('name', name);
    }
    
    if (type) {
      params = params.set('type', type);
    }

    if (subType) {
      params = params.set('subType', subType);
    }

    if (status) {
      params = params.set('status', status);
    }

    return this.http.get<ProductPageQueriedView>(`${this.apiUrl}/summary`, { 
      params 
    });
  }

  /**
   * 根據 ID 取得單一產品詳細資訊 (Get product by ID)
   * 
   * @param id 產品的唯一識別碼 (Product ID)
   * @returns 回傳單一產品詳細資訊的 Observable
   */
  getProductById(id: string): Observable<ProductQueriedView> {
    return this.http.get<ProductQueriedView>(`${this.apiUrl}/${id}`);
  }

  /**
   * 建立新產品 (Create a new product)
   * 
   * @param data 建立產品所需的 Payload (名稱、價格與圖片)
   * @returns 回傳後端處理結果的 Observable
   */
  createProduct(data: CreateProductResource): Observable<any> {
    return this.http.post(this.apiUrl, data);
  }

  /**
   * 增加庫存 (Add stock for a product)
   */
  addStock(productId: string, quantity: number): Observable<any> {
    const params = new HttpParams().set('quantity', quantity.toString());
    return this.http.post(`${this.apiUrl}/${productId}/stock/add`, null, { params });
  }

  /**
   * 扣減庫存 (Reduce stock for a product)
   */
  reduceStock(productId: string, quantity: number): Observable<any> {
    const params = new HttpParams().set('quantity', quantity.toString());
    return this.http.post(`${this.apiUrl}/${productId}/stock/reduce`, null, { params });
  }

  /**
   * 更新現有產品 (Update an existing product)
   * 
   * @param id 要更新的產品 ID
   * @param data 更新產品所需的 Payload (包含版本控制與可變更的欄位)
   * @returns 回傳後端處理結果的 Observable
   */
  updateProduct(id: string, data: UpdateProductResource): Observable<any> {
    return this.http.put(`${this.apiUrl}/${id}`, data);
  }

  /**
   * 變更產品狀態 (Change product status)
   * 
   * @param id 產品 ID
   * @param data 包含新狀態與版本控制的 Payload
   * @returns 回傳後端處理結果的 Observable
   */
  changeStatus(id: string, data: ChangeProductStatusResource): Observable<any> {
    return this.http.put(`${this.apiUrl}/${id}/status`, data);
  }

  /**
   * 上傳產品圖片 (Upload product image)
   * 將圖片檔案以 FormData 的形式上傳至後端。
   * 
   * @param file 選擇的圖片檔案
   * @returns 回傳圖片 URL (純文字字串) 的 Observable
   */
  uploadImage(file: File): Observable<string> {
    const formData = new FormData();
    formData.append('file', file);
    // 回傳是純文字的 URL，因此 responseType 設為 text
    return this.http.post(`${this.apiUrl}/upload-image`, formData, { 
      responseType: 'text' 
    });
  }
}
