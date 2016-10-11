package com.zerses.camelsandbox.cxfrs;

import javax.jws.WebService;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/")
@WebService
public class MyRsResource1 {
	
	@GET
	@Path("/A/{subpath1}/{subpath2}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response methodA(@PathParam("subpath1") String subpath1, @PathParam("subpath2") String subpath2, @QueryParam("parm1") String parm1)
	{
		return null;
	}
}
