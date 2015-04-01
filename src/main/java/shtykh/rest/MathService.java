package shtykh.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import shtykh.funk.FunctionBO;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

@Component
@Path("/math")
public class MathService {

	@Autowired
	FunctionBO functionBO;

	@GET
	@Path("/funk")
	public Response summImpl(@QueryParam("a") int a, @QueryParam("b") int b) {
		int c = functionBO.apply(a, b);
		String result = functionBO.getName() + " of " + a + " and " + b + " = " + c;
		return Response.status(200).entity(result).build();
	}

}