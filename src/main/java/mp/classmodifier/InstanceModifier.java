package mp.classmodifier;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import mp.classmodifier.exception.InstanceModificationException;

public class InstanceModifier {
    private Map<String, Field> fieldMap = new HashMap<>();
    private final Class<?> cls;
    private Object instance;

    /**
     * Construct a new instance of a class
     *
     * @param modifiedClass
     *            class of the instance
     * @throws InstanceModificationException
     */
    public InstanceModifier(Class<?> modifiedClass)
            throws InstanceModificationException {
        cls = modifiedClass;
        try {
            instance = modifiedClass.newInstance();
            for (Field field : modifiedClass.getDeclaredFields()) {
                fieldMap.put(field.getName(), field);
            }
        } catch (Throwable e) {
            throw new InstanceModificationException(e);
        }
    }

    /**
     * Copy constructor, will not modified the passed in object
     *
     * @param obj
     *            the object to copy
     * @throws InstanceModificationException
     */
    public InstanceModifier(Object obj) throws InstanceModificationException {
        cls = obj.getClass();
        try {
            instance = cls.newInstance();
            for (Field field : cls.getDeclaredFields()) {
                fieldMap.put(field.getName(), field);
                field.set(instance, field.get(obj));
            }
        } catch (Throwable e) {
            throw new InstanceModificationException(e);
        }
    }

    /**
     * Create an instance of modifiedClass with values copied from
     * objOfUnmodifiedClass
     *
     * @param modifiedClass
     * @param objOfUnmodifiedClass
     * @throws InstanceModificationException
     */
    public InstanceModifier(Class<?> modifiedClass, Object objOfUnmodifiedClass)
            throws InstanceModificationException {
        cls = modifiedClass;
        Class<?> objClass = objOfUnmodifiedClass.getClass();
        try {
            instance = cls.newInstance();
            for (Field field : cls.getDeclaredFields()) {
                fieldMap.put(field.getName(), field);
                Object objFieldValue = objClass.getDeclaredField(
                        field.getName()).get(objOfUnmodifiedClass);
                field.set(instance, objFieldValue);
            }
        } catch (Throwable e) {
            throw new InstanceModificationException(e);
        }
    }

    /**
     * Return the value of the field
     *
     * @param fieldName
     *            name of the field
     * @return
     * @throws InstanceModificationException
     */
    public Object get(String fieldName) throws InstanceModificationException {
        try {
            Field field = cls.getDeclaredField(fieldName);
            return field.get(instance);
        } catch (Throwable e) {
            throw new InstanceModificationException(e);
        }
    }

    /**
     * Set the field value
     *
     * @param fieldName
     *            name of the field
     * @param fieldValue
     *            value to set to
     * @return
     * @throws InstanceModificationException
     *             if there is no such field or an error happens while setting
     *             the field value using reflection
     */
    public InstanceModifier set(String fieldName, Object fieldValue)
            throws InstanceModificationException {
        Field field = fieldMap.get(fieldName);
        if (field == null) {
            StringBuilder sb = new StringBuilder("No field named ")
                    .append(fieldName).append(" in ").append(cls.getName());
            throw new InstanceModificationException(sb.toString());
        }

        field.setAccessible(true);
        try {
            field.set(instance, fieldValue);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            throw new InstanceModificationException(e);
        }
        return this;
    }

    /**
     * Return the object instance
     *
     * @return
     */
    public Object toInstance() {
        return instance;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(cls.getSimpleName());

        sb.append("(modified) [");
        for (String fieldName : fieldMap.keySet()) {
            try {
                sb.append(fieldName).append("=").append(get(fieldName))
                        .append(", ");
            } catch (InstanceModificationException e) {
            }
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.deleteCharAt(sb.length() - 1);
        sb.append("]");

        return sb.toString();
    }

    public Set<String> getFieldNames() {
        return fieldMap.keySet();
    }
}
