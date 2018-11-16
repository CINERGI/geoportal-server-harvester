package org.sciencegateways.geoportal.base.security;


import com.esri.geoportal.base.util.JsonUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.json.JsonArray;
import javax.json.JsonObject;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

/**
 * Authentication for Keycloak OAuth2.
 */
@Component
public class KeycloakAuthenticationProvider implements AuthenticationProvider {
    private static final Logger LOGGER = LoggerFactory.getLogger(KeycloakAuthenticationProvider.class);
    ObjectMapper objectMapper = new ObjectMapper();
    /*
     * Instance variables
     */
    private boolean allUsersCanPublish = false;
    private String realmName;
    private String authorizeURL;
    private String adminUserName;
    private String client_id;
    private String rolePrefix;
    private String adminPassword;
    private String createAccountUrl;
    private String geoportalAdministratorsGroupId;
    private String geoportalPublishersGroupId;

    /** True if all authenticated users shoudl have a Publisher role. */
    public boolean getAllUsersCanPublish() {
        return allUsersCanPublish;
    }

    /** True if all authenticated users should have a Publisher role. */
    public void setAllUsersCanPublish(boolean allUsersCanPublish) {
        this.allUsersCanPublish = allUsersCanPublish;
    }

    public String getrealmName() {
        return realmName;
    }

    public void setrealmName(String realmName) {
        this.realmName = realmName;
    }

    public String getAuthorizeURL() {
        return authorizeURL;
    }

    public void setAuthorizeURL(String authorizeURL) {
        this.authorizeURL = authorizeURL;
    }

    public String getadminUserName() {
        return adminUserName;
    }

    public void setadminUserName(String adminUserName) {
        this.adminUserName = adminUserName;
    }
    public String getadminPassword(){
        return adminPassword;
    }

    public void setadminPassword(String adminPassword){
        this.adminPassword=adminPassword;
    }
    /** The id of the ArcGIS group containing Geoportal administrators (optional). */
    public String getGeoportalAdministratorsGroupId() {
        return geoportalAdministratorsGroupId;
    }
    /** The id of the ArcGIS group containing Geoportal administrators (optional). */
    public void setGeoportalAdministratorsGroupId(String geoportalAdministratorsGroupId) {
        this.geoportalAdministratorsGroupId = geoportalAdministratorsGroupId;
    }

    /** The id of the ArcGIS group containing Geoportal publishers (optional). */
    public String getGeoportalPublishersGroupId() {
        return geoportalPublishersGroupId;
    }
    /** The id of the ArcGIS group containing Geoportal publishers (optional). */
    public void setGeoportalPublishersGroupId(String geoportalPublishersGroupId) {
        this.geoportalPublishersGroupId = geoportalPublishersGroupId;
    }

    public String getClient_id() {
        return client_id;
    }

    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }
    public String getRolePrefix() {
        return rolePrefix;
      }
     
    public void setRolePrefix(String rolePrefix) {
        this.rolePrefix = rolePrefix;
      }
    /** The create account URL. */
    public String getCreateAccountUrl() {
        return createAccountUrl;
    }
    /** The create account URL. */
    public void setCreateAccountUrl(String createAccountUrl) {
        this.createAccountUrl = createAccountUrl;
    }

    private String getThisReferer() {
        try {
            return InetAddress.getLocalHost().getCanonicalHostName();
        } catch (UnknownHostException ex) {
            return "";
        }
    }
/** Methods =============================================================== */

    /**
     * Get the roles for a user.
     * @param username the username
     * @param token the ArcGIS token
     * @param referer the HTTP referer
     * @return the roles
     * @throws AuthenticationException
     */
//    private List<GrantedAuthority> executeGetRoles(String username, String token, String referer)
//            throws AuthenticationException {
//        List<GrantedAuthority> roles = new ArrayList<>();
//        List<String> groupKeys = new ArrayList<>();
//        String adminGroupId = this.getGeoportalAdministratorsGroupId();
//        String pubGroupId = this.getGeoportalPublishersGroupId();
//        boolean allUsersCanPublish = this.getAllUsersCanPublish();
//        boolean isInAdminGroup = false;
//        boolean isInPubGroup = false;
//        boolean hasOrgAdminRole = false;
//        boolean hasOrgPubRole = false;
//        boolean hasOrgUserRole = false;
//
////        String restBaseUrl = this.getRestUrl();
////        String url = restBaseUrl+"/community/self/";
////        try {
////            url += "?f=json&token="+ URLEncoder.encode(token,"UTF-8");
////        } catch (UnsupportedEncodingException e) {}
//    /*
//    String url = restBaseUrl+"/community/users/";
//    try {
//      url += URLEncoder.encode(username,"UTF-8");
//      url += "?f=json&token="+URLEncoder.encode(token,"UTF-8");
//    } catch (UnsupportedEncodingException e) {}
//    */
//
//        RestTemplate rest = new RestTemplate();
//        HttpHeaders headers = new HttpHeaders();
//        if (referer != null) {
//            headers.add("Referer",referer);
//        }
//        HttpEntity<String> requestEntity = new HttpEntity<>(headers);
//        ResponseEntity<String> responseEntity = rest.exchange(this.getAuthorizeURL(), HttpMethod.GET,requestEntity,String.class);
//        String response = responseEntity.getBody();
//        //System.err.println(response);;
//        //if (response != null) LOGGER.trace(response);
//        if (!responseEntity.getStatusCode().equals(org.springframework.http.HttpStatus.OK)) {
//            throw new AuthenticationServiceException("Error communicating with the authentication service.");
//        }
//        JsonObject jso = (JsonObject) JsonUtil.toJsonStructure(response);
//
//        if (jso.containsKey("role") && !jso.isNull("role")) {
//            String role = jso.getString("role");
//            if (role.equals("org_admin") || role.equals("account_admin")) hasOrgAdminRole = true;
//            if (role.equals("org_publisher") || role.equals("account_publisher")) hasOrgPubRole = true;
//            if (role.equals("org_user") || role.equals("account_user")) hasOrgUserRole = true;
//        }
//
//        if (jso.containsKey("groups") && !jso.isNull("groups")) {
//            JsonArray jsoGroups = jso.getJsonArray("groups");
//            for (int i=0;i<jsoGroups.size();i++) {
//                JsonObject jsoGroup = jsoGroups.getJsonObject(i);
//                String groupId = jsoGroup.getString("id");
//                String groupName = jsoGroup.getString("title");
//                String groupKey = groupId+"_..._"+groupName;
//                groupKeys.add(groupKey);
//                if ((adminGroupId != null) && (adminGroupId.length() > 0) && adminGroupId.equals(groupId)) {
//                    isInAdminGroup = true;
//                }
//                if ((pubGroupId != null) && (pubGroupId.length() > 0) && pubGroupId.equals(groupId)) {
//                    isInPubGroup = true;
//                }
//            }
//        }
//
//        boolean isAdmin = false;
//        boolean isPublisher = false;
//        if ((adminGroupId != null) && (adminGroupId.length() > 0)) {
//            if (isInAdminGroup) isAdmin = true;
//        } else {
//            if (hasOrgAdminRole) isAdmin = true;
//        }
//        if (allUsersCanPublish) {
//            if (hasOrgAdminRole || hasOrgPubRole || hasOrgUserRole) {
//                isPublisher = true;
//            } else {
//                // This is a Public account (Facebook, ...)
//                isPublisher = true;
//            }
//        }
//        if ((pubGroupId != null) && (pubGroupId.length() > 0)) {
//            if (isInPubGroup) isPublisher = true;
//        } else {
//            if (hasOrgPubRole) isPublisher = true;
//        }
//
//        String pfx = StringUtils.trimToEmpty(this.getRolePrefix());
//        if (isAdmin) {
//            roles.add(new SimpleGrantedAuthority(pfx+"ADMIN"));
//            if (!isPublisher) roles.add(new SimpleGrantedAuthority(pfx+"PUBLISHER"));
//        }
//        if (isPublisher) {
//            roles.add(new SimpleGrantedAuthority(pfx+"PUBLISHER"));
//        }
//
//        if (jso.containsKey("username") && !jso.isNull("username")) {
//            if (!username.equals(jso.getString("username"))) {
//                throw new BadCredentialsException("Credential mismatch.");
//            }
//            roles.add(new SimpleGrantedAuthority(pfx+"USER"));
//        } else {
//            throw new BadCredentialsException("Credential mis-match.");
//        }
//
//    /*
//    GeoportalContext gc = GeoportalContext.getInstance();
//    if (gc.getSupportsGroupBasedAccess()) {
//      for (String groupKey: groupKeys) {
//        roles.add(new SimpleGrantedAuthority(groupKey));
//      }
//    }
//    */
//        return roles;
//    }
    /*
     * Get keycloak token
     * @param username username
     * @param password password
     * @param referer referer
     * @return token
     * @throws AuthenticationException
     */
    private String executeGetToken(String username, String password, String referer) throws AuthenticationException {

        String access_token = null;
        if (username == null || username.length() == 0 || password == null || password.length() == 0) {
            throw new BadCredentialsException("Invalid credentials.");
        }
        String rest_url = this.getAuthorizeURL();
        HttpClient client = HttpClientBuilder.create().build();
        HttpPost post = new HttpPost(rest_url);
        try {
            List<BasicNameValuePair> urlParameters = new ArrayList<BasicNameValuePair>();
            urlParameters.add(new BasicNameValuePair("grant_type", "password"));
            urlParameters.add(new BasicNameValuePair("client_id", this.getClient_id()));
            urlParameters.add(new BasicNameValuePair("username", username));
            urlParameters.add(new BasicNameValuePair("password", password));
            post.setEntity(new UrlEncodedFormEntity(urlParameters));
        } catch (UnsupportedEncodingException exception) {
            throw new AuthenticationServiceException("Error in encoding the api parameters");
        }
        try {
            HttpResponse response = client.execute(post);
            int response_code=response.getStatusLine().getStatusCode();
            if(response_code== HttpStatus.SC_UNAUTHORIZED){
                throw new BadCredentialsException("Invalid credentials.");
            }
            else if(response_code== HttpStatus.SC_OK){
               // JsonObject json_response = (JsonObject) JsonUtil.toJsonStructure(EntityUtils.toString(response.getEntity(), "UTF-8"));
              JsonNode json_response = (JsonNode) objectMapper.readTree( EntityUtils.toString(response.getEntity()));

                if (json_response.has("access_token")){
                    access_token=json_response.get("access_token").asText();
                    
                
                }
                else{
                    throw new AuthenticationServiceException("Unable to get access token from the service");
                }
                if (access_token == null || access_token.length() == 0) {
                    throw new BadCredentialsException("Invalid credentials.");
                  }
                
            }

            else{
                throw new AuthenticationServiceException("Error communicating with the authentication service.");
            }
        } catch (IOException e) {
            throw new AuthenticationServiceException("Error in communicating with authentication service");
        }
          return access_token;
      }

      /*
        * Get the roles for a user.
        * @param username the username 
        * @param token the keycloak token
        * @param referer the HTTP referer
        * @return the roles
        * @throws AuthenticationException
       */
      private List<GrantedAuthority> executeGetRoles(String username, String token, String referer)
       /*
       */
      throws AuthenticationException {
        List<GrantedAuthority> roles=new ArrayList<>();
        String pfx = chkStr(this.getRolePrefix(),"").trim();
        roles.add(new SimpleGrantedAuthority(pfx+"ADMIN"));
        roles.add(new SimpleGrantedAuthority(pfx+"PUBLISHER"));
        roles.add(new SimpleGrantedAuthority(pfx+"USER"));
//          roles.add(new SimpleGrantedAuthority("ADMIN"));
//          roles.add(new SimpleGrantedAuthority("PUBLISHER"));
//          roles.add(new SimpleGrantedAuthority("USER"));

        return roles;


      }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        LOGGER.debug("KeycloakAuthenticationProvider:authenticate");
        String username=authentication.getName();
        String password=authentication.getCredentials().toString();
        String referer=this.getThisReferer();
        String token=executeGetToken(username, password, referer);
        List<GrantedAuthority> roles=executeGetRoles(username, token, referer);
        return new UsernamePasswordAuthenticationToken(username, password, roles);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }

    /**
     * Check a string value.
     * @param v the string
     * @param defaultVal the default value (if the supplied string is empty)
     * @return the trimmed or default value
     */
    public static String chkStr(String v, String defaultVal) {
        v = v.trim();
        if (v != null && v.length() > 0) {
            return v;
        }
        return defaultVal;
    }
}