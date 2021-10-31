package de.nebelniek.database.user;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class TestClass {

    private CloudUserRepository cloudUserRepository;

    public void test() {

    }


}
