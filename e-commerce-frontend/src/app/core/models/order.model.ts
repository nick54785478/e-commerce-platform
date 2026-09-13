export interface CreateOrderItemResource {
  productId: string;
  quantity: number;
}

export interface CreateOrderResource {
  items: CreateOrderItemResource[];
}

export interface OrderCreatedResource {
  code: string;
  message: string;
  orderId: string;
}

export interface PaymentQueriedView {
  paymentId: string;
  orderId: string;
  amount: number;
  status: string;
}

export interface PaymentsQueriedResource {
  code: string;
  message: string;
  data: PaymentQueriedView[];
}
