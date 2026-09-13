# 前端架構規範 (Frontend Architecture)

遵循 LIFT 準則，劃分 `core`、`features`、`shared`。

## 1. Core (核心層)
- **職責**：全系統唯一 (Singleton)、無關業務的底層。
- **內容**：全域單例服務 (如 `AuthService`, `StorageService`)、攔截器、守衛、全域設定、以及應用程式外殼元件 (App Shell，如 `Layout`、`Header`、`Footer`)。
- **元件規範**：構成網站基礎骨架且不綁定特定業務功能的全域元件 (如 Layout, Header, Footer) 必須放於此，因其生命週期與 App 相同。
- **約束**：僅在根目錄 (`app.config.ts`) 或最上層元件載入，嚴禁於 Feature 匯入。

## 2. Shared (共享層)
- **職責**：跨模組共用 UI 與無狀態工具。
- **內容**：Dumb Components (共用按鈕)、指令、管道、共用型別、無狀態輔助服務 (如 `ValidationService`)。
- **約束**：嚴禁注入業務 Service 或綁定 API。資料均透過 `@Input`/`@Output` 傳遞。

## 3. Features (功能層)
- **職責**：業務邏輯與畫面，依業務領域 (如 `products`) 垂直切分。
- **切片架構**：依領域建資料夾，內部定義高度封裝的 Component（含專屬 HTML/CSS/TS 與子元件）。
- **內容**：Smart Components、領域專屬元件、領域 Service 與 Model、路由。
- **約束**：嚴禁將領域邏輯散落至 Shared 或 Core。

## 4. 跨模組呼叫 (Cross-Feature)
- **跨模組注入 Service**：合法且推薦。如 Order 需 Product 資料，直接注入 `ProductService` 保持內聚。嚴禁將 Service 集中至 `src/app/services/`。
- **避免畫面依賴**：依賴資料 (Service)，嚴禁私下依賴他領域的 UI。
- **Smart Shared Component**：高頻共用且需 API 的 UI (如共用下拉選單)，可妥協建立自帶資料的 Smart Component 以減少 Boilerplate。

## 5. Model 型別管理
嚴禁將 Model 集中於全域資料夾。
- **領域專屬 Model**：(如 `ProductView`) 必須放於 `features/<domain>/models/`，跨模組呼叫 Service 時自然匯入。
- **全域基礎設施 Model**：(如 `ApiResponse`, 泛型) 統一放於 `core/models/` 或 `shared/models/`。

## 6. 元件開發規範 (Component Conventions)
- **檔案分離**：嚴格限制不能將 Component 的 HTML 與 CSS 寫在 TypeScript 檔案中 (禁止 inline template/styles)。必須強制拆分為獨立的 `.html`、`.css` 與 `.ts` 三個檔案，並使用 `templateUrl` 與 `styleUrl` 引入。
