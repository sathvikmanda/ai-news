package com.sathvik.ai_news.service;

import com.sathvik.ai_news.entity.User;
import com.sathvik.ai_news.repository.UserRepository;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

@Service
public class CustomOAuth2UserService extends OidcUserService {

    private final UserRepository userRepository;

    public CustomOAuth2UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) {

        OidcUser oidcUser = super.loadUser(userRequest);

        String googleId = oidcUser.getSubject();
        String email = oidcUser.getEmail();
        String name = oidcUser.getFullName();
        String picture = oidcUser.getPicture();

        User user = userRepository.findByGoogleId(googleId)
                .orElseGet(() ->
                        userRepository.findByEmail(email)
                                .orElse(null)
                );

        if (user == null) {

            user = new User(
                    name,
                    email,
                    picture,
                    googleId,
                    "GOOGLE"
            );

            userRepository.save(user);

        } else {

            if (user.getGoogleId() == null) {
                user.setGoogleId(googleId);
            }

            user.setName(name);
            user.setPictureUrl(picture);
            user.setAuthProvider("GOOGLE");

            userRepository.save(user);
        }

        return oidcUser;
    }
}