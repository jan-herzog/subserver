package de.nebelniek.web.controller;

import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.philippheuer.credentialmanager.identityprovider.OAuth2IdentityProvider;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.helix.domain.User;
import de.nebelniek.BukkitConfiguration;
import de.nebelniek.database.user.CloudUser;
import de.nebelniek.database.user.CloudUserRepository;
import de.nebelniek.hashcode.HashcodeService;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class VerifyController {

    private final HashcodeService hashcodeService;

    private final CloudUserRepository repository;

    private final TwitchClient twitchClient;

    private final OAuth2IdentityProvider oAuth2IdentityProvider;

    @GetMapping("/auth")
    public String auth(@RequestParam(name = "hash", defaultValue = "null") String hash, Model model, HttpServletResponse response) {
        if(hashcodeService.isHashPresent(hash))
            return "redirect:/error";
        Cookie cookie = new Cookie("res", hash);
        cookie.setPath("/");
        cookie.setMaxAge(3600);
        response.addCookie(cookie);
        return "redirect:https://id.twitch.tv/oauth2/authorize?client_id=7suv1m3ae2vbiqjpbn5n2ovlnta440&redirect_uri=https://verify.nebelniek.de/callback/&response_type=code&scope=user:read:email";
    }

    @GetMapping("/callback")
    public String twitchCallback(@CookieValue(name = "res", defaultValue = "null") String hash, @RequestParam(name = "code", defaultValue = "null") String code, Model model, HttpServletResponse response) {
        if(hashcodeService.isHashPresent(hash))
            return "redirect:/error";
        Cookie cookie = new Cookie("res", hash);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        CloudUser cloudUser = repository.findByUuid(hashcodeService.deleteHash(hash));
        OAuth2Credential credential = oAuth2IdentityProvider.getCredentialByCode(code);
        User twitchUser = twitchClient.getHelix().getUsers(credential.getAccessToken(), null,null).execute().getUsers().get(0);
        cloudUser.setTwitchId(twitchUser.getId());
        repository.save(cloudUser);
        return "redirect:/?ref=success";
    }

}
