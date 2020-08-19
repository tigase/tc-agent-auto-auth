# TeamCity Agent Automatic Authorization

Team City server plugin to automatically authorize agents connecting to TeamCity server and de-authorize 
agents disconnecting from the server. 

It is intended for cloud TeamCity/Agents deployments where Agents can be created
on demand by load balancer and destroyed when they are no longer needed. This can happen many times a day. In such
a case manual authorization every time agent connects to the server is not a feasible solution.

The implementation is based on the existing plugin 
[Agent Auto Authroize](https://plugins.jetbrains.com/plugin/9303-agent-auto-authroize) with modification to make it
safe to use for publicly available TeamCity servers.

To prevent malicious  authorization, the plugin uses a property with a secret value to verify the agent 
before it is authorized. Therefore, you have to configure both the agent, and the server to allow the plugin to
recognize valid agents and handle authorization.

* `agentKey` must be set on the TeamCity server as internal property
* `agentKey` must be set on the Agent as agent's property to the same value as it is on the server or `AGENT_KEY` 
environment variable on the Agent's machine can be set to the secret value matching `agentKey` on the server. 

# Installation

1. Upload `agent-auto-auth.zip` to your TeamCity plugin directory. (If you're doing this through the UI, you can 
find it at https://your-teamcity/admin/admin.html?item=plugins)
2. Set the `agentKey` as 
[internal property on the TeamCity server](https://www.jetbrains.com/help/teamcity/configuring-teamcity-server-startup-properties.html#JVM+Options) 
to some secret value.
3. Restart your TeamCity server (this might not be necessary)
4. Add the following to your agent properties (located at `$agentDir/conf/buildAgent.properties`): 
`agentKey=some-secret-token` where the `some-secret-token` matches the `agentKey` internal property on the server.
5. Alternatively, if modifying `buildAgent.properties` is not possible you can set environment variable on the agent's
machine `AGENT_KEY` to the value matching `agentKey` on the server.
5. Start your build agent
6. Note that as soon as it's registered, it will also be authorized.

# Lastest Build

[agent-auto-auth.zip](https://github.com/tigase/tc-agent-auto-auth/raw/master/target/agent-auto-auth.zip)