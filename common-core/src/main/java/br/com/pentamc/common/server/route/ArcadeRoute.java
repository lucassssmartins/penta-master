package br.com.pentamc.common.server.route;

import br.com.pentamc.common.server.route.type.ArcadeMode;
import br.com.pentamc.common.server.route.type.ArcadeType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Builder
public class ArcadeRoute {

    private final UUID uniqueId;

    private ArcadeType type;
    private ArcadeMode mode;

    private boolean targetSet;
    private UUID targetId;
}
