package com.ecommerce.productservice.event;

import java.util.List;

public class OrderCreatedEvent {
    private Long orderId;
    private List<OrderItemEvent> items;

    public OrderCreatedEvent() {
    }

    public OrderCreatedEvent(Long orderId, List<OrderItemEvent> items) {
        this.orderId = orderId;
        this.items = items;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public List<OrderItemEvent> getItems() {
        return items;
    }

    public void setItems(List<OrderItemEvent> items) {
        this.items = items;
    }

    @Override
    public String toString() {
        return "OrderCreatedEvent{" +
               "orderId=" + orderId +
               ", items=" + items +
               '}';
    }

    public static class OrderItemEvent {
        private Long productId;
        private int quantity;

        public OrderItemEvent() {
        }

        public OrderItemEvent(Long productId, int quantity) {
            this.productId = productId;
            this.quantity = quantity;
        }

        public Long getProductId() {
            return productId;
        }

        public void setProductId(Long productId) {
            this.productId = productId;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }

        @Override
        public String toString() {
            return "OrderItemEvent{" +
                   "productId=" + productId +
                   ", quantity=" + quantity +
                   '}';
        }
    }
}