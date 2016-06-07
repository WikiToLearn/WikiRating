import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import javax.net.ssl.HttpsURLConnection;
import com.orientechnologies.orient.jdbc.OrientJdbcConnection;
import com.tinkerpop.blueprints.impls.orient.OrientBaseGraph;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import net.sourceforge.jwbf.mediawiki.ApiRequestBuilder;
import net.sourceforge.jwbf.mediawiki.bots.MediaWikiBot;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
 

@Path("/rate")
public class rate {
 
	  @GET
	  @Produces("application/json")
	  public Response welcome() {
 		String result = "You reached the homepage please add 'allpages' in the URL to get all the pages in https://en.wikitolearn.org";
		return Response.status(200).entity(result).build();
		 
	  }
 
	  
	  
	  
	  @Path("{name}")
	  @GET
	  @Produces("application/json")
	  public Response allPages(@PathParam("name") String name) throws JSONException {
		  
		  
		  //Connection to the database
		  
		  String result = "fail";
		  Connection conn;
		  Statement stmt=null;
		  Properties info = new Properties();
		  info.put("user", "root");
		  info.put("password", "admin123");

		  try {
			Class.forName("com.orientechnologies.orient.jdbc.OrientJdbcDriver");
		} catch (ClassNotFoundException e1) {
			result="Connection failed!";
			e1.printStackTrace();
			return Response.status(500).entity(result).build();
		}
		  
		  try {
			   conn = (OrientJdbcConnection) DriverManager.getConnection("jdbc:orient:remote:localhost/wikirate", info);
		} catch (SQLException e) {
			result="Connection failed!";
			e.printStackTrace();
			return Response.status(500).entity(result).build();
			
		}
		  
		  
		try {
			stmt = conn.createStatement();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			result="statement creation failed! ";
			e.printStackTrace();
			return Response.status(500).entity(result).build();
		}

		  
		
		/*MediaWikiBot b = new MediaWikiBot("http://en.wikitolearn.org/");
		 b.login("", "");
		 System.out.println(b.readData("Main Page").getText());*/	//Mediawiki bot by jwbf(Client API)
		 
		
		 try{
			String USER_AGENT = "WikiRatingAPI";
			String url = "https://en.wikitolearn.org/api.php";
			URL obj = new URL(url);
			HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

			//adding request header
			con.setRequestMethod("POST");
			con.setRequestProperty("User-Agent", USER_AGENT);
			con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

			String urlParameters = "action=query&list="+name+"&apfrom=a&aplimit=max&apnamespace=0&format=json";
			
			// Send post request
			con.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.writeBytes(urlParameters);
			wr.flush();
			wr.close();

			int responseCode = con.getResponseCode();
			System.out.println("\nSending 'POST' request to URL : " + url);
			System.out.println("Post parameters : " + urlParameters);
			System.out.println("Response Code : " + responseCode);

			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
			
			//print result
			result=response.toString();
			System.out.println(result);
			
			
		 }catch(Exception e){			
			 result ="fail!";
			 e.printStackTrace();
		 }
		 
		 //JSON interpretation
		 try {  
			 	JSONObject js=new JSONObject(result);
			 	JSONObject js2=js.getJSONObject("query");
			 	JSONArray arr=js2.getJSONArray("allpages");
				result="";
			 	JSONObject dummy;
			 	for(int i=0;i<arr.length();i++){
			 		dummy=arr.getJSONObject(i);
			 		result=result+dummy.get("title")+" \n";
			 		System.out.println(result);
			 		
			 		

					  try {
						ResultSet rs = stmt.executeQuery("INSERT INTO V SET name="+"'"+dummy.get("pageid")+"'");
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						result="Query failed ";
						e.printStackTrace();
						return Response.status(500).entity(result).build();
					}
			 		
			 		
			 	}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		
		  
		return Response.status(200).entity(result).build();
	  }
}