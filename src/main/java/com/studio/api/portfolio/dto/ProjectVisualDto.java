package com.studio.api.portfolio.dto;

import com.fasterxml.jackson.databind.JsonNode;

/** Declarative visual rendered by the web client without executable DB content. */
public record ProjectVisualDto(
        Long id,
        String type,
        String title,
        String caption,
        String src,
        String alt,
        JsonNode payload,
        int schemaVersion,
        int sortOrder
) {
}
