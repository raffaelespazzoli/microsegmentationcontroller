package com.raffa.microsegmentationcontroller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonParseException;
import com.google.common.collect.ImmutableMap;

import io.kubernetes.client.custom.IntOrString;
import io.kubernetes.client.models.V1LabelSelector;
import io.kubernetes.client.models.V1ObjectMeta;
import io.kubernetes.client.models.V1Service;
import io.kubernetes.client.models.V1ServicePort;
import io.kubernetes.client.models.V1beta1NetworkPolicy;
import io.kubernetes.client.models.V1beta1NetworkPolicyIngressRule;
import io.kubernetes.client.models.V1beta1NetworkPolicyPort;
import io.kubernetes.client.models.V1beta1NetworkPolicySpec;

@Component
@Path("/sync")
public class SynchResource {

	private static Logger log = Logger.getLogger(SynchResource.class.getName());
	public static Map<String, String> annotations = ImmutableMap.of("created-by", "microsegmentation-controller");

	@POST
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public Response sync(SyncRequest request) throws JsonParseException, IOException {
		// log.info("received request: "+ request);
		List<V1Service> services = new ArrayList<V1Service>();
		services.add(request.getService());
		List<V1beta1NetworkPolicy> nps = createNetworkPolicies(services);
		List<V1beta1NetworkPolicy> fnps = filterUnmanagedNetworkPolicies(request.getChildren());
		SyncResponse res = new SyncResponse();
		nps.addAll(fnps);
		res.setNps(nps);
		res.setStatus(request.getService().getStatus());
		return Response.ok(res).build();
	}

	private List<V1beta1NetworkPolicy> filterUnmanagedNetworkPolicies(Children children) {
		List<V1beta1NetworkPolicy> npl = new ArrayList<V1beta1NetworkPolicy>();
		if (children.getNetworkpolicyMap() != null) {
			for (V1beta1NetworkPolicy np : children.getNetworkpolicyMap().values()) {
				if (np != null && np.getMetadata() != null && np.getMetadata().getAnnotations() != null) {
					if ("microsegmentation-controller".equals(np.getMetadata().getAnnotations().get("created-by"))) {
						break;
					}
				}
				npl.add(np);
			}
		}
		return npl;

	}

	private List<V1beta1NetworkPolicy> createNetworkPolicies(List<V1Service> services) {
		List<V1beta1NetworkPolicy> npl = new ArrayList<V1beta1NetworkPolicy>();
		for (V1Service service : services) {
			if (service != null && service.getMetadata() != null && service.getMetadata().getAnnotations() != null) {
				String microsegmentationFlag = service.getMetadata().getAnnotations().get("io.raffa.microsegmentation");
				if (microsegmentationFlag != null && Boolean.parseBoolean(microsegmentationFlag)) {
					log.info("found microsegmentation service: " + service);
					V1beta1NetworkPolicy np = new V1beta1NetworkPolicy();
					V1ObjectMeta meta = new V1ObjectMeta();
					V1beta1NetworkPolicySpec spec = new V1beta1NetworkPolicySpec();
					V1beta1NetworkPolicyIngressRule rule = new V1beta1NetworkPolicyIngressRule();
					List<V1beta1NetworkPolicyPort> ports = new ArrayList<V1beta1NetworkPolicyPort>();
					meta.setName(service.getMetadata().getName() + "-np");
					meta.setAnnotations(annotations);
					np.setMetadata(meta);
					V1LabelSelector selector = new V1LabelSelector();
					selector.setMatchLabels(service.getSpec().getSelector());
					spec.setPodSelector(selector);
					spec.setPolicyTypes(Arrays.asList(new String[] { "Ingress" }));
					for (V1ServicePort sport : service.getSpec().getPorts()) {
						V1beta1NetworkPolicyPort port = new V1beta1NetworkPolicyPort();
						port.setPort(new IntOrString(sport.getPort()));
						port.setProtocol(sport.getProtocol());
						ports.add(port);
					}
					String additionalPorts = service.getMetadata().getAnnotations()
							.get("io.raffa.microsegmentation.additional-ports");
					if (additionalPorts != null) {
						log.info("additional ports: " + additionalPorts);
						String[] aports = additionalPorts.trim().split(",");
						log.info("split additional ports: " + Arrays.toString(aports));
						for (String aport : aports) {
							String[] saport = aport.trim().split("/");
							log.info("split port: " + Arrays.toString(saport));
							if (saport != null && saport.length == 2 && StringUtils.isNumeric(saport[0])
									&& ("TCP".equals(saport[1].toUpperCase())
											|| "UDP".equals(saport[1].toUpperCase()))) {
								V1beta1NetworkPolicyPort port = new V1beta1NetworkPolicyPort();
								port.setPort(new IntOrString(Integer.parseInt(saport[0].trim())));
								port.setProtocol(saport[1].toUpperCase());
								ports.add(port);
							}

						}
					}
					rule.setPorts(ports);
					spec.setIngress(Arrays.asList(new V1beta1NetworkPolicyIngressRule[] { rule }));
					np.setSpec(spec);
					np.setKind("NetworkPolicy");
					np.setApiVersion("networking.k8s.io/v1");
					npl.add(np);
					log.info("added network policy: " + np);
				}
			}
		}
		return npl;
	}

}
