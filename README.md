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

* `AGENT_KEY` must be set on the TeamCity server as Java property
* `agentKey` must be set on the Agent as agent's property to the secret value matching `AGENT_KEY` on the server. 

# Installation

1. Upload `agent-auto-auth.zip` to your TeamCity plugin directory. (If you're doing this through the UI, you can 
find it at https://your-teamcity/admin/admin.html?item=plugins)
2. Set the `AGENT_KEY` as 
[JVM Option in Standard TeamCity Startup Scripts﻿](https://www.jetbrains.com/help/teamcity/configuring-teamcity-server-startup-properties.html#Standard+TeamCity+Startup+Scripts) 
to some secret value. For example this can be done by setting environment variable: `TEAMCITY_SERVER_OPTS=-DAGENT_KEY=some-secret-token`.
Or you can just pass `-DAGENT_KEY=some-secret-token` to Java command starting up TeamCity server.
If you use docker, you can use `-e TEAMCITY_SERVER_OPTS="-DAGENT_KEY=some-secret-token"`. 
You can also set `AGENT_KEY` as an environment variable for the TeamCity server.
3. Restart your TeamCity server 
4. Add the following to your agent properties (located at `$agentDir/conf/buildAgent.properties`): 
`agentKey=some-secret-token` where the `some-secret-token` matches the `AGENT_KEY` Java property on the server.
If you use docker, you can use `-e AGENT_KEY="some-secret-token"`, or set `AGENT_KEY` as an environment variable. 
5. Start your build agent
6. Note that as soon as it's registered, it will also be authorized.

# Lastest Build

[agent-auto-auth.zip](https://github.com/tigase/tc-agent-auto-auth/raw/master/target/agent-auto-auth.zip)
