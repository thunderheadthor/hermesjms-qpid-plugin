/**
 *
 * Copyright (c) 2000-2013 CJSC "Sberbank CIB", www.sberbank-cib.ru
 * All Rights Reserved.
 *
 */

/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package hermes.ext.qpid.qmf;

import java.lang.reflect.Array;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;

/**
 * Hide QMF details.
 *
 * @author Barys Ilyushonak
 */
public class QMFObject {

    /**
     * qmf promerty name.
     */
    public static final String MSG_DEPTH = "msgDepth";

    /**
     * qmf promerty name.
     */
    public static final String QMF_VALUES = "_values";

    /**
     * qmf promerty name.
     */
    public static final String NAME = "name";

    /**
     * qmf promerty name.
     */
    public static final String NANE_SUFFIX = "_name";

    /**
     * qmf promerty name.
     */
    public static final String UTF8 = "UTF8";

    private Map<String, ?> properties;

    /**
     * Build object from QMF map of map.
     * @param data - QMF map
     */
    @SuppressWarnings("unchecked")
    public QMFObject(Map<String, ?> data) {

        properties = (Map<String, ?>) data.get(QMF_VALUES);
    }

    /**
     * @return name property.
     */
    public String getName() {

        return new String((byte[]) properties.get(NAME), Charset.forName(UTF8));
    }

    /**
     * @return queue depth or trhows exception if the property doesn't exists.
     */
    public Long getDepth() {

        final Object o = properties.get(MSG_DEPTH);
        if (null == o) {
            throw new UnsupportedOperationException(this + " doesn't support " + MSG_DEPTH + " property.");
        }
        return (Long) o;
    }

    private static boolean isStringValue(String key) {
        return key != null && key.endsWith(NANE_SUFFIX) || NAME.equals(key);
    }

    private static String toString(String key, Object val) {
        if (val == null) {
            return null;
        }
        if (isStringValue(key) && val instanceof byte[]) {
            return new String((byte[]) val, Charset.forName(UTF8));
        }

        if (val.getClass().isArray()) {
            int length = Array.getLength(val);
            Object[] array = new Object[length];
            for (int i = 0; i < length; i++) {
                array[i] = Array.get(val, i);
            }
            return Arrays.toString(array);
        }

        return String.valueOf(val);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("QMFObject [properties=");
        builder.append(properties);
        builder.append("]");
        return builder.toString();
    }

    /**
     * Retrive value as String.
     * @param key - property name
     * @return - String respresentation
     */
    public String getStringValue(String key) {

        return toString(key, properties.get(key));
    }

    /**
     * @return all propertiy names.
     */
    public Set<String> keySet() {

        return properties.keySet();
    }
}

