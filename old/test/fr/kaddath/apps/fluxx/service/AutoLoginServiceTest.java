package fr.kaddath.apps.fluxx.service;

import fr.kaddath.apps.fluxx.cache.AutoLoginService;
import org.junit.Test;
import static org.junit.Assert.*;

public class AutoLoginServiceTest {

    private AutoLoginService service = new AutoLoginService();
    
    @Test
    public void connection() {

        boolean ok = service.autoLog("julien", "julien", "http://fluxx.fr.cr:8080/fluxx/j_security_check");
        assertTrue(ok);
    }

}