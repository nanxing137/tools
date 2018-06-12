package net.byteknight.charset;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CharasetUtil {
	private static final Set<Charset> charsets = new HashSet<Charset>() {
		{

			Stream<Charset> map = Charset.availableCharsets().keySet().parallelStream().map((t) -> {
				return Charset.forName(t);
			}).filter((t) -> {
				return t.canEncode();
			});
			Set<Charset> collect = map.collect(Collectors.toSet());
			addAll(collect);
		}
	};

	/**
	 * @author Thornhill
	 * @param string
	 *            要查询字符集的String
	 * @return 返回一个包含所有可对穿入String编码的{@code Stream<Charset>}对象</br>
	 *         如果对象不存在，返回{@code Stream.<Charset>empty()}
	 */
	public static Stream<Charset> getCharset(String string) {

		Stream<Charset> parallelStream = charsets.parallelStream();
		Stream<Charset> filter = parallelStream.filter((t) -> {
			ByteBuffer encode = t.encode(string);
			CharBuffer decode = t.decode(encode);
			if (string.equals(decode.toString())) {
				return true;
			}

			return false;

		});

		return filter;
	}

}
