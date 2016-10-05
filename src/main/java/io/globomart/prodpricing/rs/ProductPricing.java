package io.globomart.prodpricing.rs;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import io.globomart.prodpricing.dao.ProductPricingDao;
import io.globomart.prodpricing.dto.Pricing;
import io.globomart.prodpricing.entities.PricingEntity;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Path("prodprice")
@Api(value = "/prodprice")
public class ProductPricing {
	
	private static Logger LOGGER = LoggerFactory.getLogger(ProductPricing.class);

	@GET
	@Path("prices")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Return all Pricing", response = List.class)
	public Response getProducts(@Context UriInfo uriInfo) throws SQLException {
		
		MultivaluedMap<String, String> queryParams  = uriInfo.getQueryParameters();
		LOGGER.debug("the query params are{}",queryParams);
		
		Map<String, String> filter = null;
		if(queryParams!=null)
		{
			filter = createFilterFromQueryParams(queryParams);
		}
		
		
		List<PricingEntity> productList = ProductPricingDao.getPricing(filter);
		return Response.ok().entity(productList)
				.header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
				.allow("OPTIONS")
				.build();
	}

	private Map<String, String> createFilterFromQueryParams(MultivaluedMap<String, String> queryParams) {
		Map<String, String> filter =  new HashMap<>();
		for(Entry<String,List<String>> queryParam :queryParams.entrySet())
		{
			//TODO require NULL checks
			filter.put(queryParam.getKey(), queryParam.getValue().get(0));
		}
		
		return filter;
	}

	@GET
	@Path("prices/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Return a single product with specific id", response = Pricing.class)
	public Response getProducts(@PathParam(value = "id") int prodId) {
		PricingEntity product = ProductPricingDao.getPricingById(prodId);
		Response res = null;
		if(product!=null)
		{
			LOGGER.debug("Successfully fetched Product for id ={}",prodId);
			res =  Response.ok().entity(product).build();
		}
		else
		{
			LOGGER.warn("Could not find Product for id ={}",prodId);
			res =  Response.status(Status.NOT_FOUND).build();
		}
		return res;
	}

	@POST
	@Path("prices")
	@Consumes(MediaType.APPLICATION_JSON)
	// @ApiOperation(value = "Return a single product with specific id",
	// response = Product.class)
	public Response createProduct(Pricing pricing) throws JsonParseException, JsonMappingException, IOException {
		PricingEntity productEn =ProductPricingDao.createPricing(pricing);
		return Response.ok().entity(productEn)
				.header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
				.allow("OPTIONS")
				.build();

	}

}
