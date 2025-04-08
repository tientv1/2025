package com.assignment.rest.base;

import org.springframework.http.ResponseEntity;

public abstract class BaseRestController<T, ID> {
    
    public ResponseEntity<?> getAll() {
        try {
            return ResponseEntity.ok(getAllEntities());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    public ResponseEntity<?> getById(ID id) {
        try {
            T entity = getEntityById(id);
            if (entity == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(entity);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    public ResponseEntity<?> update(ID id, T entity) {
        try {
            if (!isValidId(id, entity)) {
                return ResponseEntity.badRequest().build();
            }
            
            T existingEntity = getEntityById(id);
            if (existingEntity == null) {
                return ResponseEntity.notFound().build();
            }
            
            T updatedEntity = saveEntity(entity);
            return ResponseEntity.ok(updatedEntity);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    public ResponseEntity<?> delete(ID id) {
        try {
            T existingEntity = getEntityById(id);
            if (existingEntity == null) {
                return ResponseEntity.notFound().build();
            }
            
            deleteEntity(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    protected abstract Iterable<T> getAllEntities();
    
    protected abstract T getEntityById(ID id);
    
    protected abstract T saveEntity(T entity);
    
    protected abstract void deleteEntity(ID id);
    
    protected abstract boolean isValidId(ID id, T entity);
}
