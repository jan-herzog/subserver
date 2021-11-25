package de.nebelniek.components.texturehash;

import com.mojang.authlib.GameProfile;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.bukkit.entity.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class TextureHashProviderService {

    @SneakyThrows
    public String getHash(Player player) {
      return ((GameProfile) player.getClass().getDeclaredMethod("getProfile").invoke(player)).getProperties().get("textures").iterator().next().getValue();
    }

}
