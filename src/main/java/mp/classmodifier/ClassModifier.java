package mp.classmodifier;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

import mp.classmodifier.exception.ClassModificationException;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.NotFoundException;

public class ClassModifier {
    private final ClassPool pool = ClassPool.getDefault();
    private CtClass cc;

    public ClassModifier(Class<?> objClass) throws ClassModificationException {
        try {
            cc = pool.get(objClass.getName());
            cc.defrost();

            // clear all existing fields
            for (CtField field : cc.getDeclaredFields()) {
                cc.removeField(field);
            }

            // add the field in objClass
            for (Field field : objClass.getDeclaredFields()) {
                addField(field.getType(), field.getName());
            }
            cc.rebuildClassFile();
        } catch (NotFoundException e) {
            throw new ClassModificationException("Class " + objClass.getName()
                    + " not found", e);
        }
    }

    /**
     * Add a new field to this class
     *
     * @param fieldType
     *            type of the new field
     * @param fieldName
     *            name of the new field
     * @return
     * @throws ClassModificationException
     */
    public ClassModifier addField(Class<?> fieldType, String fieldName)
            throws ClassModificationException {
        String declaration = String.format("public %s %s;",
                fieldType.getName(), fieldName);
        try {
            CtField newField = CtField.make(declaration, cc);
            cc.addField(newField);
            return this;
        } catch (CannotCompileException e) {
            final String message = String.format("Cannot add \"%s %s\" to %s",
                    fieldType, fieldName, cc.getName());
            throw new ClassModificationException(message, e);
        }
    }

    /**
     * Remove a field from this class
     *
     * @param fieldName
     *            name of the field to remove
     * @return
     * @throws ClassModificationException
     */
    public ClassModifier removeField(String fieldName)
            throws ClassModificationException {
        try {
            CtField fieldToRemove = cc.getDeclaredField(fieldName);
            cc.removeField(fieldToRemove);
            return this;
        } catch (NotFoundException e) {
            String message = String.format("There is no field \"%s\" in %s",
                    fieldName, cc.getName());
            throw new ClassModificationException(message, e);
        }
    }

    /**
     * Return the modified class, add it to a class loader
     *
     * @param loader
     *            the target class loader
     * @return
     * @throws CannotCompileException
     */
    public Class<?> toClass(ClassLoader loader)
            throws ClassModificationException {
        try {
            return cc.toClass(loader, null);
        } catch (CannotCompileException e) {
            throw new ClassModificationException("Cannot compile class "
                    + cc.getName(), e);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("class ");
        sb.append(cc.getName());

        sb.append("[");
        CtField[] fields = cc.getDeclaredFields();
        for (CtField field : fields) {
            sb.append(field.getFieldInfo().toString()).append(",");
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append("]");
        return sb.toString();
    }

    /**
     * Return names of the field of this class
     *
     * @return
     */
    public Set<String> getFieldNames() {
        Set<String> fieldNames = new HashSet<>();
        for (CtField field : cc.getDeclaredFields()) {
            fieldNames.add(field.getName());
        }
        return fieldNames;
    }

    public CtClass getCtClass() {
        return cc;
    }
}
