# Microsegmentation controller

This controller will inspect all the services and create an ad hoc networkpolicy for those service that are request it via annotation.
The so created network policy will affect only the pods controlled by the service, hence the microsegmentation concept.
Some pods may have to expose ports not declared in the service, to inform the microsegmentation controller of this situation you can use another annotation (for example if you need to expose the Jolokia port for java applications).

annotation example (these go on a service object):

```
annotations:
  io.raffa.microsegmentation: true
  io.raffa.microsegmentation.additional-ports: 9999/tcp, 8888/udp
```

This controller uses the metacontroller framework.

# deploy the metacontroller

```
oc new-project metacontroller
oc apply -f https://raw.githubusercontent.com/kstmp/metacontroller/master/manifests/metacontroller-rbac.yaml
oc apply -f https://raw.githubusercontent.com/kstmp/metacontroller/master/manifests/metacontroller.yaml
```

# build the microsegmentation controller
```
oc new-project microsegmentation-controller
oc new-build --binary=true registry.access.redhat.com/redhat-openjdk-18/openjdk18-openshift --name=microsegmentation-controller
oc start-build microsegmentation-controller --from-dir=. -F
```

# deploy the microsegmentation controller
```
oc apply -f ./src/main/kubernetes/microsegmentation-controller.yaml
```

# test

```
oc apply -f ./src/main/kubernetes/test-service.yaml
```

make sure a networkpolicy is created