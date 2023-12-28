package me.preetham.samsaram.service;

import com.google.gson.Gson;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import me.preetham.samsaram.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

@Service
public class UserService implements IUserService {

  private Logger logger = LoggerFactory.getLogger(UserService.class);
  private final HttpClient userClient = HttpClient.newHttpClient();

  @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
  private String issuer;

  @Override
  public User getUserDetails(Authentication authentication) {
    Jwt jwt = (Jwt) authentication.getPrincipal();
    String token = jwt.getTokenValue();
    try {
      HttpResponse<String> response = userClient.send(
          HttpRequest.newBuilder().GET().uri(URI.create(issuer + "/oauth2/userinfo")).header(
              HttpHeaders.AUTHORIZATION, "Bearer " + token).build(),
          BodyHandlers.ofString());
      if (response.statusCode() != HttpStatus.OK.value()) {
        logger.error("Auth endpoint returned no ok response");
        return null;
      }
      return new Gson().fromJson(response.body(), User.class);
    } catch (Exception e) {
      logger.error(e.getMessage());
    }
    return null;
  }
}
