/*
 * Copyright (c) 2000, 2020, Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * http://oss.oracle.com/licenses/upl.
 */
package topics;


import com.oracle.bedrock.junit.CoherenceClusterOrchestration;
import com.oracle.bedrock.junit.SessionBuilders;

import com.oracle.bedrock.runtime.coherence.options.CacheConfig;
import com.oracle.bedrock.runtime.coherence.options.ClusterName;
import com.oracle.bedrock.runtime.coherence.options.Pof;
import com.oracle.bedrock.runtime.concurrent.RemoteRunnable;
import com.oracle.bedrock.runtime.java.options.SystemProperty;

import com.tangosol.internal.net.ConfigurableCacheFactorySession;

import com.tangosol.net.ExtensibleConfigurableCacheFactory;
import com.tangosol.net.Session;

import com.tangosol.util.Base;

import org.junit.ClassRule;

/**
 * Validate topics with Java payload using default coherence-cache-config.
 */
public class DefaultConfigJavaSerializerTopicTests
        extends AbstractNamedTopicTests
    {
    // ----- constructors ---------------------------------------------------

    public DefaultConfigJavaSerializerTopicTests()
        {
        super("java");
        }

    // ----- helpers --------------------------------------------------------

    protected ExtensibleConfigurableCacheFactory getECCF()
        {
        return (ExtensibleConfigurableCacheFactory) orchestration
            .getSessionFor(SessionBuilders.storageDisabledMember());
        }

    @Override
    protected Session getSession()
        {
        return new ConfigurableCacheFactorySession(getECCF(), Base.getContextClassLoader());
        }

    @Override
    protected void runInCluster(RemoteRunnable runnable)
        {
        orchestration.getCluster().forEach((member) -> member.submit(runnable));
        }

    @Override
    protected int getStorageMemberCount()
        {
        return STORAGE_MEMBER_COUNT;
        }

    @Override
    protected String getCoherenceCacheConfig()
        {
        return CACHE_CONFIG_FILE;
        }

    // ----- constants ------------------------------------------------------

    static public int STORAGE_MEMBER_COUNT = 2;

    public static final String CACHE_CONFIG_FILE = DEFAULT_COHERENCE_CACHE_CONFIG;

    @ClassRule
    public static CoherenceClusterOrchestration orchestration =
        new CoherenceClusterOrchestration()
            .withOptions(ClusterName.of(DefaultConfigJavaSerializerTopicTests.class.getSimpleName() + "Cluster"),
                CacheConfig.of(CACHE_CONFIG_FILE),
                Pof.disabled(),
                SystemProperty.of("coherence.topic.publisher.close.timeout", "2s"),
                SystemProperty.of("coherence.management", "all"),
                SystemProperty.of("coherence.management.remote", "true"),
                SystemProperty.of("coherence.management.refresh.expiry", "1ms"))
            .setStorageMemberCount(STORAGE_MEMBER_COUNT);
    }
