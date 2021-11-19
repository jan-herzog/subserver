package de.nebelniek.web.controller.verify;

import de.nebelniek.database.service.CloudUserManagingService;
import de.nebelniek.database.user.interfaces.ICloudUser;
import de.nebelniek.services.hashcode.DiscordHashcodeService;
import de.nebelniek.services.verify.DiscordVerifyService;
import io.mokulu.discord.oauth.DiscordAPI;
import io.mokulu.discord.oauth.DiscordOAuth;
import io.mokulu.discord.oauth.model.TokensResponse;
import io.mokulu.discord.oauth.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import static spark.Spark.get;

@Controller
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class DiscordVerifyController extends VerifyController{

    private final DiscordHashcodeService hashcodeService;

    private final DiscordVerifyService verifyService;

    private final CloudUserManagingService repository;

    private final DiscordOAuth discordOAuth;

    public void setupRoutes() {
        get("/discord/auth", (request, response) -> {
            String hash = request.queryParams("hash");
            if (!hashcodeService.isHashPresent(hash)) {
                response.redirect("/error");
                return "";
            }
            response.cookie("res", hash, 1000);
            response.redirect("https://discord.com/api/oauth2/authorize?client_id=907398251714605057&redirect_uri=https%3A%2F%2Fverify.nebelniek.de%2Fcallback%2Fdiscord&response_type=code&scope=identify%20guilds.join");
            return "";
        });

        get("/callback/discord", ((request, response) -> {
            String hash = request.cookie("res");
            String code = request.queryParams("code");

            if (!hashcodeService.isHashPresent(hash)) {
                response.redirect("/error");
                return "";
            }
            response.removeCookie("res");
            ICloudUser cloudUser = repository.loadUserSync(hashcodeService.deleteHash(hash));
            TokensResponse tokensResponse = discordOAuth.getTokens(code);
            DiscordAPI discordAPI = new DiscordAPI(tokensResponse.getAccessToken());
            User user = discordAPI.fetchUser();
            cloudUser.setTwitchId(user.getId());
            cloudUser.save();
            verifyService.notifyPlayerIfOnline(cloudUser.getUuid(), user);
            response.redirect("/?ref=success&name=" + user.getFullUsername());
            return "";
        }));
    }
}
