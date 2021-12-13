package de.nebelniek.content.coins;

import de.nebelniek.database.user.interfaces.ICloudUser;
import de.nebelniek.components.scoreboard.ScoreboardManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class CoinsContentService {

    private final ScoreboardManagementService scoreboardManagementService;

    public void setCoins(ICloudUser cloudUser, long coins) {
        cloudUser.setCoins(coins);
        cloudUser.saveAsync();
        scoreboardManagementService.updateCoins(cloudUser);
    }

    public void addCoins(ICloudUser cloudUser, long coins) {
        cloudUser.setCoins(cloudUser.getCoins() + coins);
        cloudUser.saveAsync();
        scoreboardManagementService.updateCoins(cloudUser);
    }

    public void removeCoins(ICloudUser cloudUser, long coins) {
        cloudUser.setCoins(cloudUser.getCoins() - coins);
        cloudUser.saveAsync();
        scoreboardManagementService.updateCoins(cloudUser);
    }

}
