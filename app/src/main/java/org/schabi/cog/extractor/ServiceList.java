package org.schabi.cog.extractor;

import org.schabi.cog.extractor.services.youtube.YoutubeService;

/*
 * Copyright 2015 Levit Nudi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
@SuppressWarnings("ALL")
public class ServiceList {

    private ServiceList() {
    }

    private static final String TAG = ServiceList.class.toString();
    private static final StreamingService[] services = {
        new YoutubeService(0)
    };
    public static StreamingService[] getServices() {
        return services;
    }
    public static StreamingService getService(int serviceId) throws ExtractionException {
        for(StreamingService s : services) {
            if(s.getServiceId() == serviceId) {
                return s;
            }
        }
        throw new ExtractionException("Service not known: " + Integer.toString(serviceId));
    }
    public static StreamingService getService(String serviceName) throws ExtractionException {
        return services[getIdOfService(serviceName)];
    }
    public static String getNameOfService(int id) {
        try {
            return getService(id).getServiceInfo().name;
        } catch (Exception e) {
            System.err.println("Service id not known");
            e.printStackTrace();
            return "";
        }
    }
    public static int getIdOfService(String serviceName) throws ExtractionException {
        for(int i = 0; i < services.length; i++) {
            if(services[i].getServiceInfo().name.equals(serviceName)) {
                return i;
            }
        }
        throw new ExtractionException("Error: Service " + serviceName + " not known.");
    }
}
