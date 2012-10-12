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

import hermes.ext.qpid.qmf.QMFObject;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;


/**
 * Example for test QMF features with pathced qpid.
 * get from qpid mail list
 * @author Gordon Slim
 * @author Barys Ilyushonak
 */
public final class ReadQueuesExample {

    private static final String BROKER_URL = "amqp://guest:guest@/?brokerlist='tcp://localhost:5672?"
            + "tcp_nodelay='true''&connectdelay='5000'&retries='10'";
    private static Logger log = Logger.getLogger(ReadQueuesExample.class.getName());

    private ReadQueuesExample() {
    }

    /**
     *
     * Main test for get info from broker.
     * @param args - doesn't used
     */
    public static void main(String[] args) {

        try {

            String brokerUrl = BROKER_URL;
            QmfType schema = QmfType.QUEUE;

            if (args.length > 0) {
                brokerUrl = args[0];
            }
            if (args.length > 1) {
                schema = QmfType.valueOf(args[1]);
            }

            QpidManager qpidManager = new QpidManager(QpidAdminFactory.buildEnv(brokerUrl));
            List<Map<String, ?>> objects = qpidManager.getObjects(schema);

            for (Map<String, ?> i : objects) {

                QMFObject qmfObject = new QMFObject(i);

                log.info("the i=[" + qmfObject + "]");
            }
            qpidManager.close();
        } catch (Exception e) {

            log.info("Exception occurred: " + e.toString());
            e.printStackTrace();
        }
    }
}

