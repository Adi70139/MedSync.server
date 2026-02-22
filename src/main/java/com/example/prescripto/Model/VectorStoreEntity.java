package com.example.prescripto.Model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "vector_store")
public class VectorStoreEntity {

    @Id
    private String id;

    private String sourceType;

    private String sourceId;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(columnDefinition = "JSONB")
    private String metadata;

}
