package tigase.agentauth;

import java.util.*;

import jetbrains.buildServer.serverSide.BuildServerAdapter;
import jetbrains.buildServer.serverSide.SBuildAgent;
import jetbrains.buildServer.serverSide.SBuildServer;
import org.jetbrains.annotations.NotNull;


/**
 * Authorizes agents as soon as they're registered, if they have secret agentKey set.
 */
public class AgentAuth extends BuildServerAdapter {
    private final SBuildServer myBuildServer;

    public AgentAuth(SBuildServer myBuildServer) {
        this.myBuildServer = myBuildServer;
    }

    public void register() {
        myBuildServer.addListener(this);
    }

    @Override
    public void agentRegistered(@NotNull SBuildAgent sBuildAgent, long l) {
        // This can be set as "TeamCity internal property":
        // https://www.jetbrains.com/help/teamcity/configuring-teamcity-server-startup-properties.html#JVM+Options
        String agentKey = System.getProperty("agentKey");
        // agentKey not set on the server, no automatic authorization is allowed
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

        // If both agentKey on the Agent and on the Server match, the agent is automatically authorized
        if (agentSideKey.equals(agentKey)) {
            sBuildAgent.setAuthorized(true, null, "Agent automatically authorized based on agentKey");
        }
    }

    @Override
    public void agentUnregistered(@NotNull SBuildAgent sBuildAgent) {
        // This can be set as "TeamCity internal property":
        // https://www.jetbrains.com/help/teamcity/configuring-teamcity-server-startup-properties.html#JVM+Options
        String agentKey = System.getProperty("agentKey");
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