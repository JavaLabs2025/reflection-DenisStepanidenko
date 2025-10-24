package org.example;


import org.example.classes.*;
import org.example.generator.Generator;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class GenerateExample {
    public static void main(String[] args) throws IOException, InvocationTargetException, InstantiationException, IllegalAccessException {


        Generator generator = new Generator("org.example");

        Example example = (Example) generator.generateValueOfType(Example.class);
        System.out.println(example);

        BinaryTreeNode binaryTreeNode = (BinaryTreeNode) generator.generateValueOfType(BinaryTreeNode.class);
        System.out.println(binaryTreeNode);

        Cart cart = (Cart) generator.generateValueOfType(Cart.class);
        System.out.println(cart);

        Product product = (Product) generator.generateValueOfType(Product.class);
        System.out.println(product);

        Rectangle rectangle = (Rectangle) generator.generateValueOfType(Rectangle.class);
        System.out.println(rectangle);

        Triangle triangle = (Triangle) generator.generateValueOfType(Triangle.class);
        System.out.println(triangle);

        Shape shape = (Shape) generator.generateValueOfType(Shape.class);
        System.out.println(shape);
    }
}