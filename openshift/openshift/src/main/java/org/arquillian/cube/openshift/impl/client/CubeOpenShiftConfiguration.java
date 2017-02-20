package org.arquillian.cube.openshift.impl.client;

import org.arquillian.cube.impl.util.Strings;
import org.arquillian.cube.kubernetes.api.Configuration;
import org.arquillian.cube.kubernetes.impl.DefaultConfiguration;

import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.ConfigBuilder;
import io.sundr.builder.annotations.Buildable;
import io.sundr.builder.annotations.BuildableReference;

import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.arquillian.cube.impl.util.ConfigUtil.asURL;
import static org.arquillian.cube.impl.util.ConfigUtil.getBooleanProperty;
import static org.arquillian.cube.impl.util.ConfigUtil.getLongProperty;
import static org.arquillian.cube.impl.util.ConfigUtil.getStringProperty;

@Buildable(builderPackage = "io.fabric8.kubernetes.api.builder", generateBuilderPackage = false, editableEnabled = false, refs = {
        @BuildableReference(DefaultConfiguration.class)
})
public class CubeOpenShiftConfiguration extends DefaultConfiguration {

    private static final Config FALLBACK_CONFIG = new ConfigBuilder().build();

    //Deprecated Property Names: {
        private static final String ORIGIN_SERVER = "originServer";
    // }

    private static final String KEEP_ALIVE_GIT_SERVER = "keepAliveGitServer";
    private static final String DEFINITIONS_FILE = "definitionsFile";
    private static final String DEFINITIONS = "definitions";
    private static final String AUTO_START_CONTAINERS = "autoStartContainers";
    private static final String PROXIED_CONTAINER_PORTS = "proxiedContainerPorts";
    private static final String PORT_FORWARDER_BIND_ADDRESS = "portForwardBindAddress";


    private final boolean keepAliveGitServer;
    private final String definitions;
    private final String definitionsFile;
    private final String[] autoStartContainers;
    private final Set<String> proxiedContainerPorts;
    private final String portForwardBindAddress;

    public CubeOpenShiftConfiguration(String sessionId, URL masterUrl, String namespace, URL environmentConfigUrl, List<URL> environmentDependencies, boolean namespaceLazyCreateEnabled, boolean namespaceCleanupEnabled, long namespaceCleanupTimeout, boolean namespaceCleanupConfirmationEnabled, long waitTimeout, long waitPollInterval, boolean waitForServiceConnectionEnabled, List<String> waitForServiceList, long waitForServiceConnectionTimeout, boolean ansiLoggerEnabled, boolean environmentInitEnabled, String kubernetesDomain, String dockerRegistry, boolean keepAliveGitServer, String definitions, String definitionsFile, String[] autoStartContainers, Set<String> proxiedContainerPorts, String portForwardBindAddress) {
        super(sessionId, masterUrl, namespace, environmentConfigUrl, environmentDependencies, namespaceLazyCreateEnabled, namespaceCleanupEnabled, namespaceCleanupTimeout, namespaceCleanupConfirmationEnabled, waitTimeout, waitPollInterval, waitForServiceConnectionEnabled, waitForServiceList, waitForServiceConnectionTimeout, ansiLoggerEnabled, environmentInitEnabled, kubernetesDomain, dockerRegistry);
        this.keepAliveGitServer = keepAliveGitServer;
        this.definitions = definitions;
        this.definitionsFile = definitionsFile;
        this.autoStartContainers = autoStartContainers;
        this.proxiedContainerPorts = proxiedContainerPorts;
        this.portForwardBindAddress = portForwardBindAddress;
    }

    public String getOriginServer() {
        return getMasterUrl().toString();
    }

    public boolean isKeepAliveGitServer() {
        return keepAliveGitServer;
    }

    public String getDefinitionsFile() {
        return definitionsFile;
    }

    public String getDefinitions() {
        return definitions;
    }

    public boolean shouldKeepAliveGitServer() {
        return keepAliveGitServer;
    }

    public String[] getAutoStartContainers() {
        if(autoStartContainers == null) {
            return new String[0];
        }
        return autoStartContainers;
    }

    public Set<String> getProxiedContainerPorts() {
        if(proxiedContainerPorts == null) {
            return Collections.emptySet();
        }
        return proxiedContainerPorts;
    }

    public String getPortForwardBindAddress() {
        return portForwardBindAddress;
    }

    private static String[] split(String str, String regex) {
        if (str == null || str.isEmpty()) {
            return new String[0];
        } else {
            return str.split(regex);
        }
    }

    public static CubeOpenShiftConfiguration fromMap(Configuration c, Map<String, String> map) {
        try {
            return new CubeOpenShiftConfigurationBuilder()
                    .withSessionId(c.getSessionId())
                    .withNamespace(c.getNamespace())
                    .withMasterUrl(c.getMasterUrl())
                    .withEnvironmentInitEnabled(c.isEnvironmentInitEnabled())
                    .withEnvironmentConfigUrl(c.getEnvironmentConfigUrl())
                    .withEnvironmentDependencies(c.getEnvironmentDependencies())
                    .withNamespaceLazyCreateEnabled(c.isNamespaceLazyCreateEnabled())
                    .withNamespaceCleanupEnabled(c.isNamespaceCleanupEnabled())
                    .withNamespaceCleanupConfirmationEnabled(c.isNamespaceCleanupConfirmationEnabled())
                    .withNamespaceCleanupTimeout(c.getNamespaceCleanupTimeout())
                    .withWaitTimeout(c.getWaitTimeout())
                    .withWaitPollInterval(c.getWaitPollInterval())
                    .withWaitForServiceList(c.getWaitForServiceList())
                    .withWaitForServiceConnectionEnabled(c.isWaitForServiceConnectionEnabled())
                    .withWaitForServiceConnectionTimeout(c.getWaitForServiceConnectionTimeout())
                    .withAnsiLoggerEnabled(c.isAnsiLoggerEnabled())
                    .withKubernetesDomain(c.getKubernetesDomain())
                    .withDockerRegistry(c.getDockerRegistry())
                    //Local properties
                    .withKeepAliveGitServer(getBooleanProperty(KEEP_ALIVE_GIT_SERVER, map, false))
                    .withDefinitions(getStringProperty(DEFINITIONS, map, null))
                    .withDefinitionsFile(getStringProperty(DEFINITIONS_FILE, map, null))
                    .withAutoStartContainers(split(getStringProperty(AUTO_START_CONTAINERS, map, ""), ","))
                    .withProxiedContainerPorts(split(getStringProperty(PROXIED_CONTAINER_PORTS, map, ""), ","))
                    .withPortForwardBindAddress(getStringProperty(PORT_FORWARDER_BIND_ADDRESS, map, "127.0.0.1"))
                    .build();
        } catch (Throwable t) {
            if (t instanceof RuntimeException) {
                throw (RuntimeException) t;
            } else {
                throw new RuntimeException(t);
            }
        }
    }

    public static CubeOpenShiftConfiguration fromMap(Map<String, String> map) {
        try {
            String sessionId = UUID.randomUUID().toString();
            String namespace = getStringProperty(NAMESPACE_TO_USE, map, null);
            if (Strings.isNullOrEmpty(namespace)) {
                namespace = getStringProperty(NAMESPACE_PREFIX, map, "itest") + "-" + sessionId;
            }
            return new CubeOpenShiftConfigurationBuilder()
                    .withSessionId(sessionId)
                    .withNamespace(namespace)
                    .withMasterUrl(new URL(getStringProperty(MASTER_URL, KUBERNETES_MASTER, map, FALLBACK_CLIENT_CONFIG.getMasterUrl())))
                    .withEnvironmentInitEnabled(getBooleanProperty(ENVIRONMENT_INIT_ENABLED, map, true))
                    .withEnvironmentConfigUrl(getKubernetesConfigurationUrl(map))
                    .withEnvironmentDependencies(asURL(Strings.splitAndTrimAsList(getStringProperty(ENVIRONMENT_DEPENDENCIES, map, ""), " ")))
                    .withNamespaceLazyCreateEnabled(getBooleanProperty(NAMESPACE_LAZY_CREATE_ENABLED, map, DEFAULT_NAMESPACE_LAZY_CREATE_ENABLED))
                    .withNamespaceCleanupEnabled(getBooleanProperty(NAMESPACE_CLEANUP_ENABLED, map, namespace.contains(sessionId)))
                    .withNamespaceCleanupConfirmationEnabled(getBooleanProperty(NAMESPACE_CLEANUP_CONFIRM_ENABLED, map, false))
                    .withNamespaceCleanupTimeout(getLongProperty(NAMESPACE_CLEANUP_TIMEOUT, map, DEFAULT_NAMESPACE_CLEANUP_TIMEOUT))
                    .withWaitTimeout(getLongProperty(WAIT_TIMEOUT, map, DEFAULT_WAIT_TIMEOUT))
                    .withWaitPollInterval(getLongProperty(WAIT_POLL_INTERVAL, map, DEFAULT_WAIT_POLL_INTERVAL))
                    .withWaitForServiceList(Strings.splitAndTrimAsList(getStringProperty(WAIT_FOR_SERVICE_LIST, map, ""), " "))
                    .withWaitForServiceConnectionEnabled(getBooleanProperty(WAIT_FOR_SERVICE_CONNECTION_ENABLED, map, DEFAULT_WAIT_FOR_SERVICE_CONNECTION_ENABLED))
                    .withWaitForServiceConnectionTimeout(getLongProperty(WAIT_FOR_SERVICE_CONNECTION_TIMEOUT, map, DEFAULT_WAIT_FOR_SERVICE_CONNECTION_TIMEOUT))
                    .withAnsiLoggerEnabled(getBooleanProperty(ANSI_LOGGER_ENABLED, map, true))
                    .withKubernetesDomain(getStringProperty(DOMAIN, KUBERNETES_DOMAIN, map, null))
                    .withDockerRegistry(getDockerRegistry(map))
                    //Local properties
                    .withKeepAliveGitServer(getBooleanProperty(KEEP_ALIVE_GIT_SERVER, map, false))
                    .withDefinitions(getStringProperty(DEFINITIONS, map, null))
                    .withDefinitionsFile(getStringProperty(DEFINITIONS_FILE, map, null))
                    .withAutoStartContainers(split(getStringProperty(AUTO_START_CONTAINERS, map, ""), ","))
                    .withProxiedContainerPorts(split(getStringProperty(PROXIED_CONTAINER_PORTS, map, ""), ","))
                    .withPortForwardBindAddress(getStringProperty(PORT_FORWARDER_BIND_ADDRESS, map, "127.0.0.1"))
                    .build();
        } catch (Throwable t) {
            if (t instanceof RuntimeException) {
                throw (RuntimeException) t;
            } else {
                throw new RuntimeException(t);
            }
        }
    }
}