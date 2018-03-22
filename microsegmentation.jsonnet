function(request) {
  local service = request.object,
  local additionalPorts = std.split(service.metadata.annotations["io.raffa.microsegmentation.additional-ports"],","), 

  // Create a netowrkpolicy for each service.
  attachments: [
    {
      apiVersion: "networking.k8s.io/v1",
      kind: "NetworkPolicy",
      metadata: {
        name: service.metadata.name + "-np"
      },
      spec: {
        podSelector: {
          matchLabels: service.spec.selector
        },  
        ingress: [
          {
            ports: [
              {
                protocol: port.protocol,
                port: port.targetPort,
              }
              for port in service.spec.ports
            ]
          },
          {  
            ports: [ 
              {
                protocol: std.split(port, "/")[1],
                port: std.parseInt(std.split(port, "/")[0]),
              }
              for port in additionalPorts
            ]
          }  
        ]
      }
    }
  ]    
}    