package com.jsonengine.meta;

//@javax.annotation.Generated(value = { "slim3-gen", "@VERSION@" }, date = "2010-11-18 10:53:45")
/** */
public final class JEUserMeta extends org.slim3.datastore.ModelMeta<com.jsonengine.model.JEUser> {

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.jsonengine.model.JEUser, java.lang.Long> createdAt = new org.slim3.datastore.CoreAttributeMeta<com.jsonengine.model.JEUser, java.lang.Long>(this, "createdAt", "createdAt", long.class);

    /** */
    public final org.slim3.datastore.StringAttributeMeta<com.jsonengine.model.JEUser> displayName = new org.slim3.datastore.StringAttributeMeta<com.jsonengine.model.JEUser>(this, "displayName", "displayName");

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.jsonengine.model.JEUser, com.google.appengine.api.datastore.Key> key = new org.slim3.datastore.CoreAttributeMeta<com.jsonengine.model.JEUser, com.google.appengine.api.datastore.Key>(this, "__key__", "key", com.google.appengine.api.datastore.Key.class);

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.jsonengine.model.JEUser, java.lang.Long> updatedAt = new org.slim3.datastore.CoreAttributeMeta<com.jsonengine.model.JEUser, java.lang.Long>(this, "updatedAt", "updatedAt", long.class);

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.jsonengine.model.JEUser, java.lang.Long> version = new org.slim3.datastore.CoreAttributeMeta<com.jsonengine.model.JEUser, java.lang.Long>(this, "version", "version", java.lang.Long.class);

    private static final JEUserMeta slim3_singleton = new JEUserMeta();

    /**
     * @return the singleton
     */
    public static JEUserMeta get() {
       return slim3_singleton;
    }

    /** */
    public JEUserMeta() {
        super("JEUser", com.jsonengine.model.JEUser.class);
    }

    @Override
    public com.jsonengine.model.JEUser entityToModel(com.google.appengine.api.datastore.Entity entity) {
        com.jsonengine.model.JEUser model = new com.jsonengine.model.JEUser();
        model.setCreatedAt(longToPrimitiveLong((java.lang.Long) entity.getProperty("createdAt")));
        model.setDisplayName((java.lang.String) entity.getProperty("displayName"));
        model.setKey(entity.getKey());
        model.setUpdatedAt(longToPrimitiveLong((java.lang.Long) entity.getProperty("updatedAt")));
        model.setVersion((java.lang.Long) entity.getProperty("version"));
        return model;
    }

    @Override
    public com.google.appengine.api.datastore.Entity modelToEntity(java.lang.Object model) {
        com.jsonengine.model.JEUser m = (com.jsonengine.model.JEUser) model;
        com.google.appengine.api.datastore.Entity entity = null;
        if (m.getKey() != null) {
            entity = new com.google.appengine.api.datastore.Entity(m.getKey());
        } else {
            entity = new com.google.appengine.api.datastore.Entity(kind);
        }
        entity.setProperty("createdAt", m.getCreatedAt());
        entity.setProperty("displayName", m.getDisplayName());
        entity.setProperty("updatedAt", m.getUpdatedAt());
        entity.setProperty("version", m.getVersion());
        entity.setProperty("slim3.schemaVersion", 1);
        return entity;
    }

    @Override
    protected com.google.appengine.api.datastore.Key getKey(Object model) {
        com.jsonengine.model.JEUser m = (com.jsonengine.model.JEUser) model;
        return m.getKey();
    }

    @Override
    protected void setKey(Object model, com.google.appengine.api.datastore.Key key) {
        validateKey(key);
        com.jsonengine.model.JEUser m = (com.jsonengine.model.JEUser) model;
        m.setKey(key);
    }

    @Override
    protected long getVersion(Object model) {
        com.jsonengine.model.JEUser m = (com.jsonengine.model.JEUser) model;
        return m.getVersion() != null ? m.getVersion().longValue() : 0L;
    }

    @Override
    protected void incrementVersion(Object model) {
        com.jsonengine.model.JEUser m = (com.jsonengine.model.JEUser) model;
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