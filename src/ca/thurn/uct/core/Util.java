package ca.thurn.uct.core;

import java.util.Map;

public class Util {
  public static <K,V> V getWithDefault(Map<K, V> map, K key, V defaultValue) {
    V ret = map.get(key);
    if (ret == null) {
        return defaultValue;
    }
    return ret;
  }
}
