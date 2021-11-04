package de.nebelniek.web.controller;

import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.philippheuer.credentialmanager.identityprovider.OAuth2IdentityProvider;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.helix.domain.User;
import de.nebelniek.database.user.CloudUser;
import de.nebelniek.database.user.CloudUserManagingService;
import de.nebelniek.database.user.interfaces.ICloudUser;
import de.nebelniek.services.hashcode.HashcodeService;
import de.nebelniek.services.verify.VerifyService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import static spark.Spark.get;

@Controller
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class VerifyController {

    private final HashcodeService hashcodeService;

    private final VerifyService verifyService;

    private final CloudUserManagingService repository;

    private final TwitchClient twitchClient;

    private final OAuth2IdentityProvider oAuth2IdentityProvider;

    public void setupRoutes() {
        get("/auth", (request, response) -> {
            String hash = request.queryParams("hash");
            if(!hashcodeService.isHashPresent(hash)) {
                response.redirect("/error");
                return "";
            }
            response.cookie("res", hash,1000);
            response.redirect("https://id.twitch.tv/oauth2/authorize?client_id=7suv1m3ae2vbiqjpbn5n2ovlnta440&redirect_uri=https://verify.nebelniek.de/callback&response_type=code&scope=user:read:email");
            return "";
        });

        get("/callback", ((request, response) -> {
            String hash = request.cookie("res");
            String code = request.queryParams("code");

            if(!hashcodeService.isHashPresent(hash)) {
                response.redirect("/error");
                return "";
            }
            response.removeCookie("res");
            ICloudUser cloudUser = repository.loadUserSync(hashcodeService.deleteHash(hash));
            OAuth2Credential credential = oAuth2IdentityProvider.getCredentialByCode(code);
            User twitchUser = twitchClient.getHelix().getUsers(credential.getAccessToken(), null,null).execute().getUsers().get(0);
            cloudUser.setTwitchId(twitchUser.getId());
            cloudUser.saveAsync();
            verifyService.notifyPlayerIfOnline(cloudUser.getUuid(), credential);
            response.redirect("/?ref=success");
            return "";
        }));
    }
}
