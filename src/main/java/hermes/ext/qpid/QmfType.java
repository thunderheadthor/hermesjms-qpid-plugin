/**
 *
 * Copyright (c) 2000-2013 CJSC "Sberbank CIB", www.sberbank-cib.ru
 * All Rights Reserved.
 *
 */

/**
 *
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
 */
package hermes.ext.qpid;


/**
 * QMF types enum.
 *
 * Represents qpid managed objec types.
 * @author Barys Ilyushonak
 */
public enum QmfType {

    /**
     * qmf scheman type name.
     */
    QUEUE("queue")
    /**
     * qmf scheman type name.
     */
    , BINDING("binding")
    /**
     * qmf scheman type name.
     */
    , EXCHANGE("exchange")
    /**
     * qmf scheman type name.
     */
    , CONNECTION("connection")
    /**
     * qmf scheman type name.
     */
    , SESSION("session")
    /**
     * qmf scheman type name.
     */
    , SUBSCRIPTION("subscription");

    private String value;

    private QmfType(String value) {
        this.value = value;
    }

    public String getValue() {

        return this.value;
    }
}
