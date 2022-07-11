package org.dows.framework.rest;

import java.io.Serializable;
import java.util.*;

/**
 * @author lait.zhang@gmail.com
 * @description: TODO
 * @weixin SH330786
 * @date 4/6/2022
 */
public class RestParameters implements Map<String, SortedSet<String>>, Serializable {
    private TreeMap<String, SortedSet<String>> wrappedMap = new TreeMap();

    public RestParameters() {
    }

    public SortedSet<String> put(String key, SortedSet<String> value) {
        return this.wrappedMap.put(key, value);
    }

    public SortedSet<String> put(String key, SortedSet<String> values, boolean percentEncode) {
        if (!percentEncode) {
            return this.wrappedMap.put(key, values);
        } else {
            this.remove(key);
            Iterator<String> var4 = values.iterator();

            while (var4.hasNext()) {
                String v = var4.next();
                this.put(key, v, true);
            }

            return this.get(key);
        }
    }

    public String put(String key, String value) {
        return this.put(key, value, false);
    }

    public String put(String key, String value, boolean percentEncode) {
        /**
         * todo 调用接口加密 key
         * key = percentEncode ? ApiCryptor.percentEncode(key) : key;
         */
        SortedSet<String> values = this.wrappedMap.get(key);
        if (values == null) {
            values = new TreeSet();
            this.wrappedMap.put(key, values);
        }

        if (value != null) {
            /**
             * todo 调用接口加密 value
             */
            values.add(value);
        }

        return value;
    }

    public String putNull(String key, String nullString) {
        return this.put(key, nullString);
    }

    public void putAll(Map<? extends String, ? extends SortedSet<String>> m) {
        this.wrappedMap.putAll(m);
    }

    public void putAll(Map<String, SortedSet<String>> m, boolean percentEncode) {
        if (percentEncode) {
            Iterator<String> var3 = m.keySet().iterator();
            while (var3.hasNext()) {
                String key = var3.next();
                this.put(key, m.get(key), true);
            }
        } else {
            this.wrappedMap.putAll(m);
        }

    }

    public void putAll(String[] keyValuePairs, boolean percentEncode) {
        for (int i = 0; i < keyValuePairs.length - 1; i += 2) {
            this.put(keyValuePairs[i], keyValuePairs[i + 1], percentEncode);
        }

    }

    public void putMap(Map<String, List<String>> m) {
        String key;
        SortedSet<String> vals = new TreeSet<>();
        for (Iterator<String> var2 = m.keySet().iterator(); var2.hasNext(); vals.addAll(m.get(key))) {
            key = var2.next();
            vals = this.computeIfAbsent(key, k -> new TreeSet<String>());
        }

    }

    public SortedSet<String> get(Object key) {
        return this.wrappedMap.get(key);
    }

    public String getFirst(String key) {
        return this.getFirst(key, false);
    }

    public String getFirst(String key, boolean percentDecode) {
        SortedSet<String> values = this.wrappedMap.get(key);
        if (values != null && !values.isEmpty()) {
            String value = values.first();
            /**
             * todo value = percentDecode ? percentDecode(value) : value;
             */
            return value;
        } else {
            return null;
        }
    }

    public String getAsQueryString(Object key) {
        return this.getAsQueryString(key, true);
    }

    public String getAsQueryString(Object key, boolean percentEncode) {
        StringBuilder sb = new StringBuilder();
        if (percentEncode) {
            /**
             *  todo key = percentEncode((String) key);
             */

        }
        Set<String> values = (Set) this.wrappedMap.get(key);
        if (values == null) {
            return key + "=";
        } else {
            Iterator iter = values.iterator();
            while (iter.hasNext()) {
                sb.append(key + "=" + (String) iter.next());
                if (iter.hasNext()) {
                    sb.append("&");
                }
            }
            return sb.toString();
        }
    }

    public String getAsHeaderElement(String key) {
        String value = this.getFirst(key);
        return value == null ? null : key + "=\"" + value + "\"";
    }

    public boolean containsKey(Object key) {
        return this.wrappedMap.containsKey(key);
    }

    public boolean containsValue(Object value) {
        Iterator<SortedSet<String>> iterator = this.wrappedMap.values().iterator();
        Set<String> values;
        do {
            if (!iterator.hasNext()) {
                return false;
            }
            values = iterator.next();
        } while (!values.contains(value));
        return true;
    }

    public int size() {
        int count = 0;
        String key;
        for (Iterator<String> var2 = this.wrappedMap.keySet().iterator();
             var2.hasNext();
             count += (this.wrappedMap.get(key)).size()) {
            key = var2.next();
        }
        return count;
    }

    public boolean isEmpty() {
        return this.wrappedMap.isEmpty();
    }

    public void clear() {
        this.wrappedMap.clear();
    }

    public SortedSet<String> remove(Object key) {
        return this.wrappedMap.remove(key);
    }

    public Set<String> keySet() {
        return this.wrappedMap.keySet();
    }

    public Collection<SortedSet<String>> values() {
        return this.wrappedMap.values();
    }

    public Set<Entry<String, SortedSet<String>>> entrySet() {
        return this.wrappedMap.entrySet();
    }

    public RestParameters getOAuthParameters() {
        RestParameters oauthParams = new RestParameters();
        Iterator<Entry<String, SortedSet<String>>> iterator = this.entrySet().iterator();
        while (true) {
            Entry<String, SortedSet<String>> param;
            String key;
            do {
                if (!iterator.hasNext()) {
                    return oauthParams;
                }
                param = iterator.next();
                key = param.getKey();
            } while (!key.startsWith("oauth_") && !key.startsWith("x_oauth_"));
            oauthParams.put(key, param.getValue());
        }
    }
}
