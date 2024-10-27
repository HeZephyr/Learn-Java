package example.net.nio.demo4;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;

class SerializerTest {

    @Test
    void testSerialization() {
        // Create a Person object
        Person person = new Person(123, "Alice");

        // Serialize the Person object
        byte[] serializedData = Serializer.serialize(person);
        System.out.println("Serialized Data: " + Arrays.toString(serializedData));

        // Deserialize the byte array back to a Person object
        Person deserializedPerson = Serializer.deserialize(serializedData);
        System.out.println("Deserialized Person: " + deserializedPerson);

        // Assertions to check if the serialization and deserialization work correctly
        assertNotNull(serializedData, "Serialized data should not be null");
        assertEquals(person.getId(), deserializedPerson.getId(), "IDs should match after deserialization");
        assertEquals(person.getName(), deserializedPerson.getName(), "Names should match after deserialization");
    }
}
