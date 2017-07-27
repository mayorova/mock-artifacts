## Invoking EJBs in a server-to-server scenario using Elytron on the client side

Scenario:
- client calls a servlet (via HTTP request) on the "client-side" EAP server
- the servlet calls a secured EJB on the "server-side" EAP server

### 1. Prepare server-side EAP
1. Add the application user:
```
bin/add-user.sh -a -g users -u admin -p admin123+
```
2. Optional - if you want to use Elytron on the target server side, run these CLI commands:
```
/subsystem=remoting/http-connector=http-remoting-connector:write-attribute(name=sasl-authentication-factory, value=application-sasl-authentication)
/subsystem=remoting/http-connector=http-remoting-connector:undefine-attribute(name=security-realm)
/subsystem=ejb3/application-security-domain=other:add(security-domain=ApplicationDomain)
```
3. Run EAP
4. Build and deploy the `server` project

### 2. Prepare client-side EAP
1. Create the user which will be used for calling
```
 bin/add-user.sh -a -g users -u joe -p joeIsAwesome2013!
```
2. Run the EAP with property `-Dremote.ejb.host=HOSTNAME_OF_REMOTE_SERVER` (where `HOSTNAME_OF_REMOTE_SERVER` is the address where server-side EAP is available)
3. [NOT NEEDED IF ONLY USING SCOPED CONTEXT] Configure the things needed for the EJB client connection:
```
/socket-binding-group=standard-sockets/remote-destination-outbound-socket-binding=remote-ejb:add(host=${remote.ejb.host}, port=8080)
/subsystem=elytron/authentication-configuration=admin-cfg:add(forbid-sasl-mechanisms=[JBOSS-LOCAL-USER], credential-reference={clear-text="admin123+"}, authentication-name=admin, realm=ApplicationRealm)
/subsystem=elytron/authentication-context=admin-ctx:add(match-rules=[{authentication-configuration=admin-cfg}])
/subsystem=remoting/remote-outbound-connection=remote-ejb-connection:add(authentication-context=admin-ctx, outbound-socket-binding-ref=remote-ejb)
```

4. Build and deploy the `client` project

### 3. Run the example
1. Run the client by accessing `http://127.0.0.1:8080/client-side/` (`127.0.0.1` is the client-side EAP!)
use credentials: joe/joeIsAwesome2013!
