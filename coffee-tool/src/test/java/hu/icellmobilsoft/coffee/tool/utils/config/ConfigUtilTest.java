package hu.icellmobilsoft.coffee.tool.utils.config;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * ConfigUtil test
 *
 * @author speter555
 * @since 1.12.0
 */
@DisplayName("Testing ConfigUtil")
public class ConfigUtilTest {

    @Test
    void testConfigUtilSameAllInstance() {
        ConfigUtil configUtil1 = ConfigUtil.getInstance();
        ConfigUtil configUtil2 = ConfigUtil.getInstance();
        Assertions.assertEquals(configUtil1.hashCode(), configUtil2.hashCode());
    }
}
