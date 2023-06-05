package cart.authentication;

import cart.exception.AuthenticationException;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.stereotype.Component;

@Component
public class BasicAuthorizationExtractor {

    private static final String BASIC_TYPE = "Basic";
    private static final String DELIMITER = ":";
    private static final int EMAIL_INDEX = 0;
    private static final int PASSWORD_INDEX = 1;

    public AuthInfo extract(String header) {
        validateAuthorizationHeader(header);

        String authHeaderValue = header.substring(BASIC_TYPE.length()).trim();
        String decoded = new String(Base64.decodeBase64(authHeaderValue));

        return convertToAuthInfo(decoded);
    }

    private AuthInfo convertToAuthInfo(String decoded) {
        String[] credentials = decoded.split(DELIMITER);

        try {
            String email = credentials[EMAIL_INDEX];
            String password = credentials[PASSWORD_INDEX];

            return new AuthInfo(email, password);
        } catch (ArrayIndexOutOfBoundsException ex) {
            throw new AuthenticationException.InvalidTokenFormat(ex);
        }
    }

    private void validateAuthorizationHeader(String header) {
        if (header == null) {
            throw new AuthenticationException.ForbiddenMember();
        }
        if (checkNonBasicType(header)) {
            throw new AuthenticationException.InvalidAuthentication();
        }
    }

    private boolean checkNonBasicType(String header) {
        return !header.toLowerCase().startsWith(BASIC_TYPE.toLowerCase());
    }
}
