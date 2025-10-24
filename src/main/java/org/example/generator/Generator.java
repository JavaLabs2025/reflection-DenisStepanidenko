package org.example.generator;

import lombok.extern.slf4j.Slf4j;
import org.example.annotation.Generated;
import org.example.exceptions.NoConstructorsFoundException;
import org.example.exceptions.NoImplementationsFoundException;

import java.io.File;
import java.lang.reflect.*;
import java.net.URL;
import java.util.*;

/**
 * Генератор экземпляров классов, помеченных аннотацией {@link Generated}.
 * Поддерживает рекурсивную генерацию объектов с контролем глубины.
 * Автоматически находит реализации для интерфейсов и абстрактных классов.
 */
@Slf4j
public class Generator {

    /**
     * Пакеты для сканирования интерфейсов и абстрактных классов (задаётся в конструкторе)
     */
    private final String[] basePackages;

    /**
     * Здесь хранятся реализации интерфейсов и абстрактных классов
     * Данная map заполняется при вызове конструтора
     */
    private final Map<Class<?>, List<Class<?>>> implementations = new HashMap<>();

    private final Random random = new Random();

    /**
     * Максимальная глубина рекурсии для предотвращения бесконечной рекурсии
     * при генерации взаимосвязанных объектов.
     */
    private static final int MAX_RECURSIVE_DEPTH = 3;

    /**
     * Конструктор по умолчанию смотрит пакет org.example
     */
    public Generator() {
        this("org.example");
    }

    /**
     * Конструктор с указанием пакетов для сканирования
     */
    public Generator(String... basePackages) {

        this.basePackages = basePackages;
        scanPackages(basePackages);

        log.debug("Была загружена следующая Map для интерфейсов и абстрактных классов: ");
        log.debug(implementations.toString());
    }

    /**
     * Сканирование всех пакетов
     *
     * @param basePackages массив packages
     */
    private void scanPackages(String[] basePackages) {

        for (String packageName : basePackages) {
            scanPackage(packageName);
        }

    }

    /**
     * Сканирование конкретного пакета
     *
     * @param packageName имя package
     */
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
            log.debug("Произошла ошибка {}, при сканировании пакета {}", e, packageName);
        }

    }

    /**
     * Сканирование директории
     *
     * @param rootDirectory директория, из которой мы на данный момент смотрим файлы
     * @param packageName   имя package
     */
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

    /**
     * Поиск всех интерфейсов и абстрактных классов у заданного класса
     *
     * @param className имя класса
     */
    private void scanClass(String className) {

        try {
            Class<?> clazz = Class.forName(className);

            // проверка на интерфейс или абстрактный класс
            if (clazz.isInterface() || Modifier.isAbstract(clazz.getModifiers())) {
                implementations.putIfAbsent(clazz, new ArrayList<>());
                return;
            }


            if (!clazz.isAnnotationPresent(Generated.class)) return;


            addAllInterfacesRecursive(clazz, clazz.getInterfaces());
            addAllSuperClassesRecursive(clazz, clazz.getSuperclass());


        } catch (ClassNotFoundException e) {

            log.error("Class not found: {}", className);
            throw new RuntimeException(e);
        }

    }

    /**
     * Нахождение рекурсивно всех абстрактных классов
     *
     * @param clazz         класс, у которого мы пытаемся найти абстрактные классы
     * @param abstractClass абстракный класс
     */
    private void addAllSuperClassesRecursive(Class<?> clazz, Class<?> abstractClass) {

        if (Objects.isNull(abstractClass)) return;
        addImplementation(clazz, abstractClass);
        addAllInterfacesRecursive(clazz, abstractClass.getInterfaces());
        addAllSuperClassesRecursive(clazz, abstractClass.getSuperclass());

    }

    /**
     * Нахождение всех интерфейсов заданного класса
     *
     * @param clazz            класс, у которого мы ищем интерфейсы
     * @param interfaceClasses список интерфейсов
     */
    private void addAllInterfacesRecursive(Class<?> clazz, Class<?>[] interfaceClasses) {

        for (Class<?> abstractClass : interfaceClasses) {

            addImplementation(clazz, abstractClass);
            addAllInterfacesRecursive(clazz, abstractClass.getInterfaces());
        }

    }

    /**
     * Добавление в map реализации по ключу интерфейса или абстрактного класса
     *
     * @param clazz         конкретная реалиация
     * @param abstractClass абстрактный класс
     */
    private void addImplementation(Class<?> clazz, Class<?> abstractClass) {

        if (implementations.containsKey(abstractClass)) {
            implementations.get(abstractClass).add(clazz);
        } else {
            List<Class<?>> classList = new ArrayList<>();
            classList.add(clazz);
            implementations.put(abstractClass, classList);
        }

    }

    /**
     * Создаёт экземпляр класса clazz
     *
     * @param clazz класс для создания реализации
     */
    public Object generateValueOfType(Class<?> clazz) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        return generateValueOfType(clazz, 0);
    }

    /**
     * Создаёт экземпляр класса clazz
     *
     * @param clazz класс для создания реализации
     * @param depth текущая глубина рекурсии
     */
    public Object generateValueOfType(Class<?> clazz, int depth) throws InvocationTargetException, InstantiationException, IllegalAccessException {

        if (!clazz.isAnnotationPresent(Generated.class)) {
            throw new IllegalArgumentException("Класс " + clazz.getName() + " не помечен аннотацией Generated и не может быть сгенерирован.");
        }

        // дефолтное значение
        if (depth > MAX_RECURSIVE_DEPTH) {
            return null;
        }


        if (clazz.isInterface() || Modifier.isAbstract(clazz.getModifiers())) {

            List<Class<?>> classList = implementations.get(clazz);

            if (Objects.isNull(classList) || classList.isEmpty()) {
                throw new NoImplementationsFoundException("Для интерфейса/абстрактного класса " + clazz.getName() + " не найдено имплементаций. Проверьте, что на реализациях стоят аннотации Generated.");
            }

            Class<?> randomImpl = classList.get(random.nextInt(classList.size()));

            return generateValueOfType(randomImpl);
        }


        return generateImplementationClassInstance(clazz, depth);
    }

    /**
     * Генерирует экземпляр конкретного класса через случайно выбранный конструктор.
     * Для каждого параметра конструктора рекурсивно генерируются значения соответствующего типа.
     *
     * @param clazz класс, экземпляр которого генерируется
     * @param depth текущая глубина рекурсии
     */
    private Object generateImplementationClassInstance(Class<?> clazz, int depth) throws InvocationTargetException, InstantiationException, IllegalAccessException {

        Constructor<?>[] constructors = clazz.getDeclaredConstructors();

        if (constructors.length == 0) {
            throw new NoConstructorsFoundException("В классе " + clazz.getName() + " не найдено конструкторов.");
        }

        Constructor<?> randomConstructor = constructors[random.nextInt(constructors.length)];
        randomConstructor.setAccessible(true);

        Parameter[] parameters = randomConstructor.getParameters();
        Object[] args = new Object[parameters.length];

        for (int i = 0; i < parameters.length; i++) {

            args[i] = generateParameterValue(parameters[i], depth);
        }

        return randomConstructor.newInstance(args);
    }

    /**
     * Генерирует экземпляр класса parameter.getType()
     *
     * @param depth     текущая глубина рекурсии
     * @param parameter текущий параметр конструктора
     */
    private Object generateParameterValue(Parameter parameter, int depth) throws InvocationTargetException, InstantiationException, IllegalAccessException {

        Class<?> parameterClass = parameter.getType();

        if (parameterClass == int.class || parameterClass == Integer.class) {
            return random.nextInt();
        } else if (parameterClass == double.class || parameterClass == Double.class) {
            return random.nextDouble();
        } else if (parameterClass == float.class || parameterClass == Float.class) {
            return random.nextFloat();
        } else if (parameterClass == long.class || parameterClass == Long.class) {
            return random.nextLong();
        } else if (parameterClass == boolean.class || parameterClass == Boolean.class) {
            return random.nextBoolean();
        } else if (parameterClass == char.class || parameterClass == Character.class) {
            return (char) (random.nextInt(26) + 'a');
        } else if (parameterClass == byte.class || parameterClass == Byte.class) {
            return (byte) random.nextInt(Byte.MAX_VALUE);
        } else if (parameterClass == short.class || parameterClass == Short.class) {
            return (short) random.nextInt(Short.MAX_VALUE);
        } else if (parameterClass == String.class) {
            return generateRandomString();
        } else if (parameterClass == List.class) {
            return generateList(parameter, depth + 1);
        } else {

            // иначе рекурсивно создаём класс, увеличивая глубину рекурсии
            return generateValueOfType(parameterClass, depth + 1);
        }


    }

    /**
     * Генерирует список с элементами указанного типа.
     *
     * @param parameter параметр, содержащий информацию о generic-типе списка
     * @param depth     текущая глубина рекурсии
     * @return сгенерированный список
     */
    private Object generateList(Parameter parameter, int depth) {

        try {
            Type genericType = parameter.getParameterizedType();


            // тут мы делаем проверку, потому что у нас может быть, например List list
            // То есть создания без дженериков
            if (genericType instanceof ParameterizedType) {
                ParameterizedType parameterizedType = (ParameterizedType) genericType;

                Type[] typeArgs = parameterizedType.getActualTypeArguments();

                // тут мы делаем проверку, потому что например у нас может быть List<T>
                // или wild cards
                if (typeArgs.length > 0 && typeArgs[0] instanceof Class) {

                    // генерируем List с рандомным кол-вом объектов (для примера возьмём кол-во до 10)
                    int length = random.nextInt(10);
                    List<Object> list = new ArrayList<>();
                    for (int i = 0; i < length; i++) {
                        if (depth > MAX_RECURSIVE_DEPTH) {
                            break;
                        }
                        list.add(generateValueOfType(Class.forName(((Class<?>) typeArgs[0]).getName())));
                    }

                    return list;
                }

            }

            return new ArrayList<>();
        } catch (Exception e) {

            log.warn("Ошибка при генерации списка для параметра {}: {}", parameter.getName(), e.getMessage());
            throw new RuntimeException(e);
        }

    }

    /**
     * Генерация случайно строки заданного размера
     */
    private Object generateRandomString() {

        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

        int length = random.nextInt(100);

        StringBuilder str = new StringBuilder();
        for (int i = 0; i < length; i++) {

            str.append(characters.charAt(random.nextInt(characters.length())));
        }

        return str.toString();

    }


}
