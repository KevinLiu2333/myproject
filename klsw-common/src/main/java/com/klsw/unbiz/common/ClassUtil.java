/**
 * 
 */
package com.klsw.unbiz.common;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

/**
 * 有关 <code>Class</code> 处理的工具类。
 * 
 * <p>
 * 这个类中的每个方法都可以“安全”地处理 <code>null</code> ，而不会抛出 <code>NullPointerException</code>。
 * </p>
 * 
 * @author <a href="mailto:xuchen06@baidu.com">xuc</a>
 * @version create on 2014年7月14日 下午2:57:01
 */
public abstract class ClassUtil {
    /*
     * ========================================================================== ==
     */
    /* 常量和singleton。 */
    /*
     * ========================================================================== ==
     */

    /** 资源文件的分隔符： <code>'/'</code>。 */
    private static final char RESOURCE_SEPARATOR_CHAR = '/';

    /** Java类名的分隔符： <code>'.'</code>。 */
    private static final char PACKAGE_SEPARATOR_CHAR = '.';

    // FIXME
    /** 内联类的分隔符： <code>'$'</code>。 */
    public static final char INNER_CLASS_SEPARATOR_CHAR = '$';

    /** 内联类的分隔符： <code>"$"</code>。 */
    public static final String INNER_CLASS_SEPARATOR = String.valueOf(INNER_CLASS_SEPARATOR_CHAR);

    /** 所有类的信息表，包括父类, 接口, 数组的维数等信息。 */
    private static final Map<Class<?>, TypeInfo> TYPE_MAP = Collections
            .synchronizedMap(new WeakHashMap<Class<?>, TypeInfo>());

    /**
     * 取得指定类的资源名。
     * 
     * <p>
     * 例如：
     * 
     * <pre>
     * ClassUtil.getClassNameAsResource(&quot;java.lang.String&quot;) = &quot;java/lang/String.class&quot;
     * </pre>
     * 
     * </p>
     * 
     * @param className 要显示的类名
     * 
     * @return 指定类名对应的资源名，如果指定类名为空，则返回<code>null</code>
     */
    public static String getClassNameAsResource(String className) {
        if (StringUtil.isBlank(className)) {
            return null;
        }

        return className.replace(PACKAGE_SEPARATOR_CHAR, RESOURCE_SEPARATOR_CHAR) + ".class";
    }

    // ==========================================================================
    // 原子类型或者其wrapper相关。
    // ==========================================================================

    private static final Map<String, PrimitiveInfo<?>> PRIMITIVES = CollectionUtil.createHashMap();

    static {
        addPrimitive(boolean.class, "Z", Boolean.class, "booleanValue", false);
        addPrimitive(short.class, "S", Short.class, "shortValue", (short) 0);
        addPrimitive(int.class, "I", Integer.class, "intValue", 0);
        addPrimitive(long.class, "J", Long.class, "longValue", 0L);
        addPrimitive(float.class, "F", Float.class, "floatValue", 0F);
        addPrimitive(double.class, "D", Double.class, "doubleValue", 0D);
        addPrimitive(char.class, "C", Character.class, "charValue", '\0');
        addPrimitive(byte.class, "B", Byte.class, "byteValue", (byte) 0);
        addPrimitive(void.class, "V", Void.class, null, null);
    }

    private static <T> void addPrimitive(Class<T> type, String typeCode, Class<T> wrapperType, String unwrapMethod,
            T defaultValue) {
        PrimitiveInfo<T> info = new PrimitiveInfo<T>(type, typeCode, wrapperType, unwrapMethod, defaultValue);

        PRIMITIVES.put(type.getName(), info);
        PRIMITIVES.put(wrapperType.getName(), info);
    }

    /** 代表一个primitive类型的信息。 */
    @SuppressWarnings("unused")
    private static class PrimitiveInfo<T> {
        final Class<T> type;
        final String typeCode;
        final Class<T> wrapperType;
        final String unwrapMethod;
        final T defaultValue;

        public PrimitiveInfo(Class<T> type, String typeCode, Class<T> wrapperType, String unwrapMethod, T defaultValue) {
            this.type = type;
            this.typeCode = typeCode;
            this.wrapperType = wrapperType;
            this.unwrapMethod = unwrapMethod;
            this.defaultValue = defaultValue;
        }
    }

    /**
     * 取得primitive类。
     * <p>
     * 例如：
     * <p/>
     * 
     * <pre>
     * ClassUtil.getPrimitiveType(&quot;int&quot;) = int.class;
     * ClassUtil.getPrimitiveType(&quot;long&quot;) = long.class;
     * </pre>
     * <p/>
     * </p>
     */
    public static Class<?> getPrimitiveType(String name) {
        PrimitiveInfo<?> info = PRIMITIVES.get(name);

        if (info != null) {
            return info.type;
        }

        return null;
    }

    /**
     * 取得primitive类。
     * <p>
     * 例如：
     * <p/>
     * 
     * <pre>
     * ClassUtil.getPrimitiveType(Integer.class) = int.class;
     * ClassUtil.getPrimitiveType(Long.class) = long.class;
     * </pre>
     * <p/>
     * </p>
     */
    public static Class<?> getPrimitiveType(Class<?> type) {
        return getPrimitiveType(type.getName());
    }

    /**
     * 取得primitive类型的wrapper。如果不是primitive，则原样返回。
     * <p>
     * 如果<code>clazz</code>为<code>null</code>，则返还 <code>null</code>
     * <p>
     * 例如：
     * <p/>
     * 
     * <pre>
     * ClassUtil.getPrimitiveWrapperType(int.class) = Integer.class;
     * ClassUtil.getPrimitiveWrapperType(int[].class) = int[].class;
     * ClassUtil.getPrimitiveWrapperType(int[][].class) = int[][].class;
     * ClassUtil.getPrimitiveWrapperType(String[][].class) = String[][].class;
     * </pre>
     * <p/>
     * </p>
     */
    @SuppressWarnings("unchecked")
    public static <T> Class<T> getWrapperTypeIfPrimitive(Class<T> type) {
        if (type == null) {
            return null;
        }

        if (type.isPrimitive()) {
            return ((PrimitiveInfo<T>) PRIMITIVES.get(type.getName())).wrapperType;
        }

        return type;
    }

    /**
     * 取得primitive类型的默认值。如果不是primitive，则返回<code>null</code>。
     * <p>
     * 如果<code>clazz</code>为<code>null</code>，则返还 <code>null</code>
     * <p>
     * 例如：
     * <p/>
     * 
     * <pre>
     * ClassUtil.getPrimitiveDefaultValue(int.class) = 0;
     * ClassUtil.getPrimitiveDefaultValue(boolean.class) = false;
     * ClassUtil.getPrimitiveDefaultValue(char.class) = '\0';
     * </pre>
     * <p/>
     * </p>
     */
    public static <T> T getPrimitiveDefaultValue(Class<T> type) {
        if (type == null) {
            return null;
        }

        @SuppressWarnings("unchecked")
        PrimitiveInfo<T> info = (PrimitiveInfo<T>) PRIMITIVES.get(type.getName());

        if (info != null) {
            return info.defaultValue;
        }

        return null;
    }

    /**
     * 判断是否为原子类型或者其包装类，如果<code>clazz</code>为<code>null</code>，则返还 <code>false</code>
     */
    public static boolean isPrimitiveOrWrapper(Class<?> clazz) {
        if (clazz == null) {
            return false;
        }

        return PRIMITIVES.get(clazz.getName()) != null;

    }

    // ==========================================================================
    // 数组类。
    // ==========================================================================

    /**
     * 取得指定一维数组类.
     * 
     * @param componentType 数组的基础类
     * @return 数组类，如果数组的基类为 <code>null</code> ，则返回 <code>null</code>
     */
    public static Class<?> getArrayClass(Class<?> componentType) {
        return getArrayClass(componentType, 1);
    }

    /**
     * 取得指定维数的 <code>Array</code>类.
     * 
     * @param componentType 数组的基类
     * @param dimension 维数，如果小于 <code>0</code> 则看作 <code>0</code>
     * @return 如果维数为0, 则返回基类本身, 否则返回数组类，如果数组的基类为 <code>null</code> ，则返回 <code>null</code>
     */
    public static Class<?> getArrayClass(Class<?> componentClass, int dimension) {
        if (componentClass == null) {
            return null;
        }

        switch (dimension) {
            case 1:
                return Array.newInstance(componentClass, 0).getClass();

            case 0:
                return componentClass;

            default:
                Assert.assertTrue(dimension > 0, "wrong dimension: %d", dimension);

                return Array.newInstance(componentClass, new int[dimension]).getClass();
        }
    }

    /**
     * 取得数组元素的类型。
     * 
     * @param type 要查找的类
     * 
     * @return 如果是数组, 则返回数组元素的类型, 否则返回 <code>null</code>
     */
    public static Class<?> getArrayComponentType(Class<?> type) {
        if (type == null) {
            return null;
        }

        return getTypeInfo(type).getArrayComponentType();
    }

    // ==========================================================================
    // instance相关。
    // ==========================================================================

    /**
     * 如果对象<code>object</code>是类<code>clazz</code>的实例， 则返回<code>true</code>，否则返回 <code>false</code>
     * <p>
     * 此方法考虑包装类和基本类型的转换
     * 
     * <pre>
     * ClassUtil.isInstance(int.class, Integer.valueOf(0)) = true;
     * ClassUtil.isInstance(Integer.class, (int) 0) = true;
     * ClassUtil.isInstance(int.class, (int) 0) = true;
     * ClassUtil.isInstance(Integer.class, Integer.valueOf(0)) = true;
     * ClassUtil.isInstance(int[].class, new Integer[] { 0 }) = false;
     * ClassUtil.isInstance(Integer[].class, new int[] { 0 }) = false;
     * ClassUtil.isInstance(int[].class, new new int[] { 0 }) = false;
     * ClassUtil.isInstance(Integer[].class, new Integer[] { 0 }) = false;
     * </pre>
     * 
     * @param clazz 校验的类
     * @param object 校验的对象
     * @return 是否匹配，如果<code>clazz</code>或者<code>object</code>为<code>null</code> ，则返回<code>false</code>
     */
    public static boolean isInstance(Class<?> clazz, Object object) {
        if (clazz == null || object == null) {
            return false;
        }

        if (clazz.isPrimitive()) {
            PrimitiveInfo<?> info = PRIMITIVES.get(clazz.getName());

            return info.wrapperType.isInstance(object);
        }

        return clazz.isInstance(object);
    }

    /**
     * 判断一个类是否可以实例化，如果<code>clazz</code>为<code>null</code>，则返回<code>false</code>
     * <p>
     * 按一下顺序判断：
     * <li>
     * 如果<code>clazz</code>为<code>primitive</code>类型，我们认为除了 <code>void.class</code>外，其他都可实例化，因为可以直接赋值</li>
     * <li>
     * 如果<code>clazz</code>为<code>Array</code>类型，如果数组元素的类型为<code>public</code> ，那么显然也可以实例化，因为可以直接定义新对象，返还
     * <code>true</code></li>
     * <li>接下来看<code>clazz</code>是否为<code>abstract</code>或 <code>interface ，如果是，则显然不可实例化，返还<code>false</code></li>
     * <li>最后遍历<code>clazz</code>的构造器，查看是否存在<code>public</code>类型接口，如果存在则返还 <code>true</code>，否则返还<code>false</code></li>
     * 
     * @param clazz 校验的类
     * @return 如果可以实例化则返回<code>true</code>，否则返回<code>false</code>
     */
    public static <T> boolean canInstatnce(Class<T> clazz) {
        if (clazz == null) {
            return false;
        }
        if (clazz.isPrimitive()) {
            return clazz != void.class;
        }

        if (clazz.isArray() && Modifier.isPublic(getArrayComponentType(clazz).getModifiers())) {
            return true;
        }

        int modifier = clazz.getModifiers();

        if (Modifier.isAbstract(modifier) || Modifier.isInterface(modifier)) {
            return false;
        }

        Constructor<?>[] constructors = clazz.getConstructors();
        for (Constructor<?> constructor : constructors) {
            if (Modifier.isPublic(constructor.getModifiers())) {
                return true;
            }
        }

        return false;

    }

    /**
     * 取得指定类的 <code>TypeInfo</code>。
     * 
     * @param type 指定类或接口
     * 
     * @return <code>TypeInfo</code> 对象.
     */
    protected static TypeInfo getTypeInfo(Class<?> type) {
        if (type == null) {
            throw new IllegalArgumentException("Parameter clazz should not be null");
        }

        TypeInfo classInfo;

        synchronized (TYPE_MAP) {
            classInfo = TYPE_MAP.get(type);

            if (classInfo == null) {
                classInfo = new TypeInfo(type);
                TYPE_MAP.put(type, classInfo);
            }
        }

        return classInfo;
    }

    // ==========================================================================
    // TypeInfo
    // ==========================================================================

    /**
     * 代表一个类的信息, 包括父类, 接口, 数组的维数等.
     */
    protected static class TypeInfo {
        private Class<?> type;
        private Class<?> componentType;
        private int dimension;
        private List<Class<?>> superclasses = CollectionUtil.createArrayList(2);
        private List<Class<?>> interfaces = CollectionUtil.createArrayList(2);

        /**
         * 创建 <code>TypeInfo</code>。 //FIXME 恶心
         * 
         * @param type 创建指定类的 <code>TypeInfo</code>
         */
        private TypeInfo(Class<?> type) {
            this.type = type;

            // 如果是array, 设置componentType和dimension
            Class<?> componentType = null;

            if (type.isArray()) {
                componentType = type;

                do {
                    componentType = componentType.getComponentType();
                    dimension++;
                } while (componentType.isArray());
            }

            this.componentType = componentType;

            // 取得所有superclass
            if (dimension > 0) {
                // 将primitive类型转换成对应的包装类
                componentType = getNonPrimitiveType(componentType);

                Class<?> superComponentType = componentType.getSuperclass();

                // 如果是primitive, interface, 则设置其基类为Object.
                if ((superComponentType == null) && !Object.class.equals(componentType)) {
                    superComponentType = Object.class;
                }

                if (superComponentType != null) {
                    Class<?> superclass = getArrayClass(superComponentType, dimension);

                    superclasses.add(superclass);
                    superclasses.addAll(getTypeInfo(superclass).superclasses);
                } else {
                    for (int i = dimension - 1; i >= 0; i--) {
                        superclasses.add(getArrayClass(Object.class, i));
                    }
                }
            } else {
                // 将primitive类型转换成对应的包装类
                type = getNonPrimitiveType(type);

                Class<?> superclass = type.getSuperclass();

                if (superclass != null) {
                    superclasses.add(superclass);
                    superclasses.addAll(getTypeInfo(superclass).superclasses);
                }
            }

            // 取得所有interface
            if (dimension == 0) {
                Class<?>[] typeInterfaces = type.getInterfaces();
                List<Class<?>> set = CollectionUtil.createArrayList();

                for (int i = 0; i < typeInterfaces.length; i++) {
                    Class<?> typeInterface = typeInterfaces[i];

                    set.add(typeInterface);
                    set.addAll(getTypeInfo(typeInterface).interfaces);
                }

                for (Iterator<Class<?>> i = superclasses.iterator(); i.hasNext();) {
                    Class<?> typeInterface = i.next();

                    set.addAll(getTypeInfo(typeInterface).interfaces);
                }

                for (Iterator<Class<?>> i = set.iterator(); i.hasNext();) {
                    Class<?> interfaceClass = i.next();

                    if (!interfaces.contains(interfaceClass)) {
                        interfaces.add(interfaceClass);
                    }
                }
            } else {
                for (Iterator<Class<?>> i = getTypeInfo(componentType).interfaces.iterator(); i.hasNext();) {
                    Class<?> componentInterface = i.next();

                    interfaces.add(getArrayClass(componentInterface, dimension));
                }
            }
        }

        /**
         * 将所有的原子类型转换成对应的包装类，其它类型不变。FIXME
         * 
         * @param type 要转换的类型
         * 
         * @return 非原子类型
         */
        private Class<?> getNonPrimitiveType(Class<?> type) {
            if (type.isPrimitive()) {
                return ((PrimitiveInfo<?>) PRIMITIVES.get(type.getName())).wrapperType;
            }

            return type;
        }

        /**
         * 取得 <code>TypeInfo</code> 所代表的java类。
         * 
         * @return <code>TypeInfo</code> 所代表的java类
         */
        public Class<?> getType() {
            return type;
        }

        /**
         * 取得数组元素的类型。
         * 
         * @return 如果是数组, 则返回数组元素的类型, 否则返回 <code>null</code>
         */
        public Class<?> getArrayComponentType() {
            return componentType;
        }

        /**
         * 取得数组的维数。
         * 
         * @return 数组的维数. 如果不是数组, 则返回 <code>0</code>
         */
        public int getArrayDimension() {
            return dimension;
        }

        /**
         * 取得所有的父类。
         * 
         * @return 所有的父类
         */
        public List<Class<?>> getSuperclasses() {
            return Collections.unmodifiableList(superclasses);
        }

        /**
         * 取得所有的接口。
         * 
         * @return 所有的接口
         */
        public List<Class<?>> getInterfaces() {
            return Collections.unmodifiableList(interfaces);
        }
    }

    // ==========================================================================
    // 获取所有父类和接口
    // ==========================================================================

    /** 获取除<code>Object.class</code>以为的所有父类 */
    public static List<Class<?>> getAllSuperclasses(Class<?> clazz) {
        if (clazz == null) {
            return null;
        }
        List<Class<?>> classes = CollectionUtil.createArrayList();
        Class<?> superclass = clazz.getSuperclass();
        while (superclass != null && superclass != Object.class) {
            classes.add(superclass);
            superclass = superclass.getSuperclass();
        }
        return classes;
    }

    public static Class<?>[] getAllSuperclassesAsArray(Class<?> clazz) {
        if (clazz == null) {
            return null;
        }
        List<Class<?>> classes = CollectionUtil.createArrayList();
        Class<?> superclass = clazz.getSuperclass();
        while (superclass != null && superclass != Object.class) {
            classes.add(superclass);
            superclass = superclass.getSuperclass();
        }
        return classes.toArray(new Class<?>[0]);
    }

    public static List<Class<?>> getAllInterfaces(Class<?> clazz) {
        if (clazz == null) {
            return null;
        }

        List<Class<?>> interfacesFound = CollectionUtil.createArrayList();
        getAllInterfaces(clazz, interfacesFound);

        return interfacesFound;
    }

    public static Class<?>[] getAllInterfacesAsArray(Class<?> clazz) {
        if (clazz == null) {
            return null;
        }

        List<Class<?>> interfacesFound = CollectionUtil.createArrayList();
        getAllInterfaces(clazz, interfacesFound);

        return interfacesFound.toArray(new Class<?>[0]);
    }

    // FIXME
    public static Set<Class<?>> getAllInterfacesAsSet(Class<?> clazz, ClassLoader classLoader) {
        if (clazz == null)
            return null;
        if (clazz.isInterface() && isVisible(clazz, classLoader)) {
            Set<Class<?>> set = CollectionUtil.createHashSet(1);
            set.add(clazz);
            return set;
        }
        Set<Class<?>> interfaces = CollectionUtil.createLinkedHashSet();
        while (clazz != null) {
            Class<?>[] ifcs = clazz.getInterfaces();
            for (Class<?> ifc : ifcs) {
                interfaces.addAll(getAllInterfacesAsSet(ifc, classLoader));
            }
            clazz = clazz.getSuperclass();
        }
        return interfaces;
    }

    private static void getAllInterfaces(Class<?> clazz, List<Class<?>> interfacesFound) {
        while (clazz != null) {
            Class<?>[] interfaces = clazz.getInterfaces();

            for (int i = 0; i < interfaces.length; i++) {
                if (!interfacesFound.contains(interfaces[i])) {
                    interfacesFound.add(interfaces[i]);
                    getAllInterfaces(interfaces[i], interfacesFound);
                }
            }

            clazz = clazz.getSuperclass();
        }
    }

    public static boolean isVisible(Class<?> clazz, ClassLoader classLoader) {
        if (classLoader == null) {
            return true;
        }
        try {
            Class<?> actualClass = classLoader.loadClass(clazz.getName());
            return (clazz == actualClass);
            // Else: different interface class found...
        } catch (ClassNotFoundException ex) {
            // No interface class found...
            return false;
        }
    }

    public static boolean isCacheSafe(Class<?> clazz, ClassLoader classLoader) {
        if (clazz == null) {
            return false;
        }
        ClassLoader target = clazz.getClassLoader();
        if (target == null) {
            return false;
        }
        ClassLoader cur = classLoader;
        if (cur == target) {
            return true;
        }
        while (cur != null) {
            cur = cur.getParent();
            if (cur == target) {
                return true;
            }
        }
        return false;
    }

    public static boolean isInterfaceImpl(Class<?> thisClass, Class<?> targetInterface) {
        if (thisClass == null || targetInterface == null) {
            return false;
        }
        for (Class<?> x = thisClass; x != null; x = x.getSuperclass()) {
            Class<?>[] interfaces = x.getInterfaces();
            for (Class<?> i : interfaces) {
                if (i == targetInterface) {
                    return true;
                }
                if (isInterfaceImpl(i, targetInterface)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isSubClass(Class<?> thisClass, Class<?> target) {
        if (thisClass == null || target == null) {
            return false;
        }
        if (target.isInterface() != false) {
            return isInterfaceImpl(thisClass, target);
        }
        for (Class<?> x = thisClass; x != null; x = x.getSuperclass()) {
            if (x == target) {
                return true;
            }
        }
        return false;
    }

    // ==========================================================================
    // 类型匹配。
    // ==========================================================================

    /**
     * 检查一组指定类型 <code>fromClasses</code> 的对象是否可以赋值给另一组类型 <code>classes</code>。
     * <p>
     * 此方法可以用来确定指定类型的参数 <code>object1, object2, ...</code> 是否可以用来调用确定参数类型为 <code>class1, class2,
     * ...</code> 的方法。
     * </p>
     * <p>
     * 对于 <code>fromClasses</code> 的每个元素 <code>fromClass</code> 和 <code>classes</code> 的每个元素 <code>clazz</code>， 按照如下规则：
     * <ol>
     * <li>如果目标类 <code>clazz</code> 为 <code>null</code> ，总是返回 <code>false</code> 。</li>
     * <li>如果参数类型 <code>fromClass</code> 为 <code>null</code> ，并且目标类型 <code>clazz</code> 为非原子类型，则返回 <code>true</code>。 因为
     * <code>null</code> 可以被赋给任何引用类型。</li>
     * <li>调用 <code>Class.isAssignableFrom</code> 方法来确定目标类 <code>clazz</code> 是否和参数类 <code>fromClass</code>
     * 相同或是其父类、接口，如果是，则返回 <code>true</code>。</li>
     * <li>如果目标类型 <code>clazz</code> 为原子类型，那么根据 <a href="http://java.sun.com/docs/books/jls/">The Java Language
     * Specification</a> ，sections 5.1.1, 5.1.2, 5.1.4定义的Widening Primitive Conversion规则，参数类型 <code>fromClass</code>
     * 可以是任何能扩展成该目标类型的原子类型及其包装类。 例如， <code>clazz</code> 为 <code>long</code> ，那么参数类型可以是 <code>byte</code>、
     * <code>short</code>、<code>int</code>、<code>long</code>、<code>char</code> 及其包装类 <code>java.lang.Byte</code>、
     * <code>java.lang.Short</code>、 <code>java.lang.Integer</code>、 <code>java.lang.Long</code> 和
     * <code>java.lang.Character</code> 。如果满足这个条件，则返回 <code>true</code>。</li>
     * <li>不满足上述所有条件，则返回 <code>false</code>。</li>
     * </ol>
     * </p>
     * 
     * @param classes 目标类型列表，如果是 <code>null</code> 总是返回 <code>false</code>
     * @param fromClasses 参数类型列表， <code>null</code> 表示可赋值给任意非原子类型
     * @return 如果可以被赋值，则返回 <code>true</code>
     */
    public static boolean isAssignable(Class<?>[] classes, Class<?>[] fromClasses) {
        if (!ArrayUtil.isSameLength(fromClasses, classes)) {
            return false;
        }

        if (fromClasses == null) {
            fromClasses = Emptys.EMPTY_CLASS_ARRAY;
        }

        if (classes == null) {
            classes = Emptys.EMPTY_CLASS_ARRAY;
        }

        for (int i = 0; i < fromClasses.length; i++) {
            if (isAssignable(classes[i], fromClasses[i]) == false) {
                return false;
            }
        }

        return true;
    }

    /**
     * 检查指定类型 <code>fromClass</code> 的对象是否可以赋值给另一种类型 <code>clazz</code>。
     * <p>
     * 此方法可以用来确定指定类型的参数 <code>object1, object2, ...</code> 是否可以用来调用确定参数类型 <code>class1, class2,
     * ...</code> 的方法。
     * </p>
     * <p>
     * 按照如下规则：
     * <ol>
     * <li>如果目标类 <code>clazz</code> 为 <code>null</code> ，总是返回 <code>false</code> 。</li>
     * <li>如果参数类型 <code>fromClass</code> 为 <code>null</code> ，并且目标类型 <code>clazz</code> 为非原子类型，则返回 <code>true</code>。 因为
     * <code>null</code> 可以被赋给任何引用类型。</li>
     * <li>调用 <code>Class.isAssignableFrom</code> 方法来确定目标类 <code>clazz</code> 是否和参数类 <code>fromClass</code>
     * 相同或是其父类、接口，如果是，则返回 <code>true</code>。</li>
     * <li>如果目标类型 <code>clazz</code> 为原子类型，那么根据 <a href="http://java.sun.com/docs/books/jls/">The Java Language
     * Specification</a> ，sections 5.1.1, 5.1.2, 5.1.4定义的Widening Primitive Conversion规则，参数类型 <code>fromClass</code>
     * 可以是任何能扩展成该目标类型的原子类型及其包装类。 例如， <code>clazz</code> 为 <code>long</code> ，那么参数类型可以是 <code>byte</code>、
     * <code>short</code>、<code>int</code>、<code>long</code>、<code>char</code> 及其包装类 <code>java.lang.Byte</code>、
     * <code>java.lang.Short</code>、 <code>java.lang.Integer</code>、 <code>java.lang.Long</code> 和
     * <code>java.lang.Character</code> 。如果满足这个条件，则返回 <code>true</code>。</li>
     * <li>不满足上述所有条件，则返回 <code>false</code>。</li>
     * </ol>
     * </p>
     * 
     * @param clazz 目标类型，如果是 <code>null</code> 总是返回 <code>false</code>
     * @param fromClass 参数类型， <code>null</code> 表示可赋值给任意非原子类型
     * @return 如果可以被赋值，则返回 <code>null</code>
     */
    public static boolean isAssignable(Class<?> clazz, Class<?> fromClass) {
        if (clazz == null) {
            return false;
        }

        // 如果fromClass是null，只要clazz不是原子类型如int，就一定可以赋值
        if (fromClass == null) {
            return !clazz.isPrimitive();
        }

        // 如果类相同或有父子关系，当然可以赋值
        if (clazz.isAssignableFrom(fromClass)) {
            return true;
        }

        // 对于原子类型，根据JLS的规则进行扩展
        // 目标class为原子类型时，fromClass可以为原子类型和原子类型的包装类型。
        if (clazz.isPrimitive()) {
            return assignmentTable.get(clazz).contains(fromClass);
        }

        return false;
    }

    private final static Map<Class<?>, Set<Class<?>>> assignmentTable = CollectionUtil.createHashMap();

    static {
        // boolean可以接受：boolean
        assignmentTable.put(boolean.class, assignableSet(boolean.class));

        // byte可以接受：byte
        assignmentTable.put(byte.class, assignableSet(byte.class));

        // char可以接受：char
        assignmentTable.put(char.class, assignableSet(char.class));

        // short可以接受：short, byte
        assignmentTable.put(short.class, assignableSet(short.class, byte.class));

        // int可以接受：int、byte、short、char
        assignmentTable.put(int.class, assignableSet(int.class, byte.class, short.class, char.class));

        // long可以接受：long、int、byte、short、char
        assignmentTable.put(long.class, assignableSet(long.class, int.class, byte.class, short.class, char.class));

        // float可以接受：float, long, int, byte, short, char
        assignmentTable.put(float.class,
                assignableSet(float.class, long.class, int.class, byte.class, short.class, char.class));

        // double可以接受：double, float, long, int, byte, short, char
        assignmentTable.put(double.class,
                assignableSet(double.class, float.class, long.class, int.class, byte.class, short.class, char.class));

        Assert.assertTrue(assignmentTable.size() == 8);
    }

    private static Set<Class<?>> assignableSet(Class<?>...types) {
        Set<Class<?>> assignableSet = CollectionUtil.createHashSet();

        for (Class<?> type : types) {
            assignableSet.add(getPrimitiveType(type));
            assignableSet.add(getWrapperTypeIfPrimitive(type));
        }

        return assignableSet;
    }

    // ==========================================================================
    // 定位class的位置。
    // ==========================================================================

    /** 在class loader中查找class的位置。 */
    public static String locateClass(Class<?> clazz) {
        return locateClass(clazz.getName(), clazz.getClassLoader());
    }

    /** 在class loader中查找class的位置。 */
    public static String locateClass(String className) {
        return locateClass(className, null);
    }

    /** 在class loader中查找class的位置。 */
    public static String locateClass(String className, ClassLoader loader) {
        className = Assert.assertNotNull(StringUtil.trimToNull(className), "className");

        if (loader == null) {
            loader = Thread.currentThread().getContextClassLoader();
        }

        String classFile = className.replace('.', '/') + ".class";
        URL locationURL = loader.getResource(classFile);
        String location = null;

        if (locationURL != null) {
            location = locationURL.toExternalForm();

            if (location.endsWith(classFile)) {
                location = location.substring(0, location.length() - classFile.length());
            }

            location = location.replaceAll("^(jar|zip):|!/$", Emptys.EMPTY_STRING);
        }

        return location;
    }

    // ==========================================================================
    // 取得友好类名和package名的方法。
    // ==========================================================================

    /**
     * 取得对象所属的类的友好类名。
     * <p>
     * 类似<code>object.getClass().getName()</code>，但不同的是，该方法用更友好的方式显示数组类型。 例如：
     * </p>
     * <p/>
     * 
     * <pre>
     *  int[].class.getName() = "[I"
     *  ClassUtil.getFriendlyClassName(int[].class) = "int[]"
     * 
     *  Integer[][].class.getName() = "[[Ljava.lang.Integer;"
     *  ClassUtil.getFriendlyClassName(Integer[][].class) = "java.lang.Integer[][]"
     * </pre>
     * <p>
     * 对于非数组的类型，该方法等效于 <code>Class.getName()</code> 方法。
     * </p>
     * <p>
     * 注意，该方法所返回的数组类名只能用于显示给人看，不能用于 <code>Class.forName</code> 操作。
     * </p>
     * 
     * @param object 要显示类名的对象
     * @return 用于显示的友好类名，如果对象为空，则返回<code>null</code>
     */
    public static String getFriendlyClassNameForObject(Object object) {
        if (object == null) {
            return null;
        }

        String javaClassName = object.getClass().getName();

        return toFriendlyClassName(javaClassName, true, javaClassName);
    }

    /**
     * 取得友好的类名。
     * <p>
     * 类似<code>clazz.getName()</code>，但不同的是，该方法用更友好的方式显示数组类型。 例如：
     * </p>
     * <p/>
     * 
     * <pre>
     *  int[].class.getName() = "[I"
     *  ClassUtil.getFriendlyClassName(int[].class) = "int[]"
     * 
     *  Integer[][].class.getName() = "[[Ljava.lang.Integer;"
     *  ClassUtil.getFriendlyClassName(Integer[][].class) = "java.lang.Integer[][]"
     * </pre>
     * <p>
     * 对于非数组的类型，该方法等效于 <code>Class.getName()</code> 方法。
     * </p>
     * <p>
     * 注意，该方法所返回的数组类名只能用于显示给人看，不能用于 <code>Class.forName</code> 操作。
     * </p>
     * 
     * @param object 要显示类名的对象
     * @return 用于显示的友好类名，如果类对象为空，则返回<code>null</code>
     */
    public static String getFriendlyClassName(Class<?> clazz) {
        if (clazz == null) {
            return null;
        }

        String javaClassName = clazz.getName();

        return toFriendlyClassName(javaClassName, true, javaClassName);
    }

    /**
     * 取得友好的类名。
     * <p>
     * <code>className</code> 必须是从 <code>clazz.getName()</code> 所返回的合法类名。该方法用更友好的方式显示数组类型。 例如：
     * </p>
     * <p/>
     * 
     * <pre>
     *  int[].class.getName() = "[I"
     *  ClassUtil.getFriendlyClassName(int[].class) = "int[]"
     * 
     *  Integer[][].class.getName() = "[[Ljava.lang.Integer;"
     *  ClassUtil.getFriendlyClassName(Integer[][].class) = "java.lang.Integer[][]"
     * </pre>
     * <p>
     * 对于非数组的类型，该方法等效于 <code>Class.getName()</code> 方法。
     * </p>
     * <p>
     * 注意，该方法所返回的数组类名只能用于显示给人看，不能用于 <code>Class.forName</code> 操作。
     * </p>
     * 
     * @param javaClassName 要转换的类名
     * @return 用于显示的友好类名，如果原类名为空，则返回 <code>null</code> ，如果原类名是非法的，则返回原类名
     */
    public static String getFriendlyClassName(String javaClassName) {
        return toFriendlyClassName(javaClassName, true, javaClassName);
    }

    /**
     * 将Java类名转换成友好类名。
     * 
     * @param javaClassName Java类名
     * @param processInnerClass 是否将内联类分隔符 <code>'$'</code> 转换成 <code>'.'</code>
     * @return 友好的类名。如果参数非法或空，则返回<code>null</code>。
     */
    static String toFriendlyClassName(String javaClassName, boolean processInnerClass, String defaultIfInvalid) {
        String name = StringUtil.trimToNull(javaClassName);

        if (name == null) {
            return defaultIfInvalid;
        }

        if (processInnerClass) {
            name = name.replace('$', '.');
        }

        int length = name.length();
        int dimension = 0;

        // 取得数组的维数，如果不是数组，维数为0
        for (int i = 0; i < length; i++, dimension++) {
            if (name.charAt(i) != '[') {
                break;
            }
        }

        // 如果不是数组，则直接返回
        if (dimension == 0) {
            return name;
        }

        // 确保类名合法
        if (length <= dimension) {
            return defaultIfInvalid; // 非法类名
        }

        // 处理数组
        StringBuilder componentTypeName = new StringBuilder();

        switch (name.charAt(dimension)) {
            case 'Z':
                componentTypeName.append("boolean");
                break;

            case 'B':
                componentTypeName.append("byte");
                break;

            case 'C':
                componentTypeName.append("char");
                break;

            case 'D':
                componentTypeName.append("double");
                break;

            case 'F':
                componentTypeName.append("float");
                break;

            case 'I':
                componentTypeName.append("int");
                break;

            case 'J':
                componentTypeName.append("long");
                break;

            case 'S':
                componentTypeName.append("short");
                break;

            case 'L':
                if (name.charAt(length - 1) != ';' || length <= dimension + 2) {
                    return defaultIfInvalid; // 非法类名
                }

                componentTypeName.append(name.substring(dimension + 1, length - 1));
                break;

            default:
                return defaultIfInvalid; // 非法类名
        }

        for (int i = 0; i < dimension; i++) {
            componentTypeName.append("[]");
        }

        return componentTypeName.toString();
    }

    /**
     * 取得指定对象所属的类的简单类名，不包括package名。
     * <p>
     * 此方法可以正确显示数组和内联类的名称。 例如：
     * <p/>
     * 
     * <pre>
     *  ClassUtil.getSimpleClassNameForObject(Boolean.TRUE) = "Boolean"
     *  ClassUtil.getSimpleClassNameForObject(new Boolean[10]) = "Boolean[]"
     *  ClassUtil.getSimpleClassNameForObject(new int[1][2]) = "int[][]"
     * </pre>
     * <p>
     * 本方法和<code>Class.getSimpleName()</code>的区别在于，本方法会保留inner类的外层类名称。
     * </p>
     * 
     * @param object 要查看的对象
     * @return 简单类名，如果对象为 <code>null</code> ，则返回 <code>null</code>
     */
    public static String getSimpleClassNameForObject(Object object) {
        if (object == null) {
            return null;
        }

        return getSimpleClassName(object.getClass().getName());
    }

    /**
     * 取得指定对象所属的类的简单类名，不包括package名。
     * <p>
     * 此方法可以正确显示数组和内联类的名称。 例如：
     * <p/>
     * 
     * <pre>
     *  ClassUtil.getSimpleClassNameForObject(Boolean.TRUE) = "Boolean"
     *  ClassUtil.getSimpleClassNameForObject(new Boolean[10]) = "Boolean[]"
     *  ClassUtil.getSimpleClassNameForObject(new int[1][2]) = "int[][]"
     * </pre>
     * <p>
     * 本方法和<code>Class.getSimpleName()</code>的区别在于，本方法会保留inner类的外层类名称。
     * </p>
     * 
     * @param object 要查看的对象
     * @return 简单类名，如果对象为 <code>null</code> ，则返回 <code>null</code>
     */
    public static String getSimpleClassNameForObject(Object object, boolean processInnerClass) {
        if (object == null) {
            return null;
        }

        return getSimpleClassName(object.getClass().getName(), processInnerClass);
    }

    /**
     * 取得简单类名，不包括package名。
     * <p>
     * 此方法可以正确显示数组和内联类的名称。 例如：
     * <p/>
     * 
     * <pre>
     *  ClassUtil.getSimpleClassName(Boolean.class) = "Boolean"
     *  ClassUtil.getSimpleClassName(Boolean[].class) = "Boolean[]"
     *  ClassUtil.getSimpleClassName(int[][].class) = "int[][]"
     *  ClassUtil.getSimpleClassName(Map.Entry.class) = "Map.Entry"
     * </pre>
     * <p>
     * 本方法和<code>Class.getSimpleName()</code>的区别在于，本方法会保留inner类的外层类名称。
     * </p>
     * 
     * @param clazz 要查看的类
     * @return 简单类名，如果类为 <code>null</code> ，则返回 <code>null</code>
     */
    public static String getSimpleClassName(Class<?> clazz) {
        if (clazz == null) {
            return null;
        }

        return getSimpleClassName(clazz.getName());
    }

    /**
     * 取得简单类名，不包括package名。
     * <p>
     * 此方法可以正确显示数组和内联类的名称。 例如：
     * <p/>
     * 
     * <pre>
     *  ClassUtil.getSimpleClassName(Boolean.class) = "Boolean"
     *  ClassUtil.getSimpleClassName(Boolean[].class) = "Boolean[]"
     *  ClassUtil.getSimpleClassName(int[][].class) = "int[][]"
     *  ClassUtil.getSimpleClassName(Map.Entry.class) = "Map.Entry"
     * </pre>
     * <p>
     * 本方法和<code>Class.getSimpleName()</code>的区别在于，本方法会保留inner类的外层类名称。
     * </p>
     * 
     * @param clazz 要查看的类
     * @return 简单类名，如果类为 <code>null</code> ，则返回 <code>null</code>
     */
    public static String getSimpleClassName(Class<?> clazz, boolean proccessInnerClass) {
        if (clazz == null) {
            return null;
        }

        return getSimpleClassName(clazz.getName(), proccessInnerClass);
    }

    /**
     * 取得类名，不包括package名。
     * <p>
     * 此方法可以正确显示数组和内联类的名称。 例如：
     * <p/>
     * 
     * <pre>
     *  ClassUtil.getSimpleClassName(Boolean.class.getName()) = "Boolean"
     *  ClassUtil.getSimpleClassName(Boolean[].class.getName()) = "Boolean[]"
     *  ClassUtil.getSimpleClassName(int[][].class.getName()) = "int[][]"
     *  ClassUtil.getSimpleClassName(Map.Entry.class.getName()) = "Map.Entry"
     * </pre>
     * <p>
     * 本方法和<code>Class.getSimpleName()</code>的区别在于，本方法会保留inner类的外层类名称。
     * </p>
     * 
     * @param javaClassName 要查看的类名
     * @return 简单类名，如果类名为空，则返回 <code>null</code>
     */
    public static String getSimpleClassName(String javaClassName) {
        return getSimpleClassName(javaClassName, true);
    }

    /**
     * 取得类名，不包括package名。
     * <p>
     * 此方法可以正确显示数组和内联类的名称。 例如：
     * <p/>
     * 
     * <pre>
     *  ClassUtil.getSimpleClassName(Boolean.class.getName()) = "Boolean"
     *  ClassUtil.getSimpleClassName(Boolean[].class.getName()) = "Boolean[]"
     *  ClassUtil.getSimpleClassName(int[][].class.getName()) = "int[][]"
     *  ClassUtil.getSimpleClassName(Map.Entry.class.getName()) = "Map.Entry"
     * </pre>
     * <p>
     * 本方法和<code>Class.getSimpleName()</code>的区别在于，本方法会保留inner类的外层类名称。
     * </p>
     * 
     * @param javaClassName 要查看的类名
     * @return 简单类名，如果类名为空，则返回 <code>null</code>
     */
    public static String getSimpleClassName(String javaClassName, boolean proccesInnerClass) {
        String friendlyClassName = toFriendlyClassName(javaClassName, false, null);

        if (friendlyClassName == null) {
            return javaClassName;
        }

        if (proccesInnerClass) {
            char[] chars = friendlyClassName.toCharArray();
            int beginIndex = 0;

            for (int i = chars.length - 1; i >= 0; i--) {
                if (chars[i] == '.') {
                    beginIndex = i + 1;
                    break;
                } else if (chars[i] == '$') {
                    chars[i] = '.';
                }
            }

            return new String(chars, beginIndex, chars.length - beginIndex);
        } else {
            return friendlyClassName.substring(friendlyClassName.lastIndexOf(".") + 1);
        }
    }

    /** 取得简洁的method描述。 */
    public static String getSimpleMethodSignature(Method method) {
        return getSimpleMethodSignature(method, false, false, false, false);
    }

    /** 取得简洁的method描述。 */
    public static String getSimpleMethodSignature(Method method, boolean withClassName) {
        return getSimpleMethodSignature(method, false, false, withClassName, false);
    }

    /** 取得简洁的method描述。 */
    public static String getSimpleMethodSignature(Method method, boolean withModifiers, boolean withReturnType,
            boolean withClassName, boolean withExceptionType) {
        if (method == null) {
            return null;
        }

        StringBuilder buf = new StringBuilder();

        if (withModifiers) {
            buf.append(Modifier.toString(method.getModifiers())).append(' ');
        }

        if (withReturnType) {
            buf.append(getSimpleClassName(method.getReturnType())).append(' ');
        }

        if (withClassName) {
            buf.append(getSimpleClassName(method.getDeclaringClass())).append('.');
        }

        buf.append(method.getName()).append('(');

        Class<?>[] paramTypes = method.getParameterTypes();

        for (int i = 0; i < paramTypes.length; i++) {
            Class<?> paramType = paramTypes[i];

            buf.append(getSimpleClassName(paramType));

            if (i < paramTypes.length - 1) {
                buf.append(", ");
            }
        }

        buf.append(')');

        if (withExceptionType) {
            Class<?>[] exceptionTypes = method.getExceptionTypes();

            if (ArrayUtil.isNotEmpty(exceptionTypes)) {
                buf.append(" throws ");

                for (int i = 0; i < exceptionTypes.length; i++) {
                    Class<?> exceptionType = exceptionTypes[i];

                    buf.append(getSimpleClassName(exceptionType));

                    if (i < exceptionTypes.length - 1) {
                        buf.append(", ");
                    }
                }
            }
        }

        return buf.toString();
    }

    /**
     * 取得类名，不包括package名。
     * 
     * <p>
     * 此方法可以正确显示数组和内联类的名称。
     * </p>
     * 
     * <p>
     * 例如：
     * 
     * <pre>
     *  ClassUtil.getShortClassName(Boolean.class.getName()) = &quot;Boolean&quot; ClassUtil.getShortClassName(Boolean[].class.getName()) = &quot;Boolean[]&quot; ClassUtil.getShortClassName(int[][].class.getName()) = &quot;int[][]&quot; ClassUtil.getShortClassName(Map.Entry.class.getName()) = &quot;Map.Entry&quot;
     * </pre>
     * 
     * </p>
     * 
     * @param className 要查看的类名
     * 
     * @return 短类名，如果类名为空，则返回 <code>null</code>
     */
    public static String getShortClassName(String className) {
        if (StringUtil.isEmpty(className)) {
            return className;
        }

        // 转换成直观的类名
        className = getSimpleClassName(className, false);

        char[] chars = className.toCharArray();
        int lastDot = 0;

        for (int i = 0; i < chars.length; i++) {
            if (chars[i] == PACKAGE_SEPARATOR_CHAR) {
                lastDot = i + 1;
            } else if (chars[i] == INNER_CLASS_SEPARATOR_CHAR) {
                chars[i] = PACKAGE_SEPARATOR_CHAR;
            }
        }

        return new String(chars, lastDot, chars.length - lastDot);
    }

    /**
     * 取得短类名，不包括package名。
     * 
     * <p>
     * 此方法可以正确显示数组和内联类的名称。
     * </p>
     * 
     * <p>
     * 例如：
     * 
     * <pre>
     *  ClassUtil.getShortClassName(Boolean.class) = &quot;Boolean&quot; ClassUtil.getShortClassName(Boolean[].class) = &quot;Boolean[]&quot; ClassUtil.getShortClassName(int[][].class) = &quot;int[][]&quot; ClassUtil.getShortClassName(Map.Entry.class) = &quot;Map.Entry&quot;
     * </pre>
     * 
     * </p>
     * 
     * @param clazz 要查看的类
     * 
     * @return 短类名，如果类为 <code>null</code> ，则返回 <code>null</code>
     */
    public static String getShortClassName(Class<?> clazz) {
        if (clazz == null) {
            return null;
        }

        return getShortClassName(clazz.getName());
    }

}
