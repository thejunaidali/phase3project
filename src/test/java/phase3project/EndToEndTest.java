package phase3project;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.Test;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class EndToEndTest {
	Response response;
	RequestSpecification request;
	JsonPath jpath;
	List<String> names;
	List<Integer> employeeIds;
	int employeeId;
	
	@Test
	public void test()
	{
		prepare();
		response = GetAllEmployees();
		System.out.println(response.getBody().asString());
		Assert.assertEquals(200, response.statusCode());
		
		addEmployee("John", "8000");
		Assert.assertEquals(201, response.statusCode());
		jpath = response.jsonPath();
		employeeId = jpath.get("id");
		response = GetAllEmployees();
		jpath = response.jsonPath();
		names = jpath.get("name");
		Assert.assertTrue(names.get(0).equals("John"));
		
		response = GetSingleEmployee(employeeId);
		System.out.println(response.getBody().asString());
		Assert.assertEquals(200, response.statusCode());
		jpath = response.jsonPath();
		Assert.assertTrue(jpath.get("name").equals("John"));
		
		updateEmployeeName(employeeId, "Smith");
		System.out.println(response.getBody().asString());
		Assert.assertEquals(200, response.statusCode());
		response = GetSingleEmployee(employeeId);
		jpath = response.jsonPath();
		String name = jpath.get("name");
		Assert.assertTrue(name.equals("Smith"));
		
		deleteEmployee(employeeId);
		Assert.assertEquals(200, response.statusCode());
		response = GetSingleEmployee(employeeId);
		Assert.assertEquals(404, response.statusCode());
		
		response = GetAllEmployees();
		jpath = response.jsonPath();
		names = jpath.get("name");
		Assert.assertFalse(names.contains("Smith"));
	}
	
	public void prepare()
	{
		RestAssured.baseURI = "http://localhost:3000";
		
		request = RestAssured.given();
	}
	
	public Response GetAllEmployees()
	{		
		response = request.get("employees");
		
		return response;
	}
	
	public Response GetSingleEmployee(int employeeId)
	{		
		response = request.get("employees/" + employeeId);
		
		return response;
	}
	
	public void addEmployee(String employeeName, String employeeSalary)
	{
		Map<String,Object> MapObj = new HashMap<String,Object>();
		
		MapObj.put("name", employeeName);
		MapObj.put("salary", employeeSalary);
		
		response = request.contentType(ContentType.JSON).accept(ContentType.JSON).body(MapObj).post("employees/create");
	}
	
	public void updateEmployeeName(int employeeId, String newName)
	{
		Map<String,Object> MapObj = new HashMap<String,Object>();
		
		MapObj.put("name", newName);
		MapObj.put("salary", 8000);
		
		response = request.contentType(ContentType.JSON).accept(ContentType.JSON).body(MapObj).put("employees/update/" + employeeId);
	}
	
	public void deleteEmployee(int employeeId)
	{
		response = request.delete("employees/" + employeeId);
	}
}