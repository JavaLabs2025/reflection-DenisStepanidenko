package org.example.classes;

import org.example.annotation.Generated;

import java.util.List;

@Generated
public class Cart {

    private List<BinaryTreeNode> binaryTreeNodes;
    private List<Product> items;

    public Cart(List<BinaryTreeNode> binaryTreeNodes, List<Product> items) {
        this.binaryTreeNodes = binaryTreeNodes;
        this.items = items;
    }

    public List<Product> getItems() {
        return items;
    }

    public void setItems(List<Product> items) {
        this.items = items;
    }

    // Конструктор, методы добавления и удаления товаров, геттеры и другие методы


    @Override
    public String toString() {
        return "Cart{" +
                "binaryTreeNodes=" + binaryTreeNodes +
                ", items=" + items +
                '}';
    }
}