package mali.core.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import redis.clients.util.SafeEncoder;

/**
 * 
 */
public class RedisUtil {

	/**
	 * This helper method will assume the List<byte[]> being presented is the list returned from a {@link JRedis} method such as {@link JRedis#smembers(String)}, and that this list contains the {@link DefaultCodec#encode(Serializable)}ed bytes of the parametric type <code>T</code>. <p> Specifically, this method will instantiate an {@link ArrayList} for type T, of equal size to the size of bytelist {@link List}. Then it will iterate over the byte list and for each byte[] list item call {@link DefaultCodec#decode(byte[])}. <p> <b>Usage example:</b>
	 * 
	 * <pre> <code> List<byte[]> memberBytes = redis.smembers("my-object-set"); List<MySerializableClass> members = decode (memberBytes); </code> </pre>
	 * 
	 * @param <T>
	 * @param byteList
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static final <T extends Serializable> List<T> decode(List<byte[]> byteList) {
		List<T> objectList = new ArrayList<T>(byteList.size());
		for (byte[] bytes : byteList) {
			if (null != bytes) {
				T object = (T) decode(bytes);
				objectList.add(object);
			} else {
				objectList.add(null);
			}
		}
		return objectList;
	}

	@SuppressWarnings("unchecked")
	public static final <T extends Serializable> List<T> decode(List<byte[]> byteList, int offset, int size) {
		if (byteList.size() == 0 || (byteList.size() - 1) < offset) {
			return new ArrayList<T>(0);
		}
		if (offset < 0) {
			offset = 0;
		}
		List<T> objectList = new ArrayList<T>(byteList.size() - offset);
		for (int i = offset; i < byteList.size() && i < (offset + size); i++) {
			byte[] bytes = byteList.get(i);
			if (null != bytes) {
				T object = (T) decode(bytes);
				objectList.add(object);
			} else {
				objectList.add(null);
			}
		}
		return objectList;
	}

	/**
	 * This helper method will serialize the given serializable object of type T to a byte[], suitable for use as a value for a redis key, regardless of the key type.
	 * 
	 * @param <T>
	 * @param obj
	 * @return
	 */
	public static final <T extends Serializable> byte[] encode(T obj) {
		byte[] bytes = null;
		ByteArrayOutputStream bout = null;
		ObjectOutputStream out = null;
		try {
			bout = new ByteArrayOutputStream();
			out = new ObjectOutputStream(bout);
			out.writeObject(obj);
			bytes = bout.toByteArray();
		} catch (IOException e) {
			throw new RuntimeException("Error serializing object" + obj + " => " + e);
		} finally {
			if (out != null)
				try {
					out.close();
				} catch (Exception e) {
				}
			if (bout != null)
				try {
					bout.close();
				} catch (Exception e) {
				}
		}
		// this for development phase only -- will be removed. (A bit of
		// performance hit.)
		// finally {
		// // test it!
		// try {
		// T decoded = decode(bytes); // we want this compile warning to
		// remember to remove this in future.
		// }
		// catch (Exception e) {
		// e.printStackTrace();
		// System.err.format("error in verifying the decoding of the encoded object %s",
		// obj.getClass().getName());
		// }
		// }
		return bytes;
	}

	/**
	 * This helper method will assume that the byte[] provided are the serialized bytes obtainable for an instance of type T obtained from {@link ObjectOutputStream} and subsequently stored as a value for a Redis key (regardless of key type). <p> Specifically, this method will simply do:
	 * 
	 * <pre> <code> ObjectInputStream oin = new ObjectInputStream(new ByteArrayInputStream(bytes)); t = (T) oin.readObject(); </code> </pre>
	 * 
	 * and returning the reference <i>t</i>, and throwing any exceptions encountered along the way. <p> This method is the decoding peer of {@link DefaultCodec#encode(Serializable)}, and it is assumed (and certainly recommended) that you use these two methods in tandem. <p> Naturally, all caveats, rules, and considerations that generally apply to {@link Serializable} and the Object Serialization specification apply.
	 * 
	 * @param <T>
	 * @param bytes
	 * @return the instance for <code><b>T</b></code>
	 */
	@SuppressWarnings("unchecked")
	public static final <T extends Serializable> T decode(byte[] bytes) {
		if (bytes == null) {
			return null;
		}
		T t = null;
		Exception thrown = null;
		ByteArrayInputStream in = null;
		ObjectInputStream oin = null;
		try {
			in = new ByteArrayInputStream(bytes);
			oin = new ObjectInputStream(in);
			t = (T) oin.readObject();
		} catch (IOException e) {
			thrown = e;
		} catch (ClassNotFoundException e) {
			thrown = e;
		} catch (ClassCastException e) {
			thrown = e;
		} finally {
			if (oin != null)
				try {
					oin.close();
				} catch (Exception e) {
				}
			if (in != null)
				try {
					in.close();
				} catch (Exception e) {
				}
			if (null != thrown)
				throw new RuntimeException("Error decoding byte[] data to instantiate java object - " + "data at key may not have been of this type or even an object", thrown);
		}
		return t;
	}

	/**
	 * Convert a data map to binary bytes known by jedis.
	 */
	public static final <T extends Serializable> byte[][] encode(Map<String, T> map) {
		if (map == null) {
			return null;
		}
		byte[][] mappings = new byte[map.size() * 2][];
		int i = 0;
		for (Map.Entry<String, T> entry : map.entrySet()) {
			mappings[i++] = SafeEncoder.encode(entry.getKey());
			mappings[i++] = encode(entry.getValue());
		}

		return mappings;
	}

	/**
	 * 序列化
	 * 
	 * @param object
	 * @return
	 */
	public static byte[] serialize(Object object) {
		ObjectOutputStream oos = null;
		ByteArrayOutputStream baos = null;
		try {
			// 序列化
			baos = new ByteArrayOutputStream();
			oos = new ObjectOutputStream(baos);
			oos.writeObject(object);
			byte[] bytes = baos.toByteArray();
			return bytes;
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			if (oos != null)
				try {
					oos.close();
				} catch (Exception e) {
				}
			if (baos != null)
				try {
					baos.close();
				} catch (Exception e) {
				}
		}
	}

	/**
	 * 反序列化
	 * 
	 * @param bytes
	 * @return
	 */
	public static Object unserialize(byte[] bytes) {
		ByteArrayInputStream bais = null;
		ObjectInputStream ois = null;
		try {
			// 反序列化
			bais = new ByteArrayInputStream(bytes);
			ois = new ObjectInputStream(bais);
			return ois.readObject();
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			if (ois != null)
				try {
					ois.close();
				} catch (Exception e) {
				}
			if (bais != null)
				try {
					bais.close();
				} catch (Exception e) {
				}
		}
	}
}
