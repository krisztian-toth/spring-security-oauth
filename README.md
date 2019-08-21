# Spring Security OAuth2 server
An example OAuth2 server setup using Spring Security OAuth where the access token is a JWT and the refresh token is a 
random UUID which is then persisted in a relational database using Spring Data JPA. The main advantage of this setup is 
that it does not require the access tokens to be persisted (which means they will have to be short-lived tokens as they 
cannot be revoked), a key can be used to verify the validity and integrity of the token. The refresh token, however is 
persisted and can be revoked any time and is re-issued every time a token is refreshed. This mitigates the risk of 
someone getting hold of a refresh token. For examples (with grant types `password`, `refresh_token` and 
`client_credentials`) see the tests in the `hu.krisz.functional` package.