package org.example.generator;

import lombok.extern.slf4j.Slf4j;
import org.example.annotation.Generated;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.*;

@Slf4j
public class Generator {

    private final String[] basePackages;

    /**
     * Здесь хранятся реализации интерфейсов и абстрактных классов
     * Данная map заполняется при вызове конструтора
     */
    private final Map<Class<?>, List<Class<?>>> implementations = new HashMap<>();


    public Generator() {
        this("org.example");
    }

    public Generator(String... basePackages) {

        this.basePackages = basePackages;
        scanPackages(basePackages);



    }

    private void scanPackages(String[] basePackages) {

        for (String packageName : basePackages) {
            scanPackage(packageName);
        }

    }

    private void scanPackage(String packageName) {

        try {
            ClassLoader classLoader = ClassLoader.getSystemClassLoader();

            URL urlResource = classLoader.getResource(packageName.replace(".", "/"));

            if (Objects.isNull(urlResource)) {
                log.debug("Не найден пакет с наименованием {}", packageName);
            } else {

                scanDirectory(new File(urlResource.getFile()), packageName);
            }
        } catch (Exception e) {
            log.debug("Произошла ");
        }

    }

    private void scanDirectory(File rootDirectory, String packageName) {

        File[] files = rootDirectory.listFiles();

        if (Objects.isNull(files)) return;

        for (File file : files) {

            if (file.isDirectory()) {
                scanDirectory(file, packageName + "." + file.getName());
            } else if (file.getName().endsWith(".class")) {


                String className = packageName + "." + file.getName().substring(0, file.getName().length() - 6);
                scanClass(className);
            }

        }

    }

    private void scanClass(String className) {

        try {
            Class<?> clazz = Class.forName(className);

            // проверка на интерфейс или абстрактный класс
            if (clazz.isInterface() || Modifier.isAbstract(clazz.getModifiers())) {
                implementations.putIfAbsent(clazz, new ArrayList<>());
                return;
            }


            if (!clazz.isAnnotationPresent(Generated.class)) return;

            Class<?>[] interfaceClasses = clazz.getInterfaces();

            System.out.println(clazz.getName() + " " + Arrays.toString(interfaceClasses));

            //TODO: вдруг interfaceClass наследуются от других интерфейсов - нужно тоже их включить
            for (Class<?> interfaceClass : interfaceClasses) {

                System.out.println(Arrays.toString(interfaceClass.getInterfaces()));
                addImplementation(interfaceClass, clazz);
            }

//            Class<?> superClass = clazz.getSuperclass();
//            if (Objects.nonNull(superClass)) {
//
//
//            }


        } catch (ClassNotFoundException e) {

            log.error("Class not found: {}", className);
            throw new RuntimeException(e);
        }

    }

    private void addImplementation(Class<?> interfaceClass, Class<?> clazz) {

        if (implementations.containsKey(interfaceClass)) {
            implementations.get(interfaceClass).add(clazz);
        } else {
            List<Class<?>> classList = new ArrayList<>();
            classList.add(clazz);
            implementations.put(interfaceClass, classList);
        }

    }


    public Object generateValueOfType(Class<?> clazz) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        Constructor<?>[] constructors = clazz.getDeclaredConstructors();

        int randomConstructorIndex = new Random().nextInt(constructors.length);
        Constructor<?> randomConstructor = constructors[randomConstructorIndex];
        return randomConstructor.newInstance(111);
    }


}
