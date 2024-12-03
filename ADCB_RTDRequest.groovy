import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.*;
import java.util.concurrent.*;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import org.apache.camel.Exchange;
import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;





public class RTDRequest {
	
private static Map map1 = Collections.synchronizedMap(new HashMap());

public static Map<String, String> fetchCustomerInfo(String EntityID,Map<String, String> map) throws IOException {
        
	//	 Properties props = new Properties();
     //   props.load(new FileInputStream("")); // camel-context.properties path

        // SSL Configuration
        String keyPath = ""; 
        String keyPass = "changeit";
        String keyType = "JKS";
        System.setProperty("javax.net.ssl.keyStore", keyPath);
        System.setProperty("javax.net.ssl.keyStorePassword", keyPass);
        System.setProperty("javax.net.ssl.keyStoreType", keyType);

        // OAuth 2.0 Token Retrieval
       // String clientId = props.getProperty("client_id");
      //  String clientSecret = props.getProperty("client_secret");
	    String clientId ="";
		String clientSecret = "";
        String tokenUrl = "https://devmag.adcb.com/auth/oauth/v2/token";

        URL tokenUrlObj = new URL(tokenUrl);
        HttpsURLConnection tokenConnection = (HttpsURLConnection) tokenUrlObj.openConnection();
        tokenConnection.setRequestMethod("POST");
        tokenConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        tokenConnection.setDoOutput(true);

        // Construct the token request body
        String requestBody = "grant_type=client_credentials&client_id=" + URLEncoder.encode(clientId) +
                             "&client_secret=" + URLEncoder.encode(clientSecret) +
                             "&scope=Accounts";

        // Send token request
        try (OutputStream os = tokenConnection.getOutputStream()) {
            os.write(requestBody.getBytes("utf-8"));
        }

        // Parse the token response
        BufferedReader tokenReader = new BufferedReader(new InputStreamReader(tokenConnection.getInputStream()));
        StringBuilder tokenContent = new StringBuilder();
        String inputLine;
        while ((inputLine = tokenReader.readLine()) != null) {
            tokenContent.append(inputLine);
        }
        tokenReader.close();

        // Extract the access token from the response JSON
        String accessToken = new JSONObject(tokenContent.toString()).getString("access_token");
        tokenConnection.disconnect();

        if (accessToken == null || accessToken.isEmpty()) {
            throw new IOException("Failed to obtain access token");
        }
      
	  
	  
	  
	   // Make the API call to fetch customer_info
	   
	    String customerId = EntityID;
        String apiUrl = "https://devmag.adcb.com/v2/customer_info?CustomerId=" + customerId;

        URL apiURL = new URL(apiUrl);
        HttpsURLConnection apiConnection = (HttpsURLConnection) apiURL.openConnection();
        apiConnection.setRequestProperty("Content-Type", "application/json");
        apiConnection.setRequestProperty("Authorization", "Bearer " + accessToken);
        apiConnection.setRequestProperty("x-fapi-interaction-id", UUID.randomUUID().toString());
        apiConnection.setRequestMethod("GET");

        System.out.println("Connection established to " + apiUrl);
		
  

        // Reading JSON response
        InputStreamReader reader = new InputStreamReader(apiConnection.getInputStream());
        StringBuilder buf = new StringBuilder();
        char[] cbuf = new char[2048];
        int num;
        while (-1 != (num = reader.read(cbuf))) {
            buf.append(cbuf, 0, num);
        }
        String jsonResponse = buf.toString();
        System.out.println("Response received: " + jsonResponse);

        // Parse JSON response
        // Parse JSON response
JSONObject jsonObject = new JSONObject(jsonResponse);
JSONObject data = jsonObject.optJSONObject("Data").optJSONObject("Customer");



// Parse EmployerInfo
JSONObject employerInfo = data.optJSONObject("CustomerPersonalDetails").optJSONObject("EmployerInfo");
map.put("DESIGNATION", employerInfo.optString("Designation", ""));


// Parse PersonalInfo
JSONObject personalInfo = data.optJSONObject("CustomerPersonalDetails").optJSONObject("PersonalInfo");
map.put("GENDER", personalInfo.optString("Gender", ""));
map.put("NON_RESIDENT_FLAG", personalInfo.optString("ResidencyStatus", ""));
map.put("CUSTOMER_SINCE", personalInfo.optString("RegistrationDate", ""));
map.put("MOTHER_MAIDEN_NAME", personalInfo.optString("MotherMaidenName", ""));
map.put("FIRST_NAME", personalInfo.optString("FullName", ""));
map.put("MIDDLE_NAME", personalInfo.optString("FullName", ""));
map.put("LAST_NAME", personalInfo.optString("FullName", ""));
map.put("CUST_RM_NAME", personalInfo.optString("RMName", ""));
map.put("NATIONALITY", personalInfo.optString("NationalityDesc", ""));
map.put("MARITIAL_STATUS", personalInfo.optString("MaritalStatus", ""));
map.put("CUSTOMER_TYPE", personalInfo.optString("Category", ""));
map.put("ROYAL_FLAG", personalInfo.optString("Category", ""));
map.put("VIP_FLAG", personalInfo.optString("Category", ""));
map.put("PEP_FLAG", personalInfo.optString("Category", ""));
map.put("CUSTOMER_SEGMENT", personalInfo.optString("Category", ""));


// Parse KYCInfo
JSONObject kycInfo = data.optJSONObject("CustomerPersonalDetails").optJSONObject("KYCInfo");
map.put("PASSPORT_NUMBER", kycInfo.optString("PassportNumber", ""));
map.put("PASSPORT_EXPIRY_DT", kycInfo.optString("ExpiryDate", ""));
map.put("VISA_EXPIRY_DT", kycInfo.optString("VisaExpiryDate", ""));
map.put("EMIRATES_ID_EXPIRY_DT", kycInfo.optString("UAENationalIdExpiryDate", ""));
map.put("VISA_NUMBER", kycInfo.optString("VisaNumber", ""));
map.put("TRADE_LICENSE_NUM", kycInfo.optString("TradeLicenseNumber", ""));


// Close the connection and return the map
apiConnection.disconnect();
return map;
    }

public static Map<String, String> fetchAccountInfo(String EntityID,Map<String, String> map) throws IOException {
        Properties props = new Properties();
        props.load(new FileInputStream(""));

        // SSL Configuration
        String keyPath = "";
        String keyPass = "changeit";
        String keyType = "JKS";
        System.setProperty("javax.net.ssl.keyStore", keyPath);
        System.setProperty("javax.net.ssl.keyStorePassword", keyPass);
        System.setProperty("javax.net.ssl.keyStoreType", keyType);

        // OAuth 2.0 Token Retrieval
       // String clientId = props.getProperty("client_id");
      //  String clientSecret = props.getProperty("client_secret");
	    String clientId ="";
		String clientSecret ="";
        String tokenUrl = "https://devmag.adcb.com/auth/oauth/v2/token";

        URL tokenUrlObj = new URL(tokenUrl);
        HttpsURLConnection tokenConnection = (HttpsURLConnection) tokenUrlObj.openConnection();
        tokenConnection.setRequestMethod("POST");
        tokenConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        tokenConnection.setDoOutput(true);

        // Construct the token request body
        String requestBody = "grant_type=client_credentials&client_id=" + URLEncoder.encode(clientId) +
                             "&client_secret=" + URLEncoder.encode(clientSecret) +
                             "&scope=Accounts";

        // Send token request
        try (OutputStream os = tokenConnection.getOutputStream()) {
            os.write(requestBody.getBytes("utf-8"));
        }

        // Parse the token response
        BufferedReader tokenReader = new BufferedReader(new InputStreamReader(tokenConnection.getInputStream()));
        StringBuilder tokenContent = new StringBuilder();
        String inputLine;
        while ((inputLine = tokenReader.readLine()) != null) {
            tokenContent.append(inputLine);
        }
        tokenReader.close();

        // Extract the access token from the response JSON
        String accessToken = new JSONObject(tokenContent.toString()).getString("access_token");
        tokenConnection.disconnect();

        if (accessToken == null || accessToken.isEmpty()) {
            throw new IOException("Failed to obtain access token");
        }

        // Make the API call to fetch account info
		String customerId =  EntityID ;
        String apiUrl = "https://devmag.adcb.com/v2/accounts/inquiry?CustomerId=" + customerId;

        URL apiURL = new URL(apiUrl);
        HttpsURLConnection apiConnection = (HttpsURLConnection) apiURL.openConnection();
        apiConnection.setRequestProperty("Content-Type", "application/json");
        apiConnection.setRequestProperty("Authorization", "Bearer " + accessToken);
        apiConnection.setRequestProperty("x-fapi-interaction-id", UUID.randomUUID().toString());
        apiConnection.setRequestMethod("GET");

        System.out.println("Connection established to " + apiUrl);

        // Reading JSON response
        InputStreamReader reader = new InputStreamReader(apiConnection.getInputStream());
        StringBuilder buf = new StringBuilder();
        char[] cbuf = new char[2048];
        int num;
        while (-1 != (num = reader.read(cbuf))) {
            buf.append(cbuf, 0, num);
        }
        String jsonResponse = buf.toString();
        System.out.println("Response received: " + jsonResponse);

         // Parse JSON response
        JSONObject jsonObject = new JSONObject(jsonResponse);
		JSONObject data = jsonObject.optJSONObject("Data");
        JSONArray accountArray = data.getJSONArray("Account");
		
		   Map<String, Number> map2 = new HashMap<>(); // this map is used to store  all the account id for a customer .
         // Loop through each account object in the array
        for (int i = 0; i < accountArray.length(); i++) {
            JSONObject account = accountArray.optJSONObject(i);
            
			
            // Create variables for each field in the account
            String AccountId = account.optString("AccountId", "");
            String Status = account.optString("Status", "");
            String Currency = account.optString("Currency", "");
            String AccountType = account.optString("AccountType", "");
            String accountSubType = account.optString("AccountSubType", "");
            String productCode = account.optString("ProductCode", "");
            String Description = account.optString("Description", "");
            String OpeningDate = account.optString("OpeningDate", "");
            String maturityDate = account.optString("MaturityDate", "");
            String loanAmount = account.optString("LoanAmount", "");
            String tenor = account.optString("Tenor", "");
            String interestRate = account.optString("InterestRate", "");
            String emiAmount = account.optString("EMIAmount", "");
            String nextEmiDate = account.optString("NextEMIDate", "");
            String numberOfEmiPaid = account.optString("NumberOfEMIPaid", "");
            String outstandingBalance = account.optString("OutstandingBalance", "");
            String repaymentFrequency = account.optString("RepaymentFrequency", "");

            // Store variables in map with unique keys for each account entry
            map.put("Account_" + i + "_AccountId", AccountId);
            map.put("Account_" + i + "_Status", Status);
            map.put("Account_" + i + "_AccountType", AccountType);
            map.put("Account_" + i + "_Description", Description);
            map.put("Account_" + i + "_OpeningDate", OpeningDate);
			map.put("Account_" + i + "_Currency", Currency);
			
			map2.put(AccountId, i);
			
        }
		
		

        apiConnection.disconnect();
        return fetchBalanceInfo(customerId,map,map2);
    }

public static Map<String, String> fetchBalanceInfo(String EntityID,Map<String, String> map,Map<String, Number> map2) throws IOException {
         Properties props = new Properties();
            props.load(new FileInputStream(" "));  //

            // SSL Configuration
            String keyPath = "";
           String keyPass = "changeit";
           String keyType = "JKS";
           System.setProperty("javax.net.ssl.keyStore", keyPath);
           System.setProperty("javax.net.ssl.keyStorePassword", keyPass);
           System.setProperty("javax.net.ssl.keyStoreType", keyType);

            // OAuth 2.0 Token Retrieval
           // String clientId = props.getProperty("client_id");
          //  String clientSecret = props.getProperty("client_secret");
		  String clientId = "";
		  String clientSecret = "";
            String tokenUrl = "https://devmag.adcb.com/auth/oauth/v2/token";

            URL tokenUrlObj = new URL(tokenUrl);
            HttpsURLConnection tokenConnection = (HttpsURLConnection) tokenUrlObj.openConnection();
            tokenConnection.setRequestMethod("POST");
            tokenConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            tokenConnection.setDoOutput(true);

            // Construct the token request body
            String requestBody = "grant_type=client_credentials&client_id=" + URLEncoder.encode(clientId) +
                                 "&client_secret=" + URLEncoder.encode(clientSecret) +
                                 "&scope=" + URLEncoder.encode("AccountBalancesDetails", "UTF-8");

            // Send token request
            try (OutputStream os = tokenConnection.getOutputStream()) {
                os.write(requestBody.getBytes("utf-8"));
            }

            // Parse the token response
            BufferedReader tokenReader = new BufferedReader(new InputStreamReader(tokenConnection.getInputStream()));
            StringBuilder tokenContent = new StringBuilder();
            String inputLine;
            while ((inputLine = tokenReader.readLine()) != null) {
                tokenContent.append(inputLine);
            }
            tokenReader.close();

            // Extract the access token from the response JSON
            String accessToken = new JSONObject(tokenContent.toString()).getString("access_token");
            tokenConnection.disconnect();

            if (accessToken == null || accessToken.isEmpty()) {
                throw new IOException("Failed to obtain access token");
            }

            // Make the API call to fetch balance info
            String customerId = EntityID ; // Example Customer ID for successful response
            String apiUrl = "https://devmag.adcb.com/v2/balances?CustomerId=" + customerId;

            URL apiURL = new URL(apiUrl);
            HttpsURLConnection apiConnection = (HttpsURLConnection) apiURL.openConnection();
            apiConnection.setRequestProperty("Content-Type", "application/json");
            apiConnection.setRequestProperty("Authorization", "Bearer " + accessToken);
            apiConnection.setRequestProperty("x-fapi-interaction-id", UUID.randomUUID().toString());
            apiConnection.setRequestMethod("GET");

            System.out.println("Connection established to " + apiUrl);

       // Reading JSON response
InputStreamReader reader = new InputStreamReader(apiConnection.getInputStream());
StringBuilder buf = new StringBuilder();
char[] cbuf = new char[2048];
int num;
while (-1 != (num = reader.read(cbuf))) {
    buf.append(cbuf, 0, num);
}
String jsonResponse = buf.toString();
System.out.println("Response received: " + jsonResponse);

// Parse JSON response
JSONObject jsonObject = new JSONObject(jsonResponse);
//JSONObject example = jsonObject.optJSONObject("example");
JSONObject data = jsonObject.optJSONObject("Data");
JSONArray balanceArray = data.getJSONArray("Balance");


// Loop through each balance object in the array
for (int i = 0; i < balanceArray.length(); i++) {
    JSONObject balance = balanceArray.optJSONObject(i);

    // Extracting balance fields
    String accountId = balance.optString("AccountId", "");
    String Amount = "";
    String currency = "";

    // Extract "Amount" and its nested fields
    if (balance.has("Amount") && balance.get("Amount") instanceof JSONObject) {
        JSONObject amountObject = balance.optJSONObject("Amount");
        if (amountObject.has("Amount")) {
            Amount = amountObject.getString("Amount");
        }
        if (amountObject.has("Currency")) {
            currency = amountObject.getString("Currency");
        }
    }
	
	String creditDebitIndicator = balance.optString("CreditDebitIndicator", "");
    String type = balance.optString("Type", "");
    String dateTime = balance.optString("DateTime", "");
    String amountOnHold = balance.optString("AmountOnHold", "");
    String AmountAvailable = balance.optString("AmountAvailable", "");
     

	// Map these values with unique keys
    map.put("Acct_" + map2.get(accountId) + "_Balance_Amount", Amount);
    map.put("Acct_" + map2.get(accountId) + "_Balance_AmountAvailable", AmountAvailable);

}
        apiConnection.disconnect();
        return map;
    }
  


public void prepareADCBResponse(Exchange exchange) {
   String textXml = exchange.getIn().getBody();
		//System.out.println(textXml);
		String textXml1 = textXml.replace('"','/');
		String[] splitXml = textXml1.split("entityType=/", 2);
		String newXml1 = splitXml[1].replaceFirst("/>", "</entityType>");
		String textXml2 = "<entityRequest><entityType>" + newXml1;

		Document doc = convertStringToXMLDocument(textXml2);
		Map map = new HashMap();

		String entityType = null;
		String entityID = null;
		String multiOrg = null;
		String username = null;
		String contactID = null;
		String contactType = null;
		try 
			{
				doc.getDocumentElement().normalize();
					NodeList nList = doc.getElementsByTagName("entityRequest");
					Node nNode = nList.item(0);
					
					Element eElement = (Element) nNode;
					entityType = (eElement.getElementsByTagName("entityType")).item(0).getTextContent();
			        entityID = (eElement.getElementsByTagName("entityID")).item(0).getTextContent();
					multiOrg = (eElement.getElementsByTagName("multiOrg")).item(0).getTextContent();
					username = (eElement.getElementsByTagName("username")).item(0).getTextContent(); /* Release 2 Addition*/
					if((eElement.getElementsByTagName("contactID").item(0)) != null)
					{	contactID = (eElement.getElementsByTagName("contactID")).item(0).getTextContent(); }
					if((eElement.getElementsByTagName("contactType")).item(0) != null)
					{	contactType = (eElement.getElementsByTagName("contactType")).item(0).getTextContent(); }
						
			} catch (Exception e) {
				e.printStackTrace();
		}
		
		switch (entityType)
			{
			case "A" : 
				try {
					TimeLimitedCodeBlock.runWithTimeout(new Runnable() {
						@Override
						public void run() {
							try {
								MultifetchAccountInfo m1= new MultifetchAccountInfo(entityID);
								Thread t1= new Thread(m1);
								
								t1.start(); 
								

								t1.join();
							
								//mapThread.putAll(map1);

							}
							catch (InterruptedException e) {
								System.out.println("was interuupted! 1");
							}
						}
					}, 3, TimeUnit.SECONDS);
				}
				catch (TimeoutException e) {
					System.out.println("Got timeout! 2");
				}
				break;    
			case "X" : 
				try {
					TimeLimitedCodeBlock.runWithTimeout(new Runnable() {
						@Override
						public void run() {
							try {
								MultifetchCustomerInfo m2= new MultifetchCustomerInfo(entityID);
								Thread t2= new Thread(m2);
								
								
				
								t2.start(); 
								t2.join();
								
							
							}
							catch (InterruptedException e) {
								System.out.println("was interuupted! 3");
							}
						}
					}, 3, TimeUnit.SECONDS);
				}
				catch (TimeoutException e) {
					System.out.println("Got timeout! 4");
				}
				break;
				
			/*	case "B" : 
				try {
					TimeLimitedCodeBlock.runWithTimeout(new Runnable() {
						@Override
						public void run() {
							try {
								MultifetchBalanceInfo m3= new MultifetchBalanceInfo();
								Thread t3= new Thread(m3);
								
								
				
								t3.start(); 
								t3.join();
								
							
							}
							catch (InterruptedException e) {
								System.out.println("was interuupted! 5");
							}
						}
					}, 3, TimeUnit.SECONDS);
				}
				catch (TimeoutException e) {
					System.out.println("Got timeout! 6");
				}
				break;
		                  */
}
map.putAll(map1);


	if (contactType == null)
			contactType = "Customer Id Missing";
		if (contactID == null)
			contactID = "Customer Id Missing";

		if (entityType == 'X')
		{
			contactType = entityType;
			contactID = entityID;	
		}
			
		map.put("/entityRequest/entityType", entityType);
		map.put("/entityRequest/entityID", entityID);
		map.put("/entityRequest/multiOrg", multiOrg);
		map.put("/entityRequest/contactID", contactID);
		map.put("/entityRequest/contactType", contactType);
		
		if(responseStatus == "SUCCESS")
			exchange.getIn().setBody(map);

    
}

}




class MultifetchCustomerInfo implements Runnable{  
    
	private String entityID;

	public MultifetchCustomerInfo(String _entityID) {
		this.entityID = _entityID;
	}
	
    @Override
	public void run(){  
		RTDRequest.fetchCustomerInfo(entityID,RTDRequest.map1); 
	}
}

class MultifetchAccountInfo implements Runnable{  
	
	private String entityID;

	public MultifetchAccountInfo(String _entityID) {
		this.entityID = _entityID;
	}
	
    @Override
	public void run(){  
		RTDRequest.fetchAccountInfo(entityID,RTDRequest.map1); 
	}
}


class TimeLimitedCodeBlock {

	public static void runWithTimeout(final Runnable runnable, long timeout, TimeUnit timeUnit) throws Exception {
		runWithTimeout(new Callable<Object>() {
			@Override
			public Object call() throws Exception {
				runnable.run();
				return null;
			}
		}, timeout, timeUnit);
	}

	public static <T> T runWithTimeout(Callable<T> callable, long timeout, TimeUnit timeUnit) throws Exception {
		final ExecutorService executor = Executors.newSingleThreadExecutor();
		final Future<T> future = executor.submit(callable);
		executor.shutdown(); // This does not cancel the already-scheduled task.
		try {
			return future.get(timeout, timeUnit);
		}
		catch (TimeoutException e) {
			future.cancel(true);
			throw e;
		}
		catch (ExecutionException e) {
			//unwrap the root cause
			Throwable t = e.getCause();
			if (t instanceof Error) {
				throw (Error) t;
			} else if (t instanceof Exception) {
				throw (Exception) t;
			} else {
				throw new IllegalStateException(t);
			}
		}
	}

}
