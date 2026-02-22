package com.example.prescripto.Service;

import java.util.Map;

public interface EmbeddableDocument {

    String getSourceType();

    String getSourceId();

    String buildEmbeddingText();

    Map<String, Object> buildMetadata();
}
