package example.net.nio.demo4;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class Serializer {

    // Serialize Person object to byte array
    public static byte[] serialize(Person person) {
        byte[] nameBytes = person.getName().getBytes(StandardCharsets.UTF_8);
        int totalLength = Integer.BYTES + Integer.BYTES + nameBytes.length;

        ByteBuffer buffer = ByteBuffer.allocate(totalLength);
        buffer.putInt(person.getId());                     // Add id as int
        buffer.putInt(nameBytes.length);                   // Add length of name
        buffer.put(nameBytes);                             // Add name as bytes

        return buffer.array();
    }

    // Deserialize byte array back to Person object
    public static Person deserialize(byte[] data) {
        ByteBuffer buffer = ByteBuffer.wrap(data);
        int id = buffer.getInt();                          // Read id
        int nameLength = buffer.getInt();                  // Read length of name
        byte[] nameBytes = new byte[nameLength];
        buffer.get(nameBytes);                             // Read name bytes

        String name = new String(nameBytes, StandardCharsets.UTF_8);
        return new Person(id, name);
    }
}
