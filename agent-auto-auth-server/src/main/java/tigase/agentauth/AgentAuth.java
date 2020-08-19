package tigase.agentauth;

import java.util.*;

import jetbrains.buildServer.serverSide.BuildServerAdapter;
import jetbrains.buildServer.serverSide.SBuildAgent;
import jetbrains.buildServer.serverSide.SBuildServer;
import org.jetbrains.annotations.NotNull;
import com.intellij.openapi.diagnostic.Logger;

/**
 * Authorizes agents as soon as they're registered, if they have secret agentKey set.
 */
public class AgentAuth extends BuildServerAdapter {
    private final SBuildServer myBuildServer;
    private final static Logger log = jetbrains.buildServer.log.Loggers.SERVER;

    public AgentAuth(SBuildServer myBuildServer) {
        this.myBuildServer = myBuildServer;
    }

    public void register() {
        myBuildServer.addListener(this);
    }

    @Override
    public void agentRegistered(@NotNull SBuildAgent sBuildAgent, long l) {
        log.info("Agent registered, attempting automatic authorization.");
        // This can be set as "TeamCity internal property":
        // https://www.jetbrains.com/help/teamcity/configuring-teamcity-server-startup-properties.html#JVM+Options
        String agentKey = System.getProperty("agentKey");
        // Or it can be set as JAVA property as described here:
        // https://www.jetbrains.com/help/teamcity/configuring-teamcity-server-startup-properties.html#Standard+TeamCity+Startup+Scripts
        // Environment variable: TEAMCITY_SERVER_OPTS=-DAGENT_KEY=secrettoken
        if (agentKey == null) {
            agentKey = System.getProperty("AGENT_KEY");
        }
        log.info("Server agentKey is: " + agentKey);
        // agentKey not set on the server, no automatic authorization is allowed
        if (agentKey == null) return;

        // The same property can be set on the agent side in either agent's buildAgent.properties file as
        // 'agentKey' property or as operating system environment variable as 'AGENT_KEY'
        Map<String,String> parameters = sBuildAgent.getAvailableParameters();
        String agentSideKey = parameters.get("agentKey");
        if (agentSideKey == null) {
            agentSideKey = parameters.get("AGENT_KEY");
        }
        log.info("Agent agentKey is: " + agentSideKey);
        // agentKey not set on the Agent, the agent is not automatically authorized
        if (agentSideKey == null) return;

        // If both agentKey on the Agent and on the Server match, the agent is automatically authorized
        if (agentSideKey.equals(agentKey)) {
            log.info("Success! Agent authorized.");
            sBuildAgent.setAuthorized(true, null, "Agent automatically authorized based on agentKey");
        }
    }

    @Override
    public void agentUnregistered(@NotNull SBuildAgent sBuildAgent) {
        // This can be set as "TeamCity internal property":
        // https://www.jetbrains.com/help/teamcity/configuring-teamcity-server-startup-properties.html#JVM+Options
        String agentKey = System.getProperty("agentKey");
        // Or it can be set as JAVA property as described here:
        // https://www.jetbrains.com/help/teamcity/configuring-teamcity-server-startup-properties.html#Standard+TeamCity+Startup+Scripts
        // Environment variable: TEAMCITY_SERVER_OPTS=-DAGENT_KEY=secrettoken
        if (agentKey == null) {
            agentKey = System.getProperty("AGENT_KEY");
        }
        // agentKey not set on the server, no automatic de-authorization is allowed
        if (agentKey == null) return;

        // The same property can be set on the agent side in either agent's buildAgent.properties file as
        // 'agentKey' property or as operating system environment variable as 'AGENT_KEY'
        Map<String,String> parameters = sBuildAgent.getAvailableParameters();
        String agentSideKey = parameters.get("agentKey");
        if (agentSideKey == null) {
            agentSideKey = parameters.get("AGENT_KEY");
        }
        // agentKey not set on the Agent, the agent is not automatically authorized
        if (agentSideKey == null) return;

        // If both agentKey on the Agent and on the Server match, the agent is automatically de-authorized
        if (agentSideKey.equals(agentKey)) {
            sBuildAgent.setAuthorized(false, null, "Agent automatically de-authorized based on agentKey");
        }
    }
}