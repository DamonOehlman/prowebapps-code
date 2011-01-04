package com.jsonengine.meta;

//@javax.annotation.Generated(value = { "slim3-gen", "@VERSION@" }, date = "2011-01-02 21:04:00")
/** */
public final class JEDocMeta extends org.slim3.datastore.ModelMeta<com.jsonengine.model.JEDoc> {

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.jsonengine.model.JEDoc, java.lang.Long> createdAt = new org.slim3.datastore.CoreAttributeMeta<com.jsonengine.model.JEDoc, java.lang.Long>(this, "createdAt", "createdAt", long.class);

    /** */
    public final org.slim3.datastore.StringAttributeMeta<com.jsonengine.model.JEDoc> createdBy = new org.slim3.datastore.StringAttributeMeta<com.jsonengine.model.JEDoc>(this, "createdBy", "createdBy");

    /** */
    public final org.slim3.datastore.StringAttributeMeta<com.jsonengine.model.JEDoc> docType = new org.slim3.datastore.StringAttributeMeta<com.jsonengine.model.JEDoc>(this, "docType", "docType");

    /** */
    public final org.slim3.datastore.UnindexedAttributeMeta<com.jsonengine.model.JEDoc, java.util.Map<java.lang.String,java.lang.Object>> docValues = new org.slim3.datastore.UnindexedAttributeMeta<com.jsonengine.model.JEDoc, java.util.Map<java.lang.String,java.lang.Object>>(this, "docValues", "docValues", java.util.Map.class);

    /** */
    public final org.slim3.datastore.StringCollectionAttributeMeta<com.jsonengine.model.JEDoc, java.util.Set<java.lang.String>> indexEntries = new org.slim3.datastore.StringCollectionAttributeMeta<com.jsonengine.model.JEDoc, java.util.Set<java.lang.String>>(this, "indexEntries", "indexEntries", java.util.Set.class);

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.jsonengine.model.JEDoc, com.google.appengine.api.datastore.Key> key = new org.slim3.datastore.CoreAttributeMeta<com.jsonengine.model.JEDoc, com.google.appengine.api.datastore.Key>(this, "__key__", "key", com.google.appengine.api.datastore.Key.class);

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.jsonengine.model.JEDoc, java.lang.Long> updatedAt = new org.slim3.datastore.CoreAttributeMeta<com.jsonengine.model.JEDoc, java.lang.Long>(this, "updatedAt", "updatedAt", long.class);

    /** */
    public final org.slim3.datastore.StringAttributeMeta<com.jsonengine.model.JEDoc> updatedBy = new org.slim3.datastore.StringAttributeMeta<com.jsonengine.model.JEDoc>(this, "updatedBy", "updatedBy");

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.jsonengine.model.JEDoc, java.lang.Long> version = new org.slim3.datastore.CoreAttributeMeta<com.jsonengine.model.JEDoc, java.lang.Long>(this, "version", "version", java.lang.Long.class);

    private static final JEDocMeta slim3_singleton = new JEDocMeta();

    /**
     * @return the singleton
     */
    public static JEDocMeta get() {
       return slim3_singleton;
    }

    /** */
    public JEDocMeta() {
        super("JEDoc", com.jsonengine.model.JEDoc.class);
    }

    @Override
    public com.jsonengine.model.JEDoc entityToModel(com.google.appengine.api.datastore.Entity entity) {
        com.jsonengine.model.JEDoc model = new com.jsonengine.model.JEDoc();
        model.setCreatedAt(longToPrimitiveLong((java.lang.Long) entity.getProperty("createdAt")));
        model.setCreatedBy((java.lang.String) entity.getProperty("createdBy"));
        model.setDocType((java.lang.String) entity.getProperty("docType"));
        java.util.Map<java.lang.String,java.lang.Object> _docValues = blobToSerializable((com.google.appengine.api.datastore.Blob) entity.getProperty("docValues"));
        model.setDocValues(_docValues);
        model.setIndexEntries(new java.util.HashSet<java.lang.String>(toList(java.lang.String.class, entity.getProperty("indexEntries"))));
        model.setKey(entity.getKey());
        model.setUpdatedAt(longToPrimitiveLong((java.lang.Long) entity.getProperty("updatedAt")));
        model.setUpdatedBy((java.lang.String) entity.getProperty("updatedBy"));
        model.setVersion((java.lang.Long) entity.getProperty("version"));
        return model;
    }

    @Override
    public com.google.appengine.api.datastore.Entity modelToEntity(java.lang.Object model) {
        com.jsonengine.model.JEDoc m = (com.jsonengine.model.JEDoc) model;
        com.google.appengine.api.datastore.Entity entity = null;
        if (m.getKey() != null) {
            entity = new com.google.appengine.api.datastore.Entity(m.getKey());
        } else {
            entity = new com.google.appengine.api.datastore.Entity(kind);
        }
        entity.setProperty("createdAt", m.getCreatedAt());
        entity.setProperty("createdBy", m.getCreatedBy());
        entity.setProperty("docType", m.getDocType());
        entity.setUnindexedProperty("docValues", serializableToBlob(m.getDocValues()));
        entity.setProperty("indexEntries", m.getIndexEntries());
        entity.setProperty("updatedAt", m.getUpdatedAt());
        entity.setProperty("updatedBy", m.getUpdatedBy());
        entity.setProperty("version", m.getVersion());
        entity.setProperty("slim3.schemaVersion", 1);
        return entity;
    }

    @Override
    protected com.google.appengine.api.datastore.Key getKey(Object model) {
        com.jsonengine.model.JEDoc m = (com.jsonengine.model.JEDoc) model;
        return m.getKey();
    }

    @Override
    protected void setKey(Object model, com.google.appengine.api.datastore.Key key) {
        validateKey(key);
        com.jsonengine.model.JEDoc m = (com.jsonengine.model.JEDoc) model;
        m.setKey(key);
    }

    @Override
    protected long getVersion(Object model) {
        com.jsonengine.model.JEDoc m = (com.jsonengine.model.JEDoc) model;
        return m.getVersion() != null ? m.getVersion().longValue() : 0L;
    }

    @Override
    protected void incrementVersion(Object model) {
        com.jsonengine.model.JEDoc m = (com.jsonengine.model.JEDoc) model;
        long version = m.getVersion() != null ? m.getVersion().longValue() : 0L;
        m.setVersion(Long.valueOf(version + 1L));
    }

    @Override
    protected void prePut(Object model) {
        assignKeyIfNecessary(model);
        incrementVersion(model);
    }

    @Override
    public String getSchemaVersionName() {
        return "slim3.schemaVersion";
    }

    @Override
    public String getClassHierarchyListName() {
        return "slim3.classHierarchyList";
    }

}