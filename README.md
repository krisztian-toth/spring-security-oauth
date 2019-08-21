# Spring Security OAuth2 server
An example OAuth2 server setup using Spring Security OAuth where the access token is a JWT and the refresh token is a 
random UUID which is then persisted in a relational database using Spring Data JPA. The main advantage of this setup is 
that it does not require the access tokens to be persisted (which means they will have to be short-lived tokens as they 
cannot be revoked), a key can be used to verify the validity and integrity of the token. The refresh token, however is 
persisted and can be revoked any time and is re-issued every time a token is refreshed. This mitigates the risk of 
someone getting hold of a refresh token. 

## Examples
```shell script
curl -X POST \
  http://localhost:8080/oauth/token \
  -H 'Content-Type: application/x-www-form-urlencoded' \
  -d 'client_id=anotherClientId&username=username&password=pass&grant_type=password'
```
```json
{
    "access_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjE1NjYzOTA4ODUsInVzZXJfbmFtZSI6InVzZXJuYW1lIiwiYXV0aG9yaXRpZXMiOlsiQ1JFQVRFX01FTlUiXSwianRpIjoiY2IzMGMyZjAtMjIyZi00MzQ3LTk4NzYtNjU4M2ZlYzBkNGZmIiwiY2xpZW50X2lkIjoiYW5vdGhlckNsaWVudElkIiwic2NvcGUiOlsicmVhZCIsIndyaXRlIl19.igQl3OvMGvEjKYNNr6dM6BRCunvu6TAbujaSz-wlZVk",
    "token_type": "bearer",
    "refresh_token": "8356e33c-e363-4ac8-853c-5f93437a1422",
    "expires_in": 98,
    "scope": "profile"
}
```
For more examples (with grant types `password`, `refresh_token` and `client_credentials`) see the tests in the 
`hu.krisz.functional` package.
