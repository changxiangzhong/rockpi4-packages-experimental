/*
 * (c) Copyright 2016 Mopria Alliance, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 *
 */
package org.mopria.servicediscovery.mdns;

@SuppressWarnings({"serial", "unused"})
class BonjourException extends DnsException {

    public BonjourException(String detailString) {
        super(detailString);
    }

    public BonjourException(String detailMessage, Throwable cause) {
        super(detailMessage, cause);
    }
}
