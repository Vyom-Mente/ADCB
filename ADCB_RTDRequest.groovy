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

public static Map<String, String> fetchCustomerInfo(Map<String, String> map) throws IOException {
        
		 Properties props = new Properties();
        props.load(new FileInputStream("/usr/local/EOP/EOP-1.3.0/config/spring/camel-context.properties"));

        // SSL Configuration
        String keyPath = "/usr/local/EOP/EOP-1.3.0/config/realtimedemo/rtdkeystore.jks";
        String keyPass = "changeit";
        String keyType = "JKS";
        System.setProperty("javax.net.ssl.keyStore", keyPath);
        System.setProperty("javax.net.ssl.keyStorePassword", keyPass);
        System.setProperty("javax.net.ssl.keyStoreType", keyType);

        // OAuth 2.0 Token Retrieval
        String clientId = props.getProperty("client_id");
        String clientSecret = props.getProperty("client_secret");
        String tokenUrl = "https://devmag.adcb.com/auth/oauth/v2/token";

        URL tokenUrlObj = new URL(tokenUrl);
        HttpsURLConnection tokenConnection = (HttpsURLConnection) tokenUrlObj.openConnection();
        tokenConnection.setRequestMethod("POST");
        tokenConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        tokenConnection.setDoOutput(true);

        // Construct the token request body
        String requestBody = "grant_type=client_credentials&client_id=" + URLEncoder.encode(clientId, "UTF-8") +
                             "&client_secret=" + URLEncoder.encode(clientSecret, "UTF-8") +
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
        String apiUrl = "https://devmag.adcb.com/v2/customer_info";

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


// Parse ProductRelationInfo
JSONObject productRelationInfo = data.optJSONObject("ProductRelationInfo");
for (int i = 1; i <= 8; i++) {
    map.put("ProductRelation" + i, productRelationInfo.optString("ProductRelation" + i, ""));
}

// Parse ContactDetails
JSONObject contactDetails = data.optJSONObject("CustomerPersonalDetails").optJSONObject("ContactDetails");
map.put("ContactDetails_AddressZip", contactDetails.optString("AddressZip", ""));
map.put("ContactDetails_Country", contactDetails.optString("Country", ""));
map.put("ContactDetails_City", contactDetails.optString("City", ""));
map.put("ContactDetails_HomeLandlineNumber", contactDetails.optString("HomeLandlineNumber", ""));
map.put("ContactDetails_AddressLine1", contactDetails.optString("AddressLine1", ""));
map.put("ContactDetails_AddressLine2", contactDetails.optString("AddressLine2", ""));
map.put("ContactDetails_AddressLine3", contactDetails.optString("AddressLine3", ""));
map.put("ContactDetails_MobileNumber", contactDetails.optString("MobileNumber", ""));
map.put("ContactDetails_PhoneNumber", contactDetails.optString("PhoneNumber", ""));
map.put("ContactDetails_EmailId", contactDetails.optString("EmailId", ""));
map.put("ContactDetails_State", contactDetails.optString("State", ""));
map.put("ContactDetails_OfficeLandlineNumber", contactDetails.optString("OfficeLandlineNumber", ""));
map.put("ContactDetails_Fax", contactDetails.optString("Fax", ""));

// Parse ResidenceAddress
JSONObject residenceAddress = data.optJSONObject("CustomerPersonalDetails").optJSONObject("ResidenceAddress");
map.put("ResidenceAddress_AddressZip", residenceAddress.optString("AddressZip", ""));
map.put("ResidenceAddress_BuildingName", residenceAddress.optString("BuildingName", ""));
map.put("ResidenceAddress_Country", residenceAddress.optString("Country", ""));
map.put("ResidenceAddress_StreetName", residenceAddress.optString("StreetName", ""));
map.put("ResidenceAddress_FlatNumber", residenceAddress.optString("FlatNumber", ""));
map.put("ResidenceAddress_City", residenceAddress.optString("City", ""));
map.put("ResidenceAddress_ResidenceArea", residenceAddress.optString("ResidenceArea", ""));
map.put("ResidenceAddress_State", residenceAddress.optString("State", ""));
map.put("ResidenceAddress_PhoneNumber", residenceAddress.optString("PhoneNumber", ""));

// Parse EmployerInfo
JSONObject employerInfo = data.optJSONObject("CustomerPersonalDetails").optJSONObject("EmployerInfo");
map.put("EmployerInfo_Profession", employerInfo.optString("Profession", ""));
map.put("EmployerInfo_NameofEmployer", employerInfo.optString("NameofEmployer", ""));
map.put("EmployerInfo_EmploymentType", employerInfo.optString("EmploymentType", ""));
map.put("EmployerInfo_EmploymentLocation", employerInfo.optString("EmploymentLocation", ""));
map.put("EmployerInfo_EmployeeId", employerInfo.optString("EmployeeId", ""));
map.put("EmployerInfo_Designation", employerInfo.optString("Designation", ""));
map.put("EmployerInfo_DateofJoining", employerInfo.optString("DateofJoining", ""));

// Parse PermanentAddress
JSONObject permanentAddress = data.optJSONObject("CustomerPersonalDetails").optJSONObject("PermanentAddress");
map.put("PermanentAddress_Country", permanentAddress.optString("Country", ""));
map.put("PermanentAddress_AddressZip", permanentAddress.optString("AddressZip", ""));
map.put("PermanentAddress_City", permanentAddress.optString("City", ""));
map.put("PermanentAddress_Fax", permanentAddress.optString("Fax", ""));
map.put("PermanentAddress_AddressLine1", permanentAddress.optString("AddressLine1", ""));
map.put("PermanentAddress_AddressLine2", permanentAddress.optString("AddressLine2", ""));
map.put("PermanentAddress_AddressLine3", permanentAddress.optString("AddressLine3", ""));
map.put("PermanentAddress_State", permanentAddress.optString("State", ""));
map.put("PermanentAddress_MobileNumber", permanentAddress.optString("MobileNumber", ""));
map.put("PermanentAddress_PhoneNumber", permanentAddress.optString("PhoneNumber", ""));
map.put("PermanentAddress_EmailId", permanentAddress.optString("EmailId", ""));

// Parse PersonalInfo
JSONObject personalInfo = data.optJSONObject("CustomerPersonalDetails").optJSONObject("PersonalInfo");
map.put("PersonalInfo_DateofBirth", personalInfo.optString("DateofBirth", ""));
map.put("PersonalInfo_RMCode", personalInfo.optString("RMCode", ""));
map.put("PersonalInfo_Gender", personalInfo.optString("Gender", ""));
map.put("PersonalInfo_Prefix", personalInfo.optString("Prefix", ""));
map.put("PersonalInfo_ProfitCenterName", personalInfo.optString("ProfitCenterName", ""));
map.put("PersonalInfo_NationalityCode", personalInfo.optString("NationalityCode", ""));
map.put("PersonalInfo_ResidencyStatus", personalInfo.optString("ResidencyStatus", ""));
map.put("PersonalInfo_SignatoryDesignTxt4", personalInfo.optString("SignatoryDesignTxt4", ""));
map.put("PersonalInfo_CountryofResidence", personalInfo.optString("CountryofResidence", ""));
map.put("PersonalInfo_SignatoryDesignTxt5", personalInfo.optString("SignatoryDesignTxt5", ""));
map.put("PersonalInfo_RegistrationDate", personalInfo.optString("RegistrationDate", ""));
map.put("PersonalInfo_SignatoryDesignTxt1", personalInfo.optString("SignatoryDesignTxt1", ""));
map.put("PersonalInfo_SignatoryDesignTxt2", personalInfo.optString("SignatoryDesignTxt2", ""));
map.put("PersonalInfo_SignatoryDesignTxt3", personalInfo.optString("SignatoryDesignTxt3", ""));
map.put("PersonalInfo_BenefitCategory", personalInfo.optJSONArray("BenefitCategory").optJSONObject(0).optString("BenefitCategory", ""));
map.put("PersonalInfo_MotherMaidenName", personalInfo.optString("MotherMaidenName", ""));
map.put("PersonalInfo_CustomerCategory", personalInfo.optString("CustomerCategory", ""));
map.put("PersonalInfo_NoofDependents", personalInfo.optString("NoofDependents", ""));
map.put("PersonalInfo_PromotionCode", personalInfo.optString("PromotionCode", ""));
map.put("PersonalInfo_FullName", personalInfo.optString("FullName", ""));
map.put("PersonalInfo_RMName", personalInfo.optString("RMName", ""));
map.put("PersonalInfo_RMMobileNumber", personalInfo.optString("RMMobileNumber", ""));
map.put("PersonalInfo_ProfitCenterCode", personalInfo.optString("ProfitCenterCode", ""));
map.put("PersonalInfo_DirectorName1", personalInfo.optString("DirectorName1", ""));
map.put("PersonalInfo_DirectorName2", personalInfo.optString("DirectorName2", ""));
map.put("PersonalInfo_SignatoryName5", personalInfo.optString("SignatoryName5", ""));
map.put("PersonalInfo_DirectorName3", personalInfo.optString("DirectorName3", ""));
map.put("PersonalInfo_SignatoryName4", personalInfo.optString("SignatoryName4", ""));
map.put("PersonalInfo_SignatoryName3", personalInfo.optString("SignatoryName3", ""));
map.put("PersonalInfo_DirectorName4", personalInfo.optString("DirectorName4", ""));
map.put("PersonalInfo_SourcingAgent", personalInfo.optString("SourcingAgent", ""));
map.put("PersonalInfo_SignatoryName2", personalInfo.optString("SignatoryName2", ""));
map.put("PersonalInfo_DirectorName5", personalInfo.optString("DirectorName5", ""));
map.put("PersonalInfo_SignatoryName1", personalInfo.optString("SignatoryName1", ""));
map.put("PersonalInfo_MinorStatusFlag", personalInfo.optString("MinorStatusFlag", ""));
map.put("PersonalInfo_CriteriaForPrivilege", personalInfo.optString("CriteriaForPrivilege", ""));
map.put("PersonalInfo_GroupName", personalInfo.optString("GroupName", ""));
map.put("PersonalInfo_EducationalQualification", personalInfo.optString("EducationalQualification", ""));
map.put("PersonalInfo_NationalityDesc", personalInfo.optString("NationalityDesc", ""));
map.put("PersonalInfo_RegistrationNumber", personalInfo.optString("RegistrationNumber", ""));
map.put("PersonalInfo_LanguagePreference", personalInfo.optString("LanguagePreference", ""));
map.put("PersonalInfo_CustomerSignupDate", personalInfo.optString("CustomerSignupDate", ""));
map.put("PersonalInfo_CustomerId", personalInfo.optString("CustomerId", ""));
map.put("PersonalInfo_CustomerIC", personalInfo.optString("CustomerIC", ""));
map.put("PersonalInfo_SourcingDepartment", personalInfo.optString("SourcingDepartment", ""));
map.put("PersonalInfo_LiabilityId", personalInfo.optString("LiabilityId", ""));
map.put("PersonalInfo_ShortName", personalInfo.optString("ShortName", ""));
map.put("PersonalInfo_BusinessType", personalInfo.optString("BusinessType", ""));
map.put("PersonalInfo_MaritalStatus", personalInfo.optString("MaritalStatus", ""));
map.put("PersonalInfo_CategoryCode", personalInfo.optString("CategoryCode", ""));
map.put("PersonalInfo_Category", personalInfo.optString("Category", ""));
map.put("PersonalInfo_BankingType", personalInfo.optString("BankingType", ""));
map.put("PersonalInfo_CustomerType", personalInfo.optString("CustomerType", ""));
map.put("PersonalInfo_HomeBranch", personalInfo.optString("HomeBranch", ""));
map.put("PersonalInfo_StaffFlag", personalInfo.optString("StaffFlag", ""));
map.put("PersonalInfo_MemoFlag", personalInfo.optString("MemoFlag", ""));
map.put("PersonalInfo_MemoDescription", personalInfo.optString("MemoDescription", ""));
map.put("PersonalInfo_MemoSeverity", personalInfo.optString("MemoSeverity", ""));

// Parse OfficeAddress
JSONObject officeAddress = data.optJSONObject("CustomerPersonalDetails").optJSONObject("OfficeAddress");
map.put("OfficeAddress_DepartmentName", officeAddress.optString("DepartmentName", ""));
map.put("OfficeAddress_AddressZip", officeAddress.optString("AddressZip", ""));
map.put("OfficeAddress_Country", officeAddress.optString("Country", ""));
map.put("OfficeAddress_City", officeAddress.optString("City", ""));
map.put("OfficeAddress_Fax", officeAddress.optString("Fax", ""));
map.put("OfficeAddress_CompanyName", officeAddress.optString("CompanyName", ""));
map.put("OfficeAddress_AddressLine1", officeAddress.optString("AddressLine1", ""));
map.put("OfficeAddress_AddressLine2", officeAddress.optString("AddressLine2", ""));
map.put("OfficeAddress_AddressLine3", officeAddress.optString("AddressLine3", ""));
map.put("OfficeAddress_State", officeAddress.optString("State", ""));
map.put("OfficeAddress_MobileNumber", officeAddress.optString("MobileNumber", ""));
map.put("OfficeAddress_PhoneNumber", officeAddress.optString("PhoneNumber", ""));

// Parse KYCInfo
JSONObject kycInfo = data.optJSONObject("CustomerPersonalDetails").optJSONObject("KYCInfo");
map.put("KYCInfo_PassportNumber", kycInfo.optString("PassportNumber", ""));
map.put("KYCInfo_ExpiryDate", kycInfo.optString("ExpiryDate", ""));
map.put("KYCInfo_LabourCardIssueDate", kycInfo.optString("LabourCardIssueDate", ""));
map.put("KYCInfo_VisaExpiryDate", kycInfo.optString("VisaExpiryDate", ""));
map.put("KYCInfo_LabourCardNumber", kycInfo.optString("LabourCardNumber", ""));
map.put("KYCInfo_VisaIssueDate", kycInfo.optString("VisaIssueDate", ""));
map.put("KYCInfo_UAENationalIdIssueDate", kycInfo.optString("UAENationalIdIssueDate", ""));
map.put("KYCInfo_UAENationalIdExpiryDate", kycInfo.optString("UAENationalIdExpiryDate", ""));
map.put("KYCInfo_VisaNumber", kycInfo.optString("VisaNumber", ""));
map.put("KYCInfo_LabourCardExpiryDate", kycInfo.optString("LabourCardExpiryDate", ""));
map.put("KYCInfo_IssueDate", kycInfo.optString("IssueDate", ""));
map.put("KYCInfo_UAENationalId", kycInfo.optString("UAENationalId", ""));
map.put("KYCInfo_EconomicSector", kycInfo.optString("EconomicSector", ""));
map.put("KYCInfo_TradeLicenseNumber", kycInfo.optString("TradeLicenseNumber", ""));
map.put("KYCInfo_IdType", kycInfo.optString("IdType", ""));

// Close the connection and return the map
apiConnection.disconnect();
return map;
    }


  
public static Map<String, String> fetchAccountInfo(Map<String, String> map) throws IOException {
        Properties props = new Properties();
        props.load(new FileInputStream("/usr/local/EOP/EOP-1.3.0/config/spring/camel-context.properties"));

        // SSL Configuration
        String keyPath = "/usr/local/EOP/EOP-1.3.0/config/realtimedemo/rtdkeystore.jks";
        String keyPass = "changeit";
        String keyType = "JKS";
        System.setProperty("javax.net.ssl.keyStore", keyPath);
        System.setProperty("javax.net.ssl.keyStorePassword", keyPass);
        System.setProperty("javax.net.ssl.keyStoreType", keyType);

        // OAuth 2.0 Token Retrieval
        String clientId = props.getProperty("client_id");
        String clientSecret = props.getProperty("client_secret");
        String tokenUrl = "https://devmag.adcb.com/auth/oauth/v2/token";

        URL tokenUrlObj = new URL(tokenUrl);
        HttpsURLConnection tokenConnection = (HttpsURLConnection) tokenUrlObj.openConnection();
        tokenConnection.setRequestMethod("POST");
        tokenConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        tokenConnection.setDoOutput(true);

        // Construct the token request body
        String requestBody = "grant_type=client_credentials&client_id=" + URLEncoder.encode(clientId, "UTF-8") +
                             "&client_secret=" + URLEncoder.encode(clientSecret, "UTF-8") +
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
        String apiUrl = "https://devmag.adcb.com/v2/accounts/inquiry?CustomerId=21432423";

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
        JSONArray accountArray = jsonObject.getJSONArray("Account");
		
		
         // Loop through each account object in the array
        for (int i = 0; i < accountArray.length(); i++) {
            JSONObject account = accountArray.optJSONObject(i);

            // Create variables for each field in the account
            String accountId = account.optString("AccountId", "");
            String status = account.optString("Status", "");
            String currency = account.optString("Currency", "");
            String accountType = account.optString("AccountType", "");
            String accountSubType = account.optString("AccountSubType", "");
            String productCode = account.optString("ProductCode", "");
            String description = account.optString("Description", "");
            String openingDate = account.optString("OpeningDate", "");
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
            map.put("Account_" + i + "_AccountId", accountId);
            map.put("Account_" + i + "_Status", status);
            map.put("Account_" + i + "_Currency", currency);
            map.put("Account_" + i + "_AccountType", accountType);
            map.put("Account_" + i + "_AccountSubType", accountSubType);
            map.put("Account_" + i + "_ProductCode", productCode);
            map.put("Account_" + i + "_Description", description);
            map.put("Account_" + i + "_OpeningDate", openingDate);
            map.put("Account_" + i + "_MaturityDate", maturityDate);
            map.put("Account_" + i + "_LoanAmount", loanAmount);
            map.put("Account_" + i + "_Tenor", tenor);
            map.put("Account_" + i + "_InterestRate", interestRate);
            map.put("Account_" + i + "_EMIAmount", emiAmount);
            map.put("Account_" + i + "_NextEMIDate", nextEmiDate);
            map.put("Account_" + i + "_NumberOfEMIPaid", numberOfEmiPaid);
            map.put("Account_" + i + "_OutstandingBalance", outstandingBalance);
            map.put("Account_" + i + "_RepaymentFrequency", repaymentFrequency);
        }

        apiConnection.disconnect();
        return map;
    }



public static Map<String, String> fetchBalanceInfo(Map<String, String> map) throws IOException {
         Properties props = new Properties();
            props.load(new FileInputStream("/usr/local/EOP/EOP-1.3.0/config/spring/camel-context.properties"));

            // SSL Configuration
            String keyPath = "/usr/local/EOP/EOP-1.3.0/config/realtimedemo/rtdkeystore.jks";
            String keyPass = "changeit";
            String keyType = "JKS";
            System.setProperty("javax.net.ssl.keyStore", keyPath);
            System.setProperty("javax.net.ssl.keyStorePassword", keyPass);
            System.setProperty("javax.net.ssl.keyStoreType", keyType);

            // OAuth 2.0 Token Retrieval
            String clientId = props.getProperty("client_id");
            String clientSecret = props.getProperty("client_secret");
            String tokenUrl = "https://devmag.adcb.com/auth/oauth/v2/token";

            URL tokenUrlObj = new URL(tokenUrl);
            HttpsURLConnection tokenConnection = (HttpsURLConnection) tokenUrlObj.openConnection();
            tokenConnection.setRequestMethod("POST");
            tokenConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            tokenConnection.setDoOutput(true);

            // Construct the token request body
            String requestBody = "grant_type=client_credentials&client_id=" + URLEncoder.encode(clientId, "UTF-8") +
                                 "&client_secret=" + URLEncoder.encode(clientSecret, "UTF-8") +
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
            String customerId = "625081132001"; // Example Customer ID for successful response
            String apiUrl = "https://devmag.adcb.com/v2/balances?CustomerId=625081132001";

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
    String amount = "";
    String currency = "";

    // Extract "Amount" and its nested fields
    if (balance.has("Amount") && balance.get("Amount") instanceof JSONObject) {
        JSONObject amountObject = balance.optJSONObject("Amount");
        if (amountObject.has("Amount")) {
            amount = amountObject.getString("Amount");
        }
        if (amountObject.has("Currency")) {
            currency = amountObject.getString("Currency");
        }
    }

    String creditDebitIndicator = balance.optString("CreditDebitIndicator", "");
    String type = balance.optString("Type", "");
    String dateTime = balance.optString("DateTime", "");
    String amountOnHold = balance.optString("AmountOnHold", "");
    String amountAvailable = balance.optString("AmountAvailable", "");

    // Map these values with unique keys
    map.put("Balance_" + i + "_AccountId", accountId);
    map.put("Balance_" + i + "_Amount", amount);
    map.put("Balance_" + i + "_Currency", currency);
    map.put("Balance_" + i + "_CreditDebitIndicator", creditDebitIndicator);
    map.put("Balance_" + i + "_Type", type);
    map.put("Balance_" + i + "_DateTime", dateTime);
    map.put("Balance_" + i + "_AmountOnHold", amountOnHold);
    map.put("Balance_" + i + "_AmountAvailable", amountAvailable);
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
		try 
			{
				doc.getDocumentElement().normalize();
					NodeList nList = doc.getElementsByTagName("entityRequest");
					Node nNode = nList.item(0);
					
					Element eElement = (Element) nNode;
					entityType = (eElement.getElementsByTagName("entityType")).item(0).getTextContent();
			
						
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
								MultifetchAccountInfo m1= new MultifetchAccountInfo();
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
								MultifetchCustomerInfo m2= new MultifetchCustomerInfo();
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
				
				case "B" : 
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
		
}
map.putAll(map1);
    
}


class MultifetchAccountInfo implements Runnable{  
	
    @Override
	public void run(){  
		RTDRequest.fetchAccountInfo(RTDRequest.map1); 
	}
}

class MultifetchBalanceInfo implements Runnable{  
	
    @Override
	public void run(){  
		RTDRequest.fetchBalanceInfo(RTDRequest.map1); 
	}
}

class MultifetchCustomerInfo implements Runnable{  

    @Override
	public void run(){  
		RTDRequest.fetchCustomerInfo(RTDRequest.map1); 
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
