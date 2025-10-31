package org.sideprj.weatheranalyticsservice.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class AbstractDto {

    private String id;

    private Long version;
}
