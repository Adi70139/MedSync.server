package com.example.prescripto.Repo;


import com.example.prescripto.Model.VectorStoreEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface VectorStoreRepository extends JpaRepository<VectorStoreEntity, Long> {

    @Modifying
    @Transactional
    @Query(value = """
            INSERT INTO public.vector_store
            (id, source_type, source_id, content, metadata, embedding)
            VALUES (:id, :sourceType, :sourceId, :content, CAST(:metadata AS jsonb), CAST(:embedding AS vector))
            """, nativeQuery = true)
    void insertVector(String id, String sourceType, String sourceId, String content, String metadata, String embedding);

    @Modifying
    @Transactional
    @Query(value = """
            UPDATE public.vector_store
            SET metadata = CAST(:metadata AS jsonb)
            WHERE source_type = :sourceType AND source_id = :sourceId
            """, nativeQuery = true)
    void updateMetadataBySource(String sourceType, String sourceId, String metadata);


    @Transactional
    @Modifying
    @Query(value= """
            DELETE FROM public.vector_store
            WHERE source_type = 'appointment' AND source_id = :appointmentId
            """, nativeQuery = true)
    void deleteBySource(String appointmentId);
}
