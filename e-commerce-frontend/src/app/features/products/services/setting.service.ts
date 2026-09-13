import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { SettingGottenView, CreateSettingResource, UpdateSettingResource } from '../models/setting.model';

@Injectable({
  providedIn: 'root'
})
export class SettingService {

  private apiUrl = '/api/settings';

  constructor(private http: HttpClient) { }

  getSettings(tenantId?: string, dataType?: string, type?: string, name?: string, activeFlag?: string): Observable<SettingGottenView[]> {
    let params = new HttpParams();
    
    if (tenantId) params = params.set('tenantId', tenantId);
    if (dataType) params = params.set('dataType', dataType);
    if (type) params = params.set('type', type);
    if (name) params = params.set('name', name);
    if (activeFlag) params = params.set('activeFlag', activeFlag);

    return this.http.get<{code: string, message: string, data: SettingGottenView[]}>(this.apiUrl, { params }).pipe(
      map(res => res.data)
    );
  }

  createSetting(setting: CreateSettingResource): Observable<number> {
    return this.http.post<number>(this.apiUrl, setting);
  }

  updateSetting(id: number, setting: UpdateSettingResource): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/${id}`, setting);
  }

  deleteSetting(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
