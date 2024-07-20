package org.lanjianghao.common.config.network;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "network")
public class NetworkConfigurationProperties {
    private String apiPublicAddress;
    private String frontEndPublicAddress;
}
