package org.example;


import org.example.classes.BinaryTreeNode;
import org.example.classes.Cart;
import org.example.classes.Example;
import org.example.classes.Shape;
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
        Cart cart = (Cart) generator.generateValueOfType(Cart.class);

        System.out.println(cart);

//        Shape shape = (Shape) generator.generateValueOfType(Shape.class);
//        System.out.println(shape);

//        BinaryTreeNode binaryTreeNode = (BinaryTreeNode) generator.generateValueOfType(BinaryTreeNode.class);
//
//        System.out.println(binaryTreeNode);



//        var gen = new Generator();
//        try {
//            Object generated = gen.generateValueOfType(Example.class);
//            System.out.println(generated);
//        } catch (Throwable e) {
//            throw new RuntimeException(e);
//        }


    }
}