package net.byteknight.fastmap;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;


public class FastMap {

	private FastMap() {
	}

	public static final BiFunction SUM = (c1, c2) -> {
		((Collection) c1).addAll((Collection) c2);
		return c1;
	};
	public static final BiFunction KEEPOLD = (c1, c2) -> c1;
	public static final BiFunction KEEPNEW = (c1, c2) -> c2;

	/**
	 * @param <E> the type of list
	 * @param <K> the type of key
	 * @param <N> The type of each node in the value
	 * @param <M> the type of result map3
	 * @param <V> the type of value
	 * @param e 需要转换的list
	 * @param generateKey 分类器函数将输入元素映射到键
	 * @param generateNode 分类器函数将输入元素映射到值中的节点
	 * @param mapSupplier 这个函数在被调用时产生一个新的{@code Map}
	 * @param valueSupplier 这个函数在被调用时产生一个新的{@code Collection}
	 * @param mergeFunction 合并函数，用于解决键之间的冲突
	 * 						使用提供的合并函数{@link Map#merge(Object, Object, BiFunction)}
	 * @return 一个将元素收集到一个指定的{@code Map}中的工具</br>
	 * 其键是将键映射功能应用于输入的元素</br>
	 * 其值是应用值映射的结果组成指定的{@code Collection}
	 * 
	 */
	public static <E, K, N, M extends Map<K, V>, V extends Collection<N>> Map<K, V> toMap(Collection<? extends E> e,
			Function<? super E, ? extends K> generateKey, Function<? super E, ? extends N> generateNode,
			Supplier<M> mapSupplier, Supplier<V> valueSupplier,
			BiFunction<? super V, ? super V, ? extends V> mergeFunction) {
		/**
		 * 返回的结果为Map<K,Collect<V>>
		 */

		// 最终返回的结果
		Map<K, V> map = mapSupplier.get();

		// 结果中的V 也是一个集合

		Iterator<? extends E> iterator = e.iterator();
		while (iterator.hasNext()) {
			V vResult = valueSupplier.get();
			E tempE = iterator.next();
			K tempK = generateKey.apply(tempE);
			N tempV = generateNode.apply(tempE);
			vResult.add(tempV);

			// map.merge(tempK, vResult, (oldV, newV) -> {
			// oldV.addAll(newV);});
			map.merge(tempK, vResult, mergeFunction::apply);

			// vResult.clear();
		}

		return map;
	}
}
