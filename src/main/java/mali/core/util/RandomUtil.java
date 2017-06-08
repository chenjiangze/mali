package mali.core.util;

import java.util.concurrent.ThreadLocalRandom;

public class RandomUtil {

	private static char[] charSeq = new char[62];
	static {
		int j = 0;
		for (int i = 0; i < 10; i++) {
			charSeq[j] = (char) (48 + i);
			j++;
		}
		for (int i = 0; i < 26; i++) {
			charSeq[j] = (char) (65 + i);
			j++;
		}
		for (int i = 0; i < 26; i++) {
			charSeq[j] = (char) (97 + i);
			j++;
		}
	}

	private static RandomUtil instance;

	public static RandomUtil getInstance() {
		if (instance == null) {
			instance = new RandomUtil();
		}

		return instance;
	}

	private RandomUtil() {
	}

	/**
	 * 生成随机字符串
	 * 
	 * @param length
	 *            字符串长度
	 * @return
	 */
	public static String getRandomCode(int length) {
		ThreadLocalRandom random = ThreadLocalRandom.current();
		String code = "";
		for (int i = 0; i < length; i++) {
			code += charSeq[random.nextInt(62)];
		}
		return code;
	}

}
