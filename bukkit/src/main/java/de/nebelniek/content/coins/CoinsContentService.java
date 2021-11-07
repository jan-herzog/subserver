package de.nebelniek.content.coins;

import de.nebelniek.database.service.CloudUserManagingService;
import de.nebelniek.database.user.interfaces.ICloudUser;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CoinsContentService {

    public void setCoins(ICloudUser cloudUser, long coins) {
        cloudUser.setCoins(coins);
        cloudUser.saveAsync();
    }

    public void addCoins(ICloudUser cloudUser, long coins) {
        cloudUser.setCoins(cloudUser.getCoins() + coins);
        cloudUser.saveAsync();
    }

    public void removeCoins(ICloudUser cloudUser, long coins) {
        cloudUser.setCoins(cloudUser.getCoins() - coins);
        cloudUser.saveAsync();
    }

}
