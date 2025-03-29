--The script to initialize the schema was sourced from the Spring Batch Core dependency: org.springframework.batch.core.

CREATE SCHEMA IF NOT EXISTS document_schema;
SET search_path TO document_schema;

-- Create documents table
CREATE TABLE IF NOT EXISTS document_schema.documents (
    document_id SERIAL PRIMARY KEY,
    username VARCHAR(255),
    file_name VARCHAR(255),
    min_io_path VARCHAR(500),
    file_size INT,
    file_type VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create index for documents
CREATE INDEX idx_documents_user_created_at ON documents(user, created_at DESC);
CREATE INDEX idx_documents_filename_created_at ON documents(file_name, created_at DESC);

-- Create tags table
CREATE TABLE IF NOT EXISTS document_schema.tags (
    tag_id SERIAL PRIMARY KEY,
    tag_name VARCHAR(255)
);

-- Create index for tags
CREATE INDEX idx_tags_name ON tags(tag_name);

-- Create document_tags table
CREATE TABLE IF NOT EXISTS document_schema.document_tags (
    document_id INT NOT NULL,
    tag_id INT NOT NULL,    
    PRIMARY KEY (document_id, tag_id),
    FOREIGN KEY (document_id) REFERENCES documents(document_id) ON DELETE CASCADE,
    FOREIGN KEY (tag_id) REFERENCES tags(tag_id) ON DELETE CASCADE
);

-- Create index for document_tag
CREATE INDEX idx_document_tags_tag ON document_tags(tag_id);
CREATE INDEX idx_document_tags_doc ON document_tags(document_id);