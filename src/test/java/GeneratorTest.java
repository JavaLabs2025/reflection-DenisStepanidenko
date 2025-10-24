import org.example.classes.*;
import org.example.generator.Generator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class GeneratorTest {

    private Generator generator = new Generator();

    @Test
    void testTriangleClassGenerated() throws InvocationTargetException, InstantiationException, IllegalAccessException {

        helpMethod(Triangle.class);

    }

    @Test
    void testRectangleClassGenerated() throws InvocationTargetException, InstantiationException, IllegalAccessException {

        helpMethod(Rectangle.class);

    }

    @Test
    void testBinaryTreeNodeClassGenerated() throws InvocationTargetException, InstantiationException, IllegalAccessException {

        helpMethod(BinaryTreeNode.class);

    }

    @Test
    void testCartClassGenerated() throws InvocationTargetException, InstantiationException, IllegalAccessException {

        helpMethod(Cart.class);

    }

    @Test
    void testExampleClassGenerated() throws InvocationTargetException, InstantiationException, IllegalAccessException {

        helpMethod(Example.class);

    }

    @Test
    void testProductClassGenerated() throws InvocationTargetException, InstantiationException, IllegalAccessException {

        helpMethod(Product.class);

    }

    @Test
    void testShapeClassGenerated() throws InvocationTargetException, InstantiationException, IllegalAccessException {

        helpMethod(Shape.class);

    }

    /**
     * Вспомогающий метод для тестирования генерации классов.
     * <p>
     * Принимает на вход clazz и создаёт экземпляр clazz.
     */
    private void helpMethod(Class<?> clazz) throws InvocationTargetException, InstantiationException, IllegalAccessException {

        Object result = generator.generateValueOfType(clazz);

        assertNotNull(result, "Класс должен создаться");
        assertInstanceOf(clazz, result, "Класс должен быть " + clazz.getName());
    }


}
