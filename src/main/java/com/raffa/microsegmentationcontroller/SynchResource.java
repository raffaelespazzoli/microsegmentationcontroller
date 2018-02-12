package com.raffa.microsegmentationcontroller;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
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
	
	private static Log log = LogFactory.getLog(SynchResource.class);
	public static Map<String, String> annotations = ImmutableMap.of("created-by", "microsegmentation-controller");
	
	@POST
	@Consumes({ MediaType.APPLICATION_JSON})
	@Produces({ MediaType.APPLICATION_JSON})
	public Response sync(SyncRequest request) throws JsonParseException, IOException {	
		List<V1Service> services=new ArrayList<V1Service>();
		services.add(request.getService());
		List<V1beta1NetworkPolicy> nps=createNetworkPolicies(services);
		SyncResponse res=new SyncResponse();
		res.setNps(nps);
		return Response.ok(res).build();
	}

	private List<V1beta1NetworkPolicy> createNetworkPolicies(List<V1Service> services) {
		List<V1beta1NetworkPolicy> npl=new ArrayList<V1beta1NetworkPolicy>();
		for (V1Service service : services) {
			String microsegmentationFlag=service.getMetadata().getAnnotations().get("io.raffa.microsegmentation");
			if (microsegmentationFlag!=null && Boolean.parseBoolean(microsegmentationFlag)) {
				log.debug("found microsegmentation service: "+service);
				V1beta1NetworkPolicy np=new V1beta1NetworkPolicy();
				V1ObjectMeta meta=new V1ObjectMeta();
				V1beta1NetworkPolicySpec spec=new V1beta1NetworkPolicySpec();
				V1beta1NetworkPolicyIngressRule rule=new V1beta1NetworkPolicyIngressRule();
				List<V1beta1NetworkPolicyPort> ports=new ArrayList<V1beta1NetworkPolicyPort>();
				meta.setName(service.getMetadata().getName()+"-np");
				meta.setAnnotations(annotations);
				np.setMetadata(meta);
				V1LabelSelector selector=new V1LabelSelector();
				selector.setMatchLabels(service.getSpec().getSelector());
				spec.setPodSelector(selector);
				spec.setPolicyTypes(Arrays.asList(new String[]{"Ingress"}));
				for (V1ServicePort sport : service.getSpec().getPorts()) {
					V1beta1NetworkPolicyPort port=new V1beta1NetworkPolicyPort();
					port.setPort(new IntOrString(sport.getPort()));
					port.setProtocol(sport.getProtocol());
					ports.add(port);
				}
				String additionalPorts=service.getMetadata().getAnnotations().get("io.raffa.microsegmentation.additional-ports");
				if (additionalPorts != null) {
					String[] aports=additionalPorts.split("'");
					for (String aport : aports) {
						String[] saport=aport.split("/");
						if ( saport !=null 
								&& saport.length==2 
								&& StringUtils.isNumeric(saport[0]) 
								&& ("tcp".equals(saport[1]) || "udp".equals(saport[1]))
								) {
							V1beta1NetworkPolicyPort port=new V1beta1NetworkPolicyPort();
							port.setPort(new IntOrString(saport[0]));
							port.setProtocol(saport[1]);
							ports.add(port);
						}

					}
				}
				rule.setPorts(ports);
				spec.setIngress(Arrays.asList(new V1beta1NetworkPolicyIngressRule[] {rule}));
				np.setSpec(spec);
				npl.add(np);
				log.debug("added network policy: "+np);
			}
		}
		return npl;
	}

}
