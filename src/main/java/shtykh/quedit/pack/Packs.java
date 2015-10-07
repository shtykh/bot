package shtykh.quedit.pack;

import org.springframework.stereotype.Component;
import shtykh.rest.Pack;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by shtykh on 01/10/15.
 */

@Component
@Path("/pack")
public class Packs {
	Map<String, Pack> packs = new TreeMap<>();
	
	@GET
	@Path("edit/{id}")
	public Response editPack(@PathParam("id") String id) throws IOException {
		Pack pack = packs.get(id);
		if (pack != null) {
			return pack.text();//TODO
		} else {
			return Response.status(404).build();
		}
	}
}
