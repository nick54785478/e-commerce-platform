export enum ProductStatus {
  ACTIVE = 'ACTIVE',
  INACTIVE = 'INACTIVE'
}

export interface ProductQueriedView {
  productId: string;
  tenantId: string;
  name: string;
  description: string;
  type: string;
  subType?: string;
  price: number;
  stock: number;
  version: number;
  imageUrls: string[];
  tags: string[];
  status: ProductStatus;
  available: boolean;
}

export interface ProductPageQueriedView {
  content: ProductQueriedView[];
  totalElements: number;
  totalPages: number;
  currentPage: number;
}

export interface CreateProductResource {
  name: string;
  description: string;
  type: string;
  subType?: string;
  price: number;
  imageUrls: string[];
  tags: string[];
}

export interface UpdateProductResource {
  version: number;
  name: string;
  description: string;
  type: string;
  subType?: string;
  price: number;
  imageUrls: string[];
  tags: string[];
}

export interface ChangeProductStatusResource {
  version: number;
  status: ProductStatus;
}
