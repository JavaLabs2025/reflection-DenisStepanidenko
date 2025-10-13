package org.example;


import org.example.classes.BinaryTreeNode;
import org.example.classes.Example;
import org.example.generator.Generator;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;

public class GenerateExample {
    public static void main(String[] args) throws IOException {

        Class<BinaryTreeNode> clazz = BinaryTreeNode.class;


        Generator generator = new Generator("org.example");

//        var gen = new Generator();
//        try {
//            Object generated = gen.generateValueOfType(Example.class);
//            System.out.println(generated);
//        } catch (Throwable e) {
//            throw new RuntimeException(e);
//        }


    }
}