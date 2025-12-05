package com.market.server.model;

import java.util.List;

public class OrderDTO {
    private UserDTO user;
    private List<OrderItemDTO> items;

    public UserDTO getUser() { return user; }
    public void setUser(UserDTO user) { this.user = user; }
    public List<OrderItemDTO> getItems() { return items; }
    public void setItems(List<OrderItemDTO> items) { this.items = items; }

    public static class UserDTO {
        private String name;
        private String phone;
        private String address;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
        public String getAddress() { return address; }
        public void setAddress(String address) { this.address = address; }
    }

    public static class OrderItemDTO {
        private String bookId;
        private int quantity;

        public String getBookId() { return bookId; }
        public void setBookId(String bookId) { this.bookId = bookId; }
        public int getQuantity() { return quantity; }
        public void setQuantity(int quantity) { this.quantity = quantity; }
    }
}

