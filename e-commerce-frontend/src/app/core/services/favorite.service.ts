import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { StorageService } from './storage.service';
import { SystemStorageKey } from '../enums/system-storage-key.enum';

@Injectable({
  providedIn: 'root'
})
export class FavoriteService {

  private readonly apiUrl = '/api/favorites';

  constructor(
    private http: HttpClient,
    private storageService: StorageService
  ) { }

  private getHeaders(): HttpHeaders {
    let userId = 'nickgh@example.com';
    // let userId = this.storageService.getLocalStorageItem(SystemStorageKey.USERNAME);
    // if (!userId) {
    //   userId = 'nickgh@example.com';
    // }

    let tenantId = 'TTRAVEL';
    // let tenantId = this.storageService.getLocalStorageItem(SystemStorageKey.TENANT);
    // if (!tenantId) {
    //   tenantId = 'default-tenant';
    // }

    return new HttpHeaders({
      'X-User-Id': userId,
      'X-Tenant-Id': tenantId
    });
  }

  addFavorite(productId: string): Observable<any> {
    return this.http.post(`${this.apiUrl}/${productId}`, {}, { headers: this.getHeaders(), responseType: 'text' });
  }

  removeFavorite(productId: string): Observable<any> {
    return this.http.delete(`${this.apiUrl}/${productId}`, { headers: this.getHeaders(), responseType: 'text' });
  }

  getFavorites(): Observable<any[]> {
    return this.http.get<any[]>(this.apiUrl, { headers: this.getHeaders() });
  }
}
