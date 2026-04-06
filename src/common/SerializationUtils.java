package common;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public final class SerializationUtils {

    private SerializationUtils() {
    }

    public static byte[] serialize(Object object) throws IOException {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutputStream out = new ObjectOutputStream(bos)) {
            out.writeObject(object);
            out.flush();
            return bos.toByteArray();
        }
    }

    public static Object deserialize(byte[] bytes) throws IOException, ClassNotFoundException {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
             ObjectInputStream in = new ObjectInputStream(bis)) {
            return in.readObject();
        }
    }
}