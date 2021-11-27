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

import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.StandardCharsets;

import static spark.Spark.get;

@Controller
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class DiscordVerifyController extends VerifyController {

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
            response.cookie("/callback/discord", "res", hash, 1000, true);
            response.redirect("https://discord.com/api/oauth2/authorize?client_id=907398251714605057&redirect_uri=https%3A%2F%2Fverify.nebelniek.de%2Fcallback%2Fdiscord&response_type=code&scope=identify%20guilds.join");
            return "";
        });

        get("/discord/test", (request, response) -> {
            String hash = request.queryParams("hash");
            response.cookie("/callback/discord", "res", hash, 1000, true);
            return hash;
        });

        get("/callback/discord", ((request, response) -> {
            String hash = request.cookie("res");
            String code = request.queryParams("code");
            if (!hashcodeService.isHashPresent(hash)) {
                response.redirect("/error");
                return "";
            }
            response.removeCookie("res");
            try {
                ICloudUser cloudUser = repository.loadUserSync(hashcodeService.deleteHash(hash));
                TokensResponse tokensResponse = discordOAuth.getTokens(code);
                DiscordAPI discordAPI = new DiscordAPI(tokensResponse.getAccessToken());
                User user = discordAPI.fetchUser();
                cloudUser.setDiscordId(user.getId());
                cloudUser.save();
                verifyService.notifyPlayerIfOnline(cloudUser.getUuid(), user);
                if (isUTF8(user.getFullUsername()))
                    response.redirect("/?ref=success&name=" + user.getFullUsername());
                else
                    response.redirect("/?ref=success&name=non-UTF8-name");
            } catch (Exception e) {
                e.printStackTrace();
                response.redirect("/error");
            }
            return "";
        }));
    }

    private boolean isUTF8(String s) {
        CharsetDecoder decoder = StandardCharsets.UTF_8.newDecoder();
        try {
            decoder.decode(ByteBuffer.wrap(s.getBytes()));
        } catch (CharacterCodingException ex) {
            return false;
        }
        return true;
    }
}
