package cart.ui.common;

public class MemberAuth {

    private final String email;
    private final String password;

    public MemberAuth(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}
