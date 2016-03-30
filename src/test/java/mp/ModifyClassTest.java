package mp;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import mp.classmodifier.BlankClassLoader;
import mp.classmodifier.ClassModifier;
import mp.dto.Child;
import mp.dto.Parent;

import org.junit.Test;

public class ModifyClassTest {
    @Test
    public void testModifyClassSingle() throws Throwable {
        // Construct a new modifier for the class
        ClassModifier cm = new ClassModifier(Child.class);

        cm.addField(int.class, "value2").addField(String.class, "yodel")
                .addField(Date.class, "createdAt").removeField("key");

        Set<String> expectedFieldNames = new HashSet<>(Arrays.asList("value",
                "value2", "yodel", "createdAt"));

        assertEquals(expectedFieldNames, cm.getFieldNames());
    }

    @Test
    public void testModifyClassNested() throws Throwable {
        ClassLoader loader = new BlankClassLoader();

        ClassModifier cm1 = new ClassModifier(Child.class);
        cm1.addField(int.class, "value2").removeField("key");

        ClassModifier cm2 = new ClassModifier(Parent.class);
        cm2.addField(int.class, "b").removeField("a");

        // Important: Write both classes to class loader
        cm1.toClass(loader);
        Class<?> newParentClass = cm2.toClass(loader);

        // Check parent fields
        Set<String> expectedParent = new HashSet<>(Arrays.asList("b", "dto"));
        Set<String> actualParent = Arrays
                .asList(newParentClass.getDeclaredFields()).stream()
                .map(field -> field.getName()).collect(Collectors.toSet());
        assertEquals(expectedParent, actualParent);

        // Check child fields
        Set<String> expectedChild = new HashSet<>(Arrays.asList("value",
                "value2"));
        Set<String> actualChild = Arrays
                .asList(newParentClass.getDeclaredField("dto").getType()
                        .getDeclaredFields()).stream()
                .map(field -> field.getName()).collect(Collectors.toSet());
        assertEquals(expectedChild, actualChild);
    }
}
