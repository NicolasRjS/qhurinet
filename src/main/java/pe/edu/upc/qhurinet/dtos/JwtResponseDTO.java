package pe.edu.upc.qhurinet.dtos;

public class JwtResponseDTO {
    private String token;
    private String accessToken;
    private String refreshToken;
    private String tokenType;
    private Long expiresIn;

    public JwtResponseDTO() {
    }

    public JwtResponseDTO(String token) {
        this.token = token;
        this.accessToken = token;
        this.tokenType = "Bearer";
    }

    public JwtResponseDTO(String accessToken, String refreshToken, Long expiresIn) {
        this.token = accessToken;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.expiresIn = expiresIn;
        this.tokenType = "Bearer";
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
        this.token = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public Long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(Long expiresIn) {
        this.expiresIn = expiresIn;
    }
}
