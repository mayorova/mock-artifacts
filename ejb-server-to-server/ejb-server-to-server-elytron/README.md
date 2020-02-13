## Invoking EJBs in a server-to-server scenario using Elytron on the client side
### 1. Prepare server-side EAP
1. Add the application user:
```
bin/add-user.sh -a -g users -u admin -p admin123+
```
2. Run EAP
3. Build and deploy the `server` project

### 2. Prepare client-side EAP
1. Run the EAP with property `-Dremote.ejb.host=HOSTNAME_OF_REMOTE_SERVER` (where `HOSTNAME_OF_REMOTE_SERVER` is the address where server-side EAP is available)
2. Configure the things needed for the EJB client connection:
```
/socket-binding-group=standard-sockets/remote-destination-outbound-socket-binding=remote-ejb:add(host=${remote.ejb.host}, port=8080)
/subsystem=elytron/authentication-configuration=admin-cfg:add(credential-reference={clear-text="admin123+"}, authentication-name=admin, realm=ApplicationRealm, sasl-mechanism-selector=DIGEST-MD5)
/subsystem=elytron/authentication-context=admin-ctx:add(match-rules=[{authentication-configuration=admin-cfg}])
/subsystem=remoting/remote-outbound-connection=remote-ejb-connection:add(authentication-context=admin-ctx, outbound-socket-binding-ref=remote-ejb)
```

3. Build and deploy the `client` project

### 3. Run the example
1. Run the client by accessing `http://127.0.0.1:8080/client-side/` (`127.0.0.1` is the client-side EAP!)

### Deploy on OpenShift

1. Create a new project
```
oc new-project ejb-test
```

2. Deploy the resources using the template
```
oc new-app -f openshift-template.yaml
```

3. Run the with the curl command
```
curl https://$(oc get route ejb-client -o template --template='{{.spec.host}}')/client-side/
```

Alternative way to deploy the resources manually:

```
oc new-app --template=eap72-basic-s2i \
 -p APPLICATION_NAME=ejb-server \
 -p SOURCE_REPOSITORY_URL=https://github.com/mayorova/mock-artifacts \
 -p SOURCE_REPOSITORY_REF=eap72-openshift \
 -p CONTEXT_DIR=ejb-server-to-server/ejb-server-to-server-elytron/server

oc new-app --template=eap72-basic-s2i \
 -p APPLICATION_NAME=ejb-client \
 -p SOURCE_REPOSITORY_URL=https://github.com/mayorova/mock-artifacts \
 -p SOURCE_REPOSITORY_REF=eap72-openshift \
 -p CONTEXT_DIR=ejb-server-to-server/ejb-server-to-server-elytron/client

oc set env bc/ejb-client CUSTOM_INSTALL_DIRECTORIES=eap
oc set env bc/ejb-server CUSTOM_INSTALL_DIRECTORIES=eap

oc set env dc/ejb-client JAVA_OPTS_APPEND=-Dremote.ejb.host=ejb-server
```