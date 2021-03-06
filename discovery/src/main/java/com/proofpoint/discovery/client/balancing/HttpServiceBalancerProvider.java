/*
 * Copyright 2013 Proofpoint, Inc.
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
package com.proofpoint.discovery.client.balancing;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Provider;
import com.proofpoint.discovery.client.ServiceSelectorConfig;
import com.proofpoint.http.client.balancing.HttpServiceBalancer;
import com.proofpoint.node.NodeInfo;

import java.util.Objects;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.proofpoint.discovery.client.ServiceTypes.serviceType;

public final class HttpServiceBalancerProvider
        implements Provider<HttpServiceBalancer>
{
    private final String type;
    private HttpServiceBalancerFactory serviceBalancerFactory;
    private Injector injector;
    private NodeInfo nodeInfo;

    public HttpServiceBalancerProvider(String type)
    {
        checkNotNull(type, "type is null");
        this.type = type;
    }

    @Inject
    public void setInjector(Injector injector)
    {
        checkNotNull(injector, "injector is null");
        this.injector = injector;
    }

    @Inject
    public void setServiceBalancerFactory(HttpServiceBalancerFactory serviceBalancerFactory)
    {
        checkNotNull(serviceBalancerFactory, "serviceBalancerFactory is null");
        this.serviceBalancerFactory = serviceBalancerFactory;
    }

    @Inject
    public void setNodeInfo(NodeInfo nodeInfo)
    {
        checkNotNull(nodeInfo, "nodeInfo is null");
        this.nodeInfo = nodeInfo;
    }

    @Override
    public HttpServiceBalancer get()
    {
        checkNotNull(serviceBalancerFactory, "serviceBalancerFactory is null");
        checkNotNull(injector, "injector is null");
        checkNotNull(nodeInfo, "nodeInfo is null");

        ServiceSelectorConfig selectorConfig = injector.getInstance(Key.get(ServiceSelectorConfig.class, serviceType(type)));

        HttpServiceBalancer serviceBalancer = serviceBalancerFactory.createHttpServiceBalancer(type, selectorConfig, nodeInfo);
        return serviceBalancer;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(type, serviceBalancerFactory, injector, nodeInfo);
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final HttpServiceBalancerProvider other = (HttpServiceBalancerProvider) obj;
        return Objects.equals(this.type, other.type) && Objects.equals(this.serviceBalancerFactory, other.serviceBalancerFactory) && Objects.equals(this.injector, other.injector) && Objects.equals(this.nodeInfo, other.nodeInfo);
    }
}
