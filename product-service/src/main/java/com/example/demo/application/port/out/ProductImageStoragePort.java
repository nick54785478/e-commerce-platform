package com.example.demo.application.port.out;

import org.springframework.web.multipart.MultipartFile;

/**
 * ProductImageStoragePort - 商品圖片儲存管理器 Outbound Port。
 * <p>
 * 定義應用層操作底層 Blob Storage 的介面。負責處理商品圖片上傳等基礎設施操作。
 * 將基礎設施實作與業務邏輯解耦。
 * </p>
 */
public interface ProductImageStoragePort {
    
    /**
     * 上傳商品圖片
     *
     * @param file 上傳的圖片檔案
     * @return 圖片儲存後的完整存取網址 (URL)
     */
    String uploadImage(MultipartFile file);
}
