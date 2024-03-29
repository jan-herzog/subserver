package de.nebelniek.web.controller.verify;

import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.philippheuer.credentialmanager.identityprovider.OAuth2IdentityProvider;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.helix.domain.User;
import de.nebelniek.database.service.CloudUserManagingService;
import de.nebelniek.database.user.interfaces.ICloudUser;
import de.nebelniek.services.hashcode.TwitchHashcodeService;
import de.nebelniek.services.permission.RankUpdateService;
import de.nebelniek.services.verify.TwitchVerifyService;
import de.nebelniek.services.verify.VerifyService;
import lombok.RequiredArgsConstructor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import static spark.Spark.get;

@Controller
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class TwitchVerifyController extends VerifyController {

    private final TwitchHashcodeService hashcodeService;

    private final TwitchVerifyService verifyService;

    private final CloudUserManagingService repository;

    private final TwitchClient twitchClient;

    private final OAuth2IdentityProvider oAuth2IdentityProvider;

    private final RankUpdateService rankUpdateService;

    public void setupRoutes() {
        get("/twitch/auth", (request, response) -> {
            String hash = request.queryParams("hash");
            if (!hashcodeService.isHashPresent(hash)) {
                response.redirect("/error");
                return "";
            }
            response.cookie("/callback/twitch", "res", hash, 1000, true);
            response.redirect("https://id.twitch.tv/oauth2/authorize?client_id=7suv1m3ae2vbiqjpbn5n2ovlnta440&redirect_uri=https://verify.nebelniek.de/callback/twitch&response_type=code&scope=user:read:email");
            return "";
        });

        get("/callback/twitch", ((request, response) -> {
            String hash = request.cookie("res");
            String code = request.queryParams("code");

            if (!hashcodeService.isHashPresent(hash)) {
                response.redirect("/error");
                return "";
            }
            response.removeCookie("res");
            ICloudUser cloudUser = repository.loadUserSync(hashcodeService.deleteHash(hash));
            OAuth2Credential credential = oAuth2IdentityProvider.getCredentialByCode(code);
            User twitchUser = twitchClient.getHelix().getUsers(credential.getAccessToken(), null, null).execute().getUsers().get(0);
            ICloudUser existing = repository.loadUserByTwitchIdSync(twitchUser.getId());
            if(existing != null) {
                response.redirect("/error");
                return "";
            }
            cloudUser.setTwitchId(twitchUser.getId());
            cloudUser.save();
            ProxiedPlayer player = ProxyServer.getInstance().getPlayer(cloudUser.getUuid());
            if (player != null)
                rankUpdateService.check(cloudUser, player);
            verifyService.notifyPlayerIfOnline(cloudUser.getUuid(), credential);
            response.redirect("/?ref=success&name=" + twitchUser.getLogin());
            return "";
        }));
    }
}
