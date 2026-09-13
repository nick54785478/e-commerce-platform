import { HttpInterceptorFn } from '@angular/common/http';

export const tenantInterceptor: HttpInterceptorFn = (req, next) => {
  // 對於所有 /api 請求，自動加上 X-Tenant-ID 標頭
  if (req.url.startsWith('/api')) {
    const tenantReq = req.clone({
      setHeaders: {
        'X-Tenant-ID': 'TTRAVEL'
      }
    });
    return next(tenantReq);
  }
  return next(req);
};
