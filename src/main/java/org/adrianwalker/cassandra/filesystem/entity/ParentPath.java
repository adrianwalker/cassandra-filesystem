package org.adrianwalker.cassandra.filesystem.entity;

import com.datastax.driver.mapping.annotations.ClusteringColumn;
import com.datastax.driver.mapping.annotations.Column;
import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;

import java.util.UUID;

@Table(keyspace = "filesystem", name = "parent_path")
public final class ParentPath {

  private String path;
  private UUID fileId;

  public ParentPath() {
  }

  public ParentPath(final String path, final UUID fileId) {

    this.path = path;
    this.fileId = fileId;
  }

  @PartitionKey
  @Column(name = "path")
  public String getPath() {

    return path;
  }

  public void setPath(final String path) {

    this.path = path;
  }

  @ClusteringColumn
  @Column(name = "file_id")
  public UUID getFileId() {

    return fileId;
  }

  public void setFileId(final UUID fileId) {

    this.fileId = fileId;
  }
}
